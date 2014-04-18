/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.autoconfig;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * @author zxc Apr 15, 2014 10:16:32 PM
 */
public class AutoConfig {

    private static String  AUTO_PATH = "autoconf";
    private static boolean isDebug   = false;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("ERROR! please check args. example: java com.ms.commons.autoconfig.AutoConfig propertiesPath autoConfigPath !");
            throw new Error("args is error !");
        }

        String propertiesPath = args[0];
        String autoConfigPath = args[1];
        String s = System.getProperty("msun.autoconfig.debug");
        if (s != null && s.equalsIgnoreCase("true")) {
            isDebug = true;
        }
        convert(propertiesPath, autoConfigPath);
    }

    private static void readProperties(VelocityContext context, String propertiesPath) {
        Properties props = new Properties();
        String userHome = System.getProperty("user.home");
        VelocityContext tmpContext = new VelocityContext();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(propertiesPath));
            props.load(in);
            Iterator<Object> ir = props.keySet().iterator();
            System.out.println("-------1------ begin read properties file ");
            while (ir.hasNext()) {
                String key = ir.next().toString();
                String value = props.getProperty(key);
                value = value.replaceAll("\\{user\\.home\\}", userHome);
                // System.out.println(key + "=" + value);
                tmpContext.put(key, value);
            }
            // 把properties当成VM模板渲染一次，这样把properties中的$变量替换掉
            StringWriter w = new StringWriter();
            StringBuilder s = readFile(propertiesPath);
            try {
                Velocity.evaluate(tmpContext, w, "megerProperties", s.toString());
            } catch (ParseErrorException e) {
                e.printStackTrace();
            } catch (MethodInvocationException e) {
                e.printStackTrace();
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // ////重新读渲染后的Properties文件，把其中的变量替换掉
            in = new BufferedInputStream(new ByteArrayInputStream(w.toString().getBytes()));
            props.load(in);
            ir = props.keySet().iterator();
            System.out.println("-------2------ begin read properties file ");
            while (ir.hasNext()) {
                String key = ir.next().toString();
                String value = props.getProperty(key);
                value = value.replaceAll("USER_HOME", userHome);
                System.out.println(key + "=" + value);
                context.put(key, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> readAutoConfig(String autoConfigPath) {
        autoConfigPath += File.separator + AUTO_PATH + File.separator + "auto-config.xml";
        Resource rs = (Resource) new FileSystemResource(autoConfigPath);
        BeanFactory context = new XmlBeanFactory((org.springframework.core.io.Resource) rs);
        AutoConfigInfo autoConfig = (AutoConfigInfo) context.getBean("autoConfigInfo");
        return autoConfig.getFileMap();
    }

    private static void dealVmFile(VelocityContext context, String source, String target) {
        StringWriter w = new StringWriter();
        StringBuilder s = readFile(source);
        try {
            Velocity.evaluate(context, w, "mystring", s.toString());
        } catch (ParseErrorException e) {
            e.printStackTrace();
        } catch (MethodInvocationException e) {
            e.printStackTrace();
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(source + "--->");
        appendTxt(target, w.toString(), false);
        System.out.println(target + " OK!");
        if (isDebug) {
            System.out.println(w.toString());
        }
    }

    private static void convert(String propertiesPath, String autoConfigPath) {
        try {
            Velocity.init();

            VelocityContext context = new VelocityContext();
            readProperties(context, propertiesPath);

            Map<String, String> map = readAutoConfig(autoConfigPath);
            Iterator<String> ir = map.keySet().iterator();
            while (ir.hasNext()) {
                String sourceFile = ir.next();
                String targetFile = map.get(sourceFile);
                dealVmFile(context, autoConfigPath + File.separator + AUTO_PATH + File.separator + sourceFile,
                           autoConfigPath + File.separator + targetFile);
            }

        } catch (ResourceNotFoundException e1) {
            e1.printStackTrace();
        } catch (ParseErrorException e2) {
            e2.printStackTrace();
        } catch (MethodInvocationException e3) {
            e3.printStackTrace();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    private static StringBuilder readFile(String fileName) {

        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb;
    }

    public static void appendTxt(String fileName, String content, boolean append) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName, append), "GBK");
            out.write(content);
            out.close();
        } catch (IOException e) {
            try {
                File tmp = new File(fileName);
                if (!tmp.exists()) {
                    tmp.createNewFile();
                }
                Thread.sleep(50);
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName, append), "GBK");
                out.write(content);
                out.close();
            } catch (Exception e2) {
                System.out.println(" fileName cannot create. fileName=" + fileName);
                e2.printStackTrace();
            }
        }
    }
}
