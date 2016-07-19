package com.ms.maven.plugins.tools;

import org.apache.velocity.VelocityContext;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class MavenPropertiesContext {

    public static void put(VelocityContext context, String userHome, String projectBuildFinalName) {
        if (context == null) return;
        context.put("user_home", userHome);
        context.put("project_build_finalName", projectBuildFinalName);
    }
}
