package com.ms.commons.weixin.request;

import java.util.Map;

/**
 * <pre>
 *  <xml>
 *  <ToUserName><![CDATA[toUser]]></ToUserName>
 *  <FromUserName><![CDATA[fromUser]]></FromUserName>
 *  <CreateTime>1348831860</CreateTime>
 *  <MsgType><![CDATA[image]]></MsgType>
 *  <PicUrl><![CDATA[this is a url]]></PicUrl>
 *  <MediaId><![CDATA[media_id]]></MediaId>
 *  <MsgId>1234567890123456</MsgId>
 *  </xml>
 *  PicUrl   图片链接
 *  MediaId  图片消息媒体id，可以调用多媒体文件下载接口拉取数据。
 * </pre>
 */
public class WeixinImageRequest extends WeixinRequest {

    private String picUrl;
    private String mediaId;

    public WeixinImageRequest(Map<String, String> datas) {
        super(datas);
        picUrl = datas.get("PicUrl");
        picUrl = datas.get("MediaId");
    }

    /**
     * @return the picUrl
     */
    public String getPicUrl() {
        return picUrl;
    }

    /**
     * @return the mediaId
     */
    public String getMediaId() {
        return mediaId;
    }

}
