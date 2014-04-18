/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ms.commons.fasttext.decorator.DecoratorCallback;
import com.ms.commons.fasttext.decorator.DecoratorConstants;
import com.ms.commons.fasttext.decorator.DecoratorText;
import com.ms.commons.fasttext.segment.TermExtraInfo;
import com.ms.commons.fasttext.segment.WordTerm;

/**
 * <h3>支持字符串过滤后进行分词和分析，最后根据字符映射表进行修饰字符串。<br>
 * 支持跳字匹配和字符分割匹配，如“法-轮-功”和“氵去车仑工力”等。</h3>
 * 
 * @author zxc Apr 12, 2013 3:33:13 PM
 */
public class MappedDecoratorText extends DecoratorText {

    private static Log    logger = LogFactory.getLog(MappedDecoratorText.class);

    private Configuration config;

    /**
     * 利用现有MappedDecoratorText和定制属性创建一个新的MappedDecoratorText对象，主要用于定制MappedDecoratorText行为而不创建 新的darts对象，这个对象是线程安全的。
     * 
     * @param mdt
     * @param customeProperties
     */
    public MappedDecoratorText(MappedDecoratorText mdt, Properties customeProperties) {
        if (mdt == null || customeProperties == null) {
            throw new IllegalArgumentException("dic or skip is not valid!");
        }
        this.darts = mdt.darts;
        config = new Configuration(customeProperties);
    }

    public MappedDecoratorText(List<SkipTermExtraInfo> dic, MappedExtractText extractor, Properties customeProperties) {
        this(dic, extractor, null, customeProperties);
    }

    public MappedDecoratorText(List<SkipTermExtraInfo> dic, WordDecorator wordDecorator, Properties customeProperties) {
        this(dic, null, wordDecorator, customeProperties);
    }

    public MappedDecoratorText(List<SkipTermExtraInfo> dic, MappedExtractText extractor, WordDecorator wordDecorator,
                               Properties customeProperties) {
        if (dic == null || customeProperties == null) {
            logger.error("dic or customeProperties is not valid!");
            throw new IllegalArgumentException("dic or skip is not valid!");
        }
        config = new Configuration(extractor, wordDecorator, customeProperties);
        RadicalSkipDarts darts = new RadicalSkipDarts(dic, config);
        darts.toLowerCase = config.isToLowcase();
        this.darts = darts;
    }

    public MappedDecoratorText(List<SkipTermExtraInfo> dic, Configuration config) {
        RadicalSkipDarts darts = new RadicalSkipDarts(dic, config);
        darts.toLowerCase = config.isToLowcase();
        this.darts = darts;
    }

    /**
     * 使用字典和属性创建MappedDecoratorText对象，定制属性可以控制MappedDecoratorText行为。
     * 
     * @param dic
     * @param customeProperties
     */
    public MappedDecoratorText(List<SkipTermExtraInfo> dic, Properties customeProperties) {
        if (dic == null || customeProperties == null) {
            logger.error("dic or customeProperties is not valid!");
            throw new IllegalArgumentException("dic or skip is not valid!");
        }
        config = new Configuration(customeProperties);
        RadicalSkipDarts darts = new RadicalSkipDarts(dic, config);
        this.darts = darts;
    }

    @Deprecated
    public MappedDecoratorText(List<SkipTermExtraInfo> dic, int skip) {
        this(dic, null, skip);
    }

    @Deprecated
    public MappedDecoratorText(List<SkipTermExtraInfo> dic, List<SkipTermExtraInfo> radicalsDic, int skip) {
        if (dic == null || skip < 0) {
            logger.error("dic or skip is not valid!");
            throw new IllegalArgumentException("dic or skip is not valid!");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize with dic: " + dic + " and skip: " + skip);
        }
        RadicalSkipDarts darts = new RadicalSkipDarts(dic, radicalsDic, skip);
        this.darts = darts;
        Properties def = new Properties();
        def.put(Configuration.DARTS_SKIP_VALUE_KEY, "" + skip);
        config = new Configuration(def);
    }

    /**
     * 重载方法，利用HTML过滤器和字符映射处理，增加以下功能：<br>
     * 1、被空格等非可见字符分隔的关键字（如法　轮　功）<br>
     * 2、被一个或者多个',''-'分隔的关键字,如"法-轮-功""法，轮，功"等<br>
     * 3、被html tag分隔的关键字,如&lt;a&gt;法&lt;/a&gt;&lt;a&gt;轮&lt;/a&gt;&lt;a&gt;功&lt;/a&gt;<br>
     * 4、字符分开情况，如“氵去车仑工力”到“法轮功”<br>
     * 
     * @param content 输入文本，内部会使用CompositeHtmlExtractText进行规范化处理
     * @param toLower 全部转换为小写
     * @param callback 回调接口，用于文本加亮
     * @return 在原始上增加加亮tag后的文本
     */
    @Override
    public String decorator(String content, boolean toLower, DecoratorCallback callback) {
        if (callback == null) {
            logger.error("Token DecoratorCallback shuold be set.");
            throw new IllegalArgumentException("Token DecoratorCallback shuold be set.");
        }

        if (content == null || content.length() == 0) {
            return content;
        }
        // 过滤html tag 和全角转半角等工作
        MappedCharArray mca = MappedCharArray.createInstance(content);
        mca = config.getMappedExtracter() != null ? config.getMappedExtracter().getText(mca, toLower) : mca;
        char origin[] = mca.getSrc();
        int[] map = mca.getMap();
        StringBuilder buffer = new StringBuilder(content.length());
        List<WordTerm> keyList = parseTerms(mca, toLower, config.getSkip(), false, false);
        // 修饰字符串，注意在map字符串的字符在原字符串中不一定连续,如果重复匹配，按照正向最大单词或者权重最大单词进行修饰。
        if (keyList.size() > 0) {
            int begin = 0;
            for (int i = 0; i < keyList.size(); i++) {
                WordTerm w = keyList.get(i);
                // 处理重复匹配情况，如果有重复匹配，修饰最长字符串，如“中国”“中国共产党”同时匹配，修饰中国共产党。
                // 如果有权重，修饰权重最大的单词
                for (int j = i + 1; j < keyList.size(); j++) {
                    WordTerm w1 = keyList.get(j);
                    if (w.begin == w1.begin) {// 重复匹配
                        TermExtraInfo t = w.termExtraInfo, t1 = w1.termExtraInfo;
                        if (t instanceof SkipTermExtraInfo && t1 instanceof SkipTermExtraInfo) {
                            double weight = ((SkipTermExtraInfo) t).getWeight();
                            double weight1 = ((SkipTermExtraInfo) t1).getWeight();
                            w = weight1 >= weight ? w1 : w;
                        } else {
                            w = w1;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
                // 如果重复匹配，begin可能大于w.begin，直接跳过
                if (map[w.begin] - begin < 0) {
                    continue;
                }
                buffer.append(origin, begin, map[w.begin] - begin);
                begin = map[w.begin];
                // 从map中取关键字并进行修饰
                decorateWord(buffer, mca, w, callback);
                begin = map[w.begin + w.length - 1] + 1;
            }
            buffer.append(origin, begin, origin.length - begin);
        } else {
            buffer.append(origin);
        }
        return buffer.toString();
    }

    /**
     * 对一个分词结果进行标记。为了考虑使用tag隔开的 <font color=blue>中国</font> 的方法进行的<br>
     * 文本，对这种匹配的词进行逐字标记，避免原有的tag影响。
     * 
     * @param buf
     * @param mca
     * @param w
     * @param callback
     */
    private void decorateWord(StringBuilder buf, MappedCharArray mca, WordTerm w, DecoratorCallback callback) {
        int[] map = mca.getMap();
        char[] src = mca.getSrc();
        char[] target = mca.getTarget();
        // word decorator
        WordDecorator wd = config.getWordDecorator();
        String word = new String(src, map[w.begin], map[w.begin + w.length - 1] - map[w.begin] + 1);
        boolean skipTag = false;
        boolean wordDecoratorEnable = false;
        if (wd != null && wd.match(word, w)) {
            wordDecoratorEnable = true;
        }
        if (map[w.begin + w.length - 1] - map[w.begin] + 1 != w.length) {
            int mb = map[w.begin], me = map[w.begin + w.length - 1] + 1;
            for (int i = mb, j = w.begin; i < me; i++) {
                if (i != map[j]) {// 非匹配字符，可能已经被过滤或者跳过，需要判断
                    if (src[i] == DecoratorConstants.LEFT_TAG) {// 简单判断是否过滤html tag，提高性能
                        skipTag = true;
                        break;
                    }
                } else {
                    j++;
                }
            }
        }
        if (skipTag) {// 跳过tag，需要逐字打标记
            int begin = map[w.begin];
            for (int i = w.begin; i < w.begin + w.length; i++) {
                buf.append(src, begin, map[i] - begin);
                if (wordDecoratorEnable) {
                    buf.append(wd.decorateWord(new String(target, i, 1), w));
                } else {
                    buf.append(callback.decorator(new String(target, i, 1)));
                }
                begin = map[i] + 1;
            }
            buf.append(src, begin, map[w.begin + w.length - 1] - begin + 1);

        } else {
            if (wordDecoratorEnable) {
                buf.append(wd.decorateWord(new String(src, map[w.begin], map[w.begin + w.length - 1] - map[w.begin] + 1),
                                           w));
            } else {
                buf.append(callback.decorator(new String(src, map[w.begin], map[w.begin + w.length - 1] - map[w.begin]
                                                                            + 1)));
            }
        }
    }

    /**
     * 判断文本中是否包含字典中词，允许跳字匹配和部首匹配,例如“法-轮-功”和“氵去轮功”都能匹配。
     * 
     * @param content 文本
     * @param ignoreCase 是否或略大小写
     * @return 是否包含
     */
    @Override
    public boolean containTerm(String content, boolean ignoreCase) {
        if (content == null || content.length() == 0) {
            return false;
        }
        // 过滤html tag文本，全角转半角等
        content = config.getMappedExtracter() != null ? config.getMappedExtracter().getText(content, ignoreCase) : content;
        char inputSequence[] = content.toCharArray();
        int pos = 0;
        boolean hasTerm = false;
        while (pos < inputSequence.length) {
            List<WordTerm> wordList = darts.prefixSearch(inputSequence, pos, inputSequence.length - pos);
            if (wordList.size() > 0) {
                hasTerm = true;
                break;
            } else {
                pos++;
            }
        }
        return hasTerm;
    }

    /**
     * 查询给定内容中所有匹配到的高危词，并根据高危词的两个属性开关过滤掉不需要的。<br>
     * 匹配结果包含以下情况：<br>
     * 1、重复匹配，如“ab”和“bc”都是高危词，则对“abc”，两个词都在结果中<br>
     * 2、跳字匹配，如“法-轮-功”可以匹配法轮功<br>
     * 3、部首匹配，如果“氵去车仑功”可以匹配“法轮功”<br>
     * 
     * @see RadicalSkipDarts#prefixSearch(char[], int, int)
     * @see DecoratorText#containTerm(String, boolean)
     * @param content 需要查询的内容
     * @param ignoreCase 忽略大小写
     * @return 匹配到的词素列表，不会null，但是可以empty
     */
    @Override
    public List<WordTerm> parseTerms(String content, boolean ignoreCase) {
        if (content == null || content.length() == 0) {
            return new ArrayList<WordTerm>();
        }
        MappedCharArray mca = MappedCharArray.createInstance(content);
        return parseTerms(config.getMappedExtracter() != null ? config.getMappedExtracter().getText(mca) : mca,
                          ignoreCase, config.getSkip(), true, false);
    }

    /**
     * 现在存在奇怪的需求，同一个词表既要英文单词匹配，有时又要求禁止英文单词全词匹配，所以由此参数，以后查明 原因后可能删除。
     */
    public List<WordTerm> parseTermsForbidWholeWord(String content, boolean ignoreCase, boolean forbidWholeWord) {
        MappedCharArray mca = MappedCharArray.createInstance(content);
        return parseTerms(config.getMappedExtracter() != null ? config.getMappedExtracter().getText(mca) : mca,
                          ignoreCase, config.getSkip(), true, forbidWholeWord);
    }

    /**
     * 查询给定内容中所有匹配到的高危词，并根据高危词的两个属性开关过滤掉不需要的。<br>
     * 匹配结果包含以下情况：<br>
     * 1、重复匹配，如“ab”和“bc”都是高危词，则对“abc”，两个词都在结果中<br>
     * 2、跳字匹配，如“法-轮-功”可以匹配法轮功<br>
     * 3、部首匹配，如果“氵去车仑功”可以匹配“法轮功”<br>
     * 
     * @see RadicalSkipDarts#prefixSearch(char[], int, int)
     * @see DecoratorText#containTerm(String, boolean)
     * @param content 需要查询的内容
     * @param skip 允许跳过的字符数
     * @param isHtml 传入文本是否需要html过滤。
     * @param mapped parse后的WordTerm中的位置信息是否已经进行映射转换
     * @param forbidWholeWord 现在存在奇怪的需求，同一个词表既要英文单词匹配，有时又要求禁止英文单词全词匹配，所以由此参数，以后查明 原因后可能删除。
     * @return 匹配到的词素列表,不为null，可以为empty
     */
    private List<WordTerm> parseTerms(MappedCharArray content, boolean ignoreCase, int skip, boolean mapped,
                                      boolean forbidWholeWord) {
        List<WordTerm> result = new ArrayList<WordTerm>();
        char origin[] = content.getTarget();
        int len = 1;
        int contentLen = content.getCharCount();
        for (int pos = 0; pos < contentLen; pos += len) {
            len = 1;
            List<WordTerm> wordTermList = darts.prefixSearch(origin, pos, contentLen - pos);
            for (WordTerm term : wordTermList) {
                SkipTermExtraInfo word = (SkipTermExtraInfo) term.termExtraInfo;
                // 数据准备有误，忽略这个词
                if (word == null) {
                    continue;
                }
                // 如果该高危词不允许跳过字符，但匹配出了跳过字符的结果，忽略这个词
                if (!word.isAllowSkip() && term.length > word.getWord().length()) {
                    continue;
                }
                // 如果该高危词需要进行全词匹配，但它之前一个字符是英文字母或'_'或'-'，或者它之后为英文字母，则忽略这个词
                if (!forbidWholeWord && word.isWholeWord()) {
                    if (term.begin - 1 >= 0) {
                        char c = origin[term.begin - 1];
                        if (isEnglishLetter(c) || c == '_' || c == '-') {
                            continue;
                        }
                    }
                    if (term.begin + term.length < origin.length) {
                        char c = origin[term.begin + term.length];
                        if (isEnglishLetter(c)) {
                            continue;
                        }
                    }
                }
                if (term.termExtraInfo != null && term.termExtraInfo instanceof SkipTermExtraInfo) {
                    SkipTermExtraInfo info = (SkipTermExtraInfo) term.termExtraInfo;
                    if (info.getTransformType() != SkipTermExtraInfo.NONE_TYPE) {
                        if (!config.enablePinyinTransform() && info.getTransformType() == SkipTermExtraInfo.PINYIN_TYPE) {
                            continue;
                        }
                        if (!config.enableForkTransform() && info.getTransformType() == SkipTermExtraInfo.FORK_TYPE) {
                            continue;
                        }
                        if (!config.enableDeformTransform() && info.getTransformType() == SkipTermExtraInfo.DEFORM_TYPE) {
                            continue;
                        }
                        if (!config.enableHomophoneTransform()
                            && info.getTransformType() == SkipTermExtraInfo.HOMOPHONE_TYPE) {
                            continue;
                        }
                        if (skip == 0 && term.length > info.getWord().length()) {
                            continue;
                        }
                    }
                }
                if (mapped) {
                    // 还原字符映射消息，如过滤tag，此处可以保证term信息正确
                    int begin = term.begin;
                    int end = term.begin + term.length;
                    int[] map = content.getMap();
                    term.begin = map[begin];
                    term.length = map[end - 1] - map[begin] + 1;
                }
                if (config.isQuickMatch() && len < term.length) {
                    len = term.length;
                }
                result.add(term);
            }
        }
        return result;
    }

    /**
     * 找出文本中所有的词语列表，包含跳字匹配文本。<br>
     * 缺省返回词典中词语，例如“法-轮-功”匹配“法轮功”，在返回结果中就是带分隔符的“法轮功”，如果希望得到原始文本，请使用 parseTerms(String, boolean, boolean, boolean)
     * 
     * @param content 文本内容
     * @param ignoreCase 是否或略大小写
     * @param dupRemove 是否消除重复词语
     * @return 词语列表
     */
    @Override
    public List<String> parseTerms(String content, boolean ignoreCase, boolean dupRemove) {
        return parseTerms(content, ignoreCase, dupRemove, true);
    }

    /**
     * 找出文本中所有的词语列表。<br>
     * note:如果已经拿到List<WordTerm>，不需要分词和Parser的时候请使用PsoriasisUtil.parseWords(List<WordTerm>)可以大大提高性能。<br>
     * 
     * @see PsoriasisUtil#parseWordList(List)
     * @param content 文本内容
     * @param ignoreCase 是否或略大小写
     * @param dupRemove 是否消除重复词语
     * @param dicWord 返回word是词典中词语还是原始文本词语，例如原始“法-轮-功”匹配词典中“法轮功”，如果dicWord为true，返回词语为“法轮功”否则为包含分隔符的“法-轮-功”。
     * @return 词语列表，不会为null，但是可以empty
     */
    public List<String> parseTerms(String content, boolean ignoreCase, boolean dupRemove, boolean dicWord) {
        List<WordTerm> wordTermList = parseTerms(content, ignoreCase);
        List<String> wordList = new ArrayList<String>(wordTermList.size());
        for (WordTerm wordTerm : wordTermList) {
            if (wordTerm == null) {
                continue;
            }
            String word = null;
            if (dicWord) {
                TermExtraInfo info = wordTerm.termExtraInfo;
                if (info instanceof SkipTermExtraInfo) {
                    SkipTermExtraInfo skip = (SkipTermExtraInfo) info;
                    word = skip.getWord();
                }
            } else {
                if (wordTerm.begin >= 0 && wordTerm.begin + wordTerm.length < content.length()) {
                    word = content.substring(wordTerm.begin, wordTerm.begin + wordTerm.length);
                }
            }
            if (word != null) {
                wordList.add(word);
            }
        }
        if (dupRemove) {
            Set<String> set = new HashSet<String>();
            for (String w : wordList) {
                set.add(w);
            }
            wordList.clear();
            wordList.addAll(set);
        }
        return wordList;
    }

    public int getSkip() {
        return config.getSkip();
    }

    @Deprecated
    public void setSkip(int skip) {
        // this.skip = skip;
    }

    public MappedExtractText getExtractor() {
        return config.getMappedExtracter();
    }

    public void setExtractor(MappedExtractText extractor) {
        config.setMappedExtracter(extractor);
    }

    public WordDecorator getWordDecorator() {
        return config.getWordDecorator();
    }

    public void setWordDecorator(WordDecorator wordDecorator) {
        config.setWordDecorator(wordDecorator);
    }

    @Deprecated
    public void setExtract(MappedExtractText extract) {
        // this.extract = extract;
    }

    /**
     * 是否英文字符，只在a-zA-Z之间
     */
    private boolean isEnglishLetter(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }

}
