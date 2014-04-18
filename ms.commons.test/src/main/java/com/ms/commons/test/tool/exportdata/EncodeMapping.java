/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.exportdata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.ms.commons.test.external.jyaml.org.ho.yaml.Yaml;
import com.ms.commons.test.tool.exportdata.cmd.encodecmd.TableFields;

/**
 * @author zxc Apr 14, 2013 12:16:30 AM
 */
public class EncodeMapping {

    private Map<String, TableFields> gbkMap  = new HashMap<String, TableFields>();
    private Map<String, TableFields> utf8Map = new HashMap<String, TableFields>();

    public Map<String, TableFields> getGbkMap() {
        return gbkMap;
    }

    public void setGbkMap(Map<String, TableFields> gbkMap) {
        this.gbkMap = gbkMap;
    }

    public Map<String, TableFields> getUtf8Map() {
        return utf8Map;
    }

    public void setUtf8Map(Map<String, TableFields> utf8Map) {
        this.utf8Map = utf8Map;
    }

    private static final File f = new File(System.getProperty("user.home") + "/.export_database.ymal");

    public void tryRead() {
        if (f.exists()) {
            try {
                EncodeMapping em = Yaml.loadType(f, EncodeMapping.class);
                this.gbkMap = em.gbkMap;
                this.utf8Map = em.utf8Map;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void tryWrite() {
        try {
            if (f.exists()) {
                f.delete();
            }
            Yaml.dump(this, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
