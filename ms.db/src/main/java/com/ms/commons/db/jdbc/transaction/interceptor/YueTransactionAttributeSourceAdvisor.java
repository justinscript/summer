/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.db.jdbc.transaction.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.springframework.util.ObjectUtils;

/**
 * Advisor driven by a TransactionAttributeSource, used to exclude a TransactionInterceptor from methods that are
 * non-transactional.
 * <p>
 * Because the AOP framework caches advice calculations, this is normally faster than just letting the
 * TransactionInterceptor run and find out itself that it has no work to do.
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see TransactionInterceptor
 * @see TransactionProxyFactoryBean
 * @author zxc Apr 12, 2013 5:21:08 PM
 */
public class YueTransactionAttributeSourceAdvisor extends AbstractPointcutAdvisor {

    private static final long                        serialVersionUID = 811406466014956475L;

    private YueTransactionInterceptor                transactionInterceptor;

    private final TransactionAttributeSourcePointcut pointcut         = new TransactionAttributeSourcePointcut();

    /**
     * Create a new TransactionAttributeSourceAdvisor.
     */
    public YueTransactionAttributeSourceAdvisor() {
    }

    /**
     * Create a new TransactionAttributeSourceAdvisor.
     * 
     * @param interceptor the transaction interceptor to use for this advisor
     */
    public YueTransactionAttributeSourceAdvisor(YueTransactionInterceptor interceptor) {
        setTransactionInterceptor(interceptor);
    }

    /**
     * Set the transaction interceptor to use for this advisor.
     */
    public void setTransactionInterceptor(YueTransactionInterceptor interceptor) {
        this.transactionInterceptor = interceptor;
    }

    /**
     * Set the {@link ClassFilter} to use for this pointcut. Default is {@link ClassFilter#TRUE}.
     */
    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    public Advice getAdvice() {
        return this.transactionInterceptor;
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

    /**
     * Inner class that implements a Pointcut that matches if the underlying TransactionAttributeSource has an attribute
     * for a given method.
     */
    private class TransactionAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

        private static final long serialVersionUID = 8844901538646750691L;

        private TransactionAttributeSource getTransactionAttributeSource() {
            return (transactionInterceptor != null ? transactionInterceptor.getTransactionAttributeSource() : null);
        }

        @SuppressWarnings("rawtypes")
        public boolean matches(Method method, Class targetClass) {
            TransactionAttributeSource tas = getTransactionAttributeSource();
            return (tas != null && tas.getTransactionAttribute(method, targetClass) != null);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof TransactionAttributeSourcePointcut)) {
                return false;
            }
            TransactionAttributeSourcePointcut otherPc = (TransactionAttributeSourcePointcut) other;
            return ObjectUtils.nullSafeEquals(getTransactionAttributeSource(), otherPc.getTransactionAttributeSource());
        }

        public int hashCode() {
            return TransactionAttributeSourcePointcut.class.hashCode();
        }

        public String toString() {
            return getClass().getName() + ": " + getTransactionAttributeSource();
        }
    }
}
