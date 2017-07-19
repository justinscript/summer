/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
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
 * 通用Ajax请求的JSON 结果
 * 
 * @author zxc Apr 12, 2013 10:52:04 PM
 */
public class JsonResultUtils {

    private static String needLoginJson;

    public static JsonResult success() {
        return success(null, null);
    }

    public static JsonResult success(Object data) {
        return success(data, null);
    }

    public static JsonResult success(Object data, boolean escape) {
        return success(data, null, escape);
    }

    public static JsonResult success(Object data, String message) {
        return buildJsonResult(ResultCode.SUCCESS, data, message);
    }

    public static JsonResult success(Object data, String message, boolean escape) {
        return buildJsonResult(null, ResultCode.SUCCESS, data, message, escape);
    }

    public static JsonResult successJsonp(String callback, Object data, String message, boolean escape) {
        return buildJsonResult(callback, ResultCode.SUCCESS, data, message, escape);
    }

    public static JsonResult successJsonp(String callback, Object data, String message) {
        return buildJsonResult(callback, ResultCode.SUCCESS, data, message, true);
    }

    public static JsonResult successJsonp(String callback, Object data) {
        return buildJsonResult(callback, ResultCode.SUCCESS, data, null, true);
    }

    public static JsonResult successJsonp(String callback, Object data, boolean escape) {
        return buildJsonResult(callback, ResultCode.SUCCESS, data, null, escape);
    }

    public static JsonResult needLoJsonResult() {
        return needLogin(null, null);
    }

    public static JsonResult needLogin(Object data) {
        return needLogin(data, null);
    }

    public static JsonResult needLogin(Object data, String message) {
        return buildJsonResult(ResultCode.NEED_LOGIN, data, message);
    }

    public static String getNeedLoginJson() {
        if (StringUtils.isEmpty(needLoginJson)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("result", ResultCode.NEED_LOGIN.getValue());// 本次请求是否成功
            params.put("message", getMessage(ResultCode.NEED_LOGIN, null));// 用户封装信息，典型的是检验出错信息
            params.put("data", "");// 本次请求需要返回的数据
            try {
                needLoginJson = JsonUtils.object2Json(params);
            } catch (Exception e) {
            }
        }
        return needLoginJson;
    }

    public static JsonResult error() {
        return error(null, null);
    }

    public static JsonResult error(String message) {
        return error(null, message);
    }

    public static JsonResult error(String message, boolean escape) {
        return error(null, message, escape);
    }

    public static JsonResult error(Map<String, ? extends Object> data) {
        return error(data, null);
    }

    public static JsonResult error(Map<String, ? extends Object> data, boolean escape) {
        return error(data, null);
    }

    public static JsonResult error(Object data, String message) {
        return buildJsonResult(ResultCode.ERROR, data, message);
    }

    public static JsonResult errorJsonp(String callback, Object data, String message) {
        return buildJsonResult(callback, ResultCode.ERROR, data, message, true);
    }

    public static JsonResult errorJsonp(String callback, String message) {
        return buildJsonResult(callback, ResultCode.ERROR, null, message, true);
    }

    public static JsonResult errorJsonp(String callback, Object data, String message, boolean escape) {
        return buildJsonResult(callback, ResultCode.ERROR, data, message, escape);
    }

    public static JsonResult error(Object data, String message, boolean escape) {
        return buildJsonResult(null, ResultCode.ERROR, data, message, escape);
    }

    public static JsonResult submitted(Object data) {
        return buildJsonResult(ResultCode.SUBMITED, data, null);
    }

    public static JsonResult forbidden() {
        return buildJsonResult(ResultCode.FORBIDDEN, null, null);
    }

    public static JsonResult forbidden(Object data, String message) {
        return buildJsonResult(ResultCode.FORBIDDEN, data, message);
    }

    public static JsonResult forbidden(Object data, String message, boolean escape) {
        return buildJsonResult(null, ResultCode.FORBIDDEN, data, message, escape);
    }

    private static JsonResult buildJsonResult(ResultCode code, Object data, String message) {
        return buildJsonResult(null, code, data, message, true);
    }

    private static JsonResult buildJsonResult(String callback, ResultCode code, Object data, String message,
                                              boolean escape) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("result", code.getValue());// 本次请求是否成功
        params.put("message", getMessage(code, message));// 用户封装信息，典型的是检验出错信息
        params.put("data", data == null ? "" : data);// 本次请求需要返回的数据
        boolean isJsonp = StringUtils.isNotBlank(callback);
        return isJsonp ? new JSONPResult(params, callback, escape) : new DefaultJsonResult(params, escape);
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
                return code.name();
        }
    }
}
