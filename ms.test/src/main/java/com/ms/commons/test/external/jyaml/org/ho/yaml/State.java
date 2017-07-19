/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import static com.ms.commons.test.external.jyaml.yaml.parser.YamlParser.LIST_OPEN;
import static com.ms.commons.test.external.jyaml.yaml.parser.YamlParser.MAP_OPEN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.ms.commons.test.external.jyaml.org.ho.util.Logger;
import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.CollectionWrapper;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.MapWrapper;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.ObjectWrapper;

/**
 * @author zxc Apr 14, 2013 12:36:32 AM
 */
abstract class State {

    Logger                     logger;
    YamlDecoder                decoder;
    Map<String, ObjectWrapper> aliasMap;
    Stack<State>               stack;
    ObjectWrapper              wrapper;
    String                     declaredClassname;
    String                     anchorname;

    State(Map<String, ObjectWrapper> aliasMap, Stack<State> stack, YamlDecoder decoder, Logger logger) {
        this.aliasMap = aliasMap;
        this.stack = stack;
        this.decoder = decoder;
        this.logger = logger;
    }

    public void nextOnEvent(int event) {
        switch (event) {
            case MAP_OPEN:
                openMap(stack);
                break;
            case LIST_OPEN:
                openList(stack);
                break;
            default:
        }
    }

    public void nextOnContent(String type, String content) {
    }

    public void nextOnProperty(String type, String value) {
        if ("transfer".equals(type)) {
            if (getDeclaredClassname() == null && value.startsWith("!")) {
                setDeclaredClassname(ReflectionUtil.transfer2classname(value.substring(1), decoder.getConfig()));
            }
        } else if ("anchor".equals(type)) {
            if (value.startsWith("&")) setAnchorname(value.substring(1));
        }
    }

    public abstract void childCallback(ObjectWrapper child);

    void clear() {
        setDeclaredClassname(null);
        setAnchorname(null);
    }

    ObjectWrapper createWrapper(String fallback) {
        ObjectWrapper ret = decoder.getConfig().getWrapper(expectedType());
        if (ret == null) ret = decoder.getConfig().getWrapper(fallback);
        return ret;
    }

    protected String expectedType(String type) {
        String ret = expectedType();
        if (ret == null && "string".equals(type)) ret = "java.lang.String";
        return ret;
    }

    protected String expectedType() {
        return getClassname();
    }

    void openMap(Stack<State> stack) {
        ObjectWrapper obj = createWrapper(ReflectionUtil.className(HashMap.class));
        // if (!(obj instanceof MapWrapper))
        // throw new YamlException("TODO"); //TODO
        if (getAnchorname() != null) markAnchor(obj, getAnchorname());
        State s = new MapState(aliasMap, stack, decoder, logger);
        if (!(obj instanceof MapWrapper)) throw new YamlException(
                                                                  obj.getObject()
                                                                          + " is not a Collection and so cannot be mapped from a sequence.");
        s.wrapper = obj;
        stack.push(s);
    }

    void openList(Stack<State> stack) {
        ObjectWrapper newObject = createWrapper(ReflectionUtil.className(ArrayList.class));
        // if (!(wrapper instanceof CollectionWrapper))
        // throw new YamlException("TODO"); //TODO
        if (getAnchorname() != null) markAnchor(newObject, getAnchorname());
        State s = new ListState(aliasMap, stack, decoder, logger);
        if (!(newObject instanceof CollectionWrapper)) throw new YamlException(
                                                                               newObject.getObject()
                                                                                       + " is not a Collection and so cannot be mapped from a sequence.");
        s.wrapper = newObject;
        stack.push(s);
    }

    void markAnchor(ObjectWrapper obj, String anchorname) {
        if (aliasMap.get(anchorname) == null) aliasMap.put(anchorname, obj);
    }

    public ObjectWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(ObjectWrapper obj) {
        this.wrapper = obj;
    }

    public String getClassname() {
        return declaredClassname;
    }

    public String getDeclaredClassname() {
        return declaredClassname;
    }

    public void setDeclaredClassname(String type) {
        this.declaredClassname = type;
    }

    public String getAnchorname() {
        return anchorname;
    }

    public void setAnchorname(String anchorname) {
        this.anchorname = anchorname;
    }

}
