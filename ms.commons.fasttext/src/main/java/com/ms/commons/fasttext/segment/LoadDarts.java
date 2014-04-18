/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.segment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author zxc Apr 12, 2013 3:45:36 PM
 */
public class LoadDarts {

    public static Darts load(String dicFile) throws IOException, ClassNotFoundException {
        InputStream fis = new FileInputStream(dicFile);
        return load(fis);

    }

    public static Darts load(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(in);
        Darts dat = (Darts) ois.readObject();
        ois.close();
        return dat;
    }

    public static Darts load(File resourceAsFile) throws IOException, ClassNotFoundException {
        InputStream fis = new FileInputStream(resourceAsFile);
        return load(fis);
    }
}
