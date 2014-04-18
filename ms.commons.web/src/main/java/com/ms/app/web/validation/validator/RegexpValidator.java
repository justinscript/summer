/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.validation.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * @author zxc Apr 12, 2013 11:18:11 PM
 */
public class RegexpValidator extends AbstarctValidator {

    protected static final String MATCH_MODE_CONTAIN = "contain";
    protected static final String MATCH_MODE_EXACT   = "exact";
    protected static final String MATCH_MODE_PREFIX  = "prefix";
    private String                patternString;
    private Pattern               pattern;

    /**
     * 取得regexp。
     * 
     * @return regexp表达式
     */
    protected String getPattern() {
        return patternString;
    }

    /**
     * 设置regexp。
     * 
     * @param pattern regexp表达式
     */
    public void setPattern(String patternstr) {
        this.patternString = StringUtils.trimToNull(patternstr);
        try {
            pattern = new Perl5Compiler().compile(this.patternString, Perl5Compiler.READ_ONLY_MASK
                                                                      | Perl5Compiler.SINGLELINE_MASK);
        } catch (Exception e) {
            throw new RuntimeException("The Pattern is illegal", e);
        }
    }

    /**
     * 验证一个值。
     * 
     * @param value 要验证的值，该值不可能为空或<code>null</code>
     * @return 如果字段合法，则返回<code>true</code>
     */
    public boolean validate(Object value) {
        PatternMatcher matcher = new Perl5Matcher();
        return matcher.contains(value.toString(), pattern);
    }

    public static void main(String[] args) {
        RegexpValidator validator = new RegexpValidator();
        String patternstr = "^[0-9]+(.[0-9]{0,2})?$";
        validator.setPattern(patternstr);
        System.out.println(validator.isValid(100));
        System.out.println(validator.isValid(100.));
        System.out.println(validator.isValid(200.09));
        System.out.println(validator.isValid(300.333));
        System.out.println(validator.isValid(-399));
    }
}
