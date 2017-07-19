package com.ms.commons.weixin.bean;

import com.google.gson.annotations.SerializedName;

public class QYOAuthResult extends WeixinResult {

    @SerializedName("UserId")
    private String userId;
    @SerializedName("DeviceId")
    private String deviceId;

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
