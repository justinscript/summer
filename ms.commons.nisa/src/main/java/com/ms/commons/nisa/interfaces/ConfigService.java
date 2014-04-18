/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.interfaces;

import com.ms.commons.nisa.listener.ConfigListener;

/**
 * 配置中心接口
 * 
 * @author zxc Apr 12, 2013 6:49:05 PM
 */
public interface ConfigService {

    String getKV(String key, String defaultValue);

    int getKV(String key, int defaultValue);

    float getKV(String key, float defaultValue);

    boolean getKV(String key, boolean defaultValue);

    String[] getKVStringArray(String key);

    int[] getKVIntArray(String key);

    float[] getKVFloatArray(String key);

    boolean[] getKVBooleanArray(String key);

    void addConfigListener(ConfigListener configListener);

    // 是否需要记录URL的访问次数和时间
    String KEY_RESOURCE_TOOLS_TRACE    = "B_reocrd.resource.trace";
    //
    String KEY_RESOURCE_WEB_TEST_TRACE = "B_webtest.trace";
}
