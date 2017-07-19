package com.ms.commons.weixin.request;

import java.util.Map;

public class WeixinEventRequest extends WeixinRequest {

    private String event;

    public WeixinEventRequest(Map<String, String> datas) {
        super(datas);
        event = datas.get("Event");
    }

    /**
     * @return the event
     */
    public String getEvent() {
        return event;
    }
}
