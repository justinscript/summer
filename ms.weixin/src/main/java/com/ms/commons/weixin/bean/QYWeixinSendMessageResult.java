package com.ms.commons.weixin.bean;

/**
 * <pre>
 * {
 *    "errcode": 0,
 *    "errmsg": "ok",
 *    "invaliduser": "UserID1",
 *    "invalidparty":"PartyID1",
 *    "invalidtag":"TagID1"
 * }
 * </pre>
 */
public class QYWeixinSendMessageResult extends WeixinResult {

    private String invaliduser;
    private String invalidparty;
    private String invalidtag;

    public boolean isOutOffResponseTime() {
        return errcode != null && errcode.intValue() == 45015;
    }

    /**
     * @return the invaliduser
     */
    public String getInvaliduser() {
        return invaliduser;
    }

    /**
     * @param invaliduser the invaliduser to set
     */
    public void setInvaliduser(String invaliduser) {
        this.invaliduser = invaliduser;
    }

    /**
     * @return the invalidparty
     */
    public String getInvalidparty() {
        return invalidparty;
    }

    /**
     * @param invalidparty the invalidparty to set
     */
    public void setInvalidparty(String invalidparty) {
        this.invalidparty = invalidparty;
    }

    /**
     * @return the invalidtag
     */
    public String getInvalidtag() {
        return invalidtag;
    }

    /**
     * @param invalidtag the invalidtag to set
     */
    public void setInvalidtag(String invalidtag) {
        this.invalidtag = invalidtag;
    }
}
