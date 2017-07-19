/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.ms.commons.summer.web.annotations.PathPattern;
import com.ms.commons.summer.web.annotations.PathVariable;
import com.ms.commons.summer.web.util.BasePathMatcher;

/**
 * @author zxc Apr 12, 2013 4:11:37 PM
 */
public class DataBinderUtil {

    /**
     * 得到参数列表
     * 
     * @param method
     * @param model
     * @param request
     * @param response
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object[] getArgs(Method method, Map<String, Object> model, HttpServletRequest request,
                                   HttpServletResponse response, Class<?> c) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        Map<String, Object> argMap = new HashMap<String, Object>(args.length);
        Map<String, String> pathValues = null;
        PathPattern pathPattern = method.getAnnotation(PathPattern.class);
        if (pathPattern != null) {
            String path = request.getRequestURI();
            int index = path.lastIndexOf('.');
            if (index != -1) {
                path = path.substring(0, index);
                String[] patterns = pathPattern.patterns();
                pathValues = getPathValues(patterns, path);
            }
        }
        MapBindingResult errors = new MapBindingResult(argMap, "");
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            MethodParameter methodParam = new MethodParameter(method, i);
            methodParam.initParameterNameDiscovery(parameterNameDiscoverer);
            GenericTypeResolver.resolveParameterType(methodParam, c.getClass());

            String paramName = methodParam.getParameterName();
            // map
            if (Map.class.isAssignableFrom(paramType)) {
                args[i] = model;
            }
            // HttpServletRequest
            else if (HttpServletRequest.class.isAssignableFrom(paramType)) {
                args[i] = request;
            }
            // HttpServletResponse
            else if (HttpServletResponse.class.isAssignableFrom(paramType)) {
                args[i] = response;
            }
            // HttpSession
            else if (HttpSession.class.isAssignableFrom(paramType)) {
                args[i] = request.getSession();
            }
            // Errors
            else if (Errors.class.isAssignableFrom(paramType)) {
                args[i] = errors;
            }
            // MultipartFile
            else if (MultipartFile.class.isAssignableFrom(paramType)) {
                MultipartFile[] files = resolveMultipartFiles(request, errors, paramName);
                if (files != null && files.length > 0) {
                    args[i] = files[0];
                }
            }
            // MultipartFile[]
            else if (MultipartFile[].class.isAssignableFrom(paramType)) {
                args[i] = resolveMultipartFiles(request, errors, paramName);
            } else {
                // 简单数据类型
                if (BeanUtils.isSimpleProperty(paramType)) {
                    SimpleTypeConverter converter = new SimpleTypeConverter();
                    Object value;
                    // 是否是数组
                    if (paramType.isArray()) {
                        value = request.getParameterValues(paramName);
                    } else {
                        Object[] parameterAnnotations = methodParam.getParameterAnnotations();
                        value = null;
                        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
                            if (pathValues != null && pathValues.size() > 0) {
                                for (Object object : parameterAnnotations) {
                                    if (PathVariable.class.isInstance(object)) {
                                        PathVariable pv = (PathVariable) object;
                                        if (StringUtils.isEmpty(pv.value())) {
                                            value = pathValues.get(paramName);
                                        } else {
                                            value = pathValues.get(pv.value());
                                        }
                                        break;
                                    }
                                }
                            }
                        } else {
                            value = request.getParameter(paramName);
                        }
                    }
                    try {
                        args[i] = converter.convertIfNecessary(value, paramType, methodParam);
                        model.put(paramName, args[i]);
                    } catch (TypeMismatchException e) {
                        errors.addError(new FieldError(paramName, paramName, e.getMessage()));
                    }
                } else {
                    // 复杂数据类型POJO类
                    if (paramType.isArray()) {
                        ObjectArrayDataBinder binder = new ObjectArrayDataBinder(paramType.getComponentType(),
                                                                                 paramName);
                        args[i] = binder.bind(request);
                        model.put(paramName, args[i]);
                    } else {
                        Object bindObject = BeanUtils.instantiateClass(paramType);
                        SummerServletRequestDataBinder binder = new SummerServletRequestDataBinder(bindObject, paramName);
                        binder.bind(request);
                        BindException be = new BindException(binder.getBindingResult());
                        List<FieldError> fieldErrors = be.getFieldErrors();
                        for (FieldError fieldError : fieldErrors) {
                            errors.addError(fieldError);
                        }
                        args[i] = binder.getTarget();
                        model.put(paramName, args[i]);
                    }
                }
            }
        }
        return args;
    }

    protected static Map<String, String> getPathValues(String[] patterns, String path) {
        BasePathMatcher matcher = new BasePathMatcher();
        Map<String, String> pathValues = null;
        for (String pattern : patterns) {
            pathValues = matcher.extractUriTemplateVariables(pattern, path);
            if (pathValues.size() > 0) {
                return pathValues;
            }
        }
        return pathValues;
    }

    @SuppressWarnings("unchecked")
    private static MultipartFile[] resolveMultipartFiles(HttpServletRequest request, MapBindingResult errors,
                                                         String paramName) {
        if (request instanceof MultipartRequest) {
            try {
                Map<String, Object> map = ((MultipartRequest) request).getFileMap();
                MultipartFile[] multipartFiles = null;
                Object value = map.get(paramName);
                if (value instanceof MultipartFile) {
                    multipartFiles = new MultipartFile[] { (MultipartFile) value };
                } else {
                    multipartFiles = (MultipartFile[]) value;
                }
                return multipartFiles;
            } catch (Exception e) {
                errors.reject("fileuploaderror", e.getMessage());
            }
        }
        return null;
    }

    public static Object[] createObjectArray(HttpServletRequest request, String name, Class<?> clazz) throws Exception {
        ObjectArrayDataBinder binder = new ObjectArrayDataBinder(clazz, name);
        return binder.bind(request);
    }

}
