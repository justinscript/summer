package com.ms.commons.weixin.response;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ms.commons.weixin.request.WeixinRequest;

public abstract class WeixinResponse {

    private String  toUserName;
    private String  fromUserName;
    private Long    createTime;
    private String  msgType;
    private Integer agentId;

    public WeixinResponse(WeixinRequest request) {
        this.toUserName = request.getFromUserName();
        this.fromUserName = request.getToUserName();
        this.msgType = request.getMsgType();
        this.createTime = request.getCreateTime();
        this.agentId = request.getAgentId();
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

    public String toXML() throws IOException {
        Element root = new Element("xml");
        addDefault(root);
        addElement(root);
        Document doc = new Document(root);
        StringWriter stringWriter = new StringWriter();
        XMLOutputter xMLOutputter = new XMLOutputter();
        xMLOutputter.setFormat(Format.getPrettyFormat());
        xMLOutputter.output(doc, stringWriter);
        return stringWriter.toString().trim();
    }

    private void addDefault(Element root) {
        Element toUserNameEle = new Element("ToUserName");
        Element fromUserNameEle = new Element("FromUserName");
        Element createTimeEle = new Element("CreateTime");
        Element msgTypeEle = new Element("MsgType");
        // Element funcFlag = new Element("FuncFlag");

        toUserNameEle.setText(toUserName);
        fromUserNameEle.setText(fromUserName);
        createTimeEle.setText(String.valueOf(createTime));
        msgTypeEle.setText(getResponseType());
        // funcFlag.setText("0");

        root.addContent(toUserNameEle);
        root.addContent(fromUserNameEle);
        root.addContent(createTimeEle);
        root.addContent(msgTypeEle);

        if (agentId != null) {
            Element agentIdEle = new Element("AgentID");
            agentIdEle.setText(String.valueOf(agentId));
            root.addContent(agentIdEle);
        }
        // root.addContent(funcFlag);
    }

    /**
     * 返回数据类型
     * 
     * @return
     */
    public abstract String getResponseType();

    /**
     * 添加数据
     * 
     * @param root
     */
    public abstract void addElement(Element root);

}
