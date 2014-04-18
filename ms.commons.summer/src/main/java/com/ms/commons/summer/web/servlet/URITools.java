/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 4:26:29 PM
 */
public class URITools {

    protected static ExpandLogger logger  = LoggerFactoryWrapper.getLogger(URITools.class);
    private static String         charset = null;

    /**
     * 通用的参数添加方法（提供对url参数的通用验证处理）
     * 
     * @param parameters 参数map
     * @param name 参数名称
     * @param value 参数值
     */
    public static void addParam(Map<String, String> parameters, String name, String value)
                                                                                          throws IllegalArgumentException {
        // 验证参数值
        if (value == null) {
            if (parameters.containsKey(name)) parameters.remove(name);
            if (logger.isDebugEnabled()) {
                logger.warn("parameter value is null");
            }
            return;
        }

        // 参数值进行编码
        String encodedValue = value;
        try {
            if (charset == null) {
                final String property = System.getProperty("default_charset");
                charset = (property == null ? "UTF-8" : property);
            }

            encodedValue = URLEncoder.encode(value, charset);
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
            throw new IllegalArgumentException(e);
        }
        // 添加参数
        parameters.put(name, encodedValue);
    }

    public static String buildURIPath(String path, String target, Map<String, String> parameters) {
        return buildURIPath(path, target, prepareParam(parameters));
    }

    /**
     * path/target/parameters
     * 
     * @param path
     * @param target
     * @param parameters
     * @return
     */
    public static String buildURIPath(String path, String target, String parameters) {
        if (path == null) {
            throw new IllegalArgumentException("path not allow null");
        }
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(path);
        if (target != null) {
            if (target.length() > 0) {
                int length = buffer.length();
                if (length > 0) {
                    if (buffer.charAt(length - 1) != '/') {
                        buffer.append('/');
                    }
                    if (target.charAt(0) != '/') {
                        buffer.append(target);
                    } else {
                        buffer.append(target, 1, target.length());
                    }
                    if (buffer.charAt(length - 1) == '/') {
                        buffer.deleteCharAt(length - 1);
                    }
                }
            }
        }
        if (parameters != null && parameters.length() > 0) {
            if (path.contains("?")) {
                buffer.append('&');
                buffer.append(parameters);
            } else {
                buffer.append('?');
                buffer.append(parameters);
            }
        }
        return buffer.toString();
    }

    /**
     * 参数处理
     * 
     * @param strBuilder
     */
    private static String prepareParam(Map<String, String> parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder strBuilder = new StringBuilder(256);
        Set<String> keySet = parameters.keySet();
        int size = keySet.size();
        if (size <= 0) {
            return "";
        }
        int i = 0;
        for (String key : keySet) {
            if (i > 0 && i <= size - 1) {
                strBuilder.append("&");
            }
            strBuilder.append(key);
            strBuilder.append("=");
            strBuilder.append(parameters.get(key));
            i++;
        }

        return strBuilder.toString();
    }

    /**
     * 以currentPath为参照，获取relativePath所对应的绝对路径
     * 
     * @param currentPath 当前参照的路径
     * @param relativePath 相对路径
     * @return
     * @throws PathOverflowException
     */
    public static String getWebAbsolutePath(String currentPath, String relativePath) {
        String[] rPaths = relativePath.split("/");
        if (relativePath.startsWith("/")) {
            return relativePath;
        }
        String absolutePath = relativePath;
        int index = currentPath.indexOf("://");
        String prefix = "";
        if (index != -1) {
            prefix = currentPath.substring(0, index + 3);
            currentPath = currentPath.substring(index + 3);
        }
        String[] cPaths = currentPath.split("/");
        List<String> cPathList = new ArrayList<String>(Arrays.asList(cPaths));
        List<String> rPathList = new ArrayList<String>(Arrays.asList(rPaths));
        try {
            if (rPaths.length == 1 || !relativePath.startsWith(".")) {
                cPathList.remove(cPathList.size() - 1);
            } else {
                boolean findOneDot = false;
                boolean findTowDot = false;
                for (String rPath : rPaths) {
                    if (rPath.equals(".")) {
                        rPathList.remove(0);
                        if (!findOneDot && !findTowDot) {
                            cPathList.remove(cPathList.size() - 1);
                        }
                        findOneDot = true;
                    } else if (rPath.equals("..")) {
                        rPathList.remove(0);
                        if (!findOneDot) cPathList.remove(cPathList.size() - 1);
                        cPathList.remove(cPathList.size() - 1);
                        findOneDot = false;
                        findTowDot = true;
                    } else {
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("'" + relativePath + "' is over than '" + prefix + currentPath
                                                     + "'");
        }
        cPathList.addAll(rPathList);
        absolutePath = prefix + StringUtils.collectionToDelimitedString(cPathList, "/");
        return absolutePath;
    }
}
