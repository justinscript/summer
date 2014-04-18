/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.tool.util.AntxEnvUtil;

/**
 * @author zxc Apr 13, 2013 11:42:45 PM
 */
public class Install {

    public static void main(String[] args) {

        System.out.println("-----------------------------------------------------");
        System.out.println("| Generate unit test tool(s).                       |");
        System.out.println("|                                                   |");
        System.out.println("|           Unit Test Team @Alibaba B2B Internation |");
        System.out.println("|           Access our web page: http://goo.gl/8GaW |");
        System.out.println("-----------------------------------------------------");
        System.out.println("");

        if (args.length == 0) {
            System.out.println("Usage:.framework_test_install <cmd>");
            System.out.println("");
            System.exit(-1);
        }

        try {
            String projectRepHome = AntxEnvUtil.getProjectRepHome();
            String antxBinHome = AntxEnvUtil.getAntxBinHome();
            String antxRepHome = AntxEnvUtil.getAntxRepHome();

            File config = new File(System.getProperty("user.dir") + File.separator + args[0] + ".config");

            if (!config.exists()) {
                System.out.println("Cannot find config file for '" + args[0] + "'.");
                System.out.println("");
                System.exit(-1);
            }

            Properties p = new Properties();

            FileInputStream fis = new FileInputStream(config);
            p.load(fis);
            fis.close();

            String cmdName = p.getProperty("command_name");
            String cmdLib = p.getProperty("command_lib");
            String cmdClass = p.getProperty("command_class");
            String cmdTemplate = p.getProperty("command_template");

            File templ = new File(System.getProperty("user.dir") + File.separator + cmdTemplate + ".template");
            if (!templ.exists()) {
                System.out.println("Cannot find template file for '" + args[0] + "'.");
                System.out.println("");
                System.exit(-1);
            }

            String actualCmdLib = cmdLib.replace("%PROJECT_REP%", projectRepHome);
            actualCmdLib = actualCmdLib.replace("%ANTX_REP%", antxRepHome);
            actualCmdLib = actualCmdLib.replace(';', File.pathSeparatorChar);

            String template = readStringFromFile(templ);
            template = template.replace("%LIB%", actualCmdLib);
            template = template.replace("%CLASS%", cmdClass);

            for (Object k : p.keySet().toArray()) {
                if (k != null) {
                    String sk = k.toString();
                    String variant = "variant-";
                    if (sk.startsWith(variant)) {
                        String r = sk.substring(variant.length());
                        template = template.replace(r, p.getProperty(sk));
                    }
                }
            }

            File outCmd = new File(antxBinHome + File.separator + cmdName);
            if (outCmd.exists()) {
                System.err.println("File '" + outCmd + "' exists, remove it.");
                outCmd.delete();
            }
            FileWriter fw = new FileWriter(outCmd);
            fw.write(template);
            fw.close();

            ProcessBuilder cmdAddX = new ProcessBuilder("chmod", "+x", outCmd.getAbsolutePath());
            cmdAddX.start().waitFor();

            System.out.println("Add command '" + outCmd + "' finished.");

            System.out.println("");
            System.exit(0);
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    public static String readStringFromFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean isFirstLine = true;
        BufferedReader br = new BufferedReader(new FileReader(file));
        for (String line; (line = br.readLine()) != null;) {
            if (!isFirstLine) {
                sb.append(System.getProperty("line.separator"));
            }
            sb.append(line);
            isFirstLine = false;
        }
        return sb.toString();
    }
}
