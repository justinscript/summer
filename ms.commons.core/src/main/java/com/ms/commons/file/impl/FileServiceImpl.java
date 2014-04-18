/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.file.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;

import com.ms.commons.file.interfaces.FileService;
import com.ms.commons.file.service.ImageUtil;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 1:21:34 PM
 */
public class FileServiceImpl implements FileService {

    private static Logger logger = LoggerFactoryWrapper.getLogger(FileServiceImpl.class);

    /**
     * 调用此API则直接把需要压缩的图全部一次性压缩出来
     * 
     * @param url 需要压缩的文件url
     * @param widths 需要压缩的宽度数组，这个参数和savePaths参数可以用一个小对象来进行处理
     * @param savePaths 压缩后的图片路径，需要和widths相对应
     */
    public boolean reduceAllPicture(String url, Double quality, Integer[] widths, String[] savePaths) {
        if (url == null || url.trim().length() == 0 || widths == null || savePaths == null || widths.length == 0
            || widths.length != savePaths.length) {
            return false;
        }
        try {
            Map<String, Integer> imageBasicInfo = ImageUtil.getImageWH(url);
            int originalImageWidth = imageBasicInfo.get(ImageUtil.IMAGE_WIDTH);
            int originalImageHeight = imageBasicInfo.get(ImageUtil.IMAGE_HEIGHT);
            for (int i = 0, len = widths.length; i < len; i++) {
                double widthBo = (double) widths[i] / originalImageWidth;
                int height = (int) Math.ceil(widthBo * originalImageHeight);
                ImageUtil.scaleImage(url, widths[i], height, savePaths[i], quality);
            }
            return true;
        } catch (Exception e) {
            logger.error("图片缩放错误：", e);
            return false;
        }
    }

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
    public boolean reduceAllPicture(String url, Double quality, int[] widths, int[] heights, String[] savePaths) {
        if (url == null || url.trim().length() == 0 || widths == null || widths.length == 0 || heights == null
            || heights.length == 0 || savePaths == null || savePaths.length == 0 || widths.length != savePaths.length
            || heights.length != savePaths.length) {
            return false;
        }
        try {
            Map<String, Integer> imageBasicInfo = ImageUtil.getImageWH(url);
            int originalImageWidth = imageBasicInfo.get(ImageUtil.IMAGE_WIDTH);
            int originalImageHeight = imageBasicInfo.get(ImageUtil.IMAGE_HEIGHT);
            for (int i = 0, len = widths.length; i < len; i++) {
                int width = widths[i];
                int height = heights[i];
                if (width <= 0 && height <= 0) {
                    continue;
                }
                // 定高，宽动态计算
                if (width <= 0) {
                    double heightBo = (double) height / originalImageHeight;
                    if (heightBo > 1) {
                        width = originalImageWidth;
                        height = originalImageHeight;
                    } else {
                        width = (int) Math.ceil(heightBo * originalImageWidth);
                    }
                }
                // 定宽，高动态计算
                else {
                    double widthBo = (double) width / originalImageWidth;
                    if (widthBo > 1) {
                        width = originalImageWidth;
                        height = originalImageHeight;
                    } else {
                        height = (int) Math.ceil(widthBo * originalImageHeight);
                    }
                }
                ImageUtil.scaleImage(url, width, height, savePaths[i], quality);
            }
            return true;
        } catch (Exception e) {
            logger.error("图片缩放错误：", e);
            return false;
        }
    }

    /**
     * 从前段得到的文件流到图片存放路径
     * 
     * @param inputStream
     * @return
     * @throws IOException
     */
    public void inputStreamToFile(InputStream inputStream, String savePath) throws Exception {
        if (inputStream == null) {
            return;
        }
        int index = savePath.lastIndexOf(File.separator);
        String dirString = savePath.substring(0, index);
        FileOutputStream out = null;
        try {
            File dir = new File(dirString);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File outFile = new File(savePath);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            out = new FileOutputStream(outFile);
            int c;
            byte buffer[] = new byte[1024];
            while ((c = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, c);
            }
        } catch (Exception e) {
            logger.error("图片转换错误:" + savePath + ":", e);
            throw e;
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 图片裁剪，该方法为默认裁剪中心defaultWidth*defaultWidth
     * 
     * @param oldpath
     * @param newpath
     */
    public boolean cutImage(String oldpath, String newpath, Double quality, int defaultWidth) {
        if (oldpath == null || newpath == null || defaultWidth <= 0) {
            return false;
        }
        try {

            int x = 0, y = 0, width = 0, height = 0;
            Map<String, Integer> whInfo = ImageUtil.getImageWH(oldpath);
            // 获得原始图片的宽度。
            int originalImageWidth = whInfo.get(ImageUtil.IMAGE_WIDTH);
            // 获得原始图片的高度。
            int originalImageHeight = whInfo.get(ImageUtil.IMAGE_HEIGHT);

            x = originalImageWidth / 2 - defaultWidth / 2;
            y = originalImageHeight / 2 - defaultWidth / 2;
            width = defaultWidth;
            height = defaultWidth;
            cutImage(x, y, width, height, oldpath, newpath, quality, defaultWidth);
            return true;
        } catch (Exception e) {
            logger.error("图片裁剪失败", e);
            return false;
        }
    }

    /**
     * 切割图片
     * 
     * @param x 截点横坐标 (从左开始计数)
     * @param y 截点纵坐标 (从上开始计数)
     * @param width 截取的宽度
     * @param height 截取的长度
     * @param oldpath 图片位置
     * @param newpath 新生成的图片位置
     */
    public boolean cutImage(int x, int y, int width, int height, String oldpath, String newpath, Double quality,
                            int defaultWidth) {
        if (oldpath == null || newpath == null || defaultWidth <= 0) {
            return false;
        }
        // 先裁剪，如果裁剪后图片大小不是默认大小，需要压缩成默认大小
        if (width <= defaultWidth) {
            return ImageUtil.cutImage(oldpath, newpath, quality, x, y, width, height);
        } else {
            return ImageUtil.cutAndScaleImage(oldpath, newpath, quality, x, y, width, height, defaultWidth,
                                              defaultWidth);
        }
    }

    /**
     * 垂直切割图片
     * 
     * @param srcFile 源文件
     * @param imgWidth 源图片的宽度
     * @param imgHeight 源图片的高度
     * @param expectedWidth 期望切割的宽度
     * @param expectedHeight 期望切割的高度
     * @return
     */
    public String[] splitImage(File srcFile, Double quality, Integer expectedWidth, Integer expectedHeight) {
        try {
            String srcFileName = srcFile.getPath();
            Map<String, Integer> whInfo = ImageUtil.getImageWH(srcFileName);
            // 获得原始图片的宽度。
            int imgWidth = whInfo.get(ImageUtil.IMAGE_WIDTH);
            // 获得原始图片的高度。
            int imgHeight = whInfo.get(ImageUtil.IMAGE_HEIGHT);

            // 列数
            int col = 1;
            if (expectedWidth != null) {
                col = imgWidth / expectedWidth;
                // 容错
                col = (col == 0 ? 1 : col);
                col = (imgWidth % expectedWidth) == 0 ? col : col + 1;
            }

            // 行数
            int row = 1;
            if (expectedHeight != null) {
                row = (imgHeight / expectedHeight);
                // 容错
                row = (row == 0 ? 1 : row);
                row = (imgHeight % expectedHeight) == 0 ? row : row + 1;
            }

            // 切割图片
            ConvertCmd convert = new ConvertCmd();
            IMOperation op = new IMOperation();
            if (quality != null && quality > 0) {
                op.quality(quality);
            }
            op.addImage(srcFile.getPath());
            op.crop(null, null, null, null, col + "x" + row + "@");

            int index = srcFileName.lastIndexOf('.');
            String prefix = srcFileName.substring(0, index);
            String suffix = srcFileName.substring(index);
            String destFileName = prefix + "_%d" + suffix;
            op.addImage(destFileName);
            convert.run(op);
            String[] files = new String[row];
            for (int i = 0; i < row; i++) {
                files[i] = prefix + "_" + i + suffix;
            }
            return files;
        } catch (Exception e) {
            logger.error("图片切割失败", e);
            return null;
        }
    }

    /**
     * 图片缩放
     */
    @Override
    public boolean scaleImage(String sourceFilePath, Double quality, int width, int height, String destFilePath) {
        return ImageUtil.scaleImage(sourceFilePath, width, height, destFilePath, quality);
    }

}
