/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.segment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc Apr 12, 2013 3:18:27 PM
 */
public class DefaultVocabularyProcess implements VocabularyProcess {

    public List<InternalElement> postProcess(List<char[]> wordList) {
        List<InternalElement> elements = new ArrayList<InternalElement>(wordList.size());
        for (char[] cs : wordList) {
            elements.add(new InternalElement(cs));
        }
        return elements;
    }
}
