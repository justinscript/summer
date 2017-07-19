/*
 * Copyright 2011-2016 ZXC.com All riimport com.ms.commons.test.runtime.constant.RuntimeEnvironment; nformation of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runtime;

import com.ms.commons.test.runtime.constant.RuntimeEnvironment;

/**
 * @author zxc Apr 13, 2013 11:43:38 PM
 */
public class Runtime {

    private RuntimeEnvironment environment;
    private String             outputPath;

    public RuntimeEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(RuntimeEnvironment environment) {
        this.environment = environment;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
