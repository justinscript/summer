/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.classloader.manifest;

import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * COPIED FROM JDK5 since jdk6 in mac os x has buggie
 * 
 * @author zxc Apr 13, 2013 11:10:10 PM
 */
public class CopiedJDK5Manifest implements Cloneable {

    // manifest main attributes
    private CopiedJDK5Attributes attr    = new CopiedJDK5Attributes();

    // manifest entries
    @SuppressWarnings("rawtypes")
    private Map                  entries = new HashMap();

    public CopiedJDK5Manifest() {
    }

    public CopiedJDK5Manifest(InputStream is) throws IOException {
        read(is);
    }

    @SuppressWarnings("unchecked")
    public CopiedJDK5Manifest(CopiedJDK5Manifest man) {
        attr.putAll(man.getMainAttributes());
        entries.putAll(man.getEntries());
    }

    public CopiedJDK5Attributes getMainAttributes() {
        return attr;
    }

    @SuppressWarnings("unchecked")
    public Map<String, CopiedJDK5Attributes> getEntries() {
        return entries;
    }

    public CopiedJDK5Attributes getAttributes(String name) {
        return (CopiedJDK5Attributes) getEntries().get(name);
    }

    public void clear() {
        attr.clear();
        entries.clear();
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
    public void write(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        // Write out the main attributes for the manifest
        attr.writeMain(dos);
        // Now write out the pre-entry attributes
        Iterator it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            StringBuffer buffer = new StringBuffer("Name: ");
            String value = (String) e.getKey();
            if (value != null) {
                byte[] vb = value.getBytes("UTF8");
                value = new String(vb, 0, 0, vb.length);
            }
            buffer.append(value);
            buffer.append("\r\n");
            make72Safe(buffer);
            dos.writeBytes(buffer.toString());
            ((CopiedJDK5Attributes) e.getValue()).write(dos);
        }
        dos.flush();
    }

    static void make72Safe(StringBuffer line) {
        int length = line.length();
        if (length > 72) {
            int index = 70;
            while (index < length - 2) {
                line.insert(index, "\r\n ");
                index += 72;
                length += 3;
            }
        }
        return;
    }

    @SuppressWarnings("unchecked")
    public void read(InputStream is) throws IOException {
        // Buffered input stream for reading manifest data
        FastInputStream fis = new FastInputStream(is);
        // Line buffer
        byte[] lbuf = new byte[512];
        // Read the main attributes for the manifest
        attr.read(fis, lbuf);
        // Total number of entries, attributes read
        int ecount = 0, acount = 0;
        // Average size of entry attributes
        int asize = 2;
        // Now parse the manifest entries
        int len;
        String name = null;
        boolean skipEmptyLines = true;
        byte[] lastline = null;

        while ((len = fis.readLine(lbuf)) != -1) {
            if (lbuf[--len] != '\n') {
                throw new IOException("manifest line too long");
            }
            if (len > 0 && lbuf[len - 1] == '\r') {
                --len;
            }
            if (len == 0 && skipEmptyLines) {
                continue;
            }
            skipEmptyLines = false;

            if (name == null) {
                name = parseName(lbuf, len);
                if (name == null) {
                    throw new IOException("invalid manifest format");
                }
                if (fis.peek() == ' ') {
                    // name is wrapped
                    lastline = new byte[len - 6];
                    System.arraycopy(lbuf, 6, lastline, 0, len - 6);
                    continue;
                }
            } else {
                // continuation line
                byte[] buf = new byte[lastline.length + len - 1];
                System.arraycopy(lastline, 0, buf, 0, lastline.length);
                System.arraycopy(lbuf, 1, buf, lastline.length, len - 1);
                if (fis.peek() == ' ') {
                    // name is wrapped
                    lastline = buf;
                    continue;
                }
                name = new String(buf, 0, buf.length, "UTF8");
                lastline = null;
            }
            CopiedJDK5Attributes attr = getAttributes(name);
            if (attr == null) {
                attr = new CopiedJDK5Attributes(asize);
                entries.put(name, attr);
            }
            attr.read(fis, lbuf);
            ecount++;
            acount += attr.size();
            // XXX: Fix for when the average is 0. When it is 0,
            // you get an Attributes object with an initial
            // capacity of 0, which tickles a bug in HashMap.
            asize = Math.max(2, acount / ecount);

            name = null;
            skipEmptyLines = true;
        }
    }

    private String parseName(byte[] lbuf, int len) {
        if (toLower(lbuf[0]) == 'n' && toLower(lbuf[1]) == 'a' && toLower(lbuf[2]) == 'm' && toLower(lbuf[3]) == 'e'
            && lbuf[4] == ':' && lbuf[5] == ' ') {
            try {
                return new String(lbuf, 6, len - 6, "UTF8");
            } catch (Exception e) {
            }
        }
        return null;
    }

    private int toLower(int c) {
        return (c >= 'A' && c <= 'Z') ? 'a' + (c - 'A') : c;
    }

    public boolean equals(Object o) {
        if (o instanceof CopiedJDK5Manifest) {
            CopiedJDK5Manifest m = (CopiedJDK5Manifest) o;
            return attr.equals(m.getMainAttributes()) && entries.equals(m.getEntries());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return attr.hashCode() + entries.hashCode();
    }

    public Object clone() {
        return new CopiedJDK5Manifest(this);
    }

    static class FastInputStream extends FilterInputStream {

        private byte buf[];
        private int  count = 0;
        private int  pos   = 0;

        FastInputStream(InputStream in) {
            this(in, 8192);
        }

        FastInputStream(InputStream in, int size) {
            super(in);
            buf = new byte[size];
        }

        public int read() throws IOException {
            if (pos >= count) {
                fill();
                if (pos >= count) {
                    return -1;
                }
            }
            return buf[pos++] & 0xff;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int avail = count - pos;
            if (avail <= 0) {
                if (len >= buf.length) {
                    return in.read(b, off, len);
                }
                fill();
                avail = count - pos;
                if (avail <= 0) {
                    return -1;
                }
            }
            if (len > avail) {
                len = avail;
            }
            System.arraycopy(buf, pos, b, off, len);
            pos += len;
            return len;
        }

        public int readLine(byte[] b, int off, int len) throws IOException {
            byte[] tbuf = this.buf;
            int total = 0;
            while (total < len) {
                int avail = count - pos;
                if (avail <= 0) {
                    fill();
                    avail = count - pos;
                    if (avail <= 0) {
                        return -1;
                    }
                }
                int n = len - total;
                if (n > avail) {
                    n = avail;
                }
                int tpos = pos;
                int maxpos = tpos + n;
                while (tpos < maxpos && tbuf[tpos++] != '\n')
                    ;
                n = tpos - pos;
                System.arraycopy(tbuf, pos, b, off, n);
                off += n;
                total += n;
                pos = tpos;
                if (tbuf[tpos - 1] == '\n') {
                    break;
                }
            }
            return total;
        }

        public byte peek() throws IOException {
            if (pos == count) fill();
            return buf[pos];
        }

        public int readLine(byte[] b) throws IOException {
            return readLine(b, 0, b.length);
        }

        public long skip(long n) throws IOException {
            if (n <= 0) {
                return 0;
            }
            long avail = count - pos;
            if (avail <= 0) {
                return in.skip(n);
            }
            if (n > avail) {
                n = avail;
            }
            pos += n;
            return n;
        }

        public int available() throws IOException {
            return (count - pos) + in.available();
        }

        public void close() throws IOException {
            if (in != null) {
                in.close();
                in = null;
                buf = null;
            }
        }

        private void fill() throws IOException {
            count = pos = 0;
            int n = in.read(buf, 0, buf.length);
            if (n > 0) {
                count = n;
            }
        }
    }
}
