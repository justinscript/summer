/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.ibatis;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ms.commons.db.pagination.Pagination;
import com.ms.commons.db.pagination.PaginationList;

/**
 * @author zxc Apr 12, 2013 5:16:55 PM
 */
public class YueSqlMapClientTemplate extends SqlMapClientTemplate {

    /**
     * 分页查询
     * 
     * @param countStatementName 如果countStatementName==null，表示不查询Count()语句
     * @param queryStatementName 查询记录的StatementName
     * @param query 查询条件（需要设置currentPage和PageSize值，或是startRow和endRow值）
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PaginationList queryForPagination(String countStatementName, String queryStatementName, Pagination query) {
        PaginationList paginationList = new PaginationList(query);
        if (countStatementName != null) {
            Integer obj = (Integer) queryForObject(countStatementName, query);
            int totalCount = obj == null ? 0 : obj;
            query.init(totalCount);

            if (totalCount > 0) {
                List items = queryForList(queryStatementName, query);
                if (items != null) {
                    paginationList.addAll(items);
                }
            }
        } else {
            List items = queryForList(queryStatementName, query);
            if (items != null) {
                paginationList.addAll(items);
            }
        }
        return paginationList;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PaginationList queryForPagination(int count, String queryStatementName, Pagination query) {
        PaginationList paginationList = new PaginationList(query);
        if (count > 0) {
            query.init(count);
            List<?> items = queryForList(queryStatementName, query);
            if (items != null) {
                paginationList.addAll(items);
            }
        } else {
            List items = queryForList(queryStatementName, query);
            if (items != null) {
                paginationList.addAll(items);
            }
        }
        return paginationList;
    }

    /**
     * 神医：以分页的方式查询所有记录 -- 阿里数据库对每次查询的数量有限制，返回结果不能超过10000条记录
     * 
     * @param query 查询封装对象
     * @param countStatementName
     * @param queryStatementName
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PaginationList queryForListSafely(Pagination query, String countStatementName, String queryStatementName) {
        // 计算总记录数
        Integer obj = (Integer) queryForObject(countStatementName, query);
        int totalCount = obj == null ? 0 : obj;
        if (totalCount <= 0) {
            return new PaginationList(query);
        }
        // 设置分页大小
        Integer pageSize = query.getPageSize();
        query.setPageSize(2000);
        PaginationList result = null;
        int i = 0;
        int pageNum = 0;
        do {
            PaginationList tmpResult = queryForPagination(totalCount, queryStatementName, query);
            if (result == null) {
                result = tmpResult;
                result.ensureCapacity(totalCount);
            } else {
                result.addAll(tmpResult);
                pageNum = query.getAllPageNum();
                i++;
                query.setNowPageIndex(i);
            }
        } while (i < pageNum);
        // 恢复
        query.setPageSize(pageSize);
        return result;
    }

    /**
     * 批量插入
     * 
     * @param statementName
     * @param parameterObjects
     * @return
     * @throws DataAccessException
     */
    public Integer batchInsert(final String statementName, final Collection<? extends Object> parameterObjects)
                                                                                                               throws DataAccessException {
        return batchAction(statementName, parameterObjects, new BatchAction() {

            public void doAction(final SqlMapExecutor executor, final String statementName, final Object parameterObject)
                                                                                                                         throws SQLException {
                executor.insert(statementName, parameterObject);
            }
        });
    }

    /**
     * 批量更新
     * 
     * @param statementName
     * @param parameterObjects
     * @return 成功执行的数量
     * @throws DataAccessException
     */
    public Integer batchUpdate(final String statementName, final Collection<? extends Object> parameterObjects)
                                                                                                               throws DataAccessException {
        return batchAction(statementName, parameterObjects, new BatchAction() {

            public void doAction(final SqlMapExecutor executor, final String statementName, final Object parameterObject)
                                                                                                                         throws SQLException {
                executor.update(statementName, parameterObject);
            }
        });
    }

    /**
     * 批量删除
     * 
     * @param statementName
     * @param parameterObjects
     * @return 成功执行的数量
     * @throws DataAccessException
     */
    public Integer batchDelete(final String statementName, final Collection<? extends Object> parameterObjects)
                                                                                                               throws DataAccessException {
        return batchAction(statementName, parameterObjects, new BatchAction() {

            public void doAction(final SqlMapExecutor executor, final String statementName, final Object parameterObject)
                                                                                                                         throws SQLException {
                executor.delete(statementName, parameterObject);
            }
        });
    }

    /**
     * 可以批量指定的动作
     * 
     * @param statementName
     * @param parameterObjects
     * @return 成功执行的数量
     * @throws DataAccessException
     */
    private Integer batchAction(final String statementName, final Collection<? extends Object> parameterObjects,
                                final BatchAction batchAction) throws DataAccessException {
        Object ret = execute(new SqlMapClientCallback() {

            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                if (parameterObjects == null || parameterObjects.isEmpty()) {
                    return new Integer(0);
                } else {
                    executor.startBatch();
                    for (Object parameterObject : parameterObjects) {
                        batchAction.doAction(executor, statementName, parameterObject);
                    }
                    return new Integer(executor.executeBatch());
                }
            }
        });

        //
        if (ret instanceof Integer) {
            return (Integer) ret;
        } else {
            return new Integer(0);
        }
    }

    @Override
    public void delete(String statementName, Object parameterObject, int requiredRowsAffected)
                                                                                              throws DataAccessException {
        super.delete(statementName, parameterObject, requiredRowsAffected);
    }

    @Override
    public int delete(String statementName, Object parameterObject) throws DataAccessException {
        return super.delete(statementName, parameterObject);
    }

    @Override
    public int delete(String statementName) throws DataAccessException {
        return super.delete(statementName);
    }

    @Override
    public Object insert(String statementName, Object parameterObject) throws DataAccessException {
        return super.insert(statementName, parameterObject);
    }

    @Override
    public Object insert(String statementName) throws DataAccessException {
        return super.insert(statementName);
    }

    @Override
    public void update(String statementName, Object parameterObject, int requiredRowsAffected)
                                                                                              throws DataAccessException {
        super.update(statementName, parameterObject, requiredRowsAffected);
    }

    @Override
    public int update(String statementName, Object parameterObject) throws DataAccessException {
        return super.update(statementName, parameterObject);
    }

    @Override
    public int update(String statementName) throws DataAccessException {
        return super.update(statementName);
    }
}

/**
 * 可以批量执行的动作接口
 * 
 * @author bob.panl
 */
interface BatchAction {

    public void doAction(final SqlMapExecutor executor, final String statementName, final Object parameterObject)
                                                                                                                 throws SQLException;
}
