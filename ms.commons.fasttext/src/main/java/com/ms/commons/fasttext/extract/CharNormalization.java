/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc Apr 12, 2013 3:39:41 PM
 */
public class CharNormalization {

    public static final int        SYMBOL_MASK                  = 0x1 << 16;
    public static final int        TRAD_MASK                    = 0x2 << 16;
    public static final int        DBC_MASK                     = 0x4 << 16;
    public static final int        UPPER_MASK                   = 0x8 << 16;
    public static final int        HAN_MASK                     = 0x10 << 16;
    public static final int        SYNONYMY_MASK                = 0x20 << 16;

    public static final String     TRAD2SIMP_FILE               = "Trad2Simp_CT.txt";
    public static final String     UPPER_FILE                   = "Upper2Lower_CT.txt";
    public static final String     SYMBOL_FILE                  = "Symbol_CT.txt";
    public static final String     DBC_FILE                     = "DBC_CT.txt";
    public static final String     SYNONYMY_FILE                = "Synonymy_CT.txt";

    public static final char       PARAGRAPH_BREAK              = '\n';

    public static final String     DEFAULT_ENCODING             = "UTF-8";
    /**
     * 默认控制字符。
     */
    public static final char       DEFAULT_BLANK_CHAR           = (char) 0xffff;
    /**
     * 中文代码页分隔符
     */
    public static final char       CJK_UNIFIED_IDEOGRAPHS_START = 0x4e00;
    public static final char       CJK_UNIFIED_IDEOGRAPHS_END   = 0xA000;

    /**
     * 半角空格的值，在ASCII中为32(Decimal)
     */
    public static final char       DBC_SPACE                    = ' ';                         // 半角空格

    // 字符表编码
    private static int[]           codeTable;

    // symbol and synmomy loader
    private static CharTableLoader loader                       = new DefaultCharTableLoader();

    static {
        loadCodeTable();
    }

    /**
     * 为了性能，这里lock free，所以又线程问题，这个方法只可以在初始化的时候调用！！！
     * 
     * @param loader 用户自定义的符号过滤表和同义字表加载器
     */
    public static void setLoader(CharTableLoader loader) {
        if (loader != null) {
            CharNormalization.loader = loader;
            loadCodeTable();
        }
    }

    /**
     * <PRE>
     * 转换一个字符串,为了性能所有代码写在一个方法内，减少方法调用时间。 主要完成以下工作： &lt;br&gt;
     * 1、繁体转简体（可选） &lt;br&gt;
     * 2、全角转半角（可选）&lt;br&gt;
     * 3、转小写（可选）&lt;br&gt;
     * 4、过滤非汉字字符（可选）&lt;br&gt;
     * 5、过滤空白字符（包括&quot;\n&quot;&quot;\r&quot;&quot; &quot;&quot;\t&quot;等）（可选）&lt;br&gt;
     * 6、连续空白字符是否保留一个（可选）&lt;br&gt;
     * 
     * </PRE>
     * 
     * @param src 源字符串
     * @param needT2S 繁简体转换
     * @param needDBC 全半角转换
     * @param ignoreCase 大写转小写
     * @param filterNoneHanLetter 过滤非汉字字符
     * @param filterSymbol 是否过滤symbol字符（包括"\n""\r"" ""\t"等,见Symbol_CT.txt），
     * @param keepLastSymbol 连续symbol字符是否保留一个
     * @return 转换后的字符串，长度可能比转换前的短
     */
    public static String compositeTextConvert(String src, boolean needT2S, boolean needDBC, boolean ignoreCase,
                                              boolean filterNoneHanLetter, boolean convertSynonymy,
                                              boolean filterSymbol, boolean keepLastSymbol) {
        if (src == null || src.length() == 0) {
            return src;
        }
        char[] chs = src.toCharArray();
        StringBuilder buffer = new StringBuilder(chs.length);
        for (int i = 0; i < chs.length; i++) {
            char c = compositeCharConvert(chs[i], needT2S, needDBC, ignoreCase, filterNoneHanLetter, convertSynonymy,
                                          filterSymbol);
            // 处理连续symbol问题
            if (c == CharNormalization.DEFAULT_BLANK_CHAR) {
                if (keepLastSymbol && i < chs.length - 1) {
                    char next = chs[i + 1];
                    if (CharNormalization.isSeperatorSymbol(chs[i]) && !CharNormalization.isSeperatorSymbol(next)) {
                        c = CharNormalization.getCharFromTable(chs[i]);
                        buffer.append(c);
                        continue;
                    }
                }
            }
            // 处理其它过滤过程
            if (c != CharNormalization.DEFAULT_BLANK_CHAR) {
                buffer.append(c);
            } else {
                continue;
            }
        }
        return buffer.toString();
    }

    public static boolean isSeperatorSymbol(char c) {
        int i = codeTable[c];
        return (i & SYMBOL_MASK) != 0;
    }

    /**
     * <PRE>
     * 转换一个字符串,为了性能所有代码写在一个方法内，减少方法调用时间。 主要完成以下工作： &lt;br&gt;
     * 1、繁体转简体（可选） &lt;br&gt;
     * 2、全角转半角（可选）&lt;br&gt;
     * 3、转小写（可选）&lt;br&gt;
     * 4、过滤非汉字字符（可选）&lt;br&gt;
     * 5、过滤symbol字符（包括&quot;\n&quot;&quot;\r&quot;&quot; &quot;&quot;\t&quot;等）（可选）&lt;br&gt;
     * 6、连续symbol字符是否保留一个（可选）&lt;br&gt;
     * 
     * </PRE>
     * 
     * @param needT2S 繁简体转换
     * @param needDBC 全半角转换
     * @param ignoreCase 大写转小写
     * @param filterNoneHanLetter 过滤非汉字字符
     * @param filterSymbol 是否过滤symbol字符（包括"\n""\r"" ""\t"等,见Symbol_CT.txt），
     * @param keepLastSpace 连续symbol字符是否保留一个
     * @return 转换后的字符串，长度可能比转换前的短
     */
    public static final char compositeCharConvert(char c, boolean needT2S, boolean needDBC, boolean ignoreCase,
                                                  boolean filterNoneHanLetter, boolean convertSynonymy,
                                                  boolean filterSymbol) {
        if (needT2S) {
            c = convertCharT2S(c);
        }
        if (needDBC) {
            c = convertCharDBC(c);
        }
        if (ignoreCase) {
            c = convertChar2Lower(c);
        }
        if (filterNoneHanLetter) {
            c = filterNonHan(c);
        }
        if (convertSynonymy) {
            c = convertCharSynonymy(c);
        }
        if (filterSymbol) {
            c = filterSymbol(c);
        }
        return c;
    }

    public static char convertCharT2S(char ch) {
        int ret = codeTable[ch];
        if ((ret & TRAD_MASK) != 0) {
            return (char) ret;
        }
        return ch;
    }

    public static char convertChar2Lower(char ch) {
        int ret = codeTable[ch];
        if ((ret & UPPER_MASK) != 0) {
            return (char) ret;
        }
        return ch;
    }

    public static char convertCharDBC(char ch) {
        int ret = codeTable[ch];
        if ((ret & DBC_MASK) != 0) {
            return (char) ret;
        }
        return ch;
    }

    public static char convertCharSynonymy(char ch) {
        int ret = codeTable[ch];
        if ((ret & SYNONYMY_MASK) != 0) {
            return (char) ret;
        }
        return ch;
    }

    public static char filterNonHan(char ch) {
        int ret = codeTable[ch];
        if ((ret & HAN_MASK) != 0) {
            return ch;
        }
        return DEFAULT_BLANK_CHAR;
    }

    public static char filterSymbol(char ch) {
        int ret = codeTable[ch];
        if ((ret & SYMBOL_MASK) != 0) {
            return DEFAULT_BLANK_CHAR;
        }
        return ch;
    }

    public static char getCharFromTable(char c) {
        return (char) codeTable[c];
    }

    /**
     * 装载编码表,顺序非常重要，过滤类往往只是打标记，应先处理。
     */
    private static final void loadCodeTable() {
        int[] codeTable = new int[65536];
        // 初始化数组
        for (int i = 0; i < codeTable.length; i++) {
            codeTable[i] = i;
        }
        String ENCODING = DEFAULT_ENCODING;
        // 处理汉字过滤
        for (int i = CJK_UNIFIED_IDEOGRAPHS_START; i < CJK_UNIFIED_IDEOGRAPHS_END; i++) {
            codeTable[i] = HAN_MASK | codeTable[i];
        }
        // 处理Symbol过滤
        loadCodeTable(loader.loadSymbolTable(), codeTable, SYMBOL_MASK);
        // 处理繁体转简体
        loadCodeTable(loadListFromFile(TRAD2SIMP_FILE, ENCODING), codeTable, TRAD_MASK);
        // 处理大写转小写
        loadCodeTable(loadListFromFile(UPPER_FILE, ENCODING), codeTable, UPPER_MASK);
        // 处理全角转半角
        loadCodeTable(loadListFromFile(DBC_FILE, ENCODING), codeTable, DBC_MASK);

        // 处理汉字过滤
        for (int i = CJK_UNIFIED_IDEOGRAPHS_START; i < CJK_UNIFIED_IDEOGRAPHS_END; i++) {
            codeTable[i] = HAN_MASK | codeTable[i];
        }
        // 处理Symbol过滤
        loadCodeTable(loader.loadSymbolTable(), codeTable, SYMBOL_MASK);
        // 处理Synonymy
        loadCodeTable(loader.loadSynonmyTable(), codeTable, SYNONYMY_MASK);
        CharNormalization.codeTable = codeTable;
    }

    private static final void loadCodeTable(List<String> list, int[] codeTbl, int mask) {
        int i = 0;
        char c = 0;
        for (String line : list) {
            if (line.length() > 5) {
                i = Integer.parseInt(line.substring(1, 5), 16);// 将U0020类似的转换为整数字符值
                if (i == ',' || i == '\n' || i == '\r' || i == '\t') {// ',','\n','\r'特殊处理
                    c = (char) i;
                } else {
                    String[] tokens = line.split(",");
                    if (tokens.length == 3) {// 非','情况
                        String last = tokens[2];
                        if (last.length() == 1) {
                            c = last.charAt(0);
                        } else {
                            last = last.trim();
                            if (last.length() == 0) {// 如果是多个空格，trim可能使之为空，取空格
                                c = DBC_SPACE;
                            } else {
                                c = last.charAt(0);
                            }
                        }
                    } else {// 肯定包含一个"," , 因为第一列字符和编码不可能是","，所以只能是第三列字符是","
                        c = ',';
                    }
                }
            }
            int ret = codeTbl[i];
            if (ret == i) {
                codeTbl[i] = (mask | c);
            } else {
                ret = mask | ret;
                codeTbl[i] = (ret & 0xffff0000) | c;
            }
        }
    }

    private static List<String> loadListFromFile(String file, String encoding) {
        List<String> ret = new ArrayList<String>();
        String pckName = CharNormalization.class.getPackage().getName();
        file = "/" + pckName.replace('.', '/') + "/data/" + file;
        InputStream istream = CharNormalization.class.getResourceAsStream(file);
        if (istream == null) {
            throw new RuntimeException("Could not find code table: " + file);
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(istream, encoding), 2048);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 5) {// 合法行
                    ret.add(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read code table: " + file, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }

    static class DefaultCharTableLoader implements CharTableLoader {

        public List<String> loadSymbolTable() {
            return loadListFromFile(SYMBOL_FILE, DEFAULT_ENCODING);
        }

        public List<String> loadSynonmyTable() {
            return loadListFromFile(SYNONYMY_FILE, DEFAULT_ENCODING);
        }
    }
}
