package com.ms.commons.weixin.bean;

import java.util.*;

public class QYWeixinUserAttrs {

    private List<Attr> attrs;

    /**
     * @return the attrs
     */
    public List<Attr> getAttrs() {
        return attrs;
    }

    /**
     * @param attrs the attrs to set
     */
    public void setAttrs(List<Attr> attrs) {
        this.attrs = attrs;
    }

    public Map<String, String> toMap() {
        if (attrs == null || attrs.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<String, String> map = new HashMap<String, String>();
        for (Attr attr : attrs) {
            map.put(attr.getName(), attr.getValue());
        }
        return map;
    }

    public String toString() {
        return "QYWeixinUserAttrs [attrs=" + attrs + "]";
    }

}
