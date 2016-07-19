package com.ms.maven.plugins;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.ms.maven.plugins.autoconfig.AutoConfigInfo;
import com.ms.maven.plugins.tools.FileTools;
import com.ms.maven.plugins.tools.MavenPropertiesContext;

/**
 * @goal webautoconfig
 * @phase install see all properties http://docs.codehaus.org/display/MAVENUSER/MavenPropertiesGuide
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class WebAutoconfig extends AbstractMojo {

    // 项目的全局配置
    public static final String SYSTEM_PROJECT_PROPERTIES  = "project.properties";
    // 当前用户目录下
    public static final String AUTOCONFIG_PROPERTIES_PATH = System.getProperty("user.home") + File.separator
                                                            + SYSTEM_PROJECT_PROPERTIES;

    // 发布目录
    public static final String DEPLOY_AUTOCONFIG_PATH     = "deploy";

    // web目录
    public static final String CLASS_AUTOCONFIG_PATH      = "WEB-INF/classes/META-INF";

    // 配置文件目录
    public static final String SUB_AUTOCONFIG_PATH        = "autoconf";
    // 配置文件
    public static final String AUTOCONFIG_XML             = SUB_AUTOCONFIG_PATH + File.separator + "auto-config.xml";
    //
    public static final String DEPLOY_BIN                 = "deploy/bin";
    //
    public static final String DEPLOY_CONF                = "deploy/conf";

    /**
     * @parameter expression="${user.home}"
     */
    private String             userHome;

    /**
     * @parameter expression="${project.build.finalName}"
     */
    private String             projectBuildFinalName;

    /**
     * Base directory of the project.
     * 
     * @parameter expression="${basedir}"
     */
    private String             basedir;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String             projectBuildDirectory;

    /**
     * autoconfig paths
     * 
     * @parameter
     */
    private String[]           autoconfigPaths;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("Starting AUTO CONFIG");
        getLog().info("------------------------------------------------------------------------");

        String deployAutoconfigPath = String.format("%s" + File.separator + "%s", basedir, DEPLOY_AUTOCONFIG_PATH);
        String classAutoconfigPath = String.format("%s" + File.separator + "%s" + File.separator + "%s",
                                                   projectBuildDirectory, projectBuildFinalName, CLASS_AUTOCONFIG_PATH);

        autoconfigPaths = getMergeAutoconfigPaths(autoconfigPaths, deployAutoconfigPath, classAutoconfigPath);

        deleteBinAndConfDirs();

        VelocityContext velocityContext = getVelocityContext();
        for (String autoconfigPath : autoconfigPaths) {
            getLog().info("--- AUTO CONFIG " + autoconfigPath + " ---");
            String autoconfigXmlPath = autoconfigPath + File.separator + AUTOCONFIG_XML;
            File autoconfigXml = new File(autoconfigXmlPath);
            if (!autoconfigXml.exists()) {
                getLog().warn("can not find AUTO CONFIG file " + autoconfigXmlPath);
                continue;
            }

            AutoConfigInfo autoConfigInfo = getAutoConfigInfo(autoconfigXmlPath);
            checkConfigInfoValid(autoconfigXmlPath, autoConfigInfo);
            for (Entry<String, String> entry : autoConfigInfo.getRenderMap().entrySet()) {
                dealConfigFiles(velocityContext, autoconfigPath, entry.getKey(), entry.getValue(), true);
            }
            for (Entry<String, String> entry : autoConfigInfo.getUnRenderMap().entrySet()) {
                dealConfigFiles(velocityContext, autoconfigPath, entry.getKey(), entry.getValue(), false);
            }
        }

        chmodUserExec();
    }

    private void chmodUserExec() throws MojoExecutionException {
        File depolyBin = new File(basedir + File.separator + DEPLOY_BIN);
        getLog().info("chmod u+x " + depolyBin.getAbsolutePath() + "/*");
        if (!depolyBin.exists()) {
            getLog().info("path: " + depolyBin.getAbsolutePath() + " is not exists, not need for chmod");
            return;
        }

        for (Object binFileObj : FileUtils.listFiles(depolyBin, null, false)) {
            File binFile = (File) binFileObj;
            try {
                binFile.setExecutable(true);
            } catch (Exception e) {
                String errorMsg = String.format("%s setExecutable() failed", binFile.getAbsolutePath());
                getLog().error(errorMsg, e);
                throw new MojoExecutionException(errorMsg);
            }
        }
    }

    private void deleteBinAndConfDirs() throws MojoExecutionException {
        File depolyBin = new File(basedir + File.separator + DEPLOY_BIN);
        File deployConf = new File(basedir + File.separator + DEPLOY_CONF);
        FileTools.deleteDirectorys(getLog(), depolyBin, deployConf);
    }

    private String getDefaultProjectConfigProperties() {
        return basedir + File.separator + DEPLOY_AUTOCONFIG_PATH + File.separator + SUB_AUTOCONFIG_PATH
               + File.separator + SYSTEM_PROJECT_PROPERTIES;
    }

    private void checkConfigInfoValid(String autoconfigXmlPath, AutoConfigInfo autoConfigInfo)
                                                                                              throws MojoExecutionException {
        shouldNotShareKey(autoconfigXmlPath, autoConfigInfo.getRenderMap().keySet(),
                          autoConfigInfo.getUnRenderMap().keySet());
        shouldNotShareKey(autoconfigXmlPath, autoConfigInfo.getRenderMap().values(),
                          autoConfigInfo.getUnRenderMap().values());
    }

    @SuppressWarnings("unchecked")
    private void shouldNotShareKey(String autoconfigXmlPath, Collection<String> collection1,
                                   Collection<String> collection2) throws MojoExecutionException {
        Collection<String> retain = CollectionUtils.retainAll(collection1, collection2);
        if (retain != null && retain.size() > 0) {
            for (String key : retain) {
                getLog().error(autoconfigXmlPath + " key or value \"" + key
                                       + "\" exists at both renderMap and unRenderMap, not allowed!!");
            }
            throw new MojoExecutionException(
                                             autoconfigXmlPath
                                                     + ": autoConfigInfo renderMap and unRenderMap contain same key or value! see above");
        }
    }

    private void dealConfigFiles(VelocityContext velocityContext, String autoconfigPath, String srcPath,
                                 String destPath, boolean isRendering) throws MojoExecutionException {
        if (StringUtils.isBlank(srcPath) || StringUtils.isBlank(destPath)) {
            throw new MojoExecutionException("AUTO CONFIG dir " + autoconfigPath
                                             + " should not have empty key or value");
        }
        if (!StringUtils.startsWith(srcPath, "/")) {
            srcPath = autoconfigPath + File.separator + SUB_AUTOCONFIG_PATH + File.separator + srcPath;
        }
        if (!StringUtils.startsWith(destPath, "/")) {
            destPath = autoconfigPath + File.separator + destPath;
        }

        checkFilePath(autoconfigPath, srcPath, destPath);
        if (isRendering) {
            rendering(velocityContext, srcPath, destPath);
        } else {
            copy(srcPath, destPath);
        }
    }

    private void checkFilePath(String autoconfigPath, String srcPath, String destPath) throws MojoExecutionException {
        if (!new File(srcPath).exists()) {
            throw new MojoExecutionException("AUTO CONFIG file: " + srcPath + " do not exists!");
        }
        File destDirFile = new File(destPath).getParentFile();
        if (destDirFile != null && !destDirFile.exists()) {
            destDirFile.mkdirs();
        }
    }

    private void rendering(VelocityContext velocityContext, String srcPath, String destPath)
                                                                                            throws MojoExecutionException {
        getLog().info(String.format("--- velocity rendering from %s to %s ---", srcPath, destPath));
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(new File(srcPath));
            fos = new FileOutputStream(new File(destPath));
            String vmFileContent = IOUtils.toString(fis, "utf-8");
            StringWriter sw = new StringWriter();
            Velocity.evaluate(velocityContext, sw, "rendering", vmFileContent);
            IOUtils.write(sw.toString(), fos, "utf-8");
        } catch (Throwable t) {
            String errorMsg = String.format("velocity rendering vm from %s to %s failed", srcPath, destPath);
            getLog().error(errorMsg, t);
            throw new MojoExecutionException(errorMsg);
        } finally {
            closeQuietly(fis, fos);
        }
    }

    private void copy(String srcPath, String destPath) throws MojoExecutionException {
        getLog().info(String.format("--- copy file from %s to %s ---", srcPath, destPath));
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcPath);
            fos = new FileOutputStream(destPath);
            IOUtils.copy(fis, fos);
        } catch (Throwable t) {
            String errorMsg = String.format("copy from %s to %s failed", srcPath, destPath);
            getLog().error(errorMsg, t);
            throw new MojoExecutionException(errorMsg);
        } finally {
            closeQuietly(fis, fos);
        }
    }

    private VelocityContext getVelocityContext() throws MojoExecutionException {
        FileInputStream fis = null;
        StringReader sr1 = null, sr2 = null, sr3 = null;
        try {
            Velocity.init();
            VelocityContext context = new VelocityContext();

            String autoconfigPropertiesPath = getDefaultProjectConfigProperties();
            if (StringUtils.isNotBlank(System.getProperty(SYSTEM_PROJECT_PROPERTIES))) {
                autoconfigPropertiesPath = System.getProperty(SYSTEM_PROJECT_PROPERTIES);
                getLog().info(String.format("find system properties %s=%s", SYSTEM_PROJECT_PROPERTIES,
                                            autoconfigPropertiesPath));
            }
            File autoconfigProperties = new File(autoconfigPropertiesPath);
            if (!autoconfigProperties.exists()) {
                String errorMsg = String.format("can not find %s, check!", autoconfigPropertiesPath);
                getLog().error(errorMsg);
                throw new MojoExecutionException(errorMsg);
            }

            getLog().info("find file " + AUTOCONFIG_PROPERTIES_PATH + " as properties");

            fis = new FileInputStream(autoconfigProperties);
            String propertiesContent = IOUtils.toString(fis, "utf-8");
            sr1 = new StringReader(propertiesContent);

            Properties props = new Properties();
            props.load(sr1);
            // 如果没有配置如下项，则采用缺省值
            // msun_zxcProject_deploy=/Users/shenyi/workspace/yue.web.ysp/deploy
            // msun_zxcProject_home=/Users/shenyi/workspace/yue.web.ysp
            // msun_zxcProject_output=/Users/shenyi/workspace/yue.web.ysp/out
            {
                String[] defaultKeyValues = {
                        //
                        "msun_zxcProject_deploy", basedir + File.separator + DEPLOY_AUTOCONFIG_PATH,
                        //
                        "msun_zxcProject_home", basedir,
                        //
                        "msun_zxcProject_output", basedir + File.separator + "out" };
                boolean hasChanged = false;
                for (int i = 0; i < defaultKeyValues.length; i += 2) {
                    if (!props.containsKey(defaultKeyValues[i])) {
                        props.put(defaultKeyValues[i], defaultKeyValues[i + 1]);
                        hasChanged = true;
                    }
                }
                // 如果有变化，重新初始化
                if (hasChanged) {
                    StringWriter sw = new StringWriter();
                    props.store(sw, "");
                    propertiesContent = sw.toString();
                }
            }

            VelocityContext firstContext = new VelocityContext();

            StringWriter sw = new StringWriter();
            MavenPropertiesContext.put(firstContext, userHome, projectBuildFinalName);
            sr2 = new StringReader(propertiesContent);
            Velocity.evaluate(firstContext, sw, "first evaluate tag", sr2);

            HashSet<String> hs = new HashSet<String>();
            String evaluateProperties = sw.toString();
            int i = 1;
            while (!hs.contains(evaluateProperties) && i++ < 100) {
                evaluateProperties = evaluate(context, evaluateProperties);
            }

            sr3 = new StringReader(evaluateProperties);
            Properties finalProps = new Properties();
            finalProps.load(sr3);
            putContext(context, finalProps);

            return context;
        } catch (Exception e) {
            String errorMsg = "autoconfig getVelocityContext";
            getLog().error(errorMsg, e);
            throw new MojoExecutionException("AUTO CONFIG failed");
        } finally {
            closeQuietly(fis, sr1, sr2, sr3);
        }
    }

    private String evaluate(VelocityContext context, String propertiesContent) throws Exception {
        StringWriter sw = new StringWriter();
        StringReader sr = new StringReader(propertiesContent);
        Properties newProps = new Properties();
        newProps.load(sr);
        putContext(context, newProps);
        Velocity.evaluate(context, sw, "evaluate", propertiesContent);
        return sw.toString();
    }

    private void putContext(VelocityContext context, Properties props) {
        for (Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            context.put(key, value);
        }
    }

    private AutoConfigInfo getAutoConfigInfo(String autoconfigXmlPath) {
        Resource rs = new FileSystemResource(autoconfigXmlPath);
        BeanFactory context = new XmlBeanFactory(rs);
        AutoConfigInfo autoConfigInfo = (AutoConfigInfo) context.getBean("autoConfigInfo");
        return autoConfigInfo;
    }

    private String[] getMergeAutoconfigPaths(String[] autoconfigPaths, String deployAutoconfigPath,
                                             String classAutoconfigPath) {

        if (autoconfigPaths == null || autoconfigPaths.length == 0) {
            return new String[] { deployAutoconfigPath, classAutoconfigPath };
        }

        Set<String> autoconfigPathSet = new HashSet<String>();
        autoconfigPathSet.add(deployAutoconfigPath);
        autoconfigPathSet.add(classAutoconfigPath);
        for (int i = 0; i < autoconfigPaths.length; i++) {
            if (StringUtils.isEmpty(autoconfigPaths[i])) {
                continue;
            }
            if (!autoconfigPaths[i].startsWith("/")) {
                autoconfigPaths[i] = basedir + File.separator + autoconfigPaths[i];
            }
            autoconfigPathSet.add(autoconfigPaths[i]);
        }

        return autoconfigPathSet.toArray(new String[autoconfigPathSet.size()]);
    }

    private void closeQuietly(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                getLog().error("close", e);
            }
        }
    }
}
