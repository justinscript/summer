/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.pagecache;

import com.ms.app.web.commons.pagecache.DefaultPageCache;

/**
 * @author zxc Apr 12, 2013 11:23:00 PM
 */
public class BaobeiPageCache extends DefaultPageCache {

    public int getEffectiveTime() {
        return 2;
    }
}
