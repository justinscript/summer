/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.security.xss.impl;

import java.io.StringReader;

import org.apache.batik.css.parser.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;

import com.ms.commons.fasttext.security.xss.ScanException;

/**
 * @author zxc Apr 12, 2013 3:27:15 PM
 */
public class CssScanner {

    private static final Log logger = LogFactory.getLog(CssScanner.class);

    public String scanStyleSheet(String taintedCss, int sizeLimit, boolean inline) throws ScanException {
        if (taintedCss != null) {
            taintedCss = taintedCss.toLowerCase();
        } else {
            throw new ScanException("taintedCss is null");
        }
        // Parser is not thread safe, DO NOT PUT IT IN CLASS FIELDS VAR DEFINE
        Parser parser = new Parser();
        StringBuilder buffer = new StringBuilder(taintedCss.length() << 1);
        DocumentHandler handler = new CssDocumentHandler(buffer, inline);

        // parse the stylesheet
        parser.setDocumentHandler(handler);
        try {
            // parse the style declaration
            // note this does not count against the size limit because it
            // should already have been counted by the caller since it was
            // embedded in the HTML
            if (inline) {
                parser.parseStyleDeclaration(new InputSource(new StringReader(taintedCss)));
            } else {
                parser.parseStyleSheet(new InputSource(new StringReader(taintedCss)));
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return buffer.toString();
    }
}
