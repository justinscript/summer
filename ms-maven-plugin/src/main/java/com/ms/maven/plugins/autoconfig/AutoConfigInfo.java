package com.ms.maven.plugins.autoconfig;

import java.util.HashMap;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class AutoConfigInfo {

    HashMap<String, String> renderMap   = new HashMap<String, String>();
    HashMap<String, String> unRenderMap = new HashMap<String, String>();

    public HashMap<String, String> getRenderMap() {
        return renderMap;
    }

    public void setRenderMap(HashMap<String, String> renderMap) {
        this.renderMap = renderMap;
    }

    public HashMap<String, String> getUnRenderMap() {
        return unRenderMap;
    }

    public void setUnRenderMap(HashMap<String, String> unRenderMap) {
        this.unRenderMap = unRenderMap;
    }
}
