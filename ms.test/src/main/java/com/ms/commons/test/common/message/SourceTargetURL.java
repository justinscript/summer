/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common.message;

import java.net.URL;

/**
 * @author zxc Apr 13, 2013 11:21:15 PM
 */
public class SourceTargetURL {

    private URL targetURL;
    private URL sourceURL;

    public SourceTargetURL(URL targetURL) {
        this.targetURL = targetURL;
    }

    public SourceTargetURL(URL targetURL, URL sourceURL) {
        this.targetURL = targetURL;
        this.sourceURL = sourceURL;
    }

    public boolean hasSourceURL() {
        return (sourceURL != null);
    }

    public URL getTargetURL() {
        return targetURL;
    }

    public void setTargetURL(URL targetURL) {
        this.targetURL = targetURL;
    }

    public URL getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(URL sourceURL) {
        this.sourceURL = sourceURL;
    }
}
