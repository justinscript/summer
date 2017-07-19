/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.treedb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:31:26 PM
 */
public class JsonParser {

    static class Node implements Comparable<Node> {

        public final int keyStart;
        public final int keyEnd;
        public final int kvStart;
        public final int kvEnd;
        public final int level;

        public Node(int ks, int ke, int rs, int re, int l) {
            keyStart = ks;
            keyEnd = ke;
            kvStart = rs;
            kvEnd = re;
            level = l;
        }

        public int compareTo(Node o) {
            return keyStart - o.keyStart;
        }
    }

    public static List<Node> filterFields(String str) {
        List<int[]> valuelist = new ArrayList<int[]>();
        List<Node> reslist = new ArrayList<Node>();
        int res = findObject(0, 0, str.toCharArray(), valuelist);
        if (res == -1) findArray(0, 0, str.toCharArray(), valuelist);
        for (int[] value : valuelist) {
            reslist.add(new Node(value[0] + 1, value[1] - 1, value[0], value[3], value[4]));
        }
        Collections.sort(reslist);
        return reslist;
    }

    private static int findObject(int start, int level, char[] strs, List<int[]> valuelist) {
        if (strs[start] != '{') return -1;
        start++;// '{'
        level++; // '{'
        while (strs[start] != '}') {
            int end = findString(start, strs);
            int s1 = start;
            int e1 = end;
            start = end + 1;
            start++; // ':'
            end = findValue(start, level, strs, valuelist);
            valuelist.add(new int[] { s1, e1, start, end, level });
            start = end + 1;
            if (strs[start] == ',') {
                start++;
            }
        }
        return start;
    }

    private static int findArray(int start, int level, char[] strs, List<int[]> valuelist) {
        if (strs[start] != '[') return -1;
        start++;
        while (strs[start] != ']') {
            int end = findValue(start, level, strs, valuelist);
            start = end + 1;
            if (strs[start] == ',') {
                start++;
            }
        }
        return start;
    }

    private static int findString(int start, char[] strs) {
        if (strs[start] != '\"') return -1;
        start++;
        while (strs[start] != '\"') {
            if (strs[start] == '\\') {
                start += 2;
            } else {
                start++;
            }
        }
        return start;
    }

    private static int findNumber(int start, char[] strs) {
        if (strs[start] != '-' && strs[start] != '0' && !isDigit(strs[start])) return -1;
        if (strs[start] == '-') start++;
        if (strs[start] == '0') start++;
        else {
            while (isDigit(strs[start])) {
                start++;
            }
            if (strs[start] != '.' && strs[start] != 'e' && strs[start] != 'E') {
                return start - 1;
            }
        }
        if (strs[start] == '.') {
            start++;
            while (isDigit(strs[start])) {
                start++;
            }
            if (strs[start] != 'e' && strs[start] != 'E') {
                return start - 1;
            }
        }
        if (strs[start] == 'e' || strs[start] == 'E') {
            start++;
            if (strs[start] == '-' || strs[start] == '+') start++;
            while (isDigit(strs[start])) {
                start++;
            }
            return start - 1;
        } else {
            return start - 1;
        }
    }

    private static int findValue(int start, int level, char[] strs, List<int[]> valuelist) {
        int end = findArray(start, level, strs, valuelist);
        if (end != -1) return end;
        end = findString(start, strs);
        if (end != -1) return end;
        end = findObject(start, level, strs, valuelist);
        if (end != -1) return end;
        end = findNumber(start, strs);
        if (end != -1) return end;
        if (strs[start] == 't' || strs[start] == 'n') return start + 3;
        if (strs[start] == 'f') return start + 4;
        return -1;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
