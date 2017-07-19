/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.security.xss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ms.commons.fasttext.security.xss.model.Action;
import com.ms.commons.fasttext.security.xss.model.Attribute;
import com.ms.commons.fasttext.security.xss.model.RestrictAttribute;
import com.ms.commons.fasttext.security.xss.model.Tag;

/**
 * <pre>
 * &lt;directives&gt;
 *         &lt;directive name=&quot;maxInputSize&quot; value=&quot;100000&quot; /&gt;
 *         &lt;directive name=&quot;maxCssInputSize&quot; value=&quot;1000&quot; /&gt;
 *         &lt;directive name=&quot;enableStyleScan&quot; value=&quot;true&quot; /&gt;
 *         &lt;directive name=&quot;removeComment&quot; value=&quot;true&quot; /&gt;
 *         &lt;directive name=&quot;elemsLower&quot; value=&quot;true&quot; /&gt;
 *         &lt;directive name=&quot;attrsLower&quot; value=&quot;true&quot; /&gt;
 * &lt;/directives&gt;
 * </pre>
 * 
 * @author zxc Apr 12, 2013 3:29:18 PM
 */
public class Policy {

    public static final String  DEFAULT_STRICT_POLICY_URI  = "resources/strict-xss.xml";
    public static final String  DEFAULT_LOOSE_POLICY_URI   = "resources/loose-xss.xml";
    public static final String  DEFAULT_ALIPAY_POLICY_URI  = "resources/alipay-xss.xml";
    public static final String  DEFAULT_TAOBAO_POLICY_URI  = "resources/taobao-xss.xml";
    public static final String  DEFAULT_GONGLUE_POLICY_URI = "resources/gonglue-xss.xml";

    private Map<String, String> directives;
    private Map<String, Tag>    tagRules;

    public int                  maxInputSize               = 10000000;
    public int                  maxCssInputSize            = 100000;
    public boolean              enableStyleScan            = true;
    public boolean              removeComment              = false;
    public boolean              attrsLower                 = true;
    public boolean              elemsLower                 = true;
    public boolean              usePurifier                = false;
    public boolean              usePreXMLValid             = true;

    private Policy(InputStream is) throws PolicyException {
        try {
            parsePolicy(is);
        } catch (ParserConfigurationException e) {
            throw new PolicyException(e);
        } catch (SAXException e) {
            throw new PolicyException(e);
        } catch (IOException e) {
            throw new PolicyException(e);
        }
    }

    private Policy(URL url) throws PolicyException {
        try {
            parsePolicy(url.openStream());
        } catch (ParserConfigurationException e) {
            throw new PolicyException(e);
        } catch (SAXException e) {
            throw new PolicyException(e);
        } catch (IOException e) {
            throw new PolicyException(e);
        }
    }

    public static Policy getLoosePolicyInstance() throws PolicyException {
        InputStream is = Policy.class.getResourceAsStream(DEFAULT_LOOSE_POLICY_URI);
        return new Policy(is);
    }

    public static Policy getStrictPolicyInstance() throws PolicyException {
        InputStream is = Policy.class.getResourceAsStream(DEFAULT_STRICT_POLICY_URI);
        return new Policy(is);
    }

    public static Policy getCustomerPolicyInstance(InputStream is) throws PolicyException {
        return new Policy(is);
    }

    /**
     * 获得内置定义的资源: 支付宝/掏宝等定制要求资源
     * 
     * @param uri
     * @return
     * @throws PolicyException
     */
    public static Policy getInternalDefinePolicyInstance(String uri) throws PolicyException {
        InputStream is = Policy.class.getResourceAsStream(uri);
        return new Policy(is);
    }

    private void parsePolicy(InputStream is) throws ParserConfigurationException, SAXException, IOException,
                                            PolicyException {
        if (is == null) {
            throw new PolicyException("gived InputStream is null");
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = db.parse(is);
        Element root = dom.getDocumentElement();
        // directives
        Element directiveListNode = (Element) root.getElementsByTagName("directives").item(0);
        this.directives = parseDirectives(directiveListNode);

        // common-regexps
        Element regexpsListNode = (Element) root.getElementsByTagName("common-regexps").item(0);
        Map<String, Pattern> regexps = parseRegexps(regexpsListNode);

        // tag-attributes
        Element tagAttributesListNode = (Element) root.getElementsByTagName("tag-attributes").item(0);
        Map<String, Map<String, Attribute>> tagAttributes = parseTagAttributes(tagAttributesListNode, regexps);

        // tag-rules
        Element tagRulesListNode = (Element) root.getElementsByTagName("tag-rules").item(0);
        this.tagRules = parseTagRules(tagRulesListNode);

        // attach attribute to tag
        Collection<Tag> tagSet = tagRules.values();
        for (Tag tag : tagSet) {
            String ref = tag.getAttrRef();
            if (isEmpty(ref)) {
                ref = tag.getName();
            }
            ref = toElemsCase(ref);
            tag.setAllowedAttributes(tagAttributes.get(ref));
        }
        // attach reg to attribute
    }

    /**
     * 读取正则表达式
     * 
     * @param root
     * @return
     * @throws PolicyException
     */
    private Map<String, Pattern> parseRegexps(Element root) throws PolicyException {
        Map<String, Pattern> regexps = new HashMap<String, Pattern>();
        NodeList regNodes = root.getElementsByTagName("regexp");
        for (int i = 0; i < regNodes.getLength(); i++) {
            Element element = (Element) regNodes.item(i);
            String name = element.getAttribute("name");
            String value = element.getAttribute("value");

            if (!isEmpty(name) && !isEmpty(value)) {
                Pattern pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
                regexps.put(name, pattern);
            } else {
                throw new PolicyException("error regexps name or value");
            }
        }
        return regexps;
    }

    private Map<String, Tag> parseTagRules(Element root) throws PolicyException {
        Map<String, Tag> tagRules = new HashMap<String, Tag>();
        NodeList tagNodes = root.getElementsByTagName("tag");
        for (int i = 0; i < tagNodes.getLength(); i++) {
            Element element = (Element) tagNodes.item(i);
            String tagName = element.getAttribute("name");
            tagName = toElemsCase(tagName);
            String action = element.getAttribute("action");
            Tag tag = new Tag(tagName);
            if ("remove".equalsIgnoreCase(action)) {
                tag.setAction(Action.REMOVE);
            } else if ("accept".equalsIgnoreCase(action)) {
                tag.setAction(Action.ACCEPT);
            } else if ("csshandler".equalsIgnoreCase(action)) {
                tag.setAction(Action.CSSHANDLER);
            } else {
                throw new PolicyException("error action: " + action);
            }
            // 把标记属性写入标记对象
            String attributes = element.getAttribute("attributes");
            tag.setAttrRef(attributes);
            tagRules.put(tagName, tag);
        }
        return tagRules;
    }

    private Map<String, Map<String, Attribute>> parseTagAttributes(Element root, Map<String, Pattern> regexps)
                                                                                                              throws PolicyException {
        Map<String, Map<String, Attribute>> tagAttributes = new HashMap<String, Map<String, Attribute>>();
        NodeList attributesNodes = root.getElementsByTagName("attributes");
        for (int i = 0; i < attributesNodes.getLength(); i++) {
            Element element = (Element) attributesNodes.item(i);
            String tagName = element.getAttribute("name");
            tagName = toElemsCase(tagName);
            Map<String, Attribute> attrs = new HashMap<String, Attribute>();
            tagAttributes.put(tagName, attrs);

            NodeList attributeNodes = element.getElementsByTagName("attribute");
            for (int j = 0; j < attributeNodes.getLength(); j++) {
                Element son = (Element) attributeNodes.item(j);
                String attrName = son.getAttribute("name");
                attrName = toAttrsCase(attrName);
                Attribute attr = new Attribute();
                attr.name = attrName;
                if ("style".equals(attrName.toLowerCase())) {
                    attr.restrictAttribute = RestrictAttribute.STYLE;
                } else if ("background".equals(attrName.toLowerCase())) {
                    attr.restrictAttribute = RestrictAttribute.BACKGROUND;
                }
                // parser attribute validating regular expression list
                attr.allowedRegExp = parseAttributeRegular(son, regexps);
                attrs.put(attrName, attr);
            }
        }
        return tagAttributes;
    }

    /**
     * <pre>
     *            &lt;attribute name=&quot;src&quot;&gt;
     *                 &lt;regexp-list&gt;
     *                     &lt;regexp name=&quot;onsiteURL&quot; /&gt;
     *                     &lt;regexp name=&quot;offsiteURL&quot; /&gt;
     *                     &lt;regexp value=&quot;$http://&quot; /&gt;
     *                 &lt;/regexp-list&gt;
     *             &lt;/attribute&gt;
     * </pre>
     * 
     * @param root
     * @param regexps
     * @return
     * @throws PolicyException
     */
    private List<Pattern> parseAttributeRegular(Element root, Map<String, Pattern> regexps) throws PolicyException {
        List<Pattern> regList = null;
        if (root.hasChildNodes()) {
            regList = new ArrayList<Pattern>();
            Element e = (Element) root.getElementsByTagName("regexp-list").item(0);
            NodeList regexprNode = e.getElementsByTagName("regexp");
            for (int i = 0; i < regexprNode.getLength(); i++) {
                Element element = (Element) regexprNode.item(i);
                String regularName = element.getAttribute("name");
                String regularValue = element.getAttribute("value");
                if (!isEmpty(regularName)) {
                    regList.add(regexps.get(regularName));
                } else if (!isEmpty(regularValue)) {
                    Pattern pattern = Pattern.compile(regularValue, Pattern.CASE_INSENSITIVE);
                    regList.add(pattern);
                }
            }
        }
        return regList;
    }

    private Map<String, String> parseDirectives(Element root) {
        Map<String, String> directives = new HashMap<String, String>();
        NodeList directiveNodes = root.getElementsByTagName("directive");
        for (int i = 0; i < directiveNodes.getLength(); i++) {
            Element element = (Element) directiveNodes.item(i);
            String name = element.getAttribute("name");
            String value = element.getAttribute("value");
            directives.put(name, value);
        }
        // set some to field
        maxInputSize = Integer.parseInt(directives.get("maxInputSize"));
        maxCssInputSize = Integer.parseInt(directives.get("maxCssInputSize"));
        enableStyleScan = Boolean.parseBoolean(directives.get("enableStyleScan"));
        removeComment = Boolean.parseBoolean(directives.get("removeComment"));
        attrsLower = Boolean.parseBoolean(directives.get("attrsLower"));
        elemsLower = Boolean.parseBoolean(directives.get("elemsLower"));
        usePurifier = Boolean.parseBoolean(directives.get("usePurifier"));
        usePreXMLValid = Boolean.parseBoolean(directives.get("usePreXMLValid"));
        return directives;
    }

    public Map<String, String> getDirectives() {
        return directives;
    }

    public Map<String, Tag> getTagRules() {
        return tagRules;
    }

    public boolean isEmpty(String s) {
        if (s == null || s.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public String toAttrsCase(String s) {
        if (attrsLower) {
            return s.toLowerCase();
        } else {
            return s.toUpperCase();
        }
    }

    public String toElemsCase(String s) {
        if (elemsLower) {
            return s.toLowerCase();
        } else {
            return s.toUpperCase();
        }
    }
}
