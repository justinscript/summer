package com.ms.commons.weixin.cons;

import org.apache.commons.lang.StringUtils;

public enum WeixinEventType {

    // 订阅
    subscribe,
    // 取消订阅
    unsubscribe,
    // 地理位置
    LOCATION,

    CLICK,

    VIEW;

    public static WeixinEventType getEventType(String event) {
        if (StringUtils.isEmpty(event)) {
            return null;
        }
        for (WeixinEventType eventType : values()) {
            if (eventType.name().equals(event)) {
                return eventType;
            }
        }
        return null;
    }
}
