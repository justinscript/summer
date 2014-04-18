/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.integration.junit4.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.ms.commons.test.annotation.Prepare;
import com.ms.commons.test.annotation.TestCaseInfo;
import com.ms.commons.test.cache.BuiltInCacheKey;
import com.ms.commons.test.cache.ThreadContextCache;
import com.ms.commons.test.classloader.IntlTestProperties;
import com.ms.commons.test.common.OutputUtil;
import com.ms.commons.test.common.tool.OutputWriter;
import com.ms.commons.test.constants.IntlTestGlobalConstants;
import com.ms.commons.test.context.TestCaseRuntimeInfo;
import com.ms.commons.test.runner.RunnerMethodFilter;
import com.ms.commons.test.runner.filter.GroupRunnerMethodFilter;

/**
 * @author zxc Apr 13, 2013 11:45:45 PM
 */
public class IntlTestBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    private static RunnerMethodFilter runnerMethodFilter = createRunnerMethodFilter();

    private Class<?>                  clazz;

    public IntlTestBlockJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
        System.out.println("********************** IntlTestBlockJUnit4ClassRunner");
    }

    protected Statement classBlock(final RunNotifier notifier) {
        final Statement statement = super.classBlock(notifier);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                statement.evaluate();

                TestCaseInfo testCaseInfo = IntlTestBlockJUnit4ClassRunner.this.clazz.getAnnotation(TestCaseInfo.class);
                if ((testCaseInfo != null) && (testCaseInfo.dumpMessage())) {
                    OutputUtil.stdErr();
                }
                // flush to file
                OutputWriter.flushAllWriters();
                System.out.println("Test case run finished.");
            }
        };
    }

    protected Statement methodBlock(final FrameworkMethod fmethod) {
        final Method method = fmethod.getMethod();
        final Statement statement = super.methodBlock(fmethod);
        final TestCaseRuntimeInfo currRunetimeInfo = TestCaseRuntimeInfo.current();
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                // set test case runtime info context
                currRunetimeInfo.setClazz(method.getDeclaringClass());
                currRunetimeInfo.setTestCaseInfo(method.getDeclaringClass().getAnnotation(TestCaseInfo.class));
                currRunetimeInfo.setMethod(method);
                currRunetimeInfo.setPrepare(method.getAnnotation(Prepare.class));
                currRunetimeInfo.getContext().clear();

                ThreadContextCache.put(BuiltInCacheKey.Method, method);
                statement.evaluate();
            }
        };
    }

    protected Statement methodInvoker(FrameworkMethod fmethod, Object test) {
        final Method method = fmethod.getMethod();
        final Statement statement = super.methodInvoker(fmethod, test);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                OutputUtil.enter(method);
                try {
                    statement.evaluate();
                } finally {
                    OutputUtil.exit();
                }
            }
        };
    }

    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);
    }

    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> testMethodList = super.computeTestMethods();

        if (runnerMethodFilter == null) {
            return testMethodList;
        }
        List<FrameworkMethod> filtedTestMethodList = new ArrayList<FrameworkMethod>();
        for (FrameworkMethod frameworkMethod : testMethodList) {
            if (runnerMethodFilter.shouldRunMethod(frameworkMethod.getMethod())) {
                filtedTestMethodList.add(frameworkMethod);
            }
        }

        return filtedTestMethodList;
    }

    private static RunnerMethodFilter createRunnerMethodFilter() {
        String groupExression = IntlTestProperties.getAntxProperty(IntlTestGlobalConstants.TESTCASE_RUN_GROUPING);
        String classGroupExpression = IntlTestProperties.getAntxProperty(IntlTestGlobalConstants.TESTCASE_RUN_CLASS_GROUPING);
        if (groupExression == null && classGroupExpression == null) {
            return null;
        }
        System.err.println("FILTER ON: {[" + groupExression + "] / [" + classGroupExpression + "]}");
        return new GroupRunnerMethodFilter(groupExression, classGroupExpression);
    }
}
