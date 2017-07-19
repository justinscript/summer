package com.ms.commons.weixin.request;

import java.util.Map;

/**
 * <pre>
 *  <xml>
 *  <ToUserName><![CDATA[toUser]]></ToUserName>
 *  <FromUserName><![CDATA[fromUser]]></FromUserName>
 *  <CreateTime>1351776360</CreateTime>
 *  <MsgType><![CDATA[link]]></MsgType>
 *  <Title><![CDATA[公众平台官网链接]]></Title>
 *  <Description><![CDATA[公众平台官网链接]]></Description>
 *  <Url><![CDATA[url]]></Url>
 *  <MsgId>1234567890123456</MsgId>
 *  </xml> 
 * Title  消息标题
 * Description  消息描述
 * Url  消息链接
 * </pre>
 */
public class WeixinLinkRequest extends WeixinRequest {

    private String title;
    private String description;
    private String url;

    public WeixinLinkRequest(Map<String, String> datas) {
        super(datas);
        title = datas.get("Title");
        description = datas.get("Description");
        url = datas.get("Url");
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

}
