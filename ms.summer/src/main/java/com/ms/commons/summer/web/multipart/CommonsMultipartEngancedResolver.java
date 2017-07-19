/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.multipart;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 4:10:16 PM
 */
public class CommonsMultipartEngancedResolver extends CommonsFileUploadSupport implements MultipartResolver, ServletContextAware {

    protected static ExpandLogger logger                     = LoggerFactoryWrapper.getLogger(CommonsMultipartEngancedResolver.class);

    private final boolean         commonsFileUpload12Present = ClassUtils.hasMethod(ServletFileUpload.class,
                                                                                    "isMultipartContent",
                                                                                    new Class[] { HttpServletRequest.class });

    private boolean               resolveLazily              = false;

    public CommonsMultipartEngancedResolver() {
        super();
    }

    public CommonsMultipartEngancedResolver(ServletContext servletContext) {
        this();
        setServletContext(servletContext);
    }

    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
        if (resolveLazily) {
            return new MultipartHttpServletEngancedRequest(request) {

                private MaxUploadSizeExceededException exceededException;

                protected void initializeMultipart() {
                    if (exceededException != null) {
                        throw exceededException;
                    }
                    try {
                        MultipartParsingResult parsingResult = parseRequest(request);
                        setMultipartFiles(parsingResult.getMultipartFiles());
                        setMultipartParameters(parsingResult.getMultipartParameters());
                    } catch (MaxUploadSizeExceededException e) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(" upload file size is exceeded.", e);
                        }
                        this.exceededException = e;
                        throw e;
                    }
                }

                @SuppressWarnings("all")
                protected Map getMultipartParameters() {
                    try {
                        return super.getMultipartParameters();
                    } catch (MaxUploadSizeExceededException e) {
                        Map multipartParameters = new HashMap(0);
                        setMultipartParameters(multipartParameters);
                        return multipartParameters;
                    }
                }
            };
        } else {
            MultipartParsingResult parsingResult = parseRequest(request);
            return new MultipartHttpServletEngancedRequest(request, parsingResult.getMultipartFiles(),
                                                           parsingResult.getMultipartParameters());
        }
    }

    @SuppressWarnings("all")
    protected MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
        String encoding = determineEncoding(request);
        FileUpload fileUpload = prepareFileUpload(encoding);
        try {
            List fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
            return parseFileItems(fileItems, encoding);
        } catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
        } catch (FileUploadException ex) {
            throw new MultipartException("Could not parse multipart servlet request", ex);
        }
    }

    protected String determineEncoding(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = getDefaultEncoding();
        }
        return encoding;
    }

    @SuppressWarnings("all")
    protected MultipartParsingResult parseFileItems(List fileItems, String encoding) {
        Map<String, Object> multipartFiles = new HashMap<String, Object>();
        Map<String, Object> multipartParameters = new HashMap<String, Object>();

        for (Iterator<FileItem> it = fileItems.iterator(); it.hasNext();) {
            FileItem fileItem = it.next();
            if (fileItem.isFormField()) {
                String value = null;
                if (encoding != null) {
                    try {
                        value = fileItem.getString(encoding);
                    } catch (UnsupportedEncodingException ex) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Could not decode multipart item '" + fileItem.getFieldName()
                                        + "' with encoding '" + encoding + "': using platform default");
                        }
                        value = fileItem.getString();
                    }
                } else {
                    value = fileItem.getString();
                }
                String[] curParam = (String[]) multipartParameters.get(fileItem.getFieldName());
                if (curParam == null) {
                    // simple form field
                    multipartParameters.put(fileItem.getFieldName(), new String[] { value });
                } else {
                    // array of simple form fields
                    String[] newParam = StringUtils.addStringToArray(curParam, value);
                    multipartParameters.put(fileItem.getFieldName(), newParam);
                }
            } else {
                // multipart file field
                CommonsMultipartFile file = new CommonsMultipartFile(fileItem);
                if (multipartFiles.containsKey(file.getName())) {
                    Object value = multipartFiles.get(file.getName());
                    if (value instanceof CommonsMultipartFile) {
                        List<CommonsMultipartFile> fileList = new ArrayList<CommonsMultipartFile>();
                        fileList.add((CommonsMultipartFile) value);
                        fileList.add(file);
                        multipartFiles.put(file.getName(), fileList);
                    } else {
                        ((List<CommonsMultipartFile>) value).add(file); //
                        // multipartFiles.put(file.getName(), value);
                    }
                } else {
                    multipartFiles.put(file.getName(), file);
                }

            }
        }
        for (Map.Entry<String, Object> entry : multipartFiles.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                List<CommonsMultipartFile> fileList = (List<CommonsMultipartFile>) value;
                multipartFiles.put(entry.getKey(), fileList.toArray(new CommonsMultipartFile[] {}));
            }
        }
        return new MultipartParsingResult(multipartFiles, multipartParameters);
    }

    /*
     * (non-Javadoc)
     * @seeorg.springframework.web.multipart.commons.CommonsFileUploadSupport# cleanupFileItems(java.util.Collection)
     */
    @SuppressWarnings("rawtypes")
    protected void cleanupFileItems(Collection multipartFiles) {
        for (Iterator it = multipartFiles.iterator(); it.hasNext();) {
            Object value = it.next();
            if (value instanceof CommonsMultipartFile) {
                cleanSingleFileItem((CommonsMultipartFile) value);
            } else {
                if (value instanceof CommonsMultipartFile[]) {
                    for (CommonsMultipartFile file : (CommonsMultipartFile[]) value) {
                        cleanSingleFileItem(file);
                    }
                }
            }
        }
    }

    /**
     * @param value
     */
    private void cleanSingleFileItem(CommonsMultipartFile file) {

        if (logger.isDebugEnabled()) {
            logger.debug("Cleaning up multipart file [" + file.getName() + "] with original filename ["
                         + file.getOriginalFilename() + "], stored " + file.getStorageDescription());
        }
        file.getFileItem().delete();
    }

    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        return new ServletFileUpload(fileItemFactory);
    }

    public void setServletContext(ServletContext servletContext) {
        if (!isUploadTempDirSpecified()) {
            getFileItemFactory().setRepository(WebUtils.getTempDir(servletContext));
        }
    }

    public boolean isMultipart(HttpServletRequest request) {
        if (request == null) {
            return false;
        } else if (commonsFileUpload12Present) {
            return ServletFileUpload.isMultipartContent(request);
        } else {
            return ServletFileUpload.isMultipartContent(new ServletRequestContext(request));
        }
    }

    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (request != null) {
            try {
                cleanupFileItems(request.getFileMap().values());
            } catch (Throwable ex) {
                logger.warn("Failed to perform multipart cleanup for servlet request", ex);
            }
        }
    }

    static class MultipartHttpServletEngancedRequest extends DefaultMultipartHttpServletRequest {

        public MultipartHttpServletEngancedRequest(HttpServletRequest request) {
            super(request);

        }

        @SuppressWarnings("rawtypes")
        public MultipartHttpServletEngancedRequest(HttpServletRequest request, Map multipartFiles,
                                                   Map multipartParameters) {
            super(request);
            setMultipartFiles(multipartFiles);
            setMultipartParameters(multipartParameters);
        }

        public MultipartFile getFile(String name) {
            Object value = getMultipartFiles().get(name);
            if (value != null) {
                if (value instanceof MultipartFile) {
                    return (MultipartFile) value;
                } else if (value instanceof MultipartFile[]) {
                    return ((MultipartFile[]) value)[0];
                }
            }
            throw new IllegalStateException("the multipartFile:" + value + " is not excepted type.");
        }
    }
}
