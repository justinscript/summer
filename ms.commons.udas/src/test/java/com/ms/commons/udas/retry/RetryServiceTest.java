/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.retry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ms.commons.udas.memcached.HTMLTestObj;
import com.ms.commons.udas.retry.mock.MockRetryService;
import com.ms.commons.udas.retry.mock.MockRetryable;

/**
 * 对RetryServiceTest测试
 * 
 * <pre>
 * 主要测试他：
 * 1.丢失的数据是否会被持久化
 * 3.从持久化(BDB)中检查到任务后，是否回重写UDAS
 * 4.重试次数限制是否有效
 * </pre>
 * 
 * @author zxc Apr 12, 2013 6:37:57 PM
 */
public class RetryServiceTest {

    static {
        System.setProperty("nisa.mina.client.start", "false");
    }

    String             NOT_EXISTED_KEY    = "test_key";
    MockRetryable      udas               = new MockRetryable();
    MockRetryService   retryService       = new MockRetryService(udas, "retryservicetest");
    String             path               = RetryService.combinePath("retryservicetest");
    PersistenceService persistenceService = new PersistenceService(path, "retryservicetest");
    RetryObject        retryObject        = new RetryObject(1L, new HTMLTestObj("html"));

    @Before
    public void before() {
        // 确保BDB的干净
        persistenceService.del(NOT_EXISTED_KEY);
        // 先清理下
        udas.clear();
    }

    /**
     * 测试完成后请将持久化的数据删除
     */
    @After
    public void after() {
        persistenceService.del(NOT_EXISTED_KEY);
    }

    /**
     * 测试下，Retry是否正的会从DBD中读取数据重试。 retryService会通过retryable进行回调，所以我们只要检查retryable是否有数据就可以了。
     */
    @Test
    public void testRetry() throws InterruptedException {

        // 增加一条记录
        retryService.addRetryTask(NOT_EXISTED_KEY, retryObject);

        // 触发执行
        retryService.retryAllTasks();

        // 看看retryable中是否存在呢
        RetryObject value = udas.get(NOT_EXISTED_KEY);
        System.out.println(udas);
        Assert.assertTrue(RetryUtils.equals(retryObject, value));

    }

    /**
     * 测试下，当超过5次以后是不是就插入到失败BDB中区了
     */
    @Test
    public void testRetryOverLimit() throws InterruptedException {
        RetryObject retryObject = new RetryObject(1L, new HTMLTestObj("html"));
        retryObject.setRetryCount(5);
        retryService.addRetryTask("retry5times", retryObject);
        //
        PersistenceService failed = new PersistenceService(RetryService.combinePath("failed_retryservicetest"),
                                                           "retryservicetest");
        Assert.assertNull(persistenceService.getFromBDB("retry5times"));
        RetryObject fromBDB = failed.getFromBDB("retry5times");
        Assert.assertTrue(RetryUtils.equals(retryObject, fromBDB));

    }

    /**
     * 测试下，数据是addRetryTask方法是否真的会Persistent数据
     */
    @Test
    public void testaddRetryTaskIsPersistent() {
        retryService.addRetryTask(NOT_EXISTED_KEY, retryObject);

        // 从持久化服务取出，以证明它正的被存进去了
        RetryObject value = persistenceService.getFromBDB(NOT_EXISTED_KEY);
        Assert.assertTrue(RetryUtils.equals(retryObject, value));
        System.out.println(value);
    }
}
