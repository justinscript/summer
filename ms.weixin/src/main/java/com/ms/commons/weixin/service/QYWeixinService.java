package com.ms.commons.weixin.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.ms.commons.weixin.bean.*;
import com.ms.commons.weixin.qymessage.QYWeixinMessage;
import com.ms.commons.weixin.qymessage.QYWeixinTextMessage;
import com.ms.commons.weixin.request.WeixinRequestBody;
import com.ms.commons.weixin.tools.*;

/**
 * 企业微信
 */
public abstract class QYWeixinService extends CommonWeixinService {

    private static final int TRY_TIME = 3;
    private QYWeixinConfig   weixinConfig;
    private WXBizMsgCrypt    msgCrypt;

    /**
     * 构造器
     * 
     * @param corpid 企业Id
     * @param agentid 应用的id号
     * @param corpsecret 管理组的凭证密钥
     * @param token
     * @param encodingAESKey 用于消息体的加密，是AES密钥的Base64编码，由43位字符组成
     * @throws AesException
     */
    public QYWeixinService(String corpid, int agentid, String corpsecret, String token, String encodingAESKey)
                                                                                                              throws AesException {
        weixinConfig = new QYWeixinConfig(corpid, agentid, corpsecret, token, encodingAESKey);
        msgCrypt = new WXBizMsgCrypt(token, encodingAESKey, corpid);
    }

    /**
     * 获取APP的Id，企业时是corpid
     * 
     * @return
     */
    String getAppId() {
        return weixinConfig.getCorpid();
    }

    protected QYWeixinConfig getWeixinConfig() {
        return weixinConfig;
    }

    /**
     * 验证消息是否是微信发送过来的
     * 
     * @param msg_signature
     * @param timestamp
     * @param nonce
     * @return 需要返回的明文
     */
    public String validateUrl(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException {
        return msgCrypt.VerifyURL(msgSignature, timeStamp, nonce, echoStr);
    }

    /**
     * 微信主动推送的消息
     * 
     * @param body
     * @param msgSignature
     * @param timeStamp
     * @param nonce
     * @return
     * @throws AesException
     */
    public String service(String body, String msgSignature, String timeStamp, String nonce) throws AesException {
        // 解密
        WeixinRequestBody extractBody = extractBody(body);
        if (!msgCrypt.verifyMsg(msgSignature, timeStamp, nonce, extractBody.getEncrypt())) {
            throw new AesException(AesException.ValidateSignatureError);
        }
        String decrypt = msgCrypt.decrypt(extractBody.getEncrypt());
        String resp = super.doService(decrypt);
        if (resp == null) {
            throw new AesException(AesException.ParseXmlError);
        }
        return msgCrypt.EncryptMsg(resp, timeStamp, nonce);
    }

    WeixinRequestBody extractBody(String body) throws AesException {
        Map<String, String> paserXML = Tools.paserXML(body);
        if (paserXML.isEmpty()) {
            throw new AesException(AesException.ParseXmlError);
        }
        int agentID = Tools.toInt(paserXML.get("AgentID"), 0);
        if (agentID <= 0) {
            throw new AesException(AesException.ParseXmlError);
        }
        String encrypt = paserXML.get("Encrypt");
        String toUserName = paserXML.get("ToUserName");
        if (Tools.isEmpty(encrypt) || Tools.isEmpty(toUserName)) {
            throw new AesException(AesException.ParseXmlError);
        }
        return new WeixinRequestBody(agentID, toUserName, encrypt);
    }

    synchronized WeixinAccessToken initAccessToken() {
        log.error("WeixinAccessToken initAccessToken start~~~~~");
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://qyapi.weixin.qq.com/cgi-bin/gettoken");
        sb.append("?corpid=").append(weixinConfig.getCorpid());
        sb.append("&corpsecret=").append(weixinConfig.getCorpsecret());
        String result = HttpTools.get(sb.toString());
        if (Tools.isEmpty(result)) {
            log.error("WeixinAccessToken initAccessToken end fail~~~~~");
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        Object bean = JSONObject.toBean(jsonObject, WeixinAccessToken.class);
        WeixinAccessToken token = (WeixinAccessToken) bean;
        log.error("WeixinAccessToken initAccessToken end~~~~~");
        return token;
    }

    /**
     * https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=ACCESS_TOKE
     * 
     * @return
     */
    synchronized WeixinJSApiTicket initJSApiTicket() {
        log.error("WeixinJSApiTicket initJSApiTicket start~~~~~");
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=");
        sb.append(getAccessToken());
        String result = HttpTools.get(sb.toString());
        if (Tools.isEmpty(result)) {
            log.error("WeixinJSApiTicket initJSApiTicket end fail~~~~~");
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        Object bean = JSONObject.toBean(jsonObject, WeixinJSApiTicket.class);
        WeixinJSApiTicket ticket = (WeixinJSApiTicket) bean;
        log.error("WeixinJSApiTicket initJSApiTicket end~~~~~");
        return ticket;
    }

    /**
     * 网页授权后，根据code获取用户信息
     * 
     * @param code
     */
    public QYOAuthResult oauthResult(String code) {
        // https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE&agentid=AGENTID
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo");
        sb.append("?access_token=").append(getAccessToken());
        sb.append("&code=").append(code);
        sb.append("&agentid=").append(weixinConfig.getAgentid());
        String s = HttpTools.get(sb.toString());
        log.error("QYOAuthResult:  " + s);
        Gson gson = new Gson();
        QYOAuthResult bean = gson.fromJson(s, QYOAuthResult.class);
        // JSONObject jsonObject = JSONObject.fromObject(s);
        // Object bean = JSONObject.toBean(jsonObject, QYOAuthResult.class);
        return bean;
    }

    // ================================================
    // ============== 成员管理API start ================
    // ================================================

    /**
     * 获取帐号信息
     * 
     * @param userid 即帐号
     */
    public QYWeixinUser getUser(String userid) {
        if (StringUtils.isEmpty(userid)) {
            return null;
        }
        QYWeixinUser result = null;
        for (int i = 0; i < TRY_TIME; i++) {
            result = _getUser(userid);
            if (result.isSuccess()) {
                return result;
            }
            if (result.isAccessTokenExpired() || result.isInvalidCredential()) {
                resetWeixinAccessToken();
            }
        }
        return result;
    }

    private QYWeixinUser _getUser(String userid) {
        // https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&userid=lisi
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://qyapi.weixin.qq.com/cgi-bin/user/get");
        sb.append("?access_token=").append(getAccessToken());
        sb.append("&userid=").append(userid);
        String responseBodyAsString = HttpTools.get(sb.toString());
        log.error("_getUser : " + responseBodyAsString);
        Gson gson = new Gson();
        try {
            QYWeixinUser bean = gson.fromJson(responseBodyAsString, QYWeixinUser.class);
            return bean;
        } catch (Exception e) {
            QYWeixinUser result = new QYWeixinUser();
            result.setErrcode(47001);
            result.setErrmsg("json解析错误");
            log.error(e.getMessage(), e);
            return result;
        }
    }

    /**
     * 获取部门列表
     * 
     * @return
     */
    public QYWeixinDepartment getDepartments() {
        QYWeixinDepartment result = null;
        for (int i = 0; i < TRY_TIME; i++) {
            result = _getDepartments();
            if (result.isSuccess()) {
                return result;
            }
            if (result.isAccessTokenExpired() || result.isInvalidCredential()) {
                resetWeixinAccessToken();
            }
        }
        return result;
    }

    private QYWeixinDepartment _getDepartments() {
        // https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://qyapi.weixin.qq.com/cgi-bin/department/list");
        sb.append("?access_token=").append(getAccessToken());
        String responseBodyAsString = HttpTools.get(sb.toString());
        log.error("_getDepartments : " + responseBodyAsString);
        Gson gson = new Gson();
        try {
            QYWeixinDepartment bean = gson.fromJson(responseBodyAsString, QYWeixinDepartment.class);
            return bean;
        } catch (Exception e) {
            QYWeixinDepartment result = new QYWeixinDepartment();
            result.setErrcode(47001);
            result.setErrmsg("json解析错误");
            log.error(e.getMessage(), e);
            return result;
        }
    }

    // ================================================
    // ============== 成员管理API end ==================
    // ================================================

    // ================================================
    // ============== 发送消息 start ==================
    // ================================================

    /**
     * 发送消息
     * 
     * @param weixinMessage
     * @return
     * @throws Exception
     */
    public QYWeixinSendMessageResult sendMessage(QYWeixinMessage weixinMessage) {
        if (weixinMessage == null) {
            return null;
        }
        weixinMessage.setAgentid(weixinConfig.getAgentid());
        QYWeixinSendMessageResult result = null;
        for (int i = 0; i < TRY_TIME; i++) {
            result = _sendMessage(weixinMessage);
            if (result.isSuccess()) {
                return result;
            }
            if (result.isReplyMessageTimeout()) {
                return result;
            }
            if (result.isAccessTokenExpired() || result.isInvalidCredential()) {
                resetWeixinAccessToken();
            }
        }
        return result;
    }

    /**
     * @param weixinMessage
     * @return
     */
    private QYWeixinSendMessageResult _sendMessage(QYWeixinMessage weixinMessage) {
        StringBuilder sb = new StringBuilder(400);
        sb.append("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=");
        sb.append(getAccessToken());
        String object2Json = JsonTools.object2Json(weixinMessage);
        String content = HttpTools.send(sb.toString(), object2Json);
        log.error("_sendMessage : " + content);
        Gson gson = new Gson();
        try {
            QYWeixinSendMessageResult bean = gson.fromJson(content, QYWeixinSendMessageResult.class);
            return bean;
        } catch (Exception e) {
            QYWeixinSendMessageResult result = new QYWeixinSendMessageResult();
            result.setErrcode(47001);
            result.setErrmsg("json解析错误");
            log.error(e.getMessage(), e);
            return result;
        }
    }

    /**
     * 给指定人发送文本消息
     * 
     * @param userId
     * @return
     */
    public QYWeixinSendMessageResult sendTextMessage(String userId, String content, boolean safe) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(content)) {
            return null;
        }
        QYWeixinTextMessage message = QYWeixinTextMessage.createSendToUsers(userId, content, safe);
        return sendMessage(message);
    }

    /**
     * 给指定人s发送文本消息
     * 
     * @param userIds
     * @param content
     * @param safe
     * @return
     */
    public QYWeixinSendMessageResult sendTextMessage(List<String> userIds, String content, boolean safe) {
        if (userIds == null || userIds.isEmpty() || StringUtils.isEmpty(content)) {
            return null;
        }
        QYWeixinTextMessage message = QYWeixinTextMessage.createSendToUsers(userIds, content, safe);
        return sendMessage(message);
    }

    /**
     * 向所有人发送消息
     * 
     * @param content
     * @param safe
     * @return
     */
    public QYWeixinSendMessageResult sendTextMessage4All(String content, boolean safe) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        QYWeixinTextMessage message = QYWeixinTextMessage.createSendToAll(content, safe);
        return sendMessage(message);
    }

    /**
     * 下载一个文件 http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID
     * 
     * @param mediaId
     * @param saveFile
     */
    public WeixinDownloadResult download(String mediaId, File saveFile) {
        if (StringUtils.isEmpty(mediaId) || saveFile == null) {
            return null;
        }
        WeixinDownloadResult result = null;
        for (int i = 0; i < TRY_TIME; i++) {
            result = _download(mediaId, saveFile);
            if (result.isSuccess()) {
                return result;
            }
            if (result.isAccessTokenExpired() || result.isInvalidCredential()) {
                resetWeixinAccessToken();
            }
        }
        return result;
    }

    /**
     * @param mediaId
     * @param saveFile
     * @return
     */
    private WeixinDownloadResult _download(String mediaId, File saveFile) {
        StringBuilder sb = new StringBuilder(400);
        sb.append("https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=");
        sb.append(getAccessToken());
        sb.append("&media_id=").append(mediaId);
        String download = HttpTools.download(sb.toString(), saveFile);
        if (StringUtils.isBlank(download)) {
            return new WeixinDownloadResult();
        }
        JSONObject jsonObject = JSONObject.fromObject(download);
        Object bean = JSONObject.toBean(jsonObject, WeixinDownloadResult.class);
        return (WeixinDownloadResult) bean;
    }
}
