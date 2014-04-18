/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.extract;

/**
 * @author zxc Apr 12, 2013 3:38:41 PM
 */
public interface ExtractText {

    /**
     * 对给定的字符串按照某种算法进行转换和过滤工作。
     * 
     * @param src 源字符串
     * @return 转换后的字符串
     */
    public String getText(String src);
}
