/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.psoriasis;

import com.ms.commons.fasttext.extract.CharNormalization;
import com.ms.commons.fasttext.extract.CharTableLoader;

/**
 * <h3>HTML Decorator使用的文本过滤器，主要完成以下功能： <br>
 * </h3> 1、过滤html Tag <br>
 * 2、繁体转简体 <br>
 * 3、全角转半角 <br>
 * 4、大写转小写(可选） <br>
 * 5、过滤多个连续空格和回车等字符，只保留一个<br>
 * 
 * @author zxc Apr 12, 2013 3:35:02 PM
 */
public class CompositeHTMLExtractText extends HTMLParserExtractText {

    private boolean needHtml;
    private boolean needT2S;
    private boolean needDBC;
    private boolean ignoreCase;
    private boolean filterNoneHanLetter;
    private boolean convertSynonymy;
    private boolean filterSymbol;
    private boolean keepLastSymbol;

    public CompositeHTMLExtractText(CharTableLoader loader) {
        this(loader, false);
    }

    public CompositeHTMLExtractText(CharTableLoader loader, boolean convertSynonymy) {
        this(loader, true, true, true, true, false, convertSynonymy, true, true);
    }

    public CompositeHTMLExtractText(CharTableLoader loader, boolean needHtml, boolean needT2S, boolean needDBC,
                                    boolean ignoreCase, boolean filterNoneHanLetter, boolean convertSynonymy,
                                    boolean filterSymbol, boolean keepLastSymbol) {
        this(needHtml, needT2S, needDBC, ignoreCase, filterNoneHanLetter, convertSynonymy, filterSymbol, keepLastSymbol);
        CharNormalization.setLoader(loader);
    }

    public CompositeHTMLExtractText() {
        this(false);
    }

    public CompositeHTMLExtractText(boolean convertSynonymy) {
        this(true, true, true, true, false, convertSynonymy, true, true);
    }

    public CompositeHTMLExtractText(boolean needHtml, boolean needT2S, boolean needDBC, boolean ignoreCase,
                                    boolean filterNoneHanLetter, boolean convertSynonymy, boolean filterSymbol,
                                    boolean keepLastSymbol) {
        super();
        this.needHtml = needHtml;
        this.needT2S = needT2S;
        this.needDBC = needDBC;
        this.ignoreCase = ignoreCase;
        this.filterNoneHanLetter = filterNoneHanLetter;
        this.convertSynonymy = convertSynonymy;
        this.filterSymbol = filterSymbol;
        this.keepLastSymbol = keepLastSymbol;
    }

    /**
     * HTML Decorator使用的文本过滤器，提供字符映射表功能，主要完成以下功能：<br>
     * 1、过滤html Tag <br>
     * 2、繁体转简体 <br>
     * 3、全角转半角 <br>
     * 4、大写转小写 <br>
     * 5、过滤多个连续空格和回车等字符，只保留一个<br>
     */
    public MappedCharArray getText(MappedCharArray src) {
        return getText(src, true);
    }

    /**
     * HTML Decorator使用的文本过滤器，提供字符映射表功能，主要完成以下功能： <br>
     * 1、过滤html Tag <br>
     * 2、繁体转简体 <br>
     * 3、全角转半角 <br>
     * 4、大写转小写（可选） <br>
     * 5、过滤多个连续空格和回车等字符，只保留一个<br>
     */
    public MappedCharArray getText(MappedCharArray src, boolean ignoreCase) {
        if (src == null) {
            return src;
        }
        MappedCharArray text = src;
        if (needHtml) {
            text = super.getText(src, false);
        }
        int[] map = text.getMap();
        char[] target = text.getTarget();
        int len = text.getCharCount();
        int current = 0;
        for (int i = 0; i < len; i++) {
            char c = target[i];
            char ret = CharNormalization.compositeCharConvert(c, needT2S, needDBC, ignoreCase, filterNoneHanLetter,
                                                              convertSynonymy, filterSymbol);
            // 处理连续symbol问题
            if (ret == CharNormalization.DEFAULT_BLANK_CHAR) {
                if (keepLastSymbol && i < target.length - 1) {
                    char next = target[i + 1];
                    if (CharNormalization.isSeperatorSymbol(c) && !CharNormalization.isSeperatorSymbol(next)) {
                        c = CharNormalization.getCharFromTable(c);
                        target[current] = c;
                        map[current++] = map[i];
                        continue;
                    }
                }
            }
            // 处理其它过滤过程
            if (ret != CharNormalization.DEFAULT_BLANK_CHAR) {
                target[current] = ret;
                map[current++] = map[i];
            } else {
                text.decreaseCharCount(1);
            }
        }
        return text;
    }

    /**
     * HTML Decorator使用的文本过滤器，主要完成以下功能： <br>
     * 1、过滤html Tag <br>
     * 2、繁体转简体 <br>
     * 3、全角转半角 <br>
     * 4、大写转小写（可选） <br>
     * 5、过滤多个连续空格和回车等字符，只保留一个<br>
     */
    public String getText(String html, boolean ignoreCase) {
        if (html == null) {
            return html;
        }
        String text = html;
        if (needHtml) {
            text = super.getText(html, false);
        }
        if (text == null || text.length() == 0) {
            return text;
        }
        return CharNormalization.compositeTextConvert(text, needT2S, needDBC, ignoreCase, filterNoneHanLetter,
                                                      convertSynonymy, filterSymbol, keepLastSymbol);
    }

    /**
     * HTML Decorator使用的文本过滤器，主要完成以下功能： <br>
     * 1、过滤html Tag <br>
     * 2、繁体转简体 <br>
     * 3、全角转半角 <br>
     * 4、大写转小写 <br>
     * 5、过滤多个连续空格和回车等字符，只保留一个<br>
     */
    public String getText(String html) {
        return getText(html, true);
    }

    public boolean isNeedHtml() {
        return needHtml;
    }

    public void setNeedHtml(boolean needHtml) {
        this.needHtml = needHtml;
    }

    public boolean isNeedT2S() {
        return needT2S;
    }

    public void setNeedT2S(boolean needT2S) {
        this.needT2S = needT2S;
    }

    public boolean isNeedDBC() {
        return needDBC;
    }

    public void setNeedDBC(boolean needDBC) {
        this.needDBC = needDBC;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isFilterNoneHanLetter() {
        return filterNoneHanLetter;
    }

    public void setFilterNoneHanLetter(boolean filterNoneHanLetter) {
        this.filterNoneHanLetter = filterNoneHanLetter;
    }

    public boolean isConvertSynonymy() {
        return convertSynonymy;
    }

    public void setConvertSynonymy(boolean convertSynonymy) {
        this.convertSynonymy = convertSynonymy;
    }

    public boolean isFilterBlank() {
        return filterSymbol;
    }

    public void setFilterBlank(boolean filterBlank) {
        this.filterSymbol = filterBlank;
    }

    public boolean isKeepLastSymbol() {
        return keepLastSymbol;
    }

    public void setKeepLastSymbol(boolean keepLastSymbol) {
        this.keepLastSymbol = keepLastSymbol;
    }
}
