/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ms.commons.test.lifecycle.event.LifeCycleEvent;

/**
 * @author zxc Apr 13, 2013 11:45:02 PM
 */
public class TestCaseLifeCycleManageUtil {

    public static final String                       SYS_INIT       = "SYS_INIT";
    public static final String                       CLASS_ENTER    = "CLASS_ENTER";
    public static final String                       CLASS_EXIT     = "CLASS_EXIT";
    public static final String                       METHOD_ENTER   = "METHOD_ENTER";
    public static final String                       METHOD_EXIT    = "METHOD_EXIT";

    private static List<LifeCycleEvent>              EMPTY_LCE_LIST = Collections.unmodifiableList(new ArrayList<LifeCycleEvent>(
                                                                                                                                 0));
    private static Map<String, List<LifeCycleEvent>> LIFE_CYCLE_MAP = new HashMap<String, List<LifeCycleEvent>>();

    public static void registerLifeCycleEvent(String eventType, LifeCycleEvent event) {
        synchronized (LIFE_CYCLE_MAP) {
            getLifeCycleEventsWithoutSync(eventType).add(event);
        }
    }

    public static void unregisterLifeCycleEvent(String eventType, LifeCycleEvent event) {
        synchronized (LIFE_CYCLE_MAP) {
            getLifeCycleEventsWithoutSync(eventType).remove(event);
        }
    }

    public static List<LifeCycleEvent> getLifeCycleEvents(String eventType) {
        synchronized (LIFE_CYCLE_MAP) {
            List<LifeCycleEvent> events = LIFE_CYCLE_MAP.get(eventType);
            return (events == null) ? EMPTY_LCE_LIST : new ArrayList<LifeCycleEvent>(events);
        }
    }

    private static List<LifeCycleEvent> getLifeCycleEventsWithoutSync(String eventType) {
        List<LifeCycleEvent> events = LIFE_CYCLE_MAP.get(eventType);
        if (events == null) {
            events = new ArrayList<LifeCycleEvent>();
            LIFE_CYCLE_MAP.put(eventType, events);
        }
        return events;
    }
}
