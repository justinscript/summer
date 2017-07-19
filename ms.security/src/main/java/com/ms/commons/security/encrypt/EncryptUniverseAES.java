package com.ms.commons.security.encrypt;

import java.security.*;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * AES密码学中的高级加密标准（Advanced Encryption Standard，AES），又称 高级加密标准
 * Rijndael加密法，是美国联邦政府采用的一种区块加密标准。这个标准用来替代原先的DES，已经被多方分析且广为全世界所使用。经过五年的甄选流程，高级加密标准由美国国家标准与技术研究院（NIST）于2001年11月26日发布于FIPS
 * PUB 197，并在2002年5月26日成为有效的标准。2006年，高级加密标准已然成为对称密钥加密中最流行的算法之一。 　　该算法为比利时密码学家Joan Daemen和Vincent
 * Rijmen所设计，结合两位作者的名字，以Rijndael之命名之，投稿高级加密标准的甄选流程。
 */
@SuppressWarnings("restriction")
public class EncryptUniverseAES implements IEncrypt {

    // 算法名称
    public static final String utf8                 = "UTF-8";

    /**
     * BC包中AES算法名.
     */
    public static final String ALGORITHM_LONG_NAME  = "AES/CBC/PKCS7Padding";

    /**
     * BC包中AES算法名.
     */
    public static final String ALGORITHM_SHORT_NAME = "AES";

    /**
     * BC Provider名称.
     */
    public static final String PROVIDER_NAME        = "BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 对字符串加密
     * 
     * @param str
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encrypt(EncryptConfig config, String content, String key) throws Exception {
        // 生成密钥
        Key secretKey = getSecretKey(config, key);
        // Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(config.getIv()));
        byte[] src = content.getBytes(utf8);
        // 加密，结果保存进cipherByte
        byte[] doFinal = c.doFinal(src);
        byte[] encode = Base64.encodeBase64(doFinal);
        String strs = new String(encode, utf8);
        return strs;
    }

    /**
     * 对字符串解密
     * 
     * @param buff
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decrypt(EncryptConfig config, String content, String key) throws Exception {
        // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
        Key secretKey = getSecretKey(config, key);
        // Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(config.getIv()));
        byte[] bytes = content.getBytes(utf8);
        byte[] res = Base64.decodeBase64(bytes);
        res = c.doFinal(res);
        String str = new String(res, utf8);
        return str;
    }

    /**
     * 生成secretKey
     * 
     * @param key
     * @return
     * @throws Exception
     */
    private static Key getSecretKey(EncryptConfig config, String key) throws Exception {
        KeySpec keySpec = new PBEKeySpec(key.toCharArray(), hexStringToByteArray(config.getSalt()), 100, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public static byte[] hexStringToByteArray(String s) {
        // int len = s.length();
        // byte[] data = new byte[len / 2];
        // for (int i = 0; i < len; i += 2) {
        // data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        // }
        // return data;
        return DatatypeConverter.parseHexBinary(s);
    }

    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public static String hex(byte[] bytes) {
        return new String(Hex.encodeHex(bytes));
    }

    /**
     * @param args
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public static void main(String[] args) throws Exception {
        byte[] iv = EncryptConfig.getDefault().getIv();
        System.out.println(ReflectionToStringBuilder.toString(iv));
        for (byte b : iv) {
            System.out.println(String.valueOf(b) + "<-->" + Integer.toHexString(b));
        }
        String s = "48";
        byte parseByte = Byte.parseByte(s);
        System.out.println("" + Integer.toHexString(parseByte));
        System.out.println("parsed byte:" + parseByte);
        // EncryptUniverseAES de1 = new EncryptUniverseAES();
        // String msg = "54tYcS4FDpgJWSfkfqYG0g==";
        // // msg = "7wTXWxsnXM1q458ncLKz2g==";
        // String key = "092650";
        // String decrypt = de1.decrypt(msg, key);
        // System.out.println(decrypt);

        // System.out.println(hex(iv));
        // System.out.println(hex(hexStringToByteArray(salt)));
        // String msg = "";
        // String key = "123456";
        // msg = "abc"; // RfNnOzR6jmw3INYg5T2pGw==
        // String encontent = de1.encrypt(msg, key);
        // String decontent = de1.decrypt(encontent, key);
        // System.out.println("明文是:" + msg);
        // System.out.println("加密后:" + encontent);
        // System.out.println("解密后:" + decontent);
        // System.out.println(StringUtils.equals(decontent, msg));
        // System.out.println("iv=" + new String(iv, "utf-8"));
    }
}
