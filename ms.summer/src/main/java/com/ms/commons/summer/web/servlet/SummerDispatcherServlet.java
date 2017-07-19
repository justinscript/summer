/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.servlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;

import com.ms.commons.core.ApplicationContextManager;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.summer.core.context.SummerApplicationContext;
import com.ms.commons.summer.web.pipeline.Pipeline;
import com.ms.commons.summer.web.pipeline.PipelineMap;
import com.ms.commons.summer.web.pipeline.PipelineResult;
import com.ms.commons.summer.web.pipeline.PipelineType;
import com.ms.commons.summer.web.pipeline.PipelineValvesFactory;
import com.ms.commons.summer.web.pipeline.PipelineValvesHandler;
import com.ms.commons.summer.web.servlet.result.ResponseInterceptableModelAndView;

/**
 * summer框架的核心servlet
 * 
 * @author zxc Apr 12, 2013 4:26:52 PM
 */
public class SummerDispatcherServlet extends DispatcherServlet {

    private static final long           serialVersionUID = 7481544089600968177L;

    public static final String          PIPELINE_VALVES  = "pipelineValves";
    protected static ExpandLogger       logger           = LoggerFactoryWrapper.getLogger(SummerDispatcherServlet.class);

    private List<PipelineValvesHandler> tryPipelineValves;
    private List<PipelineValvesHandler> catchPipelineValves;
    private List<PipelineValvesHandler> finallyPipelineValves;

    protected void initStrategies(ApplicationContext context) {
        super.initStrategies(context);
        initPipelineValves(context);
        ApplicationContextManager.regist(context);
    }

    private void initPipelineValves(ApplicationContext context) {
        PipelineValvesFactory pvf = (PipelineValvesFactory) context.getBean(PIPELINE_VALVES);
        tryPipelineValves = new ArrayList<PipelineValvesHandler>();
        catchPipelineValves = new ArrayList<PipelineValvesHandler>();
        finallyPipelineValves = new ArrayList<PipelineValvesHandler>();
        logger.debug("init try pipeline");
        initPipelineValves(pvf.getTryPipelineValves(), tryPipelineValves, context);
        logger.debug("init catch pipeline");
        initPipelineValves(pvf.getCatchPipelineValves(), catchPipelineValves, context);
        logger.debug("init finally pipeline");
        initPipelineValves(pvf.getFinallyPipelineValves(), finallyPipelineValves, context);
    }

    private void initPipelineValves(Map<String, Pipeline> pipelineValves, List<PipelineValvesHandler> pvList,
                                    ApplicationContext context) {
        if (pipelineValves == null) {
            return;
        }
        Set<String> keySet = pipelineValves.keySet();
        for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
            String name = iterator.next();
            Pipeline pv = pipelineValves.get(name);
            if (pv != null) {
                pv.init(context);
                PipelineValvesHandler pvh = new PipelineValvesHandler(name, pv);
                pvList.add(pvh);
                logger.debug("init {} pipelinevalves", name);
            }
        }
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 此Wrapper用于抓住所有的输出流
        WebResponseWrapper webResponseWrapper = new WebResponseWrapper(response);
        // 此Wrapper用于抓住重定向
        RedirectResponseWrapper redirectResponseWrapper = new RedirectResponseWrapper(webResponseWrapper);
        PipelineMap map = new PipelineMap();
        PipelineResult result = null;
        String redirecUrl = null;
        try {
            result = doTryPipelineValues(request, webResponseWrapper, map);
            if (result == null || result.getType() != PipelineType.PIPELINE_FINALLY) {
                super.doDispatch(request, redirectResponseWrapper);
            }
        } catch (Exception e) {
            map.put("summerDispatcherServlet.exception", e);
            logger.error(request.toString(), e);
            result = doCatchPipelineValues(request, webResponseWrapper, map, result);
        } finally {
            redirecUrl = result == null ? null : result.getRedirectUrl();
            doFinallyPipelineValues(request, webResponseWrapper, map, result);
        }
        // 处理重定向
        if (redirectResponseWrapper.getRedirectLocation() != null) {
            response.sendRedirect(redirectResponseWrapper.getRedirectLocation());
            return;
        }
        /**
         * 截住所有response的输出,把流的输出放在最后处理<br>
         * finally的valve中有对cookie的处理,如果cookie处理放在流输出后面，会丢失cookie<br>
         */
        if (webResponseWrapper.isStreamUseed()) {
            response.getOutputStream().write(webResponseWrapper.getByte());
        } else {
            response.getWriter().write(webResponseWrapper.getString());
        }
        // 如果返回的条件中需要页面重定向，那么就执行此操作
        if (redirecUrl != null) {
            response.sendRedirect(redirecUrl);
        }
    }

    /**
     * 执行try块的pipeline
     * 
     * @param request
     * @param response
     * @param map
     * @return
     */
    private PipelineResult doTryPipelineValues(HttpServletRequest request, HttpServletResponse response, PipelineMap map)
                                                                                                                         throws Exception {
        if (tryPipelineValves.size() == 0) {
            return null;
        }
        // 防止死循环,执行某个pipeline后跳转到此pipeline前面的pipeline，再次执行此pipeline，如此死循环
        int count = 0;
        int maxcount = tryPipelineValves.size() * 3;
        PipelineResult result = null;
        PipelineValvesHandler pvh = getNextPipelineValves(tryPipelineValves, null, PipelineType.PIPELINE_TRY, null);
        while (pvh != null && count < maxcount) {
            result = pvh.getPipeline().invoke(request, response, map);
            if (result != null && result.getType() != PipelineType.PIPELINE_TRY) {
                break;
            }
            count++;
            pvh = getNextPipelineValves(tryPipelineValves, pvh, PipelineType.PIPELINE_TRY, result);
        }
        // try执行完，检查下下一个目标是不是去finally，不是直接定位到finally
        if (result != null && result.getType() != PipelineType.PIPELINE_FINALLY) {
            return PipelineResult.gotoFinally(null);
        }
        return result;
    }

    private PipelineResult doCatchPipelineValues(HttpServletRequest request, HttpServletResponse response,
                                                 PipelineMap map, PipelineResult result) throws Exception {
        if (catchPipelineValves.size() == 0) {
            return null;
        }
        int count = 0;
        int maxcount = catchPipelineValves.size() * 3;
        PipelineValvesHandler pvh = getNextPipelineValves(catchPipelineValves, null, PipelineType.PIPELINE_CATCH, null);
        while (pvh != null && count < maxcount) {
            result = pvh.getPipeline().invoke(request, response, map);
            if (result != null && result.getType() != PipelineType.PIPELINE_CATCH) {
                break;
            }
            count++;
            pvh = getNextPipelineValves(catchPipelineValves, pvh, PipelineType.PIPELINE_CATCH, result);
        }
        // catch执行完，检查下下一个目标是不是去finally，不是直接定位到finally
        if (result != null && result.getType() != PipelineType.PIPELINE_FINALLY) {
            return PipelineResult.gotoFinally(null);
        }
        return result;
    }

    private PipelineResult doFinallyPipelineValues(HttpServletRequest request, HttpServletResponse response,
                                                   PipelineMap map, PipelineResult result) throws Exception {
        if (finallyPipelineValves.size() == 0) {
            return null;
        }
        int count = 0;
        int maxcount = finallyPipelineValves.size() * 3;
        PipelineValvesHandler pvh = getNextPipelineValves(finallyPipelineValves, null, PipelineType.PIPELINE_FINALLY,
                                                          result);
        while (pvh != null && count < maxcount) {
            result = pvh.getPipeline().invoke(request, response, map);
            if (result != null && result.getType() != PipelineType.PIPELINE_FINALLY) {
                break;
            }
            count++;
            pvh = getNextPipelineValves(finallyPipelineValves, pvh, PipelineType.PIPELINE_FINALLY, result);
        }
        return result;
    }

    /**
     * 得到下一个pipeline
     * 
     * @param pvList
     * @param current
     * @param currentType
     * @param result
     * @return
     */
    private PipelineValvesHandler getNextPipelineValves(List<PipelineValvesHandler> pvList,
                                                        PipelineValvesHandler current, PipelineType currentType,
                                                        PipelineResult result) {
        if (pvList.size() == 0) {
            return null;
        }
        // 第一次时
        if (current == null) {
            // final时，有可能直接定位到某一个pipeline
            if (currentType == PipelineType.PIPELINE_FINALLY) {
                if (result != null && result.getName() != null && result.getName().length() != 0) {
                    for (int i = 0; i < pvList.size(); i++) {
                        if (pvList.get(i).getPipelineName().equals(result.getName())) {
                            return pvList.get(i);
                        }
                    }
                    return null;
                } else {
                    return pvList.get(0);
                }
            } else {
                return pvList.get(0);
            }
        }
        // 取下一个,如果goto的下一个pipeline名字为空，也认为去取下一个
        if (result == null || result.getName() == null || result.getName().length() == 0) {
            for (int i = 0; i < pvList.size(); i++) {
                if (current == pvList.get(i)) {
                    if (i < pvList.size() - 1) {
                        return pvList.get(i + 1);
                    } else {
                        return null;
                    }
                }
            }
        } else// 根据名字来取
        {
            for (int i = 0; i < pvList.size(); i++) {
                if (pvList.get(i).getPipelineName().equals(result.getName())) {
                    return pvList.get(i);
                }
            }
            logger.debug("Can't find " + result.getName() + " pipelineValves");
        }
        return null;
    }

    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean streamIsOpened = false;
        // 返回类型是否是自己处理响应流
        if (mv instanceof ResponseInterceptableModelAndView) {
            streamIsOpened = ((ResponseInterceptableModelAndView) mv).onResponse(response);
        }
        if (streamIsOpened) {
            return;
        }
        super.render(mv, request, response);
    }

    @SuppressWarnings("rawtypes")
    public Class getContextClass() {
        return SummerApplicationContext.class;
    }

    /**
     * Return the HandlerExecutionChain for this request. Try all handler mappings in order.
     * 
     * @param request current HTTP request
     * @param cache whether to cache the HandlerExecutionChain in a request attribute
     * @return the HandlerExceutionChain, or <code>null</code> if no handler could be found
     */
    protected HandlerExecutionChain getHandler(HttpServletRequest request, boolean cache) throws Exception {
        HandlerExecutionChain handler = (HandlerExecutionChain) request.getAttribute(HANDLER_EXECUTION_CHAIN_ATTRIBUTE);
        if (handler != null) {
            request.removeAttribute(HANDLER_EXECUTION_CHAIN_ATTRIBUTE);
        }

        return super.getHandler(request, cache);
    }

    protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
                                                   Object handler, Exception ex) throws Exception {
        logger.error("", ex);
        return super.processHandlerException(request, response, handler, ex);
    }

    public void destroy() {
        ApplicationContextManager.destoryAllContext();
        super.destroy();
    }
}
