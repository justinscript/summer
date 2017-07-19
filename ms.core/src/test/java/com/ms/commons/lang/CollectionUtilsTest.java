/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.lang;

import static com.ms.commons.collection.Wrapper.collection;

import org.junit.Assert;
import org.junit.Test;

import com.ms.commons.lang.CollectionUtils.MatchMode;
import com.ms.commons.lang.CollectionUtils.Op;

/**
 * @author zxc Apr 12, 2013 2:59:32 PM
 */
public class CollectionUtilsTest {

    @Test
    public void matchall() {
        Assert.assertTrue(CollectionUtils.match(MatchMode.all, collection(new String[] { "a", "b" }), Op.containsBy,
                                                "abc"));
        Assert.assertFalse(CollectionUtils.match(MatchMode.all, collection(new String[] { "abcd", "b" }), Op.contain,
                                                 "abc"));
        Assert.assertTrue(CollectionUtils.match(MatchMode.all, collection(new String[] { "abcd", "abcd" }), Op.equal,
                                                "abcd"));

        Assert.assertTrue(CollectionUtils.match(MatchMode.all, collection(new String[] { "abcd", "fabcd" }),
                                                Op.containIgnoreCase, "ABC"));

        Assert.assertFalse(CollectionUtils.match(MatchMode.all, collection(new String[] { "abcd", "fabcd" }),
                                                 Op.containIgnoreCase, "eABC"));
    }

    @Test
    public void matchany() {
        Assert.assertTrue(CollectionUtils.match(MatchMode.any, collection(new String[] { "a", "b" }), Op.containsBy,
                                                "abc"));
        Assert.assertTrue(CollectionUtils.match(MatchMode.any, collection(new String[] { "abcd", "abcddafsasd" }),
                                                Op.equal, "abcd"));

        Assert.assertTrue(CollectionUtils.match(MatchMode.any, collection(new String[] { "abcdadfad", "fabcd" }),
                                                Op.containIgnoreCase, "ABCD"));

        Assert.assertFalse(CollectionUtils.match(MatchMode.any, collection(new String[] { "abcd", "fabcd" }),
                                                 Op.containIgnoreCase, "eABC"));
    }

    @Test
    public void matchsall() {
        Assert.assertTrue(CollectionUtils.matchs(MatchMode.all, collection(new String[] { "a", "b" }), Op.containsBy,
                                                 collection(new String[] { "abc", "ab", "c" })));
        Assert.assertTrue(CollectionUtils.matchs(MatchMode.all, collection(new String[] { "abcd", "bc" }), Op.contain,
                                                 collection(new String[] { "b", "c", "e" })));
        Assert.assertTrue(CollectionUtils.matchs(MatchMode.all, collection(new String[] { "abcd", "abcd" }), Op.equal,
                                                 collection(new String[] { "abcd", "abcd" })));

        Assert.assertTrue(CollectionUtils.matchs(MatchMode.all, collection(new String[] { "abcd", "fabcd" }),
                                                 Op.containIgnoreCase, collection(new String[] { "ABC", "CD", "AB" })));

        Assert.assertFalse(CollectionUtils.matchs(MatchMode.all, collection(new String[] { "abcd", "fabd" }),
                                                  Op.containIgnoreCase,
                                                  collection(new String[] { "abc", "abcde", "e" })));
    }

    @Test
    public void matchsany() {
        Assert.assertTrue(CollectionUtils.matchs(MatchMode.any, collection(new String[] { "a", "b" }), Op.containsBy,
                                                 collection(new String[] { "b", "ef" })));
        Assert.assertTrue(CollectionUtils.matchs(MatchMode.any, collection(new String[] { "b", "f" }), Op.contain,
                                                 collection(new String[] { "b", "c", "e" })));
        Assert.assertFalse(CollectionUtils.matchs(MatchMode.any, collection(new String[] { "h", "f" }), Op.contain,
                                                  collection(new String[] { "b", "c", "e" })));
        Assert.assertTrue(CollectionUtils.matchs(MatchMode.any, collection(new String[] { "abcd", "abcdf" }), Op.equal,
                                                 collection(new String[] { "abcd", "abcadsfd" })));

        Assert.assertTrue(CollectionUtils.matchs(MatchMode.any, collection(new String[] { "abcd", "fabcd" }),
                                                 Op.containIgnoreCase, collection(new String[] { "ABC", "CD" })));

        Assert.assertFalse(CollectionUtils.matchs(MatchMode.any, collection(new String[] { "abcd", "eabcd" }),
                                                  Op.containIgnoreCase, collection(new String[] { "fabc", "abcde" })));
    }
}
