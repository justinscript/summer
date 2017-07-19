/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.view.velocity;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;

import com.ms.commons.summer.web.servlet.result.WebResult;
import com.ms.commons.summer.web.view.ForwardView;

/**
 * @author zxc Apr 12, 2013 4:22:54 PM
 */
public class SummerVelocityLayoutViewResolver extends VelocityLayoutViewResolver {

    @SuppressWarnings("rawtypes")
    protected Class requiredViewClass() {
        return SummerVelocityLayoutView.class;
    }

    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        SummerVelocityLayoutView view = (SummerVelocityLayoutView) super.buildView(viewName);
        view.setSuffix(getSuffix());
        return view;
    }

    /**
     * 处理forward情况<br>
     * springMVC的forward时会把model中的数据放入request的Attribute中,<br>
     * velocity页面渲染取值时先取attribute中值,后取model值
     * 
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#createView(java.lang.String, java.util.Locale)
     */
    protected View createView(String viewName, Locale locale) throws Exception {
        // If this resolver is not supposed to handle the given view,
        // return null to pass on to the next resolver in the chain.
        if (!canHandle(viewName, locale)) {
            return null;
        }
        // Check for special "redirect:" prefix.
        if (viewName.startsWith(WebResult.REDIRECT_URL_PREFIX)) {
            String redirectUrl = viewName.substring(WebResult.REDIRECT_URL_PREFIX.length());
            return new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
        }
        // Check for special "forward:" prefix.
        if (viewName.startsWith(WebResult.FORWARD_URL_PREFIX)) {
            String forwardUrl = viewName.substring(WebResult.FORWARD_URL_PREFIX.length());
            return new ForwardView(forwardUrl);
        }
        // Else fall back to superclass implementation: calling loadView.
        return super.createView(viewName, locale);
    }
}
