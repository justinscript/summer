/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.nisa.mina;

import java.io.IOException;

import com.ms.commons.nisa.impl.CacheRegistServiceImpl;
import com.ms.commons.nisa.impl.NisaException;
import com.ms.commons.nisa.info.NotifyInfo;
import com.ms.commons.nisa.listener.CacheUpdateListener;
import com.ms.commons.nisa.mina.client.MinaClient;

/**
 * @author zxc Apr 12, 2013 6:54:35 PM
 */
public class MinaClientDebug {

    private final static String CLIENT_IP = "localhost";
    private final static int    PORT      = 5991;

    public static void main(String a[]) {
        MinaClient minaClient = new MinaClient();
        CacheRegistServiceImpl cacheRegistService = new CacheRegistServiceImpl();
        CacheUpdateListenerA cacheListenerA = new CacheUpdateListenerA("UserCache");
        CacheUpdateListenerA cacheListenerB = new CacheUpdateListenerB("UserCache");
        CacheUpdateListenerA cacheListenerC = new CacheUpdateListenerC("UserCache");
        try {
            minaClient.start("testclicent", "nisaApp" + System.currentTimeMillis() % 10, "dev", CLIENT_IP, PORT, false);
            // minaClient.registCacheRegistService(cacheRegistService);
            Thread.sleep(5000);

            cacheRegistService.regist("GROUP_USER", new CacheUpdateListener() {

                public boolean notifyUpdate(NotifyInfo info) {
                    return true;
                }
            });

            cacheRegistService.regist(cacheListenerA.getClass().getName() + cacheListenerA.getGroupName(),
                                      cacheListenerA);
            cacheRegistService.regist(cacheListenerB.getClass().getName() + cacheListenerB.getGroupName(),
                                      cacheListenerB);
            cacheRegistService.regist(cacheListenerC.getClass().getName() + cacheListenerC.getGroupName(),
                                      cacheListenerC);

            // cacheRegistService.regist("GROUP_USER",new CacheUpdateListener(){
            // public boolean notifyUpdate(NotifyInfo info) {
            // return true;
            // }
            // });

            cacheRegistService.broadcast("UserCache", "serverA update");
            cacheRegistService.broadcast("UserCache", "serverB update");

            while (true) {
                Thread.sleep(10000);
            }
        } catch (IOException e) {
            throw new NisaException("连接nisa服务器失败。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
