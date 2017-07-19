/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.prepare.event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc Apr 14, 2013 12:22:36 AM
 */
public class PrepareEventUtil {

    private static List<PrepareEvent> eventList = new ArrayList<PrepareEvent>();

    public static void register(PrepareEvent prepareEvent) {
        synchronized (eventList) {
            eventList.add(prepareEvent);
        }
    }

    public static void clearRegister() {
        synchronized (eventList) {
            eventList.clear();
        }
    }

    public static List<PrepareEvent> getPrepareEvents() {
        synchronized (eventList) {
            return new ArrayList<PrepareEvent>(eventList);
        }
    }
}
