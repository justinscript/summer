/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.text;

/**
 * <pre>
 * 当有比较多的字符不能在某个串中出现， 通过一个查表算法确定。比如识别［a,b,c,d］不能在一个string中出现。
 * 由于消耗比较多的内存， 最好使用单一实例。 初始化过程并非线程安全。 最好一次完成初始化的过程。
 * 使用方法：
 *         DetectProhibitChar p2 = new DetectProhibitChar();
 *         p2.addProhibitChar(&quot;我们是中国人＊＊＃W￥％＆＊（￥％＆＊AAAAAAAAAAAAAAAAAAAAAAA&quot;);
 *         for (int i = 0; i &lt; 65536; i++) {
 *             if (p2.isProhibitChar((char) i)) {
 *                 System.out.print((char) i);
 *             }
 *         }
 * </pre>
 * 
 * @author zxc Apr 12, 2013 3:25:13 PM
 */
public class DetectDefinedChar {

    byte[] masks = new byte[1024 * 8];

    public DetectDefinedChar() {

    }

    public DetectDefinedChar(char prohibits[]) {
        addProhibitChar(prohibits);
    }

    /**
     * 增加一个跳越字符
     * 
     * @param c
     */
    public void addProhibitChar(char c) {
        int pos = c >> 3;
        masks[pos] = (byte) ((masks[pos] & 0xFF) | (1 << (c % 8)));
    }

    public void addProhibitChar(int c) {
        addProhibitChar((char) c);
    }

    /**
     * 增加一个string里的所有字符
     * 
     * @param str
     */
    public void addProhibitChar(String str) {
        if (str != null) {
            char cs[] = str.toCharArray();
            for (char c : cs) {
                addProhibitChar(c);
            }
        }
    }

    public void addProhibitChar(char prohibits[]) {
        if (prohibits != null) {
            for (char c : prohibits) {
                addProhibitChar(c);
            }
        }
    }

    public void removeProhibitChar(char c) {
        int pos = c >> 3;
        masks[pos] = (byte) ((masks[pos] & 0xFF) & (~(1 << (c % 8))));
    }

    public boolean isProhibitChar(char c) {
        int pos = c >> 3;
        int i = (masks[pos] & 0xFF) & (1 << (c % 8));
        return (i != 0);
    }

    public boolean hasProhibitChar(char cs[]) {
        if (cs != null) {
            for (char c : cs) {
                if (isProhibitChar(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasProhibitChar(String str) {
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                if (isProhibitChar(str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

}
