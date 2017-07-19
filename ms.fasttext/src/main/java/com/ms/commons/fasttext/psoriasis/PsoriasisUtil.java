/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.ms.commons.fasttext.extract.ExtractText;
import com.ms.commons.fasttext.segment.WordTerm;

/**
 * @author zxc Apr 12, 2013 3:32:51 PM
 */
public class PsoriasisUtil {

    static String  REGEXP         = "(.*)\\s+([0-9,\\.]+)\\s+(true|false)\\s+(true|false)";
    static String  RADICALREGEXP  = "(.*)\\s+(.*)";

    static Pattern pattern        = null;
    static Pattern radicalPattern = null;
    static {
        try {
            PatternCompiler c = new Perl5Compiler();
            pattern = c.compile(REGEXP, Perl5Compiler.CASE_INSENSITIVE_MASK | Perl5Compiler.READ_ONLY_MASK);
            radicalPattern = c.compile(RADICALREGEXP, Perl5Compiler.CASE_INSENSITIVE_MASK
                                                      | Perl5Compiler.READ_ONLY_MASK);
        } catch (MalformedPatternException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 工具方法，从WordTerm list生成String list,缺省进行了消重处理;
     * 
     * @param wordTermList term list
     * @return 消重后的word list
     */
    public static List<String> parseWordList(List<WordTerm> wordTermList) {
        Set<String> wordSet = parseWordSet(wordTermList);
        return new ArrayList<String>(wordSet);
    }

    /**
     * 工具方法，从WordTerm list生成String list,缺省进行了消重处理;
     * 
     * @param wordTermList term list
     * @return 消重后的word list
     */
    public static Set<String> parseWordSet(List<WordTerm> wordTermList) {
        Set<String> wordSet = new HashSet<String>();
        if (wordTermList == null || wordTermList.isEmpty()) {
            return wordSet;
        }
        for (WordTerm term : wordTermList) {
            if (term.termExtraInfo instanceof SkipTermExtraInfo) {
                wordSet.add(((SkipTermExtraInfo) term.termExtraInfo).getWord());
            }
        }
        return wordSet;
    }

    /**
     * 工具方法，从WordTerm list生成原始文本中匹配到的String list,缺省进行了消重处理;
     * 
     * @param wordTermList term list
     * @return 消重后的word list
     */
    public static List<String> parseWordList(String content, List<WordTerm> wordTermList) {
        Set<String> wordSet = parseWordSet(content, wordTermList);
        return new ArrayList<String>(wordSet);
    }

    /**
     * 工具方法，从WordTerm list生成原始文本中匹配到的String list,缺省进行了消重处理;
     * 
     * @param wordTermList term list
     * @return 消重后的word list
     */
    public static Set<String> parseWordSet(String content, List<WordTerm> wordTermList) {
        Set<String> wordSet = new HashSet<String>();
        if (content == null || content.length() == 0 || wordTermList == null || wordTermList.isEmpty()) {
            return wordSet;
        }
        for (WordTerm term : wordTermList) {
            int begin = term.begin;
            int end = begin + term.length;
            if (begin >= 0 && end < content.length()) {
                wordSet.add(content.substring(begin, end));
            }
        }
        return wordSet;
    }

    /**
     * 从文本字符串构建词典项，每一行文本组织形式为“法轮功 1.0 true false“，依次代表词条项中的<br>
     * “word weight allowSkip wholeWord”，每个词之间以空格分开。
     * 
     * @see SkipTermExtraInfo
     * @param dicItemList 词典文本串
     * @return 词条项列表
     */

    public static final List<SkipTermExtraInfo> loadDic(List<String> dicItemList) {
        if (dicItemList == null || dicItemList.isEmpty()) {
            return null;
        }
        PatternMatcher matcher = new Perl5Matcher();
        List<SkipTermExtraInfo> infoList = new ArrayList<SkipTermExtraInfo>();
        for (String line : dicItemList) {
            SkipTermExtraInfo skip = new SkipTermExtraInfo();
            if (matcher.matches(line, pattern)) {
                MatchResult result = matcher.getMatch();
                if (result.groups() == 5) {
                    skip.word = result.group(1).trim();
                    skip.weight = Float.parseFloat(result.group(2));
                    skip.allowSkip = Boolean.parseBoolean(result.group(3));
                    skip.wholeWord = Boolean.parseBoolean(result.group(4));
                } else {
                    throw new IllegalArgumentException("dic match failed: " + line);
                }
            } else {
                throw new IllegalArgumentException("dic match failed: " + line);
            }
            infoList.add(skip);
        }
        return infoList;
    }

    public static void normalizeWordList(List<SkipTermExtraInfo> wordList, ExtractText extractor) {
        if (wordList == null || wordList.size() == 0 || extractor == null) {
            return;
        }
        for (SkipTermExtraInfo skip : wordList) {
            // System.out.print(skip.getWord());
            skip.setWord(extractor.getText(skip.getWord()));
            // System.out.println("      "+skip.getWord());
        }
    }

    /**
     * 根据文本数据构建一个部首词典，文本组织形式为"法 氵去"，第一个字符为拆分前的字符，后面是拆分后的单词， 中间以空格分隔。
     * 
     * @see SkipTermExtraInfo
     * @param dicItemList 部首词典文本串
     * @return 部首词典词条项列表
     */

    public static final List<SkipTermExtraInfo> loadRadicalDic(List<String> dicItemList) {
        if (dicItemList == null || dicItemList.isEmpty()) {
            return null;
        }
        PatternMatcher matcher = new Perl5Matcher();
        List<SkipTermExtraInfo> infoList = new ArrayList<SkipTermExtraInfo>();
        for (String line : dicItemList) {
            SkipTermExtraInfo skip = new SkipTermExtraInfo();
            if (matcher.matches(line, PsoriasisUtil.radicalPattern)) {
                MatchResult result = matcher.getMatch();
                if (result.groups() == 3) {
                    skip.word = result.group(2).trim();
                    skip.weight = SkipTermExtraInfo.DEFAULT_RADICAL_WORD_WEIGHT;// 0.5f;应该是部首
                    skip.allowSkip = false;
                    skip.wholeWord = false;
                    skip.compositeChar = result.group(1).trim().charAt(0);
                } else {
                    throw new IllegalArgumentException("dic match failed: " + line);
                }
            } else {
                throw new IllegalArgumentException("dic match failed: " + line);
            }
            infoList.add(skip);
        }
        return infoList;
    }

    public static List<String> readList(String resource, String encoding, Class<?> clazz) {
        if (resource == null || clazz == null) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        BufferedReader br = null;
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        try {
            br = new BufferedReader(new InputStreamReader(clazz.getResourceAsStream(resource), encoding));
            String line = null;
            while ((line = br.readLine()) != null) {
                list.add(line.trim());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
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
}
