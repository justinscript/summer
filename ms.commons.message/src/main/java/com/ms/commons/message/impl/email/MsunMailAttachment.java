/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl.email;

import java.io.Serializable;

/**
 * 邮件附件
 * 
 * @author zxc Apr 13, 2014 10:46:09 PM
 */
public class MsunMailAttachment implements Serializable {

    private static final long serialVersionUID = -1574881023453305830L;

    /**
     * 附件名称
     */
    public String             name;

    /**
     * 附件数据
     */
    public byte[]             data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
