/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.valve;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.app.web.commons.utils.InvokeTypeTools;
import com.ms.app.web.commons.valve.permission.PathMatcher;
import com.ms.app.web.commons.valve.permission.WebPermission;
import com.ms.commons.result.Result;
import com.ms.commons.summer.web.pipeline.AbstractPipelineValves;
import com.ms.commons.summer.web.pipeline.PipelineMap;
import com.ms.commons.summer.web.pipeline.PipelineResult;

/**
 * @author zxc Apr 12, 2013 11:12:54 PM
 */
public abstract class BasePermissionControlValve extends AbstractPipelineValves {

    // 配置文件
    private static final String                     MUSTANG_PIPELINE_XML = "META-INF/spring/seine/mustang-pipeline.xml";

    private List<WebPermission>                     permissions;
    private Set<String>                             noCheckUrlList;
    private String                                  noPermissionUrl;
    private String                                  noPermissionUrlAjax;
    private Map<String, WebPermission>              permissonsMap;
    private AtomicBoolean                           isInited             = new AtomicBoolean(false);
    private static Logger                           logger               = LoggerFactory.getLogger(BasePermissionControlValve.class);
    // 精确URL
    private Map<String, Map<String, WebPermission>> exactUrl2ItemCodeMap = new HashMap<String, Map<String, WebPermission>>();
    // 模糊URL
    private Map<String, Map<String, WebPermission>> likeUrl2ItemCodeMap  = new HashMap<String, Map<String, WebPermission>>();

    public void init() {
        if (isInited.compareAndSet(false, true)) {
            permissonsMap = new HashMap<String, WebPermission>(permissions.size());
            for (WebPermission softPermission : permissions) {
                if (permissonsMap.containsKey(softPermission.getItemCode())) {
                    new RuntimeException(String.format("发现重复的权限配置【%s】", softPermission.getItemCode()));
                }
                permissonsMap.put(softPermission.getItemCode(), softPermission);
                setUrl2ItemCodeMap(softPermission);
            }

            logger.error("*****AccessControlValve*****");
            printUrlMap(exactUrl2ItemCodeMap);
            printUrlMap(likeUrl2ItemCodeMap);
        }
    }

    private void printUrlMap(Map<String, Map<String, WebPermission>> maps) {
        for (String url : maps.keySet()) {
            Map<String, WebPermission> map = maps.get(url);
            logger.error(String.format("URL:[%-20.35s],权限:%s", url, map.keySet()));
        }
    }

    private void setUrl2ItemCodeMap(WebPermission softPermission) {
        for (String url : softPermission.getAcceptUrls()) {
            boolean isLikeUrl = url.contains("*");
            if (isLikeUrl) {
                setValueIfNotEixsted(likeUrl2ItemCodeMap, url, softPermission);
            } else {
                setValueIfNotEixsted(exactUrl2ItemCodeMap, url, softPermission);
            }
        }
        if (softPermission.getIncludes() != null) {
            for (WebPermission includeSoftPermission : softPermission.getIncludes()) {
                setUrl2ItemCodeMap(includeSoftPermission);
            }
        }
    }

    private void setValueIfNotEixsted(Map<String, Map<String, WebPermission>> map, String url,
                                      WebPermission softPermission) {
        Map<String, WebPermission> itemCodes = map.get(url);
        if (itemCodes == null) {
            map.put(url, itemCodes = new HashMap<String, WebPermission>(4));
        }
        itemCodes.put(softPermission.getItemCode(), softPermission);
    }

    public void setPermissons(List<WebPermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public PipelineResult invoke(HttpServletRequest request, HttpServletResponse response, PipelineMap map) {
        // 请求地址
        String requestURI = request.getRequestURI();
        if (!hasLogin()) {// 未登陆用户不用检查
            return null;
        }
        Result result = hasPermission(requestURI, getItemCode());
        if (!result.isSuccess()) {
            String url = null;
            if (InvokeTypeTools.isAjax(request)) {
                request.setAttribute("__noPermissionUrl__", result.getData());
                url = noPermissionUrlAjax;
            } else {
                url = result.getData() != null ? (String) result.getData() : noPermissionUrl;
            }
            // 对Ajax请请求的处理
            return PipelineResult.gotoFinally("gotoLogin", url);
        }
        return null;
    }

    protected abstract String getItemCode();

    protected abstract boolean hasLogin();

    private Result hasPermission(String path, String itemCode) {
        // isNoCheckUrl
        for (String noCheckUrl : noCheckUrlList) {
            if (PathMatcher.match(noCheckUrl, path)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("URL【%s】匹配上【noCheckUrls】的【%s】模式！", path, noCheckUrl));
                }
                return Result.success();
            }
        }
        WebPermission softPermission = permissonsMap.get(itemCode);
        if (softPermission == null) {
            logger.error("itemCode[" + itemCode + "]没有对应的权限配置！", new RuntimeException("没有知道的权限配置！"));
            return Result.failed("itemCode[" + itemCode + "]没有对应的权限配置！");
        }
        // 优先从精确的URL中进行匹配
        if (exactUrl2ItemCodeMap.containsKey(path)) {
            Map<String, WebPermission> map = exactUrl2ItemCodeMap.get(path);
            boolean hasRight = map.containsKey(itemCode);
            if (logger.isDebugEnabled() && hasRight) {
                logger.debug(String.format("URL【%s】匹配上【%s】的精确地址", path, softPermission.getItemCode()));
            }
            return hasRight ? Result.success() : Result.failed().setData(softPermission.getNoPermissionUrl());
        }
        // 然后从模糊的URL中进行匹配
        for (String allowUrl : likeUrl2ItemCodeMap.keySet()) {
            if (PathMatcher.match(allowUrl, path)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("URL【%s】匹配上【%s】的【%s】模式！", path, softPermission.getItemCode(), allowUrl));
                }
                return Result.success();
            }
        }
        return Result.failed().setData(softPermission.getNoPermissionUrl());
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext contetx = new ClassPathXmlApplicationContext(MUSTANG_PIPELINE_XML);
        BasePermissionControlValve accescsControlVave = (BasePermissionControlValve) contetx.getBean("accessControlValve");

        System.out.println(accescsControlVave.permissions.get(0).getAcceptUrls().size());

    }

    public void setNoPermissionUrl(String noPermissionUrl) {
        this.noPermissionUrl = noPermissionUrl;
    }

    public void setNoPermissionUrlAjax(String noPermissionUrlAjax) {
        this.noPermissionUrlAjax = noPermissionUrlAjax;
    }

    public void setNoCheckUrlList(Set<String> noCheckUrlList) {
        this.noCheckUrlList = noCheckUrlList;
    }
}
