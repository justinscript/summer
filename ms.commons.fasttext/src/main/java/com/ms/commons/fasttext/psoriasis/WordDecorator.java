/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis;

import com.ms.commons.fasttext.segment.WordTerm;

/**
 * @author zxc Apr 12, 2013 3:31:36 PM
 */
public interface WordDecorator {

    /**
     * 是否对这个词进行特殊高亮。
     * 
     * @param word 原始文本
     * @param wordTerm 分词
     * @return 是否采用这个词高亮器对词进行特殊高亮，如果为false，decorateWord方法将不会被调用。
     */
    public boolean match(String word, WordTerm wordTerm);

    /**
     * 对词语进行特殊高亮
     * 
     * @param word 原始文本
     * @param wordTerm 分词
     * @return 高亮后的文本,可以增加自定义tag
     */
    public String decorateWord(String word, WordTerm wordTerm);
}
