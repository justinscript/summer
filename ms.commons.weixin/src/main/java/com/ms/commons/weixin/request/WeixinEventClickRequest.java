package com.ms.commons.weixin.request;

import java.util.Map;

/**
 * 用户点击自定义菜单后，微信会把点击事件推送给开发者，请注意，点击菜单弹出子菜单，不会产生上报
 * 
 * <pre>
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[FromUser]]></FromUserName>
 * <CreateTime>123456789</CreateTime>
 * <MsgType><![CDATA[event]]></MsgType>
 * <Event><![CDATA[CLICK]]></Event>
 * <EventKey><![CDATA[EVENTKEY]]></EventKey>
 * </xml>
 * </pre>
 */
public class WeixinEventClickRequest extends WeixinEventRequest {

    private String eventKey;

    public WeixinEventClickRequest(Map<String, String> datas) {
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
