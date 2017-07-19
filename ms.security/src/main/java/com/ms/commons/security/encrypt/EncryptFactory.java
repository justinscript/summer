/*
 * Copyright 2017-2025 YueJi.com All right reserved. This software is the confidential and proprietary information of
 * YueJi.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with YueJi.com.
 */
package com.ms.commons.security.encrypt;

/**
 * @author shenyi 2016年1月19日 上午10:53:02
 */
public class EncryptFactory {

    private static ThreadLocal<IEncrypt> threadLocal = new ThreadLocal<IEncrypt>();

    /**
     * @param encrypt
     * @return
     */
    public static IEncrypt getEncrypt(EncryptEnum encrypt) {
        IEncrypt iencrypt = threadLocal.get();
        if (iencrypt == null) {
            switch (encrypt) {
                default:
                case AES:
                    // iencrypt = new EncryptAES();
                    // 神医：支持兼容js加密解密
                    iencrypt = new EncryptUniverseAES();
                    threadLocal.set(iencrypt);
                    break;
            }

        }
        return iencrypt;
    }
}
