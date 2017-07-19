package com.ms.commons.weixin.message;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * {
 *     "touser":"OPENID",
 *     "msgtype":"text",
 *     "text":
 *     {
 *          "content":"Hello World"
 *     }
 * }
 * </pre>
 */
public class WeixinTextMessage extends WeixinMessage {

    private Map<String, String> text;

    public WeixinTextMessage(String touser) {
        this(touser, null);
    }

    public WeixinTextMessage(String touser, String content) {
        super(touser, "text");
        text = new HashMap<String, String>();
        setContent(content);
    }

    public void setContent(String content) {
        text.put("content", content);
    }

    /**
     * @return the text
     */
    public Map<String, String> getText() {
        return text;
    }

    public String toString() {
        return text.get("text");
    }
}
