/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.similitude;

/**
 * @author zxc Apr 12, 2013 3:23:36 PM
 */
public class SimilitudeComposite implements SimilitudeText {

    private SimilitudeText cos = new SimilitudeUsingCos();
    private SimilitudeText gjc = new SimilitudeUsingGJC();
    private SimilitudeText ld  = new SimilitudeUsingLD();

    private SimilitudeType compareType;

    public SimilitudeComposite(SimilitudeType type) {
        this.compareType = type;
    }

    public double similitudeValue(IDocument src, IDocument dest) {
        if (compareType == SimilitudeType.COS_TYPE) {
            return cos.similitudeValue(src, dest);
        } else if (compareType == SimilitudeType.GJC_TYPE) {
            return gjc.similitudeValue(src, dest);
        } else if (compareType == SimilitudeType.LD_TYPE) {
            return ld.similitudeValue(src, dest);
        }
        return 0;
    }
}
