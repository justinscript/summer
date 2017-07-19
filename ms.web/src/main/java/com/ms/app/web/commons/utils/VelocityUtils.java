/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.utils;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.ms.commons.summer.web.view.velocity.ViewContent;

/**
 * @author zxc Apr 12, 2013 10:50:48 PM
 */
public class VelocityUtils {

    private static final String   PREFIX                   = "/";
    private static final Logger   logger                   = LoggerFactory.getLogger(VelocityUtils.class);
    /**
     * 默认布局文件夹
     */
    public static final String    DEFAULT_LAYOUT_DIRECTORY = "/layout";
    /**
     * 默认视图文件夹
     */
    public static final String    DEFAULT_VIEW_DIRECTORY   = "/view";
    /**
     * 默认的布局vm名
     */
    private static final String   DEFAULT_LAYOUT_VM        = "default.vm";

    private static VelocityEngine velocityEngine;
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("commons_resource/spring_velocity_engine.xml");
        velocityEngine = (VelocityEngine) context.getBean("velocityEngine");
    }

    /**
     * 渲染$WORKSPACE/resources下的模板
     * 
     * @param templateName 模板的名字，注意是相对于$WORKSPACE/resources的路径名称。例如/user/join_success.vm
     * @param model 数据对象
     * @return
     */
    public static String mergetTemplate(String templateName, Map<String, Object> model) {
        StringWriter sw = new StringWriter();
        StringWriter layout = new StringWriter();
        try {
            VelocityEngineUtils.mergeTemplate(velocityEngine, getTemplatePath(templateName), model, sw);
            model.put("body", new ViewContent(sw.toString()));
            String path = (String) velocityEngine.getProperty("file.resource.loader.path");
            VelocityEngineUtils.mergeTemplate(velocityEngine, getLayoutTemplate(path, templateName), model, layout);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return layout.toString();
    }

    private static String getTemplatePath(String templateName) {
        return DEFAULT_VIEW_DIRECTORY + templateName;
    }

    /**
     * 得到布局的vm<br>
     * 同级同名的xxxx.vm,没有找同级default.vm,没有找父类default.vm<br>
     * 
     * @param viewname
     * @return
     */
    protected static String getLayoutTemplate(String layoutPath, String viewname) {
        // 如果开头不是"/"加上
        if (!viewname.startsWith(PREFIX)) {
            viewname = PREFIX + viewname;
        }
        // 尝试取同级同名的模版
        String layoutUrlToUse = DEFAULT_LAYOUT_DIRECTORY + viewname;
        File f = new File(layoutPath, layoutUrlToUse);
        if (f.exists()) {
            return layoutUrlToUse;
        }
        // 尝试获取同级默认模版
        layoutUrlToUse = DEFAULT_LAYOUT_DIRECTORY
                         + viewname.substring(0, viewname.lastIndexOf(PREFIX) + 1).concat(DEFAULT_LAYOUT_VM);
        f = new File(layoutPath, layoutUrlToUse);
        if (f.exists()) {
            return layoutUrlToUse;
        }
        int index;
        // 获取父文件夹默认模版
        while (true) {
            // 去掉最后一层/default.vm
            index = viewname.lastIndexOf(PREFIX);
            if (index == -1) {
                break;
            }
            viewname = viewname.substring(0, index);
            // 去掉上一层
            index = viewname.lastIndexOf(PREFIX);
            if (index == -1) {
                break;
            }
            viewname = viewname.substring(0, index + 1).concat(DEFAULT_LAYOUT_VM);
            // 检查下路径最小长度不可能小于默认布局长度
            if (viewname.length() <= DEFAULT_LAYOUT_VM.length()) {
                break;
            }
            layoutUrlToUse = DEFAULT_LAYOUT_DIRECTORY + viewname;
            f = new File(layoutPath, layoutUrlToUse);
            if (f.exists()) {
                return layoutUrlToUse;
            }
        }
        return DEFAULT_LAYOUT_DIRECTORY + PREFIX + DEFAULT_LAYOUT_VM;
    }

}
