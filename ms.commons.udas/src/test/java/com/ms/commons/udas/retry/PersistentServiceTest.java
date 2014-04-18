/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.retry;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ms.commons.udas.memcached.HTMLTestObj;

/**
 * @author zxc Apr 12, 2013 6:38:22 PM
 */
public class PersistentServiceTest {

    private PersistenceService  persistenceService;
    private Set<String>         allKeys        = new HashSet<String>();
    private RetryObject         retryObject    = new RetryObject(1L, new HTMLTestObj("html"));
    private static final String KEY_EXISTED    = "existed_id";
    private static final String KEY_NOT_EXITED = "not_existed_id";
    String                      dataSourcename = "testdddddd";
    String                      path           = System.getProperty("user.home") + File.separator + "localbdb"
                                                 + File.separator + "persistentServicetest";

    @Before
    public void before() {
        persistenceService = new PersistenceService(path, dataSourcename);
        persistenceService.persistent(KEY_EXISTED, retryObject);
        persistenceService.del(KEY_NOT_EXITED);
        // 所有存在ID
        allKeys.add(KEY_EXISTED);
    }

    @Test
    public void del() {
        Assert.assertNotNull(persistenceService.getFromBDB(KEY_EXISTED));
        persistenceService.del(KEY_EXISTED);
        Assert.assertNull(persistenceService.getFromBDB(KEY_EXISTED));
    }

    @Test
    public void getRetryObject() {
        RetryObject result = persistenceService.getFromBDB(KEY_EXISTED);
        Assert.assertTrue(RetryUtils.equals(retryObject, result));

        Assert.assertNull(persistenceService.getFromBDB(KEY_NOT_EXITED));
    }

    @Test
    public void getAllKey() {
        Set<String> ids = persistenceService.getAllKey();
        System.out.println(ids);
        Assert.assertTrue(RetryUtils.equals(allKeys, ids));
    }

    @Test
    public void putRetryObject() {
        RetryObject result = persistenceService.getFromBDB(KEY_NOT_EXITED);
        Assert.assertNull(result);// 第一次是NULL
        persistenceService.persistent(KEY_NOT_EXITED, retryObject);
        result = persistenceService.getFromBDB(KEY_NOT_EXITED);
        Assert.assertTrue(RetryUtils.equals(retryObject, result));// 第二次就不是NULL了
        // 测试当值已经存在的情况
        persistenceService.persistent(KEY_NOT_EXITED, retryObject);
        Assert.assertTrue(RetryUtils.equals(retryObject, result));// 值存在，再保存也不会影响结果
    }
}
