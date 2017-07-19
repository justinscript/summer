package com.ms.commons.weixin.message;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * {
 *     "touser":"OPENID",
 *     "msgtype":"video",
 *     "video":
 *     {
 *       "media_id":"MEDIA_ID",
 *       "title":"TITLE",
 *       "description":"DESCRIPTION"
 *     }
 * }
 * media_id  是   发送的视频的媒体ID
 * title    否   视频消息的标题
 * description  否   视频消息的描述
 * </pre>
 */
public class WeixinVideoMessage extends WeixinMessage {

    private Map<String, String> video;

    public WeixinVideoMessage(String touser) {
        this(touser, null, null, null);
    }

    public WeixinVideoMessage(String touser, String mediaId, String title, String description) {
        super(touser, "video");
        video = new HashMap<String, String>();
        setMediaId(mediaId);
        setTitle(title);
        setDescription(description);
    }

    public void setMediaId(String mediaId) {
        video.put("media_id", mediaId);
    }

    public void setTitle(String title) {
        video.put("title", title);
    }

    public void setDescription(String description) {
        video.put("description", description);
    }

    /**
     * @return the video
     */
    public Map<String, String> getVideo() {
        return video;
    }

}
