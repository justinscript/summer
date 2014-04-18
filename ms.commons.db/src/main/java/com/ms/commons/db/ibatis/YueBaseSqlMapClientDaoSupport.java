/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.ibatis;

import javax.sql.DataSource;

import org.springframework.dao.support.DaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ms.commons.db.pagination.Pagination;
import com.ms.commons.db.pagination.PaginationList;

/**
 * @author zxc Apr 12, 2013 5:16:23 PM
 */
public class YueBaseSqlMapClientDaoSupport extends DaoSupport {

    // 生成代理对象
    private YueSqlMapClientTemplate sqlMapClientTemplate = null;
    // private AlibabaSqlMapClientTemplate sqlMapClientTemplate = new AlibabaSqlMapClientTemplate();

    private boolean                 externalTemplate     = false;

    /**
     * 构造函数
     */
    public YueBaseSqlMapClientDaoSupport() {
        // 判断是否需要监控(挪到Interceptor里做判断)
        // if(ExecuteUnit.needTrace()){
        // 用动态代理拦截
        sqlMapClientTemplate = (YueSqlMapClientTemplate) new SqlMapClientInterceptor().proxy(new YueSqlMapClientTemplate());
        // } else {
        // sqlMapClientTemplate = new AlibabaSqlMapClientTemplate();
        // }
    }

    /**
     * Set the JDBC DataSource to be used by this DAO. Not required: The SqlMapClient might carry a shared DataSource.
     * 
     * @see #setSqlMapClient
     */
    public final void setDataSource(DataSource dataSource) {
        this.sqlMapClientTemplate.setDataSource(dataSource);
    }

    /**
     * Return the JDBC DataSource used by this DAO.
     */
    public final DataSource getDataSource() {
        return (this.sqlMapClientTemplate != null ? this.sqlMapClientTemplate.getDataSource() : null);
    }

    /**
     * Set the iBATIS Database Layer SqlMapClient to work with. Either this or a "sqlMapClientTemplate" is required.
     * 
     * @see #setSqlMapClientTemplate
     */
    public final void setSqlMapClient(SqlMapClient sqlMapClient) {
        this.sqlMapClientTemplate.setSqlMapClient(sqlMapClient);
    }

    /**
     * Return the iBATIS Database Layer SqlMapClient that this template works with.
     */
    public final SqlMapClient getSqlMapClient() {
        return this.sqlMapClientTemplate.getSqlMapClient();
    }

    /**
     * Set the SqlMapClientTemplate for this DAO explicitly, as an alternative to specifying a SqlMapClient.
     * 
     * @see #setSqlMapClient
     */
    public final void setSqlMapClientTemplate(YueSqlMapClientTemplate sqlMapClientTemplate) {
        if (sqlMapClientTemplate == null) {
            throw new IllegalArgumentException("Cannot set sqlMapClientTemplate to null");
        }
        this.sqlMapClientTemplate = sqlMapClientTemplate;
        this.externalTemplate = true;
    }

    /**
     * Return the SqlMapClientTemplate for this DAO, pre-initialized with the SqlMapClient or set explicitly.
     */
    public final YueSqlMapClientTemplate getSqlMapClientTemplate() {
        return sqlMapClientTemplate;
    }

    protected final void checkDaoConfig() {
        if (!this.externalTemplate) {
            this.sqlMapClientTemplate.afterPropertiesSet();
        }
    }

    /**
     * @param countStatementName 如果countStatementName==null，表示不查询Count()语句
     * @param queryStatementName
     * @param query
     * @return
     */
    @SuppressWarnings("rawtypes")
    public PaginationList queryForPagination(String countStatementName, String queryStatementName, Pagination query) {
        return this.getSqlMapClientTemplate().queryForPagination(countStatementName, queryStatementName, query);
    }
}
