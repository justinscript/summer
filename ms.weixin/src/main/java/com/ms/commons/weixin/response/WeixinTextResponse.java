package com.ms.commons.weixin.response;

import org.jdom.Element;

import com.ms.commons.weixin.request.WeixinRequest;

/**
 * <pre>
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[fromUser]]></FromUserName>
 * <CreateTime>12345678</CreateTime>
 * <MsgType><![CDATA[text]]></MsgType>
 * <Content><![CDATA[你好]]></Content>
 * </xml>
 * </pre>
 */
public class WeixinTextResponse extends WeixinResponse {

    private String content;

    public WeixinTextResponse(WeixinRequest request) {
        super(request);
    }

    public WeixinTextResponse(WeixinRequest request, String content) {
        super(request);
        this.content = content;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    public String getResponseType() {
        return "text";
    }

    public void addElement(Element root) {
        Element contentEle = new Element("Content");
        contentEle.setText(content);
        root.addContent(contentEle);
    }

}
