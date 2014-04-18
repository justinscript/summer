/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.decorator;

import java.util.List;

import com.ms.commons.fasttext.segment.Darts;
import com.ms.commons.fasttext.segment.WordTerm;

/**
 * 对文本中的部分词汇进行加亮/加Link的算法。
 * 
 * @author zxc Apr 12, 2013 3:43:48 PM
 */
public abstract class DecoratorText {

    protected Darts darts;
    private String  highlightStartTag = DecoratorConstants.DEFAULT_HL_START_TAG;
    private String  highlightEndTag   = DecoratorConstants.DEFAULT_HL_END_TAG;

    public DecoratorText(Darts darts) {
        this.darts = darts;
    }

    public DecoratorText() {
        this.darts = new Darts();
    }

    /**
     * 使用回调方法给指定文本打标记。
     * 
     * @param content 文本
     * @param ignoreCase 是否考虑大小写
     * @return 标记后的文本
     */
    public String highlightText(String content, boolean ignoreCase) {

        return decorator(content, ignoreCase, new DecoratorCallback() {

            public StringBuilder decorator(String src) {
                StringBuilder sb = new StringBuilder(src.length() + 20);
                sb.append(highlightStartTag);
                sb.append(src);
                sb.append(highlightEndTag);
                return sb;
            }
        });
    }

    /**
     * 判断给定文本中是否包含字典中的相关词汇
     * 
     * @param content 给定文本
     * @param ignoreCase 是否考虑大小写
     * @return 包含与否
     */
    public abstract boolean containTerm(String content, boolean ignoreCase);

    /**
     * 返回指定文本中包含的词汇列表。
     * 
     * @param content 指定文本
     * @param ignoreCase 是否考虑大小写
     * @param dupRemove 是否消除重复匹配
     * @return 词汇列表
     */
    public abstract List<String> parseTerms(String content, boolean ignoreCase, boolean dupRemove);

    /**
     * 使用用户指定的tag修饰给定文本。
     * 
     * @param content 指定文本
     * @param ignoreCase 是否考虑大小写
     * @param callback 回调接口，给匹配词汇增加tag标记，不可为空
     * @return 修饰后的字符串
     */
    public abstract String decorator(String content, boolean ignoreCase, DecoratorCallback callback);

    /**
     * 查询给定内容中所有匹配到的高危词，并根据高危词的两个属性开关过滤掉不需要的。<br>
     * 匹配结果将包含重叠的情况，如'ab'和'bc'都是高危词，则对'abc'，这两个词都在匹配结果中。<br>
     * 
     * @param content 需要查询的内容
     * @param ignoreCase 忽略大小写
     * @return 匹配到的词素列表
     */
    public abstract List<WordTerm> parseTerms(String content, boolean ignoreCase);

    public String getHighlightStartTag() {
        return highlightStartTag;
    }

    public void setHighlightStartTag(String highlightStartTag) {
        this.highlightStartTag = highlightStartTag;
    }

    public String getHighlightEndTag() {
        return highlightEndTag;
    }

    public void setHighlightEndTag(String highlightEndTag) {
        this.highlightEndTag = highlightEndTag;
    }

    public Darts getDarts() {
        return darts;
    }

    public void setDarts(Darts darts) {
        this.darts = darts;
    }

}
