/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author zxc Apr 12, 2013 5:27:30 PM
 */
public class SecurityUtils {

    /**
     * MD5 Hash
     */
    public static String createMD5(String passwordSource) {
        String password;
        byte[] passwordByte;

        password = passwordSource;

        passwordByte = DigestUtils.md5(password);

        String ReturnPassword = WebmacroBase64.encode(passwordByte);

        return Base64.encode(ReturnPassword.getBytes());
    }

    private final static class WebmacroBase64 {

        private static final byte UPPER_FOUR = (byte) (16 + 32 + 64 + 128);
        private static final byte LOWER_FOUR = (byte) (1 + 2 + 4 + 8);
        private static final byte UPPER_SIX  = (byte) (4 + 8 + 16 + 32 + 64 + 128);
        private static final byte LOWER_TWO  = (byte) (1 + 2);
        private static final byte UPPER_TWO  = (byte) (64 + 128);
        private static final byte LOWER_SIX  = (byte) (1 + 2 + 4 + 8 + 16 + 32);

        /**
         * Get the plain text version of a base64 encoded string
         */
        @SuppressWarnings("unused")
        final static String decode(String encoded) {
            return decode(encoded.getBytes());
        }

        /**
         * Get the base64 encoded version of a plain text String
         */
        @SuppressWarnings("unused")
        final static String encode(String plainText) {
            return encode(plainText.getBytes());
        }

        /**
         * Ge tthe plain text version of a base64 encoded byte array
         */
        final static String decode(byte[] encoded) {
            byte[] plain = new byte[(int) (encoded.length * 0.75) + 2];

            byte code;
            byte ptext;
            int ppos = 0;
            int epos = 0;
            boolean cutShort = false;

            while (epos < encoded.length) {
                /*
                 * base64 decoding: turn 4*6 bits into 3*8 bits via the pattern: xx111111 xx112222 xx222233 xx333333
                 * (xx: high bits unused)
                 */
                code = sixBits(encoded[epos]);

                // 1st byte: lower six bits from 1st char
                ptext = (byte) (code << 2);

                // 1st byte: plus upper two (of six) bits from 2nd char
                epos++;

                try {
                    code = sixBits(encoded[epos]);
                } catch (Exception e) {
                    code = 0; // too few chars, assume missing pad
                    cutShort = true;
                }

                ptext |= (code & UPPER_FOUR) >>> 4;
                plain[ppos++] = ptext;

                if (cutShort) {
                    break; // loop
                }

                // 2nd byte: lower four bytes from 2nd char
                ptext = (byte) ((code & LOWER_FOUR) << 4);

                // 2nd byte: plus upper four (of six) bits from 3rd char
                epos++;

                try {
                    code = sixBits(encoded[epos]);
                } catch (Exception e) {
                    code = 0; // too few chars, assume missing pad
                }

                ptext |= (code & UPPER_SIX) >>> 2;
                plain[ppos++] = ptext;

                if (cutShort) {
                    break; // loop
                }

                // 3rd byte: lower two bits of 3rd char
                ptext = (byte) ((code & LOWER_TWO) << 6);

                // 3rd byte: lower six bits of fourth char
                epos++;

                try {
                    code = sixBits(encoded[epos]);
                } catch (Exception e) {
                    code = 0; // too few chars, assume missing pad
                }

                ptext |= code;
                plain[ppos++] = ptext;

                // advance loop
                epos++;
            }

            return new String(plain);
        }

        /**
         * Get the base64 encoded version of a plain text byte array
         */
        final static public String encode(byte[] plain) {
            /*
             * base64 encoding: turn 3*8 bits into 4*6 bits via the pattern: 111111 112222 222233 333333 (xx: high bits
             * unused) UPPER6 LOWER2 LOWER4 LOWER6 UPPER4 UPPER2
             */
            StringBuffer encoded = new StringBuffer((int) (plain.length * 1.34) + 1);

            byte ptext;
            byte sixbits;
            int ppos = 0;
            boolean cutShort = false;

            while (ppos < plain.length) {
                // first char: upper 6 bytes
                ptext = plain[ppos];
                sixbits = (byte) ((ptext & UPPER_SIX) >>> 2);
                encoded.append(base64(sixbits));

                // second char: lower 2, upper 4
                sixbits = (byte) ((ptext & LOWER_TWO) << 4);

                ppos++;

                try {
                    ptext = plain[ppos];
                } catch (ArrayIndexOutOfBoundsException e) {
                    ptext = 0;
                    cutShort = true;
                }

                sixbits |= (byte) ((ptext & UPPER_FOUR) >>> 4);
                encoded.append(base64(sixbits));

                if (cutShort) {
                    encoded.append("==");

                    return encoded.toString();
                }

                // third char: lower four, upper 2
                sixbits = (byte) ((ptext & LOWER_FOUR) << 2);
                ppos++;

                try {
                    ptext = plain[ppos];
                } catch (ArrayIndexOutOfBoundsException e) {
                    ptext = 0;
                    cutShort = true;
                }

                sixbits |= (byte) ((ptext & UPPER_TWO) >>> 6);
                encoded.append(base64(sixbits));

                if (cutShort) {
                    encoded.append("=");

                    return encoded.toString();
                }

                // fourth char: lower six
                sixbits = (byte) (ptext & LOWER_SIX);
                encoded.append(base64(sixbits));

                // increment loop
                ppos++;
            }

            return encoded.toString();
        }

        /**
         * Translate a character in the base64 alphabet into a byte with the corresponding bits set (ie: a number from 0
         * to 64).
         * 
         * @return the base64 value, or 0 for the special '=' pad character
         * @param base64 the character to be translated
         * @exception NumberFormatException if base64 is not a base64 character
         */
        private static byte sixBits(byte base64) throws NumberFormatException {
            if ((base64 >= 'A') && (base64 <= 'Z')) {
                return (byte) (base64 - 'A');
            }

            if ((base64 >= 'a') && (base64 <= 'z')) {
                return (byte) (base64 - 'a' + 26);
            }

            if ((base64 >= '0') && (base64 <= '9')) {
                return (byte) (base64 - '0' + 52);
            }

            if (base64 == '+') {
                return 62;
            }

            if (base64 == '/') {
                return 63;
            }

            if (base64 == '=') {
                return 0; // pad means zeroed bits FIXME: i am not sure, really?
            }

            throw new NumberFormatException("Not a base64 character: " + base64);
        }

        /**
         * Turn a six-bit value into a base64 digit
         */
        static private char base64(byte sixBits) {
            if (sixBits <= 25) { // less/equal base64 'Z'

                return (char) ('A' + sixBits);
            }

            if (sixBits <= 51) { // less/equal base64 'z'

                return (char) (('a' + sixBits) - 26); // count from base64 'a'
            }

            if (sixBits <= 61) { // less/equal base64 '9'

                return (char) (('0' + sixBits) - 52); // count from base64 '0'
            }

            if (sixBits == 62) {
                return '+';
            }

            if (sixBits == 63) {
                return '/';
            }

            throw new NumberFormatException("Not a base64 digit: " + sixBits);
        }
    }
}
