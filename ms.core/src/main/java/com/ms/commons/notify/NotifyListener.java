/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.notify;

import com.ms.commons.notify.event.EventConfig;

/**
 * 具体事件配置接口,这个接口没有方法，是通过Annotation做的，{@link EventConfig}
 * 
 * <pre>
 * // 注册监听器（一般情况下在类的Static块中注册时间，注意捕获异常啊！）
 * NotifyService.regist(new NotifyListener() {
 * 
 *     // 方法上配置出您需要关顾的事件。多个事件用逗号连接。
 *     &#064;EventConfig(events = { EventType.itemAdd, EventType.itemDelete, EventType.itemDownshelf, EventType.itemUpshelf })
 *     public void itemEvent(ItemEvent itemEvent) {
 *         Integer storeId = itemEvent.getStoreId();// 时间的storeId
 *         EventType eventType = itemEvent.getEventType();// 事件类型
 *         List&lt;Long&gt; numIid = itemEvent.getData();// numiid 列表。
 * 
 *         // 这里去写您的业务代码就好了。
 * 
 *     }
 * 
 * });
 * 
 * </pre>
 * 
 * @author zxc Apr 12, 2013 2:57:28 PM
 */

public interface NotifyListener {

}
