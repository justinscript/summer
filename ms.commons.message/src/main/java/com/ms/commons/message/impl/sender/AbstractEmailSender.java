/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl.sender;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.cons.MessageTypeEnum;
import com.ms.commons.message.impl.MessageSerivceException;
import com.ms.commons.message.impl.email.MsunMail;
import com.ms.commons.message.impl.email.MsunMailAttachment;
import com.ms.commons.message.interfaces.Message;
import com.ms.commons.message.interfaces.Sender;
import com.ms.commons.message.utils.MessageUtil;

/**
 * @author zxc Apr 13, 2014 10:44:09 PM
 */
public abstract class AbstractEmailSender implements Sender {

    // 日志记录器
    private static final ExpandLogger logger      = LoggerFactoryWrapper.getLogger(AbstractEmailSender.class);

    // 需要正确初始化
    protected String                  hostName    = "192.168.1.190";                                          // "smtp.163.com";
    // 是否进行验证
    protected boolean                 auth        = true;
    // 发送人
    protected String                  user        = "support@msun.com";                                      // "msun_01@163.com";
    // 密码
    protected String                  password    = "123456";                                                 // "!qaz2wsx";
    // 缺省发送入
    protected String                  defaultFrom = "support@msun.com";                                      // "msun_01@163.com";
    // 缺省发送到
    protected String                  defaultTo   = "";

    @Override
    public void send(Message message) {
        if (message.getMessageType().equals(MessageTypeEnum.email)) {
            MsunMail msunEmail = (MsunMail) message;
            try {
                doSend(msunEmail);
                // 输出日志信息
                if (logger.isDebugEnabled()) {
                    logger.debug("Email发送成功! " + msunEmail.dumpInfo());
                }
            } catch (Exception ex) {
                logger.error("Email发送失败! " + msunEmail.dumpInfo(), ex);
                throw new MessageSerivceException("Email发送失败!", ex);
            }
        } else {
            logger.error("Internal failure, wrong Message type, the expected Message type is Email!");
        }
    }

    public boolean support(MessageTypeEnum messageType) {
        return MessageTypeEnum.email.equals(messageType);
    }

    public abstract void doSend(MsunMail msunEmail) throws MessageSerivceException;

    /**
     * 创建真实的Email
     * 
     * @param mail
     * @return
     * @throws Exception
     */
    protected Email getEmail(MsunMail mail) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("send mail use smth : " + hostName);
        }
        // set env for logger
        mail.getEnvironment().setHostName(hostName);
        mail.getEnvironment().setUser(user);
        mail.getEnvironment().setPassword(password);

        Email email = null;

        if (!StringUtils.isEmpty(mail.getHtmlMessage())) {
            email = makeHtmlEmail(mail, mail.getCharset());
        } else {
            if ((mail.getAttachment() == null || mail.getAttachment().length == 0) && mail.getAttachments().isEmpty()) {
                email = makeSimpleEmail(mail, mail.getCharset());
            } else {
                email = makeSimpleEmailWithAttachment(mail, mail.getCharset());
            }
        }

        if (auth) {
            // email.setAuthenticator(new MyAuthenticator(user, password));
            email.setAuthentication(user, password);
        }
        email.setHostName(hostName);

        if (mail.getTo() == null) {
            mail.setTo(defaultTo.split(";"));
        }

        if (StringUtils.isEmpty(mail.getFrom())) {
            mail.setFrom(defaultFrom);
        }

        email.setFrom(mail.getFrom(), mail.getFromName());

        List<String> unqualifiedReceiver = mail.getUnqualifiedReceiver();
        String[] mailTo = mail.getTo();
        String[] mailCc = mail.getCc();
        String[] mailBcc = mail.getBcc();
        if (unqualifiedReceiver != null && unqualifiedReceiver.size() > 0) {
            if (mailTo != null && mailTo.length > 0) {
                mailTo = filterReceiver(mailTo, unqualifiedReceiver);
            }
            if (mailCc != null && mailCc.length > 0) {
                mailCc = filterReceiver(mailCc, unqualifiedReceiver);
            }
            if (mailBcc != null && mailBcc.length > 0) {
                mailBcc = filterReceiver(mailBcc, unqualifiedReceiver);
            }
        }

        if (mailTo == null && mailCc == null && mailBcc == null) {
            throw new MessageSerivceException("信息接收者为空，无法发送该消息！");
        }

        int count = 0;

        if (mailTo != null) {
            count += mailTo.length;
            for (String to : mailTo) {
                email.addTo(to);
            }
        }

        if (mailCc != null) {
            count += mailCc.length;
            for (String cc : mailCc) {
                email.addCc(cc);
            }
        }
        if (mailBcc != null) {
            count += mailBcc.length;
            for (String bcc : mailBcc) {
                email.addBcc(bcc);
            }
        }

        if (count < 1) {
            throw new MessageSerivceException("信息接收者为空，无法发送该消息！");
        }

        if (!StringUtils.isEmpty(mail.getReplyTo())) {
            email.addReplyTo(mail.getReplyTo());
        }

        if (mail.getHeaders() != null) {
            for (Iterator<String> iter = mail.getHeaders().keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                email.addHeader(key, (String) mail.getHeaders().get(key));
            }
        }

        email.setSubject(mail.getSubject());

        // 加附件
        if (email instanceof MultiPartEmail) {
            MultiPartEmail multiEmail = (MultiPartEmail) email;

            if (mail.getAttachments().isEmpty()) {
                File[] fs = mail.getAttachment();
                if (fs != null && fs.length > 0) {
                    for (int i = 0; i < fs.length; i++) {
                        EmailAttachment attachment = new EmailAttachment();
                        attachment.setPath(fs[i].getAbsolutePath());
                        attachment.setDisposition(EmailAttachment.ATTACHMENT);
                        attachment.setName(MimeUtility.encodeText(fs[i].getName()));// 处理中文附件名称
                        multiEmail.attach(attachment);
                    }
                }
            } else {
                for (MsunMailAttachment attachment : mail.getAttachments()) {
                    DataSource ds = new ByteArrayDataSource(attachment.getData(), null);
                    multiEmail.attach(ds, MimeUtility.encodeText(attachment.getName()), null);
                }
            }
        }
        return email;
    }

    private MultiPartEmail makeHtmlEmail(MsunMail mail, String charset) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setCharset(charset);
        if (!StringUtils.isEmpty(mail.getMessage())) {
            email.setTextMsg(mail.getMessage());
        }
        email.setHtmlMsg(mail.getHtmlMessage());
        return email;
    }

    private MultiPartEmail makeSimpleEmailWithAttachment(MsunMail mail, String charset) throws EmailException {
        MultiPartEmail email = new MultiPartEmail();
        email.setCharset(charset);
        email.setMsg(mail.getMessage());
        return email;
    }

    private SimpleEmail makeSimpleEmail(MsunMail mail, String charset) throws EmailException {
        SimpleEmail email = new SimpleEmail();
        email.setCharset(charset);
        email.setContent(mail.getMessage(), "text/plain" + ";charset=" + charset);
        return email;
    }

    /**
     * 过滤掉未通过验证的信息接收者
     * 
     * @param receivers
     * @return
     */
    protected String[] filterReceiver(String[] receivers, List<String> unqualifiedReceiver) {
        for (int i = 0; i < receivers.length; i++) {
            if (unqualifiedReceiver.contains(receivers[i])) {
                receivers[i] = null;
            }
        }
        receivers = MessageUtil.removeEmptyElement(receivers);
        return receivers;
    }
}
