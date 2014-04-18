/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import java.util.ArrayList;
import java.util.List;

import com.ms.commons.config.interfaces.ConfigService;
import com.ms.commons.config.listener.ConfigListener;
import com.ms.commons.config.service.ConfigServiceLocator;

/**
 * 读取【应用】配置项，并受【应用】配置改变而改变的静态页面缓存
 * 
 * @author zxc Apr 12, 2013 10:42:35 PM
 */
public class RemoteConfigPageCache extends DefaultPageCache implements ConfigListener {

    private String enableKey;
    private String matchUrlsKey;

    public void init() {
        initConfig();
        ConfigService congfigService = ConfigServiceLocator.getCongfigService();
        congfigService.addConfigListener(this);
    }

    private void initConfig() {
        ConfigService congfigService = ConfigServiceLocator.getCongfigService();
        boolean enablekv = congfigService.getKV(enableKey, false);
        setEnable(enablekv);
        String[] kvStringArray = congfigService.getKVStringArray(matchUrlsKey);
        if (kvStringArray != null && kvStringArray.length > 0) {
            List<String> list = new ArrayList<String>();
            for (String url : kvStringArray) {
                if (!url.startsWith(PATH_SEP)) {
                    return;
                }
                list.add(url);
            }
            setMatchUrls(list);
        }
    }

    public void setEnableKey(String enableKey) {
        this.enableKey = enableKey;
    }

    public void setMatchUrlsKey(String matchUrlsKey) {
        this.matchUrlsKey = matchUrlsKey;
    }

    public void updateConfig() {
        initConfig();
    }

    public String getName() {
        return null;
    }
}
