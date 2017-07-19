/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.mina.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.nisa.impl.CacheRegistServiceImpl;
import com.ms.commons.nisa.impl.MinaMessage;
import com.ms.commons.nisa.info.ClientInfo;
import com.ms.commons.nisa.interfaces.CacheRegistService;
import com.ms.commons.nisa.service.CacheRegistServiceLocator;
import com.ms.commons.utilities.CoreUtilities;

/**
 * @author zxc Apr 12, 2013 6:48:01 PM
 */
public class MinaClient {

    private ExpandLogger      log = LoggerFactoryWrapper.getLogger(MinaClient.class);

    private String            clientProject;
    private String            clientApp;
    private String            configType;
    private String            serverIp;
    private int               serverPort;
    private MinaClientHandler minaClientHandler;

    /**
     * 用Mina链接服务器，并且建立一个长链接
     * 
     * @param configType 配置项版本。例如:Dev,Test,Run
     */
    public void start(String clientProject, String clientApp, String configType, String serverIp, int serverPort,
                      boolean wait) throws IOException {
        this.clientProject = clientProject;
        this.clientApp = clientApp;
        this.configType = configType;
        this.serverIp = serverIp;
        this.serverPort = serverPort;

        minaClientHandler = connectServerByMina(clientProject, clientApp, configType, serverIp, serverPort, wait);

        final Runnable updateThread = new Runnable() {

            public void run() {
                checkServer();
            }
        };
        final ScheduledExecutorService updateScheduler = Executors.newScheduledThreadPool(1);
        int period = 60;
        updateScheduler.scheduleAtFixedRate(updateThread, period, period, TimeUnit.SECONDS);
    }

    private MinaClientHandler connectServerByMina(String clientProject, String clientApp, String configType,
                                                  String serverIp, int serverPort, boolean wait) {
        // System.out.println("开始机器连续服务器");
        // 创建TCP/IP的连接
        NioSocketConnector connector = new NioSocketConnector();
        // 创建接收数据的过滤器
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        /*
         * 这里注意点: 1:TextLineCodecFactory设置这个过滤器一行一行(/r/n)的发送/读取数据
         * 2.ObjectSerializationCodecFactory一般发送/接收的是对象等形象,以对象形式读取
         */
        // chain.addLast("myChain",new ProtocolCodecFilter(new
        // TextLineCodecFactory()));
        chain.addLast("myChain", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        String ipHostName = CoreUtilities.getIPAddress() + "-" + CoreUtilities.getHostName();
        MinaMessage msg = new MinaMessage(new ClientInfo(ipHostName, clientProject, clientApp, configType));
        // 设置处理的类
        MinaClientHandler clientHandler = new MinaClientHandler(msg);
        connector.setHandler(clientHandler);
        // 设置时间
        connector.setConnectTimeoutMillis(300000);
        // 开始连接服务器
        ConnectFuture cf = connector.connect(new InetSocketAddress(serverIp, serverPort));

        // 等待连接结束
        if (wait) {
            cf.awaitUninterruptibly();
            cf.getSession().getCloseFuture().awaitUninterruptibly();
            connector.dispose();
        }
        return clientHandler;
    }

    /**
     * 检查服务器端是否存在
     */
    @SuppressWarnings("static-access")
    private void checkServer() {

        if (minaClientHandler != null) {
            try {
                minaClientHandler.sentHeartbeatMessage();
                Thread.sleep(5000); // 暂停5S
            } catch (Exception e) {
                e.printStackTrace();
            }
            long lastHeartbeatTime = minaClientHandler.getLastHeartbeatTime();
            if (System.currentTimeMillis() - lastHeartbeatTime >= 1000 * 60 * 3) // 超过3分钟没有反应，此时认为有
            {
                boolean isConnected = false;
                try {
                    isConnected = isConnectedServer();
                } catch (Exception e) {
                }
                if (isConnected) // 重新链接Server端
                {
                    minaClientHandler = connectServerByMina(clientProject, clientApp, configType, serverIp, serverPort,
                                                            false);
                    while (!minaClientHandler.isConnected()) {
                        log.info("重新链接Nisa服务，并且等待连接.... sleep 1s! ");
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    CacheRegistService cacheRegistService = CacheRegistServiceLocator.getCacheRegistService();
                    if (cacheRegistService instanceof CacheRegistServiceImpl) {
                        ((CacheRegistServiceImpl) cacheRegistService).registCacheUpdateListener();
                    }

                }
            } else {
                // System.out.println("it is ok!");
            }
        }
    }

    /**
     * 判断服务器端是否可用
     * 
     * @return
     * @throws Exception
     */
    private boolean isConnectedServer() throws Exception {
        Socket socket = new Socket(serverIp, serverPort);
        return socket.isConnected();
    }

    /**
     * 把CacheRegistService和MinaClientHandler做关联
     * 
     * @param cacheService
     */
    // public void registCacheRegistService(CacheRegistServiceImpl cacheRegistService)
    // {
    // if (minaClientHandler!=null)
    // {
    // minaClientHandler.setCacheRegistService(cacheRegistService);
    // cacheRegistService.setMinaClient(this);
    // }
    // }

    public MinaClientHandler getMinaClientHandler() {
        return minaClientHandler;
    }
}
