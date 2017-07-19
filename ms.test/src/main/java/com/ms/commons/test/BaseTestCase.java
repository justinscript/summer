/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.ms.commons.core.CommonServiceLocator;
import com.ms.commons.test.annotation.Mock;
import com.ms.commons.test.annotation.PrepareAnnotationTool;
import com.ms.commons.test.annotation.SecondaryJdbcSetting;
import com.ms.commons.test.annotation.TestCaseInfo;
import com.ms.commons.test.assertion.Assert;
import com.ms.commons.test.assertion.exception.AssertException;
import com.ms.commons.test.assertion.impl.DataBaseAssertion;
import com.ms.commons.test.assertion.impl.ForEachAssertion;
import com.ms.commons.test.assertion.impl.JavaBeanAssertion;
import com.ms.commons.test.assertion.impl.ParameterizedAssertion;
import com.ms.commons.test.cache.BuiltInCacheKey;
import com.ms.commons.test.cache.ThreadContextCache;
import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.common.ReflectUtil;
import com.ms.commons.test.common.SortUtil;
import com.ms.commons.test.common.StringUtil;
import com.ms.commons.test.common.comparator.Comparator;
import com.ms.commons.test.common.dbencoding.DbEncodingUtil;
import com.ms.commons.test.common.dbencoding.impl.info.CnStringDbEncodingInfo;
import com.ms.commons.test.common.dbencoding.impl.info.UTF8StringDbEncodingInfo;
import com.ms.commons.test.common.task.Task;
import com.ms.commons.test.common.task.TaskUtil;
import com.ms.commons.test.constants.IntlTestGlobalConstants;
import com.ms.commons.test.context.TestCaseRuntimeInfo;
import com.ms.commons.test.database.JdbcManagementTool;
import com.ms.commons.test.database.JdbcManagementToolBuilder;
import com.ms.commons.test.database.SecondaryPreareFilter;
import com.ms.commons.test.database.SecondarySetting;
import com.ms.commons.test.datareader.DataReaderType;
import com.ms.commons.test.datareader.DataReaderUtil;
import com.ms.commons.test.datareader.exception.ResourceNotFoundException;
import com.ms.commons.test.datareader.impl.BaseReaderUtil;
import com.ms.commons.test.integration.junit4.internal.IntlTestBlockJUnit4ClassRunner;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.mock.AliMock;
import com.ms.commons.test.mock.MockBeanPostProcessor;
import com.ms.commons.test.mock.inject.proxy.ProxyDepackageUtil;
import com.ms.commons.test.prepare.PrepareUtil;
import com.ms.commons.test.prepare.event.PrepareEventUtil;
import com.ms.commons.test.prepare.event.impl.ClearSpecialDataPrepareEvent;
import com.ms.commons.test.prepare.impl.DataBasePreparation;
import com.ms.commons.test.prepare.impl.ForEachPreparation;
import com.ms.commons.test.prepare.impl.JavaBeanPreparation;
import com.ms.commons.test.prepare.impl.YamlPreparation;
import com.ms.commons.test.treedb.JsonObjectUtils;
import com.ms.commons.test.treedb.TreeDatabase;
import com.ms.commons.test.treedb.TreeObject;
import com.ms.commons.test.treedb.TreeObjectAssert;

/**
 * @author zxc Apr 13, 2013 10:56:47 PM
 */
@RunWith(IntlTestBlockJUnit4ClassRunner.class)
public abstract class BaseTestCase extends AbstractBaseTestCase {

    protected final Logger                              log                  = Logger.getLogger(getClass());

    protected static final String                       DEFAULT_CLASS_SUFFIX = "Data";

    protected static final String                       TESTCASE_BASE_PATH   = "testcase.base.path";

    protected static volatile boolean                   firstTimeRan         = false;
    protected static volatile Map<List<Object>, Object> objectMethodMap      = new HashMap<List<Object>, Object>();

    static {
        long BEGIN_TIME = System.currentTimeMillis();

        Locale.setDefault(Locale.ENGLISH);
        System.setProperty("ark.trace.sql.stack", "false");
        System.setProperty("ark.trace.sql", "SUDI");
        // System.setProperty("com.ms.ark.trace.TraceHandler", "com.ms.ark.trace.LoggingTraceHandler");

        System.err.println("TEST STATIC INIT  TIME " + (System.currentTimeMillis() - BEGIN_TIME));
    }

    protected JdbcManagementTool                        jdbcManagementTool   = null;

    public BaseTestCase() {
        System.setProperty(TESTCASE_BASE_PATH, getAbstractBasePath());
    }

    protected Object getService(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * Spring容器管理对象的ID，通常是Service的ID
     */
    @Override
    protected Object contextKey() {
        TestCaseInfo testCaseInfo = this.getClass().getAnnotation(TestCaseInfo.class);
        if (!testCaseInfo.autoWire()) {
            return null;
        }
        if ((testCaseInfo != null) && (testCaseInfo.contextKey() != null) && (testCaseInfo.contextKey().length() != 0)) {
            return testCaseInfo.contextKey();
        } else {
            if (testCaseInfo.useDataSourceContextKey()) {
                return "DatasourceServicesLocator";
            }
            throw new RuntimeException(
                                       "You SHOULD override contextKey() or add TestCaseInfo to the test class or set autoWire false or add useDataSourceContextKey=true.");
        }
    }

    /**
     * 获取当前Spring容器
     */
    @Override
    protected ConfigurableApplicationContext loadContext(Object key) throws Exception {
        return (ConfigurableApplicationContext) CommonServiceLocator.getApplicationContext();
    }

    /**
     * 设置测试类注入的模式
     * 
     * <pre>
     * 如果测试用例 {@link TestCaseInfo#autoWire()}，则利用{@link AbstractDependencyInjectionSpringContextTests#prepareTestInstance()} 注入依赖
     * 这里最关键的是注入了dataSource和transcationManager.
     * </pre>
     */
    @Override
    protected void prepareTestInstance() throws Exception {
        TestCaseInfo testCaseInfo = this.getClass().getAnnotation(TestCaseInfo.class);

        this.initMoke();
        this.setDependencyCheck(false);

        // 依赖注入方式
        if (testCaseInfo.autoWire()) {
            this.setAutowireMode(testCaseInfo.autoWireMode());
        } else {
            this.setAutowireMode(AUTOWIRE_NO);
        }

        // 注入jdbcExtractor
        // if ((testCaseInfo == null) || testCaseInfo.useJdbcExtractor()) {
        // this.applicationContext.getBean("jdbcExtractor");
        // }

        // 利用AbstractDependencyInjectionSpringContextTests 进行依赖注入
        super.prepareTestInstance();
        afterPrepareTestInstance();
        // Mock current test instance.
        if (isMockOn()) {
            AliMock.mockObject(this);
        }

    }

    protected void afterPrepareTestInstance() {

    }

    /**
     * 因为所有的Service对象是在统一容器中的，所有这个方式也要改的，可以是怎么改呢？
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    protected void initMoke() throws InstantiationException, IllegalAccessException {
        // Mock once on startup
        if (isMockOn()) {

            if (log.isDebugEnabled()) {
                log.debug("================>initMock!");
            }
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) CommonServiceLocator.getApplicationContext();
            context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {

                public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                    beanFactory.addBeanPostProcessor(new MockBeanPostProcessor());
                }
            });
            context.refresh();
        }
    }

    @Override
    protected void onSetUpBeforeTransaction() throws Exception {
        if (getClass().getAnnotation(TestCaseInfo.class) != null) {
            if (!getClass().getAnnotation(TestCaseInfo.class).defaultRollBack()) {
                log.warn("Set default rollback to false.");
            }
            setDefaultRollback(getClass().getAnnotation(TestCaseInfo.class).defaultRollBack());
        }

        if ((getClass().getAnnotation(SecondaryJdbcSetting.class) != null) && (jdbcManagementTool == null)) {
            SecondaryJdbcSetting setting = getClass().getAnnotation(SecondaryJdbcSetting.class);
            if (setting.jdbcManagementToolBuilder() == JdbcManagementToolBuilder.class) {
                jdbcManagementTool = new JdbcManagementTool(setting);
                jdbcManagementTool.setDefaultRollback(getClass().getAnnotation(TestCaseInfo.class).defaultRollBack());
            } else {
                jdbcManagementTool = ReflectUtil.newInstance(setting.jdbcManagementToolBuilder()).buildJdbcManagementTool(this);
            }
        }

        if (jdbcManagementTool != null) {
            jdbcManagementTool.startTransaction();
            SecondarySetting secondarySetting = null;

            Method method = ThreadContextCache.get(Method.class, BuiltInCacheKey.Method);
            final PrepareAnnotationTool prepareAnnotation = new PrepareAnnotationTool(method);
            if (prepareAnnotation != null) {
                if (prepareAnnotation.secondaryPrepareFilter() != SecondaryPreareFilter.class) {
                    final SecondaryPreareFilter secondaryPreareFilter = ReflectUtil.newInstance(prepareAnnotation.secondaryPrepareFilter());
                    secondarySetting = new SecondarySetting(jdbcManagementTool, secondaryPreareFilter);
                }
            }
            if (secondarySetting == null) {
                SecondaryJdbcSetting secondaryJdbcSetting = getClass().getAnnotation(SecondaryJdbcSetting.class);
                secondarySetting = new SecondarySetting(
                                                        jdbcManagementTool,
                                                        ReflectUtil.newInstance(secondaryJdbcSetting.secondaryPrepareFilter()));
            }
            ThreadContextCache.put(BuiltInCacheKey.SecondarySetting, secondarySetting);
        }

    }

    /**
     * 当事务开始后，我们就进行我们的数据清理准备工作
     */
    // @Override
    // protected void startNewTransaction() throws TransactionException {
    // // 首先是让父亲帮助创建事务
    // super.startNewTransaction();
    // // 然后开始数据准备
    // try {
    // before();
    // } catch (Exception e) {
    // throw new RuntimeException("Init Test Data Failed", e);
    // }
    // }

    @Override
    protected void onTearDownAfterTransaction() throws Exception {
        if (jdbcManagementTool != null) {
            jdbcManagementTool.finishTrasaction();
            ThreadContextCache.put(BuiltInCacheKey.SecondarySetting, null);
        }
    }

    @Before
    public void before() throws Exception {
        log.debug("BaseTestCase.before()");
        TestCaseInfo testCaseInfo = this.getClass().getAnnotation(TestCaseInfo.class);
        if (testCaseInfo.autoWire()) {
            this.applicationContext = getContext(contextKey());
            prepareTestInstance();
        }
        onSetUp();

        initOnBeforeEveryTestCase();

        if (!firstTimeRan) {
            // 仅第一次运行
            firstTimeRan = true;
            runOnceBeforeAll();
        }
    }

    @After
    @SuppressWarnings("unchecked")
    public void after() throws Exception {
        log.debug("=========================> BaseTestCase.after()");
        if (ThreadContextCache.get(BuiltInCacheKey.Finally) != null) {

            for (Runnable runnable : (List<Runnable>) ThreadContextCache.get(List.class, BuiltInCacheKey.Finally)) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    log.error("Error occured in clear data.", e);
                }
            }
        }

        final List<Task> taskList = (List<Task>) TestCaseRuntimeInfo.current().getContext().get(ClearSpecialDataPrepareEvent.__finish__task__list__);
        if (taskList != null) {
            if (TestCaseRuntimeInfo.current().getPrepare().newThreadTransactionImport()) {
                runInBlockedThread(new Runnable() {

                    public void run() {
                        log.warn("Clear data in new thread (transaction).");
                        TransactionStatus localTransactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
                        try {
                            TaskUtil.runTasks(taskList);
                            transactionManager.commit(localTransactionStatus);
                        } catch (Throwable t) {
                            log.error("Clear data transaction has be rolled back.", t);
                            transactionManager.rollback(localTransactionStatus);
                            throw new RuntimeException(t);
                        }
                    }
                });
            } else {
                TaskUtil.runTasks(taskList);
            }
        }

        super.tearDown();

        if (isMockOn()) {
            AliMock.clearMock();
        }
        objectMethodMap.clear();
        ThreadContextCache.clear();
    }

    // 以下方法是给程序在写测试用例时使用的
    // ================================================================================

    /**
     * 在所有单元测试先于所有测试函数运行
     */
    protected void runOnceBeforeAll() {
    }

    // ================================================================================

    /**
     * 当前被测试的单元测试
     */
    protected Class<?> clazz() {
        TestCaseInfo tci = this.getClass().getAnnotation(TestCaseInfo.class);
        if ((tci == null) || (tci.testFor() == TestCase.class)) {
            String testClassName = this.getClass().getName();
            if (testClassName.endsWith("Test")) {
                try {
                    return Class.forName(testClassName.substring(0, testClassName.length() - 4));
                } catch (ClassNotFoundException e) {
                    System.err.println("Load class failed: " + testClassName.substring(0, testClassName.length() - 4));
                }
            }
            throw new RuntimeException("No TestCaseInfo or not assigned For class.");
        }
        return tci.testFor();
    }

    /**
     * 用于调用对象的私有函数时使用(non-static 函数)
     */
    protected Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        if (object instanceof Proxy) {
            object = ProxyDepackageUtil.getLastDepackageObject(object);
        }
        Class<?> clazz = object.getClass();
        return ReflectUtil.invokeMethod(clazz, object, methodName, parameterTypes, parameters);
    }

    /**
     * 和{@link BaseTestCase#invokeMethod(Object, String, Class[], Object[])}功能类似，调用没有参数的私有函数
     * 
     * @param object
     * @param methodName
     * @return
     */
    protected Object invokeMethod(Object object, String methodName) {
        return invokeMethod(object, methodName, null, null);
    }

    /**
     * 用于调用类的私有函数时使用(static 函数)
     */
    protected Object invokeMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        return ReflectUtil.invokeMethod(clazz, null, methodName, parameterTypes, parameters);
    }

    /**
     * 和{@link BaseTestCase#invokeMethod(Class, String, Class[], Object[])}功能类似，调用没有参数的私有函数
     * 
     * @param clazz
     * @param methodName
     * @return
     */
    protected Object invokeMethod(Class<?> clazz, String methodName) {
        return invokeMethod(clazz, methodName, null, null);
    }

    /**
     * 空数组
     * 
     * @return
     */
    protected Object[] nulls() {
        return new Object[] { null };
    }

    /**
     * 调用方法
     * 
     * @param methodName
     * @param params
     * @return
     */
    protected Object callStatic(String methodName, Object... params) {
        return callMethod(null, methodName, params);
    }

    /**
     * 调用方法
     * 
     * @param obj
     * @param methodName
     * @param params
     * @return
     */
    protected Object callMethod(Object obj, String methodName, Object... params) {
        Class<?> clazz = (obj == null) ? clazz() : ((obj instanceof Class<?>) ? (Class<?>) obj : obj.getClass());
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                if (method.getParameterTypes().length == params.length) {
                    method.setAccessible(true);
                    try {
                        if ((obj == null) || (obj instanceof Class<?>)) {
                            return method.invoke(null, params);
                        } else {
                            return method.invoke(obj, params);
                        }
                    } catch (Exception e) {
                        throw ExceptionUtil.wrapToRuntimeException(e);
                    }
                }
            }
        }
        throw new RuntimeException("Cannot find method '" + methodName + "' in '" + clazz + "'.");
    }

    // ================================================================================

    /**
     * 得到一个临时文件
     */
    protected File requireTempFile(String fileName) {
        return new File(IntlTestGlobalConstants.TESTCASE_USER_TEMP_DIR + File.separator + fileName);
    }

    /**
     * 得到数据文件
     * 
     * @param fileName
     * @return
     */
    protected File requireDataFile(String fileName) {
        return new File(BaseReaderUtil.getBasePath() + fileName);
    }

    // ================================================================================

    /**
     * 通过反射对数组进行排序
     */
    protected void sortList(List<?> list, String sortField) {
        SortUtil.sortList(list, sortField, true);
    }

    /**
     * 通过反射对数组进行排序
     */
    protected void sortList(List<?> list, String sortField, boolean asc) {
        SortUtil.sortList(list, sortField, asc);
    }

    /**
     * 得到一个对象(对象或类)的值
     */
    @SuppressWarnings("unchecked")
    protected <T> T getObject(Object object, String fieldName) {
        Class<?> clazz = (object.getClass() == Class.class) ? (Class<?>) object : object.getClass();
        try {
            Field field = ReflectUtil.getDeclaredField(clazz, fieldName);
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
                return (T) field.get(null);
            } else {
                return (T) field.get(object);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置到一个对象(对象或类)的值
     */
    protected void setObject(Object object, String fieldName, Object value) {
        Class<?> clazz = (object.getClass() == Class.class) ? (Class<?>) object : object.getClass();
        try {
            Field field = ReflectUtil.getDeclaredField(clazz, fieldName);
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
                field.set(null, value);
            } else {
                field.set(object, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 在当前单元测试函数中将对象设置为某个值
     * 
     * @param object
     * @param fieldName
     * @param value
     */
    protected void replaceObjectInTestCase(final Object object, final String fieldName, Object value) {

        final Object oldValue = getObject(object, fieldName);
        setObject(object, fieldName, value);

        List<Object> listKey = Arrays.asList(object, fieldName);
        if (objectMethodMap.get(listKey) == null) {
            objectMethodMap.put(listKey, Boolean.TRUE);
            // 重新设计回来，并公在第一次调用时有效
            clearDataAtFinally(new Runnable() {

                public void run() {
                    setObject(object, fieldName, oldValue);
                }
            });
        }
    }

    // ================================================================================

    /**
     * 将prepare数据文件中的指定表中的数据导入到数据库中
     */
    protected void prepareDataBase(String... tables) {
        PrepareUtil.prepareDataBase(jdbcTemplate, getMemoryDB(), tables);
    }

    protected <T> T prepareObject(Class<T> clazz, String table) {
        return PrepareUtil.prepareObject(getMemoryDB(), clazz, table);
    }

    /**
     * 将prepare数据文件中的指定表的第一行创建java pojo对象
     */
    protected <T> List<T> prepareObjectList(Class<T> clazz, String table) {
        return PrepareUtil.prepareObjectList(getMemoryDB(), clazz, table);
    }

    /**
     * 将prepare数据文件中指定表创建java pojo对象数组
     */
    protected <T> T prepareObject(Class<T> clazz, String database, String table) {
        return PrepareUtil.prepareObject(getMemoryDB(database), clazz, table);
    }

    /**
     * 将指定database数据文件中指定表创建java pojo对象
     */
    protected <T> List<T> prepareObjectList(Class<T> clazz, String database, String table) {
        return PrepareUtil.prepareObjectList(getMemoryDB(database), clazz, table);
    }

    // ================================================================================

    /**
     * 将指定database数据文件中指定表创建java pojo对象数组
     */
    private <T> boolean checkBothNull(List<T> expected, List<T> actual) {
        if (expected == null) {
            assertNull("expected is null but actual is not null", actual);
            return true;
        } else if (actual == null) {
            fail("expected is not null but actual is null");
        }
        return false;
    }

    /**
     * 检查expected和actual是否同时为null
     */
    protected <T> void assertListEquals(List<T> expected, List<T> actual) {
        if (checkBothNull(expected, actual)) return;
        assertEquals("Size not equals", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals("Index " + i + " assert failed", expected.get(i), actual.get(i));
        }
    }

    /**
     * 检查两个list是否一致，不判断list类型(如ArrayList或LinkedList)哦
     */
    protected <T> void assertListEquals(List<T> expected, List<T> actual, Comparator<T> comparator) {
        if (checkBothNull(expected, actual)) return;
        assertEquals("Size not equals", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertTrue("Index " + i + " assert failed", comparator.compare(expected.get(i), actual.get(i)));
        }
    }

    /**
     * 检查两个list在排序后是否一致，list元素必须实现Comparable接口，例如Integer和String
     */
    protected <T extends Comparable<? super T>> void assertListSortedEquals(List<T> expected, List<T> actual) {
        if (checkBothNull(expected, actual)) return;
        assertEquals("Size not equals", expected.size(), actual.size());
        Collections.sort(expected);
        Collections.sort(actual);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals("Index " + i + " assert failed", expected.get(i), actual.get(i));
        }
    }

    /**
     * 检查两个list在排序后是否一致，list元素可以是复杂对象，通过java.util.Comparator来进行排序对象的比较
     */
    protected <T> void assertListSortedEquals(List<T> expected, List<T> actual, java.util.Comparator<T> comparator) {
        if (checkBothNull(expected, actual)) return;
        assertEquals("Size not equals", expected.size(), actual.size());
        Collections.sort(expected, comparator);
        Collections.sort(actual, comparator);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals("Index " + i + " assert failed", expected.get(i), actual.get(i));
        }
    }

    /**
     * 检查两个list在排序后是否一致，list元素可以是复杂对象，通过sortField反射来进行对象的排序，默认升序
     */
    protected <T> void assertListSortedEquals(List<T> expected, List<T> actual, String sortField) {
        assertListSortedEquals(expected, actual, sortField, true);
    }

    /**
     * 检查两个list在排序后是否一致，list元素可以是复杂对象，通过sortField反射来进行对象的排序，通过asc决定升序/降序
     */
    protected <T> void assertListSortedEquals(List<T> expected, List<T> actual, String sortField, boolean asc) {
        if (checkBothNull(expected, actual)) return;
        assertEquals("Size not equals", expected.size(), actual.size());
        SortUtil.sortList(expected, sortField, asc);
        SortUtil.sortList(actual, sortField, asc);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals("Index " + i + " assert failed", expected.get(i), actual.get(i));
        }
    }

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 下面几个是新的数据准备验证API <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    /**
     * 用于准备数据到对象 *
     * 
     * @return
     */
    protected JavaBeanPreparation getObjPreparation() {
        return (new JavaBeanPreparation()).database(getMemoryDB());
    }

    protected YamlPreparation getYamlPreparation(String dataFileName) {
        return new YamlPreparation().relativePath(getRelativePath(DataReaderType.Yaml, dataFileName));
    }

    /**
     * 用于准备数据到数据库 *
     * 
     * @return
     */
    protected DataBasePreparation getDBPreparation() {
        return (new DataBasePreparation()).jdbcTemplate(jdbcTemplate).database(getMemoryDB());
    }

    /**
     * 准备ForEach数据 *
     * 
     * @param <T>
     * @return
     */
    protected <T> ForEachPreparation<T> getForPreparation() {
        return new ForEachPreparation<T>();
    }

    /**
     * 用于验证数据库数据 *
     * 
     * @return
     */
    protected DataBaseAssertion getDBAssertion() {
        return (new DataBaseAssertion()).jdbcTemplate(jdbcTemplate).database(getMemoryDB(BuiltInCacheKey.Result.getValue()));
    }

    /**
     * 用于验证对象数据 *
     * 
     * @return
     */
    protected JavaBeanAssertion getObjAssertion() {
        return (new JavaBeanAssertion()).database(getMemoryDB(BuiltInCacheKey.Result.getValue()));
    }

    /**
     * 用于验证参数函数，通过memorydatabase数据
     * 
     * @return
     */
    protected ParameterizedAssertion getParamAssertion() {
        return (new ParameterizedAssertion()).database(getMemoryDB());
    }

    /**
     * ForEach验证数据 *
     * 
     * @param <T>
     * @return
     */
    protected <T> ForEachAssertion<T> getForAssertion() {
        return new ForEachAssertion<T>();
    }

    /**
     * 断言单元测试运行正常
     */
    protected void assertSuccessed() {
        // do nothing
    }

    /**
     * Assert string is blank: whitespace, empty ("") or null
     */
    protected void assertBlank(String str) {
        assertTrue(StringUtils.isBlank(str));
    }

    /**
     * Assert string is empty ("") or null
     */
    protected void assertEmpty(String str) {
        assertTrue(StringUtils.isEmpty(str));
    }

    /**
     * Assert string is not blank
     */
    protected void assertNotBlank(String str) {
        assertTrue(StringUtils.isNotBlank(str));
    }

    /**
     * Assert string is not empty
     */
    protected void assertNotEmpty(String str) {
        assertTrue(StringUtils.isNotEmpty(str));
    }

    /**
     * Assert string contains only unicode digits.
     */
    protected void assertNumeric(String str) {
        assertTrue(StringUtils.isNumeric(str));
    }

    /**
     * Assert string contains only unicode digits or space(' ').
     */
    protected void assertNumericSpace(String str) {
        assertTrue(StringUtils.isNumericSpace(str));
    }

    /**
     * Assert string contains only whitespace.
     */
    protected void assertWhitespace(String str) {
        assertTrue(StringUtils.isWhitespace(str));
    }

    /**
     * Assert array is null or empty(length==0)
     */
    protected <T> void assertNullOrEmpty(T[] array) {
        assertTrue(array == null || array.length == 0);
    }

    /**
     * Assert list is null or empty
     */
    protected <T> void assertNullOrEmpty(List<T> list) {
        assertTrue(list == null || list.isEmpty());
    }

    /**
     * 将result数据文件中默认表(仅当数据文件中只有一个表时)的第一行与java pojo对象比较
     */
    protected void assertResult(Object bean) {
        Assert.assertResult(getMemoryDB(BuiltInCacheKey.Result.getValue()), bean);
    }

    /**
     * 将result数据文件中指定表的第一行与java pojo对象比较
     * 
     * @param bean
     * @param table
     */
    protected void assertResult(Object bean, String table) {
        Assert.assertResult(getMemoryDB(BuiltInCacheKey.Result.getValue()), bean, table);
    }

    /**
     * 将result数据文件中默认表(仅当数据文件中只有一个表时)的所有行与java pojo对象数组比较
     */
    protected void assertResultList(List<?> beanList) {
        Assert.assertResultList(getMemoryDB(BuiltInCacheKey.Result.getValue()), beanList);
    }

    /**
     * 将result数据文件中指定表的所有行与java pojo对象数组比较
     */
    protected void assertResultList(List<?> beanList, String table) {
        Assert.assertResultList(getMemoryDB(BuiltInCacheKey.Result.getValue()), beanList, table);
    }

    /**
     * 将result数据文件中指定表的所有行与java pojo对象数组比较,只比较指定的列
     */
    protected void assertResultList(List<?> beanList, String table, String[] columns) {
        Assert.assertResultList(getMemoryDB(BuiltInCacheKey.Result.getValue()), beanList, table, columns);
    }

    /**
     * 将result数据文件中默认表(仅当数据文件中只有一个表时)的所有行与排序后的java pojo对象list比较
     */
    protected void assertResultListSorted(List<?> beanList, String sortField) {
        assertResultListSorted(beanList, sortField, true);
    }

    /**
     * 将result数据文件中默认表(仅当数据文件中只有一个表时)的所有行与排序后的java pojo对象list比较
     */
    protected void assertResultListSorted(List<?> beanList, String sortField, boolean asc) {
        SortUtil.sortList(beanList, sortField, asc);
        assertResultList(beanList);
    }

    /**
     * 将result数据文件中指定表的所有行与排序后的java pojo对象list比较
     */
    protected void assertResultListSorted(List<?> beanList, String table, String sortField) {
        assertResultListSorted(beanList, table, sortField, true);
    }

    /**
     * 将result数据文件中指定表的所有行与排序后的java pojo对象list比较
     */
    protected void assertResultListSorted(List<?> beanList, String table, String sortField, boolean asc) {
        SortUtil.sortList(beanList, sortField, asc);
        assertResultList(beanList, table);
    }

    /**
     * 将result数据文件中指定表的所有行与排序后的java pojo对象list比较,只比较指定的列
     */
    protected void assertResultListSorted(List<?> beanList, String table, String[] columns, String sortField) {
        assertResultListSorted(beanList, table, columns, sortField, true);
    }

    /**
     * 将result数据文件中指定表的所有行与排序后的java pojo对象list比较,只比较指定的列
     */
    protected void assertResultListSorted(List<?> beanList, String table, String[] columns, String sortField,
                                          boolean asc) {
        SortUtil.sortList(beanList, sortField, asc);
        assertResultList(beanList, table, columns);
    }

    /**
     * 将指定database数据文件中默认表(仅当数据文件中只有一个表时)的所有行与java pojo对象数组比较
     */
    public void assertResultList(String database, List<?> beanList) {
        Assert.assertResultList(getMemoryDB(database), beanList);
    }

    /**
     * 将指定database数据文件中指定表的所有行与java pojo对象数组比较
     */
    protected void assertResultList(String database, List<?> beanList, String table) {
        Assert.assertResultList(getMemoryDB(database), beanList, table);
    }

    /**
     * 将指定database数据文件中默认表(仅当数据文件中只有一个表时)的所有行与排序后的java pojo对象list比较
     */
    public void assertResultListSorted(String database, List<?> beanList, String sortField) {
        assertResultListSorted(database, beanList, sortField, true);
    }

    /**
     * 将指定database数据文件中默认表(仅当数据文件中只有一个表时)的所有行与排序后的java pojo对象list比较
     */
    public void assertResultListSorted(String database, List<?> beanList, String sortField, boolean asc) {
        SortUtil.sortList(beanList, sortField, asc);
        assertResultList(database, beanList);
    }

    /**
     * 将指定database数据文件中指定表的所有行与排序后的java pojo对象list比较
     */
    protected void assertResultListSorted(String database, List<?> beanList, String table, String sortField) {
        assertResultListSorted(database, beanList, table, sortField, true);
    }

    /**
     * 将指定database数据文件中指定表的所有行与排序后的java pojo对象list比较
     */
    protected void assertResultListSorted(String database, List<?> beanList, String table, String sortField, boolean asc) {
        SortUtil.sortList(beanList, sortField, asc);
        assertResultList(database, beanList, table);
    }

    /**
     * 将result数据文件中指定表的所有行与数据库对应表中数据比较
     */
    protected void assertResultTable(String table) {
        Assert.assertResultTable(jdbcTemplate, getMemoryDB(BuiltInCacheKey.Result.getValue()), table);
    }

    /**
     * 将result数据文件中指定表的所有行与数据库对应表中数据比较,只检查指定的列
     */
    protected void assertResultTable(String table, String[] columns) {
        Assert.assertResultTable(jdbcTemplate, getMemoryDB(BuiltInCacheKey.Result.getValue()), table, columns);
    }

    /**
     * 将result数据文件中指定表的所有行与数据库对应表中数据比较(仅比较符合条件的数据库表中的数据)
     */
    protected void assertResultTable(String table, String whereSql, String sortSql, Object[] args) {
        Assert.assertResultTable(jdbcTemplate, getMemoryDB(BuiltInCacheKey.Result.getValue()), table, whereSql,
                                 sortSql, args);
    }

    /**
     * 将result数据文件中指定表的所有行与数据库对应表中数据比较(仅比较符合条件的数据库表中的数据)
     */
    protected void assertResultTable(String table, String whereSql, String sortSql, Object[] args, String[] columns) {
        Assert.assertResultTable(jdbcTemplate, getMemoryDB(BuiltInCacheKey.Result.getValue()), table, whereSql,
                                 sortSql, args, columns);
    }

    /**
     * 将指定database数据文件中默认表(仅当数据文件中只有一个表时)的第一行与java pojo对象比较
     */
    protected void assertResult(String database, Object bean) {
        Assert.assertResult(getMemoryDB(database), bean);
    }

    /**
     * 将指定database数据文件中指定表的第一行与java pojo对象比较
     * 
     * @param bean
     * @param table
     */
    protected void assertResult(String database, Object bean, String table) {
        Assert.assertResult(getMemoryDB(database), bean, table);
    }

    /**
     * 将指定database数据文件中指定表的所有行与数据库对应表中数据比较
     */
    protected void assertResultTable(String database, String table) {
        Assert.assertResultTable(jdbcTemplate, getMemoryDB(database), table);
    }

    /**
     * * 将指定database数据文件中指定表的所有行与数据库对应表中数据比较(仅比较符合条件的数据库表中的数据)
     */

    protected void assertResultTable(String database, String table, String whereSql, String sortSql, Object[] args) {
        Assert.assertResultTable(jdbcTemplate, getMemoryDB(database), table, whereSql, sortSql, args);
    }

    /**
     * 断言数据库指定表中没有数据
     */
    protected void assertResultTableEmpty(String table) {
        assertResultTableCount(table, 0);
    }

    /**
     * 断言数据库中没有指定条件的数据
     */
    protected void assertResultTableEmpty(String table, String where) {
        assertResultTableCount(table, where, 0);
    }

    /**
     * 断言数据库指定表中数据数
     */
    protected void assertResultTableCount(String table, int count) {
        assertResultTableCount(table, null, count);
    }

    /**
     * 断言数据库中指定条件的数据数
     */
    protected void assertResultTableCount(String table, String where, int count) {
        String w = ((where == null) || (where.length() == 0)) ? "1=1" : where;
        String we = (where == null) ? "" : where;
        int c = jdbcTemplate.queryForInt("select count(*) from " + table + " where " + w);
        if (c != count) {
            throw new AssertException("Aspect table `" + table + "` have " + count + " recoreds on `" + we
                                      + "` but actual have " + c + " records.");
        }
    }

    // ================================================================================

    /**
     * 取得prepare数据文件中的内存数据库对象
     */
    protected MemoryDatabase getMemoryDB() {
        MemoryDatabase database = ThreadContextCache.get(MemoryDatabase.class, BuiltInCacheKey.Prepare.getValue());
        if (database == null) {
            throw new RuntimeException("Memory database not prepared or prepare file not exists.");
        }
        return database;
    }

    /**
     * 取得指定数据文件名的内存数据库对象
     */
    protected MemoryDatabase getMemoryDB(String dataFileName) {
        Method method = ThreadContextCache.get(Method.class, BuiltInCacheKey.Method);
        return getMemoryDB(getDataReaderType(method), dataFileName);
    }

    /**
     * 取得指定类型及数据文件名的内存数据库对象
     */
    protected MemoryDatabase getMemoryDB(DataReaderType dataType, String dataFileName) {
        return getMemoryDB(dataType, dataFileName, true);
    }

    /**
     * * 取得指定类型及数据文件名的内存数据库对象(可以根据参数决定是否在文件未找到时抛异常)
     */
    protected MemoryDatabase getMemoryDB(DataReaderType dataType, String dataFileName, boolean checkFileExists) {

        MemoryDatabase cachedDataBase = ThreadContextCache.get(MemoryDatabase.class, dataFileName);
        if (cachedDataBase != null) {
            return cachedDataBase;
        }

        String relativePath = getRelativePath(dataType, dataFileName);
        try {
            MemoryDatabase database = (MemoryDatabase) DataReaderUtil.readData(dataType, relativePath);
            ThreadContextCache.put(dataFileName, database);

            return database;
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            if (checkFileExists) {
                throw e;
            } else {
                return null;
            }
        }
    }

    // ================================================================================

    /**
     * 清除数据库指定表的数据
     */
    protected void clearTables(String... tables) {
        for (String table : tables) {
            clearTableData(table);
        }
    }

    /**
     * 清除数据库中的数据<br>
     * 你也可以使用spring提供的deleteFromTables方法
     * 
     * @see AbstractTransactionalDataSourceSpringContextTests#deleteFromTables(String[])
     */
    protected void clearTableData(String table) {
        clearTableData(table, "1=1");
    }

    /**
     * 清除数据库中的数据(whereSql 可以是1=1)<br>
     * 你也可以使用spring提供的deleteFromTables方法
     * 
     * @see AbstractTransactionalDataSourceSpringContextTests#deleteFromTables(String[])
     */
    protected void clearTableData(String table, String whereSql) {
        if ((whereSql == null) || (whereSql.length() == 0)) {
            throw new RuntimeException("Where sql cannot be empty.");
        }
        int count = jdbcTemplate.update("delete from " + table + " where " + whereSql);
        log.info("Table `" + table + "` 's `" + count + "` records has been cleared on `" + whereSql + "`.");
    }

    // ================================================================================

    /**
     * 创建一个阻塞的新线程来处理函数
     */
    protected void runInBlockedThread(final Runnable runnable) {
        final List<Throwable> throwable = new ArrayList<Throwable>();

        Thread blockedThread = new Thread(new Runnable() {

            public void run() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    throwable.add(t);
                }
            }
        });
        blockedThread.start();
        try {
            blockedThread.join();
        } catch (InterruptedException e) {
            log.error("Thread interrupted error occured in blocked thread runing.", e);
            throw new RuntimeException(e);
        }
        if (throwable.size() > 0) {
            Throwable t = throwable.get(0);
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(throwable.get(0));
            }
        }
    }

    /**
     * 注册在函数执行结果时的动作
     */
    @SuppressWarnings("unchecked")
    protected void clearDataAtFinally(Runnable runnable) {
        if (ThreadContextCache.get(BuiltInCacheKey.Finally) == null) {
            ThreadContextCache.put(BuiltInCacheKey.Finally, new ArrayList<Runnable>());
        }
        ThreadContextCache.get(List.class, BuiltInCacheKey.Finally).add(runnable);
    }

    // ================================================================================

    // {{{{{{{{{{{{{{{{ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ }}}}}}}}}}}}}}}} //

    protected void initOnBeforeEveryTestCase() {
        log.debug("=========================> BaseTestCase.initOnBeforeEveryTestCase() init db data");
        String basePath = getClassBasePath();
        if (basePath.contains("com.mountainminds.eclemma.core")) { // if run in eclemma
            System.err.println("Run in eclemma we try to change base path!");
            String userDir = IntlTestGlobalConstants.USER_DIR;

            if (new File(FilenameUtils.concat(userDir, "src/java")).exists()) {
                basePath = getFinalClassBasePath(userDir + "/src/java.test/");
            } else if (new File(FilenameUtils.concat(userDir, "src/main")).exists()) {
                basePath = getFinalClassBasePath(userDir + "/src/test/java");
            } else {
                System.err.println("Oh, we cannot detect is antx or maven.");
            }
        }
        log.info("Base path set to `" + basePath + "`.");
        BaseReaderUtil.setBasePath(basePath);

        initMemoryDatabaseOnBeforeEveryTestCase();
    }

    // called by prepare class runner
    protected void initMemoryDatabaseOnBeforeEveryTestCase() {
        Method method = ThreadContextCache.get(Method.class, BuiltInCacheKey.Method);
        if (!isNeedPrepare(method)) {
            return;
        }
        // 清除所有已经注册的handle
        DbEncodingUtil.clearInfo();

        final PrepareAnnotationTool prepareAnnotation = new PrepareAnnotationTool(method);
        // xml_tree格式不支持MemoryDatabase
        if (DataReaderType.TreeXml == prepareAnnotation.type()) {
            return;
        }

        // 注册转换器
        DbEncodingUtil.applyInfo(new CnStringDbEncodingInfo(prepareAnnotation.cnStringEncoding()));
        DbEncodingUtil.applyInfo(new UTF8StringDbEncodingInfo(prepareAnnotation.utf8StringEncoding()));

        // 需要导入的Sheet
        final String[] importTables = StringUtil.splitAndTrimByComma(prepareAnnotation.importTables());

        PrepareEventUtil.clearRegister();
        if (prepareAnnotation.autoClearExistsData() || prepareAnnotation.autoClearImportDataOnFinish()) {
            log.info("Import data clear exists ones on.");
            PrepareEventUtil.register(new ClearSpecialDataPrepareEvent(prepareAnnotation.primaryKey()));
        }

        final MemoryDatabase prepare = getMemoryDB(prepareAnnotation.type(), BuiltInCacheKey.Prepare.getValue(), true);
        getMemoryDB(prepareAnnotation.type(), BuiltInCacheKey.Result.getValue(), false);

        // common prepare (for test case class)
        MemoryDatabase commonPrepare = null;
        if (prepareAnnotation.autoImportCommonData()) {
            commonPrepare = getMemoryDB(prepareAnnotation.type(), ":prepare", false);
        }
        final MemoryDatabase finalCommonPrepare = commonPrepare;

        if (prepareAnnotation.autoImport()) {

            boolean isDefaultRollback = isDefaultRollback();
            if (prepareAnnotation.autoClear()) {
                if ((!isDefaultRollback) || (prepareAnnotation.newThreadTransactionImport())) {
                    if (!prepareAnnotation.forceClear()) {
                        throw new RuntimeException("Cannot import data when default rollback was"
                                                   + " turned off and no force clear flag.");
                    }
                }
            }
            log.info("Auto import data on flag autoImport:" + prepareAnnotation.autoImport() + ", autoClear:"
                     + prepareAnnotation.autoClear() + ", defaultRollback:" + isDefaultRollback + ", forceClear:"
                     + prepareAnnotation.forceClear() + ", newThreadTransactionImport:"
                     + prepareAnnotation.newThreadTransactionImport());
            if (prepareAnnotation.newThreadTransactionImport()) {
                runInBlockedThread(new Runnable() {

                    public void run() {
                        log.warn("Import data in new thread (transaction).");
                        TransactionStatus localTransactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
                        try {
                            if (prepareAnnotation.autoImportCommonData() && (finalCommonPrepare != null)) {
                                PrepareUtil.prepareDataBase(jdbcTemplate, finalCommonPrepare,
                                                            prepareAnnotation.autoClear());
                            }

                            if (importTables != null) {
                                PrepareUtil.prepareDataBase(jdbcTemplate, prepare, prepareAnnotation.autoClear(),
                                                            importTables);
                            } else {
                                PrepareUtil.prepareDataBase(jdbcTemplate, prepare, prepareAnnotation.autoClear());
                            }

                            log.info("Import data transaction has be commited.");
                            transactionManager.commit(localTransactionStatus);
                        } catch (Throwable t) {
                            log.error("Import data transaction has be rolled back.", t);
                            transactionManager.rollback(localTransactionStatus);
                            throw new RuntimeException(t);
                        }
                    }
                });
            } else {
                if (prepareAnnotation.autoImportCommonData() && (finalCommonPrepare != null)) {
                    PrepareUtil.prepareDataBase(jdbcTemplate, finalCommonPrepare, prepareAnnotation.autoClear());
                }
                if (importTables != null) {
                    PrepareUtil.prepareDataBase(jdbcTemplate, prepare, prepareAnnotation.autoClear(), importTables);
                } else {
                    PrepareUtil.prepareDataBase(jdbcTemplate, prepare, prepareAnnotation.autoClear());
                }
            }
        }
    }

    protected String getAbstractBasePath() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String slashClassName = this.getClass().getName().replace('.', '/');
        String classPath = classLoader.getResource(slashClassName + ".class").getPath();
        int classIndex = classPath.indexOf(slashClassName);
        String basePart = classPath.substring(0, classIndex);
        return basePart;
    }

    protected String getClassBasePath() {

        TestCaseInfo info = this.getClass().getAnnotation(TestCaseInfo.class);
        if ((info != null) && (info.basePath().length() != 0)) {
            return info.basePath();
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String slashClassName = this.getClass().getName().replace('.', '/');
        String classPath = classLoader.getResource(slashClassName + ".class").getPath();
        int classIndex = classPath.indexOf(slashClassName);
        String basePart = classPath.substring(0, classIndex);

        return getFinalClassBasePath(basePart);
    }

    protected String getFinalClassBasePath(String basePart) {
        String slashClassName = this.getClass().getName().replace('.', '/');

        String finalPath = basePart + "/" + getResourcePrefix() + "/" + slashClassName + getClassSuffix();
        finalPath = replaceDoubleSlash(finalPath);

        if ((new File(finalPath)).exists()) {
            return finalPath;
        } else {
            String newPath = basePart + "/" + getResourcePrefix() + "/" + nomalnizeClassPath(slashClassName)
                             + getClassSuffix();
            return replaceDoubleSlash(newPath);
        }
    }

    protected String replaceDoubleSlash(String path) {
        while (path.indexOf("//") >= 0) {
            path = path.replace("//", "/");
        }
        return path;
    }

    protected String nomalnizeClassPath(String path) {
        if (path == null) {
            return null;
        }

        String[] parts = path.split("[\\/]");
        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if ((part != null) && part.length() > 0) {
                newPath.append(part.substring(0, 1).toLowerCase() + part.substring(1));
            }

            if (i < (parts.length - 1)) {
                newPath.append("/");
            }
        }
        return newPath.toString();
    }

    protected String getRelativePath(DataReaderType dataType, String dataFileName) {
        if (dataFileName.startsWith(":")) {
            dataFileName = dataFileName.substring(1);
        } else {
            String methodName = getMethodName();
            if (dataFileName.indexOf('_') <= 0) {
                dataFileName = methodName + "_" + dataFileName;
            }
        }
        String defaultExt = DataReaderUtil.getDefaultExt(dataType);
        String relativePath = dataFileName;
        if (!relativePath.endsWith(defaultExt)) {
            relativePath = relativePath + defaultExt;
        }
        return relativePath;
    }

    protected String getMethodName() {
        Method method = ThreadContextCache.get(Method.class, BuiltInCacheKey.Method);
        PrepareAnnotationTool prepare = new PrepareAnnotationTool(method);
        if ((prepare != null) && (prepare.methodName().length() != 0)) {
            return prepare.methodName();
        } else {
            return ThreadContextCache.get(Method.class, BuiltInCacheKey.Method).getName();
        }
    }

    protected String getResourcePrefix() {
        TestCaseInfo info = this.getClass().getAnnotation(TestCaseInfo.class);
        return ((info == null) || (info.defaultPath())) ? "" : info.pathPrefix();
    }

    protected String getClassSuffix() {
        TestCaseInfo info = this.getClass().getAnnotation(TestCaseInfo.class);
        return ((info == null) || (info.defaultPath())) ? DEFAULT_CLASS_SUFFIX : info.classSuffix();
    }

    protected DataReaderType getDataReaderType(Method method) {
        PrepareAnnotationTool prepare = new PrepareAnnotationTool(method);
        return prepare.type();
    }

    protected boolean isNeedPrepare(Method method) {
        return new PrepareAnnotationTool(method).isPrepareEnable();
    }

    protected boolean isMockOn() {
        boolean isMockFlagOn = (this.getClass().getAnnotation(Mock.class) != null);
        // 如果已经设置系统MOCK则忽略Mock标志
        return isMockFlagOn;
    }

    protected boolean isDefaultRollback() {
        try {
            Field defaultRollbackField = AbstractTransactionalSpringContextTests.class.getDeclaredField("defaultRollback");
            defaultRollbackField.setAccessible(true);
            return ((Boolean) defaultRollbackField.get(this)).booleanValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ================================================================================
    // 复杂对象支持,是原来memorydatabase的补充，可与memorydatabase一起使用
    protected TreeDatabase getTreeDB() {
        return getTreeDB(BuiltInCacheKey.Prepare.getValue());
    }

    protected TreeDatabase getTreeDB(String dataFileName) {
        return getTreeDB(dataFileName, true);
    }

    protected TreeDatabase getTreeDB(String dataFileName, boolean checkFileExists) {
        String relativePath = getRelativePath(DataReaderType.TreeXml, dataFileName);
        try {
            TreeDatabase database = (TreeDatabase) DataReaderUtil.readData(DataReaderType.TreeXml, relativePath);
            return database;
        } catch (ResourceNotFoundException e) {
            if (checkFileExists) {
                throw e;
            } else {
                return null;
            }
        }
    }

    /**
     * 单个复杂对象数据准备
     * 
     * @param database 默认调用getTreeDB()
     * @param objectName TreeObject.name
     * @return
     */
    protected static Object prepareObject(TreeDatabase database, String objectName) {
        List<?> treeObjectList = prepareObjectList(database, objectName);
        if (treeObjectList != null && treeObjectList.size() > 0) {
            return treeObjectList.get(0);
        }
        return null;
    }

    /**
     * 多个复杂对象数据准备
     * 
     * @param database 默认调用getTreeDB()
     * @param objectName TreeObject.name
     * @return
     */
    protected static List<?> prepareObjectList(TreeDatabase database, String objectName) {
        TreeObject treeTable = database.getObject(objectName);
        if (treeTable != null) {
            return treeTable.getTreeObjectList();
        }
        return null;
    }

    /**
     * <strong><font color=red> NEW FEATURE！</font></strong><code>测试两个对象是否相等<br>
     * 
     * <pre>
     * PersonProfile{
     * String firstName;
     * String lastName;
     * Date gmtCreate;
     * List&lt;Integer&gt; list;
     * };
     * Person{
     * Integer id;
     * Date gmtModified;
     * PersonProfile personProfile;
     * };
     * </pre>
     * 
     * @param bean 需要和result文件去比较的对象
     * @param objectName result文件里的对象名称
     * @param properties 属性列表: 对于例子中的两个Person对象的话:
     * {"id","personProfile.gmtCreate","personProfile.list"}就可以去比较了（当然，需要指定是inclusive还是exclusive的方式）！
     * @param exclusive true表示<strong><font color=red>不比较</font></strong><code>properties</code>
     * 列表中的属性；false表示<strong><font color=red>只会比较</font></strong> <code>properties</code>列表中的属性
     */
    protected void assertTreeObjectResult(Object bean, String objectName, String[] properties, boolean exclusive) {
        TreeObjectAssert.assertResult(getTreeDB(BuiltInCacheKey.Result.getValue()), bean, objectName, properties,
                                      exclusive);
    }

    /**
     * <strong><font color=red> NEW FEATURE！</font></strong><code>测试两个对象是否相等<br>
     * 
     * <pre>
     * PersonProfile{
     *  String          firstName;
     *  String          lastName;
     *  Date gmtCreate;
     *  List&lt;Integer&gt; list; 
     * };
     * Person{
     *  Integer id;
     *  Date gmtModified;
     *  PersonProfile   personProfile;
     * };
     * </pre>
     * 
     * @param beanlist 需要和result文件去比较的对象列表
     * @param objectName result文件里的对象名称
     * @param properties 属性列表: 对于例子中的两个Person对象的话:
     * {"id","personProfile.gmtCreate","personProfile.list"}就可以去比较了（当然，需要指定是inclusive还是exclusive的方式）！
     * @param exclusive true表示<strong><font color=red>不比较</font></strong><code>properties</code>
     * 列表中的属性；false表示<strong><font color=red>只会比较</font></strong> <code>properties</code>列表中的属性
     */

    protected void assertTreeObjectResultList(List<?> beanList, String objectName, String[] properties,
                                              boolean exclusive) {
        TreeObjectAssert.assertResultList(getTreeDB(BuiltInCacheKey.Result.getValue()), beanList, objectName,
                                          properties, exclusive);

    }

    /**
     * <strong><font color=red> NEW FEATURE！</font></strong><code>测试两个对象是否相等<br>
     * 
     * <pre>
     * PersonProfile{
     *  String          firstName;
     *  String          lastName;
     *  Date gmtCreate;
     *  List&lt;Integer&gt; list; 
     * };
     * Person{
     *  Integer id;
     *  Date gmtModified;
     *  PersonProfile   personProfile;
     * };
     * </pre>
     * 
     * @param object1
     * @param object2
     * @param properties 属性列表: 对于例子中的两个Person对象的话:
     * {"id","personProfile.gmtCreate","personProfile.list"}就可以去比较了（当然，需要指定是inclusive还是exclusive的方式）！
     * @param exclusive true表示<strong><font color=red>不比较</font></strong><code>properties</code>
     * 列表中的属性；false表示<strong><font color=red>只会比较</font></strong> <code>properties</code>列表中的属性
     */
    protected void assertObjectEquals(Object object1, Object object2, String[] properties, boolean exclusive) {
        if (!JsonObjectUtils.isSameObject(object1, object2, properties, exclusive)) {
            throw new AssertException("Not equals!");
        }
    }
}
