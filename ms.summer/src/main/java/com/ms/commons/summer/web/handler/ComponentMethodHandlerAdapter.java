/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.handler;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.summer.web.annotations.ControllerAction;
import com.ms.commons.summer.web.annotations.ValidationToken;
import com.ms.commons.summer.web.exception.ValidationException;
import com.ms.commons.summer.web.servlet.mvc.ComponentMethodController;
import com.ms.commons.summer.web.servlet.result.Forward;
import com.ms.commons.summer.web.servlet.result.Redirect;
import com.ms.commons.summer.web.servlet.result.View;
import com.ms.commons.summer.web.servlet.result.WebResult;
import com.ms.commons.summer.web.servlet.result.WebResultModelAndView;
import com.ms.commons.summer.web.servlet.result.mime.MimeModelAndView;
import com.ms.commons.summer.web.servlet.result.mime.MimeResult;
import com.ms.commons.summer.web.view.Widget;
import com.ms.commons.summer.web.view.velocity.SummerVelocityLayoutView;

/**
 * @author zxc Apr 12, 2013 4:12:04 PM
 */
public class ComponentMethodHandlerAdapter implements HandlerAdapter {

    public static final String        MODEL_KEY                 = "_$_MODEL_KEY_$_";
    public static final String        NAMESPACE_KEY             = "_$_NAMESPACE_KEY_$_";
    private UrlPathHelper             urlPathHelper             = new UrlPathHelper();
    private MethodNameResolverAdapter methodNameResolverAdapter = new MethodNameResolverAdapter();                                     ;
    private static final Logger       logger                    = LoggerFactoryWrapper.getLogger(ComponentMethodHandlerAdapter.class);

    public boolean supports(Object handler) {
        return (handler instanceof ComponentMethodController);
    }

    @SuppressWarnings("unchecked")
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
                                                                                                        throws Exception {
        ExtendedModelMap model = new ExtendedModelMap();
        ModelMap map = (ModelMap) request.getAttribute(MODEL_KEY);
        if (map != null) {
            model.putAll(map);
        }
        ComponentMethodController controller = (ComponentMethodController) handler;
        injectServletObject(controller, request, response);
        Object result = handleRequest(handler, request, response, model);
        // 构造ModelAndView对象
        ModelAndView mv = buildModelAndView(request, response, result, model);
        request.setAttribute(MODEL_KEY, model);
        String ns = getNameSpace(request, controller, result);
        String viewName = buildViewName(request, mv, ns);
        mv.setViewName(viewName);
        return mv;
    }

    /**
     * @return the methodNameResolverAdapter
     */
    public MethodNameResolverAdapter getMethodNameResolverAdapter() {
        return methodNameResolverAdapter;
    }

    /**
     * @param methodNameResolverAdapter the methodNameResolverAdapter to set
     */
    public void setMethodNameResolverAdapter(MethodNameResolverAdapter methodNameResolverAdapter) {
        this.methodNameResolverAdapter = methodNameResolverAdapter;
    }

    private Object handleRequest(Object handle, HttpServletRequest request, HttpServletResponse response,
                                 ExtendedModelMap model) throws Exception {
        String methodName = getHandlerMethodName(request);
        if (StringUtils.isEmpty(methodName)) {
            return null;
        }
        Method invokeMethod = getInvokeMethod(handle, methodName);
        if (invokeMethod == null) {
            return null;
        }
        return invokeNamedMethod(handle, invokeMethod, model, request, response);
    }

    /**
     * @param request
     * @return
     */
    private String getHandlerMethodName(HttpServletRequest request) {
        if (isWidget(request)) {
            String uri = String.valueOf(request.getAttribute(Widget.WIDGET_URI));
            return WebUtils.extractFilenameFromUrlPath(uri);
        }
        return getInvokeMethodName(request);
    }

    /**
     * 得到要执行的方法名
     * 
     * @param request
     * @return
     */
    protected String getInvokeMethodName(HttpServletRequest request) {
        try {
            String lookupPath = urlPathHelper.getLookupPathForRequest(request);
            MethodNameResolver methodNameResolver = methodNameResolverAdapter.getMethodNameResolver(lookupPath);
            return methodNameResolver.getHandlerMethodName(request);
        } catch (NoSuchRequestHandlingMethodException ex) {
            return null;
        }
    }

    private Method getInvokeMethod(Object handle, String methodName) {
        try {
            Method[] methods = handle.getClass().getMethods();
            Method invokeMethod = null;
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getAnnotation(ControllerAction.class) != null) {
                    invokeMethod = method;
                }
            }
            if (invokeMethod == null) {
                return null;
            }
            return invokeMethod;
        } catch (SecurityException e) {
            return null;
        }
    }

    /**
     * 判断是否是widget的请求
     * 
     * @param request
     * @return
     */
    protected final boolean isWidget(HttpServletRequest request) {
        Object o = request.getAttribute(Widget.IS_WIDGET);
        if (o != null && "true".equals(o.toString())) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Object invokeNamedMethod(Object handle, Method method, ExtendedModelMap model, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Object object = checkToken(handle, method, model, request, response);
        if (object != null) {
            return object;
        }
        Object[] args = DataBinderUtil.getArgs(method, model, request, response, this.getClass());
        // 执行方法
        try {
            return method.invoke(handle, args);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw e;
        }
    }

    /**
     * 检查token是否有效
     * 
     * @param method
     * @param model
     * @param request
     * @param response
     * @param c
     * @return
     */
    private Object checkToken(Object handle, Method method, Map<String, Object> model, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        ValidationToken validation = method.getAnnotation(ValidationToken.class);
        if (validation == null) {
            return null;
        }
        Method validationMethod = getValidationMethod(validation, handle.getClass());
        if (validationMethod == null) {
            String msg = "不能找到验证方法:[" + validation.methodName() + "]请检查!!";
            logger.error(msg);
            throw new ValidationException(msg);
        }
        String content = validation.content();
        // 执行方法
        try {
            return validationMethod.invoke(handle, model, content);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw e;
        }
    }

    private Method getValidationMethod(ValidationToken validation, Class<?> c) {
        String methodName = null;
        if (StringUtils.isNotEmpty(validation.methodName())) {
            methodName = validation.methodName();
        } else {
            methodName = validation.type().getMethodName();
        }
        try {
            Method method = c.getMethod(methodName, Map.class, String.class);
            if (method == null) {
                return null;
            }
            Class<?> returnType = method.getReturnType();
            if (!returnType.isAssignableFrom(WebResult.class)) {
                return null;
            }
            return method;
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private void injectServletObject(ComponentMethodController controller, HttpServletRequest request,
                                     HttpServletResponse response) {
        Class<? extends ComponentMethodController> clazz = controller.getClass();
        try {
            // 注入HttpServletRequest
            Method method = clazz.getMethod("setRequest", HttpServletRequest.class);
            if (method != null) {
                method.invoke(controller, request);
            }
        } catch (Exception e) {
        }

        try {
            // 注入HttpServletResponse
            Method method = clazz.getMethod("setResponse", HttpServletResponse.class);
            if (method != null) {
                method.invoke(controller, response);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 得到命名空间,获取顺序<br>
     * 先从rquest中取， 再从result中取， 最后用controller中的 <br>
     * 2011.9.12日<br>
     * fixed问题:当定位到其他名称空间的vm模版时,widget不能正常定位到指定的名称空间<br>
     * 把名称空间放入request中，一次http请求只应该有一个名称空间<br>
     * widget的名称空间必须和当前controller中指明的名称空间必须一致<br>
     * 如果时forward请求，则必须清除rquest中的名称空间
     * 
     * @param controller
     * @param result
     * @return
     * @see ComponentMethodHandlerAdapter#buildModelAndView(HttpServletRequest, HttpServletResponse, Object,
     * ExtendedModelMap)
     */
    private String getNameSpace(HttpServletRequest request, ComponentMethodController controller, Object result) {
        // 先取request中的（widget时）
        Object o = request.getAttribute(NAMESPACE_KEY);
        if (o != null && o instanceof String) {
            String ns = (String) o;
            if (ns.trim().length() > 0) {
                return ns;
            }
        }
        // 正常的取名称空间
        String ns = null;
        if (result instanceof View) {
            String na = ((View) result).getNameSpace();
            if (na != null && na.length() != 0) {
                ns = na;
            }
        }
        if (ns == null) {
            ns = controller.getNameSpace();
        }
        request.setAttribute(NAMESPACE_KEY, ns);
        return ns;
    }

    private ModelAndView buildModelAndView(HttpServletRequest request, HttpServletResponse response, Object result,
                                           ExtendedModelMap model) {
        if (result instanceof WebResult) {
            // 如果是mime类型
            if (result instanceof MimeResult) {
                return new MimeModelAndView((MimeResult) result);
            }
            WebResult webResult = (WebResult) result;
            // 重定向,清除model数据
            if (result instanceof Redirect) {
                model.clear();
            } else {
                model.addAllAttributes(webResult.getParameters());
                if (result instanceof View) {
                    View view = (View) result;
                    if (!view.isUselayout()) {
                        request.setAttribute(SummerVelocityLayoutView.USE_LAYOUT, "false");
                    }
                }
                // forward时,清除request中的名称空间
                else if (result instanceof Forward) {
                    request.removeAttribute(NAMESPACE_KEY);
                }
            }
            return new WebResultModelAndView(webResult.getView()).addAllObjects(model);
        } else {
            return new ModelAndView().addAllObjects(model);
        }
    }

    /**
     * @param request
     * @param mv
     * @return
     */
    private String buildViewName(HttpServletRequest request, ModelAndView mv, String nameSpace) {
        Object o = request.getAttribute(Widget.IS_WIDGET);
        boolean isWidget = o != null && "true".equals(o.toString());
        String viewName = mv.getViewName();
        if (viewName == null) {
            if (isWidget) {
                viewName = String.valueOf(request.getAttribute(Widget.WIDGET_URI));
            } else {
                UrlPathHelper helper = new UrlPathHelper();
                viewName = helper.getLookupPathForRequest(request);
            }
        }
        if (viewName.startsWith(WebResult.REDIRECT_URL_PREFIX)) {
            return viewName;
        }
        if (viewName.startsWith(WebResult.FORWARD_URL_PREFIX)) {
            return viewName;
        }
        // 直接定位到vm,去掉后缀(.xxx)
        int index = viewName.indexOf(".");
        if (index != -1) {
            viewName = viewName.substring(0, index);
        }
        // 路径规则:
        // /名称空间/view/xxx/yyy/zzz
        // /名称空间/widget/xxx/yyy/zzz
        if (nameSpace != null && nameSpace.length() > 0) {
            nameSpace = "/" + nameSpace;
        } else {
            nameSpace = "";
        }
        if (isWidget) {
            return nameSpace + SummerVelocityLayoutView.DEFAULT_WIDGET_DIRECTORY + viewName;
        } else {
            return nameSpace + SummerVelocityLayoutView.DEFAULT_VIEW_DIRECTORY + viewName;
        }
    }

    public long getLastModified(HttpServletRequest request, Object handler) {
        if (handler instanceof LastModified) {
            return ((LastModified) handler).getLastModified(request);
        }
        return -1L;
    }
}
