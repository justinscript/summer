package com.ms.commons.weixin.request;

import java.util.Map;

/**
 * <pre>
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[fromUser]]></FromUserName>
 * <CreateTime>1357290913</CreateTime>
 * <MsgType><![CDATA[voice]]></MsgType>
 * <MediaId><![CDATA[media_id]]></MediaId>
 * <Format><![CDATA[Format]]></Format>
 * <MsgId>1234567890123456</MsgId>
 * </xml>
 * </pre>
 */
public class WeixinVoiceRequest extends WeixinRequest {

    private String mediaId;
    private String format;
    private String recognition; // 语音识别出来的数据

    public WeixinVoiceRequest(Map<String, String> datas) {
        super(datas);
        mediaId = datas.get("MediaId");
        format = datas.get("Format");
        recognition = datas.get("Recognition");
    }

    /**
     * @return the mediaId
     */
    public String getMediaId() {
        return mediaId;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return the recognition
     */
    public String getRecognition() {
        return recognition;
    }

}
