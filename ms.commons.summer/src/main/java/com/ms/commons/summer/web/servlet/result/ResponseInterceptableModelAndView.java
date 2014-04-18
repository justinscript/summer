/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.result;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zxc Apr 12, 2013 4:45:09 PM
 */
public interface ResponseInterceptableModelAndView {

    /**
     * 以自定义的方式处理HttpServletResponse 当用户自己处理了response的输出后，应该返回true；否则返回false
     * 
     * @param response
     */
    public abstract boolean onResponse(HttpServletResponse response);
}
