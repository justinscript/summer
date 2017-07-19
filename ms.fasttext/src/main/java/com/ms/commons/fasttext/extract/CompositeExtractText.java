/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.extract;

/**
 * <pre>
 * 1. 转换成简体
 * 2. 转换全角到半角
 * 3. 过滤acsii
 * </pre>
 * 
 * @author zxc Apr 12, 2013 3:38:51 PM
 */
public class CompositeExtractText implements ExtractText {

    public String getText(String src) {
        if (src == null) {
            return src;
        }
        return CharNormalization.compositeTextConvert(src, true, true, false, true, false, false, false);
    }
}
