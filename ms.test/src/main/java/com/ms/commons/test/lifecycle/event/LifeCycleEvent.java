/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.lifecycle.event;

/**
 * @author zxc Apr 13, 2013 11:45:19 PM
 */
public interface LifeCycleEvent {

    Object doEvent(LifeCycleMessage message);
}
