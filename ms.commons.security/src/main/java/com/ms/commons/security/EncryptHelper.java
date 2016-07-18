package com.ms.commons.security;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.security.encrypt.EncryptCons;

public class EncryptHelper implements EncryptCons {

    /**
     * 去掉前缀
     * 
     * @param value
     * @return
     */
    public static String removeEncryptionPrefix(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        if (StringUtils.startsWith(value, encrypt_value_prefix)) {
            return StringUtils.substring(value, encrypt_value_prefix.length());
        }
        return value;
    }

    public static String addEncryptionPrefix(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        if (!StringUtils.startsWith(value, encrypt_value_prefix)) {
            return encrypt_value_prefix + value;
        }
        return value;
    }
}
