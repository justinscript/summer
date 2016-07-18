package com.ms.commons.weixin.pay;

import java.util.Map;

/**
 * 预支付交易返回信息
 */
public class PayUnifiedOrderResult extends AbstractPayResult {

    private String device_info; // 设备号

    private String trade_type; // 交易类型
    private String prepay_id;  // 微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
    private String code_url;   // 二维码链接

    public PayUnifiedOrderResult(Map<String, String> datas, boolean signCorrect) {
        super(datas, signCorrect);
        setDatas(datas);
    }

    private void setDatas(Map<String, String> datas) {
        if (!isReturnSuccess()) {
            return;
        }
        device_info = datas.get("device_info");
        if (!isResultSuccess()) {
            return;
        }
        trade_type = datas.get("trade_type");
        prepay_id = datas.get("prepay_id");
        code_url = datas.get("code_url");
    }

    /**
     * @return the device_info
     */
    public String getDevice_info() {
        return device_info;
    }

    /**
     * @return the trade_type
     */
    public String getTrade_type() {
        return trade_type;
    }

    /**
     * @return the prepay_id
     */
    public String getPrepay_id() {
        return prepay_id;
    }

    /**
     * @return the code_url
     */
    public String getCode_url() {
        return code_url;
    }

}
