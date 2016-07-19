package com.ms.maven.plugins.generateDao;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.util.FileUtils;

import com.ms.maven.plugins.tools.ResourcesTools;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class DaoTemplateRender {

    private DatabaseMeta          databaseMeta;
    private Prompter              prompter;
    private Log                   log;
    private String                serviceName;
    private String                basePackage;
    private String                pwd;
    private String                finalPackageName;
    private String                finalPackagePath;
    private String                shortSchemaName;
    private String[]              placeholder;
    private String[]              realVariable;

    private Map<String, Object>   contextMap           = null;

    private static final String   PREFIX               = "META-INF/com/ms/biz/generateTmpl/";
    private static String[]       projectPathTempl     = new String[] {
            "pom.xml",
            "src/main/resources/META-INF/spring/biz",
            "src/main/java/${finalPackagePath}/dataobject",
            "src/main/java/${finalPackagePath}/cons",
            "src/main/java/${finalPackagePath}/queryobject",
            "src/main/java/${finalPackagePath}/dao/interfaces",
            "src/main/java/${finalPackagePath}/dao/impl",
            "src/main/resources/META-INF/spring/biz/spring_${shortSchemaName}_dal.xml",
            "src/main/resources/META-INF/spring/biz/spring_${shortSchemaName}_service.xml",
            "src/main/resources/META-INF/ibatis/sql/${shortSchemaName}",
            "Lsrc/main/resources/META-INF/ibatis/sql/${shortSchemaName}/sqlmap-${tableName}.xml",
            "src/main/resources/META-INF/ibatis/sql-map-${shortSchemaName}.xml",
            "Lsrc/main/java/${finalPackagePath}/dataobject/${doClassName}DO.java",
            // "src/main/java/${finalPackagePath}/service/impl/${serviceName}Impl.java",
            // "src/main/java/${finalPackagePath}/service/interfaces/${serviceName}.java",
            "src/main/java/${finalPackagePath}/service/${serviceName}Locator.java",
            "Lsrc/main/java/${finalPackagePath}/queryobject/${doClassName}Query.java",
            "Lsrc/main/java/${finalPackagePath}/queryobject/${doClassName}Updater.java",
            "Lsrc/main/java/${finalPackagePath}/dao/interfaces/${doClassName}Dao.java",
            "Lsrc/main/java/${finalPackagePath}/dao/impl/${doClassName}DaoImpl.java", };

    private String[]              servicePathTempl     = new String[] {
            "src/main/java/${finalPackagePath}/service/interfaces", "src/main/java/${finalPackagePath}/service/impl",
            "src/main/java/${finalPackagePath}/service/impl/${serviceName}Impl.java",
            "src/main/java/${finalPackagePath}/service/interfaces/${serviceName}.java" };

    private static final String[] testMethods          = new String[] { "insert", "update", "find", "list", "count",
            "delete"                                  };
    private static final String[] projectTestPathTempl = new String[] { "src/test/resources/log4j.xml",
            "src/test/java/${finalPackagePath}/service/${serviceName}Test.java",
            "src/test/resources/${finalPackagePath}/service/${serviceName}TestData",
            "Lsrc/test/java/${finalPackagePath}/dao/${doClassName}DaoTest.java",
            "Lsrc/test/resources/${finalPackagePath}/dao/${doClassName}DaoTestData/prepare.wiki",
            "Lsrc/test/resources/${finalPackagePath}/dao/${doClassName}DaoTestData/${testMethod}_prepare.wiki",
            "Lsrc/test/resources/${finalPackagePath}/dao/${doClassName}DaoTestData/${testMethod}_result.wiki", };

    public DaoTemplateRender(DatabaseMeta databaseMeta, String serviceName, String basePackage, String pwd,
                             Prompter prompter, Log log) {
        this.databaseMeta = databaseMeta;
        this.serviceName = serviceName;
        this.basePackage = basePackage;
        this.pwd = pwd;
        this.prompter = prompter;
        this.log = log;
        this.finalPackageName = basePackage.substring(4);
        this.finalPackagePath = basePackage.replace(".", "/");
        this.shortSchemaName = finalPackageName.substring(finalPackageName.lastIndexOf('.') + 1);
        this.placeholder = new String[] { "serviceName", "basePackage", "finalPackageName", "finalPackagePath",
                "shortSchemaName" };
        this.realVariable = new String[] { serviceName, basePackage, finalPackageName, finalPackagePath,
                shortSchemaName };
        if (!databaseMeta.getIsSimple()) {
            List<String> strList = new ArrayList<String>();
            for (String path : projectPathTempl) {
                strList.add(path);
            }
            for (String path : servicePathTempl) {
                strList.add(path);
            }
            projectPathTempl = strList.toArray(new String[0]);
        }
    }

    public void render() throws Exception {
        VelocityContext velocityContext = getVelocityContext();
        mkBaseDirAndEmptyFile();
        render_pom_xml(velocityContext);
        render_spring_dal(velocityContext);
        render_spring_service(velocityContext);
        render_sql_schema(velocityContext);
        render_sqlmap_table(velocityContext);
        render_service(velocityContext);
        render_queryobject(velocityContext);
        render_dataobject(velocityContext);
        render_dao(velocityContext);

        mkTestBaseDirAndEmptyFile();
        render_test_log4j(velocityContext);
        render_test_prepare(velocityContext);
        render_test_class(velocityContext);
        render_test_wiki(velocityContext);
    }

    private void render_spring_dal(VelocityContext velocityContext) throws Exception {
        String vmContent = getVmContent(PREFIX + "spring/biz/spring_generateTmpl_dal.xml.vm");
        String destPath = String.format("%s/%s/src/main/resources/META-INF/spring/biz/spring_%s_dal.xml", pwd,
                                        finalPackageName, velocityContext.get("shortSchemaName"));
        rendering(velocityContext, vmContent, destPath);
    }

    private void render_spring_service(VelocityContext velocityContext) throws Exception {
        String vmContent = getVmContent(PREFIX + "spring/biz/spring_generateTmpl_service.xml.vm");
        String destPath = String.format("%s/%s/src/main/resources/META-INF/spring/biz/spring_%s_service.xml", pwd,
                                        finalPackageName, velocityContext.get("shortSchemaName"));
        rendering(velocityContext, vmContent, destPath);
    }

    private void render_sql_schema(VelocityContext velocityContext) throws Exception {
        String vmContent = getVmContent(PREFIX + "ibatis/sql-map-generateTmpl.xml.vm");
        String destPath = String.format("%s/%s/src/main/resources/META-INF/ibatis/sql-map-%s.xml", pwd,
                                        finalPackageName, velocityContext.get("shortSchemaName"));
        rendering(velocityContext, vmContent, destPath);
    }

    private void render_sqlmap_table(VelocityContext velocityContext) throws Exception {
        for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
            velocityContext.put("tableMeta", tableMeta);
            velocityContext.put("tableColumnMetaList", databaseMeta.getTableColumnMetaMap().get(tableMeta.getTable()));
            String vmContent = getVmContent(PREFIX + "ibatis/sql/generateTmpl/sqlmap-generateTmpl.xml.vm");
            String destPath = String.format("%s/%s/src/main/resources/META-INF/ibatis/sql/%s/sqlmap-%s.xml", pwd,
                                            finalPackageName, shortSchemaName, tableMeta.getTable());
            rendering(velocityContext, vmContent, destPath);
        }
    }

    private void render_service(VelocityContext velocityContext) throws Exception {
        // ServiceLocator
        String vmContent = getVmContent(PREFIX + "service/GenerateTmplServiceLocator.java.vm");
        String destPath = String.format("%s/%s/src/main/java/%s/service/%sLocator.java", pwd, finalPackageName,
                                        finalPackagePath, serviceName);
        rendering(velocityContext, vmContent, destPath);
        if (databaseMeta.getIsSimple()) {
            // Service Impl
            vmContent = getVmContent(PREFIX + "service/GenerateTmplService.java.vm");
            destPath = String.format("%s/%s/src/main/java/%s/service/%s.java", pwd, finalPackageName, finalPackagePath,
                                     serviceName);
            rendering(velocityContext, vmContent, destPath);
        } else {
            // Service interfaces
            vmContent = getVmContent(PREFIX + "service/interfaces/GenerateTmplService.java.vm");
            destPath = String.format("%s/%s/src/main/java/%s/service/interfaces/%s.java", pwd, finalPackageName,
                                     finalPackagePath, serviceName);
            rendering(velocityContext, vmContent, destPath);
            // Service Impl
            vmContent = getVmContent(PREFIX + "service/impl/GenerateTmplServiceImpl.java.vm");
            destPath = String.format("%s/%s/src/main/java/%s/service/impl/%sImpl.java", pwd, finalPackageName,
                                     finalPackagePath, serviceName);
            rendering(velocityContext, vmContent, destPath);
        }
    }

    private void render_queryobject(VelocityContext velocityContext) throws Exception {
        for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
            velocityContext.put("tableMeta", tableMeta);
            velocityContext.put("tableColumnMetaList", databaseMeta.getTableColumnMetaMap().get(tableMeta.getTable()));
            for (String action : new String[] { "Query", "Updater" }) {
                String vmContent = getVmContent(PREFIX + "queryobject/GenerateTmpl" + action + ".java.vm");
                String destPath = String.format("%s/%s/src/main/java/%s/queryobject/%s%s.java", pwd, finalPackageName,
                                                finalPackagePath, tableMeta.getDoClassName(), action);
                rendering(velocityContext, vmContent, destPath);
            }
        }
    }

    private void render_dataobject(VelocityContext velocityContext) throws Exception {
        for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
            velocityContext.put("tableMeta", tableMeta);
            velocityContext.put("tableColumnMetaList", databaseMeta.getTableColumnMetaMap().get(tableMeta.getTable()));
            String vmContent = getVmContent(PREFIX + "dataobject/GenerateTmplDO.java.vm");
            String destPath = String.format("%s/%s/src/main/java/%s/dataobject/%sDO.java", pwd, finalPackageName,
                                            finalPackagePath, tableMeta.getDoClassName());
            rendering(velocityContext, vmContent, destPath);
        }

    }

    private void render_dao(VelocityContext velocityContext) throws Exception {
        for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
            String vmContent = getVmContent(PREFIX + "dao/interfaces/GenerateTmplDao.java.vm");
            velocityContext.put("tableMeta", tableMeta);
            String destPath = String.format("%s/%s/src/main/java/%s/dao/interfaces/%sDao.java", pwd, finalPackageName,
                                            finalPackagePath, tableMeta.getDoClassName());
            rendering(velocityContext, vmContent, destPath);
            vmContent = getVmContent(PREFIX + "dao/impl/GenerateTmplDaoImpl.java.vm");
            destPath = String.format("%s/%s/src/main/java/%s/dao/impl/%sDaoImpl.java", pwd, finalPackageName,
                                     finalPackagePath, tableMeta.getDoClassName());
            rendering(velocityContext, vmContent, destPath);
        }
    }

    private void render_pom_xml(VelocityContext velocityContext) throws Exception {
        String vmContent = getVmContent(PREFIX + "pom.xml.vm");
        String destPath = String.format("%s/%s/%s", pwd, finalPackageName, "pom.xml");
        rendering(velocityContext, vmContent, destPath);
    }

    private void render_test_prepare(VelocityContext velocityContext) throws Exception {
        for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
            velocityContext.put("tableMeta", tableMeta);
            String testPrepareVmContent = getVmContent(PREFIX + "test/prepare.wiki.vm");
            velocityContext.put("prepareWiki", databaseMeta.getEmptyBasePrepareWiki(tableMeta.getTable()));
            String destPath = String.format("%s/%s/src/test/resources/%s/dao/%sDaoTestData/prepare.wiki", pwd,
                                            finalPackageName, finalPackagePath, tableMeta.getDoClassName());
            rendering(velocityContext, testPrepareVmContent, destPath);
        }
    }

    private void render_test_wiki(VelocityContext velocityContext) throws Exception {
        for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
            velocityContext.put("tableMeta", tableMeta);
            String testPrepareVmContent = getVmContent(PREFIX + "test/method_prepare.wiki.vm");
            String testResultVmContent = getVmContent(PREFIX + "test/method_result.wiki.vm");
            for (String testMethod : testMethods) {
                velocityContext.put("prepareWiki", getBasePrepareWiki(tableMeta.getTable()));
                velocityContext.put("resultWiki", getResultWiki(tableMeta.getTable(), testMethod));
                velocityContext.put("tableColumnMetaList",
                                    databaseMeta.getTableColumnMetaMap().get(tableMeta.getTable()));
                String destPath = String.format("%s/%s/src/test/resources/%s/dao/%sDaoTestData/%s_prepare.wiki", pwd,
                                                finalPackageName, finalPackagePath, tableMeta.getDoClassName(),
                                                testMethod);
                rendering(velocityContext, testPrepareVmContent, destPath);
                destPath = String.format("%s/%s/src/test/resources/%s/dao/%sDaoTestData/%s_result.wiki", pwd,
                                         finalPackageName, finalPackagePath, tableMeta.getDoClassName(), testMethod);
                rendering(velocityContext, testResultVmContent, destPath);
            }
        }
    }

    private String getResultWiki(String table, String testMethod) {
        return databaseMeta.getResultWiki(table, testMethod);
    }

    private String getBasePrepareWiki(String table) {
        return databaseMeta.getBasePrepareWiki(table);
    }

    private void render_test_class(VelocityContext velocityContext) throws Exception {
        String vmContent = getVmContent(PREFIX + "test/ServiceTest.java.vm");
        String destPath = String.format("%s/%s/src/test/java/%s/service/%sTest.java", pwd, finalPackageName,
                                        finalPackagePath, serviceName);
        rendering(velocityContext, vmContent, destPath);

        vmContent = getVmContent(PREFIX + "test/DaoTest.java.vm");
        for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
            velocityContext.put("tableMeta", tableMeta);
            velocityContext.put("tableColumnMetaList", databaseMeta.getTableColumnMetaMap().get(tableMeta.getTable()));
            destPath = String.format("%s/%s/src/test/java/%s/dao/%sDaoTest.java", pwd, finalPackageName,
                                     finalPackagePath, tableMeta.getDoClassName());
            rendering(velocityContext, vmContent, destPath);
        }
    }

    private void render_test_log4j(VelocityContext velocityContext) throws Exception {
        String vmContent = getVmContent(PREFIX + "test/log4j.xml.vm");
        String destPath = String.format("%s/%s/src/test/resources/log4j.xml", pwd, finalPackageName);
        rendering(velocityContext, vmContent, destPath);
    }

    private String getVmContent(String srcPath) throws Exception {
        return IOUtils.toString(ResourcesTools.getResourceAsStream(srcPath, DaoTemplateRender.class), "utf-8");
    }

    private void mkTestBaseDirAndEmptyFile() throws Exception {
        String projectPath = String.format("%s/%s", pwd, finalPackageName);
        for (String filePath : projectTestPathTempl) {
            filePath = getRealFilePath(filePath);
            if (!filePath.startsWith("L")) {
                String fullFilePath = String.format("%s/%s", projectPath, filePath);
                mkdirOrCreateEmptyFile(fullFilePath);
            } else {
                filePath = filePath.substring(1);
                createTestClassAndWikiFile(projectPath, filePath);
            }
        }
    }

    private void mkBaseDirAndEmptyFile() throws Exception {
        String projectPath = String.format("%s/%s", pwd, finalPackageName);
        File finalPackageNameFile = new File(projectPath);
        if (finalPackageNameFile.exists()) {
            String isRm = prompter.prompt(String.format("%s already exists, do you want to rm -rf %s, yes/no? choose no will exit",
                                                        projectPath, projectPath));
            isRm = isRm.trim();
            if (StringUtils.equalsIgnoreCase(isRm, "no")) {
                System.exit(0);
            }
            FileUtils.deleteDirectory(finalPackageNameFile);
        }
        FileUtils.mkdir(projectPath);
        for (String filePath : projectPathTempl) {
            filePath = getRealFilePath(filePath);
            if (!filePath.startsWith("L")) {
                String fullFilePath = String.format("%s/%s", projectPath, filePath);
                mkdirOrCreateEmptyFile(fullFilePath);
            } else {
                filePath = filePath.substring(1);
                createMultiEmptyFile(projectPath, filePath);
            }
        }
    }

    private void createMultiEmptyFile(String projectPath, String filePath) throws Exception {
        int indexOfTableName = filePath.indexOf("${tableName}");
        if (indexOfTableName != -1) {
            for (String tableName : databaseMeta.getTables()) {
                String tablePath = placeHolderReplace(filePath, "tableName", tableName);
                mkBasenameAndFile(projectPath, tablePath);
            }
            return;
        }
        int indexOfDoClassName = filePath.indexOf("${doClassName}");
        if (indexOfDoClassName != -1) {
            for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
                String dOClassPath = placeHolderReplace(filePath, "doClassName", tableMeta.getDoClassName());
                mkBasenameAndFile(projectPath, dOClassPath);
            }
            return;
        }
    }

    private void createTestClassAndWikiFile(String projectPath, String filePath) throws Exception {
        int indexOfTestMethod = filePath.indexOf("${testMethod}");
        int indexOfDoClassName = filePath.indexOf("${doClassName}");
        if (indexOfTestMethod != -1 && indexOfDoClassName != -1) {
            for (String testMethod : testMethods) {
                for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
                    String testWikiPath = filePath;
                    testWikiPath = placeHolderReplace(testWikiPath, "doClassName", tableMeta.getDoClassName());
                    testWikiPath = placeHolderReplace(testWikiPath, "testMethod", testMethod);
                    mkBasenameAndFile(projectPath, testWikiPath);
                }
            }
            return;
        }
        if (indexOfDoClassName != -1) {
            for (TableMeta tableMeta : databaseMeta.getTableMetaList()) {
                String dOClassPath = placeHolderReplace(filePath, "doClassName", tableMeta.getDoClassName());
                mkBasenameAndFile(projectPath, dOClassPath);
            }
            return;
        }
    }

    private void mkBasenameAndFile(String projectPath, String filePath) throws IOException {
        String fullFilePath = String.format("%s/%s", projectPath, filePath);
        String dirname = FileUtils.dirname(fullFilePath);
        new File(dirname).mkdirs();
        new File(fullFilePath).createNewFile();
    }

    private void mkdirOrCreateEmptyFile(String fullFilePath) throws IOException {
        File file = new File(fullFilePath);
        if (fullFilePath.endsWith(".xml") || fullFilePath.endsWith(".java") || fullFilePath.endsWith(".wiki")) {
            final String dirname = FileUtils.dirname(fullFilePath);
            new File(dirname).mkdirs();
            file.createNewFile();
        } else {
            file.mkdirs();
        }
    }

    private String getRealFilePath(String filePath) {
        for (int i = 0; i < placeholder.length; i++) {
            filePath = placeHolderReplace(filePath, placeholder[i], realVariable[i]);
        }
        return filePath;
    }

    private String placeHolderReplace(String filePath, String from, String to) {
        filePath = filePath.replace(String.format("${%s}", from), to);
        return filePath;
    }

    private synchronized Map<String, Object> getContextMap() {
        if (contextMap != null) {
            return contextMap;
        }
        String serviceNameLowerCase = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
        contextMap = new HashMap<String, Object>();
        contextMap.put("serviceName", serviceName);
        contextMap.put("serviceNameLowerCase", serviceNameLowerCase);
        contextMap.put("basePackage", basePackage);
        contextMap.put("artifactId", finalPackageName);
        contextMap.put("pwd", pwd);
        contextMap.put("finalPackageName", finalPackageName);
        contextMap.put("finalPackagePath", finalPackagePath);
        contextMap.put("shortSchemaName", shortSchemaName);
        contextMap.put("tables", databaseMeta.getTables());
        contextMap.put("isMysql", databaseMeta.getIsMysql());
        contextMap.put("isOracle", databaseMeta.getIsOracle());
        contextMap.put("isSimple", databaseMeta.getIsSimple());
        contextMap.put("tableMetaList", databaseMeta.getTableMetaList());
        contextMap.put("tableMetaMap", databaseMeta.getTableMetaMap());
        contextMap.put("tableColumnMetaMap", databaseMeta.getTableColumnMetaMap());
        return contextMap;
    }

    private VelocityContext getVelocityContext() throws Exception {
        Velocity.init();
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, Object> entry : getContextMap().entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
        return context;
    }

    private void rendering(VelocityContext velocityContext, String vmContent, String destPath) throws Exception {
        log.info(String.format("--- velocity rendering %s ---", destPath));
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(destPath));
            StringWriter sw = new StringWriter();
            Velocity.evaluate(velocityContext, sw, "rendering", vmContent);
            IOUtils.write(sw.toString(), fos, "utf-8");
        } catch (Throwable t) {
            String errorMsg = String.format("velocity rendering vm %s failed", destPath);
            log.error(errorMsg, t);
            throw new RuntimeException(errorMsg);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }
    }
}
