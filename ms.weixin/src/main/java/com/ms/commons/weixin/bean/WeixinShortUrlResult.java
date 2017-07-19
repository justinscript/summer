package com.ms.commons.weixin.bean;

/**
 * {"errcode":0,"errmsg":"ok","short_url":"http:\/\/w.url.cn\/s\/AvCo6Ih"}
 */
public class WeixinShortUrlResult extends WeixinResult {

    private String short_url;

    /**
     * @return the short_url
     */
    public String getShort_url() {
        return short_url;
    }

    /**
     * @param short_url the short_url to set
     */
    public void setShort_url(String short_url) {
        this.short_url = short_url;
    }

    @Override
    public String toString() {
        return "WeixinShortUrlResult [short_url=" + short_url + ", errcode=" + errcode + ", errmsg=" + errmsg + "]";
    }
}
