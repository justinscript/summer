/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.interfaces;

import java.io.Serializable;
import java.util.Map;

import com.ms.commons.udas.impl.UdasObj;

/**
 * UDAS服务接口
 * 
 * @author zxc Apr 12, 2013 5:31:36 PM
 */
public interface UdasService {

    /**
     * 通过Key来获取一个序列化数据
     * 
     * @param key
     * @return 如果数据不存在则返回<code>null</code>
     */
    Serializable getKV(String key);

    /**
     * 通过Key来获取一个序列化数据<br>
     * 如果返回的<code>Serializable</code>超过原先设定的过期时间，那么就会返回<code>null</code>
     * 
     * @param key
     * @param expireTimeSecond 过期时间，单位秒。 如果为null就不检查,如果是0表示永不过期，也不检查
     * @return 如果数据不存在则返回<code>null</code>
     */
    Serializable getKV(String key, Integer expireTimeSecond);

    /**
     * 通过批量的key来获取批量的value
     * 
     * @param keys
     * @return
     */
    Map<String, Serializable> getBulkKV(String... keys);

    /**
     * 保存KV类型的数据.其中value就是在{@link UdasObj #getValue()}返回的值。
     * 
     * @param key KV类型的Key
     * @param udasObj 将KV类型数据的Value包装为UdasObj
     */
    void put(String key, UdasObj udasObj);

    /**
     * 保存KV类型的数据.同时给数据设置一个过期时间
     * 
     * @param key KV类型的Key
     * @param expireTimeinSecondes 数据的过期时间，以秒作为单位。即该数据再多少秒以后失效.不能小于0
     * @param udasObj 将KV类型数据的Value包装为UdasObj
     * @thows RuntimeException 如果expireTimeinSecondes小于0
     */
    void put(String key, int expireTimeinSecondes, UdasObj udasObj);

    /**
     * 通过一个Key来删除一数据。
     * 
     * <pre>
     * 根据具体数据源实例的不同，有的数据源是不会清除数据的，只是将该Key对应的值赋位空
     * </pre>
     * 
     * @param key 要删除数据的Key
     */
    void del(String key);

    /**
     * 当JVM关闭或者Spring关闭时调用，目的处理一些后续事务。例如：BDB关闭时，把缓存的东西刷入内存。 目前在Spring的destroy-method=close()中调用
     * 
     * @return
     */
    boolean close();
}
