/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.similitude;

import java.util.Map;
import java.util.Set;

import com.ms.commons.fasttext.segment.WordTerm;

/**
 * @author zxc Apr 12, 2013 3:22:39 PM
 */
public class SimilitudeUsingCos implements SimilitudeText {

    @SuppressWarnings("unchecked")
    public double similitudeValue(IDocument srcDoc, IDocument destDoc) {
        int srcLen = srcDoc.getDocumentLength();
        int destLen = srcDoc.getDocumentLength();
        if (srcLen == 0 || destLen == 0) {
            return 0;
        }
        Map<Integer, WordTerm> src = (Map<Integer, WordTerm>) srcDoc.getWordTerms();
        Map<Integer, WordTerm> dest = (Map<Integer, WordTerm>) destDoc.getWordTerms();
        WordTerm w1, w2;
        double tf = 0;
        double idf_x = 0;
        double idf_y = 0;
        double count = 0;
        Set<Integer> set = src.keySet().size() > dest.keySet().size() ? dest.keySet() : src.keySet();
        for (Integer i : set) {
            w1 = src.get(i);
            if (w1 != null) {
                w2 = dest.get(i);
                if (w2 != null) {
                    tf += w1.eigenvalue * w2.eigenvalue; // d1*c1
                    idf_x += w1.eigenvalue * w1.eigenvalue; // |d1|
                    idf_y += w2.eigenvalue * w2.eigenvalue;// |c1|
                    count += 1;
                }
            }
        }
        if (((count / ((double) src.size())) <= 0.1) || ((count / ((double) dest.size())) <= 0.1)) {
            return 0;
        }
        idf_x = Math.sqrt(idf_x);
        idf_y = Math.sqrt(idf_y);
        // 考虑文档长度的对比
        double delta = (srcLen > destLen ? (double) destLen / srcLen : (double) srcLen / destLen);
        return (tf / (idf_x * idf_y)) * delta;
    }
}
