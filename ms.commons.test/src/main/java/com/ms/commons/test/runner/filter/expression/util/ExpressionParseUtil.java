/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runner.filter.expression.util;

import java.util.Stack;

import com.ms.commons.test.runner.filter.expression.internal.AbstractLRExpression;
import com.ms.commons.test.runner.filter.expression.internal.Expression;
import com.ms.commons.test.runner.filter.expression.internal.builder.SimpleExpressionBuiler;
import com.ms.commons.test.runner.filter.expression.internal.exception.ParseException;
import com.ms.commons.test.runner.filter.expression.internal.impl.AndExpression;
import com.ms.commons.test.runner.filter.expression.internal.impl.MinusExpression;
import com.ms.commons.test.runner.filter.expression.internal.impl.OrExpression;

/**
 * @author zxc Apr 14, 2013 12:20:03 AM
 */
public class ExpressionParseUtil {

    public static Expression parse(String s, SimpleExpressionBuiler seb) throws ParseException {
        if (s == null) {
            return null;
        }

        return buildExpression(s, parseTokenStack(s), seb);
    }

    private static Expression buildExpression(String expr, Stack<String> expressionStack, SimpleExpressionBuiler seb)
                                                                                                                     throws ParseException {
        if ((expressionStack.size() == 0) || (expressionStack.size() == 2)) {
            throw new ParseException("Error in parse '" + expr + "'.");
        }
        if (expressionStack.size() == 1) {
            String subExpr = expressionStack.pop();
            if (hasSeparators(subExpr)) {
                return buildExpression(expr, parseTokenStack(subExpr), seb);
            } else {
                return seb.build(subExpr);
            }
        } else {
            String subExpr = expressionStack.pop();
            char op = expressionStack.pop().charAt(0);
            return createLrExpressionByOp(expr, op, buildExpression(expr, expressionStack, seb),
                                          buildExpression(expr, parseTokenStack(subExpr), seb));
        }
    }

    private static boolean hasSeparators(String expr) {
        char[] cs = expr.toCharArray();
        int fromIndex = 0;
        int endIndex = expr.length() - 1;
        for (int i = fromIndex; i <= endIndex; i++) {
            char c = cs[i];
            switch (c) {
                case '(':
                case ')':
                case '+':
                case '&':
                case '|':
                case '-':
                    return true;
            }
        }
        return false;
    }

    private static AbstractLRExpression createLrExpressionByOp(String expr, char op, Expression left, Expression right)
                                                                                                                       throws ParseException {
        switch (op) {
            case '+':
            case '&':
                return new AndExpression(left, right);
            case '|':
                return new OrExpression(left, right);
            case '-':
                return new MinusExpression(left, right);
            default:
                throw new ParseException("Contains ilgeal OP in '" + expr + "'.");
        }
    }

    private static Stack<String> parseTokenStack(String expr) throws ParseException {
        Stack<String> expressionStack = new Stack<String>();
        StringBuilder lastTokens = new StringBuilder();
        char[] cs = expr.toCharArray();
        boolean isLastBracketsBlock = false;
        int fromIndex = 0;
        int endIndex = expr.length() - 1;
        for (int i = fromIndex; i <= endIndex; i++) {
            char c = cs[i];
            switch (c) {
                case '(':
                    if (isLastBracketsBlock) {
                        throw new ParseException("Error token '(' or ')' useage in '" + expr + "'.");
                    }
                    int nextCloseBracket = findNextCloseBracket(cs, (i + 1), endIndex);
                    if (nextCloseBracket == -1) {
                        throw new ParseException("Token '(' or ')' not paried in '" + expr + "'.");
                    }
                    for (int x = i + 1; x < nextCloseBracket; x++) {
                        lastTokens.append(cs[x]);
                    }
                    i = nextCloseBracket;
                    isLastBracketsBlock = true;
                    break;
                case '+':
                case '&':
                case '|':
                case '-':
                    checkIndex(i, fromIndex, endIndex, cs, lastTokens);
                    expressionStack.push(lastTokens.toString());
                    expressionStack.push(String.valueOf(c));
                    lastTokens = new StringBuilder();
                    isLastBracketsBlock = false;
                    break;
                default:
                    if (c == ')') {
                        throw new ParseException("Error token ')' in '" + expr + "'.");
                    }
                    if (isLastBracketsBlock) {
                        throw new ParseException("Error token '(' or ')' useage in '" + expr + "'.");
                    }
                    // check more ilgeal char ?
                    lastTokens.append(c);
                    break;
            }
        }
        if (lastTokens.length() > 0) {
            expressionStack.add(lastTokens.toString());
        }
        return expressionStack;
    }

    private static void checkIndex(int i, int fromIndex, int endIndex, char[] cs, StringBuilder lastTokens)
                                                                                                           throws ParseException {
        if ((fromIndex == i) || (i == endIndex) || (lastTokens.length() == 0)) {
            StringBuilder sb = new StringBuilder(cs.length + 2);
            for (int csIndex = 0; csIndex < cs.length; csIndex++) {
                if (csIndex == i) {
                    sb.append('[').append(cs[csIndex]).append(']');
                } else {
                    sb.append(cs[csIndex]);
                }
            }
            throw new ParseException("Error grammer for '" + cs[i] + "' of '" + sb.toString() + "'.");
        }

    }

    private static int findNextCloseBracket(char[] cs, int fromIndex, int endIndex) {
        int openBracketDepth = 0;
        for (int i = fromIndex; i <= endIndex; i++) {
            char c = cs[i];
            switch (c) {
                case '(':
                    openBracketDepth++;
                    break;
                case ')':
                    if (openBracketDepth == 0) {
                        return i;
                    }
                    openBracketDepth--;
                    break;
            }
        }
        return -1;
    }
}
