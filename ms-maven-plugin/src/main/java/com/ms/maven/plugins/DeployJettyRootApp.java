package com.ms.maven.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.ms.maven.plugins.cons.Constants;
import com.ms.maven.plugins.tools.FileTools;

/**
 * @goal deployJettyRootApp
 * @phase install
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class DeployJettyRootApp extends AbstractMojo {

    /**
     * @parameter expression="${project.build.finalName}"
     */
    private String projectBuildFinalName;

    /**
     * Base directory of the project.
     * 
     * @parameter expression="${basedir}"
     */
    private String basedir;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String projectBuildDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("Starting DEPLOY JETTY ROOT APP");
        getLog().info("------------------------------------------------------------------------");

        File buildDirectory = new File(projectBuildDirectory);
        File jettySrver = new File(basedir + File.separator + Constants.JETTY_SERVER);
        File webApps = new File(basedir + File.separator + Constants.WEBAPPS);
        File buildWarDir = new File(projectBuildDirectory + File.separator + projectBuildFinalName);
        ensureDirectorysExists(buildDirectory, jettySrver, webApps, buildWarDir);

        File rootWar = new File(basedir + File.separator + Constants.ROOT_WAR);
        File target = new File(basedir + File.separator + Constants.JETTY_SERVER_TARGET);
        FileTools.deleteDirectorys(getLog(), rootWar, target);

        copyLog4jXml(buildWarDir);
        copyDirectory(buildWarDir, rootWar);
    }

    private void copyLog4jXml(File buildWarDir) throws MojoExecutionException {

        File log4jXml = new File(basedir + File.separator + Constants.JETTY_CONF_LOG4JXML);
        ensureDirectorysExists(log4jXml);
        File destFile = new File(buildWarDir.getAbsolutePath() + "/" + Constants.JETTY_SERVER_LOG4JXML);

        getLog().info(String.format("copy log4j.xml from %s to %s", log4jXml.getAbsoluteFile(),
                                    destFile.getAbsoluteFile()));
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileUtils.copyFile(log4jXml, destFile);
        } catch (IOException e) {
            String errorMsg = String.format("copy log4j.xml from %s to %s failed!", log4jXml.getAbsoluteFile(),
                                            destFile.getAbsoluteFile());
            getLog().error(errorMsg);
            throw new MojoExecutionException(errorMsg);
        }

    }

    private void copyDirectory(File buildWarDir, File rootWar) throws MojoExecutionException {
        getLog().info(String.format("copy Directory from %s to %s", buildWarDir.getAbsoluteFile(),
                                    rootWar.getAbsoluteFile()));
        try {
            FileUtils.copyDirectory(buildWarDir, rootWar);
        } catch (IOException e) {
            String errorMsg = String.format("copyDirectory from %s to %s failed!", buildWarDir.getAbsoluteFile(),
                                            rootWar.getAbsoluteFile());
            getLog().error(errorMsg);
            throw new MojoExecutionException(errorMsg);
        }
    }

    private void ensureDirectorysExists(File... directorys) throws MojoExecutionException {
        for (File directory : directorys) {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            if (!directory.exists()) {
                String errorMsg = directory + " do not exists!";
                getLog().error(errorMsg);
                throw new MojoExecutionException(errorMsg);
            }
        }
    }
}
