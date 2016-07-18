package com.ms.commons.weixin.message;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * {
 *     "touser":"OPENID",
 *     "msgtype":"voice",
 *     "voice":
 *     {
 *       "media_id":"MEDIA_ID"
 *     }
 * }
 * </pre>
 */
public class WeixinVoiceMessage extends WeixinMessage {

    private Map<String, String> voice;

    public WeixinVoiceMessage(String touser) {
        this(touser, null);
    }

    public WeixinVoiceMessage(String touser, String mediaId) {
        super(touser, "voice");
        voice = new HashMap<String, String>();
        setVoiceMediaId(mediaId);
    }

    public void setVoiceMediaId(String mediaId) {
        voice.put("media_id", mediaId);
    }

    /**
     * @return the voice
     */
    public Map<String, String> getVoice() {
        return voice;
    }

}
