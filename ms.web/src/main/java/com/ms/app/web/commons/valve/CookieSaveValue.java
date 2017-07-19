/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.valve;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ms.commons.cookie.manager.CookieManager;
import com.ms.commons.cookie.manager.CookieManagerLocator;
import com.ms.commons.summer.web.pipeline.AbstractPipelineValves;
import com.ms.commons.summer.web.pipeline.PipelineMap;
import com.ms.commons.summer.web.pipeline.PipelineResult;

/**
 * 负责将Cookie写到Response中去
 * 
 * @author zxc Apr 12, 2013 11:15:04 PM
 */
public class CookieSaveValue extends AbstractPipelineValves {

    public PipelineResult invoke(HttpServletRequest request, HttpServletResponse response, PipelineMap map)
                                                                                                           throws Exception {

        CookieManager cookieManager = CookieManagerLocator.get(request, response);
        cookieManager.save();
        return null;
    }
}
