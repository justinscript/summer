/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.security;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 加密解密类 对于web层，一律使用comset/下的EncryptUtils
 * 
 * @author zxc Apr 12, 2013 5:26:39 PM
 */
public class EncryptUtils {

    public static final String NISA_KEY = "S_commons.security.key";

    public static String decrypt(String secretString, String secretKey) {
        if (isEmpty(secretString) || isEmpty(secretKey)) {
            return null;
        }
        try {
            byte[] encryptedData = Base64.decode(secretString);
            Security.addProvider(new com.sun.crypto.provider.SunJCE());
            SecureRandom sr = new SecureRandom();
            byte[] rawKeyData = (new String(secretKey)).getBytes();

            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, sr);

            byte[] decryptedData = cipher.doFinal(encryptedData);

            return new String(decryptedData);
        } catch (Exception e) {
            // String info = RequestInfo.get() + secretString + "\r\n" + ExceptionUtils.getFullStackTrace(e);
            // System.out.println(info);
        }
        return null;
    }

    public static String encrypt(String source, String secretKey) {
        if (isEmpty(source) || isEmpty(secretKey)) {
            return null;
        }
        try {
            byte[] decryptData = source.getBytes();
            Security.addProvider(new com.sun.crypto.provider.SunJCE());
            SecureRandom sr = new SecureRandom();
            byte[] rawKeyData = (new String(secretKey)).getBytes();

            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key, sr);

            byte[] encryptData = cipher.doFinal(decryptData);

            return Base64.encode(encryptData);
        } catch (Exception e) {
            // String info = RequestInfo.get() + source + "\r\n" + ExceptionUtils.getFullStackTrace(e);
            // System.out.println(info);
        }
        return null;
    }

    private static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }
}
