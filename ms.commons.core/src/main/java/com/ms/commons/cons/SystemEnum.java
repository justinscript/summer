/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.cons;

/**
 * @author zxc Apr 12, 2013 1:15:53 PM
 */
public enum SystemEnum {
    // 网站应用名。 Jvm系统参数取值。
    JVM_WEB_APP_NAME("msun.web.app.name"),
    // 指定收集慢SQL的时间，单位毫秒。
    I_SLOW_SQL_TIME("I_db.slow.sql.time"),
    // Comset收集url访问时间的标记。
    B_COMSET_RECORD_URL_FLAG("B_comset.recored.urltime.flag"),
    // 静态文件发布版本号配置
    SPRING_STATIC_CONTENT_DEPLOY_VERSION("/spring_static_content_deploy_version.xml");

    private String value;

    public String getValue() {
        return value;
    }

    private SystemEnum(String value) {
        this.value = value;
    }
}
