/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.HTMLElements;
import org.cyberneko.html.filters.DefaultFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ms.commons.fasttext.codec.HtmlFastEntities;
import com.ms.commons.fasttext.security.xss.ScanException;

/**
 * @author zxc Apr 12, 2013 3:37:23 PM
 */
public class HtmlTagBalance implements HtmlConverter {

    protected static final String NAMES_ELEMS_PROPERTY = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS_PROPERTY = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String FILTERS_PROPERTY     = "http://cyberneko.org/html/properties/filters";

    public String convert(String html) {
        FragmentXppParser parser = new FragmentXppParser();
        parser.setProperty(NAMES_ELEMS_PROPERTY, "lower");
        parser.setProperty(NAMES_ATTRS_PROPERTY, "lower");

        StringBuilder buffer = new StringBuilder(html.length() << 1);
        HtmlFilterDocumentHandler filter = new HtmlFilterDocumentHandler(buffer);
        XMLDocumentFilter[] filters = { filter };
        parser.setProperty(FILTERS_PROPERTY, filters);
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

    class HtmlFilterDocumentHandler extends DefaultFilter {

        private StringBuilder fwriterBuffer;
        protected boolean     isPrintChars;
        protected boolean     isNormalize;

        public HtmlFilterDocumentHandler(StringBuilder writerBuffer) {
            this.fwriterBuffer = writerBuffer;

        }

        public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs)
                                                                                                                      throws XNIException {
            isNormalize = true;
            isPrintChars = true;
            super.startDocument(locator, encoding, nscontext, augs);
        }

        /** Start document. */
        public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException {
            startDocument(locator, encoding, null, augs);
        }

        /** doctype process */
        public void doctypeDecl(String root, String publicId, String systemId, Augmentations augs) throws XNIException {
            String docType = "!DOCTYPE ";
            String publicTag = "PUBLIC ";
            String quotation = "\"";
            fwriterBuffer.append("<").append(docType).append(root).append(" ").append(publicTag).append(quotation).append(publicId).append(quotation).append(" ").append(quotation).append(systemId).append(quotation);
            if (augs != null) {
                fwriterBuffer.append(" ").append(quotation).append(augs.toString()).append(quotation);
            }
            fwriterBuffer.append(">");
        }

        /** Start element. */

        public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
            if (HTMLElements.getElement(element.rawname).code == HTMLElements.UNKNOWN) {
                return; // 未知标签
            }
            isNormalize = !HTMLElements.getElement(element.rawname).isSpecial();
            printStartElement(element, attributes, false);
            super.startElement(element, attributes, augs);

        }

        /** End element. */

        public void endElement(QName element, Augmentations augs) throws XNIException {
            if (HTMLElements.getElement(element.rawname).code == HTMLElements.UNKNOWN) {
                return; // 未知标签
            }
            isNormalize = true;
            printEndElement(element);
            super.endElement(element, augs);

        } // endElement(QName,Augmentations)

        /** Empty element. */
        public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
            if (HTMLElements.getElement(element.rawname).code == HTMLElements.UNKNOWN) {
                return; // 未知标签
            }
            printStartElement(element, attributes, true);
            super.emptyElement(element, attributes, augs);

        } // emptyElement(QName,XMLAttributes,Augmentations)

        /** Comment. */
        public void comment(XMLString text, Augmentations augs) throws XNIException {

        } // comment(XMLString,Augmentations)

        /** Processing instruction. */
        public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {

            if (isPrintChars) {
                printCharacters(data, isNormalize);
            }
            super.processingInstruction(target, data, augs);

        } // processingInstruction(String,XMLString,Augmentations)

        /** Characters. */
        public void characters(XMLString text, Augmentations augs) throws XNIException {
            if (isPrintChars) {
                printCharacters(text, isNormalize);
            }
            super.characters(text, augs);

        } // characters(XMLString,Augmentations)

        /**
         * Start general entity. </br> <li>如果是非法的html字符， 那么直接删除， 这个是为了安全起见, <li>避免特殊的字符集攻击<li>另外windows下一些特殊字符也可以被引起攻击。
         * 这个地方需要好好再研究下。
         */
        public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs)
                                                                                                                  throws XNIException {
            isPrintChars = false;
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

            fwriterBuffer.append(name);
            super.startGeneralEntity(name, id, encoding, augs);
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
        protected void printStartElement(QName element, XMLAttributes attributes, boolean isEmptyElement) {
            fwriterBuffer.append('<');
            fwriterBuffer.append(element.rawname);
            int attrCount = attributes != null ? attributes.getLength() : 0;

            for (int i = 0; i < attrCount; i++) {
                // print allowed tag attribute
                fwriterBuffer.append(' ');
                fwriterBuffer.append(attributes.getQName(i));
                if (attributes.getValue(i) == null || attributes.getValue(i).trim().length() == 0) {
                    continue;
                    // as "checked","disabled"
                }
                fwriterBuffer.append("=\"");
                printAttributeValue(attributes.getValue(i));
                fwriterBuffer.append('"');
            }
            // 如果是非闭合元素， 那么补全标记
            if (isEmptyElement) {
                fwriterBuffer.append(" /");
            }
            fwriterBuffer.append('>');
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
            fwriterBuffer.append("</");
            fwriterBuffer.append(element.rawname);
            fwriterBuffer.append('>');
        }
    }

    public class FragmentXppParser extends HTMLConfiguration {

        protected static final String DOCUMENT_FRAGMENT_PROPERTY = "http://cyberneko.org/html/features/document-fragment";
        protected static final String DEFAULT_ENCODING_PROPERTY  = "http://cyberneko.org/html/properties/default-encoding";
        protected static final String DEFAULT_ENCODING_ALGORITHM = "UTF-8";

        public FragmentXppParser() {
            super();
            this.setFeature(DOCUMENT_FRAGMENT_PROPERTY, true);
            this.setProperty(DEFAULT_ENCODING_PROPERTY, DEFAULT_ENCODING_ALGORITHM);
        }

        public void parse(InputSource source) throws SAXException, IOException {

            try {
                String pubid = source.getPublicId();
                String sysid = source.getSystemId();
                String encoding = source.getEncoding();
                InputStream stream = source.getByteStream();
                Reader reader = source.getCharacterStream();

                XMLInputSource inputSource = new XMLInputSource(pubid, sysid, sysid);
                inputSource.setEncoding(encoding);
                inputSource.setByteStream(stream);
                inputSource.setCharacterStream(reader);

                parse(inputSource);
            } catch (XMLParseException e) {
                Exception ex = e.getException();
                if (ex != null) {
                    throw new SAXParseException(e.getMessage(), null, ex);
                }
                throw new SAXParseException(e.getMessage(), null);
            }
        } // end parse(InputSource source, DocumentFragment fragment)
    }
}
