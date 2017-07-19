/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.ms.commons.udas.impl.CacheEnum;
import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.impl.UdasServiceImpl;
import com.ms.commons.udas.impl.handler.AbstractKVHandler;
import com.ms.commons.udas.impl.handler.MemCachedHandler;
import com.ms.commons.udas.memcached.HTMLTestObj;
import com.ms.commons.udas.retry.PersistenceService;
import com.ms.commons.udas.retry.RetryObject;
import com.ms.commons.udas.retry.RetryService;
import com.ms.commons.udas.retry.RetryUtils;

/**
 * 测试正常Memcached的增删改查方法
 * 
 * @author zxc Apr 12, 2013 6:36:20 PM
 */
public class MemcachedUdasServiceTest extends AbstartUdasServiceTest {

    @Override
    protected AbstractKVHandler[] gethandles() {
        return new AbstractKVHandler[] { new MemCachedHandler("html", "192.168.1.190:11211") };
    }

    /**
     * 测试UDAS写入失败后是不否会保存，到本地
     */
    @Ignore
    @Test
    public void testWirteTOBDB() {
        // 本地bdb
        String path = RetryService.combinePath("html");
        PersistenceService persistenceService = new PersistenceService(path, "html");
        // 先清理
        persistenceService.del("failed_id");
        // 试着通过一份假的Memcached配置去写
        UdasServiceImpl udasService = new UdasServiceImpl(CacheEnum.remote_memcached, "html", "SA_fake.memcahed.url");
        udasService.init();
        UdasObj obj = new UdasObj(1L, new HTMLTestObj("testWirteTOBDB"));
        udasService.put("failed_id", obj);
        // 看看信息是否会写到本地的BDB中去呢？
        RetryObject retryObj = persistenceService.getFromBDB("failed_id");
        System.out.println("====retryObj===" + retryObj);
        Assert.assertTrue(RetryUtils.equals(obj, retryObj));
    }
}
