package com.ms.commons.weixin.bean;

/**
 * <pre>
 *     appId: '', // 必填，公众号的唯一标识
 *     timestamp: , // 必填，生成签名的时间戳
 *     nonceStr: '', // 必填，生成签名的随机串
 *     signature: '',// 必填，签名，见附录1
 * </pre>
 */
public class JsSignature {

    private String appId;
    private String timestamp;
    private String nonceStr;
    private String signature;

    /**
     */
    public JsSignature() {

    }

    /**
     * 构造器
     * 
     * @param appId
     * @param timestamp
     * @param nonceStr
     * @param signature
     */
    public JsSignature(String appId, String timestamp, String nonceStr, String signature) {
        this.appId = appId;
        this.timestamp = timestamp;
        this.nonceStr = nonceStr;
        this.signature = signature;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the nonceStr
     */
    public String getNonceStr() {
        return nonceStr;
    }

    /**
     * @param nonceStr the nonceStr to set
     */
    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

}
