/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.security.velocity.directive;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个牧举为了以后扩展多个Driective时在ReferenceInsertionEventHandler中判断 Directive的类型
 * 
 * @author zxc Apr 12, 2013 3:36:08 PM
 */
public enum DirectiveType {
    NO_ESCAPE("noescape") {

        @Override
        public String escape(String str) {
            return str;
        }
    };

    private static final Map<String, DirectiveType> namedTypes = new HashMap<String, DirectiveType>();
    private final String                            name;
    public static final String                      typeKey    = "_directive_type_key_";
    static {
        for (DirectiveType escapeType : DirectiveType.values()) {
            namedTypes.put(escapeType.getName(), escapeType);
        }
    }

    private DirectiveType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String escape(String str);

    public static DirectiveType getDirectiveTypee(String name) {
        if (name.trim().length() == 0) {
            name = null;
        }
        if (name != null) {
            return namedTypes.get(name);
        }
        return null;
    }
}
