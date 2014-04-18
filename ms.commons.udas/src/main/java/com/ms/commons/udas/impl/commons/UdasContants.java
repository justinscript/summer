/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.commons;

/**
 * @author zxc Apr 12, 2013 5:37:16 PM
 */
public interface UdasContants {

    /** 永不过期 */
    int never_expire    = 0;
    /** 当Key大于这个数据时,就写到DBD中去 */
    int key_bundle_size = 100;
}
