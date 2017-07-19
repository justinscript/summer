/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.classloader.manifest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * COPIED FROM JDK5 since jdk6 in mac os x has buggie
 * 
 * @author zxc Apr 13, 2013 11:11:11 PM
 */
@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
public class CopiedJDK5Attributes implements Map<Object, Object>, Cloneable {

    protected Map<Object, Object> map;

    public CopiedJDK5Attributes() {
        this(11);
    }

    public CopiedJDK5Attributes(int size) {
        map = new HashMap(size);
    }

    public CopiedJDK5Attributes(CopiedJDK5Attributes attr) {
        map = new HashMap(attr);
    }

    public Object get(Object name) {
        return map.get(name);
    }

    public String getValue(String name) {
        return (String) get(new CopiedJDK5Attributes.Name((String) name));
    }

    public String getValue(Name name) {
        return (String) get(name);
    }

    public Object put(Object name, Object value) {
        return map.put((CopiedJDK5Attributes.Name) name, (String) value);
    }

    public String putValue(String name, String value) {
        return (String) put(new Name(name), value);
    }

    public Object remove(Object name) {
        return map.remove(name);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public boolean containsKey(Object name) {
        return map.containsKey(name);
    }

    public void putAll(Map<?, ?> attr) {
        // ## javac bug?
        if (!CopiedJDK5Attributes.class.isInstance(attr)) throw new ClassCastException();
        for (Map.Entry<?, ?> me : (attr).entrySet())
            put(me.getKey(), me.getValue());
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<Object> keySet() {
        return map.keySet();
    }

    public Collection<Object> values() {
        return map.values();
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return map.entrySet();
    }

    public boolean equals(Object o) {
        return map.equals(o);
    }

    public int hashCode() {
        return map.hashCode();
    }

    public Object clone() {
        return new CopiedJDK5Attributes(this);
    }

    void write(DataOutputStream os) throws IOException {
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            StringBuffer buffer = new StringBuffer(((Name) e.getKey()).toString());
            buffer.append(": ");

            String value = (String) e.getValue();
            if (value != null) {
                byte[] vb = value.getBytes("UTF8");
                value = new String(vb, 0, 0, vb.length);
            }
            buffer.append(value);

            buffer.append("\r\n");
            CopiedJDK5Manifest.make72Safe(buffer);
            os.writeBytes(buffer.toString());
        }
        os.writeBytes("\r\n");
    }

    void writeMain(DataOutputStream out) throws IOException {
        // write out the *-Version header first, if it exists
        String vername = Name.MANIFEST_VERSION.toString();
        String version = getValue(vername);
        if (version == null) {
            vername = Name.SIGNATURE_VERSION.toString();
            version = getValue(vername);
        }

        if (version != null) {
            out.writeBytes(vername + ": " + version + "\r\n");
        }

        // write out all attributes except for the version
        // we wrote out earlier
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String name = ((Name) e.getKey()).toString();
            if ((version != null) && !(name.equalsIgnoreCase(vername))) {

                StringBuffer buffer = new StringBuffer(name);
                buffer.append(": ");

                String value = (String) e.getValue();
                if (value != null) {
                    byte[] vb = value.getBytes("UTF8");
                    value = new String(vb, 0, 0, vb.length);
                }
                buffer.append(value);

                buffer.append("\r\n");
                CopiedJDK5Manifest.make72Safe(buffer);
                out.writeBytes(buffer.toString());
            }
        }
        out.writeBytes("\r\n");
    }

    void read(CopiedJDK5Manifest.FastInputStream is, byte[] lbuf) throws IOException {
        String name = null, value = null;
        byte[] lastline = null;

        int len;
        while ((len = is.readLine(lbuf)) != -1) {
            boolean lineContinued = false;
            if (lbuf[--len] != '\n') {
                throw new IOException("line too long");
            }
            if (len > 0 && lbuf[len - 1] == '\r') {
                --len;
            }
            if (len == 0) {
                break;
            }
            int i = 0;
            if (lbuf[0] == ' ') {
                // continuation of previous line
                if (name == null) {
                    throw new IOException("misplaced continuation line");
                }
                lineContinued = true;
                byte[] buf = new byte[lastline.length + len - 1];
                System.arraycopy(lastline, 0, buf, 0, lastline.length);
                System.arraycopy(lbuf, 1, buf, lastline.length, len - 1);
                if (is.peek() == ' ') {
                    lastline = buf;
                    continue;
                }
                value = new String(buf, 0, buf.length, "UTF8");
                lastline = null;
            } else {
                while (lbuf[i++] != ':') {
                    if (i >= len) {
                        throw new IOException("invalid header field");
                    }
                }
                if (lbuf[i++] != ' ') {
                    throw new IOException("invalid header field");
                }
                name = new String(lbuf, 0, 0, i - 2);
                if (is.peek() == ' ') {
                    lastline = new byte[len - i];
                    System.arraycopy(lbuf, i, lastline, 0, len - i);
                    continue;
                }
                value = new String(lbuf, i, len - i, "UTF8");
            }
            try {
                if ((putValue(name, value) != null) && (!lineContinued)) {
                    Logger.getLogger("java.util.jar").warning("Duplicate name in Manifest: " + name);
                }
            } catch (IllegalArgumentException e) {
                throw new IOException("invalid header field name: " + name);
            }
        }
    }

    public static class Name {

        private String name;
        private int    hashCode = -1;

        public Name(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            if (!isValid(name)) {
                throw new IllegalArgumentException(name);
            }
            this.name = name.intern();
        }

        private static boolean isValid(String name) {
            int len = name.length();
            if (len > 70 || len == 0) {
                return false;
            }
            for (int i = 0; i < len; i++) {
                if (!isValid(name.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        private static boolean isValid(char c) {
            return isAlpha(c) || isDigit(c) || c == '_' || c == '-';
        }

        private static boolean isAlpha(char c) {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        }

        private static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        public boolean equals(Object o) {
            if (o instanceof Name) {
                return name.equals(((Name) o).name);
            } else {
                return false;
            }
        }

        public int hashCode() {
            if (hashCode == -1) {
                hashCode = name.hashCode();// MODIFIED
            }
            return hashCode;
        }

        public String toString() {
            return name;
        }

        public static final Name MANIFEST_VERSION         = new Name("Manifest-Version");

        public static final Name SIGNATURE_VERSION        = new Name("Signature-Version");

        public static final Name CONTENT_TYPE             = new Name("Content-Type");

        public static final Name CLASS_PATH               = new Name("Class-Path");

        public static final Name MAIN_CLASS               = new Name("Main-Class");

        public static final Name SEALED                   = new Name("Sealed");

        public static final Name EXTENSION_LIST           = new Name("Extension-List");

        public static final Name EXTENSION_NAME           = new Name("Extension-Name");

        public static final Name EXTENSION_INSTALLATION   = new Name("Extension-Installation");

        public static final Name IMPLEMENTATION_TITLE     = new Name("Implementation-Title");

        public static final Name IMPLEMENTATION_VERSION   = new Name("Implementation-Version");

        public static final Name IMPLEMENTATION_VENDOR    = new Name("Implementation-Vendor");

        public static final Name IMPLEMENTATION_VENDOR_ID = new Name("Implementation-Vendor-Id");

        public static final Name IMPLEMENTATION_URL       = new Name("Implementation-URL");

        public static final Name SPECIFICATION_TITLE      = new Name("Specification-Title");

        public static final Name SPECIFICATION_VERSION    = new Name("Specification-Version");

        public static final Name SPECIFICATION_VENDOR     = new Name("Specification-Vendor");
    }
}
