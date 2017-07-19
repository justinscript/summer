package com.ms.commons.weixin.message;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * {
 *     "touser":"OPENID",
 *     "msgtype":"image",
 *     "image":
 *     {
 *       "media_id":"MEDIA_ID"
 *     }
 * }
 * </pre>
 */
public class WeixinImageMessage extends WeixinMessage {

    private Map<String, String> image;

    public WeixinImageMessage(String touser) {
        this(touser, null);
    }

    public WeixinImageMessage(String touser, String mediaId) {
        super(touser, "image");
        image = new HashMap<String, String>();
        setImageMediaId(mediaId);
    }

    public void setImageMediaId(String mediaId) {
        image.put("media_id", mediaId);
    }

    /**
     * @return the image
     */
    public Map<String, String> getImage() {
        return image;
    }

}
