/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.security.velocity.directive;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * @author zxc Apr 12, 2013 3:35:56 PM
 */
public class Noescape extends Directive {

    @Override
    public String getName() {
        return "noescape";
    }

    @Override
    public int getType() {
        return BLOCK;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException,
                                                                                   ResourceNotFoundException,
                                                                                   ParseErrorException,
                                                                                   MethodInvocationException {
        DirectiveType hasType = (DirectiveType) context.localPut(DirectiveType.typeKey, DirectiveType.NO_ESCAPE);
        try {
            node.jjtGetChild(0).render(context, writer);
        } finally {
            if (hasType == null) {
                context.remove(DirectiveType.typeKey);
            } else {
                context.localPut(DirectiveType.typeKey, hasType);
            }
        }
        return true;
    }

}
