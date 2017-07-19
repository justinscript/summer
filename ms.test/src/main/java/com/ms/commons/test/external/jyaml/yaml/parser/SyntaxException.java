/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.yaml.parser;

/**
 * @author zxc Apr 14, 2013 12:39:02 AM
 */
public class SyntaxException extends Exception {

    private static final long serialVersionUID = -9140617584427094168L;
    public int                line;

    public SyntaxException() {
        super();
    }

    public SyntaxException(String s) {
        super(s);
    }

    public SyntaxException(String s, int line) {
        super(s);
        this.line = line;
    }
}
