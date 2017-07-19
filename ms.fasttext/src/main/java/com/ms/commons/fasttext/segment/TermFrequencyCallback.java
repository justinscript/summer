/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.segment;

import java.util.ArrayList;
import java.util.List;

/**
 * 带词频信息的词汇表预处理
 * 
 * @author zxc Apr 12, 2013 3:45:17 PM
 */
public class TermFrequencyCallback implements VocabularyProcess {

    public List<InternalElement> postProcess(List<char[]> lines) {
        // find last space
        List<InternalElement> list = new ArrayList<InternalElement>(lines.size());
        for (char[] cs : lines) {
            String s = String.valueOf(cs);
            int idx = s.lastIndexOf(' ');
            if (idx > 0) {

                try {
                    TermExtraInfo node = new TermFrequencyExtraNode(Integer.valueOf(s.substring(idx + 1)));
                    list.add(new InternalElement(s.substring(0, idx).toCharArray(), node));
                } catch (Exception e) {
                    System.out.println(cs);
                }

            }
        }
        return list;
    }
}
