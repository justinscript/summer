package com.ms.commons.weixin.service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ms.commons.weixin.bean.*;
import com.ms.commons.weixin.cons.WeixinUploadType;
import com.ms.commons.weixin.message.WeixinMessage;
import com.ms.commons.weixin.message.WeixinTextMessage;
import com.ms.commons.weixin.pay.PayNotifyInfo;
import com.ms.commons.weixin.pay.PayOrder;
import com.ms.commons.weixin.pay.PayUnifiedOrderResult;
import com.ms.commons.weixin.tools.*;

/**
 * 微信操作的封装
 * 
 * <pre>
 * 注意：微信accesstoken必须唯一，而且是多虚拟机中必须唯一
 *      如果是集群部署：请子类覆盖
 *      getWeixinAccessToken()  setWeixinAccessToken() resetWeixinAccessToken()
 *      getWeixinJSApiTicket()  setWeixinJSApiTicket() resetWeixinJSApiTicket()
 *      6个方法，可以使用memcache或数据库来实现WeixinAccessToken,weixinJSApiTicket对象的存储
 * </pre>
 */
public abstract class WeixinService extends CommonWeixinService {

    private static final int TRY_TIME = 3;

    private static Logger    LOG      = LoggerFactory.getLogger(WeixinService.class);

    private WeixinConfig     weixinConfig;

    /**
     * 构造器
     * 
     * @param appid
     * @param secret
     * @param token
     */
    public WeixinService(String appid, String secret, String token, boolean service) {
        weixinConfig = new WeixinConfig(appid, secret, token, service);
    }

    /**
     * 设置支付配置信息
     * 
     * @param paySignKey
     * @param partnerKey
     * @param partnerId
     * @param notifyUrl
     */
    public void setPayConfig(String partnerKey, String partnerId, String notifyUrl) {
        weixinConfig.setPayConfig(partnerKey, partnerId, notifyUrl);
    }

    /**
     * 发红包时需要用到的数字签名证书
     * 
     * @param sslCaFile
     * @param sslCertFile
     * @param certPassword
     */
    public void setSSL(File sslCaFile, File sslCertFile, String certPassword) {
        weixinConfig.setSSL(sslCaFile, sslCertFile, certPassword);
    }

    /**
     * 获取APP的Id，企业时是corpid
     * 
     * @return
     */
    String getAppId() {
        return weixinConfig.getAppid();
    }

    protected WeixinConfig getWeixinConfig() {
        return weixinConfig;
    }

    /**
     * 验证请求是否是从微信发送过来的 几个参数都是从请求参数中获取的
     * 
     * @param signature 微信加密签名
     * @param timestamp 时间轴
     * @param nonce 随机数
     * @return
     */
    public boolean validateSignature(String signature, String timestamp, String nonce) {
        if (StringUtils.isEmpty(signature) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce)) {
            return false;
        }
        String[] authStrs = new String[] { weixinConfig.getToken(), timestamp, nonce };
        Arrays.sort(authStrs);
        StringBuilder sb = new StringBuilder(1000);
        for (String string : authStrs) {
            sb.append(string);
        }
        String validateSignature = DigestUtils.sha1Hex(sb.toString());
        return Tools.equals(signature, validateSignature);
    }

    /**
     * 微信主动推送的消息
     * 
     * @param body
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public String service(String body) throws JDOMException, IOException {
        return super.doService(body);
    }

    /**
     * 创建WeixinAccessToken
     * 
     * @return
     */
    synchronized WeixinAccessToken initAccessToken() {
        LOG.error("WeixinAccessToken initAccessToken start~~~~~");
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential");
        sb.append("&appid=").append(weixinConfig.getAppid());
        sb.append("&secret=").append(weixinConfig.getSecret());
        String result = HttpTools.get(sb.toString());
        if (Tools.isEmpty(result)) {
            LOG.error("WeixinAccessToken initAccessToken end fail~~~~~");
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        Object bean = JSONObject.toBean(jsonObject, WeixinAccessToken.class);
        WeixinAccessToken token = (WeixinAccessToken) bean;
        LOG.error("WeixinAccessToken initAccessToken end~~~~~");
        return token;
    }

    /**
     * https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi
     * 
     * @return
     */
    synchronized WeixinJSApiTicket initJSApiTicket() {
        log.error("WeixinJSApiTicket initJSApiTicket start~~~~~");
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=");
        sb.append(getAccessToken());
        sb.append("&type=jsapi");
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
     * 获取微信用户信息
     * 
     * <pre>
     * https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
     * </pre>
     * 
     * @param openId
     * @return
     */
    public WeixinUser getUserInfo(String openId) {
        WeixinUser result = null;
        for (int i = 0; i < TRY_TIME; i++) {
            result = _getUserInfo(openId);
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
     * @param openId
     * @return
     */
    private WeixinUser _getUserInfo(String openId) {
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://api.weixin.qq.com/cgi-bin/user/info?");
        sb.append("&access_token=").append(getAccessToken());
        sb.append("&openid=").append(openId);
        sb.append("&lang=zh_CN");
        String responseBodyAsString = HttpTools.get(sb.toString());
        Gson gson = new Gson();
        WeixinUser bean = gson.fromJson(responseBodyAsString, WeixinUser.class);
        return bean;
    }

    /**
     * 没有关注公众号的前提下，使用snsapi_userinfo，通过用户授权来获取用户信息
     * 
     * @param openId
     * @param accessToken
     * @return
     */
    public WeixinUser getSnsUserInfo(String openId, String accessToken) {
        return _getSnsUserInfo(openId, accessToken);
    }

    private WeixinUser _getSnsUserInfo(String openId, String accessToken) {
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://api.weixin.qq.com/sns/userinfo?");
        sb.append("&access_token=").append(accessToken);
        sb.append("&openid=").append(openId);
        sb.append("&lang=zh_CN");
        String responseBodyAsString = HttpTools.get(sb.toString());
        Gson gson = new Gson();
        WeixinUser bean = gson.fromJson(responseBodyAsString, WeixinUser.class);
        return bean;
    }

    /**
     * *************** 发送消息 *****************
     */

    /**
     * 发送消息
     * 
     * <pre>
     * https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN
     * </pre>
     * 
     * @param weixinMessage
     * @return
     * @throws Exception
     */
    public WeixinSendMessageResult sendMessage(WeixinMessage weixinMessage) {
        WeixinSendMessageResult result = null;
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
    private WeixinSendMessageResult _sendMessage(WeixinMessage weixinMessage) {
        StringBuilder sb = new StringBuilder(400);
        sb.append("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=");
        sb.append(getAccessToken());
        String object2Json = JsonTools.object2Json(weixinMessage);
        String result = HttpTools.send(sb.toString(), object2Json);
        JSONObject jsonObject = JSONObject.fromObject(result);
        Object bean = JSONObject.toBean(jsonObject, WeixinSendMessageResult.class);
        return (WeixinSendMessageResult) bean;
    }

    /**
     * 向某个用户发送一个文本消息
     * 
     * @param touser
     * @param message
     * @return
     */
    public WeixinSendMessageResult sendTextMessage(String touser, String message) {
        if (StringUtils.isEmpty(touser) || StringUtils.isEmpty(message)) {
            return null;
        }
        WeixinTextMessage textMessage = new WeixinTextMessage(touser, message);
        return sendMessage(textMessage);
    }

    /**
     * 向某用户发送一个带链接的消息，整个消息是一个链接
     * 
     * @param touser
     * @param message
     * @param link
     * @return
     */
    public WeixinSendMessageResult sendLinkMessage(String touser, String message, String link) {
        if (StringUtils.isEmpty(touser) || StringUtils.isEmpty(message)) {
            return null;
        }
        WeixinTextMessage textMessage;
        if (StringUtils.isNotEmpty(link)) {
            StringBuilder sb = new StringBuilder(200);
            addLink(sb, message, link);
            textMessage = new WeixinTextMessage(touser, sb.toString());
        } else {
            textMessage = new WeixinTextMessage(touser, message);
        }
        return sendMessage(textMessage);
    }

    /**
     * 上传一个文件
     * 
     * <pre>
     * 图片（image）: 128K，支持JPG格式
     * 语音（voice）：256K，播放长度不超过60s，支持AMR\MP3格式
     * 视频（video）：1MB，支持MP4格式
     * 缩略图（thumb）：64KB，支持JPG格式
     * </pre>
     * 
     * @param file
     * @param type 图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     */
    public WeixinUploadResult upload(File file, WeixinUploadType type) {
        if (file == null || type == null) {
            return null;
        }
        String suffix = Tools.getSuffix(file.getName());
        if (!type.isAllowed(suffix, file.length())) {
            return null;
        }
        WeixinUploadResult result = null;
        for (int i = 0; i < TRY_TIME; i++) {
            result = _upload(file, type);
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
     * @param file
     * @param type
     * @return
     */
    private WeixinUploadResult _upload(File file, WeixinUploadType type) {
        StringBuilder sb = new StringBuilder(400);
        sb.append("http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=");
        sb.append(getAccessToken());
        sb.append("&type=").append(type.name());
        String upload = HttpTools.upload(sb.toString(), file);
        JSONObject jsonObject = JSONObject.fromObject(upload);
        Object bean = JSONObject.toBean(jsonObject, WeixinUploadResult.class);
        return (WeixinUploadResult) bean;
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
        sb.append("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=");
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

    /**
     * 长链接变短链接
     * 
     * <pre>
     * curl -d "{\"action\":\"long2short\",\"long_url\":\"http://wap.koudaitong.com/v2/showcase/goods?alias=128wi9shh&spm=h56083&redirect_count=1\"}" 
     * "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=HFhtTnTmQTy-k00N-siDL5dKo9E42AC43oYt5n3SAya6V1Ti4CnaMuqmvy0Bna1XbLy2YEziCIO2hWS8nbi-fnTEH5t_ZxfP5UVx3pSG1A4"
     * </pre>
     * 
     * @param longUrl 需要转换的长链接，支持http://、https://、weixin://wxpay 格式的url
     * @return
     */
    public String long2short(String longUrl) {
        if (Tools.isEmpty(longUrl)) {
            return longUrl;
        }
        StringBuilder sb = new StringBuilder(400);
        sb.append("https://api.weixin.qq.com/cgi-bin/shorturl?access_token=");
        sb.append(getAccessToken());
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "long2short");
        params.put("long_url", longUrl);
        String body = JSONObject.fromObject(params).toString();
        String post = HttpTools.send(sb.toString(), body);
        JSONObject jsonObject = JSONObject.fromObject(post);
        Object bean = JSONObject.toBean(jsonObject, WeixinShortUrlResult.class);
        WeixinShortUrlResult result = (WeixinShortUrlResult) bean;
        if (result == null || !result.isSuccess()) {
            LOG.error("Weixin-long2short fail" + result);
        } else {
            LOG.error("Weixin-long2short success" + result);
        }
        return result == null ? null : result.getShort_url();
    }

    /**
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=
     * authorization_code
     * 
     * @param code
     * @return
     */
    public OAuthResult oauthResult(String code) {
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://api.weixin.qq.com/sns/oauth2/access_token");
        sb.append("?appid=").append(weixinConfig.getAppid());
        sb.append("&secret=").append(weixinConfig.getSecret());
        sb.append("&code=").append(code);
        sb.append("&grant_type=authorization_code");
        String s = HttpTools.get(sb.toString());
        LOG.error("OAuthResult:  " + s);
        JSONObject jsonObject = JSONObject.fromObject(s);
        Object bean = JSONObject.toBean(jsonObject, OAuthResult.class);
        return (OAuthResult) bean;
    }

    /**
     * -------------------- 支付----------------------
     */

    /**
     * 生成weixin支付支付信息
     * 
     * @param title 标题内容
     * @param tradeSerNum 订单号
     * @param price 价格
     * @param openId 用户微信id
     * @return
     */
    public String payCode(String title, String tradeSerNum, Integer price, String openId) {
        LOG.error("title : " + title);
        LOG.error("tradeSerNum : " + tradeSerNum);
        LOG.error("price : " + price);
        LOG.error("openId : " + openId);
        PayUnifiedOrderResult unifiedorder = payUnifiedOrder(title, tradeSerNum, price, openId);
        if (!unifiedorder.isSuccess()) {
            return null;
        }
        HashMap<String, String> result = new HashMap<String, String>();
        // 参与签名的参数有appId, timeStamp, nonceStr, package, signType
        result.put("appId", weixinConfig.getAppid());
        result.put("timeStamp", Long.toString(new Date().getTime() / 1000));
        result.put("nonceStr", Tools.createNoncestr());
        result.put("package", "prepay_id=" + unifiedorder.getPrepay_id());
        result.put("signType", "MD5");
        String signature = signature(result);
        result.put("paySign", signature);// 加入paySign
        result.remove("appId");// 去掉APPID
        LOG.error("payCode4payCode4");
        return JsonTools.object2Json(result);
    }

    /**
     * 创建微信支付订单号
     * 
     * @param title
     * @param tradeSerNum
     * @param price
     * @param openId
     * @return
     */
    public PayUnifiedOrderResult payUnifiedOrder(String title, String tradeSerNum, Integer price, String openId) {
        Map<String, String> packageMap = new HashMap<String, String>();
        packageMap.put("appid", weixinConfig.getAppid());// 微信分配的公众账号ID
        packageMap.put("mch_id", weixinConfig.getPartnerId());// 微信支付分配的商户号
        // packageMap.put("device_info", null);// 微信支付分配的终端设备号，商户自定义 (非必须)
        packageMap.put("nonce_str", Tools.createNoncestr());// 随机字符串
        packageMap.put("body", title);// 商品或支付单简要描述
        // packageMap.put("detail", null);// 商品名称明细列表 (非必须)
        // packageMap.put("attach", null);// 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据 (非必须)
        packageMap.put("out_trade_no", tradeSerNum);// 商户系统内部的订单号,32个字符内、可包含字母
        packageMap.put("fee_type", "CNY");// 默认人民币：CNY(非必须)
        packageMap.put("total_fee", "" + price);// 订单总金额，只能为整数
        packageMap.put("spbill_create_ip", "127.0.0.1");// 终端IP
        // packageMap.put("time_start", null);// 交易起始时间(非必须)
        // packageMap.put("time_expire", null);// 交易结束时间(非必须)
        // packageMap.put("goods_tag", null);// 商品标记(非必须)
        packageMap.put("notify_url", weixinConfig.getNotifyUrl());// 接收微信支付异步通知回调地址
        packageMap.put("trade_type", "JSAPI");// 交易类型
        // packageMap.put("product_id", null);// trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。(非必须)
        packageMap.put("openid", openId);// 此参数必传，用户在商户appid下的唯一标识
        // 生成签名
        String signature = signature(packageMap);
        packageMap.put("sign", signature);// 把签名放入map
        String asXML = Tools.asXML(packageMap);
        LOG.error("---start--");
        LOG.error(asXML);
        String send = HttpTools.send("https://api.mch.weixin.qq.com/pay/unifiedorder", asXML);
        LOG.error(send);
        Map<String, String> datas = Tools.paserXML(send);
        LOG.error(datas != null ? datas.toString() : "datas is null");
        LOG.error("---end--");
        PayUnifiedOrderResult result = new PayUnifiedOrderResult(datas, validateResult(datas, "sign"));
        return result;
    }

    /**
     * 解析weixin支付的主动通知数据
     * 
     * @param body
     * @return
     */
    public PayNotifyInfo payNotify(String body) {
        if (StringUtils.isEmpty(body)) {
            return null;
        }
        LOG.error("----payNotify start ------");
        LOG.error(body);
        Map<String, String> datas = Tools.paserXML(body);
        LOG.error(datas.toString());
        PayNotifyInfo payNotifyInfo = new PayNotifyInfo(datas, validateResult(datas, "sign"));
        LOG.error(ToStringBuilder.reflectionToString(payNotifyInfo));
        LOG.error("----payNotify end ------");
        return payNotifyInfo;
    }

    /**
     * weixin支付主动通知响应
     * 
     * @param success 成功
     * @param msg 失败时的错误信息
     * @return
     */
    public String payNotifyResult(boolean success, String msg) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("return_code", success ? "SUCCESS" : "FAIL");
        map.put("return_msg", msg);
        return Tools.asXML(map);
    }

    /**
     * 查询weixin支付订单信息 out_trade_no 和 必须填写一个
     * 
     * @param out_trade_no 自己的订单号
     * @param transaction_id 微信交易号（此号优先）
     * @return
     */
    public PayOrder payOrderQuery(String out_trade_no, String transaction_id) {
        if (StringUtils.isEmpty(out_trade_no) && StringUtils.isEmpty(transaction_id)) {
            return null;
        }
        LOG.error("---payOrderQuery start--");
        Map<String, String> packageMap = new HashMap<String, String>();
        packageMap.put("appid", weixinConfig.getAppid());// 微信分配的公众账号ID
        packageMap.put("mch_id", weixinConfig.getPartnerId());// 微信支付分配的商户号
        packageMap.put("transaction_id", transaction_id);// 微信的订单号，优先使用
        packageMap.put("out_trade_no", out_trade_no);// 商户系统内部的订单号，当没提供transaction_id时需要传这个。
        packageMap.put("nonce_str", Tools.createNoncestr());
        String sign = signature(packageMap);
        packageMap.put("sign", sign);
        String body = Tools.asXML(packageMap);
        String send = HttpTools.send("https://api.mch.weixin.qq.com/pay/orderquery", body);
        LOG.error(send);
        Map<String, String> datas = Tools.paserXML(send);
        LOG.error(datas.toString());
        PayOrder payOrder = new PayOrder(datas, validateResult(datas, "sign"));
        LOG.error(ToStringBuilder.reflectionToString(payOrder));
        LOG.error("---payOrderQuery end--");
        return payOrder;
    }

    /**
     * 发送红包 必须设置数字证书，不然无法发送，直接返回CA证书出错
     * 
     * @param redPack
     * @return
     */
    public SendRedPackResult sendRedPack(RedPacketInfo redPack) {
        if (weixinConfig.getSslCaFile() == null || weixinConfig.getSslCertFile() == null
            || StringUtils.isEmpty(weixinConfig.getCertPassword())) {
            Map<String, String> datas = new HashMap<String, String>();
            datas.put("return_code", "FAIL");
            datas.put("return_msg", "CA证书出错");
            datas.put("return_code", "FAIL");
            datas.put("err_code", "NOAUTH");
            datas.put("err_code_des", "CA证书出错");
            SendRedPackResult result = new SendRedPackResult(datas, false);
            return result;
        }
        Map<String, String> packageMap = new HashMap<String, String>();
        // 随机字符串
        packageMap.put("nonce_str", Tools.createNoncestr());
        // 商户订单号（每个订单号必须唯一）组成： mch_id+yyyymmdd+10 位一天内不能重复的数字。
        packageMap.put("mch_billno", redPack.getMchBillno());
        // 商户号 微信支付分配的商户号
        packageMap.put("mch_id", getWeixinConfig().getPartnerId());
        // 公众账号 appid
        packageMap.put("wxappid", getWeixinConfig().getAppid());
        // 提供方名称
        packageMap.put("nick_name", redPack.getNickName());
        // 红包发送者名称
        packageMap.put("send_name", redPack.getSendName());
        // 接收红包的用户 用户在wxappid下的openid
        packageMap.put("re_openid", redPack.getOpenId());
        // 付款金额，单位分
        String price = String.valueOf(redPack.getPrice());
        packageMap.put("total_amount", price);
        // 最小红包金额，单位分
        packageMap.put("min_value", price);
        // 最大红包金额，单位分
        packageMap.put("max_value", price);
        // 红包収放总人数
        packageMap.put("total_num", "1");
        // 红包祝福语
        packageMap.put("wishing", redPack.getWishing());
        // 调用接口的机器 Ip 地址
        packageMap.put("client_ip", StringUtils.isNotEmpty(redPack.getClientIp()) ? redPack.getClientIp() : "127.0.0.1");
        // 活劢名称
        packageMap.put("act_name", redPack.getActName());
        // 备注信息
        packageMap.put("remark", redPack.getRemark());
        // 自定义分享信息，暂不支持（微信红包API中说明）
        // 商户logo的url
        // packageMap.put("logo_imgurl", redPack.getLogoImgurl());
        // 分享文案
        // packageMap.put("share_content", redPack.getShareContent());
        // 分享链接
        // packageMap.put("share_url", redPack.getShareUrl());
        // 分享的图片
        // packageMap.put("share_imgurl", redPack.getShareImgurl());
        String signature = signature(packageMap);
        packageMap.put("sign", signature);// 把签名放入map
        String asXML = Tools.asXML(packageMap);
        log.error("---sendRedPack start--");
        log.error(asXML);
        TenpayHttpClient client = new TenpayHttpClient();

        client.setCaInfo(weixinConfig.getSslCaFile());
        client.setCertInfo(weixinConfig.getSslCertFile(), weixinConfig.getCertPassword());
        boolean success = client.callHttpsPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack", asXML);
        if (success) {
            String resContent = client.getResContent();
            log.error(resContent);
            Map<String, String> datas = Tools.paserXML(resContent);
            log.error(datas != null ? datas.toString() : "datas is null");
            log.error("---sendRedPack end--");
            SendRedPackResult result = new SendRedPackResult(datas, true);
            return result;
        } else {
            Map<String, String> datas = new HashMap<String, String>();
            datas.put("return_code", "FAIL");
            datas.put("return_msg", "CA证书出错");
            datas.put("return_code", "FAIL");
            datas.put("err_code", "PARAM_ERROR");
            datas.put("err_code_des", client.getErrInfo());
            SendRedPackResult result = new SendRedPackResult(datas, false);
            return result;
        }
    }

    /**
     * 签名
     * 
     * @param map
     * @return
     */
    protected String signature(Map<String, String> map) {
        // 1.对所有传入参数按照字段名的ASCII码从小到大排序（字典序）后
        String string1 = Tools.sortAndjoin(map, false);
        // 2.在string1最后拼接上key=paternerKey得到stringSignTemp字符串，
        String stringSignTemp = string1 + "&key=" + weixinConfig.getPartnerKey();
        // 3.并对stringSignTemp进行md5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。
        String signValue = MD5Util.MD5(stringSignTemp).toUpperCase();
        return signValue;
    }

    /**
     * 验证交易过程微信返回数据的正确性
     * 
     * @param resultMap
     * @param signKey
     * @return
     */
    protected boolean validateResult(Map<String, String> resultMap, String signKey) {
        String sign = resultMap.get(signKey);
        if (StringUtils.isEmpty(sign)) {
            return false;
        }
        HashMap<String, String> map = new HashMap<String, String>(resultMap);
        map.remove(signKey);
        String signature = signature(map);
        return sign.equals(signature);
    }

}
