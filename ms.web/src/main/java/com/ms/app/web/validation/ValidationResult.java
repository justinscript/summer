/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.validation;

import java.util.HashMap;
import java.util.Map;

import com.ms.app.web.validation.annotation.ValidationInfo;

/**
 * 表单校验的结果
 * 
 * @author zxc Apr 12, 2013 11:16:14 PM
 */
public class ValidationResult {

    /**
     * 所有的出错信息
     */
    private Map<String, String> errorMessageMap = new HashMap<String, String>();
    private boolean             isValid         = true;
    public static final String  key             = "validationresult";

    /**
     * 设置校验不通过
     */
    public void illegal() {
        isValid = false;
    }

    public void appendErrorMessage(String fieldKey, String errorMessage) {
        errorMessageMap.put(fieldKey, errorMessage);
    }

    public Map<String, String> getAllMessage() {
        return errorMessageMap;
    }

    public void putAll(Map<String, String> errorMessageMap) {
        this.errorMessageMap.putAll(errorMessageMap);
    }

    /**
     * 校验是否通过
     */
    public boolean isValid() {
        return this.isValid;
    }

    /**
     * 判断一个表单项是否校验失败
     * 
     * @param fieldKey form中Filed配置的{@link ValidationInfo#key()}
     */
    public boolean isFieldError(String fieldKey) {
        return errorMessageMap.containsKey(fieldKey);
    }

    /**
     * 获取出错详细信息
     * 
     * @param fieldKey
     * @return null 如果该表单项没有出错的话
     */
    public String getErrorMessage(String fieldKey) {
        return errorMessageMap.get(fieldKey);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(key + ":[");
        sb.append(" isValid: ").append(isValid);
        sb.append(" ErrorMessage: ").append(errorMessageMap);
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * 将其他的ValidatorResult融合到当前Result中去 如果others中一个时illegal的本当前也将变为illegal
     */
    public void combine(ValidationResult... others) {
        for (ValidationResult other : others) {
            if (!other.isValid()) {
                this.illegal();
                this.putAll(other.getAllMessage());
            }
        }
    }
}
