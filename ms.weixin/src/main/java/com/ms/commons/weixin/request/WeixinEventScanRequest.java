package com.ms.commons.weixin.request;

import java.util.Map;

/**
 * <pre>
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[FromUser]]></FromUserName>
 * <CreateTime>123456789</CreateTime>
 * <MsgType><![CDATA[event]]></MsgType>
 * <Event><![CDATA[SCAN]]></Event>
 * <EventKey><![CDATA[SCENE_VALUE]]></EventKey>
 * <Ticket><![CDATA[TICKET]]></Ticket>
 * </xml>
 * EventKey  事件KEY值，是一个32位无符号整数，即创建二维码时的二维码scene_id
 * Ticket   二维码的ticket，可用来换取二维码图片
 * </pre>
 */
public class WeixinEventScanRequest extends WeixinEventRequest {

    private String eventKey; // scene_id
    private String ticket;  //

    public WeixinEventScanRequest(Map<String, String> datas) {
        super(datas);
        eventKey = datas.get("EventKey");
        ticket = datas.get("Ticket");
    }

    /**
     * @return the eventKey
     */
    public String getEventKey() {
        return eventKey;
    }

    /**
     * @return the ticket
     */
    public String getTicket() {
        return ticket;
    }

}
