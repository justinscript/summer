/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.runner.filter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.ms.commons.test.annotation.Group;
import com.ms.commons.test.constants.IntlTestGlobalConstants;
import com.ms.commons.test.runner.RunnerMethodFilter;
import com.ms.commons.test.runner.filter.expression.GroupExpression;
import com.ms.commons.test.runner.filter.expression.SimpleGroupExpression;

/**
 * @author zxc Apr 14, 2013 12:19:40 AM
 */
public class GroupRunnerMethodFilter implements RunnerMethodFilter {

    private static Pattern  GROUP_PATTERN = Pattern.compile("^[a-zA-Z0-9\\.\\*\\+\\-\\|&\\(\\)]+$");

    private GroupExpression filterExpresssion;
    private GroupExpression classFilterExpression;

    public GroupRunnerMethodFilter(String filterExpression, String classGroupExpression) {
        if (filterExpression != null) {
            if (!GROUP_PATTERN.matcher(filterExpression).matches()) {
                throw new RuntimeException("Expression error: " + filterExpression);
            }
            filterExpresssion = new SimpleGroupExpression(replaceSym(filterExpression),
                                                          IntlTestGlobalConstants.TESTCASE_RUN_NO_GROUP);
        }
        if (classGroupExpression != null) {
            if (!GROUP_PATTERN.matcher(classGroupExpression).matches()) {
                throw new RuntimeException("Expression error: " + classGroupExpression);
            }
            classFilterExpression = new SimpleGroupExpression(replaceSym(classGroupExpression),
                                                              IntlTestGlobalConstants.TESTCASE_RUN_CLASS_NO_GROUP);
        }
    }

    public boolean shouldRunMethod(Method method) {
        if (classFilterExpression != null) {
            if (!classFilterExpression.isMatch(Arrays.asList(method.getDeclaringClass().getName()))) {
                return false;
            }
        }
        if (filterExpresssion != null) {
            return filterExpresssion.isMatch(describeGroup(method));
        }
        return true;
    }

    public List<String> describeGroup(Method method) {
        Group methodGroup = method.getAnnotation(Group.class);
        Group classGroup = method.getDeclaringClass().getAnnotation(Group.class);

        String methodGroupValue = replaceSym((methodGroup == null) ? "" : methodGroup.value()).trim();
        String classGroupValue = replaceSym((classGroup == null) ? "" : classGroup.value()).trim();

        String groupValue = (methodGroup == null) ? classGroupValue : methodGroupValue.replaceAll("[\\.]{3,}",
                                                                                                  classGroupValue);

        if (groupValue.trim().length() == 0) {
            return null;
        }

        String[] groups = groupValue.split(",");

        List<String> groupList = new ArrayList<String>();
        for (String g : groups) {
            String trimedG = g.trim();
            if (trimedG.length() > 0) {
                groupList.add(trimedG);
            }
        }

        return groupList;
    }

    private String replaceSym(String s) {
        if (s == null) return null;

        s = s.trim();
        s = s.replace('。', '.');
        s = s.replace('，', ',');
        s = s.replace('（', '(');
        s = s.replace('）', ')');
        s = s.replace('｜', '|');
        s = s.replace('＆', '&');
        s = s.replace("||", "|");
        s = s.replace("&&", "&");

        return s;
    }
}
