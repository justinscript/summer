/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl.sender;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ms.commons.config.service.ConfigServiceLocator;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.cons.SmsMsgSendType;
import com.ms.commons.message.impl.MessageSerivceException;

/**
 * @author zxc Apr 13, 2014 10:42:10 PM
 */
public class DefaultSmsSender extends AbstractSmsSender {

    /**
     * 维信公司短信管理平台 http://shsr.smsadmin.cn/
     */

    /**
     * 验证码通道短信 "http://60.28.200.150/submitdata/service.asmx";
     */

    private String                    ONTIME_URL           = "http://60.28.200.150/submitdata/service.asmx/g_Submit";

    private String                    ONTIME_USER          = "lhsh011";                                                   // "dlqdcs02";
                                                                                                                           // //
                                                                                                                           // "dlqdcs02";
                                                                                                                           // //
                                                                                                                           // "lhshdz03";
    private String                    ONTIME_PWD           = "87654321";                                                  // "qwAEIKMO";
                                                                                                                           // //
                                                                                                                           // "SjkQ35RS";
                                                                                                                           // //
    // 企业编码 // "87654321";
    private String                    ONTIME_SCORPID       = "";
    // 产品编码
    @SuppressWarnings("unused")
    private String                    ONTIME_SPRDID        = "1012818";
    /**
     * 普通群发短信URL
     */
    private String                    NORMAL_URL           = "http://www.smsadmin.cn/smsmarketing/wwwroot/api/get_send/?";
    private String                    NORMAL_USER          = "shry1114";
    private String                    NORMAL_PWD           = "!qaz3edc";
    // 群发短信用户名
    private static final String       NORMAL_SMS_USER_NAME = "S_commons.message.sms.username";
    // 群发短信用户密码
    private static final String       NORMAL_SMS_PWD       = "S_commons.message.sms.password";
    // 群发短信URL
    private static final String       NORMAL_SMS_URL       = "S_commons.message.sms.url";
    // 群发短信连接
    private HttpURLConnection         httpURLConnection;
    private String                    CHARSET_GB2312       = "gb2312";
    private String                    CHARSET_UTF8         = "UTF-8";
    // 群发短信格式
    private String                    contentType          = "text/html";

    // 日志记录器
    private static final ExpandLogger logger               = LoggerFactoryWrapper.getLogger(DefaultSmsSender.class);

    /**
     * 信息发送类型
     * 
     * <pre>
     * 1)验证码通道
     * 2)普通群发
     * </pre>
     * 
     * @author oracle Nov 18, 2011 10:15:27 AM
     */
    // public static enum MsgSendType {
    // /**
    // * 验证码短信
    // */
    // ONTIME,
    // /**
    // * 普通群发短信
    // */
    // NORMAL;
    // }

    // /**
    // * 目前只支持群发短信，不支持验证码通道短信
    // */
    // public String doSend(String content, String phoneNo, SmsMsgSendType smsMsgSendType) throws
    // MessageSerivceException {
    // return doSend(content, phoneNo, smsMsgSendType);
    // }

    // 调用短信供应商代码发送短信
    public String doSend(String content, String phoneNo, SmsMsgSendType smsMsgSendType) throws MessageSerivceException {
        {
            String rValue = "success";
            try {
                String parameter = buildMsg(content, phoneNo, smsMsgSendType);
                String result = null;
                switch (smsMsgSendType) {
                    case NORMAL:
                        parameter = getSmsURL() + parameter;
                        result = sendGet(parameter);
                        break;

                    case ONTIME:
                        result = ontimeSms(parameter, ONTIME_URL);
                        break;

                    default:
                        break;
                }
                if (result.indexOf('0') != -1) {
                    logger.info("成功发送短信到<" + phoneNo + ">,短信内容为:[" + content + "]");
                } else {
                    String errMsg = "ReturnCode=<" + result + ">.短信发送失败,短信接收人<" + phoneNo + ">,短信内容为:[" + content + "]";
                    logger.error(errMsg);
                    throw new MessageSerivceException(errMsg);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                String errMsg = "短信发送失败,短信接收人<" + phoneNo + ">,短信内容为:[" + content + "]" + e.getMessage();
                logger.error(errMsg);
                throw new MessageSerivceException(errMsg);
            }
            return rValue;
        }
    }

    @Override
    public boolean is4Debug() {
        return false;
    }

    /**
     * 组装短信发送URL，更具短信供应商API
     * 
     * @param content
     * @param phoneNo
     * @param msgSendType
     * @return
     */
    private String buildMsg(String content, String phoneNo, SmsMsgSendType smsMsgSendType) {
        Map<String, String> map = buildMap(content, phoneNo, smsMsgSendType);
        Iterator<String> iter = map.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        String key, value;

        while (iter.hasNext()) {
            key = iter.next();
            value = map.get(key);
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(key);
            sb.append("=");
            sb.append(value);
        }

        return sb.toString();
    }

    /**
     * 组装URL参数
     * 
     * @param content
     * @param phoneNo
     * @param msgSendType
     * @return
     */
    private Map<String, String> buildMap(final String content, final String phoneNo, final SmsMsgSendType smsMsgSendType) {
        Map<String, String> map = new HashMap<String, String>();
        switch (smsMsgSendType) {
            case ONTIME:
                map.put("sname", ONTIME_USER);
                map.put("spwd", ONTIME_PWD);
                map.put("sprdid", "1012818");
                map.put("scorpid", ONTIME_SCORPID);
                map.put("sdst", phoneNo);
                try {
                    map.put("smsg", java.net.URLEncoder.encode(content, CHARSET_UTF8));
                } catch (Exception e) {
                    map.put("smsg", content);
                }
                break;

            case NORMAL:
            default:
                map.put("uid", getSmsUsername());
                map.put("pwd", getSmsPWD());
                map.put("mobile", phoneNo);
                try {
                    map.put("msg", java.net.URLEncoder.encode(content, CHARSET_GB2312));
                } catch (Exception e) {
                    map.put("msg", content);
                }
                break;
        }
        return map;
    }

    // /**
    // * 调试代码
    // *
    // * @param args
    // */
    // public static void main(String[] args) {
    // DefaultSmsSender send = new DefaultSmsSender();
    // // send.doSend("^o^甲：你怎么老是做家务啊？乙：与媳妇来剪子、石头、布，我总是输，输了就得做家务！甲：你一次没赢过？乙：是的，媳妇规定我每次只准出石头,悦己网络科技有限公司！",
    // // "13621603604",
    // // MsgSendType.ONTIME);
    // send.doSend("测试", "13621603604" /* "13921176345;18662644287;18606232372" *//*
    // * / /
    // * "13621603604;18606232372;18606232435"
    // */);
    // }

    /**
     * 查询发送短信的用户名
     */
    public String getSmsUsername() {
        return ConfigServiceLocator.getCongfigService().getKV(NORMAL_SMS_USER_NAME, NORMAL_USER);
    }

    /**
     * 查询发送短信的用户密码
     */
    public String getSmsPWD() {
        return ConfigServiceLocator.getCongfigService().getKV(NORMAL_SMS_PWD, NORMAL_PWD);
    }

    /**
     * 查询发送短信的URL
     */
    public String getSmsURL() {
        return ConfigServiceLocator.getCongfigService().getKV(NORMAL_SMS_URL, NORMAL_URL);
    }

    /**
     * @param url
     * @return
     */
    private String sendGet(String url) throws Exception {
        try {
            creatConnection(url);
            httpURLConnection.setRequestMethod("GET");
            return receiveMessage(httpURLConnection);
        } catch (IOException io) {
            throw io;
        } finally {
            closeConnection();
        }
    }

    /**
     * 创建连接
     * 
     * @param url
     */
    private void creatConnection(String url) throws Exception {
        try {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            httpURLConnection = ((HttpURLConnection) new URL(url).openConnection());
            httpURLConnection.setRequestProperty("Content-Type", contentType + "; " + CHARSET_GB2312);
        } catch (IOException io) {
            throw io;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 关闭连接
     */
    private void closeConnection() {
        try {
            if (httpURLConnection != null) httpURLConnection.disconnect();

        } catch (Exception ex) {
        }
    }

    /**
     * 接收信息
     * 
     * @param httpURLConnection
     * @return
     */
    private String receiveMessage(HttpURLConnection httpURLConnection) throws Exception {
        String responseBody = null;
        try {

            InputStream httpIn = httpURLConnection.getInputStream();

            if (httpIn != null) {

                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                byte tempByte;
                while (-1 != (tempByte = (byte) httpIn.read()))
                    byteOut.write(tempByte);
                responseBody = new String(byteOut.toByteArray(), CHARSET_GB2312);
            }
        } catch (IOException ioe) {
            throw ioe;
        }
        return responseBody;
    }

    /**
     * 验证码发送通道
     */
    private String ontimeSms(String postData, String postUrl) throws Exception {
        try {
            // 发送POST请求
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Length", "" + postData.length());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), CHARSET_UTF8);
            out.write(postData);
            out.flush();
            out.close();

            // 获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "" + conn.getResponseCode();
            }
            // 获取响应内容体
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET_UTF8));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            return result;
        } catch (IOException e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        DefaultSmsSender sender = new DefaultSmsSender();
        // sender.doSend("明天技术部要去上海出差", "13621603604", SmsMsgSendType.ONTIME);
        sender.doSend("测试验证码短信发送" + (new Date()), "13621603604", SmsMsgSendType.ONTIME);
    }
}
