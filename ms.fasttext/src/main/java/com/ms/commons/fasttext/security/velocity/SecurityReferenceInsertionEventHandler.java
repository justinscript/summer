/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.security.velocity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.ContextAware;
import org.apache.velocity.util.RuntimeServicesAware;

import com.ms.commons.fasttext.codec.HtmlFastEntities;
import com.ms.commons.fasttext.codec.JavaScriptEncode;
import com.ms.commons.fasttext.json.JSONArray;
import com.ms.commons.fasttext.json.JSONObject;
import com.ms.commons.fasttext.security.velocity.directive.DirectiveType;
import com.ms.commons.fasttext.security.xss.Policy;
import com.ms.commons.fasttext.security.xss.PolicyException;
import com.ms.commons.fasttext.security.xss.XssXppScanner;

/**
 * @author zxc Apr 12, 2013 3:35:38 PM
 */
public class SecurityReferenceInsertionEventHandler extends RootReferenceFilter implements RuntimeServicesAware, ContextAware, ReferenceInsertionEventHandler {

    private static final Log  logger = LogFactory.getLog(SecurityReferenceInsertionEventHandler.class);
    private XssXppScanner     scanner;
    private RuntimeServices   rs;
    private Context           context;
    private static Class<?>[] cls    = null;

    public SecurityReferenceInsertionEventHandler() {
        try {
            scanner = new XssXppScanner(this.definePolicy());
        } catch (PolicyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 如果用户需要自己定义的策略文件， 可以继承SecurityReferenceInsertionEventHandler， 然后覆盖 protected Policy definePolicy() 以生成策略对象
     * 
     * @return
     * @throws PolicyException
     */
    protected Policy definePolicy() throws PolicyException {
        return Policy.getLoosePolicyInstance();
    }

    /**
     * @see ReferenceAction
     */
    public Object referenceInsert(String reference, Object value) {
        if (value == null) {
            return null; // fix nullpointer bug of value.getClass() and value.toString()
        }
        // TODO
        /** 国际站的国际化资源包括HTML，安全的不需是要转义 */
        if (reference != null
            && (reference.startsWith("$RESOURCE_BUNDLE") || reference.startsWith("$!RESOURCE_BUNDLE")
                || reference.startsWith("${RESOURCE_BUNDLE") || reference.startsWith("$!{RESOURCE_BUNDLE"))) {
            return value;
        }
        // END TODO
        String retValue = null;
        boolean flag = false;
        do {
            if (flag = value instanceof String) {
                retValue = (String) value;
                break; // 直接跳到jmp pointer处
            }
            if (cls == null || cls.length == 0) {
                break; // 直接跳到jmp pointer处
            }
            for (int i = 0; i < cls.length; i++) {
                if (flag = cls[i] == value.getClass()) {
                    retValue = value.toString();
                    break; // 跳出循环后直接跳到jmp pointer处
                }
            }
        } while (false);

        // jmp pointer

        if (flag) {
            DirectiveType dt = (DirectiveType) context.get(DirectiveType.typeKey);
            // 目前只有一个NO_ESCAPE
            ReferenceAction action = getReferenceAction(reference);
            if (dt != null && action == ReferenceAction.NOT_DEFINED) {

                // 当dt!= null && action != action.NOT_DEFINED时，说明是noescape的BLOCK中的自定义宏
                // 所以先让里面的自定义宏发生作用,否则直接return出去:

                return dt.escape(retValue);
            }
            switch (action) {

                case HTML_XSS_FILTER:
                    retValue = this.scanner.scan(retValue);
                    break;

                case JAVASCRIPT_ENCODE:
                    retValue = JavaScriptEncode.escapedJavaScriptValue(retValue);
                    break;
                case XML_ENCODE:
                    retValue = HtmlFastEntities.XML.escape(retValue);
                    break;
                case LITERAL:
                case URL_FILTER:// do nothing
                    break;
                case HTML_ENCODE:
                    retValue = HtmlFastEntities.HTML40.escape(retValue);
                    break;
                case PURE_TEXT_HTML_ENCODE:
                    retValue = HtmlFastEntities.HTML40.escape(retValue);
                    break;
                case JSON_ENCODE:
                case JSON_HTML_ENCODE:
                    if (logger.isErrorEnabled()) {
                        logger.error("reference=" + reference
                                     + " #SJSON() and #JSON_DOM() accept JSONObject and JSONArray only");
                    }
                    retValue = reference;
                    break;
                case NOT_DEFINED:
                default:
                    // 在默认的情况下，　日志记录，　可能是html的输出
                    if (logger.isDebugEnabled()) {
                        if (retValue.indexOf('<') > -1 && retValue.indexOf('>') > -1) {
                            logger.debug("reference=" + reference + " maybe is a html content");
                        }
                    }
                    retValue = HtmlFastEntities.HTML40.escape(retValue);
                    break;
            }
            return retValue;
        } else {
            retValue = null;
            ReferenceAction action = getReferenceAction(reference);
            switch (action) {
                case JSON_ENCODE:
                    if (value instanceof JSONObject) {
                        retValue = ((JSONObject) value).toString(JSONObject.EncodeType.JAVASCRIPT_ENCODE);
                    } else if (value instanceof JSONArray) {
                        retValue = ((JSONArray<?>) value).toString(JSONObject.EncodeType.JAVASCRIPT_ENCODE);

                    } else {
                        if (logger.isErrorEnabled()) {
                            logger.error("reference=" + reference
                                         + " #SJSON() and #JSON_DOM() accept JSONObject and JSONArray only");
                        }
                        retValue = reference;
                    }
                    break;
                case JSON_HTML_ENCODE:
                    if (value instanceof JSONObject) {
                        retValue = ((JSONObject) value).toString(JSONObject.EncodeType.HTML_ENCODE);
                    } else if (value instanceof JSONArray) {
                        retValue = ((JSONArray<?>) value).toString(JSONObject.EncodeType.HTML_ENCODE);
                    } else {
                        if (logger.isErrorEnabled()) {
                            logger.error("reference=" + reference
                                         + " #SJSON() and #JSON_DOM() accept JSONObject and JSONArray only");
                        }
                        retValue = reference;
                    }
                    break;
                default:
                    return value;
            }
            return retValue;
        }

    }

    /**
     * 回调方法，用于注入RuntimeServices
     */
    public void setRuntimeServices(RuntimeServices rs) {
        this.rs = rs;
        init2StringObject();
    }

    /**
     * 回调方法，用于注入Context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 用于从配置文件中获取需要render的类型，默认为String，如果其它类型需要 render请在配置文件中注入: <service name="VelocityService"
     * class="com.alibaba.service.velocity.DefaultVelocityService" earlyInit="true"> <property name="......."
     * value="......"/> <property name="eventCartridge.classes" value="....MyEventHandler" /> <property
     * name="user.rendertype.classes" value="org.axman.Test,org.axman.ObjectString" />
     */
    private void init2StringObject() {
        if (cls != null && cls.length > 0) {
            return;
        }
        String strCls = this.rs.getConfiguration().getString("user.rendertype.classes");
        if (strCls == null) {
            return;
        }
        String[] strArry = strCls.split("[,;:]");
        if (strArry.length == 0) {
            return;
        }
        cls = new Class[strArry.length];
        try {
            for (int i = 0; i < strArry.length; i++) {
                cls[i] = Class.forName(strArry[i]);
            }
        } catch (Exception e) {
            // 从理论上，如果抛出异常就是配置文件本身不正确
            throw new RuntimeException("get velocity.security.classes error: " + e.getMessage());
        }
    }

}

/**
 * <pre>
 * HTML_XSS_FILTER: 使用xss过滤工具输出
 * HTML_ENCODE: 按html encode输出
 * JAVASCRIPT_ENCODE: 按js encode 输出
 * XML_ENCODE: 按xml encode输出
 * LITERAL: 按原来的输出
 * URL_FILTER: 暂时没有做任何编码行为 
 * PURE_TEXT_HTML_ENCODE: 兼容原来的系统使用， 为了避免发生二次编码
 * SJSON: 对json数据作jsencode再输出，避免存在可执行script，确保script不会被执行
 * SJSON_DOM: 对json数据html encode再输出，避免存在可执行script，
 * 确保 1)浏览器直接打开url,script不会被执行,2)javascript调用innerHTML插入html时不被执行
 * NOT_DEFINED: 未定义的， 使用html encode
 * </pre>
 */
enum ReferenceAction {
    HTML_XSS_FILTER, HTML_ENCODE, JAVASCRIPT_ENCODE, XML_ENCODE, LITERAL, URL_FILTER, PURE_TEXT_HTML_ENCODE,
    JSON_ENCODE, JSON_HTML_ENCODE, NOT_DEFINED
}

class RootReferenceFilter {

    static Map<String, ReferenceAction> filter = new HashMap<String, ReferenceAction>();

    static {
        // system define
        filter.put("$screen_placeholder", ReferenceAction.LITERAL);

        // encode part
        filter.put("$!{security_h_t_m_l_z}", ReferenceAction.HTML_XSS_FILTER);
        filter.put("$!{security_x_m_l_z}", ReferenceAction.XML_ENCODE);
        filter.put("$!{security_j_s_z}", ReferenceAction.JAVASCRIPT_ENCODE);
        filter.put("$!{security_p_u_r_e_t_e_x_t_z}", ReferenceAction.HTML_ENCODE);
        filter.put("$!{security_l_i_t_e_r_a_l_z}", ReferenceAction.LITERAL);
        filter.put("$!{security_u_r_l_z}", ReferenceAction.URL_FILTER);
        // for json security
        // #SJSON() //js encode
        // #SJSON_DOM() //html encode
        filter.put("$!{security_j_s_o_n_z}", ReferenceAction.JSON_ENCODE);
        filter.put("$!{security_j_s_o_n_d_o_m_z}", ReferenceAction.JSON_HTML_ENCODE);

    }

    public static ReferenceAction getReferenceAction(String ref) {
        ReferenceAction action = filter.get(ref);
        if (action == null) {
            action = ReferenceAction.NOT_DEFINED;
        }
        return action;
    }
}
