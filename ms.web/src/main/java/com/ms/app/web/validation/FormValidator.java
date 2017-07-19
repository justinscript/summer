/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.validation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jdom.IllegalAddException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.app.web.validation.annotation.ValidationInfo;
import com.ms.app.web.validation.validator.Validator;
import com.ms.app.web.validation.validator.ValidatorFactory;

/**
 * 对一个{@link From}实例进行校验，也可以通过validatorId来指定校验器来校验
 * 
 * <pre>
 * 用法：
 * 1. 在controller中对Form对象校验
 *     @ControllerAction
 *     public WebResult join(Map<String, Object> model, JoinForm joinForm){
 *         //Java校验
 *         ValidationResult result = FormValidator.isValid(joinForm);
 *         if (!result.isValid()) {
 *             // 将出错信息放到model中去,然后返回到表单提交页面
 *             model.put(ValidationResult.key, result);
 *             return new View(JOIN_VM);
 *         }
 *         //检验通过，可以把信息转到DO去。
 *         MemberDO memberDO = new MemberDO();
 *         FormValidator.setTo(joinForm,memberDO);
 *         .....
 *  }
 *  
 *  2.直接对某个数据进行校验
 *     @ControllerAction
 *     public WebResult join(Map<String, Object> model, String password){
 *      //直接对Password校验
 *       ValidationResult result = FormValidator.isValid("password","密码",joinForm,"password");
 *       if(!result.isValid()){
 *              // 将出错信息放到model中去,然后返回到表单提交页面
 *             model.put(ValidationResult.key, result);
 *             return new View(JOIN_VM);
 *       }
 *       ....
 *     }
 * 
 * </pre>
 * 
 * @author zxc Apr 12, 2013 11:16:30 PM
 */
public class FormValidator {

    private static Logger                          logger = LoggerFactory.getLogger(FormValidator.class);

    private static Map<String, ValidatorContianer> cached = new ConcurrentHashMap<String, ValidatorContianer>();

    /**
     * 对一个Form进行校验，该Form的校验配置信息会被缓存起来
     * 
     * @param form 需要校验的form对象
     * @return ValidationResult，如果校验同则 {@link ValidationResult#isValid()} 返回为<code>true</code>
     */
    public static ValidationResult isValid(Form form) {
        if (form == null) {
            throw new IllegalAddException("Arugment cannot be null");
        }
        Class<? extends Object> type = form.getClass();
        String key = type.getName();
        ValidatorContianer contianer = cached.get(key);
        if (contianer == null) {
            contianer = new ValidatorContianer();
            cached.put(key, contianer);
        }
        ValidationResult result = new ValidationResult();
        for (Field filed : type.getDeclaredFields()) {
            validFiled(form, filed, contianer, result);
        }
        return result;
    }

    /**
     * 通过validatorId来获取指定校验器，来进行校验
     * 
     * @param validatorId 校验器ID
     * @param value 需要校验的值
     * @param fieldKey 校验项的Key
     * @param displayName 校验项的显示名称
     * @return
     */
    public static ValidationResult isValid(String fieldKey, String displayName, Object value, String... validatorIds) {
        ValidationResult result = new ValidationResult();
        for (String validatorId : validatorIds) {
            Validator validator = ValidatorFactory.get(validatorId);
            if (!validator.isValid(value)) {
                result.illegal();
                String name = displayName == null ? fieldKey : displayName;
                result.appendErrorMessage(fieldKey, validator.getErrorMessage(name));
            }
        }
        return result;
    }

    private static void validFiled(Object date, Field filed, ValidatorContianer contianer, ValidationResult result) {
        // 获取校验配置信息
        ValidationInfo validationInfo = filed.getAnnotation(ValidationInfo.class);
        if (validationInfo == null) {
            return;
        }
        List<Validator> validators = getValidators(validationInfo, contianer);
        // 开始校验
        try {
            filed.setAccessible(true);
            Object value = filed.get(date);
            isValid(validationInfo, value, validators, result);
        } catch (Exception e) {
            logger.error("获取属性" + filed.getName() + "的值时出错了", e);
        }
    }

    private static List<Validator> getValidators(ValidationInfo validationInfo, ValidatorContianer contianer) {
        List<Validator> validators = contianer.get(validationInfo.key());
        if (validators == null) {
            validators = ValidatorFactory.getValidators(validationInfo);
            contianer.add(validationInfo.key(), validators);
        }
        return validators;
    }

    private static void isValid(ValidationInfo validationInfo, Object value, List<Validator> validators,
                                ValidationResult result) {
        String displayName = validationInfo.displayName();
        if (displayName == null) {
            displayName = validationInfo.key();
        }
        for (Validator validator : validators) {
            if (!validator.isValid(value)) {
                result.illegal();
                result.appendErrorMessage(validationInfo.key(), validator.getErrorMessage(displayName));
                return;// 第一出错的信息就会返回
            }
        }
    }

    /**
     * 一个Form类的所有校验规则容器
     * 
     * @author zxc Apr 12, 2013 11:22:14 PM
     */
    static class ValidatorContianer {

        Map<String, List<Validator>> contianer = new HashMap<String, List<Validator>>();

        public List<Validator> get(String key) {
            return contianer.get(key);
        }

        public void add(String fieldKey, List<Validator> validators) {
            contianer.put(fieldKey, validators);
        }

        public boolean containsKey(String filedKey) {
            return contianer.containsKey(filedKey);
        }
    }

}
