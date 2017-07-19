package com.ms.commons.weixin.request;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

/**
 * <pre>
 * <xml><ToUserName><![CDATA[gh_b1d93f10c768]]></ToUserName>
 * <FromUserName><![CDATA[o3OCLjgf42X038Ceg0zjC46XWWU0]]></FromUserName>
 * <CreateTime>1398066340</CreateTime>
 * <MsgType><![CDATA[event]]></MsgType>
 * <Event><![CDATA[LOCATION]]></Event>
 * <Latitude>31.224501</Latitude>
 * <Longitude>121.478600</Longitude>
 * <Precision>69.000000</Precision>
 * </xml>
 * 
 * </pre>
 */
public class WeixinEventLocationRequest extends WeixinEventRequest {

    private float latitude; // 地理位置纬度
    private float longitude; // 地理位置经度
    private float precision; // 地理位置精度

    public WeixinEventLocationRequest(Map<String, String> datas) {
        super(datas);
        latitude = NumberUtils.toFloat(datas.get("Latitude"));
        longitude = NumberUtils.toFloat(datas.get("Longitude"));
        precision = NumberUtils.toFloat(datas.get("Precision"));
    }

    /**
     * 纬度
     * 
     * @return the latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * 经度
     * 
     * @return the longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * 精度
     * 
     * @return the precision
     */
    public float getPrecision() {
        return precision;
    }

}
