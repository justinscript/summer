/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis;

import com.ms.commons.fasttext.extract.ExtractText;

/**
 * @author zxc Apr 12, 2013 3:32:59 PM
 */
public interface MappedExtractText extends ExtractText {

    /**
     * 对给定的字符串按照某种算法进行转换和过滤工作， 并且保留目标字符串和源字符串之间的映射关系。
     * 
     * @param src 源字符串
     * @return 目标字符串
     */
    public MappedCharArray getText(MappedCharArray src);

    /**
     * 对给定的字符串按照某种算法进行转换和过滤工作， 并且保留目标字符串和源字符串之间的映射关系。
     * 
     * @param src 源字符串
     * @param ignoreCase 全部转换为小写
     * @return 目标字符串
     */
    public MappedCharArray getText(MappedCharArray src, boolean ignoreCase);

    /**
     * 对给定的字符串按照某种算法进行转换和过滤工作。
     * 
     * @param src 源字符串
     * @param ignoreCase 全部转换为小写
     * @return 转换后的字符串
     */
    public String getText(String src, boolean ignoreCase);
}
