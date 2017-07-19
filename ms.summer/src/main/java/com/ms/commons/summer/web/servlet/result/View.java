/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.servlet.result;

/**
 * @author zxc Apr 12, 2013 4:44:58 PM
 */
public class View extends AbstractWebResult {

    private boolean uselayout = true;

    public View(String path) {
        super(path);
    }

    public View(String nameSpace, String path) {
        super(nameSpace, path);
    }

    public boolean isUselayout() {
        return uselayout;
    }

    /**
     * 设置不需要布局，默认情况次啊需要布局
     * 
     * @param userlayout
     * @return
     */
    public View setUselayout(boolean uselayout) {
        this.uselayout = uselayout;
        return this;
    }
}
