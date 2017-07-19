package com.ms.commons.weixin.tools;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Tools {

    private static final String UTF_8 = "utf-8";
    private static final String EMPTY = "";

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static long toLong(String str) {
        return toLong(str, 0L);
    }

    public static long toLong(String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    public static String getSuffix(String name) {
        if (isEmpty(name)) {
            return EMPTY;
        }
        int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return EMPTY;
        }
        return name.substring(dot + 1);
    }

    public static String createNoncestr(int length) {
        if (length <= 0) {
            length = 16;
        }
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder res = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            res.append(chars.charAt(rd.nextInt(chars.length() - 1)));
        }
        return res.toString();
    }

    public static String createNoncestr() {
        return createNoncestr(16);
    }

    public static String getTime() {
        return "" + (System.currentTimeMillis() / 1000);
    }

    public static String asXML(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return EMPTY;
        }
        Element root = new Element("xml");
        Set<String> keys = map.keySet();
        for (String key : keys) {
            String value = map.get(key);
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            Element child = new Element(key);
            child.setText(value);
            root.addContent(child);
        }
        Document doc = new Document(root);
        StringWriter stringWriter = new StringWriter();
        XMLOutputter xMLOutputter = new XMLOutputter();
        xMLOutputter.setFormat(Format.getPrettyFormat());
        try {
            xMLOutputter.output(doc, stringWriter);
        } catch (IOException e) {
        }
        return stringWriter.toString();
    }

    @SuppressWarnings("rawtypes")
    public static Map<String, String> paserXML(String body) {
        Map<String, String> result = new HashMap<String, String>();
        SAXBuilder builder = new SAXBuilder();
        Document document = null;
        try {
            document = builder.build(new StringReader(body));
        } catch (JDOMException e) {
            return Collections.emptyMap();
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        Element rootNode = document.getRootElement();
        List children = rootNode.getChildren();
        for (Object object : children) {
            if (object instanceof Element) {
                String text = ((Element) object).getText();
                String name = ((Element) object).getName();
                result.put(name, text);
            }
        }
        return result;
    }

    /**
     * 注意！进行urlencode时要将空格转化为%20而不是+
     * 
     * @param map
     * @param encode
     * @return
     */
    public static String sortAndjoin(Map<String, String> map, boolean encode) {
        Set<String> keySet = map.keySet();
        List<String> keys = new ArrayList<String>(keySet);
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder(1000);
        boolean first = true;
        for (String key : keys) {
            String value = map.get(key);
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            if (encode) {
                value = encode(value);
            }
            // value = StringUtils.replace(value, "+", "%20");
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }

    public static String encode(String url) {
        try {
            return URLEncoder.encode(url, UTF_8);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    public static void main(String[] args) {
        System.out.println(getSuffix("abc.123"));
    }
}
