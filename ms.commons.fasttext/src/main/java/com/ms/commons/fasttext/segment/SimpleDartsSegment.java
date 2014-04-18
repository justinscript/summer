/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.segment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author zxc Apr 12, 2013 3:40:51 PM
 */
public class SimpleDartsSegment implements Serializable {

    private static final long serialVersionUID = -4815159987414538914L;
    private Set<Character>    foreign;                                 // 音译名
    private Set<Character>    numbers;                                 // 数字
    private Darts             darts;

    public SimpleDartsSegment(Darts dat) {
        this.darts = dat;
        try {
            this.load();
        } catch (IOException e) {
        }
    }

    public void load() throws IOException {
        foreign = new TreeSet<Character>();
        numbers = new TreeSet<Character>();
        loadset(numbers, "data/numbers_u8.txt");
        loadset(foreign, "data/foreign_u8.txt");
    }

    public List<WordTerm> getToken(char[] source) {
        int pos = 0;
        int len = source.length;
        List<WordTerm> list = new LinkedList<WordTerm>();
        while (pos < source.length) {

            WordTerm w = darts.prefixSearchMax(source, pos, len);
            if (w == null) {
                // is number?
                int count = 0;
                w = new WordTerm();
                w.begin = pos;
                while (pos + count < len && numbers.contains(source[pos + count])) {
                    count++;
                }
                if (count > 0) {
                    w.length = count;
                } else {
                    count = 0;
                    while (pos + count < len && foreign.contains(source[pos + count])) {
                        count++;
                    }
                    if (count > 0) {
                        w.length = count;
                    }
                }
                if (count == 0) {
                    w.length = 1;
                }
                // is forgein name
            }
            list.add(w);
            len = source.length - (w.begin + w.length);
            pos = w.begin + w.length;
        }
        return list;
    }

    @SuppressWarnings("unused")
    private void loadset(Set<Character> targetset, String sourcefile) {
        String dataline;
        BufferedReader in = null;
        try {
            int count = 0;
            InputStream setdata = getSegmentResource(sourcefile);
            in = new BufferedReader(new InputStreamReader(setdata, "UTF-8"));
            while ((dataline = in.readLine()) != null) {
                if ((dataline.indexOf("#") > -1) || (dataline.length() == 0)) {
                    continue;
                }
                targetset.add(dataline.charAt(0));
                count++;
            }

        } catch (IOException e) {
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
        }

    }

    private InputStream getSegmentResource(String sourcefile) {
        String pckName = Darts.class.getPackage().getName();
        sourcefile = "/" + pckName.replace('.', '/') + "/" + sourcefile;
        return Darts.class.getResourceAsStream(sourcefile);
    }

    public Darts getDarts() {
        return darts;
    }

    public void setDarts(Darts darts) {
        this.darts = darts;
    }
}
