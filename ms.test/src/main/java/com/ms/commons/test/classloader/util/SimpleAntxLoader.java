/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.classloader.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.ms.commons.security.Base64;
import com.ms.commons.test.common.StringUtil;
import com.ms.commons.test.constants.IntlTestGlobalConstants;
import com.ms.commons.test.runtime.RuntimeUtil;
import com.ms.commons.test.runtime.constant.RuntimeEnvironment;

/**
 * @NotThreadSafe
 * @author zxc Apr 13, 2013 11:08:06 PM
 */
public class SimpleAntxLoader {

    static final String ENCRYPT_KEY               = "intl-test.password";

    static final String ANTX_PROPERTIES           = "antx.properties";
    static final String ANTX_PROPERTIES_DEFAULT   = "default";
    static final String DEFAULT_PASSWORD          = System.getProperty("user.home") + "/.testcase_password";
    static final String DEFAULT_USER_PASSWORD     = System.getProperty("user.home") + "/.testcase_user_password";
    static final String DEFAULT_DIR_USER_PASSWORD = System.getProperty("user.dir") + "/.testcase_user_password";
    static final String CACHE_DIRECTORY           = IntlTestGlobalConstants.TESTCASE_TEMP_DIR_BASE + File.separator
                                                    + "testcase_svn_cache";

    File                antxFile;
    Properties          properties;
    Properties          otherProperties;
    Properties          antxProperties;
    String              password;

    public SimpleAntxLoader(File antxFile) {
        this.antxFile = antxFile;

        this.properties = new Properties();
        setProperties(this.properties, System.getProperties());
        this.otherProperties = new Properties();
        this.antxProperties = this.loadFromAntxFile();

        List<SvnFile> files = this.readHttpPropertiesFiles();

        if (files != null) {
            for (SvnFile file : files) {
                setProperties(this.otherProperties, this.readHttpProperties(file.svnUrl, file.file));
            }
        }
        setProperties(this.properties, this.otherProperties);
        setProperties(this.properties, this.antxProperties);
    }

    public static Properties getAntxProperties(File antxFile) {
        try {
            SimpleAntxLoader sal = new SimpleAntxLoader(antxFile);

            return sal.getProperties();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public Properties getProperties() {
        return this.properties;
    }

    protected void setProperties(Properties dest, Properties src) {
        if (src == null) {
            return;
        }

        for (Object key : src.keySet()) {
            if (String.class == key.getClass()) {
                String strKey = (String) key;
                dest.setProperty(strKey, src.getProperty(strKey));
            }
        }
    }

    protected Properties loadFromAntxFile() {
        Properties p = new Properties();

        InputStream is = null;
        try {
            is = new FileInputStream(antxFile);
            p.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
        }

        return p;
    }

    protected List<String> getSvnUrlList(Properties p) {
        List<String> svnUrlList = new ArrayList<String>();
        String mark = p.getProperty(ANTX_PROPERTIES);
        if (StringUtils.trimToNull(mark) == null) {
            mark = ANTX_PROPERTIES_DEFAULT;
        }
        if (StringUtils.isNotBlank(mark)) {
            String plusMark = ANTX_PROPERTIES + "." + StringUtils.trim(mark);

            for (Object key : p.keySet()) {
                String k = (key == null) ? "" : key.toString();
                if (k.startsWith(plusMark)) {
                    String svnUrl = StringUtils.trimToNull(p.getProperty(k));
                    svnUrlList.add(svnUrl);
                }
            }
        }
        if (!svnUrlList.isEmpty()) {
            System.err.println("Load svn urls: " + svnUrlList);
        }
        return svnUrlList;
    }

    protected String readLineFromConsole(String message) {
        try {
            System.out.print(message);
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Properties readHttpProperties(String svnUrl, String file) {
        Properties p = new Properties();
        StringBuilder content = new StringBuilder();
        int status = this.readHttpResource(svnUrl, "/" + file, content);
        if (status == 200) {
            ByteArrayInputStream bais = new ByteArrayInputStream(content.toString().getBytes());
            try {
                p.load(bais);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    protected List<SvnFile> readHttpPropertiesFiles() {
        List<SvnFile> svnUrlFileList = new ArrayList<SvnFile>();
        List<String> svnUrlList = getSvnUrlList(this.antxProperties);

        for (String svnUrl : svnUrlList) {
            List<SvnFile> fileList = readHttpPropertiesFilesBySvnUrl(svnUrl);
            if (fileList != null) {
                svnUrlFileList.addAll(fileList);
            }
        }

        return svnUrlFileList;
    }

    protected List<SvnFile> readHttpPropertiesFilesBySvnUrl(String svnUrl) {

        if (svnUrl != null) {
            File sf = new File(svnUrl);
            if (sf.exists() && sf.isDirectory()) {
                String[] fs = sf.list();
                List<SvnFile> svnFileList = new ArrayList<SvnFile>();
                for (String f : fs) {
                    if (!(f.startsWith("."))) {
                        svnFileList.add(new SvnFile(svnUrl, f));
                    }
                }
                return svnFileList;
            }
        }

        StringBuilder content = new StringBuilder();
        int status = this.readHttpResource(svnUrl, "", content);
        if (status == -1) {
            return null;
        }

        if (status == 401) {
            // password error, delete password file
            File file = new File(DEFAULT_PASSWORD);
            if (file.exists()) {
                file.delete();
            }
        }

        if (status != 200) {
            throw new RuntimeException("Password or network error, please TRY RUN AGAIN!");
        }
        return getFiles(svnUrl, content.toString());
    }

    protected List<SvnFile> getFiles(String svnUrl, String content) {
        List<SvnFile> files = new ArrayList<SvnFile>();

        Pattern pattern = Pattern.compile(".*file.*name.*href=\"([\\w\\.]+)\".*");
        Pattern pattern2 = Pattern.compile("\\s+<li><a\\s+href=\"([\\w\\.]+)\".*");
        String[] lines = content.split("[\r\n]");
        if (lines != null) {
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if ((matcher != null) && (matcher.matches())) {
                    files.add(new SvnFile(svnUrl, matcher.group(1)));
                }
                Matcher matcher2 = pattern2.matcher(line);
                if ((matcher2 != null) && (matcher2.matches())) {
                    files.add(new SvnFile(svnUrl, matcher2.group(1)));
                }
            }
        }

        return files;
    }

    protected void preparePassword(SvnUrl url) {
        if (this.password != null) {
            return;
        }
        if (url.getPassword() != null) {
            System.err.println("Read password from svn url.");
            this.password = url.getPassword();
            return;
        }
        File encryptPasswordFile = new File(DEFAULT_PASSWORD);
        File unencryptPasswordFile = new File(DEFAULT_USER_PASSWORD);
        File unencryptDirPasswordFile = new File(DEFAULT_DIR_USER_PASSWORD);
        if (unencryptDirPasswordFile.exists() || unencryptPasswordFile.exists() || encryptPasswordFile.exists()) {
            if (unencryptDirPasswordFile.exists() || unencryptPasswordFile.exists()) {
                if (unencryptDirPasswordFile.exists()) {
                    try {
                        System.err.println("Read password from: " + unencryptDirPasswordFile);
                        this.password = FileUtils.readFileToString(unencryptDirPasswordFile).trim();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        System.err.println("Read password from: " + unencryptPasswordFile);
                        this.password = FileUtils.readFileToString(unencryptPasswordFile).trim();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                try {
                    System.err.println("Read password from: " + encryptPasswordFile);
                    this.password = decryptString(FileUtils.readFileToString(encryptPasswordFile));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            if (RuntimeUtil.getRuntime().getEnvironment() == RuntimeEnvironment.Eclipse) {
                this.password = readLineFromConsole("Input your password:");
                try {
                    FileUtils.writeStringToFile(encryptPasswordFile, encryptString(this.password));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                RuntimeException e = new RuntimeException("Please put your password in file: '${user.home}"
                                                          + File.separator + ".testcase_user_password' OR '${user.dir}"
                                                          + File.separator + ".testcase_user_password'");
                if (RuntimeUtil.getRuntime().getEnvironment() == RuntimeEnvironment.AntxTest) {
                    e.printStackTrace();
                    System.exit(-1);
                } else {
                    throw e;
                }
            }
        }
    }

    protected int readCacheFile(String svnUrl, String file, StringBuilder outContent) {

        File cacheDir = new File(CACHE_DIRECTORY);
        if (!(cacheDir.exists() && cacheDir.isDirectory())) {
            cacheDir.mkdirs();
        }

        File fileDate = new File(CACHE_DIRECTORY + "/" + convertSvnUrlToDegist(svnUrl) + file + ".date");
        File fileData = new File(CACHE_DIRECTORY + "/" + convertSvnUrlToDegist(svnUrl) + file + ".data");

        if (!fileDate.exists()) {
            return -1;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date date = dateFormat.parse((FileUtils.readFileToString(fileDate)));
            Date now = dateFormat.parse(dateFormat.format(new Date()));

            if (now.equals(date)) {
                System.out.println("Read file:" + file + " from cache.");
                outContent.append(FileUtils.readFileToString(fileData));
                return 200;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return -1;
    }

    protected void writeCacheFile(String svnUrl, String file, String content) {

        File fileDate = new File(CACHE_DIRECTORY + "/" + convertSvnUrlToDegist(svnUrl) + file + ".date");
        File fileData = new File(CACHE_DIRECTORY + "/" + convertSvnUrlToDegist(svnUrl) + file + ".data");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            if (fileDate.exists()) {
                fileDate.delete();
            }
            if (fileData.exists()) {
                fileData.delete();
            }
            FileUtils.writeStringToFile(fileDate, dateFormat.format(new Date()));
            FileUtils.writeStringToFile(fileData, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // one day cache here
    protected int readHttpResource(String svnUrl, String file, StringBuilder outContent) {

        if ((svnUrl != null) && svnUrl.startsWith("/") && (new File(svnUrl + "/" + file)).exists()) {
            try {
                outContent.append(FileUtils.readFileToString(new File(svnUrl + "/" + file)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return 200;
        }

        SvnUrl url = SvnUrl.parse(svnUrl + file);
        if (url == null) {
            return -1;
        }

        int cacheStatus = readCacheFile(svnUrl, file, outContent);
        if (cacheStatus == 200) {
            return cacheStatus;
        }

        this.preparePassword(url);

        Credentials credentials = new UsernamePasswordCredentials(url.getUserName(), this.password);
        AuthScope authScope = new AuthScope(url.getHost(), 80, AuthScope.ANY_REALM);

        HttpClient client = new HttpClient();
        client.getState().setCredentials(authScope, credentials);

        System.out.println("Read HTTP resource:" + url.getFullUrl());
        client.getParams().setConnectionManagerTimeout(30 * 1000);
        client.getParams().setSoTimeout(30 * 1000);

        HttpMethod httpMethod = new GetMethod(url.getFullUrl());

        int statusCode = 0;
        try {
            statusCode = client.executeMethod(httpMethod);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (statusCode == 200) {
            String content = null;
            try {
                content = httpMethod.getResponseBodyAsString();
                outContent.append(content);
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeCacheFile(svnUrl, file, outContent.toString());
        }

        return statusCode;
    }

    protected static class SvnUrl {

        static final String PATTERN = "http:\\/\\/([^@:]+)(?::([^@]+))?@(([^/]+).*)";

        String              userName;
        String              password;
        String              host;
        String              url;
        String              fullUrl;

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getHost() {
            return host;
        }

        public String getUrl() {
            return url;
        }

        public String getFullUrl() {
            return fullUrl;
        }

        SvnUrl(String userName, String password, String host, String url, String fullUrl) {
            this.userName = userName;
            this.password = password;
            this.host = host;
            this.url = url;
            this.fullUrl = fullUrl;
        }

        static SvnUrl parse(String url) {
            if (url == null) {
                return null;
            }

            Matcher matcher = Pattern.compile(PATTERN).matcher(url);
            if ((matcher == null) || (!matcher.matches())) {

                if (url.toLowerCase().startsWith("http://") && (!url.contains("@"))) {
                    throw new RuntimeException("Bad format url: " + url + ", svn url should contain user name.");
                }

                return null;
            }
            String userName = matcher.group(1);
            String password = matcher.group(2);
            String surl = matcher.group(3);
            String host = matcher.group(4);
            String fullUrl = "http://" + surl;
            return new SvnUrl(userName, password, host, surl, fullUrl);
        }
    }

    protected static class SvnFile {

        String svnUrl;
        String file;

        SvnFile(String svnUrl, String file) {
            this.svnUrl = svnUrl;
            this.file = file;
        }

        public String getSvnUrl() {
            return svnUrl;
        }

        public void setSvnUrl(String svnUrl) {
            this.svnUrl = svnUrl;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }

    private static String convertSvnUrlToDegist(String svnUrl) {
        return StringUtil.digetsToString(svnUrl.getBytes(), "MD5");
    }

    // only for encrypt and decrypt
    private static String encryptString(String src) {
        if (StringUtils.isEmpty(src)) {
            return null;
        }

        return Base64.encode(src.getBytes());
    }

    private static String decryptString(String src) {
        if (StringUtils.isEmpty(src)) {
            return null;
        }

        return new String(Base64.decode(src));
    }
}
