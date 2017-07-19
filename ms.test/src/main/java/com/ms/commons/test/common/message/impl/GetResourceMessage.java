/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.message.impl;

import java.net.URL;

import com.ms.commons.test.common.message.Message;
import com.ms.commons.test.common.message.SourceTargetURL;

/**
 * @author zxc Apr 13, 2013 11:21:42 PM
 */
public class GetResourceMessage implements Message {

    private String          resourceName;
    private SourceTargetURL sourceTargetURL;

    public GetResourceMessage(String resourceName, URL targetURL) {
        this.resourceName = resourceName;
        this.sourceTargetURL = new SourceTargetURL(targetURL);
    }

    public GetResourceMessage(String resourceName, URL targetURL, URL sourceURL) {
        this.resourceName = resourceName;
        this.sourceTargetURL = new SourceTargetURL(targetURL, sourceURL);
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Load resource:" + resourceName);
        sb.append(" ::: ");
        if (!sourceTargetURL.hasSourceURL()) {
            sb.append(sourceTargetURL.getTargetURL());
        } else {
            sb.append(sourceTargetURL.getSourceURL() + " >>> " + sourceTargetURL.getTargetURL());
        }
        return sb.toString();
    }
}
