/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.file.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.core.ImageCommand;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.im4java.process.ArrayListOutputConsumer;

import com.ms.commons.cons.ItemImageCons;
import com.ms.commons.file.image.GravityEnum;
import com.ms.commons.file.image.ImageFormat;
import com.ms.commons.lang.Assert;

/**
 * @author zxc Apr 12, 2013 1:18:30 PM
 */
public class ImageUtil implements ItemImageCons {

    /**
     * 图片质量
     */
    public static final String  IMAGE_QUALITY = "Quality";
    /**
     * 图片高度
     */
    public static final String  IMAGE_HEIGHT  = "Height";
    /**
     * 图片宽度
     */
    public static final String  IMAGE_WIDTH   = "Width";
    /**
     * 图片格式
     */
    public static final String  IMAGE_FORMAT  = "Format";

    /**
     * 
     */
    public static final boolean isDebug       = false;

    // 确保临时目录已经创建成功
    static {
        try {
            new File(TEMP_IMAGE_PATH).mkdirs();
        } catch (Exception e) {
        }
    }

    // 是否为调试模式
    public static boolean isDebugMode() {
        return isDebug;
    }

    /**
     * @param imgFile
     * @return
     * @throws InfoException
     */
    public static Info getImageInfo(String imgFile) throws InfoException {
        Info imageInfo = new Info(imgFile);
        return imageInfo;
    }

    /**
     * 获取图片属性
     * 
     * <pre>
     * identify -verbose 图片名
     * </pre>
     * 
     * @param imageInfo
     * @param propertyName
     * @return
     */
    public static String getProperty(Info imageInfo, String propertyName) {
        String propertyValue = imageInfo.getProperty(propertyName);
        if (propertyValue == null) {
            Enumeration<String> propertyNames = imageInfo.getPropertyNames();
            String value = null;
            while (propertyNames.hasMoreElements()) {
                value = propertyNames.nextElement();
                if (value.indexOf(propertyName) != -1) {
                    propertyValue = imageInfo.getProperty(value);
                    break;
                }
            }
        }
        return propertyValue;
    }

    // /////////////////////////////////////////////////////////////////////
    //
    // 图片处理方法
    //
    // /////////////////////////////////////////////////////////////////////
    /**
     * 查询图片的基本信息:格式,质量，宽度，高度
     * 
     * <pre>
     *    %b   file size of image read in
     *    %c   comment property
     *    %d   directory component of path
     *    %e   filename extension or suffix
     *    %f   filename (including suffix)
     *    %g   layer canvas page geometry   ( = %Wx%H%X%Y )
     *    %h   current image height in pixels
     *    %i   image filename (note: becomes output filename for "info:")
     *    %k   number of unique colors
     *    %l   label property
     *    %m   image file format (file magic)
     *    %n   exact number of images in current image sequence
     *    %o   output filename  (used for delegates)
     *    %p   index of image in current image list
     *    %q   quantum depth (compile-time constant)
     *    %r   image class and colorspace
     *    %s   scene number (from input unless re-assigned)
     *    %t   filename without directory or extension (suffix)
     *    %u   unique temporary filename (used for delegates)
     *    %w   current width in pixels
     *    %x   x resolution (density)
     *    %y   y resolution (density)
     *    %z   image depth (as read in unless modified, image save depth)
     *    %A   image transparency channel enabled (true/false)
     *    %C   image compression type
     *    %D   image dispose method
     *    %G   image size ( = %wx%h )
     *    %H   page (canvas) height
     *    %M   Magick filename (original file exactly as given,  including read mods)
     *    %O   page (canvas) offset ( = %X%Y )
     *    %P   page (canvas) size ( = %Wx%H )
     *    %Q   image compression quality ( 0 = default )
     *    %S   ?? scenes ??
     *    %T   image time delay
     *    %W   page (canvas) width
     *    %X   page (canvas) x offset (including sign)
     *    %Y   page (canvas) y offset (including sign)
     *    %Z   unique filename (used for delegates)
     *    %@   bounding box
     *    %#   signature
     *    %%   a percent sign
     *    \n   newline
     *    \r   carriage return
     * </pre>
     * 
     * @param imageFilePath 图片完整路径
     * @return 返回图片的属性Map信息,如果出错，则返回空Map对象
     */
    public static Map<String, String> getImageBasicInfo(String imageFilePath) {
        // create operation
        IMOperation op = new IMOperation();
        op.ping();
        // op.format("%m\n%w\n%h\n%g\n%W\n%H\n%G\n%z\n%r\n%Q");
        op.format("%m\n%w\n%h\n%Q");
        op.addImage(imageFilePath);

        try {
            // execute ...
            IdentifyCmd identify = new IdentifyCmd();
            ArrayListOutputConsumer output = new ArrayListOutputConsumer();
            identify.setOutputConsumer(output);
            identify.run(op);

            // ... and parse result
            ArrayList<String> cmdOutput = output.getOutput();
            Iterator<String> iter = cmdOutput.iterator();
            Map<String, String> map = new Hashtable<String, String>();
            map.put(IMAGE_FORMAT, iter.next());
            map.put(IMAGE_WIDTH, iter.next());
            map.put(IMAGE_HEIGHT, iter.next());
            map.put(IMAGE_QUALITY, iter.next());
            return map;
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    /**
     * 得到宽高属性
     */
    public static Map<String, Integer> getImageWH(String imageFilePath) {
        // create operation
        IMOperation op = new IMOperation();
        op.ping();
        // op.format("%m\n%w\n%h\n%g\n%W\n%H\n%G\n%z\n%r\n%Q");
        op.format("%w\n%h");
        op.addImage(imageFilePath);

        try {
            // execute ...
            IdentifyCmd identify = new IdentifyCmd();
            ArrayListOutputConsumer output = new ArrayListOutputConsumer();
            identify.setOutputConsumer(output);
            identify.run(op);

            // ... and parse result
            ArrayList<String> cmdOutput = output.getOutput();
            Iterator<String> iter = cmdOutput.iterator();
            Map<String, Integer> map = new Hashtable<String, Integer>();

            map.put(IMAGE_WIDTH, parseInt(iter.next()));
            map.put(IMAGE_HEIGHT, parseInt(iter.next()));
            return map;
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 垂直切割图片
     * 
     * @param srcFile 源文件
     * @param expectedWidth 期望切割的宽度, 如果为空或-1，表示不需要在x方向进行切割
     * @param expectedHeight 期望切割的高度，如果为空或-1，表示不需要在y方向进行切割
     * @return 切割后的子文件名列表
     */
    public static List<String> splitImage(File srcFile, Double quality, Integer expectedWidth, Integer expectedHeight) {
        try {
            String srcFileName = srcFile.getPath();
            int imgWidth;
            int imgHeight;
            try {
                Map<String, String> imageBasicInfo = getImageBasicInfo(srcFileName);
                imgWidth = Integer.parseInt(imageBasicInfo.get(IMAGE_WIDTH));
                imgHeight = Integer.parseInt(imageBasicInfo.get(IMAGE_HEIGHT));
            } catch (Exception e) {
                return Collections.emptyList();
            }

            // /////////////////////////////////////////////////////
            // debug
            // /////////////////////////////////////////////////////
            // String quality = imageBasicInfo.get(IMAGE_QUALITY);
            // String fileSize = getProperty(imageInfo, FILESIZE);
            // System.out.println(fileSize);
            // /////////////////////////////////////////////////////
            // debug
            // /////////////////////////////////////////////////////

            // 列数
            int col = 1;
            if (!(expectedWidth == null || expectedWidth == -1)) {
                col = imgWidth / expectedWidth;
                // 容错
                col = (col == 0 ? 1 : col);
            }

            // 行数
            int row = 1;
            if (!(expectedHeight == null || expectedHeight == -1)) {
                row = (imgHeight / expectedHeight);
                int left = imgHeight % expectedHeight;
                if (left > (expectedHeight / 2) && row > 0) {
                    row++;
                }
                // 容错
                row = (row == 0 ? 1 : row);
            }

            // 切割图片
            ConvertCmd convert = new ConvertCmd();
            IMOperation op = new IMOperation();
            // 图片质量缺省设置为80
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
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < row; i++) {
                list.add(prefix + "_" + i + suffix);
            }
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }

    }

    /**
     * 裁剪+缩放图片
     * 
     * @param sourceFilePath 源图片片路径
     * @param destFilePath 目标图片路径
     * @param xOffset 裁剪x偏移
     * @param yOffset 裁剪y偏移
     * @param width 裁剪宽度
     * @param height 裁剪高度
     * @param scaleWidth 缩放宽度
     * @param scaleHeight 缩放高度
     * @return
     */
    public static boolean cutAndScaleImage(String sourceFilePath, String destFilePath, Double quality, int xOffset,
                                           int yOffset, int width, int height, int scaleWidth, int scaleHeight) {
        Assert.assertPositive(width, "图片裁剪的期望宽度不能小于0");
        Assert.assertPositive(height, "图片裁剪的期望高度不能小于0");
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        // 临时图片
        String tmpDest = destFilePath /* + "_ross_tmp" */;
        // 图片质量缺省设置为80
        if (quality != null && quality > 0) {
            op.quality(quality);
        }
        op.addImage(sourceFilePath);
        op.crop(width, height, xOffset, yOffset);
        op.addImage(tmpDest);
        try {
            // 剪切
            convert.run(op);
        } catch (Exception e) {
            return false;
        }

        // 缩放
        op = new IMOperation();
        op.addImage(tmpDest);
        op.scale(scaleWidth, scaleHeight);
        op.addImage(destFilePath);
        try {
            convert.run(op);
            // 删除临时图片
            if (!StringUtils.equals(tmpDest, destFilePath)) {
                new File(tmpDest).delete();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 切割图片
     * 
     * @param xOffset 截点横坐标 (从左开始计数)
     * @param yOffset 截点纵坐标 (从上开始计数)
     * @param width 期望截取的图片的宽度
     * @param height 期望截取的图片长度
     * @param sourceFilePath 原始图片位置的完整路径，包括后缀名
     * @param destFilePath 新生成的图片位置的完整路径，包括后缀名
     * @return 成功返回true，否则返回false
     */
    public static boolean cutImage(String sourceFilePath, String destFilePath, Double quality, int xOffset,
                                   int yOffset, int width, int height) {
        Assert.assertPositive(width, "图片裁剪的期望宽度不能小于0");
        Assert.assertPositive(height, "图片裁剪的期望高度不能小于0");
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        // 图片质量缺省设置为80
        if (quality != null && quality > 0) {
            op.quality(quality);
        }
        op.addImage(sourceFilePath);
        op.crop(width, height, xOffset, yOffset);
        op.addImage(destFilePath);
        try {
            convert.run(op);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 图片缩放
     * 
     * @param sourceImage 原始图片
     * @param width 宽 图片期望缩放或的宽度
     * @param height 高 图片期望缩放后的高度
     * @param destFilePath 图片缩放后存储的完整路径--包括后缀名
     * @return 成功返回true，否则返回false
     */
    public static boolean scaleImage(BufferedImage sourceImage, int width, int height, String destFilePath,
                                     Double quality) {
        Assert.assertNotNull(sourceImage, "图片缩放的图片源不能为空");
        Assert.assertNotBlank(destFilePath, "图片缩放的目标存储位置不能为空");
        Assert.assertPositive(width, "图片缩放的期望宽度不能小于0");
        Assert.assertPositive(height, "图片缩放的期望高度不能小于0");
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        if (quality != null && quality > 0) {
            op.quality(quality);
        }
        op.addImage();
        op.resize(width, height);
        op.addImage();
        try {
            convert.run(op, sourceImage, destFilePath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 图片缩放
     * 
     * @param sourceFilePath 原始图片完整文件名
     * @param width 宽 图片期望缩放或的宽度
     * @param height 高 图片期望缩放后的高度
     * @param destFilePath 图片缩放后存储的完整路径--包括后缀名
     * @return 成功返回true，否则返回false
     */
    public static boolean scaleImage(String sourceFilePath, int width, int height, String destFilePath, Double quality) {
        Assert.assertNotBlank(sourceFilePath, "图片缩放的图片源不能为空");
        Assert.assertNotBlank(destFilePath, "图片缩放的目标存储位置不能为空");
        Assert.assertPositive(width, "图片缩放的期望宽度不能小于0");
        Assert.assertPositive(height, "图片缩放的期望高度不能小于0");
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        if (quality != null && quality > 0) {
            op.quality(quality);
        }
        op.addImage(sourceFilePath);
        op.resize(width, height);
        op.addImage(destFilePath);
        try {
            convert.run(op);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 图标处理功能
    //
    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 合并图片--如果合并成功，则返回合并后图片的地址
     * 
     * @param bgImage 主图
     * @param iconImage 图标
     * @param 图标放置的位置 东南，东北，西南，西北
     * @param x x坐标
     * @param y y坐标
     * @param width 图标的宽度
     * @param height 图标的高度
     */
    public static String compositImage(String imageSavePath, ImageFormat format, String bgImage, String iconImage,
                                       GravityEnum position, Integer x, Integer y, Integer width, Integer height,
                                       Double rotation) {
        return compositImage(imageSavePath, format, bgImage, iconImage, position, x, y, width, height, rotation, false,
                             null);
    }

    // 合并图片
    public static String compositImage(String imageSavePath, ImageFormat format, String bgImage, String iconImage,
                                       GravityEnum position, Integer x, Integer y, Integer width, Integer height,
                                       Double rotation, boolean deleteIconFile, Double quality) {
        ImageCommand cmd = new CompositeCmd();
        IMOperation op = new IMOperation();
        // 质量
        if (quality != null) {
            op.quality(quality);
        } else {
            op.quality(85d);
        }
        // ///////////////////////////////
        // NorthWest NorthEast
        //
        // SouthWest SouthEast
        // ///////////////////////////////
        // 旋转
        if (rotation != null) {
            op.rotate(rotation);
            op.background("none");
        }
        // 位置
        if (position != null) {
            op.gravity(position.getValue());
        }
        // 容错
        x = (x == null ? 0 : x);
        y = (y == null ? 0 : y);
        // 位置
        op.geometry(width, height, x, y);
        // 图标
        op.addImage();
        // 原始图片
        op.addImage();
        // 合并后的图片
        op.addImage();
        String dsrc = imageSavePath + System.currentTimeMillis() + format.getImageSuffix();
        try {
            cmd.run(op, iconImage, bgImage, dsrc);
            // 检查大小
            long size = new File(dsrc).length();
            boolean isTooBig = size > MAX_IMAGE_SIZE_512000;
            if (isTooBig) {
                // 压缩图片大小
                try {
                    ImageCommand cvt = new ConvertCmd();
                    op = new IMOperation();
                    // 质量
                    op.quality(Math.min((100d * MAX_IMAGE_SIZE_512000) / size % 100, 80d));
                    String dsrc2 = imageSavePath + System.currentTimeMillis() + format.getImageSuffix();
                    op.addImage();
                    op.addImage();
                    cvt.run(op, dsrc, dsrc2);
                    return dsrc2;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return dsrc;
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        } finally {
            if (deleteIconFile) {
                deleteFileQuitely(iconImage);
            }
        }

    }

    /**
     * 绘制带文字的圆
     * 
     * @param text 绘制文本
     * @param textColor 文本颜色
     * @param fontName 字体名称
     * @param circleFillColor 圆圈颜色
     * @param circleStrokeColor 圆圈边框颜色
     * @param x 绘制区域x起始位置
     * @param y 绘制区域y起始位置
     * @param w 绘制区域宽度和高度
     * @return
     */
    public static String drawCircleWithText(String imageSavePath, String text, String textColor, String fontName,
                                            Integer fontSize, String circleFillColor, String circleStrokeColor,
                                            Integer circleStrokeWidth, Integer x, Integer y, Integer w, Double rotation) {
        // 绘制圆圈
        String circleImagePath = drawCircle(w, w, circleFillColor, circleStrokeColor, circleStrokeWidth, x, y, w, 0d);
        // 绘制文本
        if (fontSize == null) {
            fontSize = 24;
        }
        String textImagePath = createTextImage(text, false, fontName, fontSize, textColor, 0d);
        // 合并图片
        String destImagePath = compositImage(imageSavePath, ImageFormat.PNG, circleImagePath, textImagePath,
                                             GravityEnum.Center, x, y, null, null, null);
        destImagePath = rotateImageWisely(destImagePath, rotation);
        // 删除临时文件
        if (!isDebugMode()) {
            deleteFileQuitely(circleImagePath);
            deleteFileQuitely(textImagePath);
        }
        return destImagePath;
    }

    /**
     * 绘制带文本的矩形
     * 
     * @param text
     * @param textColor
     * @param fontName
     * @param fontSize
     * @param rectFillColor
     * @param rectStrokeColor
     * @param rectStrokeWidth
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public static String drawRectangleWithText(String imageSavePath, String text, String textColor, String fontName,
                                               Integer fontSize, String rectFillColor, String rectStrokeColor,
                                               Integer rectStrokeWidth, Integer x, Integer y, Integer w, Integer h,
                                               Double rotation) {
        // 绘制圆圈
        String rectImagePath = drawRectangle(x, y, w, h, 0, rectFillColor, rectStrokeColor, rectStrokeWidth, 0d);
        // 绘制文本
        if (fontSize == null) {
            fontSize = 24;
        }
        String textImagePath = createTextImage(text, false, fontName, fontSize, textColor, 0d);
        // String textImagePath = drawText(text, PositionEnum.Center, fontName, fontSize, textColor, "none", w, h);
        int gap = 2;
        x += gap;
        // 合并图片
        String destImagePath = compositImage(imageSavePath, ImageFormat.PNG, rectImagePath, textImagePath,
                                             GravityEnum.Center, x, y, null, null, 0d);
        destImagePath = rotateImageWisely(destImagePath, rotation);
        // 删除临时文件
        if (!isDebugMode()) {
            deleteFileQuitely(rectImagePath);
            deleteFileQuitely(textImagePath);
        }
        return destImagePath;

    }

    /**
     * 创建文本图片
     * 
     * @param text 绘制的文本
     * @param autoWrapper 是否自动换行
     * @param fontName 字体名称
     * @param fontSize 字体大小
     * @param textColor 文本颜色
     * @return
     */
    public static String createTextImage(String text, boolean autoWrapper, String fontName, Integer fontSize,
                                         String textColor, Double rotation) {
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.background("none");
        op.antialias();
        if (StringUtils.isNotBlank(textColor)) {
            op.fill(textColor);
        }
        if (StringUtils.isNotBlank(fontName)) {
            op.font(fontName);
        }
        if (fontSize != null) {
            op.pointsize(fontSize);
        }
        if (autoWrapper) {
            op.addRawArgs("caption:" + text);
        } else {
            op.addRawArgs("label:" + text);
        }
        String destImage = TEMP_IMAGE_PATH + System.currentTimeMillis() + ImageFormat.PNG.getImageSuffix();
        op.addImage(destImage);
        try {
            convert.run(op);
            destImage = rotateImageWisely(destImage, rotation);
            return destImage;
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    /**
     * 绘制矩形
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     * @param fillColor
     * @param strokeColor
     * @param strokeWidth
     * @return
     */
    public static String drawRectangle(Integer x, Integer y, Integer w, Integer h, Integer round, String fillColor,
                                       String strokeColor, Integer strokeWidth, Double rotation) {
        // 容错
        x = (x == null ? 0 : x);
        y = (y == null ? 0 : y);
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.size(w, h);
        // op.addRawArgs("canvas:none");
        op.addRawArgs("xc:none");
        op.antialias();
        if (StringUtils.isNotBlank(fillColor)) {
            op.fill(fillColor);
        }

        boolean isValidStrokeWidth = strokeWidth != null && strokeWidth > 0;
        if (StringUtils.isNotBlank(strokeColor)) {
            op.stroke(strokeColor);
            if (!isValidStrokeWidth) {
                strokeWidth = 1;
                isValidStrokeWidth = true;
            }
        }
        if (isValidStrokeWidth) {
            op.strokewidth(strokeWidth);
            int gap = strokeWidth;
            if (w != null) {
                w -= gap;
            }
            if (h != null) {
                h -= gap;
            }
        }
        if (round == null) {
            round = 0;
        }
        // roundRectangle
        // String cmd = "rectangle " + x + "," + y + " " + w + "," + h;
        String cmd = "roundRectangle " + x + "," + y + " " + w + "," + h + " " + round + "," + round;
        op.draw(cmd);
        // FIXME：png支持透明背景
        String destImage = TEMP_IMAGE_PATH + System.currentTimeMillis() + ImageFormat.PNG.getImageSuffix();
        op.addImage(destImage);
        try {
            convert.run(op);
            destImage = rotateImageWisely(destImage, rotation);
            return destImage;
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    /**
     * 绘制圆
     * 
     * <pre>
     * circle和ellipse中的第一组参数都是代表圆心的坐标，但是他们第二组参数含义不同，circle的第二组参数是圆的任何一边缘坐标，
     * 所以圆的半径就是两组坐标的距离，ellipse的第二组参数中的第一个是横向的最大长度、第二个是纵向最大高度，
     * 第三组参数是绘制的区间，区间是0~360度，0度是原点开始到从左到右，度数是顺时针方向。有了这个参数就可以很容易绘制半圆，1/4圆，
     * 乃至圆的任何一部分，所以ellipse比circle的功能更加强大。
     * </pre>
     * 
     * @param width 绘制区域宽度
     * @param height 绘制区域高度
     * @param fillColor 填充色
     * @param bgColor 背景色
     * @param strokeColor
     * @param x
     * @param y
     * @param w
     * @return
     */
    public static String drawCircle(Integer width, Integer height, String fillColor, String strokeColor,
                                    Integer strokeWidth, Integer x, Integer y, Integer w, Double rotation) {

        int radias = w / 2;
        Integer centerX = x + radias;
        Integer centerY = y + radias;
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.size(width, height);
        op.addRawArgs("canvas:none");
        op.antialias();
        op.fill(fillColor);
        if (StringUtils.isNotBlank(strokeColor)) {
            op.stroke(strokeColor);
        }
        if (strokeWidth != null) {
            op.strokewidth(strokeWidth);
        }
        String cmd = "ellipse " + centerX + "," + centerY + " " + radias + "," + radias + " 0,360";
        op.draw(cmd);
        // FIXME：png支持透明背景
        String destImage = TEMP_IMAGE_PATH + System.currentTimeMillis() + ".png";
        op.addImage(destImage);
        try {
            convert.run(op);
            destImage = rotateImageWisely(destImage, rotation);
            return destImage;
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    // 无损旋转图片
    public static String rotateImage(String originalImagePath, Double rotation) {
        // FIXME：神医，很奇怪，在终端可以执行如下命令，但通过Java调用却报错，尚未找到解决办法，
        // 暂时使用Java的Runtime来绕过
        // convert 1372771154717.jpg -virtual-pixel none +distort SRT '20' rotate_normal2.png
        String destImage = TEMP_IMAGE_PATH + System.currentTimeMillis() + ".png";
        try {
            String command = null;
            command = "convert " + originalImagePath + " -virtual-pixel none +distort SRT '" + rotation + "' "
                      + destImage;
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            int exitValue = process.exitValue();
            return exitValue == 0 ? destImage : StringUtils.EMPTY;
        } catch (IOException e1) {
            e1.printStackTrace();
            return StringUtils.EMPTY;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
        // ConvertCmd convert = new ConvertCmd();
        // IMOperation op = new IMOperation();
        // op.addImage(originalImagePath);
        // op.addRawArgs(" -virtual-pixel none +distort SRT '" + rotation + "' ");
        // op.addImage(destImage);
        // try {
        // convert.run(op);
        // return destImage;
        // } catch (Exception e) {
        // e.printStackTrace();
        // return StringUtils.EMPTY;
        // }

    }

    // FIXME: 该方法不能正确工作，待未来完善
    public static String _rotateImage(String originalImagePath, Double rotation) {
        // convert 1372771154717.jpg -virtual-pixel none +distort SRT '20' rotate_normal2.png
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        // op.virtualPixel(" none ");
        // op.p_distort(" STR " + rotation);
        op.addRawArgs(" -virtual-pixel none +distort SRT '" + rotation + "' ");
        String destImage = TEMP_IMAGE_PATH + System.currentTimeMillis() + ".png";
        op.addImage();
        op.addImage();

        // String destImage = IMAGE_PATH + System.currentTimeMillis() + ".png";
        // op.addRawArgs(" " + originalImagePath + " -virtual-pixel none +distort SRT '20' " + destImage);
        String command = null;
        try {
            convert.run(op, originalImagePath, destImage);
            return destImage;
        } catch (Exception e) {
            e.printStackTrace();
            command = "convert " + originalImagePath + " -virtual-pixel none +distort SRT " + rotation + " "
                      + destImage;
            try {
                Runtime.getRuntime().exec(command);
                return destImage;
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return StringUtils.EMPTY;
        }

    }

    /**
     * 绘制椭圆
     * 
     * @param fillColor
     * @param strokeColor
     * @param strokeWidth
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public static String drawEclipse(String fillColor, String strokeColor, Integer strokeWidth, Integer x, Integer y,
                                     Integer w, Integer h, Double rotation) {
        int halfW = w / 2;
        int halfH = h / 2;

        Integer centerX = x + halfW;
        Integer centerY = y + halfH;
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.antialias();
        op.size(w, h);
        op.addRawArgs("canvas:none");
        op.fill(fillColor);
        if (StringUtils.isNotBlank(strokeColor)) {
            op.stroke(strokeColor);
        }
        if (strokeWidth != null) {
            op.strokewidth(strokeWidth);
        }
        String cmd = "ellipse " + centerX + "," + centerY + " " + halfW + "," + halfH + " 0,360";
        op.draw(cmd);
        // FIXME png支持透明背景
        String destImage = TEMP_IMAGE_PATH + System.currentTimeMillis() + ".png";
        op.addImage(destImage);
        try {
            convert.run(op);
            destImage = rotateImageWisely(destImage, rotation);
            return destImage;
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    /**
     * 给图片加边框
     * 
     * @param srcImage 原始图片
     * @param borderWidth 边框宽度
     * @param color 颜色为#00ff00格式
     * @return
     */
    public static String borderImage(String srcImage, int borderWidth, String color) {
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.border(borderWidth);
        if (StringUtils.isNotBlank(color)) {
            op.bordercolor(color);
        }
        String destImage = TEMP_IMAGE_PATH + System.currentTimeMillis() + ImageFormat.PNG.getImageSuffix();
        op.addImage(srcImage);
        op.addImage(destImage);
        try {
            convert.run(op);
            return destImage;
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    // 删除文件
    private static boolean deleteFileQuitely(String fileName) {
        return FileUtils.deleteQuietly(new File(fileName));
    }

    // 不改变图片大小的旋转
    private static String rotateImageWisely(String destImagePath, Double rotation) {
        if (rotation != null && rotation % 360 != 0) {
            // try {
            // Thread.sleep(100);
            // } catch (InterruptedException e) {
            // }
            String rotateImagePath = rotateImage(destImagePath, rotation);
            if (!StringUtils.isBlank(rotateImagePath)) {
                destImagePath = rotateImagePath;
            } else {
                // TODO: 写错误日志
            }
        }
        return destImagePath;
    }

    public static String drawText(String text, GravityEnum gravity, String fontName, Integer fontSize,
                                  String textColor, String bgColor, Integer width, Integer height, Double rotation) {
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.antialias();
        op.size(width, height);
        if (StringUtils.isBlank(bgColor)) {
            bgColor = "none";
        }
        op.addRawArgs("xc:" + bgColor);
        op.fill(textColor);
        if (gravity != null) {
            op.gravity(gravity.getValue());
        }
        op.font(fontName);
        if (fontSize == null) {
            fontSize = DEFAULT_FONT_SIZE;
        }
        op.pointsize(fontSize);
        op.draw("text 0,0 '" + text + "'");

        String destImage = TEMP_IMAGE_PATH + System.currentTimeMillis() + ImageFormat.PNG.getImageSuffix();
        op.addImage(destImage);
        try {
            convert.run(op);// 图片旋转
            destImage = rotateImageWisely(destImage, rotation);
            return destImage;
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    /**
     * 图片自适应大小 -- 去掉透明色
     * 
     * @param srcImagePath
     * @return
     */
    public static String autoResize(String srcImagePath) {
        return autoResize(srcImagePath, null);
    }

    public static String autoResize(String srcImagePath, String destImagePath) {
        ConvertCmd convert = new ConvertCmd();
        IMOperation op = new IMOperation();
        op.addImage(srcImagePath);

        op.trim();
        op.p_repage();
        if (StringUtils.isBlank(destImagePath)) {
            destImagePath = srcImagePath;
        }
        op.addImage(destImagePath);
        try {
            convert.run(op); // 图片旋转
            return destImagePath;
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }
}
