/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader;

/**
 * @author zxc Apr 13, 2013 11:34:08 PM
 */
public enum DataReaderType {

    None("none"),

    Excel("excel"),

    ExcelVertical("excelVertical"),

    Xml("xml"),

    Json("json"),

    Wiki("wiki"),

    TreeXml("tree.xml"),

    Yaml("yaml");

    private String type;

    DataReaderType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public String toString() {
        return getType();
    }
}
