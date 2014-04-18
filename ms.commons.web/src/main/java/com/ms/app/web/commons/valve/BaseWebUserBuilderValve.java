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

import com.ms.app.web.commons.request.RequestDigger;
import com.ms.app.web.commons.tools.StaticsTools;
import com.ms.commons.cookie.manager.CookieManager;
import com.ms.commons.cookie.manager.CookieManagerLocator;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.summer.web.pipeline.AbstractPipelineValves;
import com.ms.commons.summer.web.pipeline.PipelineMap;
import com.ms.commons.summer.web.pipeline.PipelineResult;

/**
 * 公用WebUser构建逻辑
 * 
 * @author zxc Apr 12, 2013 11:14:51 PM
 */
public abstract class BaseWebUserBuilderValve<T extends BaseWebUser> extends AbstractPipelineValves {

    protected static ExpandLogger logger               = LoggerFactoryWrapper.getLogger(BaseWebUserBuilderValve.class);
    private String                noPermissionUrl      = "/login/nopermission.htm";
    private String                loginUrl             = "/login/login.htm";
    private List<String>          noCheckUrlLikeList;
    private List<String>          noCheckUrlList;
    // 默认的最大登陆状态保存时间
    protected static final long   MAX_LAST_ACCESS_TIME = 1000 * 3600 * 12;

    protected boolean             checkUrl;

    public PipelineResult invoke(HttpServletRequest request, HttpServletResponse response, PipelineMap map)
                                                                                                           throws Exception {

        // 设置下static资源的debug模式
        StaticsTools.setDebugModeIfEixisted(request);

        // 保存当前请求的信息
        RequestDigger.saveRequestInfo(request);

        String uri = request.getRequestURI();
        logger.info("<value>" + uri + "</value>");

        // 1. 构建WebUser对象
        CookieManager cookieManager = CookieManagerLocator.get(request, response);
        T webUser = createWebUser(request, cookieManager);

        // 2. 判断是否匿名访问
        boolean canAccessAnonymous = canAccessAnonymous(uri);
        if (!webUser.hasLogin()) {
            if (!canAccessAnonymous) {
                return getToLogin(request);
            } else {
                return null;
            }
        }

        // //////////////////////下面都是用户已经登陆的情况/////////////////////////////

        // 3. 判断访问时间(只针对已经登陆的情况,为了防止对login的重复调转，加上匿名访问的判断。注意把judgeAccessTime放前面，理面需要写最后一次访问时间）
        if (!judgeAccessTime(cookieManager) && !canAccessAnonymous) {
            return getToLogin(request);
        }

        // 4. 判断URL权限(只针对已经登陆的情况）
        if (!canAccessAnonymous && !judgePermission(request, uri, webUser)) {
            return PipelineResult.gotoFinally("gotoLogin", noPermissionUrl);
        }
        return null;
    }

    protected PipelineResult getToLogin(HttpServletRequest request) {
        String url = loginUrl;
        String uri = request.getRequestURI();
        // 现在参数不会传递，所以如果有参数的情况下就不追加returnurl了。
        if (request.getParameterMap().isEmpty() && !StringUtils.contains("/login", uri)) {
            url = loginUrl + "?returnurl=" + uri;
        }
        return PipelineResult.gotoFinally("gotoLogin", url);
    }

    /**
     * 判断一个URL是否可以匿名访问
     * 
     * @param uri
     * @return 如果可以匿名访问返回<code>true</code>否则返回<code>false</code>
     */
    private boolean canAccessAnonymous(String uri) {
        if (!checkUrl) {
            return true;
        }
        boolean needcheck = needcheck(uri);
        if (!needcheck) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前用户是否具有某个URL的访问权限。默认都是有权限的，子类需要覆写该方法以便实现符合自己网站业务逻辑的权限判断。
     * 
     * @return true 表示当前用户具有访问权限，否则返回false。
     */
    protected boolean judgePermission(HttpServletRequest request, String uri, T webUser) {
        return true;
    }

    /**
     * 判断当前用户的不活跃时间。默认都是返回true，子类需要按照自己的业务覆写。(只针对已经登陆的情况）
     * 
     * @return true 表示当前用户具有访问权限，否则返回false。
     */
    protected boolean judgeAccessTime(CookieManager cookieManager) {
        return true;
    }

    /**
     * 所有的自己必须按照自己的业务从Cookie或者存储中获取当前用户的信息。并且注意需要把构建的WebUser放到当前的线程缓存中(只针对已经登陆的情况）
     * 
     * @param request
     */
    protected abstract T createWebUser(HttpServletRequest request, CookieManager cookieManager);

    protected boolean needcheck(String uri) {
        if (noCheckUrlList != null && noCheckUrlList.contains(uri)) {
            return false;
        }
        if (noCheckUrlLikeList != null) {
            for (String s : noCheckUrlLikeList) {
                int index = s.lastIndexOf("*");
                if (index != -1) {
                    s = s.substring(0, index);
                    if (uri.startsWith(s)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected static long parserLong(String number) {
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setNoPermissionUrl(String noPermissionUrl) {
        this.noPermissionUrl = noPermissionUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setNoCheckUrlLikeList(List<String> noCheckUrlLikeList) {
        this.noCheckUrlLikeList = noCheckUrlLikeList;
    }

    public boolean isCheckUrl() {
        return checkUrl;
    }

    public void setCheckUrl(boolean checkUrl) {
        this.checkUrl = checkUrl;
    }

    public List<String> getNoCheckUrlList() {
        return noCheckUrlList;
    }

    public void setNoCheckUrlList(List<String> noCheckUrlList) {
        this.noCheckUrlList = noCheckUrlList;
    }
}
