/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.security.xss.model;

/**
 * Html 标记处理办法
 * 
 * <pre>
 *  1. REMOVE 删除标记和内容
 *  2. ACCEPT 保留标记和内容
 *  其余的情况是删除标记，但保留内容
 * </pre>
 * 
 * @author zxc Apr 12, 2013 3:28:13 PM
 */
public enum Action {
    REMOVE, ACCEPT, CSSHANDLER
}
