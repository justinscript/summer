/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.security.xss.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.HTMLElements;
import org.cyberneko.html.filters.DefaultFilter;

import com.ms.commons.fasttext.codec.HtmlFastEntities;
import com.ms.commons.fasttext.security.xss.Policy;
import com.ms.commons.fasttext.security.xss.model.Action;
import com.ms.commons.fasttext.security.xss.model.Attribute;
import com.ms.commons.fasttext.security.xss.model.RestrictAttribute;
import com.ms.commons.fasttext.security.xss.model.Tag;

/**
 * 为了提高性能， 把删除标记与输出做在一起， 大约可以提高100％性能。 另外也也顺便修改了一些nekohtml不合理比较string的方法。
 * 
 * @author zxc Apr 12, 2013 3:26:34 PM
 */
public class XssFilterDocumentHandler extends DefaultFilter {

    protected int              removalStack   = 0;

    /** output buffer */
    protected StringBuilder    fwriterBuffer;

    /** Seen root element. */
    protected boolean          fSeenRootElement;

    /** Normalize character content. */
    protected boolean          fNormalize;

    /** Print characters. */
    protected boolean          fPrintChars;

    protected Map<String, Tag> tagRules;

    private CssScanner         cssScanner;
    private Policy             fPolicy;
    private boolean            parserStyleTag = false;

    public XssFilterDocumentHandler(StringBuilder writerBuffer, Policy policy, CssScanner cssScanner) {
        this.fwriterBuffer = writerBuffer;
        this.fPolicy = policy;
        this.tagRules = policy.getTagRules();
        this.cssScanner = cssScanner;
    }

    //
    // XMLDocumentHandler methods
    //

    // since Xerces-J 2.2.0

    /** Start document. */
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) {
        fSeenRootElement = false;
        fNormalize = true;
        fPrintChars = true;
        parserStyleTag = false;
        super.startDocument(locator, encoding, nscontext, augs);
    }

    // old methods

    /** Start document. */
    public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException {
        startDocument(locator, encoding, null, augs);
    }

    /** Start prefix mapping. */
    public void startPrefixMapping(String prefix, String uri, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.startPrefixMapping(prefix, uri, augs);
        }
    }

    /** Start element. */

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {

        if (handleOpenTag(element, attributes) && removalStack == 0) {
            fSeenRootElement = true;
            fNormalize = !HTMLElements.getElement(element.rawname).isSpecial();
            printStartElement(element, attributes);
            super.startElement(element, attributes, augs);
        }

    }

    /** End element. */

    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (elementAccepted(element) && removalStack == 0) {
            fNormalize = true;
            printEndElement(element);
            super.endElement(element, augs);
        }

    }

    protected boolean handleOpenTag(QName element, XMLAttributes attributes) {
        Tag tag = tagRules.get(element.rawname);
        if (tag != null) {
            if (tag.getAction() == Action.ACCEPT || tag.getAction() == Action.CSSHANDLER) {
                return true;
            } else if (tag.getAction() == Action.REMOVE) {
                removalStack++;
            }
        } else { // 如果没有定义的标记
            attributes.removeAllAttributes();
        }
        return false;
    }

    protected boolean handleEmptyTag(QName element, XMLAttributes attributes) {
        Tag tag = tagRules.get(element.rawname);
        if (tag != null) {
            if (tag.getAction() == Action.ACCEPT || tag.getAction() == Action.CSSHANDLER) {
                return true;
            }
        } else { // 如果没有定义的标记
            attributes.removeAllAttributes();
        }
        return false;
    }

    private boolean elementAccepted(QName element) {
        Tag tag = tagRules.get(element.rawname);
        boolean accept = false;

        if (tag != null) {
            Action action = tag.getAction();
            if (action == Action.ACCEPT || action == Action.CSSHANDLER) {
                accept = true;
            } else if (action == Action.REMOVE) {
                removalStack--;
            }
        }
        return accept;
    }

    /** Empty element. */
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (removalStack == 0 && handleEmptyTag(element, attributes)) {
            fSeenRootElement = true;
            printEmptyElement(element, attributes);
            super.emptyElement(element, attributes, augs);
        }
    }

    /** Comment. */
    public void comment(XMLString text, Augmentations augs) throws XNIException {

        if (removalStack == 0 && !fPolicy.removeComment) {
            if (fSeenRootElement) {
                fwriterBuffer.append("\n");
            }
            fwriterBuffer.append("<!--");
            printCharacters(text, false);
            fwriterBuffer.append("-->");
            if (!fSeenRootElement) {
                fwriterBuffer.append("\n");
            }
        }

    } // comment(XMLString,Augmentations)

    /** Processing instruction. */
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            if (fPrintChars) {
                printCharacters(data, fNormalize);
            }
            super.processingInstruction(target, data, augs);
        }
    }

    /** Characters. */
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            if (fPrintChars) {
                if (parserStyleTag && cssScanner != null) {
                    String css = new String(text.ch, text.offset, text.length);
                    fwriterBuffer.append(cssScanner.scanStyleSheet(css, fPolicy.maxCssInputSize, false));
                } else {
                    printCharacters(text, fNormalize);
                }
            }
            super.characters(text, augs);
        }
    }

    /** Ignorable whitespace. */
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.ignorableWhitespace(text, augs);
        }
    }

    /**
     * Start general entity. </br> <li>如果是非法的html字符， 那么直接删除， 这个是为了安全起见, <li>避免特殊的字符集攻击<li>另外windows下一些特殊字符也可以被引起攻击。
     * 这个地方需要好好再研究下。
     */
    public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs)
                                                                                                              throws XNIException {
        fPrintChars = false;
        if (removalStack == 0) {
            if (name.startsWith("#")) {
                try {
                    boolean hex = name.startsWith("#x");
                    int offset = hex ? 2 : 1;
                    int base = hex ? 16 : 10;
                    int value = Integer.parseInt(name.substring(offset), base);
                    char[] entity = HtmlFastEntities.HTML40.getEntity((char) value);
                    if (entity != null) {
                        name = new String(entity);
                    }
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        }
        fwriterBuffer.append(name);
        super.startGeneralEntity(name, id, encoding, augs);
    }

    /** Text declaration. */
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.textDecl(version, encoding, augs);
        }
    }

    /** End general entity. */

    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        fPrintChars = true;
        if (removalStack == 0) {
            super.endGeneralEntity(name, augs);
        }
    }

    /** Start CDATA section. */
    public void startCDATA(Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.startCDATA(augs);
        }
    }

    /** End CDATA section. */
    public void endCDATA(Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.endCDATA(augs);
        }
    }

    /** End prefix mapping. */
    public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.endPrefixMapping(prefix, augs);
        }
    }

    protected void printCharacters(XMLString text, boolean normalize) {
        if (normalize) {
            for (int i = 0; i < text.length; i++) {
                char c = text.ch[text.offset + i];
                // 过滤掉过多的空格---' ', 制表符号---\t， 和回车---\n
                if (c != '\n' || c != ' ' || c != '\t') {
                    char[] entity = HtmlFastEntities.HTML40.getEntity(c);
                    if (entity != null) {
                        fwriterBuffer.append(entity);
                    } else {
                        fwriterBuffer.append(c);
                    }
                } else {
                    fwriterBuffer.append("\n");
                }
            }
        } else {
            fwriterBuffer.append(text.ch, text.offset, text.length);
        }
    }

    // print element
    protected void printStartElement(QName element, XMLAttributes attributes) {
        fwriterBuffer.append('<');
        fwriterBuffer.append(element.rawname);
        int attrCount = attributes != null ? attributes.getLength() : 0;
        Tag tag = tagRules.get(element.rawname);
        if (tag.action == Action.CSSHANDLER && element.rawname.equalsIgnoreCase("style")) {
            parserStyleTag = true;
        }
        for (int i = 0; i < attrCount; i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);
            // print allowed tag attribute
            Attribute attr = tag.getAllowedAttributes().get(name);
            if (attr != null) {
                // spec attribute should be validated that defined in policy xml
                // file.
                boolean canAcceptAttr = false;
                List<Pattern> expr = attr.allowedRegExp;
                // 属性值是空的没有意义， 不输出
                if (expr != null && !value.equals("")) {
                    for (Pattern pattern : expr) {
                        if (pattern.matcher(value).matches()) {
                            canAcceptAttr = true;
                            break;
                        }
                    }
                } else {// 没有规则定义规则的， style 不能定义规则,否则导致css 扫描失败
                    if (fPolicy.enableStyleScan) {
                        if (attr.restrictAttribute == RestrictAttribute.STYLE && cssScanner != null) {
                            value = cssScanner.scanStyleSheet(value, fPolicy.maxCssInputSize, true);
                        }
                    }
                    canAcceptAttr = true;
                }
                if (canAcceptAttr) {
                    fwriterBuffer.append(' ');
                    fwriterBuffer.append(name);
                    fwriterBuffer.append("=\"");
                    printAttributeValue(value);
                    fwriterBuffer.append('"');
                }
            }

        }
        fwriterBuffer.append('>');
    }

    /**
     * @author jinhua.wangjh 20090923
     * @description 把自闭合标签的打印独立出来处理。
     */
    protected void printEmptyElement(QName element, XMLAttributes attributes) {
        this.printStartElement(element, attributes);
        this.fwriterBuffer.insert(this.fwriterBuffer.length() - 1, '/');
    }

    /**
     * 输出属性值并转义符号(quota): <code>“</code>
     * 
     * @param text
     */
    private void printAttributeValue(String text) {
        int length = text.length();
        for (int j = 0; j < length; j++) {
            char c = text.charAt(j);
            if (c == '"') {
                fwriterBuffer.append("&quot;");
            } else {
                fwriterBuffer.append(c);
            }
        }
    }

    private void printEndElement(QName element) {
        Tag tag = tagRules.get(element.rawname);
        if (tag.action == Action.CSSHANDLER && element.rawname.equalsIgnoreCase("style")) {
            parserStyleTag = false;
        }
        fwriterBuffer.append("</");
        fwriterBuffer.append(element.rawname);
        fwriterBuffer.append('>');
    }
}
