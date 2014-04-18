/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.validation.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.app.web.validation.annotation.ValidationInfo;

/**
 * 所有校验规则所在地
 * 
 * @author zxc Apr 12, 2013 11:17:06 PM
 */
@SuppressWarnings("unchecked")
public class ValidatorFactory {

    private static Map<String, Validator> allValidtors;
    private static final Logger           logger = LoggerFactory.getLogger(ValidatorFactory.class);
    static {
        String path = "classpath*:/META-INF/validators/*_validators.xml";
        ApplicationContext context = new ClassPathXmlApplicationContext(path);
        allValidtors = context.getBeansOfType(Validator.class);
        logger.error("Form校验器加载完成！总共家在了" + allValidtors.size() + "个校验器");
        if (logger.isDebugEnabled()) {
            logger.debug("所有的加载器：" + allValidtors);
        }
    }

    /**
     * 通过配置信息，获取一整套验证器集合
     */
    public static List<Validator> getValidators(ValidationInfo validationInfo) {
        List<Validator> validators = new ArrayList<Validator>();
        for (String validatorId : validationInfo.validators()) {
            Validator validator = get(validatorId);
            validators.add(validator);
        }
        return validators;
    }

    /**
     * 通过配置器ID，来获取某一个验证器
     * 
     * @throws RuntimeException 名字叫validatorId的验证器不存在
     */
    public static Validator get(String validatorId) {
        if (!allValidtors.containsKey(validatorId)) {
            throw new RuntimeException("名字叫" + validatorId + "的校验器不存在");
        }
        return allValidtors.get(validatorId);
    }
}
