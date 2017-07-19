package com.ms.commons.weixin.bean;

public class RedPacketInfo {

    private String openId;
    private int    price;
    private String mchBillno;   // 商户订单号

    private String nickName;    // 提供方名称:
    private String sendName;    // 红包发送者名称:
    private String wishing;     // 红包祝福语:恭喜发财
    private String clientIp;    // ip,不填写默认为127.0.0.1
    private String actName;     // 活动名:新年红包
    private String remark;      // 备注

    private String logoImgurl;
    private String shareContent;
    private String shareUrl;
    private String shareImgurl;

    /**
     * @return the openId
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * @param openId the openId to set
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * @return the mchBillno
     */
    public String getMchBillno() {
        return mchBillno;
    }

    /**
     * @param mchBillno the mchBillno to set
     */
    public void setMchBillno(String mchBillno) {
        this.mchBillno = mchBillno;
    }

    /**
     * @return the nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName the nickName to set
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return the sendName
     */
    public String getSendName() {
        return sendName;
    }

    /**
     * @param sendName the sendName to set
     */
    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    /**
     * @return the wishing
     */
    public String getWishing() {
        return wishing;
    }

    /**
     * @param wishing the wishing to set
     */
    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    /**
     * @return the clientIp
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * @param clientIp the clientIp to set
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * @return the actName
     */
    public String getActName() {
        return actName;
    }

    /**
     * @param actName the actName to set
     */
    public void setActName(String actName) {
        this.actName = actName;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the logoImgurl
     */
    public String getLogoImgurl() {
        return logoImgurl;
    }

    /**
     * @param logoImgurl the logoImgurl to set
     */
    public void setLogoImgurl(String logoImgurl) {
        this.logoImgurl = logoImgurl;
    }

    /**
     * @return the shareContent
     */
    public String getShareContent() {
        return shareContent;
    }

    /**
     * @param shareContent the shareContent to set
     */
    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    /**
     * @return the shareUrl
     */
    public String getShareUrl() {
        return shareUrl;
    }

    /**
     * @param shareUrl the shareUrl to set
     */
    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    /**
     * @return the shareImgurl
     */
    public String getShareImgurl() {
        return shareImgurl;
    }

    /**
     * @param shareImgurl the shareImgurl to set
     */
    public void setShareImgurl(String shareImgurl) {
        this.shareImgurl = shareImgurl;
    }

}
