/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.security.xss.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ms.commons.fasttext.security.xss.Policy;
import com.ms.commons.fasttext.security.xss.PolicyException;
import com.ms.commons.fasttext.security.xss.XssXppScanner;

/**
 * @author zxc Apr 12, 2013 3:26:47 PM
 */
public class MainUI extends JFrame {

    private static final long serialVersionUID = 4852051150406340218L;
    JTextArea                 input, output;
    JButton                   ok, cancel, clear;
    Policy                    p;
    XssXppScanner             scanner;

    public MainUI() throws PolicyException {
        setTitle("XSS validate tools");
        setBounds(50, 50, 800, 600);
        init();
        this.pack();
        p = Policy.getStrictPolicyInstance();
        scanner = new XssXppScanner(p);
    }

    private void init() {
        JPanel root = new JPanel(new BorderLayout());
        input = new JTextArea();
        output = new JTextArea();
        input.setLineWrap(true);
        output.setLineWrap(true);
        input.setRows(10);
        input.setColumns(80);
        output.setRows(10);
        output.setColumns(80);
        ok = new JButton("OK");
        cancel = new JButton("Cancel");
        clear = new JButton("Clear");
        output.setEditable(false);
        JPanel up = new JPanel();
        up.add(new JScrollPane(input, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        JPanel center = new JPanel();
        center.add(new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                   JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        JPanel down = new JPanel();
        JPanel butPan = new JPanel(new FlowLayout(0));
        butPan.add(clear);
        butPan.add(ok);
        butPan.add(cancel);

        down.add(butPan);
        root.add(up, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(down, BorderLayout.SOUTH);
        this.getContentPane().add(root);
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String html = input.getText();
                String s = scanner.scan(html);
                if (s != null) {
                    if (s.length() == 0) {
                        output.setText("all html was removed");
                    } else {
                        output.setText(s);
                    }
                } else {
                    output.setText("code bug, contact me:)");
                }
            }
        });
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        clear.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                input.setText("");
                output.setText("");
            }
        });
        up.setOpaque(false);
        center.setOpaque(false);
        down.setOpaque(false);
        root.setBackground(Color.BLACK);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            MainUI frame = new MainUI();
            frame.setVisible(true);
        } catch (PolicyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
