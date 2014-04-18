/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.config.listener;

/**
 * 具体的应用如果想监听配置项有修改的话，实现找个接口就可以了，同时请到ConfigService.addConfigListener()注册
 * 
 * @author zxc Apr 12, 2013 6:48:20 PM
 */
public interface ConfigListener {

    /**
     * 更新配置文件
     */
    void updateConfig();

    /**
     * 返回注册监听器的名字
     * 
     * @return
     */
    String getName();
}
