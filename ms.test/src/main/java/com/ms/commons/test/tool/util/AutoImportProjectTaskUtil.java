/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.tool.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import com.ms.commons.test.common.FileUtil;
import com.ms.commons.test.common.task.Task;

/**
 * auto import project
 * 
 * @author zxc Apr 14, 2013 12:19:03 AM
 */
public class AutoImportProjectTaskUtil {

    private static Set<String> importList = new HashSet<String>(Arrays.asList("test/junit", "test/jmockit",
                                                                              "ajax/json", "jakarta/poi",
                                                                              "sourceforge/spring/mock",
                                                                              "alibaba/intl/test", /*************/
                                                                              "alibaba/intl/commons/framework",
                                                                              "alibaba/intl/biz/datasource",
                                                                              "alibaba/intl/commons/share"));

    @SuppressWarnings({ "unchecked", "deprecation" })
    public static Task wrapAutoImportTask(final File project, final Task oldTask) {
        InputStream fis = null;
        try {
            fis = new BufferedInputStream(project.toURL().openStream());
            SAXBuilder b = new SAXBuilder();
            Document document = b.build(fis);

            List<Element> elements = XPath.selectNodes(document, "/project/build/dependencies/include");

            List<String> addList = new ArrayList<String>(importList);
            if (elements != null) {
                for (Element ele : elements) {
                    String uri = ele.getAttribute("uri").getValue().trim().toLowerCase();
                    if (importList.contains(uri)) {
                        addList.remove(uri);
                    }
                }
            }

            if (addList.size() > 0) {
                System.err.println("Add projects:" + addList);

                List<Element> testElements = XPath.selectNodes(document, "/project/build[@profile='TEST']/dependencies");
                Element testEle;
                if ((testElements == null) || (testElements.size() == 0)) {
                    Element buildEle = new Element("build");
                    buildEle.setAttribute("profile", "TEST");

                    Element filesetsEle = new Element("filesets");
                    filesetsEle.setAttribute("name", "java.resdirs");
                    Element excludeEle = new Element("exclude");
                    excludeEle.setAttribute("fileset", "java.configdir");
                    filesetsEle.addContent(excludeEle);

                    testEle = new Element("dependencies");

                    buildEle.addContent(Arrays.asList(filesetsEle, testEle));

                    ((Element) XPath.selectNodes(document, "/project").get(0)).addContent(buildEle);
                } else {
                    testEle = testElements.get(0);
                }

                for (String add : addList) {
                    Element e = new Element("include");
                    e.setAttribute("uri", add);
                    testEle.addContent(e);
                }

                String newF = project.getAbsolutePath() + ".new";

                XMLOutputter xmlOutputter = new XMLOutputter();
                Writer writer = new BufferedWriter(new FileWriter(newF));
                xmlOutputter.output(document, writer);
                writer.flush();
                FileUtil.closeCloseAbleQuitly(writer);

                final File newFile = new File(newF);
                return new Task() {

                    public void finish() {
                        boolean hasError = false;
                        File backUpFile = new File(project.getAbsoluteFile() + ".backup");
                        try {
                            FileUtils.copyFile(project, backUpFile);
                            FileUtils.copyFile(newFile, project);

                            oldTask.finish();
                        } catch (Exception e) {
                            hasError = true;
                            e.printStackTrace();
                        } finally {
                            try {
                                FileUtils.copyFile(backUpFile, project);
                            } catch (IOException e) {
                                hasError = true;
                                e.printStackTrace();
                            }
                            newFile.delete();
                            backUpFile.delete();
                        }
                        if (hasError) {
                            System.exit(-1);
                        }
                    }
                };
            }

            return oldTask;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            FileUtil.closeCloseAbleQuitly(fis);
        }
    }
}
