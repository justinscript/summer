/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.classloader.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.ms.commons.test.classloader.util.VelocityTemplateUtil.KeyConverter;

/**
 * @author zxc Apr 13, 2013 11:08:59 PM
 */
public class AutoConfigUtil {

    final static KeyConverter UNDER_LINE_CONVERTER = new KeyConverter() {

                                                       public Object convert(Object key, boolean isToPro) {
                                                           if (key == null) {
                                                               return key;
                                                           }
                                                           if (key.getClass() == String.class) {
                                                               if (isToPro) {
                                                                   return ((String) key).replace('_', '.');
                                                               } else {
                                                                   return ((String) key).replace('.', '_');
                                                               }
                                                           }
                                                           return key;
                                                       }
                                                   };

    public static AutoConfigMap loadAutoConfigMap(ClassLoader classloader, Properties properties) {
        AutoConfigMap map = new AutoConfigMap();
        try {
            List<URL> urls = loadAllAutoConfigFiles(classloader);
            for (URL url : urls) {
                // if (!url.getFile().contains(".jar!")) {
                fillAutoConfigMap(map, url, properties);
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static AutoConfigMap loadAutoConfigMapWithFind(URLClassLoader classloader, Properties properties) {
        AutoConfigMap map = new AutoConfigMap();
        try {
            List<URL> urls = loadAllAutoConfigFilesWithFind(classloader);
            for (URL url : urls) {
                // if (!url.getFile().contains(".jar!")) {
                fillAutoConfigMap(map, url, properties);
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static URL autoConfigFile(ClassLoader classloader, final Map<Object, Object> context, AutoConfigItem item,
                                     String destpath) {
        return autoConfigFile(classloader, context, item, destpath, false);
    }

    @SuppressWarnings("deprecation")
    public static URL autoConfigFile(ClassLoader classloader, final Map<Object, Object> context, AutoConfigItem item,
                                     String destpath, boolean replaceNewFile) {

        URL sourceUrl = item.getAutoConfig();
        String file = CLFileUtil.getFilePath(sourceUrl.getFile());// From jar, we must get File
        String readFile = ((file.contains(".jar")) ? "jar:" : "file:") + file;

        File newFile = new File(destpath + "/" + item.destfile);

        if (replaceNewFile && newFile.exists() && newFile.isFile()) {
            newFile.delete();
        }

        if (!newFile.exists()) {
            try {
                CLFileUtil.copyAndProcessFileContext(new URL(readFile + "/" + item.getTemplate()), newFile,
                                                     item.getCharset(), new CLFileUtil.Processor() {

                                                         public String process(String content) {
                                                             return VelocityTemplateUtil.mergeContent(context, content,
                                                                                                      UNDER_LINE_CONVERTER);
                                                         }
                                                     });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        try {
            return newFile.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<URL> loadAllAutoConfigFiles(ClassLoader classloader) {
        List<URL> urlList = new ArrayList<URL>();
        urlList.addAll(loadAutoConfigFiles(classloader, "META-INF/autoconf/auto-config.xml"));
        urlList.addAll(loadAutoConfigFiles(classloader, "META-INF/auto-config.xml"));
        return urlList;
    }

    protected static List<URL> loadAutoConfigFiles(ClassLoader classLoader, String resourceName) {
        try {
            List<URL> urlList = new ArrayList<URL>();
            Enumeration<URL> urls = classLoader.getResources(resourceName);

            for (; urls.hasMoreElements();) {
                URL url = urls.nextElement();
                urlList.add(url);
            }
            return urlList;
        } catch (IOException e) {
            return new ArrayList<URL>();
        }
    }

    public static List<URL> loadAllAutoConfigFilesWithFind(URLClassLoader classloader) {
        List<URL> urlList = new ArrayList<URL>();
        urlList.addAll(loadAutoConfigFilesWithFind(classloader, "META-INF/autoconf/auto-config.xml"));
        urlList.addAll(loadAutoConfigFilesWithFind(classloader, "META-INF/auto-config.xml"));
        return urlList;
    }

    protected static List<URL> loadAutoConfigFilesWithFind(URLClassLoader classLoader, String resourceName) {
        try {
            List<URL> urlList = new ArrayList<URL>();
            Enumeration<URL> urls = classLoader.findResources(resourceName);

            for (; urls.hasMoreElements();) {
                URL url = urls.nextElement();
                urlList.add(url);
            }
            return urlList;
        } catch (IOException e) {
            return new ArrayList<URL>();
        }
    }

    protected static void fillAutoConfigMap(AutoConfigMap map, URL config, Properties properties) {
        InputStream fis = null;
        try {
            fis = config.openStream();
            SAXBuilder b = new SAXBuilder();
            Document document = b.build(fis);
            try {
                {
                    List<?> elements = XPath.selectNodes(document, "/config/group/property");

                    for (Object ele : elements) {
                        Element element = (Element) ele;

                        if (element.getAttribute("name") == null) {
                            continue;
                        }
                        if (element.getAttribute("defaultValue") == null) {
                            continue;
                        }

                        String name = element.getAttribute("name").getValue();
                        String defaultValue = element.getAttribute("defaultValue").getValue();

                        if (!properties.containsKey(name)) {
                            properties.put(name, defaultValue);
                        }
                    }
                }

                {
                    List<?> elements = XPath.selectNodes(document, "/config/script/generate");

                    for (Object ele : elements) {
                        Element element = (Element) ele;

                        String template = element.getAttribute("template").getValue();
                        String destFile = element.getAttribute("destfile").getValue();
                        String charset = element.getAttribute("charset").getValue();
                        AutoConfigItem item = new AutoConfigItem(config, template, destFile, charset);

                        map.put(item.getDestfile(), item);
                    }
                }
            } catch (JDOMException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
