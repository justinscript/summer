package com.ms.commons.weixin.request;

public class WeixinRequestBody {

    private Integer agentId;
    private String  toUserName;
    private String  encrypt;

    /**
     * 构造器
     * 
     * @param agentId
     * @param toUserName
     * @param encrypt
     */
    public WeixinRequestBody(Integer agentId, String toUserName, String encrypt) {
        this.agentId = agentId;
        this.toUserName = toUserName;
        this.encrypt = encrypt;
    }

    /**
     * @return the agentId
     */
    public Integer getAgentId() {
        return agentId;
    }

    /**
     * @return the toUserName
     */
    public String getToUserName() {
        return toUserName;
    }

    /**
     * @return the encrypt
     */
    public String getEncrypt() {
        return encrypt;
    }
}
