/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.cookie;

import com.ms.commons.cookie.annotation.CookieKeyPolicy;

/**
 * @author zxc Apr 12, 2014 7:35:24 PM
 */
public enum CookieKeyEnum {

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 顶级域(.msun.com)的Cookie
    //
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * wap_profile记录浏览器的分辨率及操作系统等
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    wap_profile("wap_profile"),

    /**
     * Cookie_id 只要用户访问过我们的站点就会有这样一个值
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    cookie_id("c_id"),

    /** ----------------------以下是用户登录后需要重写的Cookie----------------------- **/

    /**
     * 会员cookie的版本号
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    version("y_c_v"),

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 以下www.msun.com的Cookie
    //
    // //////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 会员帐号的ID
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    account_id("y_a_id"),
    /**
     * 会员信息的ID
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    member_id("y_m_id"),
    /**
     * 会员的Email
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    email("y_m_e"),
    /**
     * 会员的mobile
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    mobile("m_mo"),
    /**
     * 会员的identity唯一标识
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    member_identity("y_m_iden"),
    /**
     * 会员的type
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    member_type("y_m_ty"),
    /**
     * 会员的nickname
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    member_nickname("y_m_nn"),
    /**
     * 第三方平台登陆的accesstoken
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    member_access_token("y_m_at"),
    /**
     * 会员上一次访问时间
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_last_login)
    last_access_time("y_l_a_t"),

    /**
     * 会员最近活动时间
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_cookie_forever)
    last_active_time("y_l_ac_t"),

    /**
     * 跟踪点击的类型
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_click_track)
    ts_ct_type("ct_t"),
    /**
     * 来源的ID
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_click_track)
    ts_ct_from("ct_fr"),
    /**
     * 到达的ID
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_click_track)
    ts_ct_to("ct_to"),
    /**
     * 用户来自那个网站
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_source)
    ys_source("ys_so"),
    /**
     * baidu跟踪字段hmsr来源
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_source)
    ys_hmsr("hmsr"),
    /**
     * baidu跟踪字段hmmd媒介
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_source)
    ys_hmmd("hmmd"),
    /**
     * baidu跟踪字段hmpl计划
     */
    @CookieKeyPolicy(withinCooKieName = CookieNameEnum.msun_source)
    ys_hmpl("hmpl");

    private String key;

    private CookieKeyEnum(String cookieKey) {
        this.key = cookieKey;
    }

    public String getKey() {
        return key;
    }

    public static CookieKeyEnum getEnum(String key) {
        for (CookieKeyEnum cookieKey : values()) {
            if (cookieKey.getKey().equals(key)) {
                return cookieKey;
            }
        }
        return null;
    }

    public String toString() {
        return name();
    }
}
