package com.ms.commons.weixin.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.weixin.bean.JsSignature;
import com.ms.commons.weixin.bean.WeixinAccessToken;
import com.ms.commons.weixin.bean.WeixinJSApiTicket;
import com.ms.commons.weixin.cons.WeixinEventType;
import com.ms.commons.weixin.cons.WeixinMsgType;
import com.ms.commons.weixin.request.*;
import com.ms.commons.weixin.response.WeixinResponse;
import com.ms.commons.weixin.tools.SHA1Util;
import com.ms.commons.weixin.tools.Tools;

public abstract class CommonWeixinService {

    protected Logger          log   = LoggerFactory.getLogger(CommonWeixinService.class);
    static final String       UTF_8 = "utf-8";
    private WeixinAccessToken weixinToken;
    private WeixinJSApiTicket weixinJSApiTicket;

    /**
     * 获取APP的Id，企业时是corpid
     * 
     * @return
     */
    abstract String getAppId();

    /**
     * 生成oauth链接
     * 
     * @param redirectUri
     * @return
     */
    public String oauthUrl(String redirectUri) {
        return oauthUrl(redirectUri, false);
    }

    public String oauthUrl(String redirectUri, boolean userinfo) {
        StringBuilder sb = new StringBuilder(500);
        sb.append("https://open.weixin.qq.com/connect/oauth2/authorize");
        sb.append("?appid=").append(getAppId());
        sb.append("&redirect_uri=").append(encode(redirectUri));
        sb.append("&response_type=code");
        if (userinfo) {
            sb.append("&scope=snsapi_userinfo");
        } else {
            sb.append("&scope=snsapi_base");
        }
        // sb.append("&state=abc");
        sb.append("#wechat_redirect");
        return sb.toString();
    }

    // ============================================================
    // ================== weixinToken start =======================
    // ============================================================
    protected String getAccessToken() {
        // 获取token
        WeixinAccessToken weixinAccessToken = getWeixinAccessToken();
        if (weixinAccessToken == null) {
            log.error("WeixinAccessToken is null need init");
            weixinAccessToken = initAccessToken();
            // 保存token
            setWeixinAccessToken(weixinAccessToken);
        }
        if (weixinAccessToken != null && !weixinAccessToken.isEffect()) {
            log.error("WeixinAccessToken is expire need init");
            weixinAccessToken = initAccessToken();
            // 保存token
            setWeixinAccessToken(weixinAccessToken);
        }
        if (weixinAccessToken == null) {
            log.error("WeixinAccessToken is null");
            return "";
        }
        // 获取token 失败
        if (!weixinAccessToken.isSuccess()) {
            log.error("WeixinAccessToken is fail");
            return "";
        }
        log.error("WeixinAccessToken : get access token success");
        return weixinAccessToken.getAccessToken();
    }

    /**
     * 获取 WeixinAccessToken 对象
     * 
     * <pre>
     * 集群部署请覆盖此方法，多jvm公用同一数据，可把对象放入memcache中
     * </pre>
     * 
     * @return
     */
    WeixinAccessToken getWeixinAccessToken() {
        return weixinToken;
    }

    /**
     * 设置 WeixinAccessToken 对象
     * 
     * <pre>
     * 集群部署请覆盖此方法，多jvm公用同一数据，可把对象放入memcache中
     * </pre>
     * 
     * @param weixinAccessToken
     */
    void setWeixinAccessToken(WeixinAccessToken weixinAccessToken) {
        this.weixinToken = weixinAccessToken;
    }

    /**
     * 重置 WeixinAccessToken，可以认为是直接把对象设置为null即可
     * 
     * <pre>
     * 集群部署请覆盖此方法，多jvm公用同一数据可把对象放入memcache中
     * 此方法清除memcache中数据
     * </pre>
     */
    void resetWeixinAccessToken() {
        log.error("WeixinAccessToken REST~~~~~");
        this.weixinToken = null;
    }

    /**
     * 通过API获取WeixinAccessToken
     * 
     * @return
     */
    abstract WeixinAccessToken initAccessToken();

    // ============================================================
    // ================== weixinToken end =========================
    // ============================================================

    // ============================================================
    // ================== WeixinJSApiTicket start =================
    // ============================================================
    /**
     * js权限ticket
     * 
     * <pre>
     * 集群部署请覆盖此方法，多jvm公用同一数据，可把对象放入memcache中
     * </pre>
     * 
     * @return
     */
    WeixinJSApiTicket getWeixinJSApiTicket() {
        return weixinJSApiTicket;
    }

    /**
     * 设置 WeixinJSApiTicket 对象
     * 
     * <pre>
     * 集群部署请覆盖此方法，多jvm公用同一数据，可把对象放入memcache中
     * </pre>
     * 
     * @param weixinJSApiTicket
     */
    void setWeixinJSApiTicket(WeixinJSApiTicket weixinJSApiTicket) {
        this.weixinJSApiTicket = weixinJSApiTicket;
    }

    /**
     * 重置 WeixinJSApiTicket，可以认为是直接把对象设置为null即可
     * 
     * <pre>
     * 集群部署请覆盖此方法，多jvm公用同一数据可把对象放入memcache中
     * 此方法清除memcache中数据
     * </pre>
     */
    void resetWeixinJSApiTicket() {
        log.error("WeixinJSApiTicket REST~~~~~");
        this.weixinJSApiTicket = null;
    }

    /**
     * 获取ticket
     * 
     * @return
     */
    String getJSTicket() {
        // 获取ticket
        WeixinJSApiTicket weixinJSApiTicket = getWeixinJSApiTicket();
        if (weixinJSApiTicket == null) {
            log.error("WeixinJSApiTicket is null need init");
            weixinJSApiTicket = initJSApiTicket();
            // 保存ticket
            setWeixinJSApiTicket(weixinJSApiTicket);
        }
        if (weixinJSApiTicket != null && !weixinJSApiTicket.isEffect()) {
            log.error("WeixinJSApiTicket is expire need init");
            weixinJSApiTicket = initJSApiTicket();
            // 保存ticket
            setWeixinJSApiTicket(weixinJSApiTicket);
        }
        if (weixinJSApiTicket == null) {
            log.error("WeixinJSApiTicket is null");
            return "";
        }
        // 获取token 失败
        if (!weixinJSApiTicket.isSuccess()) {
            log.error("WeixinJSApiTicket is fail");
            return "";
        }
        log.error("WeixinJSApiTicket : get js ticket success");
        return weixinJSApiTicket.getTicket();
    }

    /**
     * 通过API获取WeixinJSApiTicket
     * 
     * @return
     */
    abstract WeixinJSApiTicket initJSApiTicket();

    /**
     * js签名算法
     * 
     * <pre>
     * 签名算法
     * 签名生成规则如下：
     *  参与签名的字段包括noncestr（随机字符串）, 
     *  有效的jsapi_ticket, 
     *  timestamp（时间戳, 
     *  url（当前网页的URL，不包含#及其后面部分。
     *  对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，
     *  使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1。
     *  这里需要注意的是所有参数名均为小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL 转义。
     * 即signature=sha1(string1)。 示例：
     * 
     * noncestr=Wm3WZYTPz0wzccnW
     * jsapi_ticket=sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg
     * timestamp=1414587457
     * url=http://mp.weixin.qq.com
     * </pre>
     * 
     * @param url 当前URL
     * @return
     */
    public JsSignature jsSignature(String url) {
        String noncestr = Tools.createNoncestr();
        String timestamp = Tools.getTime();
        String jsapi_ticket = getJSTicket();
        String signature = _jsSignature(noncestr, jsapi_ticket, timestamp, url);
        return new JsSignature(getAppId(), timestamp, noncestr, signature);
    }

    String _jsSignature(String noncestr, String jsapi_ticket, String timestamp, String url) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("noncestr", noncestr);
        map.put("jsapi_ticket", jsapi_ticket);
        map.put("timestamp", timestamp);
        map.put("url", url);
        String signature = Tools.sortAndjoin(map, false);
        signature = SHA1Util.Sha1(signature);
        return signature;
    }

    // ============================================================
    // ================== WeixinJSApiTicket end ===================
    // ============================================================

    public int getByteSize(String content) {
        if (StringUtils.isEmpty(content)) {
            return 0;
        }
        try {
            // 汉字采用utf-8编码时占3个字节
            return content.getBytes(UTF_8).length;
        } catch (UnsupportedEncodingException e) {
            return 0;
        }
    }

    protected String encode(String url) {
        try {
            return URLEncoder.encode(url, UTF_8);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    public static StringBuilder addLink(StringBuilder source, String title, String link) {
        source.append("<a href='").append(link).append("'>");
        source.append(title).append("</a>");
        return source;
    }

    /**
     * 微信主动推送的消息
     * 
     * @param body
     * @return
     */
    String doService(String body) {
        if (Tools.isEmpty(body)) {
            return null;
        }
        Map<String, String> datas = Tools.paserXML(body);
        String msgType = datas.get("MsgType");
        WeixinMsgType type = WeixinMsgType.getMsgType(msgType);
        if (type == null) {
            return null;
        }
        WeixinResponse response = null;
        switch (type) {
            case text:
                response = textRequest(new WeixinTextRequest(datas));
                break;
            case voice:
                response = voiceRequest(new WeixinVoiceRequest(datas));
                break;
            case event:
                response = eventResponse(datas);
                break;
            case location:
                response = locationRequest(new WeixinLocationRequest(datas));
                break;
            default:
                break;
        }
        if (response != null) {
            try {
                return response.toXML();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private WeixinResponse eventResponse(Map<String, String> datas) {
        String string = datas.get("Event");
        WeixinEventType eventType = WeixinEventType.getEventType(string);
        if (eventType == null) {
            return null;
        }
        WeixinResponse response = null;
        switch (eventType) {
            case subscribe:
                response = eventSubscribeRequest(new WeixinEventSubscribeRequest(datas));
                break;
            case unsubscribe:
                response = eventSubscribeRequest(new WeixinEventSubscribeRequest(datas));
                break;
            case LOCATION:
                response = eventLocationRequest(new WeixinEventLocationRequest(datas));
                break;
            case CLICK:
                response = eventClickRequest(new WeixinEventClickRequest(datas));
                break;
            case VIEW:
                response = eventViewRequest(new WeixinEventViewRequest(datas));
                break;
            default:
                break;
        }
        return response;
    }

    /**
     * 处理微信的text请求
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse textRequest(WeixinTextRequest request);

    /**
     * 处理图片消息
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse imageRequest(WeixinImageRequest request);

    /**
     * 处理语音消息
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse voiceRequest(WeixinVoiceRequest request);

    /**
     * 处理视频消息
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse videoRequest(WeixinVideoRequest request);

    /**
     * 地理位置请求
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse locationRequest(WeixinLocationRequest request);

    /**
     * 链接消息
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse linkRequest(WeixinLinkRequest request);

    // --------------------------event--------------------

    /**
     * 微信关注事件
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse eventSubscribeRequest(WeixinEventSubscribeRequest request);

    /**
     * 扫描事件
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse eventScanRequest(WeixinEventScanRequest request);

    /**
     * 上报地理位置事件
     * 
     * @param weixinEventLocationRequest
     * @return
     */
    public abstract WeixinResponse eventLocationRequest(WeixinEventLocationRequest request);

    /**
     * 点击菜单拉取消息事件
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse eventClickRequest(WeixinEventClickRequest request);

    /**
     * 点击菜单跳转链接事件
     * 
     * @param request
     * @return
     */
    public abstract WeixinResponse eventViewRequest(WeixinEventViewRequest request);

}
