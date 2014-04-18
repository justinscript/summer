/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ms.commons.fasttext.codec.HtmlFastEntities;
import com.ms.commons.fasttext.decorator.DecoratorCallback;
import com.ms.commons.fasttext.extract.CharNormalization;
import com.ms.commons.fasttext.psoriasis.HTMLParserExtractText;
import com.ms.commons.fasttext.psoriasis.MappedDecoratorText;
import com.ms.commons.fasttext.psoriasis.PsoriasisUtil;
import com.ms.commons.fasttext.psoriasis.SkipTermExtraInfo;
import com.ms.commons.fasttext.psoriasis.WordTransformer;
import com.ms.commons.fasttext.text.PinyinUtil;

/**
 * 提供违禁词检查，替换功能<br>
 * 汉字转拼音<br>
 * 过滤html标签<br>
 * 
 * @author zxc Apr 12, 2013 3:25:32 PM
 */
public class FasttextService {

    private static Log                      logger              = LogFactory.getLog(FasttextService.class);

    // System属性中的key值
    public static final String              BANNED_DIR          = "banneddir";
    // 违禁词
    public static final String              BANNEDWORD          = "BANNEDWORD_U8.TXT";
    // 同音词
    public static final String              HOMOPHONE           = "HOMOPHONE_U8.TXT";
    // 拆分词
    public static final String              RADICALDIC          = "RADICALDIC_U8.TXT";

    public static final String              DEFAULT_REPLACE_STR = "***";

    //
    private static MappedDecoratorText      mdt;
    // 默认的违禁词修饰类
    private static DefaultDecoratorCallback defaultDecoratorCallback;
    //
    private static HTMLParserExtractText    pet                 = new HTMLParserExtractText();

    private static final int                DELAY_TIME          = 10 * 60;

    private static long                     weijinLastModified;
    private static long                     forkLastModified;
    private static long                     homoLastModified;

    static {
        // System.setProperty(BANNED_DIR, "/Users/zxc/msun/");
        loadText();
        createThreadPool();
    }

    private static void createThreadPool() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {

            public void run() {
                reload();
            }
        }, DELAY_TIME, DELAY_TIME, TimeUnit.SECONDS);
    }

    private static void reload() {
        logger.info("重新载入违禁词开始...");
        if (!checkFile(false)) {
            logger.info("无需重新载入违禁词,正使用系统默认设置或外部文件不存在");
            return;
        }
        String path = System.getProperty(BANNED_DIR);
        String weijinfilename = path + File.separator + BANNEDWORD;
        String forkfilename = path + File.separator + RADICALDIC;
        String homofilename = path + File.separator + HOMOPHONE;
        File wf = new File(weijinfilename);
        File ff = new File(forkfilename);
        File hf = new File(homofilename);
        if (weijinLastModified != wf.lastModified() || forkLastModified != ff.lastModified()
            || homoLastModified != hf.lastModified()) {
            // 有变化,重新载入
            logger.info("内容发生变化,重新载入违禁词....结束...");
            loadText();
            logger.info("重新载入违禁词....结束...");
        } else {
            logger.info("内容没变化,没有重载...");
        }
    }

    private static void loadText() {
        String path = System.getProperty(BANNED_DIR);
        List<String> pinying;
        List<String> weijin;
        List<String> fork;
        List<String> homo;
        // path = "/Users/hanjie/Documents/bannedword";
        pinying = PinyinUtil.loadListFromFile("data", "PINYING_U8.TXT", "utf8");
        if (!checkFile(true)) {
            // 默认的
            weijin = loadListFromFile("data", BANNEDWORD, "utf8");
            fork = loadListFromFile("data", RADICALDIC, "utf8");
            homo = loadListFromFile("data", HOMOPHONE, "utf8");
        } else {
            String filename = path + File.separator + BANNEDWORD;
            weijin = readList(filename, "utf8");
            filename = path + File.separator + RADICALDIC;
            fork = readList(filename, "utf8");
            filename = path + File.separator + HOMOPHONE;
            homo = readList(filename, "utf8");
        }

        WordTransformer transform = new WordTransformer(pinying, fork, null, homo);
        List<SkipTermExtraInfo> radList = new ArrayList<SkipTermExtraInfo>();
        List<SkipTermExtraInfo> weijinList = PsoriasisUtil.loadDic(weijin);
        for (SkipTermExtraInfo skipTermExtraInfo : weijinList) {
            radList.add(skipTermExtraInfo);
            // 拼音+违禁词 违禁词用拼音代替
            radList.addAll(transform.transformPinyinWords(skipTermExtraInfo.getWord()));
            // 拆分词+违禁词 违禁词用拆分词代替 （法 三去）
            radList.addAll(transform.transformForkWords(skipTermExtraInfo.getWord()));
            // 同声词+违禁词 违禁词用同声词代替
            List<SkipTermExtraInfo> hm = transform.transformHomophoneWords(skipTermExtraInfo.getWord());
            radList.addAll(hm);

            // 同音词+拼音+违禁词
            for (SkipTermExtraInfo skipTermExtraInfo2 : hm) {
                radList.addAll(transform.transformPinyinWords(skipTermExtraInfo2.getWord()));
            }
        }
        Properties props = new Properties();
        mdt = new MappedDecoratorText(radList, props);
        defaultDecoratorCallback = new DefaultDecoratorCallback();
    }

    private static boolean checkFile(boolean isInit) {
        String path = System.getProperty(BANNED_DIR);
        if (path == null || path.trim().length() == 0) {
            return false;
        }
        String weijinfilename = path + File.separator + BANNEDWORD;
        String forkfilename = path + File.separator + RADICALDIC;
        String homofilename = path + File.separator + HOMOPHONE;
        File wf = new File(weijinfilename);
        File ff = new File(forkfilename);
        File hf = new File(homofilename);
        boolean flag = wf.exists() && ff.exists() && hf.exists();
        if (flag && isInit) {
            weijinLastModified = wf.lastModified();
            forkLastModified = ff.lastModified();
            homoLastModified = hf.lastModified();
        }
        return flag;
    }

    private static List<String> readList(String resource, String encoding) {
        if (resource == null) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        BufferedReader br = null;
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(resource), encoding));
            String line = null;
            while ((line = br.readLine()) != null) {
                list.add(line.trim());
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("read " + resource + "fail...", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("read " + resource + "fail...", e);
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return list;
    }

    private static final List<String> loadListFromFile(String subfold, String file, String encoding) {
        String pckName = FasttextService.class.getPackage().getName();
        file = "/" + pckName.replace('.', '/') + "/" + subfold + "/" + file;
        InputStream istream = FasttextService.class.getResourceAsStream(file);
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

    /**
     * 检查违禁词，并默认使用***来替换违禁词
     * 
     * @param content
     * @return
     */
    public static String decorator(String content) {
        return mdt.decorator(content, false, defaultDecoratorCallback);
    }

    /**
     * 检查违禁词，并默认使用replaceStr来替换违禁词
     * 
     * @param content
     * @param replaceStr 不能为null
     * @return
     */
    public static String decorator(String content, final String replaceStr) {
        return mdt.decorator(content, false, new DecoratorCallback() {

            public StringBuilder decorator(String src) {
                return new StringBuilder(replaceStr);
            }
        });
    }

    /**
     * 检查时候包含违禁词
     * 
     * @param content
     * @return
     */
    public static boolean containTerm(String content) {
        return mdt.containTerm(content, false);
    }

    /**
     * 得到汉字的对应拼音，可能为多个
     * 
     * @param han 汉字
     * @return 拼音，可能为null或者多个
     */
    public static String[] getPinyingOfHan(String han) {
        return PinyinUtil.getPinyingOfHan(han);
    }

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等
     * 
     * @param html 输入html文本
     * @return 转换后的文本
     */
    public static String parserExtractText(String html) {
        return pet.getText(html);
    }

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等
     * 
     * @param html 输入html文本
     * @param ignoreCase 结果是否需要转换为小写。
     * @return 转换后的文本
     */
    public static String parserExtractText(String html, boolean ignoreCase) {
        return pet.getText(html, ignoreCase);
    }

    /**
     * 判断字符是否是汉字
     */
    public static boolean isHanLetter(char ch) {
        return ch >= PinyinUtil.CJK_UNIFIED_IDEOGRAPHS_START && ch < PinyinUtil.CJK_UNIFIED_IDEOGRAPHS_END;
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
        return CharNormalization.compositeTextConvert(src, needT2S, needDBC, ignoreCase, filterNoneHanLetter,
                                                      convertSynonymy, filterSymbol, keepLastSymbol);
    }

    /**
     * 默认的违禁词替换类
     * 
     * @author hanjie 2011-7-21 下午01:57:30
     */
    private static class DefaultDecoratorCallback implements DecoratorCallback {

        public StringBuilder decorator(String src) {
            return new StringBuilder(DEFAULT_REPLACE_STR);
        }
    }

    /**
     * 防xss转码
     * 
     * @param str
     * @return
     */
    public static String escape(String str) {
        return HtmlFastEntities.HTML40.escape(str);
    }

    /**
     * 得到数组的排列组合
     * 
     * @param charArgs
     * @return
     */
    public static String[] getCombination(char[][] charArgs) {
        // 先计算出行列数
        int row = 1;
        // 列数就是char[][]的长度
        int col = charArgs.length;
        // 行数大小是每维大小相乘
        for (int i = 0; i < charArgs.length; i++) {
            row = row * charArgs[i].length;
        }
        // 一维数组循环次数
        int zhengti = 1;
        char[][] out = new char[row][col];
        for (int i = 0; i < col; i++) {
            char[] chs = charArgs[i];
            // 长度
            int length = chs.length;
            // 单个元素循环次数
            int dange = row / length / zhengti;
            int start = 0;
            for (int j = 0; j < zhengti; j++) {
                for (int k = 0; k < length; k++) {
                    char c = chs[k];
                    for (int l = 0; l < dange; l++) {
                        out[start][i] = c;
                        start++;
                    }
                }
            }
            // 下次的循环次数就是这次循环次数*本数组长度
            zhengti = zhengti * length;
        }
        String[] ss = new String[row];
        for (int i = 0; i < row; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < col; j++) {
                sb.append(out[i][j]);
            }
            ss[i] = sb.toString();
        }
        return ss;
    }

}
