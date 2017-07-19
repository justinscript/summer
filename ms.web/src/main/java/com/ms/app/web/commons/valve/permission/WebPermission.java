/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.valve.permission;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zxc Apr 12, 2013 11:07:02 PM
 */
public class WebPermission {

    private String               itemCode;
    private String               noPermissionUrl;
    private Set<String>          acceptUrls = new HashSet<String>();
    private List<WebPermission> includes;

    public WebPermission() {
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getNoPermissionUrl() {
        return noPermissionUrl;
    }

    public void setNoPermissionUrl(String noPermissionUrl) {
        this.noPermissionUrl = noPermissionUrl;
    }

    public Set<String> getAcceptUrls() {
        return acceptUrls;
    }

    public void setAcceptUrls(Set<String> acceptUrls) {
        this.acceptUrls = acceptUrls;
    }

    public List<WebPermission> getIncludes() {
        return includes;
    }

    public void setIncludes(List<WebPermission> includes) {
        this.includes = includes;
    }

    public void init() {
        if (this.includes != null) {
            for (WebPermission permission : includes) {
                this.acceptUrls.addAll(permission.getAcceptUrls());
            }
        }
    }
}
