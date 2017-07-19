/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.pipeline;

/**
 * @author zxc Apr 12, 2013 4:16:24 PM
 */
public class PipelineResult {

    private PipelineType type;
    private String       name;
    private String       redirectUrl;

    private PipelineResult(PipelineType type) {
        this(type, null, null);
    }

    private PipelineResult(PipelineType type, String name, String redirectUrl) {
        this.type = type;
        this.name = name;
        this.redirectUrl = redirectUrl;
    }

    public static PipelineResult gotoTry(String name) {
        return new PipelineResult(PipelineType.PIPELINE_TRY, name, null);
    }

    public static PipelineResult gotoCatch(String name) {
        return new PipelineResult(PipelineType.PIPELINE_CATCH, name, null);
    }

    // public static PipelineResult gotoFinally() {
    // return new PipelineResult(PipelineType.PIPELINE_FINALLY, null);
    // }

    public static PipelineResult gotoFinally(String name) {
        return gotoFinally(name, null);
    }

    public static PipelineResult gotoFinally(String name, String redirectUrl) {
        return new PipelineResult(PipelineType.PIPELINE_FINALLY, name, redirectUrl);
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public PipelineType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
