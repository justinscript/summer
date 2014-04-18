/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl.filter;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author zxc Apr 13, 2014 10:44:38 PM
 */
public class MsgKey implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1180128584171374842L;

    // 信息接收人
    private String            to;

    // 信息发送内容，如果是短信，则是消息内容，如果是Email，目前暂且判断主题和主题内容--如果内容过长，则取前80个字符
    private String            content;
    // 原有content的长度
    private Integer           originalLength;
    // 哈希值
    private int               hash;

    /**
     * Msgkey类主要用来邮件和短信滥发
     * 
     * @param to 通常情况行to不会为空
     * @param content content有可能为空
     */
    public MsgKey(String to, String content, Integer originalLength) {
        this.to = to;
        this.content = content;
        this.originalLength = originalLength;
    }

    public String getTo() {
        return to;
    }

    /*
     * 计算哈希值
     */
    public int hashCode() {
        if (hash == 0) {
            HashCodeBuilder hashcodeBuilder = new HashCodeBuilder(17, 37);
            if (to != null) {
                hashcodeBuilder.append(to);
            }
            if (content != null) {
                hashcodeBuilder.append(content);
            }
            if (originalLength != null) {
                hashcodeBuilder.append(originalLength);
            }
            hash = hashcodeBuilder.toHashCode();
            // if (content != null) {
            // // to应该肯定不会为空
            // hash = (to + content).hashCode();
            // } else {
            // hash = to.hashCode();
            // }
            // // 将orginalLength考虑到Hashcode
            // hash += originalLength;
        }
        return hash;
    }

    /*
     * 判断对象是否相同
     */
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof MsgKey) {
            MsgKey anotherMsgKey = (MsgKey) anObject;

            if (anotherMsgKey.originalLength != originalLength) {
                return false;
            }

            if ((anotherMsgKey.to == null && to != null) || (anotherMsgKey.to != null && to == null)
                || (anotherMsgKey.content == null && content != null)
                || (anotherMsgKey.content != null && content == null)) {
                return false;
            }
            return anotherMsgKey.to.equals(to) && anotherMsgKey.content.equals(content);
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("To=" + getTo());
        sb.append(" content=" + content);
        sb.append(" orginalLength=" + originalLength);
        return sb.toString();
    }
}
