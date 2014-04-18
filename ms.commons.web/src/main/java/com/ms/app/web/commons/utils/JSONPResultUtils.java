/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.lang.Argument;
import com.ms.commons.summer.web.servlet.result.mime.DefaultJsonResult;
import com.ms.commons.summer.web.servlet.result.mime.JSONPResult;
import com.ms.commons.summer.web.servlet.result.mime.JsonResult;
import com.ms.commons.summer.web.util.json.JsonUtils;

/**
 * 通用Ajax请求的JSONP 结果
 * 
 * @author zxc Apr 12, 2013 10:52:26 PM
 */
public class JSONPResultUtils {

    // --- success
    public static JsonResult success(Object data, String callback) {
        return success(data, callback, null, true);
    }

    public static JsonResult success(Object data, String callback, boolean escape) {
        return success(data, callback, null, escape);
    }

    public static JsonResult success(Object data, String callback, String message) {
        return success(data, callback, message, true);
    }

    public static JsonResult success(Object data, String callback, String message, boolean escape) {
        return buildJsonResult(ResultCode.SUCCESS, data, callback, message, escape);
    }

    // --- login
    public static JsonResult needLoJsonResult(String callback) {
        return needLogin(null, callback, null);
    }

    public static JsonResult needLogin(Object data, String callback) {
        return needLogin(data, callback, null);
    }

    public static JsonResult needLogin(Object data, String callback, String message) {
        return buildJsonResult(ResultCode.NEED_LOGIN, data, callback, message, true);
    }

    public static String getNeedLoginJson(String callback) {
        if (StringUtils.isEmpty(callback)) {
            return JsonResultUtils.getNeedLoginJson();
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("result", ResultCode.NEED_LOGIN.getValue());// 本次请求是否成功
        params.put("message", getMessage(ResultCode.NEED_LOGIN, null));// 用户封装信息，典型的是检验出错信息
        params.put("data", "");// 本次请求需要返回的数据
        try {
            StringBuilder sb = new StringBuilder(200);
            sb.append(callback);
            sb.append('(');
            sb.append(JsonUtils.object2Json(params));
            sb.append(')');
            return sb.toString();
        } catch (Exception e) {
        }
        return JsonResultUtils.getNeedLoginJson();
    }

    // ---error
    public static JsonResult error(String callback) {
        return error(null, callback, null, true);
    }

    public static JsonResult error(String callback, String message) {
        return error(null, callback, message, true);
    }

    public static JsonResult error(Object data, String callback, String message) {
        return error(data, callback, message, true);
    }

    public static JsonResult error(Object data, String callback, String message, boolean escape) {
        return buildJsonResult(ResultCode.ERROR, data, callback, message, escape);
    }

    // --submit
    public static JsonResult submitted(Object data, String callback) {
        return buildJsonResult(ResultCode.SUBMITED, data, callback, null, true);
    }

    public static JsonResult forbidden(String callback) {
        return buildJsonResult(ResultCode.FORBIDDEN, null, callback, null, true);
    }

    public static JsonResult forbidden(Object data, String callback, String message) {
        return buildJsonResult(ResultCode.FORBIDDEN, data, callback, message, true);
    }

    public static JsonResult buildJsonResult(ResultCode code, Object data, String callback, String message,
                                             boolean escape) {
        if (StringUtils.isEmpty(callback)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("result", code.getValue());// 本次请求是否成功
            params.put("message", getMessage(code, message));// 用户封装信息，典型的是检验出错信息
            params.put("data", data == null ? "" : data);// 本次请求需要返回的数据
            return new DefaultJsonResult(params, escape);
        } else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("result", code.getValue());// 本次请求是否成功
            params.put("message", getMessage(code, message));// 用户封装信息，典型的是检验出错信息
            params.put("data", data == null ? "" : data);// 本次请求需要返回的数据
            return new JSONPResult(params, callback, escape);
        }
    }

    private static String getMessage(ResultCode code, String message) {
        if (Argument.isNotBlank(message)) {
            return message;
        }
        switch (code) {
            case SUCCESS:
                return "操作成功";
            case ERROR:
                return "操作失败";
            case NEED_LOGIN:
                return "需要登录";
            case SUBMITED:
                return "表单重复提交";
            case FORBIDDEN:
                return "权限不够";
            default:
                return null;
        }
    }
}
