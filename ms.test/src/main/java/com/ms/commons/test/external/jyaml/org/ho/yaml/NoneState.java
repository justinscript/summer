/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import java.util.Map;
import java.util.Stack;

import com.ms.commons.test.external.jyaml.org.ho.util.Logger;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.ObjectWrapper;

/**
 * NoneState is a special state that never gets put on the Stack. You will only ever be in the NoneState at the
 * beginning of a parser when you don't know whether the top level element if a map or a list yet.
 * 
 * @author zxc Apr 14, 2013 12:36:52 AM
 */
class NoneState extends State {

    NoneState(Map<String, ObjectWrapper> aliasMap, Stack<State> stack, YamlDecoder decoder, Logger logger) {
        super(aliasMap, stack, decoder, logger);
    }

    @Override
    public void childCallback(ObjectWrapper child) {
        this.wrapper = child;
    }

    /*
     * (non-Javadoc)
     * @see org.ho.yaml.states.State#nextOnContent(java.lang.String, java.lang.String)
     */
    @Override
    public void nextOnContent(String type, String content) {
        wrapper = decoder.getConfig().getWrapperSetContent(expectedType(type), content);
    }
}
