/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.runner.filter.expression;

import java.util.List;
import java.util.regex.Pattern;

import com.ms.commons.test.classloader.IntlTestProperties;
import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.runner.filter.expression.internal.AbstractSimpleExpression;
import com.ms.commons.test.runner.filter.expression.internal.Expression;
import com.ms.commons.test.runner.filter.expression.internal.builder.SimpleExpressionBuiler;
import com.ms.commons.test.runner.filter.expression.internal.exception.ParseException;
import com.ms.commons.test.runner.filter.expression.util.ExpressionParseUtil;

/**
 * @author zxc Apr 14, 2013 12:19:48 AM
 */
public class SimpleGroupExpression implements GroupExpression {

    public static final String NO_GROUP = "NOGROUP";

    private static class Param {

        private List<String> groupList;
        private String       noGroupKey;

        public Param(List<String> groupList, String noGroupKey) {
            this.groupList = groupList;
            this.noGroupKey = noGroupKey;
        }

        public List<String> getGroupList() {
            return groupList;
        }

        public String getNoGroupKey() {
            return noGroupKey;
        }
    }

    public static class SimpleGroupExpressionImpl extends AbstractSimpleExpression {

        private Pattern expressionPattern;

        public SimpleGroupExpressionImpl(String value) {
            super(value);

            String re = value;
            re = re.replace(".", "\\.");
            re = re.replace("*", ".*");
            re = "^" + re + "$";
            expressionPattern = Pattern.compile(re);
        }

        public Object evaluate(Object param) {

            Param p = (Param) param;

            List<String> groupList = p.getGroupList();

            if ((groupList == null) || groupList.isEmpty()) {
                if (NO_GROUP.equals(value)) {
                    return true;
                }

                return IntlTestProperties.isAntxFlagOn(p.getNoGroupKey());
            }

            for (String g : groupList) {
                if (expressionPattern.matcher(g).matches()) {
                    return true;
                }
            }

            return false;
        }
    }

    private String     noGroupKey;
    private Expression expression;

    public SimpleGroupExpression(String expr, String noGroupKey) {
        this.noGroupKey = noGroupKey;

        try {
            expression = ExpressionParseUtil.parse(expr, new SimpleExpressionBuiler() {

                public AbstractSimpleExpression build(String value) {
                    return new SimpleGroupExpressionImpl(value);
                }
            });
        } catch (ParseException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public boolean isMatch(List<String> groupList) {
        return ((Boolean) expression.evaluate(new Param(groupList, noGroupKey))).booleanValue();
    }
}
