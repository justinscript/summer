/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.valve;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ms.app.web.commons.utils.InvokeTypeTools;
import com.ms.app.web.commons.utils.JSONPResultUtils;
import com.ms.app.web.commons.utils.JsonResultUtils;
import com.ms.commons.cookie.CookieKeyEnum;
import com.ms.commons.cookie.manager.CookieManager;
import com.ms.commons.cookie.manager.CookieManagerLocator;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.summer.web.pipeline.AbstractPipelineValves;
import com.ms.commons.summer.web.pipeline.PipelineMap;
import com.ms.commons.summer.web.pipeline.PipelineResult;

/**
 * @author zxc Apr 12, 2013 11:07:14 PM
 */
public class AccessControlValue extends AbstractPipelineValves {

    private static ExpandLogger logger = LoggerFactoryWrapper.getLogger(AccessControlValue.class);

    private List<String>        noCheckUrlList;
    private List<String>        noCheckUrlLikeList;
    private String              normalUrl;                                                        // 普通验证需要跳转的登录URL

    /***
     * 默认是需要登录的
     */
    public PipelineResult invoke(HttpServletRequest request, HttpServletResponse response, PipelineMap map)
                                                                                                           throws Exception {
        BaseWebUser webUser = BaseWebUser.getCurrentUser();
        CookieManager cookieManager = CookieManagerLocator.get(request, response);
        // 已登陆，更新最后访问时间
        if (webUser != null && webUser.isHasLogin()) {
            cookieManager.set(CookieKeyEnum.last_access_time, String.valueOf(System.currentTimeMillis()));
            return null;
        }
        String uri = request.getRequestURI();
        boolean canAnonymousAccess = canAnonymousAccess(uri);
        if (canAnonymousAccess) {
            return null;
        }
        String qs = request.getQueryString();
        if (qs != null) {
            uri = uri + "?" + qs;
        }
        boolean isAjax = InvokeTypeTools.isAjax(request);
        if (isAjax) {
            String callback = request.getParameter("callback");
            if (StringUtils.isEmpty(callback)) {
                String needLogin = JsonResultUtils.getNeedLoginJson();
                response.getOutputStream().write(needLogin.getBytes("utf-8"));
            } else {
                String needLogin = JSONPResultUtils.getNeedLoginJson(callback);
                response.getOutputStream().write(needLogin.getBytes("utf-8"));
            }
            return PipelineResult.gotoFinally("gotoLogin", null);
        } else {
            String returnUrl = request.getParameter("returnurl");
            String gotoUrl = normalUrl;
            gotoUrl = gotoUrl + "?returnurl=" + (returnUrl == null ? uri : returnUrl);
            logger.info("gotouri=" + gotoUrl);
            return PipelineResult.gotoFinally("gotoLogin", gotoUrl);
        }

    }

    /**
     * 判断一个URI是否存在于匿名访问列表中，从而判断是否可以匿名访问
     * 
     * @return 如果可以匿名访问返回<code>true</code>,否则返回<code>false</code>
     */
    private boolean canAnonymousAccess(String uri) {
        logger.info("uri=" + uri);
        // 1.noCheckUrlList 是精确匹配列表
        if (noCheckUrlList != null && noCheckUrlList.contains(uri)) {
            return true;
        }
        // 2. noCheckUrlLikeList 是模糊匹配列表
        if (noCheckUrlLikeList != null) {
            for (String s : noCheckUrlLikeList) {
                int index = s.lastIndexOf("*");
                if (index != -1) {
                    s = s.substring(0, index);
                    if (uri.startsWith(s)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setNoCheckUrlList(List<String> noCheckUrlList) {
        this.noCheckUrlList = noCheckUrlList;
    }

    public void setNoCheckUrlLikeList(List<String> noCheckUrlLikeList) {
        this.noCheckUrlLikeList = noCheckUrlLikeList;
    }

    public void setNormalUrl(String normalUrl) {
        this.normalUrl = normalUrl;
    }
}
