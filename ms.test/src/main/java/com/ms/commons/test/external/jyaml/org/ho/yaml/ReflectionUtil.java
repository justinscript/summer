/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;

/**
 * @author zxc Apr 14, 2013 12:36:42 AM
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReflectionUtil {

    /**
     * @param bean
     * @param key
     * @return
     */
    public static PropertyDescriptor getPropertyDescriptor(Class clazz, String key) {
        try {
            for (PropertyDescriptor prop : Introspector.getBeanInfo(clazz).getPropertyDescriptors())
                if (key.equals(prop.getName())) return prop;
        } catch (IntrospectionException e) {
        }
        return null;
    }

    public static boolean hasProperty(Class clazz, String prop) {
        return null != getPropertyDescriptor(clazz, prop);
    }

    public static boolean hasProperty(Object obj, String prop) {
        return null != getPropertyDescriptor(obj.getClass(), prop);
    }

    /**
     * @param obj
     * @param key
     * @return
     */
    public static PropertyDescriptor getPropertyDescriptor(Object obj, String key) {
        return getPropertyDescriptor(obj.getClass(), key);
    }

    /**
     * @param bean
     * @return
     */
    public static List<PropertyDescriptor> getProperties(Object bean) {
        return getProperties(bean.getClass());
    }

    public static List<PropertyDescriptor> getProperties(Class clazz) {
        try {
            return filterProps(Introspector.getBeanInfo(clazz).getPropertyDescriptors());
        } catch (IntrospectionException e) {
            throw new YamlException(e);
        }
    }

    public static List<PropertyDescriptor> getPropertiesExcluding(List<String> exclude, Object bean) {
        try {
            return filterProps(Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors(), exclude);
        } catch (IntrospectionException e) {
            throw new YamlException(e);
        }
    }

    public static List<Field> getFields(Object bean) {
        ArrayList<Field> ret = new ArrayList<Field>();
        Field[] fields = bean.getClass().getFields();
        for (Field field : fields)
            ret.add(field);
        return ret;
    }

    public static List<Field> getFieldsExcluding(List<String> exclude, Object bean) {
        ArrayList<Field> ret = new ArrayList<Field>();
        Field[] fields = bean.getClass().getFields();
        for (Field field : fields)
            for (String toExclude : exclude)
                if (!toExclude.equals(field.getName())) ret.add(field);
        return ret;
    }

    public static boolean isMemberField(Field field) {
        return 0 == (Modifier.STATIC & field.getModifiers());
    }

    public static boolean isAbstract(Class clazz) {
        return 0 == (~Modifier.ABSTRACT & Modifier.ABSTRACT & clazz.getModifiers());
    }

    /**
     * @param props
     * @return
     */
    static List<PropertyDescriptor> filterProps(PropertyDescriptor[] props) {
        return filterProps(props, null);
    }

    /**
     * This method will filter properties down to the ones that have both read and write methods.
     * 
     * @param props
     * @return
     */
    static List<PropertyDescriptor> filterProps(PropertyDescriptor[] props, List<String> exclude) {
        ArrayList<PropertyDescriptor> ret = new ArrayList<PropertyDescriptor>();
        for (PropertyDescriptor prop : props)
            if (exclude != null) {
                if (!"class".equals(prop.getName())) for (String toExclude : exclude)
                    if (!toExclude.equals(prop.getName())) ret.add(prop);
            } else if (prop.getReadMethod() != null && prop.getWriteMethod() != null) ret.add(prop);
        return ret;
    }

    /**
     * @param bean
     * @param key
     * @param value
     */
    public static void setProperty(Object bean, String key, Object value) throws IllegalAccessException,
                                                                         InvocationTargetException {
        PropertyDescriptor prop = getPropertyDescriptor(bean, key);
        prop.getWriteMethod().invoke(bean, new Object[] { value });

    }

    /**
     * @param clazz
     * @param fieldname
     * @return
     */
    public static Class getFieldType(Class clazz, String fieldname) {
        try {
            Field field = clazz.getField(fieldname);
            return field.getType();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param obj
     * @param fieldname
     * @return
     */
    public static Object readField(Object obj, String fieldname) {
        try {
            Field field = obj.getClass().getField(fieldname);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param obj
     * @param fieldname
     * @param value
     */
    public static void setField(Object obj, String fieldname, Object value) {
        try {
            Field field = obj.getClass().getField(fieldname);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * @param clazz
     * @param argTypes
     * @param args
     * @return
     */
    public static Object invokeConstructor(Class clazz, Class[] argTypes, Object[] args) {
        Constructor constructor = ReflectionUtil.getConstructor(clazz, argTypes);
        try {
            Object newObject = constructor.newInstance(args);
            return newObject;
        } catch (Exception e) {
            throw new YamlException("Can't invoke constructor for " + clazz + " with arguments "
                                    + Arrays.asList(argTypes) + " with values " + Arrays.asList(args));
        }
    }

    /**
     * @param clazz
     * @param argTypes
     * @return
     */
    public static Constructor getConstructor(Class clazz, Class[] argTypes) {
        try {
            return clazz.getConstructor(argTypes);
        } catch (Exception e) {
            throw new YamlException("Can't find constructor for " + clazz + " with arguments "
                                    + Arrays.asList(argTypes) + "\n" + e);
        }

    }

    /**
     * @param name
     * @return
     */
    public static boolean isPrimitiveType(String name) {
        return Integer.TYPE.getName().equals(name) || Double.TYPE.getName().equals(name)
               || Float.TYPE.getName().equals(name) || Boolean.TYPE.getName().equals(name)
               || Character.TYPE.getName().equals(name) || Byte.TYPE.getName().equals(name)
               || Long.TYPE.getName().equals(name) || Short.TYPE.getName().equals(name)
               || Character.TYPE.getName().equals(name);
    }

    /**
     * @param name
     * @return
     */
    public static Class getPrimitiveType(String name) {
        if (Integer.TYPE.getName().equals(name)) return Integer.TYPE;
        else if (Double.TYPE.getName().equals(name)) return Double.TYPE;
        else if (Float.TYPE.getName().equals(name)) return Float.TYPE;
        else if (Boolean.TYPE.getName().equals(name)) return Boolean.TYPE;
        else if (Character.TYPE.getName().equals(name)) return Character.TYPE;
        else if (Byte.TYPE.getName().equals(name)) return Byte.TYPE;
        else if (Long.TYPE.getName().equals(name)) return Long.TYPE;
        else if (Short.TYPE.getName().equals(name)) return Short.TYPE;
        else if (Character.TYPE.getName().equals(name)) return Character.TYPE;
        else throw new YamlException(name + " is not a primitive type.");
    }

    public static boolean isArrayName(String classname) {
        return classname != null && classname.indexOf("[]") != -1;
    }

    public static String arrayComponentName(String classname) {
        return classname.substring(0, classname.length() - 2);
    }

    public static Class getArrayType(String arrayname) {
        if (isArrayName(arrayname)) return getArrayTypeHelper(arrayname);
        else return null;
    }

    /**
     * assumes arrayType is an array type in the top level invocation
     * 
     * @param arrayType
     * @return
     */
    public static String arrayName(Class arrayType, YamlConfig config) {
        if (arrayType.isArray()) return arrayName(arrayType.getComponentType(), config) + "[]";
        else if (config != null) return config.classname2transfer(arrayType.getName());
        else return arrayType.getName();
    }

    public static Class classForName(String classname) {
        if (isPrimitiveType(classname)) return getPrimitiveType(classname);
        else if (isArrayName(classname)) return getArrayType(classname);
        else try {
            return Class.forName(classname);
        } catch (Exception e) {
            return null;
        }
    }

    public static String transfer2classname(String transfer, YamlConfig config) {
        if (isArrayName(transfer)) {
            return transfer2classname(arrayComponentName(transfer), config) + "[]";
        } else return config.transfer2classname(transfer);
    }

    static Class getArrayTypeHelper(String classname) {
        if (!isArrayName(classname)) return classForName(classname);
        else return Array.newInstance(getArrayTypeHelper(arrayComponentName(classname)), 0).getClass();
    }

    public static String className(Class clazz) {
        return className(clazz, null);
    }

    public static String className(Class clazz, YamlConfig config) {
        if (clazz.isArray()) return arrayName(clazz, config);
        else if (config != null) return config.classname2transfer(clazz.getName());
        else return clazz.getName();
    }

    public static boolean isSimpleType(Class c) {
        return c.isPrimitive() || c == String.class || c == Integer.class || c == Long.class || c == Short.class
               || c == Double.class || c == Float.class || c == Boolean.class || c == Character.class;
        // ||
        // c == BigInteger.class || c == BigDecimal.class ;
        // ||
        // c == Date.class || c == File.class ||
        // c.isEnum();
    }

    public static boolean isCastableFrom(Class from, Class to) {
        if (to.isAssignableFrom(from)) return true;
        else if (to.isPrimitive()) {
            try {
                if (from.getField("TYPE").get(from) == to) return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static boolean isCastableFrom(Class from, String to) {
        return isCastableFrom(from, ReflectionUtil.classForName(to));
    }
}
