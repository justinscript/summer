/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * simple code review
 * 
 * @author zxc Apr 13, 2013 11:42:23 PM
 */
public class SimpleSvnReview {

    private static final String default_svn_encoding = "GBK";
    private static final String svnEncode            = System.getProperty("svn.encoding");
    private static final String outEncode            = System.getProperty("out.encoding");

    public static void main(String[] args) {
        try {

            if (args.length == 0) {
                System.err.println("Please input svn url.");
                System.err.println("Usage:");
                System.err.println("svn_diff <svn_url>");
                System.exit(-1);
            }

            String rev = parseRevision(getSvnLog(args));
            System.out.println("REVISION: " + rev);

            if (rev == null) {
                throw new RuntimeException("Cannot find svn log.");
            }

            String diff = getSvnDiff(args, rev);
            if (outEncode == null) {
                System.out.print(diff);
            } else {
                System.out.write(diff.getBytes(outEncode));
            }
            System.out.println();

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static String parseRevision(String log) throws IOException {
        BufferedReader sr = new BufferedReader(new StringReader(log));

        String rev = null;
        Pattern P = Pattern.compile("(r\\d+)\\s+\\|\\s+.*");
        for (String line; (line = sr.readLine()) != null;) {
            Matcher m = P.matcher(line);
            if (m.matches()) {
                rev = m.group(1);
            }
        }

        return rev;
    }

    public static String getSvnDiff(String[] args, String rev) throws UnsupportedEncodingException {
        List<String> cmd = new ArrayList<String>(Arrays.asList("svn", "di", "-" + rev + ":HEAD"));
        cmd.addAll(Arrays.asList(args));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ProcessTool pt = new ProcessTool(cmd, bos);
        pt.runCommand();
        return new String(bos.toByteArray(), (svnEncode == null ? default_svn_encoding : svnEncode));
    }

    public static String getSvnLog(String[] args) throws UnsupportedEncodingException {
        List<String> cmd = new ArrayList<String>(Arrays.asList("svn", "log", "--stop-on-copy"));
        cmd.addAll(Arrays.asList(args));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ProcessTool pt = new ProcessTool(cmd, bos);
        pt.runCommand();
        return new String(bos.toByteArray(), (svnEncode == null ? default_svn_encoding : svnEncode));
    }
}
