/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.dbencoding;

/**
 * @author zxc Apr 13, 2013 11:22:27 PM
 */
public interface DbEncoding<T> {

    T encode(Object value);

    Object decode(T value);
}
