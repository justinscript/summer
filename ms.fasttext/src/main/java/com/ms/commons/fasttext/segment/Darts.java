/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.segment;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zxc Apr 12, 2013 3:18:39 PM
 */
public class Darts implements Externalizable {

    private static final long serialVersionUID = 5420875306574617251L;
    // 节点信息
    protected int             baseArray[];
    protected int             checkArray[];
    // 保存节点已经使用
    transient private boolean usedArray[];
    protected TermExtraInfo   extraNodeArray[];

    transient private int     nextCheckPos;
    transient protected int   maxCode          = 0;
    protected boolean         hasExtraData     = false;

    private static int        MAX_CJK_CODE     = 65536;

    public void build(List<char[]> wordList) {
        this.build(wordList, null);
    }

    public void build(List<char[]> wordList, VocabularyProcess process) {
        if (wordList == null) {
            return;
        }
        int size = wordList.size();
        if (size > 0) {
            List<InternalElement> elements = null;
            if (process == null) {
                process = new DefaultVocabularyProcess();
            }
            elements = process.postProcess(wordList);
            hasExtraData = !(process instanceof DefaultVocabularyProcess);
            Collections.sort(elements);
            resize(1);
            baseArray[0] = 1;
            nextCheckPos = 0;
            TermNode root_node = new TermNode();
            root_node.left = 0;
            root_node.right = size;
            root_node.depth = 0;
            List<TermNode> siblings = createSiblings();
            fetch(elements, root_node, siblings);
            insert(elements, siblings);
            // repack data
            resize(maxCode + MAX_CJK_CODE);

        }
    }

    private int insert(List<InternalElement> internalElements, List<TermNode> siblings) {
        int begin = 0;
        int nonZeroCount = 0;
        boolean first = false;

        int pos = (siblings.get(0).code + 1 > nextCheckPos ? siblings.get(0).code + 1 : nextCheckPos) - 1;
        if (pos >= usedArray.length) {
            resize(pos + 1);
        }
        while (true) {
            pos++;

            if (pos >= usedArray.length) {
                resize(pos + MAX_CJK_CODE);
            }
            if (checkArray[pos] != 0) {
                nonZeroCount++;
                continue;
            } else if (!first) {
                nextCheckPos = pos;
                first = true;
            }
            begin = pos - siblings.get(0).code;

            int t = begin + siblings.get(siblings.size() - 1).code;
            if (t > usedArray.length) {
                resize(t + MAX_CJK_CODE);
            }

            if (usedArray[begin]) {
                continue;
            }
            boolean flag = false;
            for (int i = 1; i < siblings.size(); i++) {
                if (checkArray[begin + siblings.get(i).code] != 0) {
                    flag = true;
                    break;
                }
            }
            if (!flag) break;
        }

        if (1.0 * nonZeroCount / (pos - nextCheckPos + 1) >= 0.95) {
            nextCheckPos = pos;
        }
        usedArray[begin] = true;
        for (TermNode termNode : siblings) {
            checkArray[begin + termNode.code] = begin;
        }

        for (TermNode termNode : siblings) {
            List<TermNode> newSiblings = createSiblings();
            if (fetch(internalElements, termNode, newSiblings) == 0) {
                baseArray[begin + termNode.code] = -termNode.left - 1;
                if (hasExtraData) {
                    extraNodeArray[begin + termNode.code] = internalElements.get(termNode.left).termExtraInfo;
                }
            } else {
                int ins = insert(internalElements, newSiblings);
                baseArray[begin + termNode.code] = ins;
            }
            if (begin + termNode.code > maxCode) {
                maxCode = begin + termNode.code;
            }
        }

        return begin;
    }

    private List<TermNode> createSiblings() {
        return new ArrayList<TermNode>();
    }

    private void resize(int size) {
        int len = 0;
        if (baseArray != null) {
            if (size > baseArray.length) {
                len = baseArray.length;
            } else {
                len = size;
            }
        }
        // checkArray array
        int tmp[] = new int[size];
        if (baseArray != null) {
            System.arraycopy(baseArray, 0, tmp, 0, len);
        }
        baseArray = tmp;

        // baseArray array
        int tmp1[] = new int[size];
        if (checkArray != null) {
            System.arraycopy(checkArray, 0, tmp1, 0, len);
        }
        checkArray = tmp1;

        // usedArray array
        boolean tmp2[] = new boolean[size];
        if (usedArray != null) {
            System.arraycopy(usedArray, 0, tmp2, 0, len);
        }
        usedArray = tmp2;

        // extraNodeArray
        if (hasExtraData) {
            TermExtraInfo tmp3[] = new TermExtraInfo[size];
            if (extraNodeArray != null) {
                System.arraycopy(extraNodeArray, 0, tmp3, 0, len);
            }
            extraNodeArray = tmp3;
        }
    }

    private int fetch(List<InternalElement> words, TermNode parent, List<TermNode> siblings) {
        int prev = 0;
        TermNode preNode = null;
        for (int i = parent.left; i < parent.right; i++) {
            char word[] = words.get(i).sequence;
            int len = word.length;
            if (len < parent.depth) {
                continue;
            }
            int cur = 0;
            if (len != parent.depth) {
                cur = word[parent.depth] + 1;
            }

            if (prev > cur) {
                throw new RuntimeException("Fatal: given strings are not sorted.\n");
            }
            if (cur != prev || siblings.size() == 0) {
                TermNode tmpNode = new TermNode();
                tmpNode.depth = parent.depth + 1;
                tmpNode.code = cur; // 重新计算每个字的映射？
                tmpNode.left = i;
                if (len == parent.depth + 1) {
                    tmpNode.termExtraInfo = words.get(i).termExtraInfo;
                }
                if (preNode != null) {
                    preNode.right = i;
                }
                preNode = tmpNode;
                siblings.add(tmpNode);
            }
            prev = cur;
        }

        if (preNode != null) {
            preNode.right = parent.right;
        }
        return siblings.size();
    }

    public int search(String key) {
        return search(key.toCharArray(), 0, key.length());
    }

    public int search(char key[], int pos, int len) {

        if (baseArray == null) {// 传入空字典，内部没有初始化，出现NPE，赵一涵发现
            return -1;
        }
        if (len == 0) {
            len = key.length;
        }
        int b = baseArray[0];
        int p;

        for (int i = pos; i < pos + len; i++) {// zgl17458:此处应为pos+len，

            p = b + key[i] + 1;
            if (b == checkArray[p]) {
                b = baseArray[p];
            } else {
                return -1;
            }
        }
        p = b;
        int n = baseArray[p];
        if (b == checkArray[p] && n < 0) {
            return -n - 1;
        }
        return -1;
    }

    public List<WordTerm> prefixSearch(char[] key, int pos, int len) {
        List<WordTerm> result = new ArrayList<WordTerm>();

        if (baseArray == null) {// 传入空字典，内部没有初始化，出现NPE，赵一涵发现
            return result;
        }
        int p, n, i, b = baseArray[0];

        for (i = pos; i < pos + len; ++i) {// zgl17458:此处应为pos+len，
            p = b; // + 0;
            n = baseArray[p];
            if (b == checkArray[p] && n < 0) {
                WordTerm w = new WordTerm();
                w.position = -n - 1;
                w.begin = pos;
                w.length = i - pos;
                w.termExtraInfo = extraNodeArray[p];
                result.add(w);
            }
            p = b + (key[i]) + 1;
            if (b == checkArray[p]) {
                b = baseArray[p];
            } else {
                return result;
            }
        }
        p = b;
        n = baseArray[p];
        if (b == checkArray[p] && n < 0) {
            WordTerm w = new WordTerm();
            w.position = -n - 1;
            w.begin = pos;
            w.length = i - pos;
            w.termExtraInfo = extraNodeArray[p];
            result.add(w);
        }

        return result;
    }

    public WordTerm prefixSearchMax(char[] key, int pos, int len) {

        if (baseArray == null) {// 传入空字典，内部没有初始化，出现NPE，赵一涵发现
            return null;
        }
        int p, n, i, b = baseArray[0];
        WordTerm w = null;
        for (i = pos; i < pos + len; ++i) {
            p = b; // + 0;
            n = baseArray[p];
            if (b == checkArray[p] && n < 0) {
                if (w == null) {
                    w = new WordTerm();
                }
                w.position = -n - 1;
                w.begin = pos;
                w.length = i - pos;
                if (hasExtraData) {
                    w.termExtraInfo = extraNodeArray[p];
                }
            }
            p = b + (key[i]) + 1;
            if (b == checkArray[p]) {
                b = baseArray[p];
            } else {
                return w;
            }
        }
        p = b;
        n = baseArray[p];
        if (b == checkArray[p] && n < 0) {
            if (w == null) {
                w = new WordTerm();
            }
            w.position = -n - 1;
            w.begin = pos;
            w.length = i - pos;
            if (hasExtraData) {
                w.termExtraInfo = extraNodeArray[p];
            }
        }
        return w;
    }

    /**
     * <pre>
     * protected int baseArray[]
     * protected int checkArray[]
     * protected TermExtraInfo extraNodeArray[]
     * </pre>
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        hasExtraData = in.readBoolean();
        baseArray = (int[]) in.readObject();
        checkArray = (int[]) in.readObject();
        extraNodeArray = (TermExtraInfo[]) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(hasExtraData);
        out.writeObject(baseArray);
        out.writeObject(checkArray);
        out.writeObject(extraNodeArray);
    }

}

class TermNode {

    int           code;
    int           depth;
    int           left;
    int           right;
    TermExtraInfo termExtraInfo;
}
