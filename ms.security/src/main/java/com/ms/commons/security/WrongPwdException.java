package com.ms.commons.security;

/**
 * 错误密码
 */
public class WrongPwdException extends RuntimeException {

    private static final long serialVersionUID = -3433011252679430587L;

    public WrongPwdException() {
        super("密码不正确");
    }
}
