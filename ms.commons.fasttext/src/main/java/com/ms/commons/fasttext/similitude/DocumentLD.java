/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.similitude;

import java.util.ArrayList;
import java.util.List;

import com.ms.commons.fasttext.segment.WordTerm;

/**
 * @author zxc Apr 12, 2013 3:23:58 PM
 */
public class DocumentLD implements IDocument {

    int hashword[];

    public DocumentLD(List<WordTerm> wordTerms) {
        // 删除非词汇信息
        List<WordTerm> frequnce = new ArrayList<WordTerm>();
        for (WordTerm wordTerm : wordTerms) {
            if (wordTerm.position != -1) {
                // skip not word
                frequnce.add(wordTerm);
            }
        }
        // end for
        hashword = new int[frequnce.size()];
        int i = 0;
        for (WordTerm word2 : frequnce) {
            hashword[i] = word2.position;
            i++;
        }
    }

    public Object getWordTerms() {
        return hashword;
    }

    public SimilitudeText getSimilitudeText() {

        return new SimilitudeUsingLD();
    }

    public Object getAttach() {
        return null;
    }

    public void setAttach(Object attach) {
    }

    public int getDocumentLength() {
        return 0;
    }
}
