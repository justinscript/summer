/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.file.image;

import org.apache.commons.lang.StringUtils;

/**
 * 图片合并时的位置
 * 
 * @author zxc Apr 12, 2013 1:20:31 PM
 */
public enum GravityEnum {
    // NorthWest, North, NorthEast, West, Center, East, SouthWest, South, SouthEast
    // 西北
    NorthWest("NorthWest", 10),
    // 东北
    NorthEast("NorthEast", 20),
    // 西南
    SouthWest("SouthWest", 30),
    // 东南
    SouthEast("SouthEast", 40),

    //
    North("North", 5),
    //
    West("West", 5),
    //
    Center("Center", 0),
    //
    South("South", 5),
    //
    East("East", 5);

    private String value;
    private int    order;

    GravityEnum(String value, int order) {
        this.value = value;
        this.order = order;
    }

    public String getValue() {
        return value;
    }

    public static GravityEnum getByValue(String gravity) {
        if (StringUtils.isBlank(gravity)) {
            return null;
        }
        for (GravityEnum g : GravityEnum.values()) {
            if (StringUtils.equalsIgnoreCase(g.getValue(), gravity)) {
                return g;
            }
        }
        return null;
    }

    public int getOrder() {
        return order;
    }
}
