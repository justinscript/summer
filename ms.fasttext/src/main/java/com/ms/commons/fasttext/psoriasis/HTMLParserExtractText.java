/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.psoriasis;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.StyleTag;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

/**
 * 使用HTMLParser進行html tag過濾，在html不規範時也可以正確過濾，性能和 質量都高於Swing的html parser。 类HTMLParserExtractText.java的实现描述
 * 
 * @author zxc Apr 12, 2013 3:34:24 PM
 */
public class HTMLParserExtractText implements MappedExtractText {

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等
     * 
     * @param html 输入html文本
     * @param ignoreCase 结果是否需要转换为小写。
     * @return 转换后的文本
     */
    public String getText(String html, boolean ignoreCase) {
        if (html == null) {
            return html;
        }
        try {
            Parser parser = new Parser();
            parser.setInputHTML(html);
            HTMLVisitor visitor = new HTMLVisitor();
            parser.visitAllNodesWith(visitor);
            String ret = visitor.toString();
            return ignoreCase ? ret.toLowerCase() : ret;
        } catch (ParserException e) {
        }
        return null;
    }

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等
     * 
     * @param src 输入html文本，提供映射功能
     * @return 转换后的文本
     */
    public MappedCharArray getText(MappedCharArray src) {
        return getText(src, false);
    }

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等，保留大小写信息。
     * 
     * @param src 输入html文本
     * @return 转换后的文本
     */
    public String getText(String src) {
        return getText(src, false);
    }

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等。 提供字符串映射功能。
     */
    public MappedCharArray getText(MappedCharArray src, boolean ignoreCase) {
        if (src == null) {
            return src;
        }
        try {
            Parser parser = new Parser();
            String content = new String(src.getTarget(), 0, src.getCharCount());
            if (ignoreCase) {
                content = content.toLowerCase();
            }
            parser.setInputHTML(content);
            parser.setNodeFactory(parser.getLexer());// 设置一个简单的NodeFactory
            HTMLVisitor visitor = new HTMLVisitor(src);
            parser.visitAllNodesWith(visitor);
            src.decreaseCharCount(src.getCharCount() - visitor.getCharCount());
            return visitor.getResult();
        } catch (ParserException e) {
        }
        return null;
    }

    private class HTMLVisitor extends NodeVisitor {

        private StringBuilder   buffer  = new StringBuilder();
        private MappedCharArray mca;
        private int             current = 0;
        private int             excludeStart, excludeEnd;

        HTMLVisitor() {
            super();
        }

        HTMLVisitor(MappedCharArray mca) {
            this();
            this.mca = mca;

        }

        public void visitStringNode(Text node) {
            int start = node.getStartPosition();
            if (start > excludeStart && start < excludeEnd) {
                return;
            }
            Node parent = node.getParent();
            if (parent != null
                && (parent.getClass().isAssignableFrom(ScriptTag.class) || parent.getClass().isAssignableFrom(StyleTag.class))) {
                Tag tag = (Tag) parent;
                Tag endTag = tag.getEndTag();
                int s = tag.getStartPosition();
                int e = endTag.getEndPosition();
                if (s < excludeStart) {
                    excludeStart = s;
                }
                if (e > excludeEnd) {
                    excludeEnd = e;
                }
                return;
            }
            int pos = node.getStartPosition();
            String str = node.getText();
            if (str != null) {
                if (mca != null) {
                    int[] map = mca.getMap();
                    char[] target = mca.getTarget();
                    for (int i = 0; i < str.length(); i++) {
                        target[current] = str.charAt(i);
                        if (pos < map.length) {
                            map[current++] = map[pos++];
                        }
                    }
                } else {
                    buffer.append(str);
                }
            }
        }

        public int getCharCount() {
            return current;
        }

        public String toString() {
            return buffer.toString();
        }

        public MappedCharArray getResult() {
            return mca;
        }
    }
}
