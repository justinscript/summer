/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.interfaces;

import java.util.List;

/**
 * @author zxc Apr 13, 2014 10:41:28 PM
 */
public interface Filter {

    // 过滤器，返回一个违反过滤规则的接收着数组
    List<String> doFilter(Message message);
}
