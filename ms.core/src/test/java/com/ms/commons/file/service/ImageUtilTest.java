/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.file.service;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author zxc Apr 12, 2013 1:27:37 PM
 */
public class ImageUtilTest {

    @Test
    public void getImageBaseInfo() {
        String imageFilePath = getImageFullPath("01.jpg");
        try {
            Map<String, String> imageBaseInfo = ImageUtil.getImageBasicInfo(imageFilePath);
            print(imageBaseInfo);

            org.junit.Assert.assertNotNull(imageBaseInfo);
            Assert.assertEquals("JPEG", imageBaseInfo.get(ImageUtil.IMAGE_FORMAT));
            Assert.assertEquals("80", imageBaseInfo.get(ImageUtil.IMAGE_QUALITY));
            Assert.assertEquals("220", imageBaseInfo.get(ImageUtil.IMAGE_WIDTH));
            Assert.assertEquals("220", imageBaseInfo.get(ImageUtil.IMAGE_HEIGHT));

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void getImageBaseInfo_png() {
        String imageFilePath = getImageFullPath("10.png");
        try {
            Map<String, String> imageBaseInfo = ImageUtil.getImageBasicInfo(imageFilePath);
            print(imageBaseInfo);
            org.junit.Assert.assertNotNull(imageBaseInfo);
            Assert.assertEquals("PNG", imageBaseInfo.get(ImageUtil.IMAGE_FORMAT));
            Assert.assertEquals("0", imageBaseInfo.get(ImageUtil.IMAGE_QUALITY));
            Assert.assertEquals("220", imageBaseInfo.get(ImageUtil.IMAGE_WIDTH));
            Assert.assertEquals("220", imageBaseInfo.get(ImageUtil.IMAGE_HEIGHT));

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getImageBaseInfo_tiff() {
        String imageFilePath = getImageFullPath("20.tiff");
        try {
            Map<String, String> imageBaseInfo = ImageUtil.getImageBasicInfo(imageFilePath);
            print(imageBaseInfo);
            org.junit.Assert.assertNotNull(imageBaseInfo);
            Assert.assertEquals("TIFF", imageBaseInfo.get(ImageUtil.IMAGE_FORMAT));
            Assert.assertEquals("0", imageBaseInfo.get(ImageUtil.IMAGE_QUALITY));
            Assert.assertEquals("220", imageBaseInfo.get(ImageUtil.IMAGE_WIDTH));
            Assert.assertEquals("220", imageBaseInfo.get(ImageUtil.IMAGE_HEIGHT));

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void splitImage() {
        String imageFilePath = getImageFullPath("01.jpg");
        List<String> splitImage = ImageUtil.splitImage(new File(imageFilePath), 80d, -1, 60);
        Assert.assertTrue(splitImage.size() > 0);
        for (String fileName : splitImage) {
            boolean delete = new File(fileName).delete();
            Assert.assertTrue(delete);
        }
    }

    @Test
    public void cutImage() {
        String imageFilePath = getImageFullPath("01.jpg");
        String newPath = imageFilePath + "_crop_" + imageFilePath.substring(imageFilePath.lastIndexOf('.'));
        boolean success = ImageUtil.cutImage(imageFilePath, newPath, 80d, 50, 50, 100, 100);
        Assert.assertTrue(success);
        boolean delete = new File(newPath).delete();
        Assert.assertTrue(delete);
    }

    @Test
    public void scaleImage() {
        String sourceFilePath = getImageFullPath("01.jpg");
        Map<String, String> imageBaseInfo = ImageUtil.getImageBasicInfo(sourceFilePath);

        String destFilePath = sourceFilePath + "_scale_" + sourceFilePath.substring(sourceFilePath.lastIndexOf('.'));
        int width = Integer.parseInt(imageBaseInfo.get(ImageUtil.IMAGE_WIDTH)) / 2;
        int height = Integer.parseInt(imageBaseInfo.get(ImageUtil.IMAGE_HEIGHT)) / 2;
        boolean success = ImageUtil.scaleImage(sourceFilePath, width, height, destFilePath, 80d);
        Assert.assertTrue(success);
        boolean delete = new File(destFilePath).delete();
        Assert.assertTrue(delete);
    }

    @SuppressWarnings("unused")
    @Test
    public void scaleImage_bufferedImage() {
        String sourceFilePath = getImageFullPath("01.jpg");
        BufferedImage image = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(sourceFilePath));
            image = ImageIO.read(is);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
            return;
        }

        Map<String, String> imageBaseInfo = ImageUtil.getImageBasicInfo(sourceFilePath);

        String destFilePath = sourceFilePath + "_scale_buffered_"
                              + sourceFilePath.substring(sourceFilePath.lastIndexOf('.'));
        int width = Integer.parseInt(imageBaseInfo.get(ImageUtil.IMAGE_WIDTH)) / 2;
        int height = Integer.parseInt(imageBaseInfo.get(ImageUtil.IMAGE_HEIGHT)) / 2;
        boolean success = ImageUtil.scaleImage(image, width, height, destFilePath, 80d);
        Assert.assertTrue(success);

        Map<String, String> imageBasicInfo = ImageUtil.getImageBasicInfo(destFilePath);
        // Iterator<String> iterator = imageBasicInfo.keySet().iterator();
        // while (iterator.hasNext()) {
        // String key = iterator.next();
        // String value = imageBaseInfo.get(key);
        // System.out.println(key + "=" + value);
        // }

        boolean delete = new File(destFilePath).delete();
        Assert.assertTrue(delete);

        // destFilePath = sourceFilePath + "_scale_datou_" + sourceFilePath.substring(sourceFilePath.lastIndexOf('.'));
        // reducePicture(sourceFilePath, destFilePath, width, height);
    }

    /**
     * @param imageFileName
     * @return
     */
    private String getImageFullPath(String imageFileName) {
        String parentPath = "com.ms.commons/file/service/";
        URL resource = getClass().getClassLoader().getResource(parentPath + imageFileName);
        return resource.getFile();
    }

    /**
     * @param imageBaseInfo
     */
    private void print(Map<String, String> imageBaseInfo) {
        // Iterator<String> iterator = imageBaseInfo.keySet().iterator();
        // while (iterator.hasNext()) {
        // String key = iterator.next();
        // String value = imageBaseInfo.get(key);
        // System.out.println(key + "=" + value);
        // }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // debug

    // private void reducePicture(String url, String savePath, int w, int h) {
    // try {
    // // 处理url，得到绝对路径
    // BufferedImage originalPic = ImageIO.read(new FileInputStream(url));
    // // 获得原始图片的宽度。
    // int originalImageWidth = originalPic.getWidth();
    // // 获得原始图片的高度。
    // int originalImageHeight = originalPic.getHeight();
    //
    // // 宽度比例
    // double widthBo = (double) w / originalImageWidth;
    // // 高度比例动态计算
    // if (h < 0) {// 高度根据宽度比例计算
    // h = (int) (widthBo * originalImageHeight);
    // }
    // reduceImg(originalPic, w, h, savePath);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    //
    // /**
    // * 缩放img
    // *
    // * @param img
    // * @param w
    // * @param h
    // * @param savePath
    // * @throws Exception
    // */
    // private void reduceImg(BufferedImage img, int w, int h, String savePath) throws Exception {
    // // 生成处理后的图片存储空间。
    // BufferedImage changedImage = new BufferedImage(w, h, img.getType());
    // changedImage.getGraphics().drawImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, w, h, null); //
    // 绘制缩小后的图
    // FileOutputStream out = new FileOutputStream(savePath);
    // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    // JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(changedImage);
    // param.setQuality(0.8f, false);
    // encoder.setJPEGEncodeParam(param);
    // encoder.encode(changedImage);
    // out.close();
    // }

}
