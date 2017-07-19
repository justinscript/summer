/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.pipeline;

import java.util.Map;

/**
 * @author zxc Apr 12, 2013 4:15:49 PM
 */
public class PipelineValvesFactory {

    private Map<String, Pipeline> tryPipelineValves;
    private Map<String, Pipeline> catchPipelineValves;
    private Map<String, Pipeline> finallyPipelineValves;

    public Map<String, Pipeline> getTryPipelineValves() {
        return tryPipelineValves;
    }

    public void setTryPipelineValves(Map<String, Pipeline> tryPipelineValves) {
        this.tryPipelineValves = tryPipelineValves;
    }

    public Map<String, Pipeline> getCatchPipelineValves() {
        return catchPipelineValves;
    }

    public void setCatchPipelineValves(Map<String, Pipeline> catchPipelineValves) {
        this.catchPipelineValves = catchPipelineValves;
    }

    public Map<String, Pipeline> getFinallyPipelineValves() {
        return finallyPipelineValves;
    }

    public void setFinallyPipelineValves(Map<String, Pipeline> finallyPipelineValves) {
        this.finallyPipelineValves = finallyPipelineValves;
    }
}
