package com.ms.commons.weixin.bean;

/**
 * 微信返回的数据 {"errcode":40013,"errmsg":"invalid appid"}
 */
public abstract class WeixinResult {

    protected Integer errcode;
    protected String  errmsg;

    /**
     * @return the errcode
     */
    public Integer getErrcode() {
        return errcode;
    }

    /**
     * @param errcode the errcode to set
     */
    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    /**
     * @return the errmsg
     */
    public String getErrmsg() {
        return errmsg;
    }

    /**
     * @param errmsg the errmsg to set
     */
    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    /**
     * 是否是系统忙
     * 
     * @return
     */
    public boolean isBusy() {
        return errcode != null && errcode.intValue() == -1;
    }

    /**
     * 请求是否成功
     * 
     * @return
     */
    public boolean isSuccess() {
        return errcode == null || errcode.intValue() == 0;
    }

    /**
     * 是否是accessToken超时
     * 
     * @return
     */
    public boolean isAccessTokenExpired() {
        return errcode != null && errcode.intValue() == 42001;
    }

    /**
     * 是否是accessToken无效了
     * 
     * @return
     */
    public boolean isInvalidCredential() {
        return errcode != null && errcode.intValue() == 40001;
    }

    /**
     * 回复时间超过限制
     * 
     * @return
     */
    public boolean isReplyMessageTimeout() {
        return errcode != null && errcode.intValue() == 45015;
    }
}
