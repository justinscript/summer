/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.mvc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.ms.commons.summer.web.handler.DataBinderUtil;
import com.ms.commons.summer.web.servlet.result.Forward;
import com.ms.commons.summer.web.servlet.result.Redirect;
import com.ms.commons.summer.web.servlet.result.View;

/**
 * @author zxc Apr 12, 2013 4:15:06 PM
 */
public abstract class AbstractController extends ComponentMethodController {

    // public boolean isAjax() {
    // return InvokeTypeTools.isAjax(request);
    // }

    public Redirect toRedirect(String url) {
        return new Redirect(url);
    }

    public Forward toForward(String url) {
        return new Forward(url);
    }

    public View toView(String url) {
        return new View(url);
    }

    public Object[] createObjectArray(HttpServletRequest request, String name, Class<?> clazz) throws Exception {
        return DataBinderUtil.createObjectArray(request, name, clazz);
    }

    /**
     * copy对象，把source中的属性值copy到制定的类创建的对象中<br>
     * 原则，属性名必须相同，且类型必须相同，并提供对应的get、set方法
     * 
     * @param source
     * @param clazz
     * @return
     */
    public static Object copyBean(Object source, Class<?> clazz) {
        if (source == null) {
            return null;
        }
        try {
            Object obj = clazz.newInstance();
            Class<?> sourceClazz = source.getClass();
            Field[] targetFields = clazz.getDeclaredFields();
            Field[] sourceFields = sourceClazz.getDeclaredFields();
            for (Field field : targetFields) {
                String name = field.getName();
                for (Field sfield : sourceFields) {
                    if (name.equals(sfield.getName()) && field.getType() == sfield.getType()) {
                        try {
                            Method sm = sourceClazz.getMethod(getMethodName(name, true));
                            if (sm == null) {
                                break;
                            }
                            Object vObject = sm.invoke(source);
                            if (vObject == null) {
                                break;
                            }
                            Method tm = clazz.getMethod(getMethodName(name, false), vObject.getClass());
                            if (tm == null) {
                                break;
                            }
                            tm.invoke(obj, vObject);
                        } catch (Exception e) {
                        }
                        break;
                    }
                }
            }
            return obj;
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        return null;
    }

    private static String getMethodName(String fieldName, boolean isGet) {
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return isGet ? "get" + fieldName : "set" + fieldName;
    }
}
