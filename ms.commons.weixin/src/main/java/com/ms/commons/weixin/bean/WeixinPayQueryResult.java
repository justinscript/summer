package com.ms.commons.weixin.bean;

public class WeixinPayQueryResult extends WeixinResult {

    private OrderInfo orderInfo;

    /**
     * @return the orderInfo
     */
    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    /**
     * @param orderInfo the orderInfo to set
     */
    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public boolean isPaid() {
        return orderInfo != null && orderInfo.isPaid();
    }
}
