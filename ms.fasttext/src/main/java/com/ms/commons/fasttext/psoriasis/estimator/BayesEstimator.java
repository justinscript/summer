/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis.estimator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ms.commons.fasttext.psoriasis.PsoriasisUtil;
import com.ms.commons.fasttext.psoriasis.SkipTermExtraInfo;
import com.ms.commons.fasttext.segment.TermExtraInfo;
import com.ms.commons.fasttext.segment.WordTerm;

/**
 * @author zxc Apr 12, 2013 3:31:15 PM
 */
public class BayesEstimator implements Estimator {

    private static final Log     logger       = LogFactory.getLog(BayesEstimator.class);

    private List<WeightAdjuster> adjusterList = new ArrayList<WeightAdjuster>();

    public synchronized void addWeightAdjuster(WeightAdjuster adjuster) {
        if (adjuster == null || adjusterList.contains(adjuster)) {
            logger.error("adjuster can not be null or adjuster already exists.");
            return;
        }
        adjusterList.add(adjuster);
        if (logger.isDebugEnabled()) {
            logger.debug("WeightAdjuster " + adjuster + "is adding!");
        }
    }

    public int getAdjusterCount() {
        return adjusterList.size();
    }

    public WeightAdjuster getAdjusterAt(int index) {
        return adjusterList.get(index);
    }

    public synchronized void removeWeightAdjuster(WeightAdjuster adjuster) {
        if (adjuster == null) {
            logger.error("adjuster can not be null");
            return;
        }
        adjusterList.remove(adjuster);
        if (logger.isDebugEnabled()) {
            logger.debug("WeightAdjuster " + adjuster + "is removed!");
        }
    }

    /**
     * 根据业务给定的词语权重对文档进行评分，目前实现是Bayes算法，不考虑词频影响。<br>
     * 算法如下：<br>
     * 如果文档中分别有词t1,t2,t3,...,tn,其权重分别为w1,w2,w3,...,wn<br>
     * 则根据Bayes算法，文档权重为：<br>
     * 设p1 = w1*w2*w3*...*wn<br>
     * 设p2=(1-w1)*(1-w2)*(1-w3)*...*(1-wn)<br>
     * 则文档权重 w = p1/(p1+p2)<br>
     * 如果p1+p2=0,文档权重直接设为1 <br>
     * 因为考虑词频收敛太快，这里不考虑词频<br>
     * 
     * @param wordTermList 文档分词结果
     * @return 文档权重
     */
    public double estimateValue(List<WordTerm> wordTermList) {
        return estimateValue(wordTermList, true);
    }

    /**
     * 根据业务给定的词语权重对文档进行评分，目前实现是Bayes算法。<br>
     * 算法如下：<br>
     * 如果文档中分别有词t1,t2,t3,...,tn,其权重分别为w1,w2,w3,...,wn<br>
     * 则根据Bayes算法，文档权重为：<br>
     * 设p1 = w1*w2*w3*...*wn<br>
     * 设p2=(1-w1)*(1-w2)*(1-w3)*...*(1-wn)<br>
     * 则文档权重 w = p1/(p1+p2)<br>
     * 如果p1+p2=0,文档权重直接设为1 <br>
     * 
     * @param wordTermList 文档分词结果
     * @param ignoreTF 是否或略词频影响，如果考虑词频，文档权重可能会很快收敛,ignore term frequency(TF)
     * @return 文档权重
     */
    public double estimateValue(List<WordTerm> wordTermList, boolean ignoreTF) {
        // 调用adjuster.beginWeight初始化Adjuster
        Set<String> wordSet = PsoriasisUtil.parseWordSet(wordTermList);
        Set<String> tmpWordSet = null;
        if (ignoreTF) {
            tmpWordSet = new HashSet<String>();
        }
        for (WeightAdjuster adjuster : adjusterList) {
            try {
                adjuster.beginAdjust(wordSet);
            } catch (Exception e) {
                logger.error("Adjuster " + adjuster + "initialization error!" + e.getMessage());
            }
        }
        try {
            if (wordTermList == null || wordTermList.size() == 0) {
                return 0;
            }
            double prob1 = 1, prob2 = 1;
            for (WordTerm wordTerm : wordTermList) {
                TermExtraInfo info = wordTerm.termExtraInfo;
                // 没有weight信息，不参与评估
                if (info == null || !(info instanceof SkipTermExtraInfo)) {
                    continue;
                }
                if (ignoreTF) {
                    if (tmpWordSet.contains(((SkipTermExtraInfo) wordTerm.termExtraInfo).getWord())) {// 如果考虑词频收敛太快，这里不考虑词频
                        continue;
                    } else {
                        tmpWordSet.add(((SkipTermExtraInfo) wordTerm.termExtraInfo).getWord());
                    }
                }
                SkipTermExtraInfo info1 = (SkipTermExtraInfo) info;
                double weight = info1.getWeight();
                // 调整单个词的权重
                for (WeightAdjuster adjuster : adjusterList) {
                    double tmp = weight;
                    try {
                        // allow user to adjust word weight
                        weight = adjuster.adjustWord(wordTerm, weight);
                    } catch (Exception e) {
                        logger.error("Word weight adjust error." + e.getMessage());
                        weight = tmp;
                    }
                }
                weight = weight >= 0 ? (weight <= 1 ? weight : 1) : 0;
                prob1 *= weight;
                prob2 *= (1 - weight);
            }
            double prob = 0;
            if (prob1 + prob2 != 0) {
                prob = prob1 / (prob1 + prob2);
            } else {
                prob = 1;// 如果
            }
            // allow user to adjust document weight
            for (WeightAdjuster adjuster : adjusterList) {
                double tmp = prob;
                try {
                    prob = adjuster.adjustDocument(wordSet, prob);
                } catch (Exception e) {
                    logger.error("Weight adjust error!" + e.getMessage());
                    // when error ocured,restore prob to clear exception impact.
                    prob = tmp;
                }
            }
            return prob >= 0 ? (prob <= 1 ? prob : 1) : 0;
        } finally {
            for (WeightAdjuster adjuster : adjusterList) {
                adjuster.endAdjust(wordSet);
            }
        }
    }
}
