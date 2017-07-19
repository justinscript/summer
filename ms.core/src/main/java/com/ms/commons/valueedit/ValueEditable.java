/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.valueedit;

import java.util.Map;

/**
 * @author zxc Apr 12, 2013 2:26:56 PM
 */
public interface ValueEditable {

    /**
     * 更新属性的值，将更新后的值放到name2Values中去
     * 
     * @param raw 需要回去属性的对象
     * @param name2Values 当前对象的属性名和对应的值
     */
    @SuppressWarnings("rawtypes")
    void edit(Object raw, Map name2Values);
}
