package com.ms.commons.weixin.cons;

import org.apache.commons.lang.ArrayUtils;

public enum WeixinUploadType {

    /**
     * 
     */
    image(128 * 1024, new String[] { "jpg" }),
    /**
     * 
     */
    voice(256 * 1024, new String[] { "arm", "mp3" }),
    /**
     * 
     */
    video(1 * 1024 * 1024, new String[] { "mp4" }),
    /**
     * 
     */
    thumb(64 * 1024, new String[] { "jpg" });

    private long     maxSize;
    private String[] formates;

    private WeixinUploadType(long maxSize, String[] formates) {
        this.maxSize = maxSize;
        this.formates = formates;
    }

    public boolean isAllowed(String suffix, long size) {
        return isAllowedSize(size) && isAllowedFormat(suffix);
    }

    /**
     * @param suffix
     * @return
     */
    public boolean isAllowedFormat(String suffix) {
        if (formates == null) {
            return true;
        }
        if (suffix == null || suffix.length() == 0) {
            return false;
        }
        suffix = suffix.toLowerCase();
        return ArrayUtils.indexOf(formates, suffix) != -1;
    }

    /**
     * @param size
     * @return
     */
    public boolean isAllowedSize(long size) {
        return size <= maxSize;
    }

}
