/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl.sender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.cons.SmsMsgSendType;
import com.ms.commons.message.impl.MessageSerivceException;

/**
 * @author zxc Apr 13, 2014 10:42:58 PM
 */
public class DebugSmsSender extends AbstractSmsSender {

    // 信息发送文件路经
    // private String msgSavePath;
    // 日志记录器
    private static final ExpandLogger logger        = LoggerFactoryWrapper.getLogger(DebugSmsSender.class);
    // 组合信息，减少临时对象的创建
    private StringBuilder             stringBuilder = new StringBuilder(100);
    // 格式化日期
    private SimpleDateFormat          simpleFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 为测试而添加的字段变量
    private Map<String, String>       msgHistory    = new HashMap<String, String>();
    // 为单元测试而添加的字段变量
    static boolean                    IS_UNIT_TEST  = false;

    /**
     * 查询短信发送记录
     * 
     * @return the msgHistory
     */
    public String getMsgHistory(String phone) {
        return msgHistory.get(phone);
    }

    /**
     * 删除历史记录
     * 
     * @param phone
     * @return
     */
    public String removeMsgHistory(String phone) {
        return msgHistory.remove(phone);
    }

    /**
     * 覆盖父类方法，具体实现Message的发送
     */
    public String doSend(String content, String phoneNo, SmsMsgSendType smsMsgSendType) throws MessageSerivceException {
        try {

            Date date = new Date();
            stringBuilder.append(simpleFormat.format(date));
            stringBuilder.append(" To<");
            stringBuilder.append(phoneNo);
            stringBuilder.append("> Content<");
            stringBuilder.append(content);
            if (smsMsgSendType != null) {
                stringBuilder.append("> type<" + smsMsgSendType.getName());
            }
            stringBuilder.append(">\n");
            // 写到日志文件
            String msg = stringBuilder.toString();
            logger.info(msg);
            // 将短信内容记录到内存中，
            if (IS_UNIT_TEST) {
                msgHistory.put(phoneNo, content);
            }
            // File file = getCurrentLoggerFile();
            // RandomAccessFile raf = new RandomAccessFile(file, "rw");
            // raf.seek(file.length());
            // raf.write(stringBuilder.toString().getBytes());
            // raf.close();
        } catch (Exception e) {
            logger.error("信息发送给[" + phoneNo + "]失败！", e);
            throw new MessageSerivceException("信息发送给[" + phoneNo + "]失败！", e);
        }

        return "success";
    }

    @Override
    public boolean is4Debug() {
        return true;
    }

    //
    // /**
    // * 需要由Spring注入
    // *
    // * @return
    // */
    // public String getMsgSavePath() {
    // if (msgSavePath == null) {
    // msgSavePath = System.getProperty("user.dir");
    // if (msgSavePath == null) {
    // msgSavePath = getClass().getClassLoader().getResource(".").getPath();
    // }
    // logger.info("信息将会存储在[" + msgSavePath + "]目录中。");
    // }
    // return msgSavePath;
    // }
    //
    // /**
    // * 获得完整的日志文件
    // *
    // * @return
    // */
    // protected File getCurrentLoggerFile() {
    // String loggerFile = getMsgSavePath();
    // int num = 0;
    // String name = "DebugSmsSender_";
    // File file;
    // do {
    // file = new File(loggerFile + File.separator + name + num + ".txt");
    // num++;
    // } while (file.exists() && file.length() > 50280);
    //
    // return file;
    // }
    //
    // /**
    // * 日志路径
    // *
    // * @param loggerPath
    // */
    // public void setMsgSavePath(String loggerPath) {
    // this.msgSavePath = loggerPath;
    // }

    // public static void main(String a[]) {
    // System.out.println(new DebugSmsSender().getClass().getClassLoader().getResource(".").getPath());
    // }

}
