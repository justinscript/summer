/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import com.ms.commons.test.external.jyaml.org.ho.yaml.YamlConfig;

/**
 * @author zxc Apr 14, 2013 12:30:00 AM
 */
public interface ObjectWrapper {

    public Object getObject();

    public void setObject(Object object);

    @SuppressWarnings("rawtypes")
    public Class getType();

    public Object createPrototype();

    public void setYamlConfig(YamlConfig config);

    public interface CreateListener {

        public void created(Object obj);
    }

    public void addCreateHandler(CreateListener listener);
}
