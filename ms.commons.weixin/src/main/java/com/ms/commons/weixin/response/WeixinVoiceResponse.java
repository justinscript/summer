package com.ms.commons.weixin.response;

import org.jdom.Element;

import com.ms.commons.weixin.request.WeixinRequest;

/**
 * <pre>
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[fromUser]]></FromUserName>
 * <CreateTime>12345678</CreateTime>
 * <MsgType><![CDATA[voice]]></MsgType>
 * <Voice>
 * <MediaId><![CDATA[media_id]]></MediaId>
 * </Voice>
 * </xml>
 * </pre>
 */
public class WeixinVoiceResponse extends WeixinResponse {

    private String mediaId;

    public WeixinVoiceResponse(WeixinRequest request) {
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
        return "voice";
    }

    public void addElement(Element root) {
        Element voiceEle = new Element("Voice");
        Element mediaIdEle = new Element("MediaId");
        mediaIdEle.setText(mediaId);
        voiceEle.addContent(mediaIdEle);
        root.addContent(voiceEle);
    }

}
