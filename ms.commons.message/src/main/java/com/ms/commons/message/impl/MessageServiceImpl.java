/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ms.commons.nisa.interfaces.ConfigService;
import com.ms.commons.nisa.listener.ConfigListener;
import com.ms.commons.nisa.service.ConfigServiceLocator;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.cons.MessageTypeEnum;
import com.ms.commons.message.interfaces.Filter;
import com.ms.commons.message.interfaces.Message;
import com.ms.commons.message.interfaces.MessageService;
import com.ms.commons.message.interfaces.Sender;

/**
 * @author zxc Apr 13, 2014 10:41:40 PM
 */
public class MessageServiceImpl implements MessageService {

    // 日志
    private static final ExpandLogger logger                   = LoggerFactoryWrapper.getLogger(MessageServiceImpl.class);
    /**
     * 过滤器
     */
    private ArrayList<Filter>         filters;
    /**
     * 具体发送者,目前暂且把Email和短信发送实现类放置在一起，更有效的方法是将他们分开
     */
    private ArrayList<Sender>         senders;

    /**
     * 自动测试开关
     */
    private ArrayList<Sender>         autoTestSenders;
    /**
     * 是否打开email调试功能
     */
    private String                    debugEmailKey            = "B_commons.message.email.debug";
    /**
     * 是否需要关闭email发送功能
     */
    private String                    emailSwitch              = "B_commons.message.email.switch";

    /**
     * 是否打开短信调试
     */
    private String                    debugSmsKey              = "B_commons.message.sms.debug";

    /**
     * 是否需要关闭短信发送功能
     */
    private String                    smsSwitch                = "B_commons.message.sms.switch";

    /**
     * <pre>
     * 为支持web Test自动化测试而设置的开关，当该值为true时，系统会为辅助自动化测试而多处理一些事情，
     * 比如说写入某些数据到数据库或文件，供自动化测试进行验证.
     * 
     * 可以在Nisa配置中心对该值进行配置
     * </pre>
     */
    public static final String        S_commons_webtest_switch = "S_commons_webtest_switch";

    /**
     * 构建器
     */
    public MessageServiceImpl() {
    }

    @Override
    public void send(Message message) throws MessageSerivceException {
        // 1.过一些列规则，看看是不是重复风暴发送
        Filter filter;
        List<String> unqualifiedReceiver = null;
        if (filters != null) {
            for (int i = 0; i < filters.size(); i++) {
                filter = filters.get(i);
                if (filter != null) {
                    List<String> list = filter.doFilter(message);
                    if (list != null) {
                        if (unqualifiedReceiver == null) {
                            unqualifiedReceiver = new ArrayList<String>();
                        }
                        unqualifiedReceiver.addAll(list);
                    }
                }
            }
        }
        if (unqualifiedReceiver == null) {
            unqualifiedReceiver = Collections.emptyList();
        } else {
            // 有规则不通过
            logger.warn("如下信息接收者未能通过信息验证，将不会收到本次发送出的信息！" + dumpListInfo(unqualifiedReceiver));
        }
        if (message.getAllReceiver().length == unqualifiedReceiver.size()) {
            logger.warn("由于所有信息接收着未能通过验证，本次信息发送取消" + message.dumpInfo());
            return;
        }
        message.setUnqualifiedReceiver(unqualifiedReceiver);

        try {
            MessageTypeEnum messageType = message.getMessageType();
            if (senders != null && senders.size() > 0) {
                // 优先处理调试
                if (isDebug(messageType)) {
                    for (Sender sender : senders) {
                        if (sender.is4Debug() && sender.support(messageType)) {
                            sender.send(message);
                            if (logger.isDebugEnabled()) {
                                logger.debug("消息发送成功到调试器! " + message.dumpInfo());
                            }
                        }
                    }
                }
                // 真正处理发送短信或邮件
                if (isMessageSwitchOn(messageType)) {
                    for (Sender sender : senders) {
                        if (!sender.is4Debug() && sender.support(messageType)) {
                            sender.send(message);
                            if (logger.isDebugEnabled()) {
                                logger.debug("消息发送成功! " + message.dumpInfo());
                            }
                        }
                    }

                } else {
                    logger.warn("邮件和短信发送功能邮件关闭，发送被取消，如果需要打开邮件和短信功能，请到Nisa配置中心进行修改！");
                }
            } else {
                logger.error("由于Sender为空，信息未发送成功，请检查Spring中的Sender的配置！" + message.dumpInfo());
            }
            // 处理自动测试
            if (autoTestSenders != null && isAutoTestSwitchOn()) {
                for (Sender sender : autoTestSenders) {
                    sender.send(message);
                }
            }
        } catch (Exception e) {
            logger.error("发送邮件或短信失败", e);
            e.printStackTrace();
            throw new MessageSerivceException(e);
        }
    }

    /**
     * 设置过滤器，由Spring负责注入
     * 
     * @param filters
     */
    public void setFilters(ArrayList<Filter> filters) {
        this.filters = filters;
    }

    /**
     * @param autoTestSenders
     */
    public void setAutoTestSenders(ArrayList<Sender> autoTestSenders) {
        this.autoTestSenders = autoTestSenders;
    }

    public void addAutoTestSender(Sender sender) {
        if (sender != null) {
            if (autoTestSenders == null) {
                autoTestSenders = new ArrayList<Sender>();
            }
            autoTestSenders.add(sender);
        }
    }

    /**
     * @param senders
     */
    public void setSenders(ArrayList<Sender> senders) {
        this.senders = senders;
    }

    /**
     * 将List中的内容转换为String
     * 
     * @param list
     * @return
     */
    private String dumpListInfo(List<String> list) {
        if (list == null || list.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = list.size(); i < size; i++) {
            sb.append('<' + list.get(i) + '>' + ' ');
        }
        return sb.toString();
    }

    /**
     * 从配置中心动态读取调试开关数据
     * 
     * @return
     */
    public boolean isDebug(MessageTypeEnum messageType) {
        boolean result = false;
        switch (messageType) {
            case email:
                result = ConfigServiceLocator.getCongfigService().getKV(debugEmailKey, false);
                break;
            case sms:
                result = ConfigServiceLocator.getCongfigService().getKV(debugSmsKey, false);
                break;
        }
        return result;
    }

    /**
     * 从配置中心读取短信和邮件发送开关
     * 
     * @param messageType
     * @return
     */
    public boolean isMessageSwitchOn(MessageTypeEnum messageType) {
        boolean result = true;
        switch (messageType) {
            case email:
                result = ConfigServiceLocator.getCongfigService().getKV(emailSwitch, true);
                break;
            case sms:
                result = ConfigServiceLocator.getCongfigService().getKV(smsSwitch, true);
                break;
        }
        return result;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    //
    // 以下方法是为增强代码的的可测试行而添加
    //
    // /////////////////////////////////////////////////////////////////////////////////////

    /**
     * 动态查询Web自动测试开关是否打开
     * 
     * @return true表示打开
     */
    public static boolean isAutoTestSwitchOn() {

        return ConfigServiceLocator.getCongfigService().getKV(ConfigService.KEY_RESOURCE_WEB_TEST_TRACE, false);
    }

    /**
     * 注册一个新的信息发送者
     * 
     * @param sender
     */
    public void registerSender(Sender sender) {
        if (sender != null) {
            senders.add(sender);
        }
    }

    /**
     * 删除一个已经注册的信息发送者
     * 
     * @param sender
     */
    public void unregisterSender(Sender sender) {
        if (sender != null) {
            senders.remove(sender);
        }
    }
}
