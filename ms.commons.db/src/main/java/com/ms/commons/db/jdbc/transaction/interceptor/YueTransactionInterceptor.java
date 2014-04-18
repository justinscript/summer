/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.jdbc.transaction.interceptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * AOP Alliance MethodInterceptor providing declarative transaction management using the common Spring transaction
 * infrastructure.
 * <p>
 * Derives from the TransactionAspectSupport class. That class contains the necessary calls into Spring's underlying
 * transaction API: subclasses such as this are responsible for calling superclass methods such as
 * <code>createTransactionIfNecessary</code> in the correct order, in the event of normal invocation return or an
 * exception.
 * <p>
 * TransactionInterceptors are thread-safe.
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see YueTransactionProxyFactoryBean
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.transaction.interceptor.TransactionAspectSupport
 * @see org.springframework.transaction.PlatformTransactionManager
 */
public class YueTransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 5156851299519417865L;

    private ExpandLogger      logger           = LoggerFactoryWrapper.getLogger(YueTransactionInterceptor.class);

    // 有事务的方法的执行时间统计
    // TODO:记录时间
    // private static ExecuteUnitStatics transactionExecStatics = null;

    /**
     * Create a new TransactionInterceptor. Transaction manager and transaction attributes still need to be set.
     * 
     * @see #setTransactionManager
     * @see #setTransactionAttributes(java.util.Properties)
     * @see #setTransactionAttributeSource(TransactionAttributeSource)
     */
    public YueTransactionInterceptor() {
        // TODO:记录时间
        // transactionExecStatics =
        // RequestStatics.registStatics("transactionService", new
        // ExecuteUnitStatics(200,100));
    }

    /**
     * Create a new TransactionInterceptor.
     * 
     * @param ptm the transaction manager to perform the actual transaction management
     * @param attributes the transaction attributes in properties format
     * @see #setTransactionManager
     * @see #setTransactionAttributes(java.util.Properties)
     */
    public YueTransactionInterceptor(PlatformTransactionManager ptm, Properties attributes) {
        setTransactionManager(ptm);
        setTransactionAttributes(attributes);
    }

    /**
     * Create a new TransactionInterceptor.
     * 
     * @param ptm the transaction manager to perform the actual transaction management
     * @param tas the attribute source to be used to find transaction attributes
     * @see #setTransactionManager
     * @see #setTransactionAttributeSource(TransactionAttributeSource)
     */
    public YueTransactionInterceptor(PlatformTransactionManager ptm, TransactionAttributeSource tas) {
        setTransactionManager(ptm);
        setTransactionAttributeSource(tas);
    }

    /**
     * 加入对方法的拦截
     * <p>
     * <li>一个没有事务的service 执行时间超过5秒的需要将方法打印出来</li>
     * <li>一个有事务的service执行时间超过2秒的需要打印出来</li>
     * <li>统计service 的执行时间,得出热点.</li>
     * </p>
     * 
     * @param invocation -
     */
    @SuppressWarnings("rawtypes")
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        // Work out the target class: may be <code>null</code>.
        // The TransactionAttributeSource should be passed the target class
        // as well as the method, which may be from an interface.
        Class targetClass = (invocation.getThis() != null ? invocation.getThis().getClass() : null);

        // If the transaction attribute is null, the method is
        // non-transactional.
        final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(invocation.getMethod(),
                                                                                                    targetClass);
        final String joinpointIdentification = methodIdentification(invocation.getMethod());

        if (txAttr == null || !(getTransactionManager() instanceof CallbackPreferringPlatformTransactionManager)) {
            long startTime = System.currentTimeMillis();
            // Standard transaction demarcation with getTransaction and
            // commit/rollback calls.
            TransactionInfo txInfo = createTransactionIfNecessary(txAttr, joinpointIdentification);
            Object retVal = null;
            try {
                // This is an around advice: Invoke the next interceptor in the
                // chain.
                // This will normally result in a target object being invoked.
                retVal = invocation.proceed();
            } catch (Throwable ex) {
                // target invocation exception
                completeTransactionAfterThrowing(txInfo, ex);
                throw ex;
            } finally {
                cleanupTransactionInfo(txInfo);
            }
            commitTransactionAfterReturning(txInfo);

            // 计算执行时间
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 2000) {
                logger.error(joinpointIdentification + " (Trac) used :" + duration);
            }
            // TODO:记录时间
            // transactionExecStatics.complete(joinpointIdentification,
            // duration,false);

            return retVal;
        }

        else {
            // It's a CallbackPreferringPlatformTransactionManager: pass a
            // TransactionCallback in.
            try {
                Object result = ((CallbackPreferringPlatformTransactionManager) getTransactionManager()).execute(txAttr,
                                                                                                                 new TransactionCallback() {

                                                                                                                     public Object doInTransaction(TransactionStatus status) {
                                                                                                                         TransactionInfo txInfo = prepareTransactionInfo(txAttr,
                                                                                                                                                                         joinpointIdentification,
                                                                                                                                                                         status);
                                                                                                                         try {
                                                                                                                             return invocation.proceed();
                                                                                                                         } catch (Throwable ex) {
                                                                                                                             if (txAttr.rollbackOn(ex)) {
                                                                                                                                 // A
                                                                                                                                 // RuntimeException:
                                                                                                                                 // will
                                                                                                                                 // lead
                                                                                                                                 // to
                                                                                                                                 // a
                                                                                                                                 // rollback.
                                                                                                                                 throw new ThrowableHolderException(
                                                                                                                                                                    ex);
                                                                                                                             } else {
                                                                                                                                 // A
                                                                                                                                 // normal
                                                                                                                                 // return
                                                                                                                                 // value:
                                                                                                                                 // will
                                                                                                                                 // lead
                                                                                                                                 // to
                                                                                                                                 // a
                                                                                                                                 // commit.
                                                                                                                                 return new ThrowableHolder(
                                                                                                                                                            ex);
                                                                                                                             }
                                                                                                                         } finally {
                                                                                                                             cleanupTransactionInfo(txInfo);
                                                                                                                         }
                                                                                                                     }
                                                                                                                 });

                // Check result: It might indicate a Throwable to rethrow.
                if (result instanceof ThrowableHolder) {
                    throw ((ThrowableHolder) result).getThrowable();
                } else {
                    return result;
                }
            } catch (ThrowableHolderException ex) {
                throw ex.getThrowable();
            }
        }
    }

    // ---------------------------------------------------------------------
    // Serialization support
    // ---------------------------------------------------------------------

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Rely on default serialization, although this class itself doesn't
        // carry state anyway...
        ois.defaultReadObject();

        // Serialize all relevant superclass fields.
        // Superclass can't implement Serializable because it also serves as
        // base class
        // for AspectJ aspects (which are not allowed to implement
        // Serializable)!
        setTransactionManager((PlatformTransactionManager) ois.readObject());
        setTransactionAttributeSource((TransactionAttributeSource) ois.readObject());
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        // Rely on default serialization, although this class itself doesn't
        // carry state anyway...
        oos.defaultWriteObject();

        // Deserialize superclass fields.
        oos.writeObject(getTransactionManager());
        oos.writeObject(getTransactionAttributeSource());
    }

    /**
     * Internal holder class for a Throwable, used as a return value from a TransactionCallback (to be subsequently
     * unwrapped again).
     */
    private static class ThrowableHolder {

        private final Throwable throwable;

        public ThrowableHolder(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }

    /**
     * Internal holder class for a Throwable, used as a RuntimeException to be thrown from a TransactionCallback (and
     * subsequently unwrapped again).
     */
    private static class ThrowableHolderException extends RuntimeException {

        private static final long serialVersionUID = 8673535821464864137L;
        private final Throwable   throwable;

        public ThrowableHolderException(Throwable throwable) {
            super(throwable.toString());
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public String toString() {
            return this.throwable.toString();
        }
    }
}
