/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.statics;

/**
 * @author zxc Apr 12, 2013 10:40:35 PM
 */
public enum StaticResourcesEnum {
    CSS("CSS", "/css", ".css"), APPJS("APPJS", "/js", ".js"), RUNJS("RUNJS", "/js", ".js");

    private String name;
    private String basePath;
    private String suffix;

    private StaticResourcesEnum(String name, String basePath, String suffix) {
        this.name = name;
        this.basePath = basePath;
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isCss() {
        return CSS == this;
    }

    // public boolean isJs() {
    // return JS == this;
    // }
}
