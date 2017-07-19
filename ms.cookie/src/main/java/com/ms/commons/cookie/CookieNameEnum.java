/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.cookie;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.cookie.annotation.CookieDomain;
import com.ms.commons.cookie.annotation.CookieMaxAge;
import com.ms.commons.cookie.annotation.CookieNamePolicy;

/**
 * 一个Cookie组，其实就是Servlet 中一个Cookie项，他的Name就是Servelet Cookie中的Name
 * 
 * @author zxc Apr 12, 2014 7:35:52 PM
 */
public enum CookieNameEnum {

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 顶级域(.msun.com)的Cookie
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 永久Cookie
     */
    @CookieNamePolicy(domain = CookieDomain.DOT_MSUN_COM)
    msun_cookie_forever("_cf_"),
    /**
     * last_login_time的Cookie name ，因为last_login_time时一个频繁变化的值。需要希望将他单独提出来
     */
    @CookieNamePolicy(domain = CookieDomain.DOT_MSUN_COM)
    msun_last_login("_cfl_"),
    /**
     * 临时Cookie
     */
    @CookieNamePolicy(domain = CookieDomain.DOT_MSUN_COM, maxAge = CookieMaxAge.TEMP)
    msun_cookie_temp("_ct_"),
    /**
     * 明文的全局数据，可以供前端读写
     */
    @CookieNamePolicy(domain = CookieDomain.DOT_MSUN_COM, isEncrypt = false, isSimpleValue = true, maxAge = CookieMaxAge.FOREVER)
    msun_cookie_gdata("_gdt_"),
    /**
     * 用于跟踪点击等信息
     */
    @CookieNamePolicy(domain = CookieDomain.DOT_MSUN_COM, maxAge = CookieMaxAge.TEMP)
    msun_click_track("_yct_"),
    /**
     * 用户记录用户签名的cookie
     */
    @CookieNamePolicy(domain = CookieDomain.DOT_MSUN_COM, isEncrypt = false, isSimpleValue = true)
    msun_signature("ysig"),
    /**
     * 用于跟踪来源
     */
    @CookieNamePolicy(domain = CookieDomain.DOT_MSUN_COM)
    msun_source("_yso_"),
    /**
     * 未读消息个数
     */
    @CookieNamePolicy(domain = CookieDomain.DOT_MSUN_COM, isEncrypt = false, isSimpleValue = true, maxAge = CookieMaxAge.FOREVER)
    msg_count("msg_count"),
    /**
     * token的cookie
     */
    @CookieNamePolicy(domain = CookieDomain.WWW_MSUN_COM, isEncrypt = true, isSimpleValue = true, maxAge = CookieMaxAge.TEMP)
    msun_cookie_tooken("_utk_"),
    /**
     * check code的存储值
     */
    @CookieNamePolicy(domain = CookieDomain.WWW_MSUN_COM, isEncrypt = true, isSimpleValue = true, maxAge = CookieMaxAge.FOREVER)
    msun_checkcode("_ucc_");

    private String cookieName;

    private CookieNameEnum(String cookieName) {
        this.setCookieName(cookieName);
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String toString() {
        return name();
    }

    public static CookieNameEnum getEnum(String name) {
        for (CookieNameEnum cookieNameEnum : values()) {
            if (StringUtils.equals(name, cookieNameEnum.getCookieName())) return cookieNameEnum;
        }
        return null;
    }
}
