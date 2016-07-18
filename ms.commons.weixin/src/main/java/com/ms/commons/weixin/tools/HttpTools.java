package com.ms.commons.weixin.tools;

import java.io.*;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;

public class HttpTools {

    private static final String                       EMPTY             = "";
    private static final String                       UTF_8             = "utf-8";
    private static MultiThreadedHttpConnectionManager connectionManager = null;
    private static HttpClient                         client            = null;

    static {
        connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setConnectionTimeout(10000);
        params.setSoTimeout(20000);
        params.setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, 1000);
        params.setMaxTotalConnections(10000);
        connectionManager.setParams(params);
        client = new HttpClient(connectionManager);
        client.getParams().setParameter("http.protocol.max-redirects", 3);
    }

    public static String get(String url) {
        HttpMethod method = new GetMethod(url);
        return getResponseBodyAsString(method, null, null);
    }

    public static String post(String url) {
        return post(url, null);
    }

    public static String post(String url, Map<String, String> params) {
        PostMethod method = new PostMethod(url);
        if (params != null && !params.isEmpty()) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                String value = params.get(key);
                if (value != null) {
                    method.addParameter(key, value);
                }
            }
        }
        return getResponseBodyAsString(method, null, null);
    }

    /**
     * 把内容做为请求体发送出去
     * 
     * @param url
     * @return
     */
    public static String postBody(String url, String body) {
        return send(url, body);
    }

    /**
     * @param url
     * @param body
     * @return
     */
    public static String send(String url, String body) {
        PostMethod method = new PostMethod(url);
        method.getParams().setContentCharset(UTF_8);
        RequestEntity entity = new StringRequestEntity(body);
        method.setRequestEntity(entity);
        return getResponseBodyAsString(method, null, null);
    }

    // private static File caFile = new File(
    // HttpTools.class.getResource("/META-INF/pfx/1218882901_20140425151818.pfx").getFile());
    //
    // private static File certFile;
    // private static String certPasswd = null;
    // private static final String JKS_CA_FILENAME = "tenpay_cacert.jks";
    // private static final String JKS_CA_ALIAS = "tenpay";
    // private static final String JKS_CA_PASSWORD = "";
    //
    // public static String send4HTTPS(String url, String body) {
    // // ca目录
    // String caPath = caFile.getParent();
    // File jksCAFile = new File(caPath + "/" + JKS_CA_FILENAME);
    // if (!jksCAFile.isFile()) {
    // X509Certificate cert = (X509Certificate) HttpClientUtil.getCertificate(caFile);
    // FileOutputStream out = new FileOutputStream(jksCAFile);
    // // store jks file
    // HttpClientUtil.storeCACert(cert, JKS_CA_ALIAS, JKS_CA_PASSWORD, out);
    // out.close();
    // }
    // FileInputStream trustStream = new FileInputStream(jksCAFile);
    // FileInputStream keyStream = new FileInputStream(certFile);
    //
    // SSLContext sslContext = HttpClientUtil.getSSLContext(trustStream, JKS_CA_PASSWORD, keyStream, certPasswd);
    //
    // // 关闭流
    // keyStream.close();
    // trustStream.close();
    // httpsPostMethod(url, body.getBytes(), sslContext);
    // // if ("POST".equals(this.method.toUpperCase())) {
    // // String url = HttpClientUtil.getURL(this.reqContent);
    // // String queryString = HttpClientUtil.getQueryString(this.reqContent);
    // // byte[] postData = queryString.getBytes(this.charset);
    // //
    // // this.httpsPostMethod(url, postData, sslContext);
    // //
    // // return;
    // // }
    //
    // this.httpsGetMethod(this.reqContent, sslContext);
    // }
    //
    // protected static void httpsPostMethod(String url, byte[] postData, SSLContext sslContext) throws IOException {
    //
    // SSLSocketFactory sf = sslContext.getSocketFactory();
    //
    // HttpsURLConnection conn = HttpClientUtil.getHttpsURLConnection(url);
    //
    // conn.setSSLSocketFactory(sf);
    // this.doPost(conn, postData);
    //
    // }

    public static String getResponseBodyAsString(HttpMethod method, Integer tryTimes, Integer soTimeoutMill) {
        if (tryTimes == null) {
            tryTimes = 1;
        }
        if (soTimeoutMill == null) {
            soTimeoutMill = 20000;
        }
        method.getParams().setSoTimeout(soTimeoutMill);
        method.getParams().setContentCharset(UTF_8);
        InputStream httpInputStream = null;
        for (int i = 0; i < tryTimes; i++) {
            try {
                int responseCode = client.executeMethod(method);
                if (responseCode == HttpStatus.SC_OK || responseCode == HttpStatus.SC_MOVED_PERMANENTLY
                    || responseCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    return method.getResponseBodyAsString();
                }
                return EMPTY;
            } catch (Exception e) {
            } finally {
                IOUtils.closeQuietly(httpInputStream);
                method.releaseConnection();
            }
        }
        return EMPTY;
    }

    public static String upload(String url, File file) {
        PostMethod postMethod = new PostMethod(url);
        postMethod.getParams().setSoTimeout(50000);
        try {
            // FilePart：用来上传文件的类
            FilePart fp = new FilePart("filedata", file);
            Part[] parts = { fp };
            // 对于MIME类型的请求，httpclient建议全用MulitPartRequestEntity进行包装
            MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
            postMethod.setRequestEntity(mre);
            int status = client.executeMethod(postMethod);
            String result;
            if (status == HttpStatus.SC_OK) {
                System.out.println("success");
                result = postMethod.getResponseBodyAsString();
            } else {
                System.out.println("fail");
                result = EMPTY;
            }
            System.out.println("result : " + result);
            // String result = new String(responseBody, "utf-8");
            return result;
        } catch (Exception e) {
        } finally {
            // 释放连接
            postMethod.releaseConnection();
        }
        return EMPTY;
    }

    public static String download(String url, File saveFile) {
        GetMethod method = new GetMethod(url);
        method.getParams().setSoTimeout(50000);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(saveFile);
            int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                Header responseHeader = method.getResponseHeader("Content-disposition");
                // 文件下载
                if (responseHeader != null) {
                    byte[] responseBody = method.getResponseBody();
                    fileOutputStream.write(responseBody);
                }
                // 错误
                else {
                    return method.getResponseBodyAsString();
                }
            } else {
                return EMPTY;
            }
        } catch (FileNotFoundException e1) {
        } catch (HttpException e) {
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
            // 释放连接
            method.releaseConnection();
        }
        return EMPTY;
    }
}
