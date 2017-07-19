/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.classloader.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.springframework.ui.velocity.VelocityEngineFactory;

/**
 * @author zxc Apr 13, 2013 11:07:47 PM
 */
public class VelocityTemplateUtil {

    static VelocityEngine VELOCITY_ENGINE;
    static {
        try {
            VELOCITY_ENGINE = new VelocityEngineFactory().createVelocityEngine();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (VelocityException e) {
            e.printStackTrace();
        }
    }

    public static interface KeyConverter {

        Object convert(Object key, boolean isToPro);
    }

    public static String mergeContent(final Map<Object, Object> context, String text) {
        StringReader sr = new StringReader(text);
        StringWriter sw = new StringWriter();
        Context c = new Context() {

            public boolean containsKey(Object key) {
                return context.containsKey(key);
            }

            public Object get(String key) {
                return context.get(key);
            }

            public Object[] getKeys() {
                return context.keySet().toArray();
            }

            public Object put(String key, Object value) {
                return context.put(key, value);
            }

            public Object remove(Object key) {
                return context.remove(key);
            }
        };

        try {
            VELOCITY_ENGINE.evaluate(c, sw, VelocityTemplateUtil.class.getSimpleName(), sr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    public static String mergeContent(final Map<Object, Object> context, String text, final KeyConverter converter) {
        StringReader sr = new StringReader(text);
        StringWriter sw = new StringWriter();

        Context c = new Context() {

            public boolean containsKey(Object key) {
                return context.containsKey(convertKey(key, true));
            }

            public Object get(String key) {
                return context.get(convertKey(key, true));
            }

            public Object[] getKeys() {
                Object[] keys = context.keySet().toArray();
                Object[] cKeys = new Object[keys.length];
                for (int i = 0; i < keys.length; i++) {
                    cKeys[i] = convertKey(keys[i], false);
                }
                return cKeys;
            }

            public Object put(String key, Object value) {
                return null;
            }

            public Object remove(Object key) {
                return null;
            }

            protected Object convertKey(Object key, boolean isToPro) {
                if (converter == null) {
                    return key;
                } else {
                    return converter.convert(key, isToPro);
                }
            }
        };

        try {
            VELOCITY_ENGINE.evaluate(c, sw, VelocityTemplateUtil.class.getSimpleName(), sr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    public static String simpleMerge(final Map<Object, Object> context, String text) {
        return evalString(context, text, new MutableInt(0));
    }

    protected static String evalString(Map<Object, Object> context, String str, MutableInt depth) {
        if (depth.intValue() > 5) {
            System.err.println("eval depth > 5 for `" + StringUtils.substring(str, 0, 50) + "`.");
            return str;
        }

        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();

        boolean in = false;
        StringBuilder tsb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            char nc = 0;
            if ((i + 1) < chars.length) {
                nc = chars[i + 1];
            }

            if (in) {
                if (c != '}') {
                    tsb.append(c);
                } else {
                    Object value = context.get(tsb.toString());
                    if (value == null) {
                        sb.append("${" + tsb.toString() + "}");
                    } else {
                        sb.append(value);
                    }
                    tsb = new StringBuilder();
                    in = false;
                }
            } else {
                if ((c == '$') && (nc == '{')) {
                    in = true;
                    i++;
                } else {
                    sb.append(c);
                }
            }
        }
        if (in) {
            throw new RuntimeException("at last is in");
        }

        String result = sb.toString();

        if (result.contains("${")) {
            depth.add(1);
            return evalString(context, result, depth);
        } else {
            return result;
        }
    }
}
