/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext;

import junit.framework.TestCase;

/**
 * @author zxc Apr 12, 2013 3:12:45 PM
 */
public class FasttextServiceTest extends TestCase {

    public void testDecorator() {

        String src = "法轮功";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "法轮轮功";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "法－轮－功";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "三去轮功";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "法lun功";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "法 lun 功";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "法伦";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "中国法伦";
        assertEquals("中国***", FasttextService.decorator(src));
        assertEquals("中国###", FasttextService.decorator(src, "###"));

        src = "中国";
        assertEquals("中国", FasttextService.decorator(src));
        assertEquals("中国", FasttextService.decorator(src, "###"));

        src = "六四事件";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "大家一起练习法轮功";
        assertEquals("大家一起练习***", FasttextService.decorator(src));
        assertEquals("大家一起练习###", FasttextService.decorator(src, "###"));

        src = "六 四  事  件";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "中~共~暴~政";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));

        src = "中共练法轮功暴政";
        assertEquals("中共练***暴政", FasttextService.decorator(src));
        assertEquals("中共练###暴政", FasttextService.decorator(src, "###"));

        src = "中共的暴政";
        assertEquals("***", FasttextService.decorator(src));
        assertEquals("###", FasttextService.decorator(src, "###"));
        assertEquals("", FasttextService.decorator(src, ""));

        src = "fa 轮 功我的氵去的车仑工力hao发lun功oiu法轮工,大家都退党啊~~全国退党";
        assertEquals("***我的***hao***oiu***,大家都***啊~~***", FasttextService.decorator(src));
        assertEquals("###我的###hao###oiu###,大家都###啊~~###", FasttextService.decorator(src, "###"));
        assertEquals("我的haooiu,大家都啊~~", FasttextService.decorator(src, ""));

    }

    public void testContainTerm() {
        String src = "法轮功";
        assertEquals(true, FasttextService.containTerm(src));

        src = "大家好";
        assertEquals(false, FasttextService.containTerm(src));

        src = "fa 轮 功我的氵去的车仑工力hao发lun功oiu法轮工,大家都退党啊~~全国退党";
        assertEquals(true, FasttextService.containTerm(src));

        src = "<html><body><div>我是大法弟子</div></body></html>";
        assertEquals(true, FasttextService.containTerm(src));
    }

    public void testGetPinyingOfHan() {
        String src = "韩";
        String[] py = FasttextService.getPinyingOfHan(src);
        assertNotNull(py.length);
        assertEquals(1, py.length);
        assertEquals("han", py[0]);

        // 角 gu,jiao,jue,lu
        src = "角";
        py = FasttextService.getPinyingOfHan(src);
        assertNotNull(py.length);
        assertEquals(4, py.length);
        assertEquals("gu", py[0]);
        assertEquals("jiao", py[1]);
        assertEquals("jue", py[2]);
        assertEquals("lu", py[3]);
    }

    public void testParserExtractText() {

        String html = "<html><body><div>我是大ABC法弟子</div></body></html>";
        String result = FasttextService.parserExtractText(html);
        assertEquals("我是大ABC法弟子", result);
        result = FasttextService.parserExtractText(html, true);
        assertEquals("我是大abc法弟子", result);
    }

    public void testCompositeTextConvert() {
        String src = "<html><body><b>text<br>s        dfsd</b></body></html>";
        String result = FasttextService.compositeTextConvert(src, true, true, true, false, true, true, true);
        assertEquals("<html><body><b>text<br>s dfsd</b></body></html>", result);
    }

    public void testgetCombination1() {
        char[] c1 = { 'a', 'b', 'c' };
        char[] c2 = { 'x', 'y' };
        char[] c3 = { '1', '2' };
        char[][] cs = { c1, c2, c3 };
        String[] aaa = FasttextService.getCombination(cs);
        assertEquals(aaa.length, 12);
        assertEquals(aaa[0], "ax1");
        assertEquals(aaa[1], "ax2");
        assertEquals(aaa[2], "ay1");
        assertEquals(aaa[3], "ay2");
        assertEquals(aaa[4], "bx1");
        assertEquals(aaa[5], "bx2");
        assertEquals(aaa[6], "by1");
        assertEquals(aaa[7], "by2");
        assertEquals(aaa[8], "cx1");
        assertEquals(aaa[9], "cx2");
        assertEquals(aaa[10], "cy1");
        assertEquals(aaa[11], "cy2");
    }

    public void testgetCombination2() {
        char[] c1 = { 'a' };
        char[] c2 = { 'x' };
        char[] c3 = { '1' };
        char[][] cs = { c1, c2, c3 };
        String[] aaa = FasttextService.getCombination(cs);
        assertEquals(aaa.length, 1);
        assertEquals(aaa[0], "ax1");
    }

    public void testgetCombination3() {
        char[] c1 = { 'b' };
        char[] c2 = { 'a', 'b' };
        char[] c3 = { 'b' };
        char[][] cs = { c1, c2, c3 };
        String[] aaa = FasttextService.getCombination(cs);
        assertEquals(aaa.length, 2);
        assertEquals(aaa[0], "bab");
        assertEquals(aaa[1], "bbb");
    }

    public void testgetCombination4() {
        char[] c1 = { 'D', 'C' };
        char[] c2 = { 'C', 'X' };
        char[][] cs = { c1, c2 };
        String[] aaa = FasttextService.getCombination(cs);
        assertEquals(aaa.length, 4);
        assertEquals(aaa[0], "DC");
        assertEquals(aaa[1], "DX");
        assertEquals(aaa[2], "CC");
        assertEquals(aaa[3], "CX");
    }

    public void testescape() {
        String str = "<script>";
        String result = FasttextService.escape(str);
        assertEquals("&lt;script&gt;", result);
    }
}
