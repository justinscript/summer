/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.valve;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ms.commons.summer.web.pipeline.AbstractPipelineValves;
import com.ms.commons.summer.web.pipeline.PipelineMap;
import com.ms.commons.summer.web.pipeline.PipelineResult;

/**
 * 根据用户访问用的是手机还是PC，跳转到手机版或PC版
 * 
 * @author zxc Apr 12, 2013 11:07:31 PM
 */
public class SiteRedirectValve extends AbstractPipelineValves {

    public PipelineResult invoke(HttpServletRequest request, HttpServletResponse response, PipelineMap map)
                                                                                                           throws Exception {

        return super.invoke(request, response, map);
    }
}
