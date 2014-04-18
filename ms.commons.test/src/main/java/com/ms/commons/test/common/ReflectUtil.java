/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.mutable.Mutable;

import com.ms.commons.test.common.comparator.CompareUtil;
import com.ms.commons.test.common.convert.TypeConvertUtil;
import com.ms.commons.test.exception.JavaFieldNotFoundException;
import com.ms.commons.test.exception.UnknowException;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryFieldType;
import com.ms.commons.test.memorydb.MemoryRow;

/**
 * @author zxc Apr 13, 2013 11:18:06 PM
 */
public class ReflectUtil {

    public static void setObject(Object bean, String field, Object value) {
        Class<?> clazz = bean.getClass();
        try {
            Field f = getDeclaredField(clazz, field);
            f.setAccessible(true);
            try {
                f.set(bean, value);
            } catch (IllegalArgumentException e) {
                throw new UnknowException(e);
            } catch (IllegalAccessException e) {
                throw new UnknowException(e);
            }
        } catch (SecurityException e) {
            throw new UnknowException(e);
        } catch (NoSuchFieldException e) {
            throw new JavaFieldNotFoundException(clazz, field);
        }
    }

    public static void setValueToBean(Object bean, String field, Object value) {
        Class<?> clazz = bean.getClass();
        try {
            Field f = getDeclaredField(clazz, NamingUtil.dbNameToJavaName(field));
            f.setAccessible(true);
            try {
                f.set(bean, TypeConvertUtil.convert(f.getType(), value));
            } catch (IllegalArgumentException e) {
                throw new UnknowException(e);
            } catch (IllegalAccessException e) {
                throw new UnknowException(e);
            }
        } catch (SecurityException e) {
            throw new UnknowException(e);
        } catch (NoSuchFieldException e) {
            throw new JavaFieldNotFoundException(clazz, field);
        }
    }

    public static Object getObject(Object bean, String field) {
        Class<?> clazz = bean.getClass();
        try {
            Field f = getDeclaredField(clazz, field);
            f.setAccessible(true);
            try {
                return f.get(bean);
            } catch (IllegalArgumentException e) {
                throw new UnknowException(e);
            } catch (IllegalAccessException e) {
                throw new UnknowException(e);
            }
        } catch (SecurityException e) {
            throw new UnknowException(e);
        } catch (NoSuchFieldException e) {
            throw new JavaFieldNotFoundException(clazz, field);
        }
    }

    public static Object getValueFromBean(Object bean, String field) {
        Class<?> clazz = bean.getClass();
        try {
            Field f = getDeclaredField(clazz, NamingUtil.dbNameToJavaName(field));
            f.setAccessible(true);
            try {
                return f.get(bean);
            } catch (IllegalArgumentException e) {
                throw new UnknowException(e);
            } catch (IllegalAccessException e) {
                throw new UnknowException(e);
            }
        } catch (SecurityException e) {
            throw new UnknowException(e);
        } catch (NoSuchFieldException e) {
            throw new JavaFieldNotFoundException(clazz, field);
        }
    }

    public static boolean isValueEqualsBean(Object bean, String field, Object value) {
        Class<?> clazz = bean.getClass();
        try {
            Field f = getDeclaredField(clazz, NamingUtil.dbNameToJavaName(field));
            f.setAccessible(true);
            try {
                Object beanValue = f.get(bean);
                Object fieldValue = TypeConvertUtil.convert(f.getType(), value);
                return CompareUtil.isObjectEquals(beanValue, fieldValue);
            } catch (IllegalArgumentException e) {
                throw new UnknowException(e);
            } catch (IllegalAccessException e) {
                throw new UnknowException(e);
            }
        } catch (SecurityException e) {
            throw new UnknowException(e);
        } catch (NoSuchFieldException e) {
            throw new JavaFieldNotFoundException(clazz, field);
        }
    }

    public static Object getValueAccroudBean(Object bean, String field, Object value) {
        Class<?> clazz = bean.getClass();
        try {
            Field f = getDeclaredField(clazz, NamingUtil.dbNameToJavaName(field));
            f.setAccessible(true);
            try {
                return TypeConvertUtil.convert(f.getType(), value);
            } catch (IllegalArgumentException e) {
                throw new UnknowException(e);
            }
        } catch (SecurityException e) {
            throw new UnknowException(e);
        } catch (NoSuchFieldException e) {
            throw new JavaFieldNotFoundException(clazz, field);
        }
    }

    public static void setRowToBean(Object bean, MemoryRow row) {
        for (MemoryField field : row.getFieldList()) {
            Object value = (field.getType() == MemoryFieldType.Null) ? null : field.getValue();
            setValueToBean(bean, field.getName(), value);
        }
    }

    public static boolean isRowEqualsToBean(Object bean, MemoryRow row) {
        for (MemoryField field : row.getFieldList()) {
            Object value = (field.getType() == MemoryFieldType.Null) ? null : field.getValue();
            if (!isValueEqualsBean(bean, field.getName(), value)) {
                return false;
            }
        }
        return true;
    }

    public static Object invokeMethod(Class<?> clazz, Object object, String methodName, Class<?>[] parameterTypes,
                                      Object[] parameters) {
        Method method = getDeclaredMethod(clazz, methodName, parameterTypes);
        if (method == null) {
            throw new RuntimeException("Method `" + methodName + "` not found in class `" + clazz.getName() + "`.");
        }
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(object, parameters);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getDeclaredMethod(Class<?> clazz, String methodName) {
        try {
            Method foundMethod = null;
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                if (methodName.equals(m.getName())) {
                    if (foundMethod != null) {
                        throw new RuntimeException("Found two method named: " + methodName + " in class: " + clazz);
                    }
                    foundMethod = m;
                }
            }
            if (foundMethod == null) {
                throw new NoSuchMethodException("Cannot find method named: " + methodName + " in class: " + clazz);
            }
            return foundMethod;
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            if (clazz == Object.class) {
                return null;
            } else {
                return getDeclaredMethod(clazz.getSuperclass(), methodName);
            }
        }
    }

    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            return method;
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            if (clazz == Object.class) {
                return null;
            } else {
                return getDeclaredMethod(clazz.getSuperclass(), methodName, parameterTypes);
            }
        }
    }

    public static Field[] getStaticFields(Field[] fields) {
        if (fields == null) {
            return null;
        }
        List<Field> staticFields = new ArrayList<Field>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                staticFields.add(field);
            }
        }
        return staticFields.toArray(new Field[0]);
    }

    public static Field[] getDeclaredFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        getDeclaredFields(clazz, fieldList);
        return fieldList.toArray(new Field[] {});
    }

    public static Field getDeclaredField(Class<?> clazz, String field) throws SecurityException, NoSuchFieldException {
        if (clazz == Object.class) {
            throw new NoSuchFieldException("Field `" + field + "` in class `" + clazz.getName() + "` cannot be found.");
        }
        try {
            return clazz.getDeclaredField(field);
        } catch (SecurityException e) {
            throw e;
        } catch (NoSuchFieldException e) {
            return getDeclaredField(clazz.getSuperclass(), field);
        }
    }

    protected static void getDeclaredFields(Class<?> clazz, List<Field> fieldList) {
        if (clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                fieldList.add(field);
            }
        }
        getDeclaredFields(clazz.getSuperclass());
    }

    public static final Class<?>[] getInterfaces(Class<?> clazz) {
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        getInterfaces(clazz, clazzList);
        return (new LinkedHashSet<Class<?>>(clazzList)).toArray(new Class<?>[] {});
    }

    protected static final void getInterfaces(Class<?> clazz, List<Class<?>> clazzList) {
        if (clazz == Object.class) {
            return;
        }
        for (Class<?> c : clazz.getInterfaces()) {
            clazzList.add(c);
        }
        getInterfaces(clazz.getSuperclass(), clazzList);
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public static Object invokeMethodByMemoryRow(Object object, Method method, MemoryRow memoryRow,
                                                 Mutable outParameters) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        method.getTypeParameters();
        Object[] parameterObjects = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            MemoryField field = memoryRow.getField(i + 1);
            Object value = (field.getType() == MemoryFieldType.Null) ? null : field.getValue();
            parameterObjects[i] = TypeConvertUtil.convert(clazz, value);
        }
        if (outParameters != null) {
            outParameters.setValue(Arrays.asList(parameterObjects));
        }
        try {
            method.setAccessible(true);
            return method.invoke(object, parameterObjects);
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }
}
