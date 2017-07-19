/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl.email;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.message.cons.MessageConstants;
import com.ms.commons.message.cons.MessageTypeEnum;
import com.ms.commons.message.cons.SmsMsgSendType;
import com.ms.commons.message.impl.filter.MsgKey;
import com.ms.commons.message.interfaces.Message;
import com.ms.commons.message.utils.MessageUtil;
import com.ms.commons.utilities.CoreUtilities;

/**
 * 邮件对象。
 * 
 * <pre>
 * 有三个部分组成：
 * 1.邮件的认证信息，发送过程中的认证信息。{@link MsunMailEnvironment}
 * 2.邮件信息:收件人,发件人,主题,邮件正文。
 * 3.邮件附件。{@link MsunMailAttachment}
 * </pre>
 * 
 * @author zxc Apr 13, 2014 10:46:41 PM
 */
public class MsunMail implements Message {

    private static final long         serialVersionUID     = 1257695092672869727L;

    private MsunMailEnvironment      environment          = new MsunMailEnvironment();

    private List<MsunMailAttachment> msunMailAttachments = new ArrayList<MsunMailAttachment>(1);

    private String                    from;

    private String                    fromName;

    private String[]                  to;

    private String[]                  cc;

    private String[]                  bcc;

    private String                    replyTo;

    private String                    subject;

    private String                    message;

    /**
     * 邮件信息
     */
    private String                    htmlMessage;

    /**
     * 编码
     */
    private String                    charset              = "utf-8";

    /**
     * 附件
     */
    private File[]                    attachment;

    /**
     * 邮件头
     */
    private Map<String, String>       headers              = new HashMap<String, String>();

    /**
     * 未能通过验证的信息接收着
     */
    private List<String>              unqualifiedReceiver;

    /**
     * 辅助自动化测试的字段，对业务逻辑没有影响
     */
    private String                    testKey;

    /**
     * 缺省构建器
     */
    public MsunMail() {
    }

    public MsunMail(String from, String[] to, String subject, String htmlMessage) {
        this(from, to, subject, htmlMessage, CoreUtilities.getIPAddress() + "_mail_key_");
    }

    /**
     * @param from 发送者,请使用有效的邮件地址，同时建议调用setFromName方法设置FromName属性
     * @param to 接受者,参数中如果存在无效数据(空值),程序会自动剔除
     * @param subject 主题
     * @param htmlMessage 文件内容
     */
    public MsunMail(String from, String[] to, String subject, String htmlMessage, String testKey) {
        this.from = from;
        this.to = MessageUtil.removeEmptyElement(to);
        this.subject = subject;
        this.htmlMessage = htmlMessage;
        this.testKey = testKey;
    }

    public File[] getAttachment() {
        return attachment;
    }

    public void setAttachment(File[] attachment) {
        this.attachment = attachment;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        // 过滤掉无效数据
        this.bcc = MessageUtil.removeEmptyElement(bcc);
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        // 过滤掉无效数据
        this.cc = MessageUtil.removeEmptyElement(cc);
    }

    public String getHtmlMessage() {
        return htmlMessage;
    }

    public void setHtmlMessage(String htmlMessage) {
        this.htmlMessage = htmlMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }

    /**
     * 添加邮件附件对象
     * 
     * @param attachment - 邮件附件对象
     */
    public void addAttachment(MsunMailAttachment attachment) {
        if (attachment != null) {
            msunMailAttachments.add(attachment);
        }
    }

    /**
     * 获得邮件附件对象List
     * 
     * @return - 邮件附件对象List
     */
    public List<MsunMailAttachment> getAttachments() {
        return msunMailAttachments;
    }

    public MsunMailEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(MsunMailEnvironment environment) {
        this.environment = environment;
    }

    /**
     * 返回所有的信息接收者
     */
    public String[] getAllReceiver() {
        int len = 0;
        if (getTo() != null) {
            len = getTo().length;
        }
        if (getCc() != null) {
            len += getCc().length;
        }
        if (getBcc() != null) {
            len += getBcc().length;
        }
        String[] receivers = new String[len];
        int index = 0;
        if (getTo() != null) {
            System.arraycopy(getTo(), 0, receivers, 0, getTo().length);
            index = getTo().length;
        }
        if (getCc() != null) {
            System.arraycopy(getCc(), 0, receivers, index, getCc().length);
            index += getCc().length;
        }
        if (getBcc() != null) {
            System.arraycopy(getBcc(), 0, receivers, index, getBcc().length);
        }
        return receivers;
    }

    public String dumpInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("MessageType=" + getMessageType());
        sb.append(";from=");
        sb.append(this.getFrom());
        sb.append(";to=");
        sb.append(StringUtils.join(this.getTo(), ","));
        sb.append(";cc=");
        sb.append(StringUtils.join(this.getCc(), ","));
        sb.append(";bcc=");
        sb.append(StringUtils.join(this.getBcc(), ","));
        sb.append(";reply=");
        sb.append(this.getReplyTo());
        sb.append(";charset=");
        sb.append(this.getCharset());
        sb.append(";host=");
        sb.append(this.getEnvironment().getHostName());
        sb.append(";usr=");
        sb.append(this.getEnvironment().getUser());
        sb.append(";pwd=");
        sb.append("***encrypt***"/* this.getEnvironment().getPassword() */);
        sb.append(";subject=");
        sb.append(getSubject());
        sb.append(";htmMessage=");
        sb.append(getHtmlMessage());
        sb.append(";message=");
        sb.append(getMessage());
        if (attachment != null && attachment.length > 0) {
            sb.append(";attachment");
            sb.append("[");
            for (File file : attachment) {
                if (file != null) {
                    sb.append(file.getName());
                    sb.append(',');
                }
            }
            sb.append("]");
        }
        return sb.toString();
    }

    public String toString() {
        return dumpInfo();
    }

    public MsgKey[] identity() {
        int count = 0;
        boolean hasTo = (to != null && to.length > 0);
        boolean hasCc = (cc != null && cc.length > 0);
        boolean hasBcc = (bcc != null && bcc.length > 0);
        if (hasTo) {
            count += to.length;
        }
        if (hasCc) {
            count += cc.length;
        }
        if (hasBcc) {
            count += bcc.length;
        }

        MsgKey[] msgKeys = new MsgKey[count];
        // 收集主题
        // 信息发送内容，如果是短信，则是消息内容，如果是Email，目前暂且判断主题和主题内容--如果内容过长，则取前80个字符
        String content = generateMsgKeyContent();
        Integer contentOriginalLength = getContentLength();

        int startIndex = 0;
        if (hasTo) {
            startIndex = to.length;
            for (int i = 0; i < to.length; i++) {
                msgKeys[i] = new MsgKey(to[i], content, contentOriginalLength);
            }
        }
        if (hasCc) {
            for (int i = 0; i < cc.length; i++) {
                msgKeys[startIndex + i] = new MsgKey(cc[i], content, contentOriginalLength);
            }
            startIndex += cc.length;
        }
        if (hasBcc) {
            for (int i = 0; i < bcc.length; i++) {
                msgKeys[startIndex + i] = new MsgKey(bcc[i], content, contentOriginalLength);
            }
        }
        return msgKeys;
    }

    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.email;
    }

    /**
     * 生成消息的content
     * 
     * @return
     */
    private String generateMsgKeyContent() {
        String content = getSubject() == null ? "" : getSubject();

        // 优先考虑textMessage
        if (StringUtils.isNotEmpty(getMessage())) {
            // 如果长度大于MAX_LENGTH，则截取Header(30), Middle(30), Tail(30)
            Integer len = getMessage().length();
            if (len >= MessageConstants.MAX_LENGTH) {
                Integer mid = len / 2 - 15;
                content = content.concat(getMessage().substring(0, 29));
                content = content.concat(getMessage().substring(mid, mid + 29));
                content = content.concat(getMessage().substring(len - 30, len - 1));
            } else {
                content = content.concat(getMessage());
            }
        }

        // 如果长度不足MAX_LENGTH，并且htmlMessage不为空，则将content补足为MAX_LENGTH
        if (content.length() < MessageConstants.MAX_LENGTH && StringUtils.isNotEmpty(getHtmlMessage())) {
            Integer len = getHtmlMessage().length();
            // 如果htmlmessage较长，则应充分考虑htmlmessage
            if (len >= MessageConstants.MAX_LENGTH) {
                Integer mid = len / 2 - 15;
                content = content.concat(getHtmlMessage().substring(0, 29));
                content = content.concat(getHtmlMessage().substring(mid, mid + 29));
                content = content.concat(getHtmlMessage().substring(len - 30, len - 1));
            } else {
                int left = MessageConstants.MAX_LENGTH - content.length();
                content = content + getHtmlMessage().substring(0, Math.min(left, len));
            }
        }
        return content;
    }

    /**
     * 获得信息的长度
     * 
     * @return
     */
    private Integer getContentLength() {
        Integer rValue = null;
        if (StringUtils.isNotEmpty(getHtmlMessage())) {
            rValue = getHtmlMessage().length();
        } else if (StringUtils.isNotEmpty(getMessage())) {
            rValue = getMessage().length();
        } else {
            rValue = getSubject().length();
        }
        return rValue;
    }

    @Override
    public void setUnqualifiedReceiver(List<String> list) {
        unqualifiedReceiver = list;
    }

    @Override
    public List<String> getUnqualifiedReceiver() {
        return unqualifiedReceiver;
    }

    // //////////////////////////////////////////////////////////////////////////////
    //
    // 辅助Web自动化测试方法
    //
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * 不能为空，请确保Key值的唯一行
     */
    @Override
    public void setTestKey(String key) {
        this.testKey = key;
    }

    @Override
    public String getTestKey() {
        return testKey;
    }

    @Override
    public void setSmsMsgSendType(SmsMsgSendType smsMsgSendType) {

    }

    @Override
    public SmsMsgSendType getSmsMsgSendType() {
        return null;
    }
}
