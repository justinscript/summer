/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.message.impl;

import java.util.ArrayList;
import java.util.List;

import com.ms.commons.test.common.message.Message;
import com.ms.commons.test.common.message.SourceTargetURL;

/**
 * @author zxc Apr 13, 2013 11:21:32 PM
 */
public class GetResourcesMessage implements Message {

    private String                resourceName;
    private List<SourceTargetURL> sourceTargetURLs;

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Load resource(s):" + resourceName);
        sb.append(" ::: ");

        if (sourceTargetURLs != null) {
            List<String> stl = new ArrayList<String>();
            for (SourceTargetURL sourceTargetURL : sourceTargetURLs) {
                stl.add(convertSourceTargetURLToString(sourceTargetURL));
            }
            sb.append(stl.toString());
        } else {
        }

        return sb.toString();
    }

    private String convertSourceTargetURLToString(SourceTargetURL sourceTargetURL) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (!sourceTargetURL.hasSourceURL()) {
            sb.append(sourceTargetURL.getTargetURL());
        } else {
            sb.append(sourceTargetURL.getSourceURL() + " >>> " + sourceTargetURL.getTargetURL());
        }
        sb.append(")");
        return sb.toString();
    }
}
