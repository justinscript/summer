/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.nisa.interfaces.ConfigService;
import com.ms.commons.nisa.listener.ConfigListener;
import com.ms.commons.nisa.mina.client.MinaClient;

/**
 * @author zxc Apr 12, 2013 6:51:17 PM
 */
public class ConfigServiceImpl implements ConfigService {

    // nisa配置文件在系统属性中的key值
    public static final String      KEY_NISA_PROPERTIES        = "msun.datasource.properties";
    // 日志文件文件名
    private static final String     PROPERTIES_FILE_NAME       = "msun.datasource.properties";
    // 是否读取本地文件key
    private static final String     READ_LOCAL_FILE_KEY        = "nisa.config.read.local.file";
    //
    private static final String     LOCAL_FILE_PATH_KEY        = "nisa.config.local.properties.path";
    private static final String     NISA_SERVER_IP_KEY         = "nisa.server.ip";
    private static final String     NISA_SERVICE_PORT_KEY      = "nisa.server.port";
    private static final String     NISA_CLIENT_PROJECT_KEY    = "nisa.client.project";
    private static final String     NISA_CLIENT_APPNAME_KEY    = "nisa.client.appname";
    private static final String     NISA_CLIENT_CONFIGTYPE_KEY = "nisa.client.configtype";
    // System.properties中的key
    public static final String      KEY_START_MINA_CLIENT      = "nisa.mina.client.start";
    // 默认是启动MINA客户端的
    public static final boolean     IS_START_MINA_CLIENT       = true;

    private static final String     USER_HOME                  = "{user.home}";
    private static final String     WEB_APP_HOME               = "{web.app.name}";

    private ExpandLogger            log                        = LoggerFactoryWrapper.getLogger(ConfigServiceImpl.class);
    private static ConfigMap        configMap                  = new ConfigMap();

    private String                  serverIp;                                                                            // 配置中心服务器IP
    private int                     serverPort;                                                                          // 配置中心服务器的端口号
    private String                  clientProject;                                                                       // 客户端项目名
    private String                  clientApp;                                                                           // 客户端应用名
    private String                  configType;                                                                          // 取配置项的版本号。这个是配置中心定义的名称。例如：DEV,TEST,RUN
    private String                  configPath;                                                                          // 如果有值，或者路径名存在，就取本地的配置项，而不是远程注册
    private boolean                 readLocalFile;                                                                       // 如果true，就取本地的配置项，而不是远程注册

    private List<ConfigListener>    listeners                  = new ArrayList<ConfigListener>();

    private MinaClient              minaClient;

    public static ConfigServiceImpl instance;

    public void setReadLocalFile(boolean readLocalFile) {
        this.readLocalFile = readLocalFile;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setClientApp(String clientApp) {
        this.clientApp = clientApp;
    }

    private void readProperties(String filepath) throws IOException {
        InputStream path = new FileInputStream(filepath);
        Properties pros = new Properties();
        pros.load(path);
        @SuppressWarnings("rawtypes")
        Iterator ir = pros.keySet().iterator();
        while (ir.hasNext()) {
            Object key = ir.next();
            Object value = pros.getProperty((String) key);
            value = convertValue((String) key, (Serializable) value);
            setKeyAndValue((String) key, value.toString());
        }
        configMap.printConfigMap();
    }

    private void setKeyAndValue(String key, String value) {

        log.debug("Key=" + key + " Value=" + value);
        if (key.startsWith("I_")) {
            configMap.putKV(key, Integer.parseInt(value.trim()));
        } else if (key.startsWith("B_")) {
            configMap.putKV(key, Boolean.parseBoolean(value.trim()));
        } else if (key.startsWith("F_")) {
            configMap.putKV(key, Float.parseFloat(value.trim()));
        } else if (key.startsWith("S_")) {
            configMap.putKV(key, value.trim());
        } else if (key.startsWith("IA_")) {
            String[] sp = value.trim().split(";");
            int v[] = new int[sp.length];
            for (int i = 0; i < sp.length; i++) {
                v[i] = Integer.parseInt(sp[i].trim());
            }
            configMap.putKV(key, v);
        } else if (key.startsWith("BA_")) {
            String[] sp = value.trim().split(";");
            boolean v[] = new boolean[sp.length];
            for (int i = 0; i < sp.length; i++) {
                v[i] = Boolean.parseBoolean(sp[i].trim());
            }
            configMap.putKV(key, v);
        } else if (key.startsWith("FA_")) {
            String[] sp = value.trim().split(";");
            float v[] = new float[sp.length];
            for (int i = 0; i < sp.length; i++) {
                v[i] = Float.parseFloat(sp[i].trim());
            }
            configMap.putKV(key, v);
        } else if (key.startsWith("SA_")) {
            configMap.putKV(key, value.trim().split(";"));
        } else {
            log.error("The kv is error ! please check it . ----- Key=" + key + " Value=" + value);
        }
    }

    @SuppressWarnings("static-access")
    public void init() {
        instance = this; // 特殊用途的。一种撮的做法。
        // 获取配置文件
        // 先从系统属性中读，如果有则使用此文件
        String file = null;
        String value = System.getProperty(KEY_NISA_PROPERTIES);
        if (value == null || value.trim().length() == 0) {
            log.warn("系统属性中没有设置nisa配置中心配置文件路径");
            // 没有，则取用户路径下的配置文件
            String userdir = System.getProperty("user.home");
            file = userdir + File.separator + PROPERTIES_FILE_NAME;
            log.warn("使用当前用户路径下的配置文件msun.datasource.properties");
        } else {
            file = value;
        }
        File f = new File(file);
        // 如果文件不存在
        if (!f.exists()) {
            log.error("配置文件 " + file + " 不存在!!!!!!!!!");
            throw new NisaException("nisa配置文件:" + file + " 不存在!!!");
        }
        Properties prop = new Properties();
        try {
            log.info("读取配置文件:" + file);
            prop.load(new FileInputStream(f));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            log.recordThrowable("error", "nisa配置文件:" + file + " 没有找到", e1);
        } catch (IOException e1) {
            e1.printStackTrace();
            log.recordThrowable("error", "nisa配置文件:" + file + " 没有找到", e1);
        }
        String readlocal = prop.getProperty(READ_LOCAL_FILE_KEY);
        // readlocal配置不存在
        if (readlocal == null || readlocal.trim().length() == 0) {
            log.error("配置信息 nisa.config.read.local.file 不存在!!!");
            throw new NisaException("配置信息 nisa.config.read.local.file 不存在!!!");
        } else {
            readLocalFile = Boolean.valueOf(readlocal);
            log.info("nisa.config.read.local.file: " + readLocalFile);
        }
        // path
        String localpath = prop.getProperty(LOCAL_FILE_PATH_KEY);
        if (localpath == null || localpath.trim().length() == 0) {
            log.error("配置信息 nisa.config.local.properties.path 不存在!!!");
            if (readLocalFile) {
                throw new NisaException("配置信息 nisa.config.read.local.file 不存在!!!");
            }
        } else {
            log.info("nisa.config.read.local.file: " + localpath);
            if (localpath.contains(USER_HOME)) {
                String userhome = System.getProperty("user.home");
                localpath = localpath.replace(USER_HOME, userhome);
                log.info("nisa.config.read.local.file: 真实路径: " + localpath);
            }
            configPath = localpath;
        }
        // server ip
        String sip = prop.getProperty(NISA_SERVER_IP_KEY);
        if (sip == null || sip.trim().length() == 0) {
            log.error("配置信息 nisa.server.ip 不存在!!!");
            if (readLocalFile) {
                throw new NisaException("配置信息 nisa.server.ip 不存在!!!");
            }
        } else {
            serverIp = sip;
            log.info("nisa.server.ip : " + serverIp);
        }
        // server port
        String sport = prop.getProperty(NISA_SERVICE_PORT_KEY);
        if (sport == null || sport.trim().length() == 0) {
            log.error("配置信息 nisa.server.port 不存在");
            if (readLocalFile) {
                throw new NisaException("配置信息 nisa.server.port 不存在!!!");
            }
        } else {
            try {
                serverPort = Integer.parseInt(sport);
            } catch (NumberFormatException e) {
                log.error("配置信息 nisa.server.port 存在 但不是数字");
                throw new NisaException("配置信息 nisa.server.port 不是数字");
            }
            log.info("nisa.server.port : " + serverPort);
        }
        // project
        String project = prop.getProperty(NISA_CLIENT_PROJECT_KEY);
        if (project == null || project.trim().length() == 0) {
            log.error("配置信息 nisa.client.project 不存在");
            if (readLocalFile) {
                throw new NisaException("配置信息 nisa.client.project 不存在!!!");
            }
        } else {
            clientProject = project;
            log.info("nisa.client.project : " + clientProject);
        }
        // appname
        String appname = prop.getProperty(NISA_CLIENT_APPNAME_KEY);
        if (appname == null || appname.trim().length() == 0) {
            log.error("配置信息 nisa.client.appname 不存在");
            if (readLocalFile) {
                throw new NisaException("配置信息 nisa.client.appname 不存在!!!");
            }
        } else {
            clientApp = appname;
            String tmpSystemAppName = System.getProperty("msun.web.app.name");
            if (tmpSystemAppName != null) {
                clientApp += "(" + tmpSystemAppName + ")";
            }
            log.info("nisa.client.appname : " + clientApp);
        }
        // type
        String apptype = prop.getProperty(NISA_CLIENT_CONFIGTYPE_KEY);
        if (apptype == null || apptype.trim().length() == 0) {
            log.error("配置信息 nisa.client.configtype 不存在");
            if (readLocalFile) {
                throw new NisaException("配置信息 nisa.client.configtype 不存在!!!");
            }
        } else {
            configType = apptype;
            log.info("nisa.client.configtype : " + configType);
        }
        // 是否启动客户端
        boolean start = IS_START_MINA_CLIENT;
        String startvalue = System.getProperty(KEY_START_MINA_CLIENT);
        if (!(startvalue == null || startvalue.trim().length() == 0)) {
            start = Boolean.parseBoolean(startvalue);
        }
        try {
            if (readLocalFile) {
                File lf = new File(configPath);
                if (lf.exists()) {
                    log.info("读取本地配置文件: " + configPath);
                    readProperties(lf.getAbsolutePath());
                } else {
                    log.error("本地配置文件读取失败。。。文件不存在");
                    throw new NisaException("本地配置文件读取失败。。。文件不存在!!!");
                }
            }
            // 不是本地配置文件就启动远程配置项
        } catch (Exception e) {
            log.error("Init ConfigService Failure! 5555555555 ", e);
            e.printStackTrace();
            throw new NisaException("本地配置文件读取失败。。。");
        }
        if (!readLocalFile && start) {
            minaClient = new MinaClient();
            try {
                minaClient.start(clientProject, clientApp, configType, serverIp, serverPort, false);
                log.info("start minaclient:....");
            } catch (IOException e) {
                log.info("连接nisa服务器失败。。。", e);
                throw new NisaException("连接nisa服务器失败。。。");
            }
        } else {
            log.info("未启动minaclient...");
        }

        if (!readLocalFile && start) {
            int count = 0;
            while (!minaClient.getMinaClientHandler().isConnected()) {
                log.info("等待读取配置中心的数据.... sleep 1s!  ip=" + sip + "  prot=" + sport);
                try {
                    Thread.currentThread().sleep(1000);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (count > 60) {
                    log.info("等待读取配置中心的数据已经超过60s了，请检查配置中心是否正确?");
                    System.exit(-1);
                }
            }
        }
    }

    public void deal(MinaMessage minaMessage) {
        // hasReadConfigCenter = true;
        HashMap<String, Serializable> paramMap = minaMessage.getParamMap();
        if (paramMap != null && !paramMap.isEmpty()) {
            ConfigMap newConfigMap = configMap.copy();
            Iterator<String> ir = paramMap.keySet().iterator();
            while (ir.hasNext()) {
                String key = ir.next();
                Serializable value = paramMap.get(key);
                value = convertValue(key, value);
                newConfigMap.putKV(key, value);
                // log.info("更新配置中心属性。key="+key+" value="+value);
            }
            configMap = newConfigMap;
            configMap.printConfigMap();
            fireConfigListener();
        } else {
            log.info("配置中心属性没有发生变化!");
        }
    }

    /**
     * 替换一些环境变量。例如{user.home}和{web.app.name}
     * 
     * @return
     */
    public static Serializable convertValue(String key, Serializable value) {
        if (key == null || value == null) {
            return value;
        }
        if (key.startsWith("S_") || key.startsWith("SA_")) {
            if (value instanceof String) {
                String strV = (String) value;
                String userhome = System.getProperty("user.home");
                if (userhome != null) {
                    strV = strV.replace(USER_HOME, userhome);
                }
                String appName = System.getProperty("msun.web.app.name");
                if (appName != null) {
                    strV = strV.replace(WEB_APP_HOME, appName);
                }
                return strV;
            }
        }
        return value;
    }

    public String getKV(String key, String defaultValue) {
        return configMap.getKV(key, defaultValue);
    }

    public int getKV(String key, int defaultValue) {
        return configMap.getKV(key, defaultValue);
    }

    public float getKV(String key, float defaultValue) {
        return configMap.getKV(key, defaultValue);
    }

    public boolean getKV(String key, boolean defaultValue) {
        return configMap.getKV(key, defaultValue);
    }

    public String[] getKVStringArray(String key) {
        return configMap.getKVStringArray(key);
    }

    public int[] getKVIntArray(String key) {
        return configMap.getKVIntArray(key);
    }

    public float[] getKVFloatArray(String key) {
        return configMap.getKVFloatArray(key);
    }

    public boolean[] getKVBooleanArray(String key) {
        return configMap.getKVBooleanArray(key);
    }

    public void addConfigListener(ConfigListener configListener) {
        listeners.add(configListener);
        log.error("注册监听器成功! Class=" + configListener.getClass().getName() + " Name=" + configListener.getName());
    }

    private void fireConfigListener() {
        log.debug("更新配置项");
        if (listeners != null) {
            for (ConfigListener c : listeners) {
                c.updateConfig();
            }
        }
    }

    public MinaClient getMinaClient() {
        return minaClient;
    }
}
