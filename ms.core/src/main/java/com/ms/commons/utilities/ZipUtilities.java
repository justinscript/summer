/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author zxc Apr 12, 2013 1:35:31 PM
 */
public class ZipUtilities {

    /**
     * 把byte[]数组转换为Object对象
     * 
     * @param bytes
     * @return
     */
    public static Object bytesToObject(byte[] bytes) throws Exception {
        Object obj = null;
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        obj = oi.readObject();
        bi.close();
        oi.close();
        return obj;
    }

    /**
     * 把Serializable对象转换为byte数组
     * 
     * @param obj
     * @return
     */
    public static byte[] objectToBytes(Serializable obj) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        bytes = bo.toByteArray();
        bo.close();
        oo.close();
        return bytes;
    }

    /**
     * 把byte[]数组转换为String
     * 
     * @param bytes
     * @return
     */
    public static String getString(byte[] bytes, String charset) {
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    /**
     * 把带有序列化的对象通过GZip压缩后，返回压缩的byte[]数组
     * 
     * @param object
     * @return
     */
    public static byte[] gZipObject(Serializable object) throws Exception {

        byte[] dataByte = null;
        // 建立字节数组输出流
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        // 建立gzip压缩输出流
        GZIPOutputStream gzout = new GZIPOutputStream(o);
        // 建立对象序列化输出流
        ObjectOutputStream out = new ObjectOutputStream(gzout);
        out.writeObject(object);
        out.flush();
        out.close();
        gzout.close(); // 返回压缩字节流
        dataByte = o.toByteArray();
        o.close();
        return dataByte;
    }

    /**
     * 把经过压缩后的byte[]数组还原为原来的Object对象
     * 
     * @param dataByte
     * @return
     */
    public static Object ungZipObject(byte[] dataByte) throws Exception {

        Object object = null;
        // 建立字节数组输入流
        ByteArrayInputStream i = new ByteArrayInputStream(dataByte); // 建立gzip解压输入流
        GZIPInputStream gzin = new GZIPInputStream(i); // 建立对象序列化输入流
        ObjectInputStream in = new ObjectInputStream(gzin); // 按制定类型还原对象
        object = in.readObject();
        i.close();
        gzin.close();
        in.close();
        return object;
    }

    /**
     * 把byte[]通过GZip压缩后，返回压缩的byte[]数组
     * 
     * @param object
     * @return
     */
    public static byte[] gZipBytes(byte[] object) throws Exception {

        byte[] dataByte = null;
        // 建立字节数组输出流
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        // 建立gzip压缩输出流
        GZIPOutputStream gzout = new GZIPOutputStream(o);
        gzout.write(object);
        gzout.finish();
        gzout.close();
        // 返回压缩字节流
        dataByte = o.toByteArray();
        o.close();
        return dataByte;
    }

    /**
     * 把经过压缩后的byte[]数组还原为原来的Object对象
     * 
     * @param dataByte
     * @return
     */
    public static byte[] ungZipBytes(byte[] dataByte) throws Exception {
        byte[] b = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(dataByte);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        byte[] buf = new byte[1024];
        int num = -1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((num = gzip.read(buf, 0, buf.length)) != -1) {
            baos.write(buf, 0, num);
        }
        b = baos.toByteArray();
        baos.flush();
        baos.close();
        gzip.close();
        bis.close();
        return b;
    }

    // 压缩
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return out.toString("ISO-8859-1");
        // return out.toString("UTF-8");
    }

    // 解压缩
    public static String uncompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        // ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
        return out.toString("UTF-8");
    }
}
