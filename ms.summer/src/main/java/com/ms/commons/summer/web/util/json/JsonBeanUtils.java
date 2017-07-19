/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.util.json;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ezmorph.Morpher;
import net.sf.ezmorph.array.ObjectArrayMorpher;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.ezmorph.object.IdentityObjectMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.regexp.RegexpUtils;
import net.sf.json.util.EnumMorpher;
import net.sf.json.util.JSONUtils;
import net.sf.json.util.PropertyFilter;
import net.sf.json.util.PropertySetStrategy;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zxc Apr 12, 2013 4:25:31 PM
 */
@SuppressWarnings("all")
final class JsonBeanUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonBeanUtils.class);

    /**
     * Creates a JSONDynaBean from a JSONObject.
     */
    public static Object toBean(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.isNullObject()) {
            return null;
        }

        DynaBean dynaBean = null;

        Map props = JSONUtils.getProperties(jsonObject);
        dynaBean = JSONUtils.newDynaBean(jsonObject, new JsonConfig());
        for (Iterator entries = jsonObject.names().iterator(); entries.hasNext();) {
            String name = (String) entries.next();
            String key = JSONUtils.convertToJavaIdentifier(name, new JsonConfig());
            Class type = (Class) props.get(name);
            Object value = jsonObject.get(name);
            try {
                if (!JSONUtils.isNull(value)) {
                    if (value instanceof JSONArray) {
                        dynaBean.set(key, JSONArray.toCollection((JSONArray) value));
                    } else if (String.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)
                               || JSONUtils.isNumber(type) || Character.class.isAssignableFrom(type)
                               || JSONFunction.class.isAssignableFrom(type)) {
                        dynaBean.set(key, value);
                    } else {
                        dynaBean.set(key, toBean((JSONObject) value));
                    }
                } else {
                    if (type.isPrimitive()) {
                        // assume assigned default value
                        log.warn("Tried to assign null value to " + key + ":" + type.getName());
                        dynaBean.set(key, JSONUtils.getMorpherRegistry().morph(type, null));
                    } else {
                        dynaBean.set(key, null);
                    }
                }
            } catch (JSONException jsone) {
                throw jsone;
            } catch (Exception e) {
                throw new JSONException("Error while setting property=" + name + " type" + type, e);
            }
        }

        return dynaBean;
    }

    /**
     * Creates a bean from a JSONObject, with a specific target class.<br>
     */
    public static Object toBean(JSONObject jsonObject, Class beanClass) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setRootClass(beanClass);
        return toBean(jsonObject, jsonConfig);
    }

    /**
     * Creates a bean from a JSONObject, with a specific target class.<br>
     * If beanClass is null, this method will return a graph of DynaBeans. Any attribute that is a JSONObject and
     * matches a key in the classMap will be converted to that target class.<br>
     * The classMap has the following conventions:
     * <ul>
     * <li>Every key must be an String.</li>
     * <li>Every value must be a Class.</li>
     * <li>A key may be a regular expression.</li>
     * </ul>
     */
    public static Object toBean(JSONObject jsonObject, Class beanClass, Map classMap) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setRootClass(beanClass);
        jsonConfig.setClassMap(classMap);
        return toBean(jsonObject, jsonConfig);
    }

    /**
     * Creates a bean from a JSONObject, with the specific configuration.
     */
    public static Object toBean(JSONObject jsonObject, JsonConfig jsonConfig) {
        if (jsonObject == null || jsonObject.isNullObject()) {
            return null;
        }

        Class beanClass = jsonConfig.getRootClass();
        Map classMap = jsonConfig.getClassMap();

        if (beanClass == null) {
            return toBean(jsonObject);
        }
        if (classMap == null) {
            classMap = Collections.EMPTY_MAP;
        }

        Object bean = null;
        try {
            if (beanClass.isInterface()) {
                if (!Map.class.isAssignableFrom(beanClass)) {
                    throw new JSONException("beanClass is an interface. " + beanClass);
                } else {
                    bean = new HashMap();
                }
            } else {
                bean = jsonConfig.getNewBeanInstanceStrategy().newInstance(beanClass, jsonObject);
            }
        } catch (JSONException jsone) {
            throw jsone;
        } catch (Exception e) {
            throw new JSONException(e);
        }

        Map props = JSONUtils.getProperties(jsonObject);
        PropertyFilter javaPropertyFilter = jsonConfig.getJavaPropertyFilter();
        for (Iterator entries = jsonObject.names().iterator(); entries.hasNext();) {
            String name = (String) entries.next();
            Class type = (Class) props.get(name);
            Object value = jsonObject.get(name);
            if (javaPropertyFilter != null && javaPropertyFilter.apply(bean, name, value)) {
                continue;
            }
            String key = Map.class.isAssignableFrom(beanClass)
                         && jsonConfig.isSkipJavaIdentifierTransformationInMapKeys() ? name : JSONUtils.convertToJavaIdentifier(name,
                                                                                                                                jsonConfig);
            try {
                if (Map.class.isAssignableFrom(beanClass)) {
                    // no type info available for conversion
                    if (JSONUtils.isNull(value)) {
                        setProperty(bean, key, value, jsonConfig);
                    } else if (value instanceof JSONArray) {
                        setProperty(bean,
                                    key,
                                    convertPropertyValueToCollection(key, value, jsonConfig, name, classMap, List.class),
                                    jsonConfig);
                    } else if (String.class.isAssignableFrom(type) || JSONUtils.isBoolean(type)
                               || JSONUtils.isNumber(type) || JSONUtils.isString(type)
                               || JSONFunction.class.isAssignableFrom(type)) {
                        if (jsonConfig.isHandleJettisonEmptyElement() && "".equals(value)) {
                            setProperty(bean, key, null, jsonConfig);
                        } else {
                            setProperty(bean, key, value, jsonConfig);
                        }
                    } else {
                        Class targetClass = findTargetClass(key, classMap);
                        targetClass = targetClass == null ? findTargetClass(name, classMap) : targetClass;
                        JsonConfig jsc = jsonConfig.copy();
                        jsc.setRootClass(targetClass);
                        jsc.setClassMap(classMap);
                        if (targetClass != null) {
                            setProperty(bean, key, toBean((JSONObject) value, jsc), jsonConfig);
                        } else {
                            setProperty(bean, key, toBean((JSONObject) value), jsonConfig);
                        }
                    }
                } else {
                    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, key);
                    if (pd != null && pd.getWriteMethod() == null) {
                        log.warn("Property '" + key + "' has no write method. SKIPPED.");
                        continue;
                    }

                    if (pd != null) {
                        Class targetType = pd.getPropertyType();
                        if (!JSONUtils.isNull(value)) {
                            if (value instanceof JSONArray) {
                                if (List.class.isAssignableFrom(pd.getPropertyType())) {
                                    setProperty(bean,
                                                key,
                                                convertPropertyValueToCollection(key, value, jsonConfig, name,
                                                                                 classMap, pd.getPropertyType()),
                                                jsonConfig);
                                } else if (Set.class.isAssignableFrom(pd.getPropertyType())) {
                                    setProperty(bean,
                                                key,
                                                convertPropertyValueToCollection(key, value, jsonConfig, name,
                                                                                 classMap, pd.getPropertyType()),
                                                jsonConfig);
                                } else {
                                    setProperty(bean,
                                                key,
                                                convertPropertyValueToArray(key, value, targetType, jsonConfig,
                                                                            classMap), jsonConfig);
                                }
                            } else if (String.class.isAssignableFrom(type) || JSONUtils.isBoolean(type)
                                       || JSONUtils.isNumber(type) || JSONUtils.isString(type)
                                       || JSONFunction.class.isAssignableFrom(type)) {
                                if (pd != null) {
                                    if (jsonConfig.isHandleJettisonEmptyElement() && "".equals(value)) {
                                        setProperty(bean, key, null, jsonConfig);
                                    } else if (!targetType.isInstance(value)) {
                                        setProperty(bean, key, morphPropertyValue(key, value, type, targetType),
                                                    jsonConfig);
                                    } else {
                                        setProperty(bean, key, value, jsonConfig);
                                    }
                                } else if (beanClass == null || bean instanceof Map) {
                                    setProperty(bean, key, value, jsonConfig);
                                } else {
                                    log.warn("Tried to assign property " + key + ":" + type.getName()
                                             + " to bean of class " + bean.getClass().getName());
                                }
                            } else {
                                if (jsonConfig.isHandleJettisonSingleElementArray()) {
                                    JSONArray array = new JSONArray().element(value, jsonConfig);
                                    Class newTargetClass = findTargetClass(key, classMap);
                                    newTargetClass = newTargetClass == null ? findTargetClass(name, classMap) : newTargetClass;
                                    JsonConfig jsc = jsonConfig.copy();
                                    jsc.setRootClass(newTargetClass);
                                    jsc.setClassMap(classMap);
                                    if (targetType.isArray()) {
                                        setProperty(bean, key, JSONArray.toArray(array, jsc), jsonConfig);
                                    } else if (JSONArray.class.isAssignableFrom(targetType)) {
                                        setProperty(bean, key, array, jsonConfig);
                                    } else if (List.class.isAssignableFrom(targetType)
                                               || Set.class.isAssignableFrom(targetType)) {
                                        jsc.setCollectionType(targetType);
                                        setProperty(bean, key, JSONArray.toCollection(array, jsc), jsonConfig);
                                    } else {
                                        setProperty(bean, key, toBean((JSONObject) value, jsc), jsonConfig);
                                    }
                                } else {
                                    if (targetType == Object.class) {
                                        targetType = findTargetClass(key, classMap);
                                        targetType = targetType == null ? findTargetClass(name, classMap) : targetType;
                                    }
                                    JsonConfig jsc = jsonConfig.copy();
                                    jsc.setRootClass(targetType);
                                    jsc.setClassMap(classMap);
                                    setProperty(bean, key, toBean((JSONObject) value, jsc), jsonConfig);
                                }
                            }
                        } else {
                            if (type.isPrimitive()) {
                                // assume assigned default value
                                log.warn("Tried to assign null value to " + key + ":" + type.getName());
                                setProperty(bean, key, JSONUtils.getMorpherRegistry().morph(type, null), jsonConfig);
                            } else {
                                setProperty(bean, key, null, jsonConfig);
                            }
                        }
                    } else {
                        if (!JSONUtils.isNull(value)) {
                            if (value instanceof JSONArray) {
                                setProperty(bean,
                                            key,
                                            convertPropertyValueToCollection(key, value, jsonConfig, name, classMap,
                                                                             List.class), jsonConfig);
                            } else if (String.class.isAssignableFrom(type) || JSONUtils.isBoolean(type)
                                       || JSONUtils.isNumber(type) || JSONUtils.isString(type)
                                       || JSONFunction.class.isAssignableFrom(type)) {
                                if (pd != null) {
                                    if (jsonConfig.isHandleJettisonEmptyElement() && "".equals(value)) {
                                        setProperty(bean, key, null, jsonConfig);
                                    } else {
                                        setProperty(bean, key, value, jsonConfig);
                                    }
                                } else if (beanClass == null || bean instanceof Map) {
                                    setProperty(bean, key, value, jsonConfig);
                                } else {
                                    log.warn("Tried to assign property " + key + ":" + type.getName()
                                             + " to bean of class " + bean.getClass().getName());
                                }
                            } else {
                                if (jsonConfig.isHandleJettisonSingleElementArray()) {
                                    Class newTargetClass = findTargetClass(key, classMap);
                                    newTargetClass = newTargetClass == null ? findTargetClass(name, classMap) : newTargetClass;
                                    JsonConfig jsc = jsonConfig.copy();
                                    jsc.setRootClass(newTargetClass);
                                    jsc.setClassMap(classMap);
                                    setProperty(bean, key, toBean((JSONObject) value, jsc), jsonConfig);
                                } else {
                                    setProperty(bean, key, value, jsonConfig);
                                }
                            }
                        } else {
                            if (type.isPrimitive()) {
                                // assume assigned default value
                                log.warn("Tried to assign null value to " + key + ":" + type.getName());
                                setProperty(bean, key, JSONUtils.getMorpherRegistry().morph(type, null), jsonConfig);
                            } else {
                                setProperty(bean, key, null, jsonConfig);
                            }
                        }
                    }
                }
            } catch (JSONException jsone) {
                throw jsone;
            } catch (Exception e) {
                throw new JSONException("Error while setting property=" + name + " type " + type, e);
            }
        }

        return bean;
    }

    private static Object convertPropertyValueToArray(String key, Object value, Class targetType,
                                                      JsonConfig jsonConfig, Map classMap) {
        Class innerType = JSONUtils.getInnerComponentType(targetType);
        Class targetInnerType = findTargetClass(key, classMap);
        if (innerType.equals(Object.class) && targetInnerType != null && !targetInnerType.equals(Object.class)) {
            innerType = targetInnerType;
        }
        JsonConfig jsc = jsonConfig.copy();
        jsc.setRootClass(innerType);
        jsc.setClassMap(classMap);
        Object array = JSONArray.toArray((JSONArray) value, jsc);
        if (innerType.isPrimitive() || JSONUtils.isNumber(innerType) || Boolean.class.isAssignableFrom(innerType)
            || JSONUtils.isString(innerType)) {
            array = JSONUtils.getMorpherRegistry().morph(Array.newInstance(innerType, 0).getClass(), array);
        } else if (!array.getClass().equals(targetType)) {
            if (!targetType.equals(Object.class)) {
                Morpher morpher = JSONUtils.getMorpherRegistry().getMorpherFor(Array.newInstance(innerType, 0).getClass());
                if (IdentityObjectMorpher.getInstance().equals(morpher)) {
                    ObjectArrayMorpher beanMorpher = new ObjectArrayMorpher(
                                                                            new BeanMorpher(
                                                                                            innerType,
                                                                                            JSONUtils.getMorpherRegistry()));
                    JSONUtils.getMorpherRegistry().registerMorpher(beanMorpher);
                }
                array = JSONUtils.getMorpherRegistry().morph(Array.newInstance(innerType, 0).getClass(), array);
            }
        }
        return array;
    }

    /**
     * Creates a bean from a JSONObject, with the specific configuration.
     */
    public static Object toBean(JSONObject jsonObject, Object root, JsonConfig jsonConfig) {
        if (jsonObject == null || jsonObject.isNullObject() || root == null) {
            return root;
        }

        Class rootClass = root.getClass();
        if (rootClass.isInterface()) {
            throw new JSONException("Root bean is an interface. " + rootClass);
        }

        Map classMap = jsonConfig.getClassMap();
        if (classMap == null) {
            classMap = Collections.EMPTY_MAP;
        }

        Map props = JSONUtils.getProperties(jsonObject);
        PropertyFilter javaPropertyFilter = jsonConfig.getJavaPropertyFilter();
        for (Iterator entries = jsonObject.names().iterator(); entries.hasNext();) {
            String name = (String) entries.next();
            Class type = (Class) props.get(name);
            Object value = jsonObject.get(name);
            if (javaPropertyFilter != null && javaPropertyFilter.apply(root, name, value)) {
                continue;
            }
            String key = JSONUtils.convertToJavaIdentifier(name, jsonConfig);
            try {
                PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(root, key);
                if (pd != null && pd.getWriteMethod() == null) {
                    log.warn("Property '" + key + "' has no write method. SKIPPED.");
                    continue;
                }

                if (!JSONUtils.isNull(value)) {
                    if (value instanceof JSONArray) {
                        if (pd == null || List.class.isAssignableFrom(pd.getPropertyType())) {
                            Class targetClass = findTargetClass(key, classMap);
                            targetClass = targetClass == null ? findTargetClass(name, classMap) : targetClass;
                            Object newRoot = jsonConfig.getNewBeanInstanceStrategy().newInstance(targetClass, null);
                            List list = JSONArray.toList((JSONArray) value, newRoot, jsonConfig);
                            setProperty(root, key, list, jsonConfig);
                        } else {
                            Class innerType = JSONUtils.getInnerComponentType(pd.getPropertyType());
                            Class targetInnerType = findTargetClass(key, classMap);
                            if (innerType.equals(Object.class) && targetInnerType != null
                                && !targetInnerType.equals(Object.class)) {
                                innerType = targetInnerType;
                            }
                            Object newRoot = jsonConfig.getNewBeanInstanceStrategy().newInstance(innerType, null);
                            Object array = JSONArray.toArray((JSONArray) value, newRoot, jsonConfig);
                            if (innerType.isPrimitive() || JSONUtils.isNumber(innerType)
                                || Boolean.class.isAssignableFrom(innerType) || JSONUtils.isString(innerType)) {
                                array = JSONUtils.getMorpherRegistry().morph(Array.newInstance(innerType, 0).getClass(),
                                                                             array);
                            } else if (!array.getClass().equals(pd.getPropertyType())) {
                                if (!pd.getPropertyType().equals(Object.class)) {
                                    Morpher morpher = JSONUtils.getMorpherRegistry().getMorpherFor(Array.newInstance(innerType,
                                                                                                                     0).getClass());
                                    if (IdentityObjectMorpher.getInstance().equals(morpher)) {
                                        ObjectArrayMorpher beanMorpher = new ObjectArrayMorpher(
                                                                                                new BeanMorpher(
                                                                                                                innerType,
                                                                                                                JSONUtils.getMorpherRegistry()));
                                        JSONUtils.getMorpherRegistry().registerMorpher(beanMorpher);
                                    }
                                    array = JSONUtils.getMorpherRegistry().morph(Array.newInstance(innerType, 0).getClass(),
                                                                                 array);
                                }
                            }
                            setProperty(root, key, array, jsonConfig);
                        }
                    } else if (String.class.isAssignableFrom(type) || JSONUtils.isBoolean(type)
                               || JSONUtils.isNumber(type) || JSONUtils.isString(type)
                               || JSONFunction.class.isAssignableFrom(type)) {
                        if (pd != null) {
                            if (jsonConfig.isHandleJettisonEmptyElement() && "".equals(value)) {
                                setProperty(root, key, null, jsonConfig);
                            } else if (!pd.getPropertyType().isInstance(value)) {
                                Morpher morpher = JSONUtils.getMorpherRegistry().getMorpherFor(pd.getPropertyType());
                                if (IdentityObjectMorpher.getInstance().equals(morpher)) {
                                    log.warn("Can't transform property '" + key + "' from " + type.getName() + " into "
                                             + pd.getPropertyType().getName() + ". Will register a default BeanMorpher");
                                    JSONUtils.getMorpherRegistry().registerMorpher(new BeanMorpher(
                                                                                                   pd.getPropertyType(),
                                                                                                   JSONUtils.getMorpherRegistry()));
                                }
                                setProperty(root, key,
                                            JSONUtils.getMorpherRegistry().morph(pd.getPropertyType(), value),
                                            jsonConfig);
                            } else {
                                setProperty(root, key, value, jsonConfig);
                            }
                        } else if (root instanceof Map) {
                            setProperty(root, key, value, jsonConfig);
                        } else {
                            log.warn("Tried to assign property " + key + ":" + type.getName() + " to bean of class "
                                     + root.getClass().getName());
                        }
                    } else {
                        if (pd != null) {
                            Class targetClass = pd.getPropertyType();
                            if (jsonConfig.isHandleJettisonSingleElementArray()) {
                                JSONArray array = new JSONArray().element(value, jsonConfig);
                                Class newTargetClass = findTargetClass(key, classMap);
                                newTargetClass = newTargetClass == null ? findTargetClass(name, classMap) : newTargetClass;
                                Object newRoot = jsonConfig.getNewBeanInstanceStrategy().newInstance(newTargetClass,
                                                                                                     null);
                                if (targetClass.isArray()) {
                                    setProperty(root, key, JSONArray.toArray(array, newRoot, jsonConfig), jsonConfig);
                                } else if (Collection.class.isAssignableFrom(targetClass)) {
                                    setProperty(root, key, JSONArray.toList(array, newRoot, jsonConfig), jsonConfig);
                                } else if (JSONArray.class.isAssignableFrom(targetClass)) {
                                    setProperty(root, key, array, jsonConfig);
                                } else {
                                    setProperty(root, key, toBean((JSONObject) value, newRoot, jsonConfig), jsonConfig);
                                }
                            } else {
                                if (targetClass == Object.class) {
                                    targetClass = findTargetClass(key, classMap);
                                    targetClass = targetClass == null ? findTargetClass(name, classMap) : targetClass;
                                }
                                Object newRoot = jsonConfig.getNewBeanInstanceStrategy().newInstance(targetClass, null);
                                setProperty(root, key, toBean((JSONObject) value, newRoot, jsonConfig), jsonConfig);
                            }
                        } else if (root instanceof Map) {
                            Class targetClass = findTargetClass(key, classMap);
                            targetClass = targetClass == null ? findTargetClass(name, classMap) : targetClass;
                            Object newRoot = jsonConfig.getNewBeanInstanceStrategy().newInstance(targetClass, null);
                            setProperty(root, key, toBean((JSONObject) value, newRoot, jsonConfig), jsonConfig);
                        } else {
                            log.warn("Tried to assign property " + key + ":" + type.getName() + " to bean of class "
                                     + rootClass.getName());
                        }
                    }
                } else {
                    if (type.isPrimitive()) {
                        // assume assigned default value
                        log.warn("Tried to assign null value to " + key + ":" + type.getName());
                        setProperty(root, key, JSONUtils.getMorpherRegistry().morph(type, null), jsonConfig);
                    } else {
                        setProperty(root, key, null, jsonConfig);
                    }
                }
            } catch (JSONException jsone) {
                throw jsone;
            } catch (Exception e) {
                throw new JSONException("Error while setting property=" + name + " type " + type, e);
            }
        }

        return root;
    }

    private static List convertPropertyValueToList(String key, Object value, JsonConfig jsonConfig, String name,
                                                   Map classMap) {
        Class targetClass = findTargetClass(key, classMap);
        targetClass = targetClass == null ? findTargetClass(name, classMap) : targetClass;
        JsonConfig jsc = jsonConfig.copy();
        jsc.setRootClass(targetClass);
        jsc.setClassMap(classMap);
        List list = (List) JSONArray.toCollection((JSONArray) value, jsc);
        return list;
    }

    private static Collection convertPropertyValueToCollection(String key, Object value, JsonConfig jsonConfig,
                                                               String name, Map classMap, Class collectionType) {
        Class targetClass = findTargetClass(key, classMap);
        targetClass = targetClass == null ? findTargetClass(name, classMap) : targetClass;
        JsonConfig jsc = jsonConfig.copy();
        jsc.setRootClass(targetClass);
        jsc.setClassMap(classMap);
        jsc.setCollectionType(collectionType);
        return JSONArray.toCollection((JSONArray) value, jsc);
    }

    /*
     * private static Collection convertPropertyValueToCollection( String key, Object value, String name, Object bean,
     * JsonConfig jsonConfig, Map classMap ) { Class targetClass = findTargetClass( key, classMap ); targetClass =
     * targetClass == null ? findTargetClass( name, classMap ) : targetClass; PropertyDescriptor pd; try{ pd =
     * PropertyUtils.getPropertyDescriptor( bean, key ); }catch( IllegalAccessException e ){ throw new JSONException( e
     * ); }catch( InvocationTargetException e ){ throw new JSONException( e ); }catch( NoSuchMethodException e ){ throw
     * new JSONException( e ); } if( null == targetClass ){ Class[] cType = JSONArray.getCollectionType( pd, false );
     * if( null != cType && cType.length == 1 ){ targetClass = cType[0]; } } JsonConfig jsc = jsonConfig.copy();
     * jsc.setRootClass( targetClass ); jsc.setClassMap( classMap ); jsc.setCollectionType( pd.getPropertyType() );
     * jsc.setEnclosedType( targetClass ); Collection collection = JSONArray.toCollection( (JSONArray) value, jsonConfig
     * ); return collection; }
     */

    /**
     * Locates a Class associated to a specifi key.<br>
     * The key may be a regexp.
     */
    private static Class findTargetClass(String key, Map classMap) {
        // try get first
        Class targetClass = (Class) classMap.get(key);
        if (targetClass == null) {
            // try with regexp
            // this will hit performance as it must iterate over all the keys
            // and create a RegexpMatcher for each key
            for (Iterator i = classMap.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                if (RegexpUtils.getMatcher((String) entry.getKey()).matches(key)) {
                    targetClass = (Class) entry.getValue();
                    break;
                }
            }
        }

        return targetClass;
    }

    private static boolean isTransientField(String name, Class beanClass) {
        try {
            Field field = beanClass.getDeclaredField(name);
            return (field.getModifiers() & Modifier.TRANSIENT) == Modifier.TRANSIENT;
        } catch (Exception e) {
            // swallow exception
        }
        return false;
    }

    private static Object morphPropertyValue(String key, Object value, Class type, Class targetType) {
        Morpher morpher = JSONUtils.getMorpherRegistry().getMorpherFor(targetType);
        if (IdentityObjectMorpher.getInstance().equals(morpher)) {
            log.warn("Can't transform property '" + key + "' from " + type.getName() + " into " + targetType.getName()
                     + ". Will register a default Morpher");
            if (Enum.class.isAssignableFrom(targetType)) {
                JSONUtils.getMorpherRegistry().registerMorpher(new EnumMorpher(targetType));
            } else {
                JSONUtils.getMorpherRegistry().registerMorpher(new BeanMorpher(targetType,
                                                                               JSONUtils.getMorpherRegistry()));
            }
        }
        value = JSONUtils.getMorpherRegistry().morph(targetType, value);
        return value;
    }

    /**
     * Sets a property on the target bean.<br>
     * Bean may be a Map or a POJO.
     */
    private static void setProperty(Object bean, String key, Object value, JsonConfig jsonConfig) throws Exception {
        PropertySetStrategy propertySetStrategy = jsonConfig.getPropertySetStrategy() != null ? jsonConfig.getPropertySetStrategy() : PropertySetStrategy.DEFAULT;
        propertySetStrategy.setProperty(bean, key, value);
    }

}
