/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.file.interfaces;

import java.io.File;
import java.io.InputStream;

/**
 * 图片处理接口
 * 
 * @author zxc Apr 12, 2013 1:22:24 PM
 */
public interface FileService {

    /**
     * 根据路径存储文件
     * 
     * @param inputStream
     * @param savePath
     */
    void inputStreamToFile(InputStream inputStream, String savePath) throws Exception;

    /**
     * 图片缩放
     * 
     * @param url
     * @param h
     * @param w
     * @return
     */
    // void reducePicture(String url, String savePath, int w, int h);

    /**
     * 裁剪图片
     * 
     * @param oldpath
     * @param newpath
     */
    boolean cutImage(String oldpath, String newpath, Double quality, int defaultWidth);

    /**
     * 裁剪图片
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param oldpath
     * @param newpath
     */
    boolean cutImage(int x, int y, int width, int height, String oldpath, String newpath, Double quality,
                     int defaultWidth);

    /**
     * 根据bufferedImage和大小压缩
     * 
     * @param img
     * @param w
     * @param h
     * @param savePath
     */
    // void reduceImg(BufferedImage img, int w, int h, String savePath) throws Exception;

    /**
     * 生成所有小图
     * 
     * @param url
     * @param savePath
     * @param w1
     * @param w2
     * @param w3
     */
    boolean reduceAllPicture(String url, Double quality, Integer[] widths, String[] savePaths);

    /**
     * 生成所有小图
     * 
     * @param url
     * @param quality
     * @param widths
     * @param heights
     * @param savePaths
     * @return
     */
    boolean reduceAllPicture(String url, Double quality, int[] widths, int[] heights, String[] savePaths);

    /**
     * 垂直切割图片
     * 
     * @param srcFile 源文件
     * @param quality 压缩的本例，如果为空或 <=0 表示不需要压缩图片质量
     * @param expectedWidth 期望切割的宽度
     * @param expectedHeight 期望切割的高度
     * @return
     */
    public String[] splitImage(File srcFile, Double quality, Integer expectedWidth, Integer expectedHeight);

    /**
     * @param sourceFilePath
     * @param width
     * @param height
     * @param destFilePath
     * @return
     */
    public boolean scaleImage(String sourceFilePath, Double quality, int width, int height, String destFilePath);
}
