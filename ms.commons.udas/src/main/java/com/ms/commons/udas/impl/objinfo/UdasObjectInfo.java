/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.objinfo;

/**
 * 存入udas中的数据,最好都应该实现此接口,便于监控查看值的信息
 * 
 * @author zxc Apr 12, 2013 5:33:36 PM
 */
public interface UdasObjectInfo {

    String toSimpleString();
}
