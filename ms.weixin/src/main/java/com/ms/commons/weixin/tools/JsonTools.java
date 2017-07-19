package com.ms.commons.weixin.tools;

import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

public final class JsonTools {

    public static String object2Json(Object dataObject) {
        StringWriter sw = new StringWriter();
        object2Json(dataObject, sw);
        return sw.toString();
    }

    /**
     * 写出Json数据
     * 
     * @param dataObject
     * @param writer
     * @throws Exception
     */
    public static void object2Json(Object dataObject, Writer writer) {
        if (dataObject == null) {
            return;
        }

        // 增加对数据安全的过滤
        JsonConfig jsonConfig = new JsonConfig();

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
