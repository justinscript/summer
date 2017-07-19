/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.comset.filter.info;

import java.util.List;

import com.ms.commons.comset.filter.RecordEnum;

/**
 * @author zxc Apr 12, 2013 5:18:14 PM
 */
public class UnitInfo extends LeafInfo {

    public UnitInfo() {
    }

    public UnitInfo(RecordEnum type, String name) {
        super(type, name);
    }

    /**
     * 添加DB，Search这一层Type的节点
     * 
     * @param leaf
     */
    public void addTypeLeaf(LeafInfo leaf) {
        if (leaf == null || leaf.getType() == null) {
            return;
        }
        LeafInfo typeLeaf = getLeafUnit(leaf.getType(), leaf.getType().name(), true);
        if (typeLeaf instanceof UnitInfo) {
            ((UnitInfo) typeLeaf).addTypeSonLeaf(leaf);
            typeLeaf.addRunTime(leaf.getCount(), leaf.getPeriod());
        }
    }

    private void addTypeSonLeaf(LeafInfo leaf) {
        if (leaf == null) {
            return;
        }
        LeafInfo sonLeaf = getLeafUnit(leaf.getType(), leaf.getName(), true);
        if (sonLeaf == null) {
            return;
        }
        sonLeaf.addRunTime(leaf.getCount(), leaf.getPeriod());
    }

    /**
     * 返回DB、Searh这一层的节点
     * 
     * @param type
     * @param create
     * @return
     */
    public LeafInfo getLeafUnit(RecordEnum type, String name, boolean create) {
        if (type == null || name == null) {
            return null;
        }
        UnitInfo leaf = null;
        List<LeafInfo> sonList = getLeafList();
        if (sonList != null) {
            for (LeafInfo tmp : sonList) {
                if (name.equals(tmp.getName())) {
                    return tmp;
                }
            }
        }
        if (create) {
            leaf = new UnitInfo(type, name);
            addLeaf(leaf);
            return leaf;
        }
        return null;
    }

    /**
     * 返回DB、Searh这一层的节点
     * 
     * @param type
     * @param create
     * @return
     */
    public LeafInfo getTypeUnit(int index) {
        RecordEnum tmpType = RecordEnum.DB;
        if (index == 1) {
            tmpType = RecordEnum.SERVICE;
        } else if (index == 2) {
            tmpType = RecordEnum.CACHE;
        }
        return getLeafUnit(tmpType, tmpType.name(), false);
    }
}
