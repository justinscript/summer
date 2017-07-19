/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.result.mime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.ms.commons.summer.web.servlet.result.ResponseInterceptableModelAndView;

/**
 * @author zxc Apr 12, 2013 4:46:55 PM
 */
public class MimeModelAndView extends ModelAndView implements ResponseInterceptableModelAndView {

    private MimeResult mimeResult;

    /**
     * @param mimeResult
     */
    public MimeModelAndView(MimeResult mimeResult) {
        this.mimeResult = mimeResult;
    }

    @SuppressWarnings("deprecation")
    public boolean onResponse(HttpServletResponse response) {
        byte[] content = this.mimeResult.getContent();
        String characterEncoding = this.mimeResult.getCharacterEncoding();
        if (characterEncoding != null) {
            response.setCharacterEncoding(characterEncoding);
        }
        String contentType = this.mimeResult.getContentType();
        if (contentType != null) {
            response.setContentType(contentType);
        }
        boolean streamIsOpened = this.mimeResult.onResponse(response);
        if (content == null) {// 检查流是否已经打开，如果已经打开，直接返回
            return streamIsOpened;
        } else {
            try {
                response.getOutputStream().write(content);
            } catch (Exception e) {
                throw new RuntimeException("write mimeResult error ", e);
            }
            return true;
        }
    }
}
