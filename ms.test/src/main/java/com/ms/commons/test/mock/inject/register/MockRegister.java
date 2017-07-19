/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.inject.register;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ms.commons.test.mock.MockContext;
import com.ms.commons.test.mock.MockPair;

/**
 * @author zxc Apr 14, 2013 12:13:18 AM
 */
public class MockRegister {

    private static final Object                         lock                        = new Object();

    private static final Map<MockClassKey, MockContext> classNameMockContextMap     = new HashMap<MockClassKey, MockContext>();
    private static final Map<MockClassKey, MockContext> fullClassNameMockContextMap = new HashMap<MockClassKey, MockContext>();
    private static final Map<MockClassKey, MockContext> classMockContextMap         = new HashMap<MockClassKey, MockContext>();

    private static final Map<String, MockContext>       mehtodMockContextMap        = new HashMap<String, MockContext>();

    public static void clear() {
        synchronized (lock) {
            mehtodMockContextMap.clear();
            classMockContextMap.clear();
            fullClassNameMockContextMap.clear();
            classNameMockContextMap.clear();
        }
    }

    public static void register(Class<?> clazz, String method, MockPair mockPair) {
        synchronized (lock) {
            MockClassKey key = new MockClassKey(clazz, method);
            MockContext mockContext = classMockContextMap.get(key);
            if (mockContext == null) {
                List<MockContext> mockContextList = listNotLinkedMockContextList();

                List<MockContext> matchedResultList = matchMutipleMockContexts(mockContextList, clazz, method,
                                                                               classMatcher);

                if (matchedResultList.size() == 0) {
                    mockContext = new MockContext(clazz, null, false);
                    classMockContextMap.put(key, mockContext);
                } else {
                    mockContext = matchedResultList.get(0);
                    classMockContextMap.put(key, mockContext);
                }
            }
            mockContext.addMockPair(mockPair);
        }
    }

    public static void register(String clazz, String method, MockPair mockPair) {
        synchronized (lock) {
            MockClassKey key = new MockClassKey(clazz, method);
            if (clazz.indexOf('.') >= 0) {
                MockContext mockContext = fullClassNameMockContextMap.get(key);
                if (mockContext == null) {

                    List<MockContext> mockContextList = listNotLinkedMockContextList();

                    List<MockContext> matchedResultList = matchMutipleMockContexts(mockContextList, clazz, method,
                                                                                   fullClassNameMatcher);

                    if (matchedResultList.size() == 0) {
                        mockContext = new MockContext(null, null, false);
                        fullClassNameMockContextMap.put(key, mockContext);
                    } else {
                        mockContext = matchedResultList.get(0);
                        classMockContextMap.put(key, mockContext);
                    }
                }
                mockContext.addMockPair(mockPair);
            } else {
                MockContext mockContext = classNameMockContextMap.get(key);
                if (mockContext == null) {

                    List<MockContext> mockContextList = listNotLinkedMockContextList();

                    List<MockContext> simpleNamematchedResultList = matchMutipleMockContexts(mockContextList, clazz,
                                                                                             method,
                                                                                             simpleClassNameMatcher);

                    checkDuplicatedSimpleClasses(simpleNamematchedResultList);

                    List<MockContext> matchedResultList = matchMutipleMockContexts(mockContextList, clazz, method,
                                                                                   classNameMatcher);

                    if (matchedResultList.size() == 0) {
                        mockContext = new MockContext(null, null, false);
                        classNameMockContextMap.put(key, mockContext);
                    } else {
                        mockContext = matchedResultList.get(0);
                        classMockContextMap.put(key, mockContext);
                    }
                }
                mockContext.addMockPair(mockPair);
            }
        }
    }

    public static MockContext getMockContext(Class<?> clazz, String method) {
        return getMockContext(new MockClassKey(clazz, method));
    }

    public static MockContext getMockContext(String clazz, String method) {
        return getMockContext(new MockClassKey(clazz, method));
    }

    public static MockContext getMockContext(MockClassKey key) {
        synchronized (lock) {
            {
                MockContext context = classMockContextMap.get(key);
                if (context != null) {
                    return context;
                }
            }
            {
                MockContext context = fullClassNameMockContextMap.get(key);
                if (context != null) {
                    return context;
                }
            }
            {
                MockContext context = classNameMockContextMap.get(key);
                if (context != null) {
                    return context;
                }
            }
            return null;
        }
    }

    public static MockContext getMockContext(Method method) {
        synchronized (lock) {
            {
                MockContext context = mehtodMockContextMap.get(getFullMethodName(method));
                if (context != null) {
                    return context;
                }
            }

            Class<?> clazz = method.getDeclaringClass();
            {
                MockContext context = classMockContextMap.get(new MockClassKey(clazz, method.getName()));
                if (context != null) {
                    context.setClazz(method.getDeclaringClass());
                    context.setMethod(method.getName());
                    context.setLinked(true);

                    mehtodMockContextMap.put(getFullMethodName(method), context);
                    return context;
                }
            }
            {
                MockContext context = fullClassNameMockContextMap.get(new MockClassKey(clazz.getName(),
                                                                                       method.getName()));
                if (context != null) {
                    context.setClazz(method.getDeclaringClass());
                    context.setMethod(method.getName());
                    context.setLinked(true);

                    mehtodMockContextMap.put(getFullMethodName(method), context);
                    return context;
                }
            }
            {
                MockContext context = classNameMockContextMap.get(new MockClassKey(clazz.getSimpleName(),
                                                                                   method.getName()));
                if (context != null) {
                    if ((context.getClazz() != null) && (context.getClazz() != clazz)) {
                        throw new RuntimeException("Two class `" + context.getClazz().getName() + "` and `"
                                                   + clazz.getName()
                                                   + "` with same simple name (and this name was used) detected.");
                    }

                    context.setClazz(method.getDeclaringClass());
                    context.setMethod(method.getName());
                    context.setLinked(true);

                    mehtodMockContextMap.put(getFullMethodName(method), context);
                    return context;
                }
            }
            {
                MockContext context = new MockContext(method.getDeclaringClass(), method.getName(), false);
                mehtodMockContextMap.put(getFullMethodName(method), context);
                return context;
            }
        }
    }

    protected static String getFullMethodName(Method method) {
        return (method.getDeclaringClass().getName() + "#" + method.getName());
    }

    protected static List<MockContext> listNotLinkedMockContextList() {
        List<MockContext> notLinkedMockContextList = new ArrayList<MockContext>();
        Collection<MockContext> mockContextList = mehtodMockContextMap.values();
        for (MockContext mockContext : mockContextList) {
            if (!mockContext.isLinked()) {
                notLinkedMockContextList.add(mockContext);
            }
        }
        return notLinkedMockContextList;
    }

    protected static List<MockContext> matchMutipleMockContexts(List<MockContext> mockContextList, Object clazz,
                                                                String method, Matcher matcher) {
        List<MockContext> matchedMockContextList = new ArrayList<MockContext>();

        if (mockContextList != null) {
            for (MockContext mockContext : mockContextList) {
                if (matcher.match(mockContext, clazz, method)) {
                    matchedMockContextList.add(mockContext);

                    if (!matcher.mutipleMatch()) {
                        return matchedMockContextList;
                    }
                }
            }
        }

        return matchedMockContextList;
    }

    protected static void checkDuplicatedSimpleClasses(List<MockContext> simpleNamematchedResultList) {
        if (simpleNamematchedResultList.size() > 1) {
            Map<String, Class<?>> classNameMap = new HashMap<String, Class<?>>();
            for (MockContext context : simpleNamematchedResultList) {
                String simpleName = context.getClazz().getSimpleName();
                if (classNameMap.get(simpleName) == null) {
                    classNameMap.put(simpleName, context.getClazz());
                } else {
                    if (classNameMap.get(simpleName) != context.getClazz()) {
                        throw new RuntimeException("Two class `" + classNameMap.get(simpleName).getName() + "` and `"
                                                   + context.getClazz().getName()
                                                   + "` with same simple name (and this name was used) detected.");
                    }
                }
            }
        }
    }

    static interface Matcher {

        boolean mutipleMatch();

        boolean match(MockContext context, Object clazz, String method);
    }

    static Matcher classMatcher           = new Matcher() {

                                              public boolean match(MockContext context, Object clazz, String method) {
                                                  if (context.getClazz().equals(clazz)) {
                                                      if (context.getMethod().equals(method)) {
                                                          return true;
                                                      }
                                                  }
                                                  return false;
                                              }

                                              public boolean mutipleMatch() {
                                                  return false;
                                              }
                                          };

    static Matcher fullClassNameMatcher   = new Matcher() {

                                              public boolean match(MockContext context, Object clazz, String method) {
                                                  if (context.getClazz().getName().equals(clazz)) {
                                                      if (context.getMethod().equals(method)) {
                                                          return true;
                                                      }
                                                  }
                                                  return false;
                                              }

                                              public boolean mutipleMatch() {
                                                  return false;
                                              }
                                          };

    // ONLY CHECK class name
    static Matcher simpleClassNameMatcher = new Matcher() {

                                              public boolean match(MockContext context, Object clazz, String method) {
                                                  if (context.getClazz().getSimpleName().equals(clazz)) {
                                                      return true;
                                                  }
                                                  return false;
                                              }

                                              public boolean mutipleMatch() {
                                                  return true;
                                              }
                                          };

    static Matcher classNameMatcher       = new Matcher() {

                                              public boolean match(MockContext context, Object clazz, String method) {
                                                  if (context.getClazz().getSimpleName().equals(clazz)) {
                                                      if (context.getMethod().equals(method)) {
                                                          return true;
                                                      }
                                                  }
                                                  return false;
                                              }

                                              public boolean mutipleMatch() {
                                                  return false;
                                              }
                                          };
}
