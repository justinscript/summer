/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.view;

import java.util.Map;

/**
 * @author zxc Apr 12, 2013 4:21:58 PM
 */
public class ViewToolsFactory {

    private Map<String, Object> viewTools;

    public Map<String, Object> getViewTools() {
        return viewTools;
    }

    public void setViewTools(Map<String, Object> viewTools) {
        this.viewTools = viewTools;
    }
}
