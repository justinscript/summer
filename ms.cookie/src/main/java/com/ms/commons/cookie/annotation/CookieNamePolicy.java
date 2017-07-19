/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.cookie.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一组Cookie的配置。所有受管的Cookie的都是分组管理的，一个组的Cookie具有相同的
 * <ul>
 * <li>域</li>
 * <li>路径</li>
 * <li>加密策略</li>
 * <li>过期时间</li>
 * </ul>
 * 还有一个关键点是：他们采用一个Key进行保存。这样可以减少Cookie的个数。 另外对Cookie进行分组的原因是基于业务考虑，我们将相同业务作用的Cookie分配到统一的组内，方便程序使用。
 * 例如：我们将需要再登陆时更新，登出时清理掉的Cookie使用同一个组。
 * 
 * @author zxc Apr 12, 2014 7:37:15 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookieNamePolicy {

    CookieDomain domain();

    /**
     * cookie max age property
     */
    int maxAge() default CookieMaxAge.FOREVER;

    /**
     * 是否对Cookie值加密
     */
    boolean isEncrypt() default true;

    /**
     * cookie path property
     */
    CookiePath path() default CookiePath.ROOT;

    /**
     * 是否是单一的值，没有kev-value对的。注意，如果是，一定要设置为true
     */
    boolean isSimpleValue() default false;
}
