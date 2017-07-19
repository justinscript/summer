/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas.memcached.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 * 用内存模拟Memcached
 * 
 * @author zxc Apr 12, 2013 6:40:05 PM
 */
public class MockMemcachedClient extends MemcachedClient {

    private boolean             onlyInnerMemory;                              // 只是保存到内存中去
    private Map<String, Object> innermap      = new HashMap<String, Object>();

    Future<Boolean>             BooleanFuture = new Future<Boolean>() {

                                                  @Override
                                                  public boolean cancel(boolean arg0) {
                                                      return false;
                                                  }

                                                  @Override
                                                  public Boolean get() throws InterruptedException, ExecutionException {
                                                      return null;
                                                  }

                                                  @Override
                                                  public Boolean get(long arg0, TimeUnit arg1)
                                                                                              throws InterruptedException,
                                                                                              ExecutionException,
                                                                                              TimeoutException {
                                                      return null;
                                                  }

                                                  @Override
                                                  public boolean isCancelled() {
                                                      return false;
                                                  }

                                                  @Override
                                                  public boolean isDone() {
                                                      return false;
                                                  }
                                              };

    /**
     * @param addrs
     * @param isFake 如果设为True，则只会记录到内存，而不会到Memcached
     * @throws IOException
     */
    public MockMemcachedClient(String address, boolean isFake) throws IOException {
        super(AddrUtil.getAddresses(address));
        if (isFake) {
            this.onlyInnerMemory = true;
        } else {
            innermap = null;// 防止对结果干扰
        }
    }

    // @Override
    // public Future<Boolean> set(String key, int exp, Object value) {
    // if (onlyInnerMemory) {
    // innermap.put(key, value);
    // return BooleanFuture;
    // } else {
    // return super.set(key, exp, value);
    // }
    // }

    @Override
    public Object get(String key) {
        Object result = null;

        if (onlyInnerMemory) {
            result = innermap.get(key);
        } else {
            result = super.get(key);
        }

        return result;
    }

    // @Override
    // public Future<Boolean> delete(String key) {
    // if (onlyInnerMemory) {
    // innermap.remove(key);
    // return BooleanFuture;
    // } else {
    // return super.delete(key);
    // }
    // }
}
