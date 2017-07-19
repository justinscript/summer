/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.classloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import sun.misc.Launcher;
import sun.misc.Resource;
import sun.misc.URLClassPath;

import com.ms.commons.test.classloader.manifest.CopiedJDK5Manifest;
import com.ms.commons.test.classloader.util.AutoConfigItem;
import com.ms.commons.test.classloader.util.AutoConfigMap;
import com.ms.commons.test.classloader.util.AutoConfigUtil;
import com.ms.commons.test.classloader.util.ClassDependcyUtil;
import com.ms.commons.test.classloader.util.ClassPathAccessor;
import com.ms.commons.test.common.CollectionUtil;
import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.common.FileUtil;
import com.ms.commons.test.common.StringUtil;
import com.ms.commons.test.common.tool.OutputWriter;
import com.ms.commons.test.constants.IntlTestGlobalConstants;
import com.ms.commons.test.runtime.RuntimeUtil;
import com.ms.commons.test.runtime.constant.RuntimeEnvironment;

/**
 * intl-test URLClassPath
 * 
 * @author zxc Apr 13, 2013 11:06:28 PM
 */
public class IntlTestURLClassPath extends URLClassPath {

    private static final String ISOLATED_CLASS_LOADER    = "org.apache.maven.surefire.booter.IsolatedClassLoader";

    private static final String TESTCASE_JAR_SIGNATURE   = ".intl_test_signature";
    private static final String TESTCASE_JAR_TEMP_DIR    = IntlTestGlobalConstants.TESTCASE_TEMP_DIR + File.separator
                                                           + "expand_jars";
    private static final String TESTCASE_SPRING_TEMP_DIR = IntlTestGlobalConstants.TESTCASE_TEMP_DIR + File.separator
                                                           + "spring_xml";
    static {
        new File(TESTCASE_JAR_TEMP_DIR).mkdirs();
        FileUtil.clearAndMakeDirs(TESTCASE_SPRING_TEMP_DIR);
    }
    private static final String TESTCASE_TEMP_LDRES_DIR  = IntlTestGlobalConstants.TESTCASE_TEMP_DIR + File.separator
                                                           + "loaded_resources" + File.separator
                                                           + "loadedResources.txt";

    // parent class constructors
    public IntlTestURLClassPath(URL[] paramArrayOfURL, URLStreamHandlerFactory paramURLStreamHandlerFactory) {
        super(paramArrayOfURL, paramURLStreamHandlerFactory);
        throw new RuntimeException("Never called!");
    }

    // parent class constructors
    public IntlTestURLClassPath(URL[] paramArrayOfURL) {
        super(paramArrayOfURL);
        throw new RuntimeException("Never called!");
    }

    public IntlTestURLClassPath(URLClassPath urlClassPath) {
        super(getParamArrayOfURL(urlClassPath), getURLStreamHandlerFactory());
    }

    public static boolean isIntlTestURLClassPathInited = false;

    synchronized public static void initIntlTestURLClassLoader() {
        if (!isIntlTestURLClassPathInited) {
            try {
                System.err.println(">>>>>> Begin 'IntlTestURLClassPath'. >>>>>>>>>>>>>>>>>>");
                long BEGIN = System.currentTimeMillis();
                System.err.println("Current classloader: " + IntlTestURLClassPath.class.getClassLoader());
                {
                    URLClassLoader loader = (URLClassLoader) Launcher.getLauncher().getClassLoader();
                    Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
                    ucpField.setAccessible(true);
                    IntlTestURLClassPath itucp = new IntlTestURLClassPath(getLauncherUrlClassPath(loader));
                    ucpField.set(loader, itucp);
                }

                // deal with "org.apache.maven.surefire.booter.IsolatedClassLoader"
                if (ISOLATED_CLASS_LOADER.equals(IntlTestURLClassPath.class.getClassLoader().getClass().getName())) {
                    System.err.println("    >> Begin 'org.apache.maven.surefire.booter.IsolatedClassLoader'. >>>>>>>>>>>>>>>>>>");
                    URLClassLoader isoLoader = (URLClassLoader) IntlTestURLClassPath.class.getClassLoader();
                    Field isoUcpField = URLClassLoader.class.getDeclaredField("ucp");
                    isoUcpField.setAccessible(true);
                    IntlTestURLClassPath isoItucp = new IntlTestURLClassPath(getLauncherUrlClassPath(isoLoader));
                    isoUcpField.set(isoLoader, isoItucp);
                    System.err.println("    << Init 'org.apache.maven.surefire.booter.IsolatedClassLoader' successfuled. <<<<<<");
                }

                System.err.println("Do 'IntlTestURLClassPath' cost:" + (System.currentTimeMillis() - BEGIN));
                System.err.println("<<<<<< Init 'IntlTestURLClassPath' successfuled. <<<<<<");

                isIntlTestURLClassPathInited = true;
            } catch (Exception e) {
                throw ExceptionUtil.wrapToRuntimeException(e);
            }
        }
    }

    private static URLClassPath getLauncherUrlClassPath(URLClassLoader classLoader) {
        try {
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucpField.setAccessible(true);
            return (URLClassPath) ucpField.get(classLoader);
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static URL[] getParamArrayOfURL(URLClassPath urlClassPath) {
        try {
            Field pathField = urlClassPath.getClass().getDeclaredField("path");
            pathField.setAccessible(true);
            List<URL> path = (List) pathField.get(urlClassPath);

            path = dealWithMaevnTest(path);
            path = filterMavenSurefireBooter(path);
            path = adjustJarIndex(path);

            getCallCopyAdditationURLPath();

            List<URL> translatedPath = new ArrayList<URL>(path.size());
            for (URL url : path) {
                translatedPath.add(translateURL(url));
            }

            System.err.println("Final classpath url(s):" + translatedPath);

            return translatedPath.toArray(new URL[0]);
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    private static URLStreamHandlerFactory getURLStreamHandlerFactory() {
        try {
            Field factoryField = Launcher.class.getDeclaredField("factory");
            factoryField.setAccessible(true);
            return (URLStreamHandlerFactory) factoryField.get(null);
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    private static final OutputWriter OUTPUT_WRITER = OutputWriter.createWriter(TESTCASE_TEMP_LDRES_DIR);

    @SuppressWarnings("unchecked")
    public URL findResource(String paramString, boolean paramBoolean) {
        URL resource = super.findResource(paramString, paramBoolean);
        URL newResource = null;

        if (resource != null) {
            boolean testcaseLazyInit = IntlTestProperties.isAntxFlagOn(IntlTestGlobalConstants.TESTCASE_LAZY_INIT);
            if (testcaseLazyInit && paramString.contains("spring_") && paramString.endsWith(".xml")) {
                try {
                    String nResource = TESTCASE_SPRING_TEMP_DIR + "/"
                                       + StringUtil.replaceNoWordChars(resource.toString().hashCode() + paramString)
                                       + ".xml";
                    File newF = new File(nResource);
                    if (!newF.exists()) {
                        InputStream is = resource.openStream();

                        SAXBuilder b = new SAXBuilder();
                        Document document = b.build(new InputStreamReader(is, "UTF-8"));

                        List<Element> elements = XPath.selectNodes(document, "/beans");

                        if (elements != null && elements.size() == 1) {
                            elements.get(0).setAttribute("default-lazy-init", "true");

                            XMLOutputter xmlOutputter = new XMLOutputter();
                            Writer writer = new BufferedWriter(new FileWriter(newF));
                            xmlOutputter.output(document, writer);
                            writer.flush();
                            FileUtil.closeCloseAbleQuitly(writer);

                        }
                    }
                    newResource = newF.toURI().toURL();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        OUTPUT_WRITER.writeLine("FindResource [" + paramString + "] --> " + resource
                                + ((newResource == null) ? "" : " # " + newResource));
        return resource;
    }

    public Resource getResource(String paramString) {
        Resource resource = getResource(paramString, true);
        return resource;
    }

    public Resource getResource(String paramString, boolean paramBoolean) {
        Resource resource = super.getResource(paramString, paramBoolean);
        return resource;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Enumeration findResources(String paramString, boolean paramBoolean) {
        List reses = (List) CollectionUtil.toCollection(ArrayList.class, super.findResources(paramString, paramBoolean));
        OUTPUT_WRITER.writeLine("FindResources [" + paramString + "] ==> " + reses);
        return CollectionUtil.toEnumeration(reses);
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getResources(String paramString) {
        return getResources(paramString, true);
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getResources(String paramString, boolean paramBoolean) {
        return super.getResources(paramString, paramBoolean);
    }

    // =================================== INTL_TEST SPECIAL ===================================

    private static List<URL> filterMavenSurefireBooter(List<URL> urlList) {
        try {
            if (IntlTestURLClassPath.class.getClassLoader().toString().contains("sun.misc.Launcher$AppClassLoader")) {
                if (RuntimeUtil.getRuntime().getEnvironment() == RuntimeEnvironment.Maven) {
                    List<URL> filteredUrlList = new ArrayList<URL>();
                    for (URL u : urlList) {
                        if (u.toString().contains("surefirebooter")) {
                            System.err.println("URL: " + u.toString() + " has been ingored!");
                        } else {
                            filteredUrlList.add(u);
                        }
                    }
                    return filteredUrlList;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in: " + IntlTestURLClassPath.class + "." + "filterMavenSurefireBooter");
            e.printStackTrace();
        }

        return urlList;
    }

    private static List<URL> dealWithMaevnTest(List<URL> urlList) {
        List<URL> newUrlList = new ArrayList<URL>();
        try {
            if (IntlTestURLClassPath.class.getClassLoader().toString().contains("sun.misc.Launcher$AppClassLoader")) {
                if (RuntimeUtil.getRuntime().getEnvironment() == RuntimeEnvironment.Maven) {
                    CopiedJDK5Manifest mf = findSurefireBooterManifest();
                    if (mf != null) {
                        String cp = mf.getMainAttributes().getValue("Class-Path");
                        System.err.println("Maven test class path: " + cp);
                        if (cp != null) {
                            String[] cps = cp.split(" ");
                            for (String c : cps) {
                                try {
                                    newUrlList.add(new URL(c.trim()));
                                } catch (Exception e) {
                                    System.err.println("Error in: " + IntlTestURLClassPath.class + "."
                                                       + "dealWithMaevnTest#new URL(" + c + ")");
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in: " + IntlTestURLClassPath.class + "." + "dealWithMaevnTest");
            e.printStackTrace();
        }
        newUrlList.addAll(urlList);
        return newUrlList;
    }

    private static List<URL> adjustJarIndex(List<URL> urlList) {
        int indexOfJ2EE = findJarIndex(urlList, ".*[\\W]j2ee[^\\\\/]*\\.jar");
        int indexOfMail = findJarIndex(urlList, ".*[\\W]mail[^\\\\/]*\\.jar");
        int indexOfActivation = findJarIndex(urlList, ".*[\\W]activation.*\\.jar");

        int indexMaxMailOrActivation = Math.max(indexOfMail, indexOfActivation);
        if ((indexOfJ2EE == -1) || (indexMaxMailOrActivation == -1)) {
            System.err.println("Cannot find j2ee or mail|activation, do not adj. index.");
        } else {
            if (indexOfJ2EE > indexMaxMailOrActivation) {
                System.err.println("J2ee is after mail|activation, do not adj. index.");
            } else {
                URL j2eeUrl = urlList.remove(indexOfJ2EE);
                urlList.add(j2eeUrl);
                System.err.println("Classpath url order has been adjusted:" + urlList);
            }
        }

        // adjust classes and classes.test
        try {
            if (urlList.size() >= 2) {
                URL url1 = urlList.get(0);
                URL url2 = urlList.get(1);
                if (url1.toString().contains("/target/classes/") && url2.toString().contains("/target/classes.test/")) {
                    urlList.add(0, urlList.remove(1));
                }
            }
        } catch (Exception e) {
            System.err.println("Adjust classes and classes.test failed.");
            e.printStackTrace();
        }

        return urlList;
    }

    // 查询某jar包的位置
    private static int findJarIndex(List<URL> urlList, String jarPattern) {
        for (int i = 0; i < urlList.size(); i++) {
            if (Pattern.compile(jarPattern).matcher(urlList.get(i).getPath()).matches()) {
                return i;
            }
        }
        return -1;
    }

    // 翻译URL，如果对应URL的JAR包中包含autoconfig则解压缩并做autoconfig处理
    private static URL translateURL(URL url) throws Exception {
        URLClassLoader ucl = new URLClassLoader(new URL[] { url });

        // 判断该URL是否有autoconfig元素
        List<URL> autoConfigXmlList = AutoConfigUtil.loadAllAutoConfigFilesWithFind(ucl);
        if ((autoConfigXmlList == null) || (autoConfigXmlList.size() == 0)) {
            return url;
        }

        AutoConfigMap acm = AutoConfigUtil.loadAutoConfigMapWithFind(ucl, IntlTestProperties.PROPERTIES);
        // 是否做URL翻译
        boolean urlTranslated = false;
        // 目标输出文件夹
        String outputPath;
        if ("file".equals(url.getProtocol()) && !url.toString().toLowerCase().endsWith(".jar")) {
            outputPath = FileUtil.convertURLToFilePath(url);
        } else if (url.toString().toLowerCase().endsWith(".jar")) {
            String fileN = FileUtil.convertURLToFile(url).getName();
            String jarUrlPath = fileN + "@" + url.toString().hashCode();
            jarUrlPath = jarUrlPath.replace(".jar", "").replace("alibaba", "");
            outputPath = TESTCASE_JAR_TEMP_DIR + File.separator + StringUtil.replaceNoWordChars(jarUrlPath);
            // 压缩包签名（目前包含文件大小和最后修改时间）
            File signature = new File(outputPath + File.separator + TESTCASE_JAR_SIGNATURE);

            StringBuilder refAst = new StringBuilder();
            if (isOutOfDate(signature, url, refAst)) {
                // 如果JAR包数据已经过期，则：删除目标文件夹，解压缩JAR包，再写入JAR签名时间
                FileUtil.clearAndMakeDirs(outputPath);

                expandJarTo(new JarFile(FileUtil.convertURLToFile(url)), outputPath);

                FileUtils.writeStringToFile(signature, refAst.toString());
            }

            urlTranslated = true;
        } else {
            throw new RuntimeException("URL protocol unknow:" + url);
        }

        // 做autoconfig处理
        System.err.println("Auto config for:" + url);
        for (AutoConfigItem antxConfigResourceItem : acm.values()) {
            AutoConfigUtil.autoConfigFile(ucl, IntlTestProperties.PROPERTIES, antxConfigResourceItem, outputPath, true);
        }

        return urlTranslated ? (new File(outputPath)).toURI().toURL() : url;
    }

    // 将JAR包解压缩到目标文件夹
    private static void expandJarTo(JarFile jarFile, String outputPath) throws IOException {
        List<JarEntry> entries = CollectionUtil.toCollection(ArrayList.class, jarFile.entries());
        for (JarEntry entry : entries) {
            if (entry.isDirectory()) {
                // ignore directory
            } else {
                // 解压缩目标文件
                File efile = new File(outputPath, entry.getName());
                efile.getParentFile().mkdirs();
                InputStream in = new BufferedInputStream(jarFile.getInputStream(entry));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(efile));
                IOUtils.copy(in, out);
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(in);
            }
        }
    }

    // 判断已经解压缩过的JAR文件包是否有更新
    private static boolean isOutOfDate(File signature, URL jar, StringBuilder refAst) throws IOException {
        File jf = FileUtil.convertURLToFile(jar);
        long len = jf.length();
        long mod = jf.lastModified();
        String ast = String.valueOf(len) + "/" + String.valueOf(mod);
        refAst.append(ast);

        if (!signature.exists()) {
            return true;
        }
        String st = FileUtils.readFileToString(signature);
        return (!StringUtil.trimedIgnoreCaseEquals(st, ast));
    }

    // 将当前项目的conf, conf.test数据拷贝到目标文件夹
    private static void getCallCopyAdditationURLPath() {
        try {
            boolean isRunInEclipse = (RuntimeUtil.getRuntime().getEnvironment() == RuntimeEnvironment.Eclipse);
            if (isRunInEclipse) {
                File classPath = new File(IntlTestGlobalConstants.TESTCASE_CLASSPATH);
                String outPath = ClassPathAccessor.getOutputPath(classPath);
                if (!ClassPathAccessor.isSrcConfIncluded(classPath)) {
                    copyDirectoryTo(new File(IntlTestGlobalConstants.USER_DIR + "/src/conf"), new File(outPath));
                }
                if (!ClassPathAccessor.isSrcConfTestIncluded(classPath)) {
                    copyDirectoryTo(new File(IntlTestGlobalConstants.USER_DIR + "/src/conf.test"), new File(outPath));
                }

                // 处理一方库依赖
                ClassDependcyUtil.copyFirstClassDependicies();
            }

            boolean isRunInAntxTest = (RuntimeUtil.getRuntime().getEnvironment() == RuntimeEnvironment.AntxTest);
            if (isRunInAntxTest) {
                String outPath = IntlTestGlobalConstants.USER_DIR + "/target/classes.test";
                copyDirectoryTo(new File(IntlTestGlobalConstants.USER_DIR + "/src/conf.test"), new File(outPath));

                String instrumentedOutPath = IntlTestGlobalConstants.USER_DIR + "/target/instrumented-classes";
                if (new File(instrumentedOutPath).exists()) {
                    copyDirectoryTo(new File(IntlTestGlobalConstants.USER_DIR + "/src/conf.test"),
                                    new File(instrumentedOutPath));
                }
            }
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    // 拷贝文件夹
    private static void copyDirectoryTo(File dirFrom, File dirTo) throws IOException {
        if (dirFrom.exists() && dirFrom.isDirectory()) {
            FileUtil.copyDirectory(dirFrom, dirTo, new FileFilter() {

                public boolean accept(File pathname) {
                    return (!pathname.toString().contains(".svn"));
                }
            });
        }
    }

    private static CopiedJDK5Manifest findSurefireBooterManifest() throws Exception {
        ArrayList<URL> urls = CollectionUtil.toCollection(ArrayList.class,
                                                          IntlTestURLClassPath.class.getClassLoader().getResources("META-INF/MANIFEST.MF"));
        for (URL u : urls) {
            CopiedJDK5Manifest mf = new CopiedJDK5Manifest();
            mf.read(u.openStream());
            if ("org.apache.maven.surefire.booter.SurefireBooter".equals(mf.getMainAttributes().getValue("Main-Class"))) {
                return mf;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String jarPattern = ".*[\\W]j2ee[^\\\\/]*\\.jar";
        System.out.println(Pattern.compile(jarPattern).matcher("./com/alibaba/external/java.j2ee/1.4/java.j2ee-1.4.jar").matches());
    }
}
