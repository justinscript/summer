/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.summer.web.handler.WidgetHandlerMapping;

/**
 * 试图widget组件
 * 
 * @author zxc Apr 12, 2013 4:21:46 PM
 */
public class Widget {

    private static final ExpandLogger         logger            = LoggerFactoryWrapper.getLogger(Widget.class);
    public static final String                IS_WIDGET         = "_$_WIDGET_$_";
    public static final String                WIDGET_URI        = "_$_WIDGET_URI_$_";
    private HttpServletRequest                request;
    private HttpServletResponse               response;
    private Map<String, Object>               widgetObjectParam = new HashMap<String, Object>();
    private String                            uri;
    private ApplicationContext                applicationContent;
    private static List<WidgetHandlerMapping> handlerMappings;
    private static HandlerAdapter             handlerAdapter;
    private ViewResolver                      viewResolver      = null;

    public Widget(ApplicationContext applicationContent, HttpServletRequest request, HttpServletResponse response) {
        this.applicationContent = applicationContent;
        this.request = request;
        this.response = response;
    }

    public Widget setTemplate(String name) {
        this.uri = name;
        // 应为在一个vm中，使用的是同一个widget对象,每次使用时,清楚参数
        widgetObjectParam.clear();
        return this;
    }

    public Widget setParameter(String name, Object value) {
        return this.addParam(name, value);
    }

    /**
     * 添加参数
     * 
     * @param name 参数名称
     * @param value 参数值
     * @return
     */
    public Widget addParam(String name, Object value) {
        if (value == null) {
            return this;
        }
        this.widgetObjectParam.put(name, value);
        return this;
    }

    public String toString() {
        try {
            return this.buildContent(this.uri.toString());
        } catch (Exception e) {
            logger.warn("", e);
            return "";
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String buildContent(String url) throws IOException, Exception {
        initContext();
        if (handlerAdapter == null || handlerMappings == null || viewResolver == null) {
            logger.warn("fail to excute widget : " + url);
            return "";
        }
        HandlerExecutionChain instance = null;
        ModelAndView mv = null;
        try {
            request.setAttribute(IS_WIDGET, "true");
            request.setAttribute(WIDGET_URI, url);
            // 查找handler
            instance = getHanderMapping(url, request);
            if (instance == null) {
                logger.warn("fail to excute widget : " + url + "  can't find HandlerExecutionChain");
                return "";
            }
            mv = handlerAdapter.handle(request, response, instance.getHandler());
            if (mv == null) {
                return "";
            }
            Map model = mv.getModel();
            // 2011.9.14 widget中加入的widgetObjectParam，只能在当前widget中有效
            ModelAndView wmv = new ModelAndView(mv.getViewName(), model);
            model = wmv.getModel();
            for (String key : this.widgetObjectParam.keySet()) {
                model.put(key, this.widgetObjectParam.get(key));
            }
            return doRender(request, response, wmv);
        } catch (Exception e) {
            logger.error("fail to excute widget : " + url, e);
            throw e;
        }
    }

    private HandlerExecutionChain getHanderMapping(String url, HttpServletRequest request) throws Exception {
        for (WidgetHandlerMapping hm : handlerMappings) {
            Object handler = hm.lookupHandler(url, request);
            if (handler != null && handler instanceof HandlerExecutionChain) {
                return (HandlerExecutionChain) handler;
            }
        }
        return null;
    }

    private String doRender(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception {
        View view = viewResolver.resolveViewName(mv.getViewName(), Locale.getDefault());
        WebResultResponseWrapper bufferdResponse = new WebResultResponseWrapper(response);
        view.render(mv.getModel(), request, bufferdResponse);
        // 获取渲染后的内容结果
        String content = bufferdResponse.getString();
        return content;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initContext() throws Exception {
        if (handlerAdapter == null) {
            handlerAdapter = (HandlerAdapter) applicationContent.getBean(DispatcherServlet.HANDLER_ADAPTER_BEAN_NAME);
        }
        if (handlerMappings == null) {
            Map matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContent,
                                                                               WidgetHandlerMapping.class, true, false);
            if (!matchingBeans.isEmpty()) {
                handlerMappings = new ArrayList<WidgetHandlerMapping>(matchingBeans.values());
                Collections.sort(handlerMappings, new OrderComparator());
            }
        }
        if (viewResolver == null) {
            viewResolver = (ViewResolver) applicationContent.getBean(DispatcherServlet.VIEW_RESOLVER_BEAN_NAME);
        }
    }

    private static class WebResultResponseWrapper extends HttpServletResponseWrapper {

        /** The Writer we convey. */
        private StringWriter          sw;

        /** A buffer, alternatively, to accumulate bytes. */
        private ByteArrayOutputStream bos;

        /** 'True' if getWriter() was called; false otherwise. */
        private boolean               isWriterUsed;

        /** 'True if getOutputStream() was called; false otherwise. */
        private boolean               isStreamUsed;

        /** The HTTP status set by the target. */
        private int                   status = 200;

        public WebResultResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        public PrintWriter getWriter() {
            if (isStreamUsed) {
                throw new IllegalStateException("Unexpected internal error during import: "
                                                + "Target servlet called getWriter(), then getOutputStream()");
            }
            isWriterUsed = true;
            if (sw == null) {
                sw = new StringWriter(2048);
            }
            return new PrintWriter(sw);
        }

        public ServletOutputStream getOutputStream() {
            if (isWriterUsed) {
                throw new IllegalStateException("Unexpected internal error during import: "
                                                + "Target servlet called getOutputStream(), then getWriter()");
            }
            isStreamUsed = true;
            if (bos == null) {
                bos = new ByteArrayOutputStream();
            }
            ServletOutputStream sos = new ServletOutputStream() {

                public void write(int b) throws IOException {
                    bos.write(b);
                }
            };
            return sos;
        }

        public void setContentType(String x) {
            // ignore
        }

        public void setLocale(Locale x) {
            // ignore
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @SuppressWarnings("unused")
        public int getStatus() {
            return status;
        }

        public String getString() throws UnsupportedEncodingException {
            if (isWriterUsed) {
                return sw.toString();
            } else if (isStreamUsed) {
                return bos.toString(this.getCharacterEncoding());
            } else {
                return ""; // target didn't write anything
            }
        }
    }
}
