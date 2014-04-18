/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.comset.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;

import com.ms.app.web.commons.utils.MimeFileResultAdapter;
import com.ms.commons.comset.filter.ResourceTools;
import com.ms.commons.comset.filter.info.UnitInfo;
import com.ms.commons.file.interfaces.FileService;
import com.ms.commons.file.service.FileServiceLocator;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.summer.web.annotations.ControllerAction;
import com.ms.commons.summer.web.servlet.mvc.AbstractController;
import com.ms.commons.summer.web.servlet.result.WebResult;
import com.ms.commons.udas.impl.UdasServiceImpl;
import com.ms.commons.udas.impl.handler.AbstractKVHandler;
import com.ms.commons.udas.interfaces.UdasService;
import com.ms.commons.udas.service.UdasServiceLocator;

/**
 * comset监控控制类
 * 
 * @author zxc Apr 12, 2013 11:02:28 PM
 */
public class ComsetController extends AbstractController {

    private static Logger       logger        = LoggerFactoryWrapper.getLogger(ComsetController.class);
    private static final String staticDirPath = "/msun/static/nfs/uzaiimg";
    private static final byte[] emptyBytes    = new byte[0];
    private FileService         fileService   = FileServiceLocator.getFileService();

    @ControllerAction
    public WebResult show(Map<String, Object> model, final String sortcount, final String clear) throws Exception {
        if ("true".equals(clear)) {
            ResourceTools.clear();
        }
        List<UnitInfo> list = ResourceTools.getRecordUnitInfo();

        Collections.sort(list, new Comparator<UnitInfo>() {

            public int compare(UnitInfo o1, UnitInfo o2) {
                long v1 = o1.getCount();
                long v2 = o2.getCount();
                if ("false".equals(sortcount)) {
                    v1 = o1.getPeriod() / o1.getCount();
                    v2 = o2.getPeriod() / o2.getCount();
                }
                if (v1 < v2) {
                    return 1;
                } else if (v1 > v2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        if (list.size() > 100) {
            List<UnitInfo> newlist = new ArrayList<UnitInfo>(100);
            for (int i = 0; i < 100; i++) {
                newlist.add(list.get(i));
            }
            model.put("comsetList", newlist);
        } else {
            model.put("comsetList", list);
        }
        model.put("sort_type", "false".equals(sortcount) ? 0 : 1);
        return toView("/comset/show.htm");
    }

    @ControllerAction
    public WebResult udas(Map<String, Object> model) throws Exception {
        Map<String, UdasService> allMap = UdasServiceLocator.getAllUdasServiceMap();
        List<UdasInfo> udasList = new ArrayList<UdasInfo>();
        if (allMap != null) {
            Iterator<String> ir = allMap.keySet().iterator();
            while (ir.hasNext()) {
                String key = ir.next();
                Object obj = allMap.get(key);
                if (obj instanceof UdasServiceImpl) {
                    UdasServiceImpl v = (UdasServiceImpl) obj;
                    String dealHandler = "none";
                    // String dealHandler = v.getRealDealHander() == null ? "none" :
                    // v.getRealDealHander().getClass().getName();
                    UdasInfo udasInfo = new UdasInfo(key, v.getCacheEnum().name(), v.getNamespace(), v.getConfigKey(),
                                                     dealHandler, v.getSuccess().get(), v.getFailure().get());
                    AbstractKVHandler h[] = v.getHandlers();
                    if (h != null) {
                        List<KVHanderInfo> handList = new ArrayList<KVHanderInfo>();
                        for (AbstractKVHandler tmp : h) {
                            handList.add(new KVHanderInfo(tmp.getConfig(), tmp.isEnabled(), tmp.getExceptionCount()));
                        }
                        udasInfo.setHandlerList(handList);
                    }
                    udasList.add(udasInfo);
                }
            }
        }
        model.put("udasList", udasList);
        return toView("/comset/udas.htm");
    }

    @ControllerAction
    public WebResult scaleImageDynamic(Map<String, Object> model, String uri) throws Exception {
        long t1 = System.currentTimeMillis();
        int lastSlashIndex = getRightIndexOf(uri, '/');
        if (lastSlashIndex == -1) {
            return goto404();
        }
        String fileName = uri.substring(lastSlashIndex + 1);
        int lastDotIndex = getRightIndexOf(fileName, '.');
        if (lastDotIndex == -1) {
            return goto404();
        }
        String contentType = "image/jpeg";
        String suffix = fileName.substring(lastDotIndex + 1);
        if (!StringUtils.equals(suffix, "jpg")) {
            contentType = String.format("image/%s", suffix);
        }
        byte[] imageBytes = getImageBytes(uri, lastSlashIndex);
        if (imageBytes == null || imageBytes.length == 0) {
            return goto404();
        }
        MimeFileResultAdapter imageResultAdapter = getMimeFileResultAdapter(fileName, contentType, imageBytes);
        long t2 = System.currentTimeMillis();
        logger.error(String.format("scaleImageDynamic, uri: %s, comsume millis: %s", uri, (t2 - t1)));
        logger.error(request.getHeader("Referer"));
        return imageResultAdapter;
    }

    private WebResult goto404() {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }

    private byte[] getImageBytes(String uri, int lastSlashIndex) {
        int lastDotIndex = getRightIndexOf(uri, '.');
        int lastUnderlineIndex = getRightIndexOf(uri, '_');
        // (lastSlashIndex < lastUnderlineIndex < lastDotIndex)
        if (lastUnderlineIndex == -1 || lastUnderlineIndex > lastDotIndex || lastUnderlineIndex < lastSlashIndex) {
            return emptyBytes;
        }
        String mainImageFilePath = String.format("%s%s%s", staticDirPath, uri.substring(0, lastUnderlineIndex),
                                                 uri.substring(lastDotIndex));
        String generateImageFilePath = String.format("%s%s", staticDirPath, uri);
        int width = NumberUtils.toInt(uri.substring(lastUnderlineIndex + 1, lastDotIndex), -1);
        if (width == -1) {
            return emptyBytes;
        }
        File mainImageFile = new File(mainImageFilePath);
        if (mainImageFile.exists()) {
            fileService.reduceAllPicture(mainImageFilePath, null, new Integer[] { width },
                                         new String[] { generateImageFilePath });
        }
        File generateImageFile = new File(generateImageFilePath);
        if (!generateImageFile.exists()) {
            return emptyBytes;
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(generateImageFile);
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            logger.error("write file " + generateImageFile, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return emptyBytes;
    }

    private MimeFileResultAdapter getMimeFileResultAdapter(String fileName, String contentType, final byte[] imageBytes) {
        MimeFileResultAdapter mimeFileResultAdapter = new MimeFileResultAdapter(fileName) {

            @Override
            protected void onDownload(OutputStream outputStream) throws IOException {
                InputStream inputStream = new ByteArrayInputStream(imageBytes);
                IOUtils.copy(inputStream, outputStream);
            }
        };
        mimeFileResultAdapter.setContentType(contentType);
        return mimeFileResultAdapter;
    }

    private int getRightIndexOf(String uri, char findChar) {
        for (int i = uri.length() - 1; i >= 0; i--) {
            if (uri.charAt(i) == findChar) {
                return i;
            }
        }
        return -1;
    }

    public static class UdasInfo {

        private String             springId;
        private String             cacheEnum;
        private String             namespace;
        private String             configKey;
        private String             dealHandler;
        private long               success;
        private long               failure;
        private List<KVHanderInfo> handlerList;

        public UdasInfo(String springId, String cacheEnum, String namespace, String configKey, String dealHandler,
                        long success, long failure) {
            this.springId = springId;
            this.cacheEnum = cacheEnum;
            this.namespace = namespace;
            this.configKey = configKey;
            this.dealHandler = dealHandler;
            this.success = success;
            this.failure = failure;
        }

        /**
         * 返回表的RowSpan
         * 
         * @return
         */
        public int getRowSpan() {
            if (handlerList == null) {
                return 1;
            }
            return handlerList.size();
        }

        public float getRatio() {
            if (failure + success == 0) {
                return 0;
            }
            float tmp = success * 1f / (failure + success);
            tmp = ((int) (tmp * 1000 + 0.5f)) / 1000f;
            return tmp;
        }

        public String getSpringId() {
            return springId;
        }

        public List<KVHanderInfo> getHandlerList() {
            if (handlerList == null || handlerList.size() == 0) {
                handlerList = new ArrayList<KVHanderInfo>();
                handlerList.add(new KVHanderInfo(null, null, null));
            }
            return handlerList;
        }

        public void setHandlerList(List<KVHanderInfo> handlerList) {
            this.handlerList = handlerList;
        }

        public String getCacheEnum() {
            return cacheEnum;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getConfigKey() {
            return configKey;
        }

        public String getDealHandler() {
            return dealHandler;
        }

        public long getSuccess() {
            return success;
        }

        public long getFailure() {
            return failure;
        }
    }

    public static class KVHanderInfo {

        private String  config;
        private Boolean enabled;
        private Integer exceptionCount;

        public KVHanderInfo(String config, Boolean enabled, Integer exceptionCount) {
            this.config = config;
            this.enabled = enabled;
            this.exceptionCount = exceptionCount;
        }

        public String getConfig() {
            return config;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public Integer getExceptionCount() {
            return exceptionCount;
        }
    }
}
