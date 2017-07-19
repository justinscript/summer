/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.treedb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.processors.DefaultValueProcessorMatcher;
import net.sf.json.util.JSONUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ms.commons.test.treedb.JsonParser.Node;

/**
 * @author zxc Apr 13, 2013 11:32:10 PM
 */
@SuppressWarnings("rawtypes")
public class JsonObjectUtils {

    private static final Log        log               = LogFactory.getLog(JsonObjectUtils.class);
    private static final JsonConfig defaultJsonConfig = new JsonConfig();
    static {
        DefaultValueProcessor defaultValueProcessor = new DefaultValueProcessor() {

            public Object getDefaultValue(Class type) {
                return null;
            }
        };
        DefaultValueProcessorMatcher defaultValueProcessorMatcher = new DefaultValueProcessorMatcher() {

            @SuppressWarnings("unchecked")
            public Object getMatch(Class target, Set set) {
                if (target.isArray()) {
                    return Collection.class;
                }
                Set<Class> cset = (Set<Class>) set;
                for (Class c : cset) {
                    Class clazz = null;
                    try {
                        clazz = target.asSubclass(c);
                    } catch (Exception e) {
                    }
                    if (clazz != null) {
                        return c;
                    }
                }
                return null;
            }
        };
        defaultJsonConfig.setDefaultValueProcessorMatcher(defaultValueProcessorMatcher);
        defaultJsonConfig.registerDefaultValueProcessor(Number.class, defaultValueProcessor);
        defaultJsonConfig.registerDefaultValueProcessor(String.class, defaultValueProcessor);
        defaultJsonConfig.registerDefaultValueProcessor(Collection.class, defaultValueProcessor);
        defaultJsonConfig.registerDefaultValueProcessor(Boolean.class, defaultValueProcessor);
        defaultJsonConfig.registerDefaultValueProcessor(Enum.class, defaultValueProcessor);
    }

    public static boolean isSameObject(Object obj0, Object obj1, String[] fields, boolean exclusive) {
        if (obj0 == null && obj1 == null) {
            return true;
        }
        if (obj0 == null || obj1 == null || !obj0.getClass().equals(obj1.getClass())) {
            return false;
        }
        if (JSONUtils.isNumber(obj0) || JSONUtils.isBoolean(obj0) || JSONUtils.isString(obj0)) {
            return obj0.equals(obj1);
        }
        String jsons0 = null;
        String jsons1 = null;
        if (obj0 instanceof Enum || JSONUtils.isArray(obj0)) {
            jsons0 = JSONArray.fromObject(obj0, defaultJsonConfig).toString();
            jsons1 = JSONArray.fromObject(obj1, defaultJsonConfig).toString();
        } else {
            jsons0 = JSONObject.fromObject(obj0, defaultJsonConfig).toString();
            jsons1 = JSONObject.fromObject(obj1, defaultJsonConfig).toString();
        }
        if (log.isDebugEnabled()) {
            log.debug("Raw result0 : " + jsons0);
            log.debug("Raw result1 : " + jsons1);
        }
        System.out.println("Raw result0 : " + jsons0);
        System.out.println("Raw result1 : " + jsons1);
        if (fields == null || fields.length == 0) {
            if (exclusive) {
                return jsons0.equals(jsons1);
            } else {
                return true;
            }
        }
        List<Node> valuelist0 = JsonParser.filterFields(jsons0);
        List<Node> valuelist1 = JsonParser.filterFields(jsons1);

        List<Node> list0 = new ArrayList<Node>();
        List<Node> list1 = new ArrayList<Node>();
        for (String str : fields) {
            String[] strs = str.split("\\.");
            if (strs == null || strs.length == 0) {
                continue;
            }
            list0.addAll(findFieldsStr(strs, valuelist0, jsons0));
            list1.addAll(findFieldsStr(strs, valuelist1, jsons1));
        }
        if (exclusive) {
            return isSameExclusiveObjs(list0, list1, jsons0, jsons1);
        } else {
            return isSameInclusiveObjs(list0, list1, jsons0, jsons1);
        }
    }

    private static boolean isSameInclusiveObjs(List<Node> inclusiveList0, List<Node> inclusiveList1, String jsons0,
                                               String jsons1) {
        Collections.sort(inclusiveList0);
        Collections.sort(inclusiveList1);
        String str0 = calInclusive(inclusiveList0, jsons0);
        String str1 = calInclusive(inclusiveList1, jsons1);
        if (log.isDebugEnabled()) {
            log.debug("Processed result0 : " + str0);
            log.debug("Processed result1 : " + str1);
        }
        System.out.println("Processed result0 : " + str0);
        System.out.println("Processed result1 : " + str1);
        return str0.equals(str1);
    }

    /**
     * @param inclusiveList0
     * @param jsons0
     * @return
     */
    private static String calInclusive(List<Node> inclusiveList, String jsons) {
        StringBuilder sb = new StringBuilder();
        for (Node p : inclusiveList) {
            sb.append(jsons.subSequence(p.kvStart, p.kvEnd + 1));
        }
        return sb.toString();
    }

    private static boolean isSameExclusiveObjs(List<Node> exclusiveLists0, List<Node> exclusiveLists1, String jsons0,
                                               String jsons1) {
        Collections.sort(exclusiveLists0);
        Collections.sort(exclusiveLists1);
        String str0 = calExclusive(exclusiveLists0, jsons0);
        String str1 = calExclusive(exclusiveLists1, jsons1);
        if (log.isDebugEnabled()) {
            log.debug("Processed result0 : " + str0);
            log.debug("Processed result1 : " + str1);
        }
        System.out.println("Processed result0 : " + str0);
        System.out.println("Processed result1 : " + str1);
        return str0.equals(str1);
    }

    private static String calExclusive(List<Node> list, String jsons) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        for (Node p : list) {
            sb.append(jsons.subSequence(start, p.kvStart));
            start = p.kvEnd + 1;
        }
        sb.append(jsons.subSequence(start, jsons.length()));
        return sb.toString();
    }

    private static List<Node> findFieldsStr(String[] fields, List<Node> valuelist, String str) {
        List<Node> reslist = new ArrayList<Node>();
        for (int i = 0; i < valuelist.size(); i++) {
            Node value = valuelist.get(i);
            if (value.level != fields.length) {
                continue;
            }
            String substr = str.substring(value.keyStart, value.keyEnd + 1);
            if (!fields[value.level - 1].equals(substr)) {
                continue;
            }
            int level = value.level - 1;
            for (int k = i - 1; k >= 0 && level > 0; k--) {
                Node value2 = valuelist.get(k);
                if (value2.level == level) {
                    String substr2 = str.substring(value2.keyStart, value2.keyEnd + 1);
                    if (!fields[level - 1].equals(substr2)) {
                        break;
                    }
                    level--;
                }
            }
            if (level == 0) {
                reslist.add(value);
            }
        }
        return reslist;
    }
}
