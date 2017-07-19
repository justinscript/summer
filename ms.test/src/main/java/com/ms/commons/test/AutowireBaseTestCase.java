/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test;

// import java.util.Locale;
//
// import junit.framework.TestCase;
//
// import org.junit.Before;
// import org.junit.runner.RunWith;
// import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
//
// import com.ms.commons.commons.service.SingletonServiceManagerLoader;
// import com.ms.commons.test.annotation.TestCaseInfo;
// import com.ms.commons.test.classloader.IntlTestURLClassPath;
// import com.ms.commons.test.integration.jmockit.internal.JMockItUtil;
// import com.ms.commons.test.integration.junit4.internal.IntlTestBlockJUnit4ClassRunner;
// import com.ms.service.ServiceManager;
// import com.ms.service.resource.ResourceLoaderService;
// import com.ms.service.spring.BeanFactoryService;

/**
 * @author zxc Apr 13, 2013 11:16:50 PM
 */
// @RunWith(IntlTestBlockJUnit4ClassRunner.class)
public abstract class AutowireBaseTestCase extends AbstractDependencyInjectionSpringContextTests {

    // static {
    // JMockItUtil.startUpJMockItIfPossible();
    // JMockItUtil.mockUpDecorators();
    //
    // IntlTestURLClassPath.initIntlTestURLClassLoader();
    // }
    //
    // protected static volatile boolean firstTimeRan = false;
    // protected static ServiceManager manager;
    // protected static ResourceLoaderService resourceLoaderService;
    // static {
    // long BEGIN_TIME = System.currentTimeMillis();
    //
    // Locale.setDefault(Locale.ENGLISH);
    // System.setProperty("ark.trace.sql.stack", "false");
    // System.setProperty("ark.trace.sql", "SUDI");
    // // System.setProperty("com.ms.ark.trace.TraceHandler", "com.ms.ark.trace.LoggingTraceHandler");
    //
    // manager = SingletonServiceManagerLoader.getInstance("classpath*:/biz/*_services_locator.xml");
    // resourceLoaderService = (ResourceLoaderService) manager.getService(ResourceLoaderService.SERVICE_NAME);
    //
    // System.err.println("TEST STATIC INIT  TIME " + (System.currentTimeMillis() - BEGIN_TIME));
    // }
    //
    // protected static ResourceLoaderService getResourceLoaderService() {
    // return resourceLoaderService;
    // }
    //
    // protected Object getService(String name) {
    // return applicationContext.getBean(name);
    // }
    //
    // @Override
    // protected Object contextKey() {
    // TestCaseInfo testCaseInfo = this.getClass().getAnnotation(TestCaseInfo.class);
    // if (!testCaseInfo.autoWire()) {
    // return null;
    // }
    // // 需要AutoWire则ContextKey
    // if ((testCaseInfo != null) && (testCaseInfo.contextKey() != null) && (testCaseInfo.contextKey().length() != 0)) {
    // return testCaseInfo.contextKey();
    // } else {
    // // 使�framework-biz-datasource中的ServicesLocator
    // if (testCaseInfo.useDataSourceContextKey()) {
    // return "DatasourceServicesLocator";
    // }
    // throw new RuntimeException(
    // "You SHOULD override contextKey() or add TestCaseInfo to the test class or set autoWire false or add useDataSourceContextKey=true.");
    // }
    // }
    //
    // /**
    // * 从service manager中装载spring容器。
    // */
    // @Override
    // protected ConfigurableApplicationContext loadContext(Object key) throws Exception {
    // BeanFactoryService beanFactory = (BeanFactoryService) manager.getService((String) key);
    // return (ConfigurableApplicationContext) beanFactory.getBeanFactory();
    // }
    //
    // /**
    // * 设置测试类注入的模式。
    // */
    // @Override
    // protected void prepareTestInstance() throws Exception {
    // TestCaseInfo testCaseInfo = this.getClass().getAnnotation(TestCaseInfo.class);
    //
    // this.setDependencyCheck(false);
    //
    // // 依赖注入方式
    // if (testCaseInfo.autoWire()) {
    // this.setAutowireMode(testCaseInfo.autoWireMode());
    // } else {
    // this.setAutowireMode(AUTOWIRE_NO);
    // }
    //
    // // 初始化jdbcExtractor
    // if ((testCaseInfo == null) || testCaseInfo.useJdbcExtractor()) {
    // this.applicationContext.getBean("jdbcExtractor");
    // }
    // super.prepareTestInstance();
    // }
    //
    // @Before
    // public void before() throws Exception {
    // TestCaseInfo testCaseInfo = this.getClass().getAnnotation(TestCaseInfo.class);
    // if (testCaseInfo.autoWire()) {
    // this.applicationContext = getContext(contextKey());
    // prepareTestInstance();
    // }
    // onSetUp();
    //
    // if (!firstTimeRan) {
    // // 仅在第一次运行
    // firstTimeRan = true;
    // runOnceBeforeAll();
    // }
    // } // ================================================================================
    //
    // /**
    // * 在所有单元测试先于所有测试函数运行
    // */
    // protected void runOnceBeforeAll() {
    // }
    //
    // // ================================================================================
    //
    // /**
    // * 当前被测试的单元测试
    // */
    // protected Class<?> clazz() {
    // TestCaseInfo tci = this.getClass().getAnnotation(TestCaseInfo.class);
    // if ((tci == null) || (tci.testFor() == TestCase.class)) {
    // String testClassName = this.getClass().getName();
    // if (testClassName.endsWith("Test")) {
    // try {
    // return Class.forName(testClassName.substring(0, testClassName.length() - 4));
    // } catch (ClassNotFoundException e) {
    // System.err.println("Load class failed: " + testClassName.substring(0, testClassName.length() - 4));
    // }
    // }
    // throw new RuntimeException("No TestCaseInfo or not assigned For class.");
    // }
    // return tci.testFor();
    // }
}
