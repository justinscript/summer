package com.ms.commons.weixin.bean;

/**
 * <pre>
 *  {
 *      "ret_code":0,
 *      "ret_msg":"",
 *      "input_charset":"GBK",
 *      "trade_state":"0",
 *      "trade_mode":"1",
 *      "partner":"1218882901",
 *      "bank_type":"CMB_CREDIT",
 *      "bank_billno":"201412256081377567",
 *      "total_fee":"1","fee_type":"1",
 *      "transaction_id":"1218882901201412256182145784",
 *      "out_trade_no":"2014122516539032108",
 *      "is_split":"false",
 *      "is_refund":"false",
 *      "attach":"",
 *      "time_end":"20141225165319",
 *      "transport_fee":"0",
 *      "product_fee":"1",
 *      "discount":"0",
 *      "rmb_total_fee":""
 *  }
 * 
 * </pre>
 */
public class OrderInfo {

    // ret_code 是查询结果状态码,0 表明成功,其他表明错误;
    private Integer retCode;
    // ret_msg 是查询结果出错信息;
    private String  retMsg;
    // input_charset 是返回信息中的编码方式;
    private String  inputCharset;

    // --------------业务代码

    // trade_state 是订单状态,0 为成功,其他为失败;
    private String  tradeState;
    // trade_mode 是交易模式,1 为即时到帐,其他保留;
    private String  tradeMode;

    // partner 是财付通商户号,即前文的 partnerid;
    private String  partner;

    // bank_type 是银行类型;
    private String  bankType;

    // bank_billno 是银行订单号;
    private String  bankBillno;
    // total_fee 是总金额,单位为分;
    private int     totalFee;
    // fee_type 是币种,1 为人民币;
    private String  feeType;
    // transaction_id 是财付通订单号;
    private String  transactionId;
    // out_trade_no 是第三方订单号;
    private String  outTradeNo;
    // is_split 表明是否分账,false 为无分账,true 为有分账;
    private boolean isSplit;
    // is_refund 表明是否退款,false 为无退款,ture 为退款;
    private boolean isRefund;
    // attach 是商户数据包,即生成订单 package 时商户填入的 attach; time_end 是支付完成时间;
    private String  attach;

    // transport_fee 是物流费用,单位为分;
    private Integer transportFee;

    // // product_fee 是物品费用,单位为分;
    private Integer productFee;
    // // discount 是折扣价格,单位为分;
    private Integer discount;

    public Integer getRetCode() {
        return retCode;
    }

    public boolean isPaid() {
        return "0".equals(this.tradeState);
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getTradeState() {
        return tradeState;
    }

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState;
    }

    public String getTradeMode() {
        return tradeMode;
    }

    public void setTradeMode(String tradeMode) {
        this.tradeMode = tradeMode;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    /**
     * @return the bankType
     */
    public String getBankType() {
        return bankType;
    }

    /**
     * @param bankType the bankType to set
     */
    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankBillno() {
        return bankBillno;
    }

    public void setBankBillno(String bankBillno) {
        this.bankBillno = bankBillno;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public boolean isSplit() {
        return isSplit;
    }

    public void setSplit(boolean isSplit) {
        this.isSplit = isSplit;
    }

    public boolean isRefund() {
        return isRefund;
    }

    public void setRefund(boolean isRefund) {
        this.isRefund = isRefund;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public Integer getTransportFee() {
        return transportFee;
    }

    public void setTransportFee(Integer transportFee) {
        this.transportFee = transportFee;
    }

    public Integer getProductFee() {
        return productFee;
    }

    public void setProductFee(Integer productFee) {
        this.productFee = productFee;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public boolean isDelveried() {
        return false;
    }

}
