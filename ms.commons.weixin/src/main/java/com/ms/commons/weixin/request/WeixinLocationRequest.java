package com.ms.commons.weixin.request;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

/**
 * <pre>
 * <xml><ToUserName><![CDATA[gh_b1d93f10c768]]></ToUserName>
 * <FromUserName><![CDATA[o3OCLjgf42X038Ceg0zjC46XWWU0]]></FromUserName>
 * <CreateTime>1398058662</CreateTime>
 * <MsgType><![CDATA[location]]></MsgType>
 * <Location_X>31.222715</Location_X>
 * <Location_Y>121.483139</Location_Y>
 * <Scale>16</Scale>
 * <Label><![CDATA[中国上海市黄浦区会稽路11号 邮政编码: 200021]]></Label>
 * <MsgId>6004616231379707261</MsgId>
 * </xml>
 * </pre>
 */
public class WeixinLocationRequest extends WeixinRequest {

    private float  locationX;
    private float  locationY;
    private float  scale;
    private String label;

    public WeixinLocationRequest(Map<String, String> datas) {
        super(datas);
        locationX = NumberUtils.toFloat(datas.get("Location_X"));
        locationY = NumberUtils.toFloat(datas.get("Location_Y"));
        scale = NumberUtils.toFloat(datas.get("Scale"));
        label = datas.get("Label");
    }

    /**
     * @return the locationX
     */
    public float getLocationX() {
        return locationX;
    }

    /**
     * @return the locationY
     */
    public float getLocationY() {
        return locationY;
    }

    /**
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }
}
