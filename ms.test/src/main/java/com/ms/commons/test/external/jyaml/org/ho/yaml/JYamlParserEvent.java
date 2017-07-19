/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.ms.commons.test.external.jyaml.org.ho.util.Logger;
import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.ObjectWrapper;
import com.ms.commons.test.external.jyaml.yaml.parser.YamlParserEvent;

/**
 * @author zxc Apr 14, 2013 12:37:28 AM
 */
class JYamlParserEvent extends YamlParserEvent {

    Stack<State>               stack    = new Stack<State>();
    Map<String, ObjectWrapper> aliasMap = new HashMap<String, ObjectWrapper>();

    public JYamlParserEvent(Logger logger, YamlDecoder decoder) {
        stack.push(new NoneState(aliasMap, stack, decoder, logger));
    }

    public JYamlParserEvent(Object object, Logger logger, YamlDecoder decoder) {
    }

    @SuppressWarnings("rawtypes")
    public JYamlParserEvent(Class clazz, Logger logger, YamlDecoder decoder) {
        this(logger, decoder);
        String classname = ReflectionUtil.className(clazz);
        stack.peek().setDeclaredClassname(classname);
        // stack.peek().setWrapper(decoder.getConfig().getWrapper(clazz));
        // if (!clazz.isArray() && !ReflectionUtil.isSimpleType(clazz))
        // try {
        // stack.peek().setWrapper(clazz.newInstance());
        // } catch (Exception e){
        // throw new YamlException("Can't instantiate object of type " + clazz.getName());
        // }
    }

    @Override
    public void content(String a, String b) {
        stack.peek().nextOnContent(a, b);
    }

    @Override
    public void error(Exception e, int line) {
        throw new YamlException(e.getMessage(), line);
    }

    @Override
    public void event(int c) {
        stack.peek().nextOnEvent(c);
    }

    @Override
    public void property(String a, String b) {
        stack.peek().nextOnProperty(a, b);
    }

    public Object getBean() {
        return stack.peek().getWrapper().getObject();
    }
}
