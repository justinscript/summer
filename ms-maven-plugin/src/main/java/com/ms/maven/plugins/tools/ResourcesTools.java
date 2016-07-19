package com.ms.maven.plugins.tools;

import java.io.InputStream;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class ResourcesTools {

    public static InputStream getResourceAsStream(String name, Class<?> clzz) {
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (inStream != null) return inStream;
        ClassLoader c = clzz.getClassLoader();
        if (c != null) {
            inStream = c.getResourceAsStream(name);
            if (inStream != null) return inStream;
        }
        return ClassLoader.getSystemResourceAsStream(name);
    }
}
