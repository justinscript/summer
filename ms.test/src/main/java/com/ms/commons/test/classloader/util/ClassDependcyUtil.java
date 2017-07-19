/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.classloader.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.common.FileUtil;

/**
 * @author zxc Apr 13, 2013 11:08:48 PM
 */
public class ClassDependcyUtil {

    public static List<File> listUnloadedFirstClassDependcies(ClassLoader classLoader) {
        List<File> ufcdl = new ArrayList<File>();
        List<File> fcdl = listFirstClassDependcies();
        for (File f : fcdl) {
            if (!isBizServicesLocatorXmlLoaded(f, classLoader)) {
                ufcdl.add(f);
            }
        }
        return ufcdl;
    }

    public static boolean isBizServicesLocatorXmlLoaded(File f, ClassLoader classLoader) {
        if ((f == null) || (!f.exists())) {
            return false;
        }
        File biz = new File(f.getAbsolutePath() + "/biz");
        if (!biz.exists()) {
            System.err.println("Path not exists:" + biz);
        }
        final List<String> filenames = new ArrayList<String>();
        biz.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (name.endsWith("_services_locator.xml")) {
                    filenames.add(name);
                }
                return false;
            }
        });

        if (filenames.size() == 0) {
            return false;
        }

        URL url = classLoader.getResource("biz/" + filenames.get(0));

        return (url != null);
    }

    public static List<File> listFirstClassDependcies() {
        try {
            Set<File> firstClassDependcyList = new LinkedHashSet<File>();
            PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver(
                                                                                                ClassDependcyUtil.class.getClassLoader());
            Resource[] resources = pmrpr.getResources("classpath*:/com");
            if (resources != null) {
                for (Resource r : resources) {
                    if ("file".equals(r.getURL().getProtocol())) {
                        String p = r.getFile().getAbsolutePath();

                        String newP = p + "../../../../src/conf";

                        File f = new File(newP).getCanonicalFile();

                        if (f.exists()) {
                            firstClassDependcyList.add(f);
                        } else {
                            System.err.println("First class dependcy cannot find:" + f);
                        }
                    }
                }
            }
            return new ArrayList<File>(firstClassDependcyList);
        } catch (IOException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public static void copyFirstClassDependicies() {
        try {
            PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver(
                                                                                                ClassDependcyUtil.class.getClassLoader());
            Resource[] resources = pmrpr.getResources("classpath*:/com");
            if (resources != null) {
                for (Resource r : resources) {
                    if ("file".equals(r.getURL().getProtocol())) {
                        String p = r.getFile().getAbsolutePath();

                        String newP;
                        if (p.contains("_test/") || p.contains("/test-")) {
                            newP = p + "/../../../src/conf.test";
                        } else {
                            newP = p + "/../../../src/conf";
                        }

                        File f = new File(newP).getCanonicalFile();
                        File t = new File(p + "/../").getCanonicalFile();

                        if (f.exists() && (f.isDirectory())) {
                            System.err.println("Copy from '" + f + "' to '" + t + "/../'.");
                            FileUtil.copyDirectory(f, t, new FileFilter() {

                                public boolean accept(File pathname) {
                                    return (!pathname.toString().contains(".svn"));
                                }
                            });
                        } else {
                            System.err.println("First class dependcy cannot find:" + f);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }
}
