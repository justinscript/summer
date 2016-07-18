package com.ms.commons.weixin.bean;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.weixin.pay.AbstractPayResult;

public class SendRedPackResult extends AbstractPayResult {

    public static final String NOAUTH              = "NOAUTH";             // 无权限
    public static final String PARAM_ERROR         = "PARAM_ERROR";        // 参数错误
    public static final String OPENID_ERROR        = "OPENID_ERROR";       // Openid错误
    public static final String NOTENOUGH           = "NOTENOUGH";          // 余额不足
    public static final String SYSTEMERROR         = "SYSTEMERROR";        // 系统繁忙，请再试
    public static final String TIME_LIMITED        = "TIME_LIMITED";       // 企业红包的发送时间受限
    public static final String SECOND_OVER_LIMITED = "SECOND_OVER_LIMITED"; // 企业红包的按分钟发送受限
    public static final String MONEY_LIMIT         = "MONEY_LIMIT";        // 红包金额収放限制 每个红包金额必须大于 1 元，小于 200 元

    private String             mchBillno;                                  // 商户订单号（每个订单号必须唯一）组成： mch_id+yyyymmdd+10
                                                                            // 位一天内不能重复的数字。
    private String             mchId;                                      // 微信支付分配的商户号
    private String             wxAppId;                                    // 商户 appid
    private String             reOpenId;                                   // 接叐收红包的用户用户在 wxappid 下的 openid
    private String             totalAmount;                                // 付款金额，单位分

    public SendRedPackResult(Map<String, String> datas, boolean signCorrect) {
        super(datas, signCorrect);
        setDatas(datas);
    }

    private void setDatas(Map<String, String> datas) {
        if (!isResultSuccess()) {
            return;
        }
        mchBillno = datas.get("mch_billno");
        mchId = datas.get("mch_id");
        wxAppId = datas.get("wxappid");
        reOpenId = datas.get("re_openid");
        totalAmount = datas.get("total_amount");
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
     * @return the mchId
     */
    public String getMchId() {
        return mchId;
    }

    /**
     * @param mchId the mchId to set
     */
    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    /**
     * @return the wxAppId
     */
    public String getWxAppId() {
        return wxAppId;
    }

    /**
     * @param wxAppId the wxAppId to set
     */
    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    /**
     * @return the reOpenId
     */
    public String getReOpenId() {
        return reOpenId;
    }

    /**
     * @param reOpenId the reOpenId to set
     */
    public void setReOpenId(String reOpenId) {
        this.reOpenId = reOpenId;
    }

    /**
     * @return the totalAmount
     */
    public String getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @param errorCode 参考常量
     * @return
     */
    public boolean isError(String errorCode) {
        return StringUtils.equals(errorCode, getErr_code());
    }
}
