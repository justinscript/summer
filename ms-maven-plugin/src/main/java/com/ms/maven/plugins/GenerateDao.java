package com.ms.maven.plugins;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import com.ms.maven.plugins.generateDao.DaoTemplateRender;
import com.ms.maven.plugins.generateDao.DatabaseMeta;

/**
 * @goal generateDao
 * @phase install:install-file see all properties http://docs.codehaus.org/display/MAVENUSER/MavenPropertiesGuide
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class GenerateDao extends AbstractMojo {

    /**
     * @parameter expression="${style}" default-value=""
     */
    private String style;
    /**
     * @parameter expression="${url}" default-value=""
     */
    private String url;
    /**
     * @parameter expression="${user}" default-value=""
     */
    private String user;
    /**
     * @parameter expression="${password}" default-value=""
     */
    private String password;
    /**
     * @parameter expression="${tables}" default-value=""
     */
    private String tables;
    /**
     * @parameter expression="${serviceName}" default-value=""
     */
    private String serviceName;
    /**
     * @parameter expression="${basePackage}" default-value=""
     */
    private String basePackage;

    /**
     * @component
     */
    Prompter       prompter;

    private String pwd;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        pwd = System.getProperty("user.dir", System.getProperty("user.home"));
        Log log = getLog();
        usage();
        setParameterIfNeed();
        assertParameterNotBlank();
        DatabaseMeta databaseMeta = new DatabaseMeta(url, user, password, tables, log);
        if (StringUtils.equals(style, "simple")) {
            databaseMeta.setIsSimple(true);
        }
        DaoTemplateRender daoTemplateRender = new DaoTemplateRender(databaseMeta, serviceName, basePackage, pwd,
                                                                    prompter, getLog());
        try {
            daoTemplateRender.render();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoExecutionException("daoTemplateRender.render()", e);
        }

    }

    private void assertParameterNotBlank() {
        url.trim();
        user.trim();
        serviceName.trim();
        tables.trim();
        tables.replaceAll(" ", "");
        basePackage.trim();
        if (StringUtils.isBlank(url) || StringUtils.isBlank(user) || StringUtils.isBlank(password)
            || StringUtils.isBlank(serviceName) || StringUtils.isBlank(tables) || StringUtils.isBlank(basePackage)
            || !basePackage.startsWith("com.") || basePackage.length() < 5) {
            getLog().error("配置参数不满足，请检查输入是否有误！");
            throw new RuntimeException("need url, user, password, serviceName, tables, basePackage");
        }
    }

    private void setParameterIfNeed() {
        getLog().info("请按照提示依次输入jdbc连接url，user，password（参照~/msun.datasource.properties），需要生成的表名，服务名字, 包基本路径");
        if (StringUtils.isBlank(url)) {
            url = getPrompter("输入 db.jdbc.url，比如 jdbc:oracle:thin:@192.168.1.190:1521:msun\n> ");
        }
        if (StringUtils.isBlank(user)) {
            user = getPrompter("输入 db.jdbc.user，比如 test\n> ");
        }
        if (StringUtils.isBlank(password)) {
            password = getPrompter("输入 db.jdbc.pwd，比如 test1234\n> ");
        }
        if (StringUtils.isBlank(tables)) {
            tables = getPrompter("输入需要自动生成的表名，使用 , 分割，比如 cms_node,cms_tag,cms_data,cms_tag_node\n> ");
        }
        if (StringUtils.isBlank(serviceName)) {
            serviceName = getPrompter("输入生成的服务类名，比如 CmsService\n> ");
        }
        if (StringUtils.isBlank(basePackage)) {
            basePackage = getPrompter("输入生成基本包路径，比如 com.ms.biz.cms\n> ");
        }
    }

    private String getPrompter(String prompt) {
        try {
            return prompter.prompt(prompt);
        } catch (PrompterException e) {
            getLog().error("getPrompter() error", e);
            e.printStackTrace();
        }
        return null;
    }

    private void usage() {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("Starting GENERATE DAO");
        getLog().info("------------------------------------------------------------------------");
        String usage = "这是两只猫\n  |\\_/|        ****************************    (\\__/)\n"
                       + " / @ @ \\       *  Keep It Simple, Stupid  *   (='.'=)\n"
                       + "( > º < )      *   Small is beautiful     *   (\")_(\") \n"
                       + " `»»x««´       *   Do one thing well.     *\n"
                       + " /  O  \\       ****************************";
        getLog().info(usage);
    }
}
