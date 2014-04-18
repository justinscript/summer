/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.codec;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author zxc Apr 12, 2013 3:17:31 PM
 */
public class JavaScriptEncode {

    static boolean COMMON_ASCII[] = new boolean[127];
    static {
        // 0-9
        for (char i = '0'; i <= '9'; i++) {
            COMMON_ASCII[i] = true;
        }
        // a-z
        for (char i = 'a'; i <= 'z'; i++) {
            COMMON_ASCII[i] = true;
        }
        // A-Z
        for (char i = 'A'; i <= 'Z'; i++) {
            COMMON_ASCII[i] = true;
        }
        COMMON_ASCII[32] = true;
        COMMON_ASCII[44] = true;
        COMMON_ASCII[46] = true;

    }

    public static String escapedJavaScript(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        StringBuilder buffer = new StringBuilder(string.length() << 1);
        String hex;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c < 127 && COMMON_ASCII[c]) {
                buffer.append(c);
            } else if (c <= 127) {
                hex = Integer.toHexString(c).toUpperCase();
                if (hex.length() < 2) {
                    buffer.append("\\x0");
                } else {
                    buffer.append("\\x");
                }
                buffer.append(hex);
            } else {
                // c > 0x7F // len >= 2
                hex = Integer.toHexString(c).toUpperCase();
                if (c < 0x100) {// len == 2
                    buffer.append("\\u00");
                } else if (c < 0x1000) {// len == 3
                    buffer.append("\\u0");
                } else {// len == 4
                    buffer.append("\\u");
                }
                buffer.append(hex);
            }
        }
        return buffer.toString();
    }

    public static String escapedJavaScriptValue(String str) {

        if (str == null) {
            return null;
        }
        int length = str.length();
        Writer out = new StringWriter(length << 1);
        try {
            for (int i = 0; i < length; i++) {
                char ch = str.charAt(i);

                if (ch < 32) {
                    switch (ch) {
                        case '\b':

                            out.write('\\');

                            out.write('b');
                            break;

                        case '\n':
                            out.write('\\');
                            out.write('n');
                            break;

                        case '\t':
                            out.write('\\');
                            out.write('t');
                            break;

                        case '\f':
                            out.write('\\');
                            out.write('f');
                            break;

                        case '\r':
                            out.write('\\');
                            out.write('r');
                            break;

                        default:
                            if (ch > 0xf) {
                                out.write("\\u00" + Integer.toHexString(ch).toUpperCase());
                            } else {
                                out.write("\\u000" + Integer.toHexString(ch).toUpperCase());
                            }

                            break;
                    }

                } else {
                    switch (ch) {
                        case '/':
                            out.write('\\');
                            out.write('/');
                            break;
                        case '\'':
                            out.write('\\');
                            out.write('\'');
                            break;
                        case '"':
                            out.write('\\');
                            out.write('"');
                            break;
                        case '\\':
                            out.write('\\');
                            out.write('\\');
                            break;
                        default:
                            out.write(ch);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            // impossible
        }
        return out.toString();
    }
}
