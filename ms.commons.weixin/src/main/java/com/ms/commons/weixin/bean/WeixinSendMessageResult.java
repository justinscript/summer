package com.ms.commons.weixin.bean;

public class WeixinSendMessageResult extends WeixinResult {

    public boolean isOutOffResponseTime() {
        return errcode != null && errcode.intValue() == 45015;
    }
}
