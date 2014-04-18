/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.yaml.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author zxc Apr 14, 2013 12:37:43 AM
 */
public class YamlParserRefEvent implements ParserEvent {

    PrintStream out;

    public YamlParserRefEvent() {
        out = System.out;
    }

    public YamlParserRefEvent(File file) throws Exception {
        out = new PrintStream(new FileOutputStream(file));
    }

    public void event(String a) {
    }

    public void error(Exception e, int line) {
        out.println("error:  " + line);
    }

    public void event(int c) {
        switch (c) {
            case YamlParser.MAP_CLOSE:
                out.println("}");
                break;
            case YamlParser.LIST_CLOSE:
                out.println("]");
                break;
            case YamlParser.MAP_NO_OPEN:
                out.println("}-");
                break;
            case YamlParser.LIST_NO_OPEN:
                out.println("]-");
                break;
            case YamlParser.LIST_OPEN:
                out.println("[");
                break;
            case YamlParser.MAP_OPEN:
                out.println("{");
                break;
        }
    }

    public void content(String a, String b) {
        out.println(a + " : " + b);
    }

    public void property(String a, String b) {
        out.println(a + " : " + b);
    }
}
