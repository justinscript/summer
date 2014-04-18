/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.result;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller和Widget的统一返回值，用来描述返回的视图及数据
 * 
 * @author zxc Apr 12, 2013 4:44:45 PM
 */
public interface WebResult {

    public static final String CONTENT_TYPE        = "_$_content_type_$_";
    public static final String CHARSET             = "_$_charset_$_";

    public static final String FORWARD_URL_PREFIX  = "forward:";
    public static final String REDIRECT_URL_PREFIX = "redirect:";

    /**
     * 返回当前结果中的参数
     */
    public Map<String, String> getParameters();

    /**
     * 返回视图的名称
     * 
     * @return
     */
    public String getView();

    /**
     * 增加新的参数
     * 
     * @param name 参数名称
     * @param value 参数值
     * @return
     */
    public WebResult addParam(String name, String value);

    /**
     * 返回内容类型
     * 
     * @return
     */
    public String getContentType();

    /**
     * 设置内容类型
     * 
     * @param contentType
     */
    public void setContentType(String contentType);

    /**
     * 设定响应的字符编码
     * 
     * @param charset
     */
    public void setCharacterEncoding(String charset);

    /**
     * 返回字符编码
     * 
     * @return
     */
    public String getCharacterEncoding();

    /**
     * 得到名称空间
     * 
     * @return
     */
    public String getNameSpace();

    /**
     * 设置名称空间
     * 
     * @param nameSpace
     */
    public void setNameSpace(String nameSpace);

    /**
     * 以自定义的方式处理HttpServletResponse 当用户自己处理了response的输出后，应该返回true；否则返回false
     * 
     * @param response
     */
    public abstract boolean onResponse(HttpServletResponse response);
}
