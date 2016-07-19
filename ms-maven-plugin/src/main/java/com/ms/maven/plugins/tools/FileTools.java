package com.ms.maven.plugins.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class FileTools {

    public static void deleteDirectorys(Log log, File... directorys) throws MojoExecutionException {
        for (File directory : directorys) {
            if (directory.exists()) {
                log.info("delete Directory: " + directory.getAbsolutePath());
                try {
                    FileUtils.deleteDirectory(directory);
                } catch (IOException e) {
                    String errorMsg = "delete " + directory.getAbsolutePath() + " failed!";
                    log.error(errorMsg, e);
                    throw new MojoExecutionException(errorMsg);
                }
            }
        }
    }
}
