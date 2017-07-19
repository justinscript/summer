/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.security.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 4:08:09 PM
 */
public class DefaultSecurityFormResolver {

    private static final ExpandLogger logger              = LoggerFactoryWrapper.getLogger(DefaultSecurityFormResolver.class);

    public static final String        SESSION_TOKEN       = "_session_token";
    public static final String        FORM_RESUBMIT_TOKEN = "_form_resubmit_token";

    /**
     * 生成token，并放入Cookie中
     * 
     * @param request
     * @param str
     */
    public void renderSessionTokenInput(final HttpServletRequest request, final HttpServletResponse response,
                                        final StringBuilder str) {
        String token = createToken();
        Cookie cookie = new Cookie(FORM_RESUBMIT_TOKEN, token);
        cookie.setPath("/");
        response.addCookie(cookie);
        str.append("<input type=\"hidden\" name=\"").append(FORM_RESUBMIT_TOKEN).append("\" value=\"").append(token).append("\"/>");
        if (logger.isDebugEnabled()) {
            logger.debug("render session token value = " + token);
        }
    }

    /**
     * 验证token是否合法，不合法则抛出InvalidTokenException
     * 
     * @param request
     * @param response
     * @throws InvalidTokenException
     */
    public void validSessionToken(final HttpServletRequest request, final HttpServletResponse response)
                                                                                                       throws InvalidTokenException {
        Cookie[] cookies = request.getCookies();
        String ctoken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (FORM_RESUBMIT_TOKEN.equals(cookie.getName())) {
                    ctoken = cookie.getValue();
                    break;
                }
            }
        }
        String rtoken = request.getParameter(FORM_RESUBMIT_TOKEN);
        if (rtoken == null || rtoken.length() == 0) {
            throw new InvalidTokenException("can't find token in request");
        }
        if (ctoken == null || ctoken.length() == 0) {
            throw new InvalidTokenException("can't find token in cookie");
        }
        if (!ctoken.equals(rtoken)) {
            throw new InvalidTokenException("failed to check for token in request");
        }
        // 移除cookie中token信息
        Cookie c = new Cookie(FORM_RESUBMIT_TOKEN, "");
        c.setPath("/");
        response.addCookie(c);
    }

    /**
     * 生产csrf token并放入cookie中
     * 
     * @param request
     * @param response
     * @param tokenCode
     */
    public void renderCSRFTokenInput(HttpServletRequest request, HttpServletResponse response, StringBuilder tokenCode) {
        String token = createToken();
        Cookie cookie = new Cookie(SESSION_TOKEN, token);
        response.addCookie(cookie);
        tokenCode.append("<input type=\"hidden\" name=\"").append(SESSION_TOKEN).append("\" value=\"").append(token).append("\"/>");
        if (logger.isDebugEnabled()) {
            logger.debug("render csrf token value = " + token);
        }
    }

    /**
     * @param request
     * @param response
     * @throws InvalidTokenException
     */
    public void validCSRFToken(final HttpServletRequest request, final HttpServletResponse response)
                                                                                                    throws InvalidTokenException {
        Cookie[] cookies = request.getCookies();
        String ctoken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_TOKEN.equals(cookie.getName())) {
                    ctoken = cookie.getValue();
                    break;
                }
            }
        }
        String rtoken = request.getParameter(SESSION_TOKEN);
        if (rtoken == null || rtoken.length() == 0) {
            throw new InvalidTokenException("can't find csrf token in request");
        }
        if (ctoken == null || ctoken.length() == 0) {
            throw new InvalidTokenException("can't find csrf token in cookie");
        }
        if (!ctoken.equals(rtoken)) {
            throw new InvalidTokenException("failed to check for csrf token in request");
        }
    }

    private String createToken() {
        byte[] bs = Base64.encodeBase64(("" + System.nanoTime()).getBytes());
        return new String(bs);
    }

}
