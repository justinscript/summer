package com.ms.commons.weixin.request;

import java.util.Map;

/**
 * <pre>
 * <xml>
 *  <ToUserName><![[toUser]]></ToUserName>
 *  <FromUserName><![CDATA[fromUser]]></FromUserName> 
 *  <CreateTime>1348831860</CreateTime>
 *  <MsgType><![CDATA[text]]></MsgType>
 *  <Content><![CDATA[this is a test]]></Content>
 *  <MsgId>1234567890123456</MsgId>
 * </xml>
 * </pre>
 */
public class WeixinTextRequest extends WeixinRequest {

    private String content;

    public WeixinTextRequest(Map<String, String> datas) {
        super(datas);
        content = datas.get("Content");
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
}
