package com.ms.maven.plugins.cons;

import java.io.File;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class Constants {

    public static final String DEPLOY_AUTOCONFIG_PATH     = "deploy";

    public static final String CLASS_AUTOCONFIG_PATH      = "WEB-INF/classes/META-INF";

    public static final String SUB_AUTOCONFIG_PATH        = "autoconf";

    public static final String AUTOCONFIG_XML             = SUB_AUTOCONFIG_PATH + File.separator + "auto-config.xml";

    public static final String AUTOCONFIG_PROPERTIES_PATH = System.getProperty("user.home") + File.separator
                                                            + "musn.properties";

    public static final String SYSTEM_PROPERTIES          = "musn.properties";

    public static final String JETTY_SERVER               = "deploy/jetty-server";

    public static final String JETTY_CONF_LOG4JXML        = "deploy/conf/log4j.xml";

    public static final String JETTY_SERVER_LOG4JXML      = "WEB-INF/classes/log4j.xml";

    public static final String JETTY_SERVER_TARGET        = "deploy/jetty-server/target";

    public static final String WEBAPPS                    = "deploy/jetty-server/webapps";

    public static final String ROOT_WAR                   = "deploy/jetty-server/webapps/root.war";

    public static final String DEPLOY_BIN                 = "deploy/bin";

    public static final String DEPLOY_CONF                = "deploy/conf";
}
