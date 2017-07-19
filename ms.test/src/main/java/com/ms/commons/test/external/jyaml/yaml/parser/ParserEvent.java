/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.yaml.parser;

/**
 * Generic parser event API.
 * 
 * @license: Open-source compatible TBD (Apache or zlib or Public Domain)
 * @author zxc Apr 14, 2013 12:39:29 AM
 */
public interface ParserEvent {

    void event(int i);

    void event(String s);

    void content(String a, String b);

    void property(String a, String b);

    void error(Exception e, int line);
}
