/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.nisa.mina;

import com.ms.commons.nisa.impl.CacheRegistServiceImpl;
import com.ms.commons.nisa.info.NotifyInfo;
import com.ms.commons.nisa.listener.CacheUpdateListener;

/**
 * @author zxc Apr 12, 2013 6:55:28 PM
 */
public class CacheUpdateListenerA implements CacheUpdateListener {

    private String groupName;

    public CacheUpdateListenerA(String groupName) {
        this.groupName = groupName;
    }

    public boolean notifyUpdate(NotifyInfo info) {

        String myKey = CacheRegistServiceImpl.getSourceIpKey();
        if (myKey.equals(info.getSourceIpKey())) {
            System.out.println("哈哈，这个是我发给大家的更新信息，我就不更新了。");
        } else {
            System.out.println("这个是" + info.getSourceIpKey() + "有更改后通知大家修改的，请修改啊！具体参数如下：");
            System.out.println("groupName=" + info.getGroup());
            System.out.println("value=" + info.getUpdaeValue());
        }
        return false;
    }

    public String getGroupName() {
        return groupName;
    }
}
