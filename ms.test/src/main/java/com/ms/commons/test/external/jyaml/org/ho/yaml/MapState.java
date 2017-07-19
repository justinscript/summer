/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import static com.ms.commons.test.external.jyaml.yaml.parser.YamlParser.MAP_CLOSE;
import static com.ms.commons.test.external.jyaml.yaml.parser.YamlParser.MAP_SEPARATOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.ms.commons.test.external.jyaml.org.ho.util.Logger;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.MapWrapper;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.ObjectWrapper;

/**
 * @author zxc Apr 14, 2013 12:37:08 AM
 */
class MapState extends State {

    String key;

    MapState(Map<String, ObjectWrapper> aliasMap, Stack<State> stack, YamlDecoder decoder, Logger logger) {
        super(aliasMap, stack, decoder, logger);
    }

    protected MapWrapper getMap() {
        return (MapWrapper) getWrapper();
    }

    @Override
    public void nextOnContent(String type, String content) {
        if (key == null) key = content;
        else {
            ObjectWrapper newObject = null;
            if ("alias".equals(type)) {
                String alias = content.substring(1);
                if (aliasMap.containsKey(alias)) {
                    newObject = aliasMap.get(alias);
                    final String currentKey = key;
                    newObject.addCreateHandler(new ObjectWrapper.CreateListener() {

                        public void created(Object obj) {
                            getMap().put(currentKey, obj);
                        }
                    });
                }
            } else {

                newObject = decoder.getConfig().getWrapperSetContent(expectedType(type), content);
                getMap().put(Utilities.decodeSimpleType(key), newObject.getObject());

                if (getAnchorname() != null) markAnchor(newObject, getAnchorname());
            }
            clear();
            key = null;
        }
    }

    @Override
    public void nextOnEvent(int event) {
        switch (event) {
            case MAP_SEPARATOR:
                break;
            case MAP_CLOSE:
                stack.pop();
                stack.peek().childCallback(wrapper);
                break;
            default:
                super.nextOnEvent(event);
        }

    }

    @Override
    public void childCallback(ObjectWrapper child) {
        getMap().put(Utilities.decodeSimpleType(key), child.getObject());
        clear();
        key = null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected String expectedType() {
        if (getClassname() != null) return getClassname();
        else {
            Class type = getMap().getExpectedType(key);
            if (type == null) return null;
            else {
                String ret = ReflectionUtil.className(type);
                if (List.class.getName().equals(ret)) return ArrayList.class.getName();
                else if (Map.class.getName().equals(ret)) return HashMap.class.getName();
                else return ret;
            }
        }
    }
}
