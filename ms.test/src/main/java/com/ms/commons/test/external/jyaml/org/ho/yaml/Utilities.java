/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;

/**
 * @author zxc Apr 14, 2013 12:35:26 AM
 */
@SuppressWarnings("rawtypes")
public class Utilities {

    public static Object decodeSimpleType(String content) {
        if ("~".equals(content)) {
            return null;
        } else {
            try {
                return new Integer(content); // return integer
            } catch (NumberFormatException e) {
            }
            try {
                return new Double(content);
            } catch (NumberFormatException e) {
            } catch (NullPointerException e) {
            }
            // UMM... this surprised me, but the double parser can throw NullPointerExceptions
            // on non-null input, this this is buggy

            if ("true".equalsIgnoreCase(content) || "false".equalsIgnoreCase(content)) return new Boolean(content); // return
                                                                                                                    // boolean
            else return content; // return String
        }
    }

    public static String quote(Object value) {
        return "\"" + value + "\"";
    }

    public static String stringify(Object value) {
        return stringify(value, "");
    }

    public static String escape(String text) {
        text = text.replace("\\", "\\\\");
        text = text.replace("\b", "\\b");
        text = text.replace("\0", "\\0");
        text = text.replace("\t", "\\t");
        text = text.replace("\"", "\\\"");
        return text;
    }

    public static String unescape(String text) {
        if (text == null) return null;
        StringBuffer sb = new StringBuffer(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i != text.length() - 1) {
                char d = text.charAt(i + 1);
                switch (d) {
                    case 'b':
                        sb.append('\b');
                        break;
                    case '0':
                        sb.append('\0');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    default:
                        sb.append(("" + c) + d);
                }
                i++;
            } else sb.append(c);
        }
        return sb.toString();
    }

    public static String stringify(Object value, String indent) {
        String text = value.toString();

        // special handling for multiple lines
        if (text.indexOf('\n') != -1) {
            if (text.length() == 1) return quote("\\n");
            StringBuffer sb = new StringBuffer();
            sb.append("|");
            String lines[] = text.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                sb.append("\n" + indent + line);
            }
            if (text.charAt(text.length() - 1) == '\n') sb.append("\n" + indent);
            return sb.toString();
        } else if ("".equals(text)) {
            return quote(text);
        } else {
            String indicators = ":[]{},\"'|*&";
            boolean quoteIt = false;
            for (char c : indicators.toCharArray())
                if (text.indexOf(c) != -1) {
                    quoteIt = true;
                    break;
                }
            if (text.trim().length() != text.length()) quoteIt = true;
            if (isNumeric(text)) quoteIt = true;
            if (quoteIt) {
                text = escape(text);
                text = quote(text);
            } else text = text;
            return text;
        }
    }

    static boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (Exception e) {
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    static String quote(String value) {
        return "\"" + value + "\"";
    }

    public static Object convertType(String value, Class type) {
        if ("~".equals(value)) return null;
        else if (type == Integer.class || type == Integer.TYPE) return new Integer(value.toString());
        else if (type == String.class) {
            return (String) value;
        } else if (type == Long.class || type == Long.TYPE) return new Long(value.toString());
        else if (type == Short.class || type == Short.TYPE) return new Short(value.toString());
        else if (type == Double.class || type == Double.TYPE) return new Double(value.toString());
        else if (type == Boolean.class || type == Boolean.TYPE) return new Boolean(value.toString());
        else if (type == Character.class || type == Character.TYPE) {
            value = value;
            return new Character(value.charAt(0));
        } else return decodeSimpleType(value);
    }

    public static Class getWrapperClass(Class type) {
        if (Integer.TYPE == type) return Integer.class;
        else if (Double.TYPE == type) return Double.class;
        else if (Float.TYPE == type) return Float.class;
        else if (Boolean.TYPE == type) return Boolean.class;
        else if (Character.TYPE == type) return Character.class;
        else if (Byte.TYPE == type) return Byte.class;
        else if (Long.TYPE == type) return Long.class;
        else if (Short.TYPE == type) return Short.class;
        else if (Character.TYPE == type) return Character.class;
        else throw new YamlException(type + " is not a primitive type.");
    }

    public static boolean classEquals(Class one, Class other) {
        if (one == other) return true;
        if (one != null && other != null) if (one.isPrimitive() || other.isPrimitive()) if (one.isPrimitive()) {
            return getWrapperClass(one) == other;
        } else return one == getWrapperClass(other);
        return false;

    }

    public static boolean same(Object one, Object other) {
        if (one != null) {
            return one.equals(other);
        } else if (other != null) return other.equals(one);
        else return true;
    }

}
