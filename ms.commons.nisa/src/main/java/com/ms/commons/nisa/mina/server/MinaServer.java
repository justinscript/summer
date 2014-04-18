/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.mina.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.ms.commons.nisa.impl.MinaMessage;
import com.ms.commons.nisa.service.ConfiginfoService;

/**
 * Mina服务的启动
 * 
 * @author zxc Apr 12, 2013 6:46:29 PM
 */
public class MinaServer {

    private int               port = 8891;
    private MinaServerHandler minaServerHandler;
    private MinaCallback      callBack;
    private ConfiginfoService infoService;

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setCallBack(MinaCallback callBack) {
        this.callBack = callBack;
    }

    public void setInfoService(ConfiginfoService infoService) {
        this.infoService = infoService;
    }

    public void init() {
        try {
            start(callBack, port);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
	 */
    public void start(MinaCallback callBack, int serverPort) throws IOException {
        // System.out.println("服务创建中");
        // 创建一个非阻塞的的server端socket,用NIO
        SocketAcceptor acceptor = new NioSocketAcceptor();
        // 创建接收数据的过滤器
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        //
        // 这里注意点:
        // 1: TextLineCodecFactory设置这个过滤器一行一行(/r/n)的读取数据
        // 2: ObjectSerializationCodecFactory一般接收的是对象等形象,以对象形式读取
        // chain.addLast("chain", new ProtocolCodecFilter(new TextLineCodecFactory()));

        chain.addLast("chain", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        // 设定服务器端消息处理器.:就是我们创建的TimeServerHandler对象
        minaServerHandler = new MinaServerHandler(callBack, infoService);
        acceptor.setHandler(minaServerHandler);
        acceptor.setReuseAddress(true);
        acceptor.bind(new InetSocketAddress(serverPort));
        // System.out.println("MINS 服务器监听的服务端口为" + serverPort);

        final Runnable updateThread = new Runnable() {

            public void run() {
                minaServerHandler.checkClient();
            }
        };
        final ScheduledExecutorService updateScheduler = Executors.newScheduledThreadPool(1);
        int period = 60;
        updateScheduler.scheduleAtFixedRate(updateThread, period, period, TimeUnit.SECONDS);
    }

    public List<IoSessionInfo> getRegistClientList() {
        return minaServerHandler.getRegistClientList();
    }

    /**
     * 外界主动要求Server端通知所有Client
     * 
     * @param minaMessage
     * @return
     */
    public boolean push(MinaMessage minaMessage) {
        return minaServerHandler.push(minaMessage);
    }

    public MinaServerHandler getMinaServerHandler() {
        return minaServerHandler;
    }
}
