/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.util.json;

/**
 * @author zxc Apr 12, 2013 4:25:07 PM
 */
public class JsonDataConvertException extends RuntimeException {

    private static final long serialVersionUID = 207008352846681699L;

    public JsonDataConvertException(Object value, Exception e) {
        super(e);
    }
}
