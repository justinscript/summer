/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.ip;

/**
 * 一条IP范围记录，不仅包括国家和区域，也包括起始IP和结束IP *
 * 
 * @author zxc Apr 12, 2013 1:29:40 PM
 */
public class IPEntry {

    public String beginIp;
    public String endIp;
    public String country;
    public String area;

    /**
     * 构造函数
     */
    public IPEntry() {
        beginIp = endIp = country = area = "";
    }

    public String toString() {
        return this.area + "  " + this.country + "IP范围:" + this.beginIp + "-" + this.endIp;
    }
}
