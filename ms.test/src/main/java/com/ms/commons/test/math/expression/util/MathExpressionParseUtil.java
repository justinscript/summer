/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.math.expression.util;

import java.util.Collections;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.test.math.expression.MathExpression;
import com.ms.commons.test.math.expression.builder.MathExpressionBuilder;
import com.ms.commons.test.math.expression.exception.MathParseException;

/**
 * @author zxc Apr 14, 2013 12:24:00 AM
 */
public class MathExpressionParseUtil {

    public static MathExpression parse(String s, MathExpressionBuilder meb) throws MathParseException {
        if (s == null) {
            return null;
        }

        return buildExpression(s, parseTokenStack(s), meb);
    }

    private static MathExpression buildExpression(String expr, Stack<String> expressionStack, MathExpressionBuilder meb)
                                                                                                                        throws MathParseException {
        if ((expressionStack.size() == 0) || (expressionStack.size() == 2)) {
            throw new MathParseException("Error in parse '" + expr + "'.");
        }
        if (expressionStack.size() == 1) {
            String subExpr = expressionStack.pop();
            if (hasSeparators(subExpr)) {
                return buildExpression(expr, parseTokenStack(subExpr), meb);
            } else {
                return meb.buildSimpleMathExpression(subExpr);
            }
        } else {
            expressionStack = adjustExpressionStack(expressionStack);
            String subExpr = expressionStack.pop();
            char op = expressionStack.pop().charAt(0);
            return meb.buildSimpleMathExpressionByOp(op, buildExpression(expr, expressionStack, meb),
                                                     buildExpression(expr, parseTokenStack(subExpr), meb));
        }
    }

    private static Stack<String> adjustExpressionStack(Stack<String> expressionStack) {
        if (expressionStack.size() <= 2) {
            return expressionStack;
        }
        int lastOpPr = Integer.MAX_VALUE;
        int lowerPrOp = -1;
        for (int i = (expressionStack.size() - 1); i >= 0; i--) {
            String expr = expressionStack.get(i);
            if ((expr.length() == 1) && hasSeparators(expr)) {
                int opPr = ("*".equals(expr) || "/".equals(expr)) ? 2 : 1;
                if (opPr < lastOpPr) {
                    lastOpPr = opPr;
                    lowerPrOp = i;
                }
            }
        }
        if (lowerPrOp != -1) {
            Stack<String> tempStack = new Stack<String>();
            int popCount = expressionStack.size() - lowerPrOp - 1;
            for (int i = 0; i < popCount; i++) {
                tempStack.push(expressionStack.pop());
            }
            Collections.reverse(tempStack);
            expressionStack.push(StringUtils.join(tempStack, ""));
        }

        return expressionStack;
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
                case '-':
                case '*':
                case '/':
                    return true;
            }
        }
        return false;
    }

    private static Stack<String> parseTokenStack(String expr) throws MathParseException {
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
                        throw new MathParseException("Error token '(' or ')' useage in '" + expr + "'.");
                    }
                    int nextCloseBracket = findNextCloseBracket(cs, (i + 1), endIndex);
                    if (nextCloseBracket == -1) {
                        throw new MathParseException("Token '(' or ')' not paried in '" + expr + "'.");
                    }
                    for (int x = i + 1; x < nextCloseBracket; x++) {
                        lastTokens.append(cs[x]);
                    }
                    i = nextCloseBracket;
                    isLastBracketsBlock = true;
                    break;
                case '+':
                case '-':
                case '*':
                case '/':
                    checkIndex(i, fromIndex, endIndex, cs, lastTokens);
                    expressionStack.push(lastTokens.toString());
                    expressionStack.push(String.valueOf(c));
                    lastTokens = new StringBuilder();
                    isLastBracketsBlock = false;
                    break;
                default:
                    if (c == ')') {
                        throw new MathParseException("Error token ')' in '" + expr + "'.");
                    }
                    if (isLastBracketsBlock) {
                        throw new MathParseException("Error token '(' or ')' useage in '" + expr + "'.");
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
                                                                                                           throws MathParseException {
        if ((fromIndex == i) || (i == endIndex) || (lastTokens.length() == 0)) {
            StringBuilder sb = new StringBuilder(cs.length + 2);
            for (int csIndex = 0; csIndex < cs.length; csIndex++) {
                if (csIndex == i) {
                    sb.append('[').append(cs[csIndex]).append(']');
                } else {
                    sb.append(cs[csIndex]);
                }
            }
            throw new MathParseException("Error grammer for '" + cs[i] + "' of '" + sb.toString() + "'.");
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
