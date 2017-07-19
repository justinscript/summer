/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.standalone;

import com.ms.commons.core.CommonServiceLocator;
import com.ms.commons.standalone.service.StandaloneService;

/**
 * @author zxc Apr 12, 2013 8:58:26 PM
 */
public class StandaloneServiceLocator extends CommonServiceLocator {

    public static StandaloneService getStandaloneService() {
        if (context == null) {
            return null;
        }
        return (StandaloneService) context.getBean("standaloneService");
    }
}
