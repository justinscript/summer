/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Element;

import com.ms.commons.test.classloader.util.ClassPathAccessor;
import com.ms.commons.test.common.task.Task;

/**
 * auto import project
 * 
 * @author zxc Apr 14, 2013 12:19:15 AM
 */
public class AutoDeleteProjectTaskUtil {

    public static Task wrapAutoDeleteTask(final File project, final Task oldTask) {

        String userDir = System.getProperty("user.dir");

        List<Element> projectElements = ClassPathAccessor.getElementsByXPath(project, "/project");
        if ((projectElements == null) || (projectElements.size() != 1)) {
            System.err.println("File '" + project + "' format error!");
            System.exit(-1);
        }
        final String projectId = projectElements.get(0).getAttributeValue("id");
        final String projectExtends = projectElements.get(0).getAttributeValue("extends");

        System.out.println("Project id:" + projectId);
        System.out.println("Project extends:" + projectExtends);

        final File baseProject = new File(userDir + File.separator + projectExtends);
        if (!baseProject.exists()) {
            System.err.println("Base file '" + baseProject + "' not found!");
            System.exit(-1);
        }

        List<Element> findProjectIdElements = ClassPathAccessor.getElementsByXPath(baseProject,
                                                                                   "/project/projects/project[@id='"
                                                                                           + projectId + "']");

        if ((findProjectIdElements != null) && (findProjectIdElements.size() > 0)) {
            System.out.println("Find project id in base project file.");
            return new Task() {

                @SuppressWarnings("unchecked")
                public void finish() {
                    boolean hasError = false;
                    File backUpFile = new File(baseProject.getAbsoluteFile() + ".backup");
                    try {
                        FileUtils.copyFile(baseProject, backUpFile);

                        // �����߼���ɾ���Ӧ�У������ͨ��XML�ļ���API������
                        List<String> outLines = new ArrayList<String>();
                        List<String> lines = FileUtils.readLines(baseProject);
                        for (String line : lines) {
                            if (!(line.contains("\"" + projectId + "\""))) {
                                outLines.add(line);
                            }
                        }
                        FileUtils.writeLines(baseProject, outLines);

                        oldTask.finish();
                    } catch (Exception e) {
                        hasError = true;
                        e.printStackTrace();
                    } finally {
                        baseProject.delete();
                        try {
                            FileUtils.copyFile(backUpFile, baseProject);
                        } catch (IOException e) {
                            hasError = true;
                            e.printStackTrace();
                        }
                        backUpFile.delete();
                    }
                    if (hasError) {
                        System.exit(-1);
                    }
                }
            };
        } else {
            System.out.println("Not find project id in base project file.");
        }

        return oldTask;
    }
}
