/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis;

import java.io.Serializable;

import com.ms.commons.fasttext.extract.CharNormalization;
import com.ms.commons.fasttext.segment.TermExtraInfo;

/**
 * <h3>类SkipTermExtraInfo.java的实现描述：</h3><br>
 * 保存词相关信息，包括：是否允许跳字匹配，是否需要全词匹配，词权重和匹配词的原词,还有部首匹配中的组合后的字符等信息<br>
 * 原词：只匹配词在词典中的值，因为考虑到跳字匹配，部首匹配，tag过滤等情况，例如“法-轮-功”“氵去车仑工力”等词都可以 匹配“法轮功”，word就是“法轮功”，以有利用分词后的后续处理。<br>
 * 权重：有用户设定，代表这个词的重要程度，用于后续的bayes评估算法，在[0,1]之间，以0.5为中间值， 大于0.5会增大整体权重，小于0.5为减小整体权重。<br>
 * 跳字匹配：指定这个词是否可以跳字匹配，如果允许，“法-轮-功""法，轮，功”都可以匹配“法轮功”，否则不可以。<br>
 * 全词匹配：主要用于英文单词，因为darts是基于trie结构，而英文单词往往基于分隔符，例如lee可以匹配“ lee ”和“sleep”中的 "lee"，但是显然前者才符合英文习惯。<br>
 * 组合字符：用于部首词条，例如“氵去”匹配“法”，那么这个字符就是法，用户后续状态机状态跳转。<br>
 * 
 * @author zxc Apr 12, 2013 3:31:54 PM
 */
public class SkipTermExtraInfo implements TermExtraInfo, Serializable {

    public static final float DEFAULT_WORD_WEIGHT         = 0.5F;
    public static final float DEFAULT_RADICAL_WORD_WEIGHT = 0.666666F;
    private static final long serialVersionUID            = -7700052525549858703L;
    // 匹配词在词典中的值，因为考虑到跳字匹配，部首匹配，tag过滤等情况，例如“法-轮-功”“氵去车仑工力”等词都可以<br> 匹配“法轮功”，word就是“法轮功”，以有利用分词后的后续处理。
    protected String          word;
    // 主要用于英文单词，因为darts是基于trie结构，而英文单词往往基于分隔符，例如lee可以匹配“ lee ”和“sleep”中的"lee"，但是显然前者才符合英文习惯。
    protected boolean         wholeWord;
    // 指定这个词是否可以跳字匹配，如果允许，“法-轮-功""法，轮，功”都可以匹配“法轮功”，否则不可以。
    protected boolean         allowSkip;
    // 权重：有用户设定，代表这个词的重要程度，用于后续的bayes评估算法，在[0,1]之间，以0.5为中间值，大于0.5会增大整体权重，小于0.5为减小整体权重。<br>
    protected float           weight                      = DEFAULT_WORD_WEIGHT;
    // 组合字符：用于部首词条，例如“氵去”匹配“法”，那么这个字符就是法，用户后续状态机状态跳转。
    protected char            compositeChar               = CharNormalization.DEFAULT_BLANK_CHAR;

    // 缺省构造器
    protected SkipTermExtraInfo() {

    }

    /**
     * 构建一个SkipTermExtraInfo对象。
     * 
     * @param word 词典中词
     * @param allowSkip 是否允许跳字匹配，如果允许，“法-轮-功""法，轮，功”都可以匹配“法轮功”，否则不可以。
     * @param wholeWord 主要用于英文单词，因为darts是基于trie结构，而英文单词往往基于分隔符，例如lee可以匹配“ lee ”和“sleep”中的"lee"，但是显然前者才符合英文习惯。
     * @param weight 权重：有用户设定，代表这个词的重要程度，用于后续的bayes评估算法，在[0,1]之间，以0.5为中间值，大于0.5会增大整体权重，小于0.5为减小整体权重。
     */
    public SkipTermExtraInfo(String word, boolean allowSkip, boolean wholeWord, float weight) {
        this(word, allowSkip, wholeWord, weight, CharNormalization.DEFAULT_BLANK_CHAR);
    }

    /**
     * 构建一个部首词表词条项，weight=0.5,allowSkip=false,wholeWord=false。
     * 
     * @param word 词典中的值，如“氵去”
     * @param folkChar 组合后的字符，如“法”
     */
    public SkipTermExtraInfo(String word, char folkChar) {
        this(word, false, false, DEFAULT_RADICAL_WORD_WEIGHT, folkChar);
    }

    /**
     * 构建一个SkipTermExtraInfo对象。
     * 
     * @param word 词典中词
     * @param allowSkip 是否允许跳字匹配，如果允许，“法-轮-功""法，轮，功”都可以匹配“法轮功”，否则不可以。
     * @param wholeWord 主要用于英文单词，因为darts是基于trie结构，而英文单词往往基于分隔符，例如lee可以匹配“ lee ”和“sleep”中的"lee"，但是显然前者才符合英文习惯。
     * @param weight 权重：有用户设定，代表这个词的重要程度，用于后续的bayes评估算法，在[0,1]之间，以0.5为中间值，大于0.5会增大整体权重，小于0.5为减小整体权重。
     * @param folkChar 组合后的字符，如“法”,只有部首词表才需要，其余为default值
     */
    protected SkipTermExtraInfo(String word, boolean allowSkip, boolean wholeWord, float weight, char folkChar) {
        this.word = word;
        this.wholeWord = wholeWord;
        this.allowSkip = allowSkip;
        this.weight = weight;
        this.compositeChar = folkChar;
    }

    /**
     * 构建一个SkipTermExtraInfo对象，weight = 0.5。
     * 
     * @param word 词典中词
     * @param allowSkip 是否允许跳字匹配，如果允许，“法-轮-功""法，轮，功”都可以匹配“法轮功”，否则不可以。
     * @param wholeWord 主要用于英文单词，因为darts是基于trie结构，而英文单词往往基于分隔符，例如lee可以匹配“ lee ”和“sleep”中的"lee"，但是显然前者才符合英文习惯。
     */
    public SkipTermExtraInfo(String word, boolean allowSkip, boolean wholeWord) {
        this(word, allowSkip, wholeWord, 0.5f);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isWholeWord() {
        return wholeWord;
    }

    public void setWholeWord(boolean wholeWord) {
        this.wholeWord = wholeWord;
    }

    public boolean isAllowSkip() {
        return allowSkip;
    }

    public void setAllowSkip(boolean skip) {
        this.allowSkip = skip;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public char getCompositeChar() {
        return compositeChar;
    }

    public void setCompositeChar(char compositeChar) {
        this.compositeChar = compositeChar;
    }

    // matrix2
    public static final int NONE_TYPE      = 0;
    public static final int PINYIN_TYPE    = 1;
    public static final int FORK_TYPE      = 2;
    public static final int HOMOPHONE_TYPE = 3;
    public static final int DEFORM_TYPE    = 4;
    public static final int SKIP_TYPE      = 5;

    protected String        protoWord;
    protected int           transformType  = NONE_TYPE;

    public SkipTermExtraInfo(String word, String protoWord, boolean allowSkip, boolean wholeword, int transformType) {
        this.word = word;
        this.protoWord = protoWord;
        this.allowSkip = allowSkip;
        this.wholeWord = wholeword;
        this.transformType = transformType;
    }

    public String getProtoWord() {
        return protoWord;
    }

    public int getTransformType() {
        return transformType;
    }

    // matrix2

    public String toString() {
        return "[" + word + "," + protoWord + "]";
    }
}
