/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.pipeline;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

/**
 * @author zxc Apr 12, 2013 4:16:59 PM
 */
public abstract class AbstractPipelineValves implements Pipeline {

    public void init(ApplicationContext context) {

    }

    public PipelineResult invoke(HttpServletRequest request, HttpServletResponse response, PipelineMap map)
                                                                                                           throws Exception {
        return null;
    }
}
