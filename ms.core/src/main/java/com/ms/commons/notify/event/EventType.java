/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.notify.event;

/**
 * 所有的事件
 * 
 * @author zxc Apr 12, 2013 2:55:24 PM
 */
public enum EventType {

    itemAdd, itemDelete, itemUpshelf, itemDownshelf, itemUpdate,

    // notify时间
    notifyItem, notifyTrade, notifyRefund,

    // INVALID_SESSION
    invalidSession;

    public static boolean isItemAdd(EventType eventType) {
        return eventType != null && eventType == EventType.itemAdd;
    }

    public static boolean isItemDelete(EventType eventType) {
        return eventType != null && eventType == EventType.itemDelete;
    }

    public boolean isItemAdd() {
        return this == itemAdd;
    }

    public boolean isItemDelete() {
        return this == itemDelete;
    }

    public boolean isItemUpdate() {
        return this == itemUpdate;
    }

    public static boolean isItemUpdate(EventType eventType) {
        return eventType != null && eventType == EventType.itemUpdate;
    }
}
