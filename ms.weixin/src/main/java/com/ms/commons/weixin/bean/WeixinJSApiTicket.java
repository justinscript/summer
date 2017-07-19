package com.ms.commons.weixin.bean;

import java.io.Serializable;

/**
 * <pre>
 * {
 * "errcode":0,
 * "errmsg":"ok",
 * "ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA",
 * "expires_in":7200
 *  }
 * </pre>
 */
public class WeixinJSApiTicket extends WeixinResult implements Serializable {

    private static final long serialVersionUID = 7108787066469747475L;
    private String            ticket;
    private int               expires_in;
    private long              effectTime;

    public WeixinJSApiTicket() {

    }

    /**
     * @return the ticket
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * @param ticket the ticket to set
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * @return the expires_in
     */
    public int getExpires_in() {
        return expires_in;
    }

    /**
     * @param expires_in the expires_in to set
     */
    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
        this.effectTime = System.currentTimeMillis() + expires_in * 1000;
    }

    public boolean isEffect() {
        return effectTime > System.currentTimeMillis();
    }
}
