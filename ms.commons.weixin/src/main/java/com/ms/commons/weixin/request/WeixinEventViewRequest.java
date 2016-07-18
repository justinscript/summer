package com.ms.commons.weixin.request;

import java.util.Map;

/**
 * 点击菜单跳转链接时的事件推送
 * 
 * <pre>
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[FromUser]]></FromUserName>
 * <CreateTime>123456789</CreateTime>
 * <MsgType><![CDATA[event]]></MsgType>
 * <Event><![CDATA[VIEW]]></Event>
 * <EventKey><![CDATA[www.qq.com]]></EventKey>
 * </xml>
 * </pre>
 */
public class WeixinEventViewRequest extends WeixinEventRequest {

    private String eventKey;

    public WeixinEventViewRequest(Map<String, String> datas) {
        super(datas);
        eventKey = datas.get("EventKey");
    }

    /**
     * @return the eventKey
     */
    public String getEventKey() {
        return eventKey;
    }

}
