/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import java.util.Iterator;

/**
 * YamlStream is nothing but an Iterator that is also an Iterable. It allows for both of the below types of stream
 * access:
 * 
 * <pre>
 * for (Object obj : Yaml.loadStream(yamlText)) {
 *     // do something with obj
 * }
 * </pre>
 * 
 * or
 * 
 * <pre>
 * Iterator iterator = Yaml.loadStream(yamlText);
 * while (iterator.hasNext()) {
 *     Object obj = iterator.next();
 *     // do something with obj
 * }
 * </pre>
 * 
 * @param <T> the type which the iterator yields
 * @author zxc Apr 14, 2013 12:33:44 AM
 */
public interface YamlStream<T> extends Iterable<T>, Iterator<T> {
}
