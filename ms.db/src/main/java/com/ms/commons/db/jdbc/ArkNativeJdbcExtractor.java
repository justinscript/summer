/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.db.jdbc;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractorAdapter;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 5:06:02 PM
 */
@SuppressWarnings("rawtypes")
public class ArkNativeJdbcExtractor extends NativeJdbcExtractorAdapter {

    // 当前实例
    private static ArkNativeJdbcExtractor currentInst      = null;

    // 日志
    private ExpandLogger                  log              = LoggerFactoryWrapper.getLogger(ArkNativeJdbcExtractor.class);

    //
    private NativeJdbcExtractor           parent           = null;

    // 候选的列表(好像这个不存在spring会挂)
    private List                          candidateParents = null;

    /**
     * 构造函数
     */
    public ArkNativeJdbcExtractor() {
        currentInst = this;
    }

    public NativeJdbcExtractor getParent() {
        return parent;
    }

    public void setParent(NativeJdbcExtractor parent) {
        this.parent = parent;
    }

    /**
     * 给出一个候选的NativeJdbcExtractor的列表，找到第一个存在的就实例化 List中存放的是类的全路径名
     * 
     * @param parentList
     */
    public void setCandidateParents(List parentList) {
        candidateParents = parentList;
        if ((parentList == null) || (parentList.size() == 0)) {
            throw new RuntimeException("Candidate Parents can not be null ..");
        } else {
            // 找到第一个合适的
            for (int i = 0; i < parentList.size(); i++) {
                String classPath = (String) parentList.get(i);

                try {
                    Class parentClass = Class.forName(classPath);
                    parent = (NativeJdbcExtractor) parentClass.newInstance();
                    break; // 找到了就跳出循环
                } catch (Exception ce) {
                    log.error(" Can not load " + classPath + " Exception : " + ce.toString());
                }
            }
            // 没有找到合适的，就抛出异常
            if (parent == null) {
                throw new RuntimeException(" No suitble NativeJdbcExtractor ..");
            }
        }
    }

    public List getCandidateParents() {
        return candidateParents;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor#getNativeConnection(java.sql.Connection)
     */
    public Connection getNativeConnection(Connection conToUse) throws SQLException {
        Connection con = parent.getNativeConnection(conToUse);

        if (isAlibabaJdbcImpl(con.getClass().getName())) {
            try {
                Method m = con.getClass().getMethod("getNativeConnection");

                return (Connection) m.invoke(con);
            } catch (Exception e) {
                throw new RuntimeException("ArkNativeJdbcExtractor extractor failed.", e);
            }
        }

        return con;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor#getNativeConnectionFromStatement(java.sql.Statement
     * )
     */
    public Connection getNativeConnectionFromStatement(Statement stmt) throws SQLException {
        return getNativeConnection(stmt.getConnection());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor#getNativeStatement(java.sql.Statement)
     */
    public Statement getNativeStatement(Statement stmtToUse) throws SQLException {
        Statement stmt = parent.getNativeStatement(stmtToUse);

        if (isAlibabaJdbcImpl(stmt.getClass().getName())) {
            try {
                Method m = stmt.getClass().getMethod("getNativeStatement");

                return (Statement) m.invoke(stmt);
            } catch (Exception e) {
                throw new RuntimeException("ArkNativeJdbcExtractor extractor failed.", e);
            }
        }

        return stmt;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor#getNativePreparedStatement(java.sql.PreparedStatement
     * )
     */
    public PreparedStatement getNativePreparedStatement(PreparedStatement psToUse) throws SQLException {
        PreparedStatement ps = parent.getNativePreparedStatement(psToUse);

        if (isAlibabaJdbcImpl(ps.getClass().getName())) {
            try {
                Method m = ps.getClass().getMethod("getNativePreparedStatement");

                return (PreparedStatement) m.invoke(ps);
            } catch (Exception e) {
                throw new RuntimeException("AlibabaNativeJdbc extractor failed.", e);
            }
        }

        return ps;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor#getNativeCallableStatement(java.sql.CallableStatement
     * )
     */
    public CallableStatement getNativeCallableStatement(CallableStatement cs) throws SQLException {
        return parent.getNativeCallableStatement(cs);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor#getNativeResultSet(java.sql.ResultSet)
     */
    public ResultSet getNativeResultSet(ResultSet rs) throws SQLException {
        return parent.getNativeResultSet(rs);
    }

    /**
     * 方法的描述.
     * 
     * @param p
     * @return
     */
    private boolean isAlibabaJdbcImpl(String p) {
        if (log.isDebugEnabled()) {
            log.debug("Check for native alibaba jdbc driver,class = " + p);
        }
        if (p == null) {
            return false;
        }
        if (p.startsWith("com.alibaba")) {
            return true;
        }
        return false;
    }

    public static ArkNativeJdbcExtractor getCurrentInst() {
        return currentInst;
    }
}
