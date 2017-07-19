package com.ms.commons.weixin.service;

import java.io.File;

public class WeixinConfig {

    private String  appid;
    private String  secret;
    private String  token;
    private boolean service;

    private String  partnerKey;
    private String  partnerId;
    private String  notifyUrl;

    private File    sslCaFile;
    private File    sslCertFile;
    private String  certPassword;

    /**
     * 构造器
     * 
     * @param appid
     * @param secret
     * @param token
     */
    public WeixinConfig(String appid, String secret, String token, boolean service) {
        this.appid = appid;
        this.secret = secret;
        this.token = token;
        this.service = service;
    }

    public void setPayConfig(String partnerKey, String partnerId, String notifyUrl) {
        this.partnerKey = partnerKey;
        this.partnerId = partnerId;
        this.notifyUrl = notifyUrl;
    }

    public void setSSL(File sslCaFile, File sslCertFile, String certPassword) {
        this.sslCaFile = sslCaFile;
        this.sslCertFile = sslCertFile;
        this.certPassword = certPassword;
    }

    /**
     * @return the appid
     */
    public String getAppid() {
        return appid;
    }

    /**
     * @return the secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the service
     */
    public boolean isService() {
        return service;
    }

    /**
     * @return the partnerKey
     */
    public String getPartnerKey() {
        return partnerKey;
    }

    /**
     * @return the partnerId
     */
    public String getPartnerId() {
        return partnerId;
    }

    /**
     * @return the notifyUrl
     */
    public String getNotifyUrl() {
        return notifyUrl;
    }

    /**
     * @return the sslCaFile
     */
    public File getSslCaFile() {
        return sslCaFile;
    }

    /**
     * @return the sslCertFile
     */
    public File getSslCertFile() {
        return sslCertFile;
    }

    /**
     * @return the certPassword
     */
    public String getCertPassword() {
        return certPassword;
    }

}
