/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.validation.validator;

/**
 * @author zxc Apr 12, 2013 11:17:30 PM
 */
public interface Validator {

    boolean isValid(Object value);

    String getErrorMessage(String displayName);
}
