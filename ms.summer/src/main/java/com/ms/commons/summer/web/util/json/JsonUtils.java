/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.util.json;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import net.sf.ezmorph.MorphUtils;
import net.sf.ezmorph.MorpherRegistry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.JSONUtils;
import net.sf.json.util.PropertyFilter;

import com.ms.commons.summer.core.util.HtmlUtil;

/**
 * @author zxc Apr 12, 2013 4:24:18 PM
 */
public final class JsonUtils {

    static {
        MorpherRegistry morpherRegistry = JSONUtils.getMorpherRegistry();
        morpherRegistry.registerMorpher(new JsonNumberMorpher(Byte.class, new Byte((byte) 0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(Short.class, new Short((short) 0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(Integer.class, new Integer(0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(Long.class, new Long(0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(Float.class, new Float(0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(Double.class, new Double(0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(BigInteger.class, BigInteger.ZERO), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(BigDecimal.class, MorphUtils.BIGDECIMAL_ZERO), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(byte.class, new Byte((byte) 0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(short.class, new Short((short) 0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(int.class, new Integer(0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(long.class, new Long(0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(float.class, new Float(0)), true);
        morpherRegistry.registerMorpher(new JsonNumberMorpher(double.class, new Double(0)), true);
    }

    public static String object2Json(Object dataObject) throws Exception {
        StringWriter sw = new StringWriter();
        object2Json(dataObject, sw);
        return sw.toString();
    }

    public static String object2Json(Object dataObject, boolean escape) throws Exception {
        StringWriter sw = new StringWriter();
        object2Json(dataObject, sw, escape);
        return sw.toString();
    }

    /**
     * 写出Json数据
     * 
     * @param dataObject
     * @param writer
     * @throws Exception
     */
    public static void object2Json(Object dataObject, Writer writer) throws Exception {
        object2Json(dataObject, writer, true);
    }

    /**
     * 写出Json数据
     * 
     * @param dataObject
     * @param writer
     * @throws Exception
     */
    public static void object2Json(Object dataObject, Writer writer, boolean escape) throws Exception {
        if (dataObject == null) {
            return;
        }

        // 增加对数据安全的过滤
        JsonConfig jsonConfig = new JsonConfig();
        if (escape) {
            jsonConfig.registerJsonValueProcessor(String.class, new JsonValueProcessor() {

                public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
                    return HtmlUtil.escapeHtml((String) value);
                }

                public Object processArrayValue(Object value, JsonConfig jsonConfig) {
                    return HtmlUtil.escapeHtml((String) value);
                }
            });
        }

        // 忽略空null的属性
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {

            public boolean apply(Object source, String name, Object value) {
                return value == null;
            }
        });

        // 增加对日期的格式化处理
        jsonConfig.registerJsonValueProcessor(Date.class,
                                              new DateJsonValueProcessor(DateJsonValueProcessor.DEFAULT_DATE_PATTERN));

        // 忽略transient类型的属性
        jsonConfig.setIgnoreTransientFields(true);

        // 临时将循环引用的对象返回为null
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

        // 输出json数据流
        if (dataObject instanceof Collection<?> || dataObject.getClass().isArray()) {
            JSONArray.fromObject(dataObject, jsonConfig).write(writer);
        } else {
            JSONObject.fromObject(dataObject, jsonConfig).write(writer);
        }
    }

    public static class DateJsonValueProcessor implements JsonValueProcessor {

        public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
        private DateFormat         dateFormat;

        /**
         * 构造方法.
         * 
         * @param datePattern 日期格式
         */
        public DateJsonValueProcessor(String datePattern) {
            try {
                dateFormat = new SimpleDateFormat(datePattern);
            } catch (Exception ex) {
                dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
            }
        }

        public Object processArrayValue(Object value, JsonConfig jsonConfig) {
            return process(value);
        }

        public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
            return process(value);
        }

        private Object process(Object value) {
            if (value == null) {
                return "";
            }
            return dateFormat.format((Date) value);
        }
    }
}
