/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.psoriasis.estimator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ms.commons.fasttext.psoriasis.SkipTermExtraInfo;
import com.ms.commons.fasttext.segment.TermExtraInfo;
import com.ms.commons.fasttext.segment.WordTerm;

/**
 * 类DefaultWeightAdjuster.java的实现描述：<br>
 * 文档权重调整器，允许业务对权重进行调整，目前可以配置规则如下：<br>
 * 1、词语伪装系数(针对词语），如“法-轮-功”，<a>法</a>律，“氵去车仑工力”等<br>
 * 2、特殊词语权重系数(针对词语），例如奥运前特殊调整“兴奋剂”权重。<br>
 * 3、词语集合权重系数(针对文档），例如规定“法轮功”“李洪志”同时出现权重调整100%。<br>
 * 
 * @author zxc Apr 12, 2013 3:30:58 PM
 */
public class DefaultWeightAdjuster implements WeightAdjuster {

    private double                   maskWeightFactor = 1;
    // 词调整比例
    private Map<String, Double>      wordFactorMap;
    // 基于规则的
    private Map<Set<String>, Double> wordSetFactorMap;

    public void beginAdjust(Set<String> wordList) {
    }

    public void endAdjust(Set<String> wordList) {
    }

    public double adjustDocument(Set<String> wordSet, double weight) {
        if (wordSetFactorMap == null) {
            return weight;
        }
        // 判断是否包含需要调整的单词集合
        for (Set<String> words : wordSetFactorMap.keySet()) {
            if (wordSet.containsAll(words)) {
                Double factor = wordSetFactorMap.get(words);
                if (factor != null) {
                    weight *= factor;
                }
            }
        }
        // 保证weight在[0,1]之间
        weight = weight >= 0 ? (weight <= 1 ? weight : 1) : 0;
        return weight;
    }

    public double adjustWord(WordTerm wordTerm, double weight) {
        if (wordTerm == null) {
            return weight;
        }
        TermExtraInfo info = wordTerm.termExtraInfo;
        if (info == null || !(info instanceof SkipTermExtraInfo)) {
            return weight;
        }
        SkipTermExtraInfo skipTermExtraInfo = (SkipTermExtraInfo) info;
        String word = skipTermExtraInfo.getWord();
        if (isMaskWord(wordTerm)) {
            weight *= maskWeightFactor;
        }
        // 调整单个词的权重
        if (wordFactorMap != null) {
            Double factor = wordFactorMap.get(word);
            if (factor != null) {
                weight *= factor;
            }
        }
        // 保证weight在[0,1]之间
        weight = weight >= 0 ? (weight <= 1 ? weight : 1) : 0;
        return weight;
    }

    public double getMaskWeightFactor() {
        return maskWeightFactor;
    }

    public void setMaskWeightFactor(double skipWeightFactor) {
        this.maskWeightFactor = skipWeightFactor;
    }

    public synchronized void setWordFactor(String word, double factor) {
        if (wordFactorMap == null) {
            wordFactorMap = new HashMap<String, Double>();
        }
        if (word == null || factor < 0) {
            return;
        }
        wordFactorMap.put(word, Double.valueOf(factor));
    }

    public synchronized void setWordSetFactor(Set<String> wordSet, double factor) {
        if (wordSetFactorMap == null) {
            wordSetFactorMap = new HashMap<Set<String>, Double>();
        }
        if (wordSet == null || factor < 0) {
            return;
        }
        wordSetFactorMap.put(wordSet, Double.valueOf(factor));
    }

    /**
     * 判断单词是否经过伪装，目前是简单规则，<br>
     * 如果单词经过跳字匹配，部首拆分等从而导致词长大于词典单词词长，就认为经过伪装。<br>
     * note:空格间隔可能会导致误判，需要精确规则，目前性能问题不考虑。
     * 
     * @param wordTerm 分词
     * @return
     */
    private boolean isMaskWord(WordTerm wordTerm) {
        if (wordTerm == null) {
            return false;
        }
        TermExtraInfo info = wordTerm.termExtraInfo;
        if (info == null || !(info instanceof SkipTermExtraInfo)) {
            return false;
        }
        SkipTermExtraInfo skipTermExtraInfo = (SkipTermExtraInfo) info;
        return (skipTermExtraInfo.getWord().length() < wordTerm.length);
    }
}
