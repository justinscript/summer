package com.ms.commons.weixin.response;

import org.jdom.Element;

import com.ms.commons.weixin.request.WeixinRequest;

/**
 * <pre>
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[fromUser]]></FromUserName>
 * <CreateTime>12345678</CreateTime>
 * <MsgType><![CDATA[image]]></MsgType>
 * <Image>
 * <MediaId><![CDATA[media_id]]></MediaId>
 * </Image>
 * </xml>
 * </pre>
 */
public class WeixinImageResponse extends WeixinResponse {

    private String mediaId;

    public WeixinImageResponse(WeixinRequest request) {
        super(request);
    }

    /**
     * @return the mediaId
     */
    public String getMediaId() {
        return mediaId;
    }

    /**
     * @param mediaId the mediaId to set
     */
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getResponseType() {
        return "image";
    }

    public void addElement(Element root) {
        Element imageEle = new Element("Image");
        Element mediaIdEle = new Element("MediaId");
        mediaIdEle.setText(mediaId);
        imageEle.addContent(mediaIdEle);
        root.addContent(imageEle);
    }

}
