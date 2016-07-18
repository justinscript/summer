package com.ms.commons.weixin.bean;

/**
 * 微信上传一个文件的返回信息
 * 
 * <pre>
 * {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
 * </pre>
 */
public class WeixinUploadResult extends WeixinResult {

    private String type;
    private String mediaId;
    private long   createdAt;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the media_id
     */
    public String getMediaId() {
        return mediaId;
    }

    /**
     * @param media_id the media_id to set
     */
    public void setMedia_id(String media_id) {
        this.mediaId = media_id;
    }

    /**
     * @return the created_at
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * @param created_at the created_at to set
     */
    public void setCreated_at(long created_at) {
        this.createdAt = created_at;
    }

}
