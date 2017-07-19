/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.autoconfig;

import java.util.HashMap;

/**
 * @author zxc Apr 15, 2014 10:17:11 PM
 */
public class AutoConfigInfo {

    HashMap<String, String> fileMap;

    public HashMap<String, String> getFileMap() {
        return fileMap;
    }

    public void setFileMap(HashMap<String, String> fileMap) {
        this.fileMap = fileMap;
    }
}
