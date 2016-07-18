package com.ms.commons.security.encrypt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 加密iv和salt配置
 */
public class EncryptConfig implements EncryptCons, Serializable {

    /**
     */
    private static final long    serialVersionUID = -8921653504442579660L;

    private static EncryptConfig default_instance = null;

    public static EncryptConfig getIntance(String salt, byte[] iv) {
        return new EncryptConfig(salt, iv);
    }

    public static EncryptConfig getIntance(String salt, String iv) {
        return new EncryptConfig(salt, iv);
    }

    public static EncryptConfig getDefault() {
        if (default_instance == null) {
            default_instance = new EncryptConfig(ENCRYPT_SALT, ENCRYPT_IV);
        }
        return default_instance;
    }

    /**
     * @param salt
     * @param iv
     */
    public EncryptConfig(String salt, byte[] iv) {
        this.salt = salt;
        this.iv = iv;
    }

    public EncryptConfig(String salt, String ivs) {
        this.salt = salt;
        this.iv = parseIv(ivs);
    }

    // 加密盐

    private String salt; // = "0102030405060708";
    /**
     * IV大小.
     */
    private byte[] iv;

    // = { 0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35, 0x30, 0x36, 0x30, 0x37, 0x30,
    // 0x38 };

    public byte[] getIv() {
        return iv;
    }

    public String getIv4Db() {
        String str = ivToStr(iv);
        return str;
    }

    public String getIv4Js() {
        String str = ivToStr(iv);
        return str;
    }

    /**
     * 转换为16进制字符串
     * 
     * @param iv
     * @return
     */
    public static String ivToHexStr(byte[] iv) {
        List<String> byteList = new ArrayList<String>(iv.length);
        for (byte b : iv) {
            byteList.add(Integer.toHexString(b));
        }
        String str = StringUtils.join(byteList, IV_DELEMITER);
        return str;
    }

    /**
     * 转换为10进制字符串
     * 
     * @param iv
     * @return
     */
    public static String ivToStr(byte[] iv) {
        List<Byte> byteList = new ArrayList<Byte>(iv.length);
        for (byte b : iv) {
            byteList.add(b);
        }
        String str = StringUtils.join(byteList, IV_DELEMITER);
        return str;
    }

    /**
     * 将字符串转换为byte数组
     * 
     * @param ivs
     * @return
     */
    public static byte[] parseIv(String ivs) {
        String[] split = StringUtils.split(ivs, IV_DELEMITER);
        byte[] tmpIv = new byte[split.length];

        for (int i = 0, size = split.length; i < size; i++) {
            tmpIv[i] = (byte) Integer.parseInt(split[i]);
        }
        return tmpIv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public static void main(String[] args) {
        byte[] b = { 0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35, 0x30, 0x36, 0x30, 0x37, 0x30, 0x38 };
        System.out.println(new String(b));
        String str = ivToStr(b);
        System.out.println(str);
        System.out.println(ivToHexStr(b));
        System.out.println(ReflectionToStringBuilder.toString(parseIv(str)));
    }

}
