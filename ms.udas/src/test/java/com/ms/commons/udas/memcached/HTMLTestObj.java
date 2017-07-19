/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.memcached;

import java.io.Serializable;

/**
 * @author zxc Apr 12, 2013 6:39:55 PM
 */
public class HTMLTestObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private String            content;

    public HTMLTestObj(String content) {
        this.setContent(content);
    }

    @Override
    public String toString() {
        return " Content: " + this.getContent();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HTMLTestObj)) {
            return false;
        }
        return ((HTMLTestObj) obj).getContent().equals(this.content);
    }
}
