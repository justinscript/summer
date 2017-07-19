package com.ms.commons.weixin.pay;

import java.util.Date;
import java.util.Map;

/**
 * weixin支付主动通知支付完成信息
 * 
 * <pre>
 * http://pay.weixin.qq.com/wiki/doc/api/index.php?chapter=9_7
 * </pre>
 */
public class PayNotifyInfo extends AbstractPayResult {

    private String device_info;   // 微信支付分配的终端设备号
    private String openid;        // 用户标识
    private String is_subscribe;  // 用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效

    private String trade_type;    // 交易类型
    private String bank_type;     // 付款银行
    private int    total_fee;     // 总金额(分)
    private String fee_type;      // 货币种类
    private int    cash_fee;      // 现金支付金额
    private String cash_fee_type; // 现金支付货币类型
    private int    coupon_fee;    // 代金券或立减优惠金额
    private int    coupon_count;  // 代金券或立减优惠使用数量
    private String transaction_id; // 微信支付订单号
    private String out_trade_no;  // 商户订单号
    private String attach;        // 商家数据包
    private Date   time_end;      // 支付完成时间

    public PayNotifyInfo(Map<String, String> datas, boolean signCorrect) {
        super(datas, signCorrect);
        setDatas(datas);
    }

    private void setDatas(Map<String, String> datas) {
        if (!isReturnSuccess()) {
            return;
        }
        if (!isResultSuccess()) {
            return;
        }
        device_info = datas.get("device_info");
        openid = datas.get("openid");
        is_subscribe = datas.get("is_subscribe");
        trade_type = datas.get("trade_type");
        bank_type = datas.get("bank_type");
        total_fee = toInt(datas.get("total_fee"));
        fee_type = datas.get("fee_type");
        cash_fee = toInt(datas.get("cash_fee"));
        cash_fee_type = datas.get("cash_fee_type");
        coupon_fee = toInt(datas.get("coupon_fee"));
        coupon_count = toInt(datas.get("coupon_count"));
        transaction_id = datas.get("transaction_id");
        out_trade_no = datas.get("out_trade_no");
        attach = datas.get("attach");
        time_end = toDate(datas.get("time_end"));
    }

    /**
     * @return the device_info
     */
    public String getDevice_info() {
        return device_info;
    }

    /**
     * @return the openid
     */
    public String getOpenid() {
        return openid;
    }

    /**
     * @return the is_subscribe
     */
    public String getIs_subscribe() {
        return is_subscribe;
    }

    /**
     * @return the trade_type
     */
    public String getTrade_type() {
        return trade_type;
    }

    /**
     * @return the bank_type
     */
    public String getBank_type() {
        return bank_type;
    }

    /**
     * @return the total_fee
     */
    public int getTotal_fee() {
        return total_fee;
    }

    /**
     * @return the fee_type
     */
    public String getFee_type() {
        return fee_type;
    }

    /**
     * @return the cash_fee
     */
    public int getCash_fee() {
        return cash_fee;
    }

    /**
     * @return the cash_fee_type
     */
    public String getCash_fee_type() {
        return cash_fee_type;
    }

    /**
     * @return the coupon_fee
     */
    public int getCoupon_fee() {
        return coupon_fee;
    }

    /**
     * @return the coupon_count
     */
    public int getCoupon_count() {
        return coupon_count;
    }

    /**
     * @return the transaction_id
     */
    public String getTransaction_id() {
        return transaction_id;
    }

    /**
     * @return the out_trade_no
     */
    public String getOut_trade_no() {
        return out_trade_no;
    }

    /**
     * @return the attach
     */
    public String getAttach() {
        return attach;
    }

    /**
     * @return the time_end
     */
    public Date getTime_end() {
        return time_end;
    }

}
