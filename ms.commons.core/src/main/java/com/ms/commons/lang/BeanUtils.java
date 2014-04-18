/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.valueedit.ValueEditable;

/**
 * @author zxc Apr 12, 2013 2:34:08 PM
 */
public class BeanUtils {

    private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    public static <T extends Object> List<T> convert(Class<T> clazz, Collection<?> raw,
                                                     ValueEditable... specialConverts) {
        if (Argument.isEmpty(raw)) {
            return Collections.emptyList();
        }
        List<T> data = new ArrayList<T>(raw.size());
        for (Object obj : raw) {
            T vo;
            try {
                vo = clazz.newInstance();
                copyProperties(vo, obj, specialConverts);
                data.add(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static <T extends Object> void copyProperties(T target, Object raw,
                                                         Collection<? extends ValueEditable> convert) {
        copyProperties(target, raw, convert.toArray(new ValueEditable[0]));
    }

    @SuppressWarnings("rawtypes")
    public static <T extends Object> void copyProperties(Collection<T> target, Collection beans, String key,
                                                         Collection<? extends ValueEditable> defaultValues) {
        Map<String, List<T>> result = CollectionUtils.toListMap(target, key);
        for (Object bean : beans) {
            try {
                Object keyProperty = PropertyUtils.getProperty(bean, key);
                List<T> keywords = result.get(keyProperty);
                if (keywords == null) {
                    continue;
                }
                //
                for (T keyword : keywords) {
                    copyProperties(keyword, bean, defaultValues);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
    public static <T extends Object> void copyProperties(T target, Object raw, ValueEditable... defaultValues) {

        try {
            Map values = raw == null ? new HashMap() : PropertyUtils.describe(raw);
            if (Argument.isNotEmptyArray(defaultValues)) {
                for (ValueEditable edit : defaultValues) {
                    edit.edit(raw, values);
                }
            }

            PropertyUtils.copyProperties(target, values);
            // 特殊处理
            // TODO
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
