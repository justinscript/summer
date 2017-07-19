/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.extract;

/**
 * 只获得CJK字符群
 * 
 * @author zxc Apr 12, 2013 3:39:09 PM
 */
public class CJKExtractText implements ExtractText {

    /**
     * 提取字符串中的所有汉字字符。
     */
    public String getText(String content) {
        if (content == null) {
            return content;
        }
        StringBuilder sb = new StringBuilder(content.length());
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c >= CharNormalization.CJK_UNIFIED_IDEOGRAPHS_START || c < CharNormalization.CJK_UNIFIED_IDEOGRAPHS_END) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
