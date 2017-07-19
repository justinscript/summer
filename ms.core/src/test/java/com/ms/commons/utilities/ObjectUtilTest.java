/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.utilities;

import junit.framework.TestCase;

import org.junit.Test;

import com.ms.commons.lang.ObjectUtils;
import com.ms.commons.utilities.bo.Architect;
import com.ms.commons.utilities.bo.Programmer;

/**
 * @author zxc Apr 12, 2013 1:28:32 PM
 */
public class ObjectUtilTest extends TestCase {

    @Test
    public void testtirm() {
        Architect a = new Architect();
        a.setLevel(" level ");
        a.setName(" name ");
        ObjectUtils.trim(a);
        assertEquals("level", a.getLevel());
        assertEquals(" name ", a.getName());
    }

    @Test
    public void testtirm2() {
        Architect a = new Architect();
        a.setLevel(" level ");
        a.setLanguage(" language ");
        a.setName(" name ");
        ObjectUtils.trim(a, Programmer.class);
        assertEquals("level", a.getLevel());
        assertEquals("language", a.getLanguage());
        assertEquals(" name ", a.getName());
    }

    @Test
    public void testtirm3() {
        Architect a = new Architect();
        a.setLevel(" level ");
        a.setLanguage(" language ");
        a.setName(" name ");
        ObjectUtils.trim(a, null);
        assertEquals("level", a.getLevel());
        assertEquals("language", a.getLanguage());
        assertEquals("name", a.getName());
    }

    @Test
    public void testtirm4() {
        Architect a = new Architect();
        a.setLevel(" level ");
        a.setLanguage(" language ");
        a.setName(" name ");
        ObjectUtils.trim(a, true);
        assertEquals("level", a.getLevel());
        assertEquals("language", a.getLanguage());
        assertEquals("name", a.getName());
    }

    @Test
    public void testtirm5() {
        Architect a = new Architect();
        a.setLevel(" level ");
        a.setLanguage(" language ");
        a.setName(" name ");
        ObjectUtils.trim(a, false);
        assertEquals("level", a.getLevel());
        assertEquals(" language ", a.getLanguage());
        assertEquals(" name ", a.getName());
    }
}
