/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.file.service;

import com.ms.commons.core.CommonServiceLocator;
import com.ms.commons.file.interfaces.FileService;

/**
 * 类FileServiceLocator.java的实现描述：
 * 
 * @author yjg 2011-8-11 下午3:53:28
 */
public class FileServiceLocator extends CommonServiceLocator {

    public static FileService getFileService() {
        if (context == null) {
            return null;
        }
        return (FileService) context.getBean("fileService");
    }

}
