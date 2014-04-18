/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * @author zxc Apr 13, 2013 11:19:43 PM
 */
public class FileUtil {

    public static void clearAndMakeDirs(String dir) {
        File f = new File(dir);
        if (f.exists()) {
            try {
                FileUtils.deleteDirectory(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        f.mkdirs();
    }

    public static String convertURLToFilePath(URL url) {
        File f = convertURLToFile(url);
        if (f == null) {
            return null;
        }
        try {
            return f.getCanonicalPath();
        } catch (IOException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public static File convertURLToFile(URL url) {
        if (url == null) {
            return null;
        }

        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            try {
                return new File(URLDecoder.decode(url.getPath(), "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                return new File(url.getPath());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static List<URL> convertFileListToUrlList(List<File> fileList) {
        List<URL> urlList = new ArrayList<URL>(fileList.size());
        for (File f : fileList) {
            try {
                urlList.add(f.toURL());
            } catch (MalformedURLException e) {
                throw ExceptionUtil.wrapToRuntimeException(e);
            }
        }
        return urlList;
    }

    public static boolean isFileSame(File f1, File f2) {
        if ((f1 == null) || (f2 == null)) {
            ExceptionUtil.thorwRuntimeException("F1 or F2 is null.");
        }
        if (!f1.exists() || !f2.exists()) {
            ExceptionUtil.thorwRuntimeException("F1 or F2 not exists.");
        }
        if (!f1.isFile() || !f2.isFile()) {
            ExceptionUtil.thorwRuntimeException("F1 or F2 is not file.");
        }
        InputStream is1 = null;
        InputStream is2 = null;
        try {
            is1 = new BufferedInputStream(new FileInputStream(f1));
            is2 = new BufferedInputStream(new FileInputStream(f2));

            return isInputStreamSame(is1, is2);
        } catch (Exception e) {
            ExceptionUtil.thorwRuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is1);
            IOUtils.closeQuietly(is2);
        }
        return true;
    }

    public static boolean isInputStreamSame(InputStream is1, InputStream is2) {
        if ((is1 == null) || (is2 == null)) {
            ExceptionUtil.thorwRuntimeException("IS1 or IS2 is null.");
        }

        try {
            int i1 = is1.read();
            int i2 = is2.read();

            while (true) {
                if (i1 != i2) {
                    return false;
                }
                if (i1 == -1) {
                    break;
                }
                i1 = is1.read();
                i2 = is2.read();
            }
        } catch (Exception e) {
            ExceptionUtil.thorwRuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is1);
            IOUtils.closeQuietly(is2);
        }
        return true;
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
        copyDirectory(srcDir, destDir, true, filter);
    }

    public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate, FileFilter filter)
                                                                                                            throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcDir.exists() == false) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (srcDir.isDirectory() == false) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }
        doCopyDirectory(srcDir, destDir, preserveFileDate, filter);
    }

    private static void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate, FileFilter filter)
                                                                                                               throws IOException {
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (destDir.mkdirs() == false) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
            if (preserveFileDate) {
                destDir.setLastModified(srcDir.lastModified());
            }
        }
        if (destDir.canWrite() == false) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        // recurse
        File[] files = srcDir.listFiles();
        if (files == null) { // null if security restricted
            throw new IOException("Failed to list contents of " + srcDir);
        }
        for (int i = 0; i < files.length; i++) {
            if (filter.accept(files[i])) {
                File copiedFile = new File(destDir, files[i].getName());
                if (files[i].isDirectory()) {
                    doCopyDirectory(files[i], copiedFile, preserveFileDate, filter);
                } else {
                    doCopyFile(files[i], copiedFile, preserveFileDate);
                }
            }
        }
    }

    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream input = new FileInputStream(srcFile);
        try {
            FileOutputStream output = new FileOutputStream(destFile);
            try {
                IOUtils.copy(input, output);
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(input);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static void closeCloseAbleQuitly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // EAT
            }
        }
    }

    public static List<File> listFiles(List<File> list, File file, FileFilter filter) {
        if (file.exists()) {
            if (file.isFile()) {
                if (filter.accept(file)) {
                    if (list != null) {
                        list.add(file);
                    }
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles(filter);
                if (files != null) {
                    for (File f : files) {
                        if (filter.accept(f)) {
                            if (f.isDirectory()) {
                                listFiles(list, f, filter);
                            } else if (f.isFile()) {
                                if (list != null) {
                                    list.add(f);
                                }
                            } else {
                                throw new RuntimeException("Error file type:" + file);
                            }
                        }
                    }
                }
            } else {
                throw new RuntimeException("Error file type:" + file);
            }
        }

        return list;
    }
}
