/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.security.xss;

import java.io.IOException;
import java.io.StringReader;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Purifier;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ms.commons.fasttext.security.xss.impl.CssScanner;
import com.ms.commons.fasttext.security.xss.impl.FragmentXppParser;
import com.ms.commons.fasttext.security.xss.impl.XssFilterDocumentHandler;
import com.ms.commons.fasttext.text.DetectDefinedChar;

/**
 * @author zxc Apr 12, 2013 3:28:39 PM
 */
public class XssXppScanner {

    private Policy                policy               = null;
    private CssScanner            cssScanner           = new CssScanner();

    protected static final String NAMES_ELEMS_PROPERTY = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS_PROPERTY = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String FILTERS_PROPERTY     = "http://cyberneko.org/html/properties/filters";

    // XML Character Define char::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    // 定义XML允许出现的合法字符
    static DetectDefinedChar      detectXMLChar        = new DetectDefinedChar();
    static {
        detectXMLChar.addProhibitChar(0x9);
        detectXMLChar.addProhibitChar(0xA);
        detectXMLChar.addProhibitChar(0xD);
        for (int i = 0x20; i < (0xD7FF + 1); i++) {
            detectXMLChar.addProhibitChar(i);
        }
        for (int i = 0xE000; i < (0xFFFD + 1); i++) {
            detectXMLChar.addProhibitChar(i);
        }
    }

    public XssXppScanner(Policy policy) {
        this.policy = policy;

    }

    public String scan(String html) {
        if (html == null) {
            throw new ScanException("input html can not be null");
        }
        if (html.length() > policy.maxInputSize) {
            throw new ScanException("input html reach max size: " + policy.maxInputSize);
        }

        if (this.policy.usePreXMLValid) {
            html = stripNonValidXMLCharacters(html);
        }

        FragmentXppParser parser = new FragmentXppParser();
        if (this.policy.elemsLower) {
            parser.setProperty(NAMES_ELEMS_PROPERTY, "lower");
        } else {
            parser.setProperty(NAMES_ELEMS_PROPERTY, "upper");
        }
        if (this.policy.attrsLower) {
            parser.setProperty(NAMES_ATTRS_PROPERTY, "lower");
        } else {
            parser.setProperty(NAMES_ATTRS_PROPERTY, "upper");
        }

        StringBuilder buffer = new StringBuilder(html.length() << 1);

        // 输出工具
        XssFilterDocumentHandler filter = new XssFilterDocumentHandler(buffer, policy, cssScanner);

        if (this.policy.usePurifier) {
            Purifier p = new Purifier();
            XMLDocumentFilter[] filters = { p, filter };
            parser.setProperty(FILTERS_PROPERTY, filters);
        } else {
            XMLDocumentFilter[] filters = { filter };
            parser.setProperty(FILTERS_PROPERTY, filters);
        }

        InputSource input = new InputSource(new StringReader(html));
        try {
            parser.parse(input);
        } catch (SAXException e) {
            throw new ScanException(e);
        } catch (IOException e) {
            throw new ScanException(e);
        }
        return buffer.toString();

    }

    /**
     * This method was borrowed from Mark McLaren, to whom I owe much beer. This method ensures that the output has only
     * valid XML unicode characters as specified by the XML 1.0 standard. For reference, please see <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the standard</a>. This method will return an empty
     * String if the input is null or empty.
     * 
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */

    public String stripNonValidXMLCharacters(String in) {
        if (in == null) {
            return null;
        }
        StringBuilder sb = null;
        int len = in.length();
        for (int i = 0; i < len; i++) {
            char ch = in.charAt(i);
            if (detectXMLChar.isProhibitChar(ch)) {
                if (sb != null) {
                    sb.append(ch);
                }
            } else {
                if (sb == null) {
                    sb = new StringBuilder(len);
                    sb.append(in, 0, i);
                }
            }

        }
        return (sb == null ? in : sb.toString());
    }
}
