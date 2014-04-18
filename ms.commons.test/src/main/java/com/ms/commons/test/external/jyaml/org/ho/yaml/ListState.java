/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import static com.ms.commons.test.external.jyaml.yaml.parser.YamlParser.LIST_CLOSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.ms.commons.test.external.jyaml.org.ho.util.Logger;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.CollectionWrapper;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.ObjectWrapper;

/**
 * @author zxc Apr 14, 2013 12:37:17 AM
 */
class ListState extends State {

    /**
     * @param aliasMap
     * @param stack
     */
    ListState(Map<String, ObjectWrapper> aliasMap, Stack<State> stack, YamlDecoder decoder, Logger logger) {
        super(aliasMap, stack, decoder, logger);
    }

    CollectionWrapper getCollection() {
        return (CollectionWrapper) getWrapper();
    }

    @Override
    public void nextOnContent(String type, String content) {
        if (content.length() > 0 && "alias".equals(type) && aliasMap.containsKey(content.substring(1))) {
            String alias = content.substring(1);
            ObjectWrapper toAdd = aliasMap.get(alias);
            final int position = getCollection().size();
            toAdd.addCreateHandler(new ObjectWrapper.CreateListener() {

                public void created(Object obj) {
                    if (getCollection().isOrdered()) getCollection().add(position, obj);
                    else getCollection().add(obj);
                }
            });
        } else {
            ObjectWrapper toAdd = decoder.getConfig().getWrapperSetContent(expectedType(type), content);
            if (getAnchorname() != null) markAnchor(toAdd, getAnchorname());
            getCollection().add(toAdd.getObject());
        }
        clear();
    }

    @Override
    public void nextOnEvent(int event) {
        switch (event) {
            case LIST_CLOSE:
                stack.pop();
                // handleArray(stack.peek().getClassname());
                stack.peek().childCallback(getWrapper());
                break;
            default:
                super.nextOnEvent(event);
        }

    }

    @SuppressWarnings("rawtypes")
    protected String expectedType() {
        if (getCollection().isTyped()) {
            Class arrayComponentType = getCollection().componentType();
            if (Object.class != arrayComponentType) return ReflectionUtil.className(arrayComponentType);
        }
        {
            String ret = super.expectedType();
            if (List.class.getName().equals(ret)) return ArrayList.class.getName();
            else if (Map.class.getName().equals(ret)) return HashMap.class.getName();
            else return ret;
        }
    }

    @Override
    public void childCallback(ObjectWrapper child) {
        getCollection().add(child.getObject());
        clear();
    }
}
