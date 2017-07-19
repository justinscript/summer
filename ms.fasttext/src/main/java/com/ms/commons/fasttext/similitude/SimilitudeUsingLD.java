/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.similitude;

/**
 * @author zxc Apr 12, 2013 3:21:59 PM
 */
public class SimilitudeUsingLD implements SimilitudeText {

    public double similitudeValue(IDocument first, IDocument document) {

        int s[] = (int[]) first.getWordTerms();
        int d[] = (int[]) document.getWordTerms();

        if (s.length == 0 && d.length == 0) {
            return 1;
        }
        return 1 - (double) (this.getEditDistance(s, d)) / (double) (Math.max(s.length, d.length));
    }

    int getEditDistance(int src[], int dest[]) {
        if (src == null || dest == null) {
            // 出错
            return -1;
        }
        // 如果src是空串，那么差异度为dest的长度
        if (src.length == 0) {
            return dest.length;
        }

        if (dest.length == 0) {
            return src.length;
        }

        // 排列src的字符串作为竖列，排列dest的字符串作为横列，出现一个二维矩阵
        // 第一维是src，第二维是dest
        int[][] matrix = new int[src.length + 1][dest.length + 1];

        // src和dest都为空串的特殊情况，编辑距离为0
        matrix[0][0] = 0;

        // [1][0]代表src长度为1，而dest为空串时的编辑距离，以此类推
        for (int i = 1; i <= src.length; i++) {
            matrix[i][0] = i;
        }

        // [0][1]代表src为空串，而dest长度为1时的编辑距离，以此类推
        for (int i = 1; i <= dest.length; i++) {
            matrix[0][i] = i;
        }

        // 显然[0][0]是不需要比较的，它的编辑距离为0
        for (int i = 1; i <= src.length; i++) {
            // 取src中的第i个字符
            int i_src = src[i - 1];

            // 假设src的长度已经确定，它是个完整的字符串，长度为i
            // 求整个dest相对于src[i]所有编辑距离，则应：
            // 分别求出dest[0],dest[1]...dest[j]对于src的编辑距离
            for (int j = 1; j <= dest.length; j++) {
                // 取dest中的第j个字符
                int j_dest = dest[j - 1];

                // case 1
                int c1;
                // 设src长度为i-1，dest长度为j-1，它们的编辑距离为k
                // 则长度为i的src与长度为j的dest之间的编辑距离可以通过：
                // a. 将长度i-1的src编辑为长度j-1的dest，这需要代价k
                // b. 长度为i的src相当于长度为i-1的src加上第i个字符
                // c. 将src第i个字符替换成dest第j个字符，需要代价cost
                // d. 如果src第i个字符与dest第j个字符相同，cost=0
                // e. 如果(d)不成立，则cost=1
                // f. 最终长度为i的src与长度为j的dest的编辑距离为k+cost

                // 如src中当前字符与dest中当前字符相等，则无编辑代价，反之为1
                int cost = i_src == j_dest ? 0 : 1;

                // 长度为i的src与长度为j的dest的编辑距离为matrix[i][j]
                c1 = matrix[i - 1][j - 1] + cost;

                // case 2
                int c2;
                // 设src长度为i-1，dest长度为j，它们的编辑距离为k
                // 则长度为i的src与长度为j的dest之间的编辑距离可以通过：
                // a. 将长度i-1的src编辑为长度j的dest，这需要代价k
                // b. 将长度为i的src编辑成长度i-1的src，需要删去第i个字符
                // c. (b)中的代价为1
                // d. 从(b)得到长度为i-1的src，再从(a)得到长度为j的dest
                // e. 最终长度为i的src与长度为j的dest的编辑距离为k+1

                c2 = matrix[i - 1][j] + 1;

                // case 3
                int c3;
                // 设src长度为i，dest长度为j-1，它们的编辑距离为k
                // 则长度为i的src与长度为j的dest之间的编辑距离可以通过：
                // a. 将长度i的src编辑为长度j-1的dest，这需要代价k
                // b. 将长度j-1的dest编辑成长度j的dest需要加上第j个字符
                // c. (b)中的代价为1
                // d. 从(a)得到长度为j-1的dest，再从(b)得到长度为j的dest
                c3 = matrix[i][j - 1] + 1;

                // 综上所述三种可能性，求最小的值做为编辑代价
                matrix[i][j] = c1 <= c2 ? c1 : c2;
                if (matrix[i][j] > c3) {
                    matrix[i][j] = c3;
                }
            }
        }
        // 长度为i的src与长度为j的dest的编辑距离为matrix[i][j]
        return matrix[src.length][dest.length];
    }
}
