package com.ms.commons.weixin.qymessage;

/**
 * <pre>
 *     touser  否   员工ID列表（消息接收者，多个接收者用‘|’分隔）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
 *     toparty 否   部门ID列表，多个接收者用‘|’分隔。当touser为@all时忽略本参数
 *     totag   否   标签ID列表，多个接收者用‘|’分隔。当touser为@all时忽略本参数
 *     msgtype 是   消息类型，此时固定为：text
 *     agentid 是   企业应用的id，整型。可在应用的设置页面查看
 * </pre>
 */
public class QYWeixinMessage {

    public static final String ALL       = "@all";
    public static final char   JOIN_CHAR = '|';

    private String             touser;
    private String             toparty;
    private String             totag;
    private String             msgtype;
    private int                agentid;
    private int                safe      = 0;

    public QYWeixinMessage(String touser, String toparty, String totag, String msgtype) {
        this.touser = touser;
        this.toparty = toparty;
        this.totag = totag;
        this.msgtype = msgtype;
    }

    /**
     * @return the touser
     */
    public String getTouser() {
        return touser;
    }

    /**
     * @return the toparty
     */
    public String getToparty() {
        return toparty;
    }

    /**
     * @return the totag
     */
    public String getTotag() {
        return totag;
    }

    /**
     * @return the msgtype
     */
    public String getMsgtype() {
        return msgtype;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    /**
     * @return the agentid
     */
    public int getAgentid() {
        return agentid;
    }

    /**
     * @param safe
     */
    public void setSafe(boolean safe) {
        this.safe = safe ? 1 : 0;
    }

    /**
     * @return
     */
    public int getSafe() {
        return safe;
    }
}
