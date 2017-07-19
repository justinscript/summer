package com.ms.commons.weixin.service;

public class QYWeixinConfig {

    private int    agentid;
    private String corpid;
    private String corpsecret;
    private String token;
    private String encodingAESKey;

    /**
     * 构造器
     * 
     * @param corpid 企业Id
     * @param agentid 应用的id号
     * @param corpsecret 管理组的凭证密钥
     * @param token
     * @param encodingAESKey 用于消息体的加密，是AES密钥的Base64编码，由43位字符组成
     */
    public QYWeixinConfig(String corpid, int agentid, String corpsecret, String token, String encodingAESKey) {
        this.corpid = corpid;
        this.agentid = agentid;
        this.corpsecret = corpsecret;
        this.token = token;
        this.encodingAESKey = encodingAESKey;
    }

    /**
     * @return the agentid
     */
    public int getAgentid() {
        return agentid;
    }

    /**
     * @return the corpid
     */
    public String getCorpid() {
        return corpid;
    }

    /**
     * @return the corpsecret
     */
    public String getCorpsecret() {
        return corpsecret;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the encodingAESKey
     */
    public String getEncodingAESKey() {
        return encodingAESKey;
    }

}
