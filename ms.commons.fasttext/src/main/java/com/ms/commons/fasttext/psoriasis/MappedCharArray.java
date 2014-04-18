/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis;

/**
 * <pre>
 *  <H3>
 *      字符映射表实现类，利用一个int数组记录字符串在过滤前后的位置，目标字符串在源字符串中的位置可能不连续，<br>
 *      目标字符串的长度记录在charCount中，使用者在处理时必须及时更新。<br>
 *  </H3>
 * </pre>
 * 
 * @author zxc Apr 12, 2013 3:33:22 PM
 */
public class MappedCharArray {

    private char[] src;
    private char[] target;
    private int[]  map;
    private int    charCount;

    public MappedCharArray(char[] src) {
        if (src == null) {
            throw new IllegalArgumentException("Argument can not be null!");
        }
        this.src = src;
        createMap();
    }

    private void createMap() {
        if (map != null) {
            return;
        }
        map = new int[src.length];
        target = new char[src.length];
        System.arraycopy(src, 0, target, 0, src.length);
        charCount = src.length;
        // 考虑到链式反应，对此类操作的目标字符串是target，所以map需要初始化
        for (int i = 0; i < map.length; i++) {
            map[i] = i;
        }
    }

    public static MappedCharArray createInstance(String src) {
        return new MappedCharArray(src.toCharArray());
    }

    public char[] getSrc() {
        return src;
    }

    public char[] getTarget() {
        return target;
    }

    public int[] getMap() {
        return map;
    }

    public int getCharCount() {
        return charCount;
    }

    public void decreaseCharCount(int amount) {
        charCount -= amount;
    }

    public String getMapString() {
        StringBuffer buf2 = new StringBuffer();
        for (int i = 0; i < charCount; i++) {
            buf2.append(src[map[i]]);
        }
        return buf2.toString();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        StringBuffer buf1 = new StringBuffer();

        buffer.append("MappedCharArray{\n");
        buffer.append("    length:     [" + src.length + "]\n");
        buffer.append("    charCount:  [" + charCount + "]\n");
        buffer.append("    src:        [" + new String(src) + "]\n");
        buffer.append("    target:     [" + new String(target, 0, charCount) + "]\n");
        if (charCount > 0) {
            for (int i = 0; i < charCount - 1; i++) {
                buf1.append(map[i] + ",");
            }
            buf1.append(map[charCount - 1]);
            buffer.append("    map:        [" + buf1.toString() + "]\n");
            StringBuffer buf2 = new StringBuffer();
            for (int i = 0; i < charCount; i++) {
                buf2.append(src[map[i]]);
            }
            buffer.append("    mapStr:     [" + buf2.toString() + "]\n");
        }
        buffer.append("}\n");
        return buffer.toString();
    }
}
