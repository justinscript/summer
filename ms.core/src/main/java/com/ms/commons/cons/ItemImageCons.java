/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.cons;

/**
 * @author zxc Apr 12, 2013 1:11:46 PM
 */
public interface ItemImageCons {

    //
    public static final Integer DEFAULT_FONT_SIZE       = 12;
    //
    public static final String  DEFAULT_FONT_NAME       = "Microsoft-YaHei-Bold";

    /**
     * 图片存储位置
     */
    public static final String  TEMP_IMAGE_PATH         = "/tmp/ferrari/img";       // System.getProperty("user.home")
                                                                                     // +
    // 主图最大大小
    public static final int     MAX_IMAGE_SIZE_512000   = 511999;

    // 图片的目录结构暂时定为：img/storeId/numIid/
    String                      IMAGE_PATH_ROOT         = "/data/static/style/pic/";

    // 素材的绝对路径
    // String ROOT_ICON_PATH_TEST = "/data/static/style/";
    // 主图缺省宽度
    Integer                     DEFAULT_Main_PIC_WIDTH  = 800;
    // 主图缺省高度
    Integer                     DEFAULT_Main_PIC_HEIGHT = 800;
}
