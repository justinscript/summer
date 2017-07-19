/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ms.commons.fasttext.json.JSONObject.EncodeType;

/**
 * @author zxc Apr 12, 2013 3:37:14 PM
 */
public class JSONArray<T> {

    private List<T> jsonList = new ArrayList<T>();

    public String toString() {
        return toString(EncodeType.RAW);
    }

    /**
     * @param encodeType 编码类型
     * @return
     */
    public String toString(JSONObject.EncodeType encodeType) {
        Iterator<T> iter = jsonList.iterator();
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        buffer.append('[');
        while (iter.hasNext()) {
            T value = iter.next();
            if (i++ > 0) {
                buffer.append(JSONObject.COMMA);
            }
            if (value instanceof String) {
                buffer.append(JSONObject.QUOTA);
                buffer.append(JSONObject.escape((String) value, encodeType));
                buffer.append(JSONObject.QUOTA);
            } else {
                ((JSONObject) value).build(encodeType, buffer);
            }
        }
        buffer.append(']');
        return buffer.toString();
    }

    /**
     * 加入元素，只能是JSONObject或者String类型
     * 
     * @param value
     * @return
     */
    public JSONArray<T> add(T value) {
        if (value instanceof JSONObject || value instanceof String) {
            this.jsonList.add(value);
        } else {
            throw new RuntimeException("class type must to be JSONObject or String");
        }
        return this;
    }

}
