package com.ms.commons.security.encrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * AES密码学中的高级加密标准（Advanced Encryption Standard，AES），又称 高级加密标准
 * Rijndael加密法，是美国联邦政府采用的一种区块加密标准。这个标准用来替代原先的DES，已经被多方分析且广为全世界所使用。经过五年的甄选流程，高级加密标准由美国国家标准与技术研究院（NIST）于2001年11月26日发布于FIPS
 * PUB 197，并在2002年5月26日成为有效的标准。2006年，高级加密标准已然成为对称密钥加密中最流行的算法之一。 　　该算法为比利时密码学家Joan Daemen和Vincent
 * Rijmen所设计，结合两位作者的名字，以Rijndael之命名之，投稿高级加密标准的甄选流程。
 */
public class EncryptAES implements IEncrypt {

    // 算法名称
    public static final String AES                            = "AES";
    static final String        CIPHER_ALGORITHM_ECB           = "AES/ECB/PKCS5Padding";
    static final String        CIPHER_ALGORITHM_CBC           = "AES/CBC/PKCS5Padding";
    static final String        CIPHER_ALGORITHM_CBC_NoPadding = "AES/CBC/NoPadding";
    public static final String utf8                           = "UTF-8";

    static {
        // Security.addProvider(new com.sun.crypto.provider.SunJCE());
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
    @SuppressWarnings("unused")
    public String encrypt(EncryptConfig config, String content, String key) throws Exception {
        // 生成密钥
        SecretKey secretKey = getSecretKey(key);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, secretKey);
        // byte[] encoded = secretKey.getEncoded();
        // SecretKeySpec keySpec = new SecretKeySpec(encoded, AES);
        // // 生成Cipher对象,指定其支持的DES算法
        // Cipher c = Cipher.getInstance(AES);
        // // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
        // c.init(Cipher.ENCRYPT_MODE, keySpec, getIvParameterSpec());
        byte[] src = content.getBytes(utf8);
        String tmp = new String(src, utf8);
        // 加密，结果保存进cipherByte
        byte[] doFinal = c.doFinal(src);
        // String strs = parseByte2HexStr(doFinal);
        // String strs = new BASE64Encoder().encode(doFinal);
        // String strs = parseByte2HexStr(doFinal);
        // byte[] encode = java.util.Base64.getEncoder().encode(doFinal);
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
        SecretKey secretKey = getSecretKey(key);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, secretKey);
        // byte[] encoded = secretKey.getEncoded();
        // SecretKeySpec keySpec = new SecretKeySpec(encoded, AES);
        //
        // Cipher c = Cipher.getInstance(AES);
        // c.init(Cipher.DECRYPT_MODE, keySpec, getIvParameterSpec());
        // byte[] res = new BASE64Decoder().decodeBuffer(content);
        // byte[] res = parseHexStr2Byte(content);
        // byte[] res = java.util.Base64.getDecoder().decode(content.getBytes(utf8));
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
    private static SecretKey getSecretKey(String key) throws Exception {
        SecretKey securekey = null;
        if (StringUtils.isEmpty(key)) {
            key = "";
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        byte[] bytes = key.getBytes(utf8);
        // keyGenerator.init(128, new SecureRandom(bytes));
        // javax.crypto.BadPaddingException:Given final block not properly padded
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(bytes);
        keyGenerator.init(128, secureRandom);
        securekey = keyGenerator.generateKey();
        // System.out.println("秘钥长度:" + securekey.getEncoded().length);
        return securekey;
    }

    // private static IvParameterSpec getIvParameterSpec() {
    // byte[] iv = new byte[16];
    // SecureRandom random = new SecureRandom();
    // random.nextBytes(iv);
    // iv = new byte[] { 0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65,
    // 0x79 };
    // IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    // return ivParameterSpec;
    // }

    /**
     * 将二进制转换成16进制
     * 
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     * 
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        int half = hexStr.length() / 2;
        byte[] result = new byte[half];
        for (int i = 0; i < half; i++) {
            int index = i * 2;
            int high = Integer.parseInt(hexStr.substring(index, index + 1), 16);
            int low = Integer.parseInt(hexStr.substring(index + 1, index + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
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
        EncryptConfig config = EncryptConfig.getDefault();
        EncryptAES de1 = new EncryptAES();
        String msg = "郭XX-搞笑相声全集1111111111112222222223333333333333555555555555555555555555555"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff"
                     + "444444444444444444433333333333qqqqqqqqqqqfffffffffffffffffffffffffffffffffffff33ee78";
        String key = "wr2d  fa@#sdf";
        // msg = "我是需要被加密的明文";
        String encontent = de1.encrypt(config, msg, key);
        String decontent = de1.decrypt(config, encontent, key);
        System.out.println("明文是:" + msg);
        System.out.println("加密后:" + encontent);
        System.out.println("解密后:" + decontent);
        System.out.println(StringUtils.equals(decontent, msg));
    }
}
