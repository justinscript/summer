/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.jdbc.transaction.interceptor;

import java.util.Properties;

import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.AttributesTransactionAttributeSource;
import org.springframework.transaction.interceptor.MethodMapTransactionAttributeSource;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeEditor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSourceEditor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * Proxy factory bean for simplified declarative transaction handling.
 * <p>
 * This class is a convenient alternative to a standard AOP {@link org.springframework.aop.framework.ProxyFactoryBean}
 * with a separate {@link TransactionInterceptor} definition.
 * <p>
 * This class is intended to cover the <i>typical</i> case of declarative transaction demarcation: namely, wrapping a
 * singleton target object with a transactional proxy, proxying all the interfaces that the target implements.
 * <p>
 * There are three main properties that need to be specified:
 * <ul>
 * <li>"transactionManager": the {@link PlatformTransactionManager} implementation to use (for example, a
 * {@link org.springframework.transaction.jta.JtaTransactionManager} instance)
 * <li>"target": the target object that a transactional proxy should be created for
 * <li>"transactionAttributes": the transaction attributes (for example, propagation behavior and "readOnly" flag) per
 * target method name (or method name pattern)
 * </ul>
 * <p>
 * If the "transactionManager" property is not set explicitly and this {@link FactoryBean} is running in a
 * {@link ListableBeanFactory}, a single matching bean of type {@link PlatformTransactionManager} will be fetched from
 * the {@link BeanFactory}.
 * <p>
 * In contrast to {@link TransactionInterceptor}, the transaction attributes are specified as properties, with method
 * names as keys and transaction attribute descriptors as values. Method names are always applied to the target class.
 * <p>
 * Internally, a {@link TransactionInterceptor} instance is used, but the user of this class does not have to care.
 * Optionally, a method pointcut can be specified to cause conditional invocation of the underlying
 * {@link TransactionInterceptor}.
 * <p>
 * The "preInterceptors" and "postInterceptors" properties can be set to add additional interceptors to the mix, like
 * {@link org.springframework.aop.interceptor.PerformanceMonitorInterceptor} or
 * {@link org.springframework.orm.hibernate3.HibernateInterceptor} / {@link org.springframework.orm.jdo.JdoInterceptor}.
 * <p>
 * <b>HINT:</b> This class is often used with parent / child bean definitions. Typically, you will define the
 * transaction manager and default transaction attributes (for method name patterns) in an abstract parent bean
 * definition, deriving concrete child bean definitions for specific target objects. This reduces the per-bean
 * definition effort to a minimum.
 * 
 * <pre code="class">
 * &lt;bean id="baseTransactionProxy" class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean"
 *     abstract="true"&gt;
 *   &lt;property name="transactionManager" ref="transactionManager"/&gt;
 *   &lt;property name="transactionAttributes"&gt;
 *     &lt;props&gt;
 *       &lt;prop key="insert*"&gt;PROPAGATION_REQUIRED&lt;/prop&gt;
 *       &lt;prop key="update*"&gt;PROPAGATION_REQUIRED&lt;/prop&gt;
 *       &lt;prop key="*"&gt;PROPAGATION_REQUIRED,readOnly&lt;/prop&gt;
 *     &lt;/props&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="myProxy" parent="baseTransactionProxy"&gt;
 *   &lt;property name="target" ref="myTarget"/&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="yourProxy" parent="baseTransactionProxy"&gt;
 *   &lt;property name="target" ref="yourTarget"/&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * @author Juergen Hoeller
 * @author Dmitriy Kopylenko
 * @author Rod Johnson
 * @since 21.08.2003
 * @see #setTransactionManager
 * @see #setTarget
 * @see #setTransactionAttributes
 * @see TransactionInterceptor
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @author zxc Apr 12, 2013 5:14:08 PM
 */
public class YueTransactionProxyFactoryBean extends AbstractSingletonProxyFactoryBean implements FactoryBean, BeanFactoryAware {

    private static final long               serialVersionUID       = 7026892321037132635L;

    private final YueTransactionInterceptor transactionInterceptor = new YueTransactionInterceptor();

    private Pointcut                        pointcut;

    // Cache 起来的对象
    // private Object cachedTarget ;

    // /**
    // * 需要拦截该方法,并做一个缓存。因为基类中没有提供对应对象的获取方法
    // */
    // public void setTarget(Object target) {
    // cachedTarget = target ;
    // super.setTarget(target);
    // }
    //
    // /**
    // * 反馈代理对象并设置到Target 中
    // */
    // public Object getObject() {
    // Object proxy = super.getObject();
    // if(cachedTarget != null && cachedTarget instanceof ProxyAware ){
    // ((ProxyAware)cachedTarget).setProxy(proxy);
    // }
    // return proxy;
    // }

    /**
     * Set the transaction manager. This will perform actual transaction management: This class is just a way of
     * invoking it.
     * 
     * @see TransactionInterceptor#setTransactionManager
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionInterceptor.setTransactionManager(transactionManager);
    }

    /**
     * Set properties with method names as keys and transaction attribute descriptors (parsed via
     * TransactionAttributeEditor) as values: e.g. key = "myMethod", value = "PROPAGATION_REQUIRED,readOnly".
     * <p>
     * Note: Method names are always applied to the target class, no matter if defined in an interface or the class
     * itself.
     * <p>
     * Internally, a NameMatchTransactionAttributeSource will be created from the given properties.
     * 
     * @see #setTransactionAttributeSource
     * @see TransactionInterceptor#setTransactionAttributes
     * @see TransactionAttributeEditor
     * @see NameMatchTransactionAttributeSource
     */
    public void setTransactionAttributes(Properties transactionAttributes) {
        this.transactionInterceptor.setTransactionAttributes(transactionAttributes);
    }

    /**
     * Set the transaction attribute source which is used to find transaction attributes. If specifying a String
     * property value, a PropertyEditor will create a MethodMapTransactionAttributeSource from the value.
     * 
     * @see #setTransactionAttributes
     * @see YueTransactionInterceptor#setTransactionAttributeSource
     * @see TransactionAttributeSourceEditor
     * @see MethodMapTransactionAttributeSource
     * @see NameMatchTransactionAttributeSource
     * @see AttributesTransactionAttributeSource
     * @see org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
     */
    public void setTransactionAttributeSource(TransactionAttributeSource transactionAttributeSource) {
        this.transactionInterceptor.setTransactionAttributeSource(transactionAttributeSource);
    }

    /**
     * Set a pointcut, i.e a bean that can cause conditional invocation of the TransactionInterceptor depending on
     * method and attributes passed. Note: Additional interceptors are always invoked.
     * 
     * @see #setPreInterceptors
     * @see #setPostInterceptors
     */
    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    /**
     * This callback is optional: If running in a BeanFactory and no transaction manager has been set explicitly, a
     * single matching bean of type PlatformTransactionManager will be fetched from the BeanFactory.
     * 
     * @see org.springframework.beans.factory.BeanFactoryUtils#beanOfTypeIncludingAncestors
     * @see org.springframework.transaction.PlatformTransactionManager
     */
    public void setBeanFactory(BeanFactory beanFactory) {
        if (this.transactionInterceptor.getTransactionManager() == null && beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
            PlatformTransactionManager ptm = (PlatformTransactionManager) BeanFactoryUtils.beanOfTypeIncludingAncestors(lbf,
                                                                                                                        PlatformTransactionManager.class);
            this.transactionInterceptor.setTransactionManager(ptm);
        }
    }

    /**
     * Creates an advisor for this FactoryBean's TransactionInterceptor.
     */
    protected Object createMainInterceptor() {
        this.transactionInterceptor.afterPropertiesSet();
        if (this.pointcut != null) {
            return new DefaultPointcutAdvisor(this.pointcut, this.transactionInterceptor);
        } else {
            // Rely on default pointcut.
            return new YueTransactionAttributeSourceAdvisor(this.transactionInterceptor);
        }
    }
}
