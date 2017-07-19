/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.jdbc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 5:19:45 PM
 */
public class TransactionManagerInterceptor implements MethodInterceptor {

    private ExpandLogger logger = LoggerFactoryWrapper.getLogger(TransactionManagerInterceptor.class);

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object invokeResult = invocation.proceed();
        try {
            logTransaction(invocation.getMethod().getName());
        } catch (Exception e) {
            logger.error("", e);
        }
        return invokeResult;
    }

    private void logTransaction(String methodName) {
        if (!isRequireSyslog()) return;

        // Stack<TxnLogObject> stack = SyslogThreadContext.getTxnStack();
        // TxnStatus status = TxnStatus.getTxnStatusByMethodName(methodName);
        //
        // if (status == TxnStatus.BEGIN) {
        // TxnLogObject txn = new TxnLogObject();
        // txn.setThreadStackTrace(SyslogThreadStackUtil.getAlibabaFirstServiceStack());
        // int size = stack.size();
        // if (size > 10) {
        // for (int i = 0; i < size; i++) {
        // SYSLOG.log(stack.pop());
        // }
        // }
        // stack.push(txn);
        //
        // } else if (status == TxnStatus.COMMIT || status == TxnStatus.ROOLBACK) {
        // if (stack.size() > 0) {
        // TxnLogObject txn = (TxnLogObject) stack.pop();
        // if (txn != null && txn.getStep() > 0) {
        // txn.setStatus(status.name());
        // SYSLOG.log(txn);
        // }
        // }
        // } else {
        // if (stack.size() > 0) {
        // TxnLogObject txn = (TxnLogObject) stack.peek();
        // if (txn != null) {
        // txn.setStatus(methodName);
        // }
        // }
        // }
    }

    private boolean isRequireSyslog() {
        // if (SYSLOG.isSyslogOn()) {
        // if (SYSLOG.isEnableClass(SqlLogObject.class)) {
        // return true;
        // } else {
        // if (logger.isDebugEnabled()) {
        // logger.debug("Syslog sql is not turn on !");
        // }
        // return false;
        // }
        // } else {
        // if (logger.isDebugEnabled()) {
        // logger.debug("Syslog is not turn on !");
        // }
        // return false;
        // }
        return true;
    }
}
