/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.segment;

/**
 * 分词以后的结果， 有2个含义，如果position == -1, 意味无法分的词
 * 
 * @author zxc Apr 12, 2013 3:46:04 PM
 */
public class WordTerm {

    /**
     * 在字典中的位置
     */
    public int           position   = -1;
    /**
     * 在输入文本中的位置
     */
    public int           begin;
    /**
     * 该词的长度
     */
    public int           length;

    /**
     * 该词在输入文本出现的频率
     */
    public int           frequency  = 0;
    /**
     * 该词在字典中出现的频率
     */
    public TermExtraInfo termExtraInfo;

    /**
     * 特征值
     */
    public double        eigenvalue = 0;

    public String toString() {
        return "[position=" + position + ",begin=" + begin + ",length=" + length + ",termExtraInfo=" + termExtraInfo
               + "]";
    }

    public String toString(char[] src) {
        return "[content=" + String.copyValueOf(src, begin, length) + ",position=" + position + ",begin=" + begin
               + ",length=" + length + ",termExtraInfo=" + termExtraInfo + "]";
    }

}
