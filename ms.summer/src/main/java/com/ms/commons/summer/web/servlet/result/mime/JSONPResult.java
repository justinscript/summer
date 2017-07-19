/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.result.mime;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;

import com.ms.commons.summer.web.util.json.JsonUtils;

/**
 * 以JSONP的形式返回数据
 * 
 * @author zxc Apr 12, 2013 4:48:02 PM
 */
public class JSONPResult extends JsonResult {

    private Object  model;
    private boolean escape;
    private String  callBack;

    /**
     * 生成json表达式
     * 
     * @param model
     * @param escape 是否把"<",">"等转义
     */
    public JSONPResult(Object model, String callBack) {
        this(model, callBack, true);
    }

    /**
     * 生成json表达式
     * 
     * @param model
     * @param escape 是否把"<",">"等转义
     */
    public JSONPResult(Object model, String callBack, boolean escape) {
        super();
        if (model == null) {
            throw new IllegalArgumentException("argument is null");
        }
        this.model = model;
        this.escape = escape;
        this.callBack = callBack;
    }

    /**
     * @param outputStream
     */
    protected void onSerialize(ServletOutputStream outputStream) throws IOException {
        Writer writer = new OutputStreamWriter(outputStream, this.getCharacterEncoding());
        try {
            writer.write(callBack);
            writer.write('(');
            JsonUtils.object2Json(this.model, writer, this.escape);
            writer.write(')');
            writer.flush();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
