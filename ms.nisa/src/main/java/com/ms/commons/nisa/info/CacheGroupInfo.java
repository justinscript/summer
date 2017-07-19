/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.info;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc Apr 12, 2013 6:50:22 PM
 */
public class CacheGroupInfo {

    private List<EachClient> groupList   = new ArrayList<EachClient>();
    /**
     * 存储历史记录
     */
    private List<NotifyInfo> historyList = new ArrayList<NotifyInfo>();
    private Long             updateTime;

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public void add(EachClient eachClient) {
        if (groupList == null) {
            groupList = new ArrayList<EachClient>();
        }
        for (EachClient e : groupList) {
            if (e.getClient().equals(eachClient.getClient())) {// 已存在了，不再添加了
                break;
            }
        }
        groupList.add(eachClient);
    }

    public void removeByClientInfo(ClientInfo clientInfo) {
        if (groupList == null) {
            return;
        }
        int size = groupList.size();
        for (int i = 0; i < size;) {
            EachClient e = groupList.get(i);
            if (e.getClient().equals(clientInfo)) {
                groupList.remove(e);
                size--;
            } else {
                i++;
            }
        }
    }

    /**
     * 添加Cache更新的历史记录。（最多记录10条）
     * 
     * @param notifyInfo
     */
    public void addHistory(NotifyInfo notifyInfo) {
        if (notifyInfo == null) {
            return;
        }
        historyList.add(notifyInfo);
        if (historyList.size() > 10) {
            historyList.remove(0);
        }
    }

    public List<EachClient> getGroupList() {
        return groupList;
    }

    public List<NotifyInfo> getHistoryList() {
        return historyList;
    }

    public static class EachClient {

        private ClientInfo client;
        private Long       updateTime; // 更新时间,如果是null表示还没有更新。

        public EachClient(ClientInfo client) {
            this.client = client;
        }

        public ClientInfo getClient() {
            return client;
        }

        public Long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Long updateTime) {
            this.updateTime = updateTime;
        }
    }
}
