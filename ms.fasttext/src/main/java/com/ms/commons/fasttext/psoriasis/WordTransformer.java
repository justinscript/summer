/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zxc Apr 12, 2013 3:31:26 PM
 */
public class WordTransformer {

    private static final int           HAN_CHARSET_START = 0x4e00;
    private static final int           HAN_CHARSET_END   = 0x9fa5;
    private static final String        SPLIT_CHAR        = "\t";
    private static final String        SUB_SPLIT_CHAR    = ",";

    private int                        wordListLimit     = 3000;
    private int                        wordLengthLimit   = -1;
    private boolean                    pinyinRec         = true;
    private boolean                    forkRec           = true;
    private boolean                    deformRec         = false;
    private boolean                    homoRec           = false;
    private HashMap<Character, String> c2pTable          = new HashMap<Character, String>();
    private HashMap<Character, String> c2fTable          = new HashMap<Character, String>();
    private HashMap<Character, String> c2dTable          = new HashMap<Character, String>();
    private HashMap<Character, String> c2hTable          = new HashMap<Character, String>();

    public WordTransformer(List<String> pinyin, List<String> fork, List<String> deform, List<String> homo) {
        this(pinyin, fork, deform, homo, -1, 3000);
    }

    /**
     * @param pinyin
     * @param fork
     * @param deform
     * @param homo
     * @param wordLengthLimit 递归处理的词长，如果超过词长，将不会进行递归处理。
     * @param wordListLimit 允许的最长产生词表长度，如果预测会超过这个长度，将不会进行递归处理
     */
    public WordTransformer(List<String> pinyin, List<String> fork, List<String> deform, List<String> homo,
                           int wordLengthLimit, int wordListLimit) {
        this.wordLengthLimit = wordLengthLimit;
        this.wordListLimit = wordListLimit;
        genPinyinTable(pinyin);
        genForkTable(fork);
        genDeformTable(deform);
        genHomophoneTable(homo);
    }

    public int getWordListLimit() {
        return wordListLimit;
    }

    public int getWordLengthLimit() {
        return wordLengthLimit;
    }

    public List<SkipTermExtraInfo> transformPinyinWords(String word) {
        return transformTransformWords(word, word, c2pTable, 0, pinyinRec, SkipTermExtraInfo.PINYIN_TYPE);
    }

    public List<SkipTermExtraInfo> transformForkWords(String word) {
        return transformTransformWords(word, word, c2fTable, 0, forkRec, SkipTermExtraInfo.FORK_TYPE);
    }

    public List<SkipTermExtraInfo> transformHomophoneWords(String word) {
        return transformTransformWords(word, word, c2hTable, 0, homoRec, SkipTermExtraInfo.HOMOPHONE_TYPE);
    }

    public List<SkipTermExtraInfo> transformDeformWords(String word) {
        return transformTransformWords(word, word, c2dTable, 0, deformRec, SkipTermExtraInfo.DEFORM_TYPE);
    }

    /**
     * 决定拼音是否递归组合 是否递归组合还取决于词长和单词数，超过限制将不会进行递归处理以避免内存溢出。
     * 
     * @param pinyinRec
     */
    public void setPinyinRec(boolean pinyinRec) {
        this.pinyinRec = pinyinRec;
    }

    /**
     * 决定组合词是否递归组合 是否递归组合还取决于词长和单词数，超过限制将不会进行递归处理以避免内存溢出。
     * 
     * @param forkRec
     */
    public void setForkRec(boolean forkRec) {
        this.forkRec = forkRec;
    }

    /**
     * 决定变形词是否递归组合 是否递归组合还取决于词长和单词数，超过限制将不会进行递归处理以避免内存溢出。
     * 
     * @param deformRec
     */
    public void setDeformRec(boolean deformRec) {
        this.deformRec = deformRec;
    }

    /**
     * 决定同音字是否递归组合 是否递归组合还取决于词长和单词数，超过限制将不会进行递归处理以避免内存溢出。
     * 
     * @param homoRec
     */
    public void setHomoRec(boolean homoRec) {
        this.homoRec = homoRec;
    }

    private List<SkipTermExtraInfo> transformTransformWords(String word, String protoWord,
                                                            HashMap<Character, String> table, int begin,
                                                            boolean recursive, int type) {
        if (word == null || word.length() == 0 || begin < 0 || begin > word.length()) {
            return null;
        }
        List<SkipTermExtraInfo> wordList = new ArrayList<SkipTermExtraInfo>();
        for (int i = begin; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!protoWord.contains("" + ch)) {
                continue;// 避免无限制递归组合
            }
            String fork = table.get(ch);
            if (fork != null && fork.length() != 0) {
                String[] fks = fork.split(SUB_SPLIT_CHAR);
                if (fks != null && fks.length != 0) {
                    for (String fk : fks) {
                        // System.out.println(word+","+protoWord+","+begin+","+fk);
                        if (fk.length() == 1 && fk.charAt(0) == ch) {
                            continue;// 去除重复字符
                        }
                        String trans = word.substring(0, i) + fk + word.substring(i + 1, word.length());
                        wordList.add(new SkipTermExtraInfo(trans, protoWord, true, false, type));
                        if (recursive && allowRecursive(protoWord, table)) {
                            wordList.addAll(transformTransformWords(trans, protoWord, table, begin + fk.length(),
                                                                    recursive, type));
                        }
                    }
                }
            }
        }
        return wordList;
    }

    private void genPinyinTable(List<String> pyList) {
        // 会 hui,kui，中间以\t键隔开，后面字符之间不允许有空格
        if (pyList == null) {
            return;
        }
        for (String pinyin : pyList) {
            String[] pys = pinyin.split(SPLIT_CHAR);
            if (pys != null && pys.length == 2) {
                String han = pys[0].trim();
                String py = pys[1];
                if (han.length() == 1) {
                    char ch = han.charAt(0);
                    if (ch >= HAN_CHARSET_START && ch <= HAN_CHARSET_END) {
                        if (!c2pTable.containsKey(ch)) {
                            c2pTable.put(ch, py);
                        } else {
                            c2pTable.put(ch, py + "," + c2pTable.get(ch));
                        }
                    }
                }
            }
        }
    }

    private void genForkTable(List<String> forkList) {
        // 法 氵去，中间以\t键隔开，后面字符之间不允许有空格，如果有多个拆分，每个拆分之间以“，”隔开，如“法 氵去，三去”
        if (forkList == null) {
            return;
        }
        for (String fork : forkList) {
            String[] fks = fork.split(SPLIT_CHAR);
            if (fks != null && fks.length == 2) {
                String chs = fks[0].trim();
                String fk = fks[1];
                if (chs.length() == 1) {
                    char ch = chs.charAt(0);
                    if (ch >= HAN_CHARSET_START && ch <= HAN_CHARSET_END) {
                        if (!c2fTable.containsKey(ch)) {
                            c2fTable.put(ch, fk);
                        } else {
                            c2fTable.put(ch, fk + "," + c2fTable.get(ch));
                        }
                    }
                }
            }
        }
    }

    private void genDeformTable(List<String> deformList) {
        // 膜 摸,模,馍,漠，中间以\t键隔开，后面字符之间不允许有空格，如果有多个拆分，每个拆分之间以“，”隔开
        if (deformList == null) {
            return;
        }
        for (String deform : deformList) {
            String[] dfs = deform.split(SPLIT_CHAR);
            if (dfs != null && dfs.length == 2) {
                String chs = dfs[0].trim();
                String df = dfs[1];
                if (chs.length() == 1) {
                    char ch = chs.charAt(0);
                    if (ch >= HAN_CHARSET_START && ch <= HAN_CHARSET_END) {
                        if (!c2dTable.containsKey(ch)) {
                            c2dTable.put(ch, df);
                        } else {
                            c2dTable.put(ch, df + "," + c2dTable.get(ch));
                        }
                    }
                }
            }
        }
    }

    private void genHomophoneTable(List<String> homoList) {
        // 法 珐,发,砝 中间以\t键隔开，后面字符之间不允许有空格，如果有多个拆分，每个拆分之间以“，”隔开
        if (homoList == null) {
            return;
        }
        for (String homo : homoList) {
            String[] hms = homo.split(SPLIT_CHAR);
            if (hms != null && hms.length == 2) {
                String chs = hms[0].trim();
                String hm = hms[1];
                if (chs.length() == 1) {
                    char ch = chs.charAt(0);
                    if (ch >= HAN_CHARSET_START && ch <= HAN_CHARSET_END) {
                        if (!c2hTable.containsKey(ch)) {
                            c2hTable.put(ch, hm);
                        } else {
                            c2hTable.put(ch, hm + "," + c2hTable.get(ch));
                        }
                    }
                }
            }
        }
    }

    private boolean allowRecursive(String protoWord, HashMap<Character, String> table) {
        int length = protoWord.length();
        if (wordLengthLimit != -1 && length > wordLengthLimit) {
            return false;
        }
        int count = 1;
        for (int i = 0; i < length; i++) {
            String line = table.get(protoWord.charAt(i));
            if (line != null) {
                count *= (countOfTrans(line) + 1);
            }
        }
        if (wordListLimit != -1 && count > wordListLimit) {
            return false;
        }
        return true;
    }

    private int countOfTrans(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (SUB_SPLIT_CHAR.equals("" + line.charAt(i))) {
                count++;
            }
        }
        return count + 1;
    }

    WordTransformer() {
        c2pTable.put('法', "fa");
        c2pTable.put('轮', "lun");
        c2pTable.put('功', "gong");
        c2fTable.put('法', "氵去,三去");
        c2fTable.put('轮', "车仑");
        c2fTable.put('胡', "古月");
        c2fTable.put('功', "工力");
        c2hTable.put('法', "发,珐");
        c2hTable.put('轮', "论,伦");
        c2hTable.put('功', "公,攻");
        c2dTable.put('轮', "论,纶,抡,沦,仑");
    }

    public static final void main(String[] args) {
        WordTransformer processor = new WordTransformer();
        System.out.println(processor.transformPinyinWords("法轮功"));
        System.out.println(processor.transformForkWords("法轮功"));
        System.out.println(processor.transformHomophoneWords("法轮功"));
        System.out.println(processor.transformDeformWords("法轮功"));
    }
}
