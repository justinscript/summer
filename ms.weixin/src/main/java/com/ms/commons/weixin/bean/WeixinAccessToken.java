package com.ms.commons.weixin.bean;

import java.io.Serializable;

public class WeixinAccessToken extends WeixinResult implements Serializable {

    private static final long serialVersionUID = -3724872464368526234L;
    private String            accessToken;
    private long              effectTime;

    /**
     * 构造器
     * 
     * @param accessToken
     * @param effectIn
     */
    public WeixinAccessToken() {
        this.effectTime = System.currentTimeMillis() + 7200 * 1000;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @return the effectTime
     */
    public long getEffectTime() {
        return effectTime;
    }

    /**
     * @param accessToken the accessToken to set
     */
    public void setAccess_token(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @param effectTime the effectTime to set
     */
    public void setExpires_in(long expires_in) {
        this.effectTime = System.currentTimeMillis() + 7200 * 1000;
    }

    public boolean isEffect() {
        return effectTime > System.currentTimeMillis();
    }
}
