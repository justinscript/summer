/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.handler;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ms.commons.summer.web.multipart.MultipartFileAdapter;

/**
 * @author zxc Apr 12, 2013 4:10:43 PM
 */
public class SummerServletRequestDataBinder extends ServletRequestDataBinder {

    public static final String SIMPLE_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String FULL_DATE_FORMAT_PATTERN   = "yyyy-MM-dd hh:mm:ss";

    public SummerServletRequestDataBinder(Object target) {
        super(target);
        initDate();
    }

    public SummerServletRequestDataBinder(Object target, String objectName) {
        super(target, objectName);
        initDate();
    }

    private void initDate() {
        SimpleDateFormat sdf1 = new SimpleDateFormat(FULL_DATE_FORMAT_PATTERN);
        SimpleDateFormat sdf2 = new SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN);
        registerCustomEditor(java.util.Date.class, new DateEditor(sdf1, sdf2));
    }

    public void bind(ServletRequest request) {
        MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request, getObjectName(), ".");
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            bindMultipartFiles(multipartRequest.getFileMap(), mpvs);
        }
        doBind(mpvs);
    }

    @SuppressWarnings("all")
    protected void bindMultipartFiles(Map multipartFiles, MutablePropertyValues mpvs) {
        for (Iterator it = multipartFiles.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            if (getObjectName() != null && key.startsWith(getObjectName())) {
                key = key.substring(getObjectName().length() + 1);
                Object value = entry.getValue();
                if (value != null) {
                    if (value instanceof MultipartFile) {
                        bindMultipartFile(mpvs, key, (MultipartFile) value);
                    } else if (value instanceof MultipartFile[]) {
                        bindMultipartFile(mpvs, key, (MultipartFile[]) value);
                    }
                }
            }
        }
    }

    private void bindMultipartFile(MutablePropertyValues mpvs, String key, MultipartFile file) {
        if (isBindEmptyMultipartFiles() || !file.isEmpty()) {
            mpvs.addPropertyValue(key, new MultipartFileAdapter(file));
        }
    }

    private void bindMultipartFile(MutablePropertyValues mpvs, String key, MultipartFile[] file) {
        if (isBindEmptyMultipartFiles()) {
            MultipartFileAdapter[] adapter = new MultipartFileAdapter[file.length];
            for (int i = 0; i < adapter.length; i++) {
                adapter[i] = new MultipartFileAdapter(file[i]);
            }
            mpvs.addPropertyValue(key, adapter);
        }
    }
}
