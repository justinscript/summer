/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ms.commons.fasttext.extract.CharNormalization;

/**
 * @author zxc Apr 12, 2013 3:24:57 PM
 */
public final class PinyinUtil {

    /**
     * 中文代码页分隔符
     */
    public static final char   CJK_UNIFIED_IDEOGRAPHS_START = 0x4e00;
    public static final char   CJK_UNIFIED_IDEOGRAPHS_END   = 0xA000;

    static Map<String, String> tbl_pinyin                   = null;
    static Map<String, String> tbl_bjx                      = null;
    static {
        tbl_pinyin = genPinyinTable(loadListFromFile("data", "PINYING_U8.TXT", "UTF8"));
        tbl_bjx = genPinyinTable(loadListFromFile("data", "BJX_U8.TXT", "UTF8"));
    }

    /**
     * 得到姓的首字母，如果是多音字，如果得到多个结果，如”单于“和”单“，可以使用”单于“再试一次，确认最终结果。 如果不是汉字或者姓，返回null
     * 
     * @param han 姓的汉字，可以是复姓，如单于，尉迟等
     * @return 姓的拼音首字母，可能是null或者多个，如”单“返回”s“和”c“。
     */
    public static char[] getFirstLetterOfFamilyName(String familyName) {
        String[] pys = getPinyingOfFN(familyName);
        if (pys == null) {
            pys = getPinyingOfHan(familyName);
        }
        if (pys != null) {
            if (familyName.length() == 1) {// 是复姓而不是多音字
                char[] chs = new char[pys.length];
                for (int i = 0; i < chs.length; i++) {
                    chs[i] = pys[i].charAt(0);
                }
                return chs;
            } else {
                char[] chs = new char[1];
                chs[0] = pys[0].charAt(0);
                return chs;
            }
        }
        return null;
    }

    /**
     * 得到用户名的姓首字母，全部为小写字母,如果： <br>
     * 1、是标准中文名：庄国林，则返回‘z'，如果是复姓，则去潜力股字符进行匹配，如”单于“则返回'c' <br>
     * 2、如果是英文名或者字母名称，如”member1“等，则返回第一个字母'm'<br>
     * 3、如果是数字，返回第一个数字<br>
     * 4、如果是外国人名 如 ”Peter Brown',应该返回'b'，也就是说返回空格分割的最后一个单词的首字母'b'<br>
     * 注意：这个方法在处理复姓的时候可能等不到预期结果，如姓名为“单于颖",这时候不能知道此人姓shan还是chanyu，本方法作为单于处理。
     * 
     * @param name 姓名
     * @return 首字母,如果不存在，返回字符0x0
     */
    public static char getFirstLetterOfName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Name can not be null.");
        }
        name = name.toLowerCase().trim();
        char first = name.charAt(0);
        boolean han = isHanLetter(first);
        if (han) {
            char[] chs = getFirstLetterOfFamilyName("" + first);
            if (chs == null) {
                return (char) 0;
            }
            if (chs.length == 1) {
                return chs[0];
            }
            // 多个值，可能是复姓
            if (name.length() > 2) {
                String family = name.substring(0, 2);
                char[] famChs = getFirstLetterOfFamilyName(family);
                if (famChs != null && famChs.length == 1) {
                    return famChs[0];
                }
            }
            return chs[0];
        } else {
            if (name.contains(" ")) {// 空格分割的外国人名，如“Mary Cary”
                String[] split = name.split(" ");
                if (split != null && split.length > 1) {
                    String last = split[split.length - 1];
                    if (last != null && last.length() > 0) {
                        return last.charAt(0);
                    }
                }
            } else {
                return name.charAt(0);
            }
        }
        return (char) 0;
    }

    /**
     * 判断字符是否是汉字
     */
    public static boolean isHanLetter(char ch) {
        return ch >= CJK_UNIFIED_IDEOGRAPHS_START && ch < CJK_UNIFIED_IDEOGRAPHS_END;
    }

    /**
     * 得到对应百家姓的拼音，可能为多个，如”单“可以对应”shan“和”chan“
     * 
     * @param fn 姓，为单姓或者复姓
     * @return 百家姓的拼音，可能为null,单个或者多个
     */
    public static String[] getPinyingOfFN(String fn) {
        if (fn == null || fn.trim().length() == 0) {
            return null;
        }
        fn = fn.toLowerCase().trim();
        String py = tbl_bjx.get(fn);
        if (py != null) {
            return py.split(",");
        }
        return null;
    }

    /**
     * 得到汉字的对应拼音，可能为多个
     * 
     * @param han 汉字
     * @return 拼音，可能为null或者多个
     */
    public static String[] getPinyingOfHan(String han) {
        if (han == null || han.trim().length() != 1) {
            return null;
        }
        han = han.toLowerCase().trim();
        String py = tbl_pinyin.get(han);
        if (py != null) {
            return py.split(",");
        }
        return null;
    }

    private static Map<String, String> genPinyinTable(List<String> list) {
        Map<String, String> ret = new HashMap<String, String>();
        if (list == null) {
            return ret;
        }
        for (String line : list) {
            String[] split = line.split("\t");
            if (split.length != 2) {
                throw new IllegalArgumentException("File format error.");
            }
            String han = split[0].trim();
            String py = split[1].trim();
            // 处理复姓，拼音中不可能进来
            if (han.length() > 1) {
                String first = han.substring(0, 1);
                String[] multi = py.split(",");
                if (multi.length > 0) {// 复姓同时使用第一个汉字的拼音
                    putNotDup(ret, first, multi[0]);
                }
            }
            putNotDup(ret, han, py);
        }
        return ret;
    }

    public static final List<String> loadListFromFile(String subfold, String file, String encoding) {
        String pckName = PinyinUtil.class.getPackage().getName();
        file = "/" + pckName.replace('.', '/') + "/" + subfold + "/" + file;
        InputStream istream = CharNormalization.class.getResourceAsStream(file);
        if (istream == null) {
            throw new RuntimeException("Could not find file: " + file);
        }
        List<String> ret = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(istream, encoding));
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().length() > 0) {
                    ret.add(line);
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                } else {
                    if (istream != null) {
                        istream.close();
                    }
                }
            } catch (Exception e2) {
                throw new RuntimeException("close stream failed", e2);
            }
        }
        return ret;
    }

    // 判断是否重复包含拼音
    private static boolean contains(String py, String unit) {
        if (!py.contains(unit)) {
            return false;
        }
        String[] splits = py.split(",");
        for (String s : splits) {
            if (s.equals(unit)) {
                return true;
            }
        }
        return false;
    }

    private static void putNotDup(Map<String, String> tbl, String key, String value) {
        if (tbl.containsKey(key)) {
            String v = tbl.get(key);
            if (!contains(v, value)) {
                tbl.put(key, v + "," + value);
            }
        } else {
            tbl.put(key, value);
        }
    }
}
