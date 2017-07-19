/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.similitude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ms.commons.fasttext.segment.TermFrequencyExtraNode;
import com.ms.commons.fasttext.segment.WordTerm;

/**
 * @author zxc Apr 12, 2013 3:24:44 PM
 */
public class DocumentKMeans implements IDocument {

    private Map<Integer, WordTerm> frequnce = null;
    private int                    documentLength;

    public DocumentKMeans(int documentLength, List<WordTerm> wordTerms) {
        long D = 0x5f5e100;
        this.documentLength = documentLength;
        // 计算词频, 为了提高性能， 只计算真实分词部分
        frequnce = new HashMap<Integer, WordTerm>(wordTerms.size());
        for (WordTerm wordTerm : wordTerms) {
            WordTerm tmp = frequnce.get(wordTerm.position);
            if (wordTerm.position != -1) { // skip not word
                if (tmp == null) {
                    wordTerm.frequency = 1;
                    frequnce.put(wordTerm.position, wordTerm);
                } else {
                    tmp.frequency += 1;
                }
            }
        }
        // 计算特征值
        double n1, n2;
        for (WordTerm w : wordTerms) {
            if (w.position != -1) {
                n1 = (double) w.frequency / (double) documentLength;
                int f = ((TermFrequencyExtraNode) w.termExtraInfo).termFrequency;
                n2 = Math.abs(Math.log(((double) D) / ((double) f)));
                w.eigenvalue = n1 * n2;
            }
        }

    }

    public Object getWordTerms() {
        return frequnce;
    }

    public SimilitudeText getSimilitudeText() {
        return new SimilitudeUsingCos();
    }

    public int getDocumentLength() {
        return this.documentLength;
    }
}
