package com.ms.commons.weixin.qymessage;

import java.util.*;

import org.apache.commons.lang.StringUtils;

public class QYWeixinTextMessage extends QYWeixinMessage {

    private Map<String, String> text;

    public QYWeixinTextMessage(String touser, String toparty, String totag) {
        this(touser, toparty, totag, null);
    }

    public QYWeixinTextMessage(String touser, String toparty, String totag, String content) {
        super(touser, toparty, totag, "text");
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

    /**
     * 发送给指定的人
     * 
     * @param userIds
     * @return
     */
    public static QYWeixinTextMessage createSendToUsers(String userId) {
        List<String> list = new ArrayList<String>(1);
        list.add(userId);
        return createSendToUsers(list, null, false);
    }

    /**
     * 发送给指定的人
     * 
     * @param userIds
     * @return
     */
    public static QYWeixinTextMessage createSendToUsers(List<String> userIds) {
        return createSendToUsers(userIds, null, false);
    }

    /**
     * 发送给指定的人
     * 
     * @param userIds
     * @return
     */
    public static QYWeixinTextMessage createSendToUsers(String userId, String content, boolean safe) {
        List<String> list = new ArrayList<String>(1);
        list.add(userId);
        return createSendToUsers(list, content, safe);
    }

    /**
     * 发送给所有人
     * 
     * @param content
     * @param safe
     * @return
     */
    public static QYWeixinTextMessage createSendToAll(String content, boolean safe) {
        QYWeixinTextMessage weixinTextMessage = new QYWeixinTextMessage(ALL, null, null, content);
        weixinTextMessage.setSafe(safe);
        return weixinTextMessage;
    }

    /**
     * 发送给指定的人
     * 
     * @param userIds
     * @param content
     * @param safe
     * @return
     */
    public static QYWeixinTextMessage createSendToUsers(List<String> userIds, String content, boolean safe) {
        String touser = StringUtils.join(userIds, JOIN_CHAR);
        QYWeixinTextMessage weixinTextMessage = new QYWeixinTextMessage(touser, null, null, content);
        weixinTextMessage.setSafe(safe);
        return weixinTextMessage;
    }
}
