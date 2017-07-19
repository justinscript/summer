/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.statics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Apr 12, 2013 10:41:27 PM
 */
public class StaticResources {

    private static final String   ALL = "*";
    private Map<String, String[]> cache;
    private List<String>          patternUrls;
    private List<String>          css;
    private List<String>          appjs;
    private List<String>          runjs;

    public void initData() {
        cache = new ConcurrentHashMap<String, String[]>();
        clearSuffix(css);
        clearSuffix(appjs);
        clearSuffix(runjs);
    }

    private void clearSuffix(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        int length = list.size();
        for (int i = 0; i < length; i++) {
            String str = list.get(i);
            int index = str.lastIndexOf('.');
            if (index != -1) {
                str = str.substring(0, index);
                list.set(i, str);
            }
        }
    }

    public void setPatternUrls(List<String> patternUrls) {
        this.patternUrls = patternUrls;
    }

    public void setCss(List<String> css) {
        this.css = css;
    }

    public void setAppjs(List<String> appjs) {
        this.appjs = appjs;
    }

    public void setRunjs(List<String> runjs) {
        this.runjs = runjs;
    }

    /**
     * url是否匹配
     * 
     * @param urlPath
     * @return
     */
    public boolean lookup(String urlPath) {
        if (patternUrls == null || patternUrls.isEmpty()) {
            return false;
        }
        for (String pattern : patternUrls) {
            int index = pattern.lastIndexOf(ALL);
            if (index != -1) {
                pattern = pattern.substring(0, index);
                if (urlPath.startsWith(pattern)) {
                    return true;
                }
            } else {
                if (pattern.equals(urlPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取css列表
     * 
     * @param styleServer
     * @param cssVersion
     * @param debugSuffix
     * @return
     */
    public String[] getCss(String styleServer, String cssVersion, String debugSuffix) {
        return getResources(StaticResourcesEnum.CSS, styleServer, cssVersion, debugSuffix, css);
    }

    /**
     * 获取appjs列表
     * 
     * @param styleServer
     * @param cssVersion
     * @param debugSuffix
     * @return
     */
    public String[] getAppJs(String styleServer, String cssVersion, String debugSuffix) {
        return getResources(StaticResourcesEnum.APPJS, styleServer, cssVersion, debugSuffix, appjs);
    }

    /**
     * 获取runjs列表
     * 
     * @param styleServer
     * @param cssVersion
     * @param debugSuffix
     * @return
     */
    public String[] getRunJs(String styleServer, String cssVersion, String debugSuffix) {
        return getResources(StaticResourcesEnum.RUNJS, styleServer, cssVersion, debugSuffix, runjs);
    }

    private String[] getResources(StaticResourcesEnum srtype, String styleServer, String cssVersion,
                                  String debugSuffix, List<String> sFiles) {
        String key = getKey(srtype, cssVersion, debugSuffix);
        String[] strings = cache.get(key);
        if (strings != null) {
            return strings;
        }
        if (sFiles == null || sFiles.isEmpty()) {
            return null;
        }
        int length = sFiles.size();
        String[] cssFiles = new String[length];
        for (int i = 0; i < length; i++) {
            String cssFile = sFiles.get(i);
            String create = create(srtype, styleServer, cssVersion, debugSuffix, cssFile);
            cssFiles[i] = create;
        }
        cache.put(key, cssFiles);
        return cssFiles;
    }

    // $!{url.styleServer}/css$!{static.cssVersion}/build/site/rhine/list$!{staticTools.suffix}.css
    private String create(StaticResourcesEnum type, String styleServer, String cssVersion, String debugSuffix,
                          String file) {
        StringBuilder sb = new StringBuilder(200);
        sb.append(styleServer).append(type.getBasePath()).append(cssVersion).append(file);
        if (StringUtils.isNotEmpty(debugSuffix)) {
            sb.append(debugSuffix);
        }
        sb.append(type.getSuffix());
        return sb.toString();
    }

    private String getKey(StaticResourcesEnum type, String cssVersion, String debugSuffix) {
        StringBuilder sb = new StringBuilder(100);
        sb.append(type.getName());
        if (StringUtils.isNotEmpty(cssVersion)) {
            sb.append(cssVersion);
        }
        if (StringUtils.isNotEmpty(debugSuffix)) {
            sb.append(debugSuffix);
        }
        return sb.toString();
    }
}
