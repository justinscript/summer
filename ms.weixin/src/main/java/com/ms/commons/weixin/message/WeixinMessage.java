package com.ms.commons.weixin.message;

public class WeixinMessage {

    private String touser;
    private String msgtype;

    /**
     * 构造器
     * 
     * @param touser
     * @param msgtype
     */
    public WeixinMessage(String touser, String msgtype) {
        this.touser = touser;
        this.msgtype = msgtype;
    }

    /**
     * @return the touser
     */
    public String getTouser() {
        return touser;
    }

    /**
     * @return the msgtype
     */
    public String getMsgtype() {
        return msgtype;
    }
}
