/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runtime.util;

import java.io.File;

import com.ms.commons.test.classloader.util.ClassPathAccessor;
import com.ms.commons.test.runtime.Runtime;
import com.ms.commons.test.runtime.constant.RuntimeEnvironment;

/**
 * @author zxc Apr 13, 2013 11:43:50 PM
 */
public class RuntimeBuilder {

    public static Runtime build() {
        Runtime runtime = new Runtime();
        runtime.setEnvironment(getEnvironment());

        if (runtime.getEnvironment() == RuntimeEnvironment.Eclipse) {
            fillEclipseVariants(runtime);
        }

        return runtime;
    }

    private static RuntimeEnvironment getEnvironment() {
        if (isEclispe()) {
            return RuntimeEnvironment.Eclipse;
        } else if (isAntxTest()) {
            return RuntimeEnvironment.AntxTest;
        } else if (isMavenTest()) {
            return RuntimeEnvironment.Maven;
        }
        return RuntimeEnvironment.Unknow;
    }

    private static void fillEclipseVariants(Runtime runtime) {
        runtime.setOutputPath(ClassPathAccessor.getOutputPath(new File(System.getProperty("user.dir") + "/.classpath")));
    }

    private static boolean isMavenTest() {
        return hasStackTraceStarts("org.apache.maven.surefire.booter.");
    }

    private static boolean isAntxTest() {
        return hasStackTraceStarts("org.apache.tools.ant.taskdefs.optional.junit.");
    }

    private static boolean isEclispe() {
        return hasStackTraceStarts("org.eclipse.jdt.");
    }

    private static boolean hasStackTraceStarts(String packagePath) {
        Exception e = new Exception();
        StackTraceElement[] stes = e.getStackTrace();
        for (StackTraceElement ele : stes) {
            if (ele.getClassName().startsWith(packagePath)) {
                return true;
            }
        }
        return false;
    }
}
