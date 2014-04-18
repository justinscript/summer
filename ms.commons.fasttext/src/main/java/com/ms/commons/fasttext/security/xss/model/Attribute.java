/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.security.xss.model;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author zxc Apr 12, 2013 3:27:58 PM
 */
public class Attribute {

    public String            name;
    public List<Pattern>     allowedRegExp;
    public RestrictAttribute restrictAttribute = RestrictAttribute.NONE;

    @Override
    public String toString() {
        return name;
    }
}
