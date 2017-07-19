/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.exception;

/**
 * YamlException is thrown when an invalid Yaml stream is encountered. Note: YamlException is a RuntimeException is is
 * thrown by the JYaml library as an unchecked exception.
 * 
 * @author zxc Apr 14, 2013 12:33:05 AM
 */
public class YamlException extends RuntimeException {

    private static final long serialVersionUID = 7809524471531846731L;
    int                       lineNumber;

    public YamlException() {
        super();
    }

    public YamlException(String message, Throwable cause) {
        super(message, cause);
    }

    public YamlException(String message) {
        super(message);
    }

    public YamlException(String message, int line) {
        super(message);
        lineNumber = line;
    }

    public YamlException(Throwable cause) {
        super(cause);
    }

    public void setLineNumber(int l) {
        lineNumber = l;
    }

    public String getMessage() {
        return "Error near line " + lineNumber + ": " + super.getMessage();
    }
}
