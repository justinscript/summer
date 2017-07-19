/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.classloader;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import sun.misc.Launcher;
import sun.misc.URLClassPath;

import com.ms.commons.test.common.ExceptionUtil;

/**
 * @author zxc Apr 13, 2013 11:06:01 PM
 */
public class LauncherClassLoaderUtil {

    private static URLClassPath launderUrlClassPath = getLauncherUrlClassPath();

    public static void addUrlToLaunderCLassLoader(File folder) {
        System.err.println("Adding jar(s) in: " + folder);
        if ((folder != null) && (folder.exists())) {
            listFiles(null, folder, new FileFilter() {

                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return !pathname.toString().contains(".svn");
                    }
                    if (!pathname.toString().endsWith(".jar")) {
                        return false;
                    }
                    try {
                        addUrlToLaunderCLassLoader(pathname.toURI().toURL());
                    } catch (Exception e) {
                        throw ExceptionUtil.wrapToRuntimeException(e);
                    }
                    return false;
                }
            });
        }
        System.err.println("<<<<<<<<<<<<<< FINISH ADDING <<<<<<<<<<<<<<");
    }

    public static void addUrlToLaunderCLassLoader(URL url) {
        System.err.println("Add class path to system classpath: " + url);
        launderUrlClassPath.addURL(url);
    }

    public static URLClassPath getLauncherUrlClassPath() {
        try {
            URLClassLoader loader = (URLClassLoader) Launcher.getLauncher().getClassLoader();
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucpField.setAccessible(true);
            return (URLClassPath) ucpField.get(loader);
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    private static List<File> listFiles(List<File> list, File file, FileFilter filter) {
        if (file.exists()) {
            if (file.isFile()) {
                if (filter.accept(file)) {
                    if (list != null) {
                        list.add(file);
                    }
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles(filter);
                if (files != null) {
                    for (File f : files) {
                        if (filter.accept(f)) {
                            if (f.isDirectory()) {
                                listFiles(list, f, filter);
                            } else if (f.isFile()) {
                                if (list != null) {
                                    list.add(f);
                                }
                            } else {
                                throw new RuntimeException("Error file type:" + file);
                            }
                        }
                    }
                }
            } else {
                throw new RuntimeException("Error file type:" + file);
            }
        }
        return list;
    }
}
