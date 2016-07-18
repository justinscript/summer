package com.ms.commons.security.encrypt;

/**
 * 加密解密接口
 */
public interface IEncrypt {

    /**
     * 加密方法
     * 
     * @param content 待加密的内容
     * @param key 加密key
     * @return
     */
    public String encrypt(EncryptConfig config, String content, String key) throws Exception;

    /**
     * @param content 待解密的内容
     * @param key 解密key
     * @return
     */
    public String decrypt(EncryptConfig config, String content, String key) throws Exception;
}
