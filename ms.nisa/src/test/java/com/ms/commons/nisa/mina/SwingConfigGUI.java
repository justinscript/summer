/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.mina;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ms.commons.nisa.impl.ActionEnum;
import com.ms.commons.nisa.impl.MinaMessage;
import com.ms.commons.nisa.info.CacheGroupInfo;
import com.ms.commons.nisa.info.CacheGroupInfo.EachClient;
import com.ms.commons.nisa.info.ClientInfo;
import com.ms.commons.nisa.info.NotifyInfo;
import com.ms.commons.nisa.mina.server.IoSessionInfo;
import com.ms.commons.nisa.mina.server.MinaCallback;
import com.ms.commons.nisa.mina.server.MinaServer;

/**
 * 测试使用的GUI
 * 
 * @author zxc Apr 12, 2013 6:53:58 PM
 */
@SuppressWarnings("serial")
public class SwingConfigGUI extends JFrame implements MinaCallback {

    private JTextField ipField;
    private JTextField kvField;
    private JTextArea  area;
    private MinaServer minaServer;
    public static int  PORT = 5991;

    public SwingConfigGUI() {
        setTitle("配置中心简化界面");
        JPanel toolbarPanel = new JPanel();
        JButton b1 = new JButton(new ShowRegistAction());
        b1.setText("显示注册客户端");
        toolbarPanel.add(b1);

        JButton b2 = new JButton(new ClearAction());
        b2.setText("清空界面");
        toolbarPanel.add(b2);

        JButton b3 = new JButton(new QuitAction());
        b3.setText("关闭对话盒");
        toolbarPanel.add(b3);

        area = new JTextArea(20, 70);
        area.setLineWrap(true);
        area.setSize(500, 500);
        JScrollPane scrollPanel = new JScrollPane(area);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(null);
        JLabel lab1 = new JLabel("ip_应用名");
        bottomPanel.add(lab1);
        ipField = new JTextField("192.160.70.124_ganges");
        kvField = new JTextField("key=value");
        bottomPanel.add(ipField);
        bottomPanel.add(kvField);
        JButton broadBtn = new JButton(new BroadcastAction());
        broadBtn.setText("通知");
        bottomPanel.add(broadBtn);
        int y = 1, h = 25;
        lab1.setBounds(3, y, 60, h);
        ipField.setBounds(lab1.getX() + lab1.getWidth(), y, 200, h);
        kvField.setBounds(ipField.getX() + ipField.getWidth(), y, 500, h);
        broadBtn.setBounds(kvField.getX() + kvField.getWidth(), y, 70, h);
        bottomPanel.setPreferredSize(new Dimension(40, 30));
        getContentPane().add(toolbarPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPanel);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
        setVisible(true);

        minaServer = new MinaServer();
        try {
            minaServer.start(this, PORT);
        } catch (Exception w) {
            w.printStackTrace();
        }
    }

    public class ShowRegistAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            List<IoSessionInfo> list = minaServer.getRegistClientList();
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            sb.append("\r\n－－－－－显示当前正在服务的Client－－－－－\r\n");
            for (IoSessionInfo io : list) {
                ClientInfo ckey = io.getClientInfo();
                sb.append(ckey.getIp()).append(" ").append(ckey.getAppName());
                sb.append(" 注册时间=").append(sf.format(new Date(io.getRegistTime())));
                sb.append(" 运行时间=").append((System.currentTimeMillis() - io.getRegistTime()) / 1000).append("秒");
                sb.append(" 上次心跳检测时间=").append((System.currentTimeMillis() - io.getLastHeartbeatTime()) / 1000).append("秒");
                sb.append("\r\n");
            }
            sb.append("\r\n－－－－－显示已经注册的GroupCache－－－－－");
            ConcurrentHashMap<String, CacheGroupInfo> cacheMap = minaServer.getMinaServerHandler().getCacheGroupMap();
            Iterator<String> ir = cacheMap.keySet().iterator();
            while (ir.hasNext()) {
                String groupName = ir.next();
                sb.append("\r\n\r\ngroupName＝").append(groupName);
                CacheGroupInfo tempCacheGroup = cacheMap.get(groupName);
                List<NotifyInfo> tmpHistoryList = tempCacheGroup.getHistoryList();
                for (NotifyInfo tmp : tmpHistoryList) {
                    sb.append("\r\n\t history=(").append(tmp).append(")");
                }
                List<EachClient> tmpList = tempCacheGroup.getGroupList();
                for (EachClient tmp : tmpList) {
                    sb.append("\r\n\t(").append(tmp.getClient()).append(") updateTime=");
                    sb.append((tmp.getUpdateTime() == null) ? "还没有更新" : new Date(tmp.getUpdateTime()));
                }
            }
            notifyAction(sb);
        }
    }

    private class ClearAction extends AbstractAction {

        private static final long serialVersionUID = 1655297424639924560L;

        public void actionPerformed(ActionEvent e) {
            area.setText("");
        }
    }

    private class BroadcastAction extends AbstractAction {

        /**
         */
        private static final long serialVersionUID = -6276019615521905411L;

        public void actionPerformed(ActionEvent e) {
            String s1 = ipField.getText().trim();
            int index = s1.lastIndexOf("_");
            if (index == -1) {
                notifyAction("错误！IP中不包含有“_”。正确的写法是IP_AppName 例如：192.168.1.1_ganges");
                return;
            }
            String ip = s1.substring(0, index);
            String appName = s1.substring(index + 1);

            String s2 = kvField.getText().trim();
            index = s2.lastIndexOf("=");
            if (index == -1) {
                notifyAction("错误！KV中不包含有“=”。正确的写法是Key=Value 例如：debug=flase");
                return;
            }
            String key = s2.substring(0, index);
            String value = s2.substring(index + 1);
            MinaMessage minaMessage = new MinaMessage(new ClientInfo(ip, "msun", appName, "dev"));
            minaMessage.setAction(ActionEnum.SERVER_SEND_MESSAGE);
            minaMessage.putKV(key, value);
            minaServer.push(minaMessage);
        }
    }

    private class QuitAction extends AbstractAction {

        private static final long serialVersionUID = -6389802816912005370L;

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public Object notifyAction(Object obj) {
        if (obj != null) {
            area.append(obj.toString() + "\r\n");
            int len = area.getText().length();
            area.setCaretPosition(len);
        }
        return null;
    }

    public static void main(String[] args) {

        new SwingConfigGUI();

        // ConfigServiceLocator.getCongfigService();

        // Object obj = CommonServiceLocator.getBean("userService");
        // if (obj instanceof UserService)
        // {
        // DepartmentDO dd = new DepartmentDO();
        // dd.setName("2222");
        // dd.setShortName("eee");
        // ((UserService)obj).insertDepartment(dd);
        // }
        // obj = CommonServiceLocator.getBean("userDao");
        // if (obj instanceof UserDao)
        // {
        // List a = ((UserDao)obj).listDepartment(null);
        // System.out.println(a.size());
        // }
    }
}
