/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.scombiz.solr.solr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest.Create;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.ms.commons.nisa.service.ConfigServiceLocator;
import com.ms.commons.result.Result;
import com.ms.scombiz.solr.service.SolrServerUnAvailableException;
import com.ms.scombiz.solr.utils.BaseSolrQueryConvert;
import com.ms.scombiz.solr.utils.CounterMonitor;

/**
 * 和Solr交互的客户端
 * 
 * @author zxc Apr 12, 2013 9:34:45 PM
 */
public class SolrClient implements SolrConfig {

    // RFC规范是255个字节，但多数浏览器不要超过2000个，IE8+ 支持2083,搜索引擎对超过2048个字符以上就不索引了。
    private static final int                                   ERROR_STATUS      = 0;
    private static final int                                   MAX_URL_LENGTH    = 200;
    private CounterMonitor                                     counterMonitor    = null;
    private volatile ConcurrentHashMap<String, HttpSolrServer> httpSolrServerMap = new ConcurrentHashMap<String, HttpSolrServer>();
    String                                                     rootSolrServerUrl;
    String                                                     rootIndexDir;

    SolrServer                                                 rootSolrServer;

    /**
     * client的初始化
     * 
     * <pre>
     * 1.从Nisa中获取SolrServer地址,根地址
     * 2.创建一个错误计数器（以便服务端出错后，及时丢弃池化的连接）
     * </pre>
     */
    public void init() {
        rootSolrServerUrl = ConfigServiceLocator.getCongfigService().getKV(KEY_ROOT_SERVER_URL, "");
        rootIndexDir = ConfigServiceLocator.getCongfigService().getKV(KEY_ROOT_INDEX_DIR, "");
        if (StringUtils.isEmpty(rootSolrServerUrl) || StringUtils.isEmpty(rootIndexDir)) {
            throw new RuntimeException("rootSolrServerUrl and rootIndexDir should not empty");
        }
        counterMonitor = new CounterMonitor(0, TimeUnit.SECONDS.toMillis(15));
        rootSolrServer = getRootSolrServer();
    }

    /**
     * solr的查询方法
     * 
     * @param corename 命名空间，用户区分不同的搜索类型
     * @param returnType 返回值的Class类型
     * @param solrQuery 使用{@link SolrQueryConvert}进行转化
     * @return
     */
    public <T> List<T> query(String corename, final Class<T> returnType, final SolrQuery solrQuery) {
        final HttpSolrServer server = getOrCreateSolrServer(corename);
        final List<T> queryResult = new ArrayList<T>();
        exec(new Executor() {

            public Result exec() throws SolrServerException, IOException {
                QueryResponse query = null;
                if (solrQuery.toString().length() > MAX_URL_LENGTH) {
                    query = server.query(solrQuery, SolrRequest.METHOD.POST);
                } else {
                    query = server.query(solrQuery, SolrRequest.METHOD.GET);
                }
                List<T> beans = query.getBeans(returnType);
                if (beans != null) {
                    queryResult.addAll(beans);
                }
                return Result.success();
            }
        });
        return queryResult;
    }

    public <T> boolean addBean(String corename, final T bean) {
        ArrayList<T> beans = new ArrayList<T>();
        beans.add(bean);
        return addBeans(corename, beans);
    }

    /**
     * 索引方法
     */
    public boolean addBeans(String corename, final List<?> beans) {
        final HttpSolrServer server = getOrCreateSolrServer(corename);
        Result result = exec(new Executor() {

            @Override
            public Result exec() throws SolrServerException, IOException {
                UpdateResponse addBeans = server.addBeans(beans);
                server.commit();
                return new Result().setSuccess(addBeans.getStatus() == ERROR_STATUS);
            }
        });
        return result.isSuccess();
    }

    /**
     * 清空所有
     * 
     * @param corename
     * @return
     */
    public boolean delAll(String corename) {
        final HttpSolrServer server = getOrCreateSolrServer(corename);
        Result result = exec(new Executor() {

            @Override
            public Result exec() throws SolrServerException, IOException {
                String query = BaseSolrQueryConvert.toAll().getQuery();
                UpdateResponse deleteByQuery = server.deleteByQuery(query);
                server.commit();
                return new Result().setSuccess(deleteByQuery.getStatus() == ERROR_STATUS);
            }
        });
        return result.isSuccess();

    }

    /**
     * 删除
     */
    public boolean del(String corename, final SolrQuery solrQuery) {
        final HttpSolrServer server = getOrCreateSolrServer(corename);
        final String query = solrQuery.getQuery();
        Result result = exec(new Executor() {

            @Override
            public Result exec() throws SolrServerException, IOException {
                UpdateResponse deleteByQuery = server.deleteByQuery(query);
                server.commit();
                return new Result().setSuccess(deleteByQuery.getStatus() == ERROR_STATUS);
            }
        });
        return result.isSuccess();
    }

    public boolean commit() {
        Result result = exec(new Executor() {

            @Override
            public Result exec() throws SolrServerException, IOException {
                for (HttpSolrServer solrServ : httpSolrServerMap.values()) {
                    UpdateResponse updateResponse = solrServ.commit();
                    if (updateResponse.getStatus() != 0) {
                        return Result.failed();
                    }
                }
                return Result.success();
            }
        });
        return result.isSuccess();
    }

    protected void commitAndNewServer() {
        try {
            this.commit();
        } catch (Throwable t) {
            // pass away
        }
        httpSolrServerMap = new ConcurrentHashMap<String, HttpSolrServer>();

    }

    /**
     * 获取或者创建一个corename的server
     */
    public HttpSolrServer getOrCreateSolrServer(String corename) {
        if (httpSolrServerMap.get(corename) != null) {
            return httpSolrServerMap.get(corename);
        }
        // create
        synchronized (httpSolrServerMap) {
            if (!httpSolrServerMap.contains(corename)) {
                HttpSolrServer solrServer = createSolrServer(corename);
                httpSolrServerMap.putIfAbsent(corename, solrServer);
            }
        }
        return httpSolrServerMap.get(corename);
    }

    private HttpSolrServer createSolrServer(String corename) {
        createDateDirIfNotExisted(corename);
        String solrServerUrl = this.rootSolrServerUrl + corename + File.separator;
        HttpSolrServer solrServer = newSolrServer(solrServerUrl);
        return solrServer;

    }

    private HttpSolrServer newSolrServer(String solrServerUrl) {
        HttpSolrServer solrServer = new HttpSolrServer(solrServerUrl);
        solrServer.setSoTimeout(60000);
        solrServer.setConnectionTimeout(5000);
        solrServer.setDefaultMaxConnectionsPerHost(50);
        solrServer.setMaxTotalConnections(100);
        solrServer.setFollowRedirects(false);
        solrServer.setAllowCompression(false);
        solrServer.setMaxRetries(1);
        solrServer.setParser(new BinaryResponseParser());
        solrServer.setRequestWriter(new BinaryRequestWriter());
        commit();
        return solrServer;
    }

    private void createDateDirIfNotExisted(String corename) {
        boolean solrCoreExist = isDataDirExsited(corename);
        if (!solrCoreExist) {
            Create create = new Create();
            create.setCoreName(corename);
            create.setDataDir(rootIndexDir + File.separator + corename);
            create.setInstanceDir(rootIndexDir + File.separator + corename);
            try {
                rootSolrServer.request(create);
            } catch (SolrServerException e) {
                logger.error("createSolrCore", e);
                throw new RuntimeException("createSolrCore", e);
            } catch (IOException e) {
                logger.error("createSolrCore", e);
                throw new RuntimeException("createSolrCore", e);
            }
            this.commit();
        }
    }

    private SolrServer getRootSolrServer() {
        if (rootSolrServer == null) {
            synchronized (SolrClient.class) {
                if (rootSolrServer == null) {
                    rootSolrServer = newSolrServer(rootSolrServerUrl);
                }
            }
        }
        return rootSolrServer;
    }

    private boolean isDataDirExsited(String namespace) {
        CoreAdminResponse status;
        try {
            status = CoreAdminRequest.getStatus(namespace, rootSolrServer);
        } catch (SolrServerException e) {
            logger.error("isSolrCoreExist", e);
            throw new SolrServerUnAvailableException("isSolrCoreExist", e);
        } catch (IOException e) {
            logger.error("isSolrCoreExist", e);
            throw new SolrServerUnAvailableException("isSolrCoreExist", e);
        }
        return status != null && status.getCoreStatus(namespace).get("instanceDir") != null;
    }

    protected Result exec(Executor executor) {
        try {
            return executor.exec();
        } catch (SolrServerException e) {
            logger.error(e.getMessage(), e);
            counterMonitor.pulse();
            if (counterMonitor.isExceedWithInForOnce()) {
                commitAndNewServer();
            }
            return Result.failed();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return Result.failed();
        }

    }

    private static interface Executor {

        Result exec() throws SolrServerException, IOException;

    }
}
