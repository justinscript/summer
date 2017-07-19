/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.pipeline;

/**
 * @author zxc Apr 12, 2013 4:15:35 PM
 */
public class PipelineValvesHandler {

    private String   pipelineName;
    private Pipeline pipeline;

    public PipelineValvesHandler(String pipelineName, Pipeline pipeline) {
        if (pipelineName == null || pipelineName.trim().length() == 0) {
            throw new PipelineResultException("pipeline name can not null");
        }
        if (pipeline == null) {
            throw new PipelineResultException("pipeline can not null");
        }
        this.pipelineName = pipelineName;
        this.pipeline = pipeline;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }
}
