/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:17:39 PM
 */
public class StringUtil {

    public static String join(String[] strArray, String separater) {
        StringBuilder sb = new StringBuilder();
        if (strArray != null) {
            for (int i = 0; i < strArray.length; i++) {
                if (i != 0) {
                    sb.append(separater);
                }
                sb.append(strArray[i]);
            }
        }
        return sb.toString();
    }

    public static boolean trimedIgnoreCaseEquals(String a, String b) {
        return trimToEmpty(a).toLowerCase().equals(trimToEmpty(b).toLowerCase());
    }

    public static String trimToEmpty(String s) {
        return (s == null) ? "" : s.trim();
    }

    public static String[] splitAndTrimByComma(String s) {
        if (s == null) {
            return null;
        }
        String trimedS = trimReplaceFullSpaceToHalf(s).trim();
        if (trimedS.length() == 0) {
            return null;
        }
        String[] sarray = trimedS.split(",");
        List<String> slist = new ArrayList<String>();
        for (String sa : sarray) {
            String trimedSA = trimToEmpty(sa);
            if (trimedSA.length() > 0) {
                slist.add(trimedSA);
            }
        }
        if (slist.size() == 0) {
            return null;
        }
        return slist.toArray(new String[0]);
    }

    public static String digetsToString(byte[] bytes, String algorithm) {
        return convertBytesToHex(digest(bytes, algorithm));
    }

    public static byte[] digest(byte[] bytes, String algorithm) {
        MessageDigest alg;
        try {
            alg = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
        alg.update(bytes);
        return alg.digest();
    }

    public static String convertBytesToHex(byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder();

        for (int n = 0; n < bytes.length; n++) {
            String stmp = Integer.toHexString(bytes[n] & 0XFF);
            if (stmp.length() == 1) {
                hexBuilder.append("0");
            }
            hexBuilder.append(stmp);
        }
        return hexBuilder.toString().toUpperCase();
    }

    public static String replaceNoWordChars(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[^a-zA-Z0-9]+", "_");
    }

    public static String trimReplaceFullSpaceToHalf(String s) {
        if (s == null) return null;

        s = s.trim();
        s = s.replace('　', ' ');
        s = s.replace('／', '/');
        s = s.replace('。', '.');
        s = s.replace('，', ',');
        s = s.replace('（', '(');
        s = s.replace('）', ')');
        s = s.replace('｜', '|');
        s = s.replace('＆', '&');

        return s;
    }
}
