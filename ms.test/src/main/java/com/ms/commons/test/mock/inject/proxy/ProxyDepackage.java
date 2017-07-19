/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock.inject.proxy;

import java.lang.reflect.Proxy;

/**
 * @author zxc Apr 14, 2013 12:13:48 AM
 */
public interface ProxyDepackage {

    String proxyName();

    Object depackage(Proxy proxy);
}
