package com.ms.commons.weixin.request;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

public abstract class WeixinRequest {

    private String  toUserName;
    private String  fromUserName;
    private Long    createTime;
    private String  msgType;
    private Long    msgId;
    private Integer agentId;

    public WeixinRequest(Map<String, String> datas) {
        init(datas);
    }

    /**
     * 初始化数据
     * 
     * @param datas
     */
    private void init(Map<String, String> datas) {
        msgType = datas.get("MsgType");
        toUserName = datas.get("ToUserName");
        fromUserName = datas.get("FromUserName");
        long ct = NumberUtils.toLong(datas.get("CreateTime"), 0);
        if (ct > 0) {
            createTime = ct;
        }
        long mid = NumberUtils.toLong(datas.get("MsgId"), 0);
        if (mid > 0) {
            msgId = mid;
        }
        String agentIdStr = datas.get("AgentID");
        if (agentIdStr != null) {
            int aid = NumberUtils.toInt(agentIdStr, 0);
            if (aid > 0) {
                agentId = aid;
            }
        }
    }

    /**
     * @return the toUserName
     */
    public String getToUserName() {
        return toUserName;
    }

    /**
     * @return the fromUserName
     */
    public String getFromUserName() {
        return fromUserName;
    }

    /**
     * @return the createTime
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * @return the msgType
     */
    public String getMsgType() {
        return msgType;
    }

    /**
     * @return the msgId
     */
    public Long getMsgId() {
        return msgId;
    }

    /**
     * @return the agentId
     */
    public Integer getAgentId() {
        return agentId;
    }

}
