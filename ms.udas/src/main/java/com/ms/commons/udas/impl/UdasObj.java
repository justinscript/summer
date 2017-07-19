/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl;

import java.io.Serializable;

/**
 * @author zxc Apr 12, 2013 5:33:12 PM
 */
public class UdasObj implements Serializable {

    private static final long serialVersionUID = 3093834281889759513L;
    private long              creatTime;                              // 创建时间
    private Serializable      value;                                  // 真正存储的对象

    public UdasObj(Serializable value) {
        this.creatTime = System.currentTimeMillis();
        this.value = value;
    }

    public UdasObj(long creatTime, Serializable value) {
        this.creatTime = creatTime;
        this.value = value;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public Serializable getValue() {
        return value;
    }
}
