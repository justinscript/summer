/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.extract;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
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

import com.ms.commons.fasttext.security.xss.ScanException;

/**
 * <code>
 * 线程安全, 从HTML抽取文本, 特殊标记的文本会被删除. 实体转化将会被取消。 所有文本以原始类型显示。
 * </code>
 * 
 * @author zxc Apr 12, 2013 3:38:24 PM
 */
public class HtmlExtractText implements ExtractText {

    public String getText(String src) {
        return convert(src);
    }

    protected static final String FILTERS_PROPERTY = "http://cyberneko.org/html/properties/filters";

    public String convert(String html) {
        if (html == null) {
            return null;
        }
        FragmentXppParser parser = new FragmentXppParser();
        StringBuilder buffer = new StringBuilder(html.length());
        HtmlFilterDocumentHandler filter = new HtmlFilterDocumentHandler(buffer, this);
        // 将当前实例传给HtmlFilterDocumentHandler是为了处理特殊标签(SCRIPT)不匹配时，会把后续内容全部当作
        // 标签内的value来处理。如<script><a><dev>aaa,则<a><dev>aaa会当作<script>的内容，所以要对它再一交
        // convert。即在HtmlFilterDocumentHandler的characters事件中调用convert
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

        private StringBuilder   fwriterBuffer;
        private boolean         isSpecial = false;
        private HtmlExtractText het;

        public HtmlFilterDocumentHandler(StringBuilder writerBuffer, HtmlExtractText het) {
            this.fwriterBuffer = writerBuffer;
            this.het = het;
        }

        /** Start element. */

        public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
            isSpecial = HTMLElements.getElement(element.rawname).isSpecial();
            super.startElement(element, attributes, augs);
        }

        /** Comment. */
        public void comment(XMLString text, Augmentations augs) throws XNIException {
            // skip it, do nothing
        } // comment(XMLString,Augmentations)

        /** Characters. */
        public void characters(XMLString text, Augmentations augs) throws XNIException {

            printCharacters(text);
            super.characters(text, augs);

        } // characters(XMLString,Augmentations)

        protected void printCharacters(XMLString text) {
            if (isSpecial) {
                fwriterBuffer.append(this.het.convert(new String(text.ch, text.offset, text.length)));
            } else {
                fwriterBuffer.append(text.ch, text.offset, text.length);
            }
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
}// end class HtmlExtractText
