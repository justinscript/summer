/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.classloader.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author zxc Apr 13, 2013 11:09:44 PM
 */
public class AntxconfigUtil {

    static final String         USER_HOME              = System.getProperty("user.home");
    static final String         TEMP_DIR               = USER_HOME + "/testcase_antxconfig_temp";

    static final String         BASE_DEFAULT_ANTX_FILE = USER_HOME + "/antx.properties";
    static final String         DEFAULT_ANTX_FILE      = USER_HOME + "/testcase_antx.properties";

    private final static String META_PATH              = "META-INF/autoconf";
    private final static String META_PATH2             = "META-INF";
    private final static String META_FILE              = META_PATH + "/auto-config.xml";
    private final static String META_FILE2             = META_PATH2 + "/auto-config.xml";
    private final static String GEN_PATH               = "/config/script/generate";
    private final static String ANTXCONFIG_CMD         = "antxconfig . ";
    private final static String ENDODING               = "UTF-8";
    private final static String PROPERTY_CLEAN         = "testcase.antxconfig.clean";

    @SuppressWarnings("deprecation")
    public static void changeClassLoader() throws Exception {
        File tmp = new File(TEMP_DIR);
        URLClassLoader cl = new URLClassLoader(new URL[] { tmp.toURL() },
                                               Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(cl);
    }

    private static void writeUrlToFile(URL url, File file) throws Exception {
        InputStream in = url.openStream();
        String str = IOUtils.toString(in, ENDODING);
        str = str.replaceAll("description=\".*\"", "description=\"\"");
        IOUtils.closeQuietly(in);
        FileUtils.writeStringToFile(file, str, ENDODING);
    }

    @SuppressWarnings("unchecked")
    private static void autoconf(File tmp, String autoconfigPath, String autoconfigFile, PrintStream out)
                                                                                                         throws Exception {
        Enumeration<URL> urls = AntxconfigUtil.class.getClassLoader().getResources(autoconfigFile);
        for (; urls.hasMoreElements();) {
            URL url = urls.nextElement();
            // copy xml
            File autoconfFile = new File(tmp, autoconfigFile);
            writeUrlToFile(url, autoconfFile);
            // copy vm
            SAXBuilder b = new SAXBuilder();
            Document document = b.build(autoconfFile);
            List<Element> elements = XPath.selectNodes(document, GEN_PATH);
            for (Element element : elements) {
                String path = url.getPath();
                String vm = element.getAttributeValue("template");
                String vmPath = StringUtils.substringBeforeLast(path, "/") + "/" + vm;
                URL vmUrl = new URL(url.getProtocol(), url.getHost(), vmPath);
                File vmFile = new File(tmp, autoconfigPath + "/" + vm);
                writeUrlToFile(vmUrl, vmFile);
            }
            // call antxconfig
            String args = "";
            if (new File(DEFAULT_ANTX_FILE).isFile()) {
                args = " -u " + DEFAULT_ANTX_FILE;
            }

            Process p = Runtime.getRuntime().exec(ANTXCONFIG_CMD + args, null, tmp);
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String s;
            while ((s = br.readLine()) != null) {
                out.println(new String(s.getBytes(), ENDODING));
            }
            FileUtils.deleteDirectory(new File(tmp, autoconfigPath));
        }
    }

    public static void autoconf(PrintStream out) throws Exception {

        File tmp = new File(TEMP_DIR);
        boolean isClean = "y".equalsIgnoreCase(System.getProperty(PROPERTY_CLEAN));
        if (!isClean && tmp.exists() && tmp.listFiles().length > 0) {
            return;
        }
        autoconf(tmp, META_PATH, META_FILE, out);
        autoconf(tmp, META_PATH2, META_FILE2, out);
        System.setProperty(PROPERTY_CLEAN, "n");
    }

}
