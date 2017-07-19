package com.ms.commons.weixin.bean;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * {
 *    "errcode": 0,
 *    "errmsg": "ok",
 *    "userid": "zhangsan",
 *    "name": "李四",
 *    "department": [1, 2],
 *    "position": "后台工程师",
 *    "mobile": "15913215421",
 *    "email": "zhangsan@gzdev.com",
 *    "weixinid": "lisifordev",  
 *    "avatar": "http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6DSZUfiakYe37PKnQhBIeOQBO4czqrnZDS79FH5Wm5m4X69TBicnHFlhiafvDwklOpZeXYQQ2icg/0",
 *    "status": 1,
 *    "extattr": {"attrs":[{"name":"爱好","value":"旅游"},{"name":"卡号","value":"1234567234"}]}
 * }
 * </pre>
 */
public class QYWeixinUser extends WeixinResult {

    private String            userid;
    private String            name;
    private List<Integer>     department;
    private String            position;
    private String            mobile;
    private String            email;
    private String            weixinid;
    private String            avatar;
    private Integer           status;
    private QYWeixinUserAttrs extattr;

    /**
     * @return the userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the weixinid
     */
    public String getWeixinid() {
        return weixinid;
    }

    /**
     * @param weixinid the weixinid to set
     */
    public void setWeixinid(String weixinid) {
        this.weixinid = weixinid;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the department
     */
    public List<Integer> getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(List<Integer> department) {
        this.department = department;
    }

    /**
     * @return the extattr
     */
    public QYWeixinUserAttrs getExtattr() {
        return extattr;
    }

    /**
     * @return the extattr
     */
    public Map<String, String> getExtattrMap() {
        if (extattr == null) {
            return Collections.emptyMap();
        }
        return extattr.toMap();
    }

    /**
     * @param extattr the extattr to set
     */
    public void setExtattr(QYWeixinUserAttrs extattr) {
        this.extattr = extattr;
    }

    public String toString() {
        return "QYWeixinUser [userid=" + userid + ", name=" + name + ", department=" + department + ", position="
               + position + ", email=" + email + ", weixinid=" + weixinid + ", avatar=" + avatar + ", status=" + status
               + ", extattr=" + extattr + "]";
    }

}
