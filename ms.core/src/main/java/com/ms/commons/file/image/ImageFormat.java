/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.file.image;

/**
 * @author zxc Apr 12, 2013 1:21:02 PM
 */
public enum ImageFormat {
    //
    JPG(".jpg"),
    //
    PNG(".png"),
    //
    GIF(".gif");

    private String imageSuffix;

    ImageFormat(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }
}
