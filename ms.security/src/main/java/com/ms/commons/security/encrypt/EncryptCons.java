package com.ms.commons.security.encrypt;

import com.google.gson.annotations.Expose;

public interface EncryptCons {

    /**
     * 如果字段加密过来，则家一个前缀
     */
    @Expose
    public static final String encrypt_value_prefix = "_@mi#_";
    @Expose
    public static final String IV_DELEMITER         = ",";

    /**
     * 加密盐
     */
    @Expose
    public static final String ENCRYPT_SALT         = "0102030405060708";
    /**
     * IV大小.
     */
    @Expose
    public static final byte[] ENCRYPT_IV           = { 0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35,
            0x30, 0x36, 0x30, 0x37, 0x30, 0x38     };
}
