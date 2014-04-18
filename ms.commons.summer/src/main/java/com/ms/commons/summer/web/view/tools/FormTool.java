package com.ms.commons.summer.web.view.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ms.commons.summer.security.web.DefaultSecurityFormResolver;

/**
 * @author zxc Apr 12, 2013 4:23:25 PM
 */
public class FormTool {

    private HttpServletRequest                 request;
    private HttpServletResponse                response;
    private static DefaultSecurityFormResolver securityFormResolver = new DefaultSecurityFormResolver();

    public FormTool(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public StringBuilder renderToken() {
        StringBuilder tokenCode = new StringBuilder(1024);
        securityFormResolver.renderSessionTokenInput(request, response, tokenCode);
        return tokenCode;
    }

    public StringBuilder renderCSRF() {
        StringBuilder tokenCode = new StringBuilder(1024);
        securityFormResolver.renderCSRFTokenInput(request, response, tokenCode);
        return tokenCode;
    }
}
