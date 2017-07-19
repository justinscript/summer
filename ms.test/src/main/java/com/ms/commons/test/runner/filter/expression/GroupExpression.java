/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runner.filter.expression;

import java.util.List;

/**
 * @author zxc Apr 14, 2013 12:19:56 AM
 */
public interface GroupExpression {

    boolean isMatch(List<String> groupList);
}
