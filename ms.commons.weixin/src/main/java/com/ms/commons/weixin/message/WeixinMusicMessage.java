package com.ms.commons.weixin.message;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * {
 *     "touser":"OPENID",
 *     "msgtype":"music",
 *     "music":
 *     {
 *       "title":"MUSIC_TITLE",
 *       "description":"MUSIC_DESCRIPTION",
 *       "musicurl":"MUSIC_URL",
 *       "hqmusicurl":"HQ_MUSIC_URL",
 *       "thumb_media_id":"THUMB_MEDIA_ID" 
 *     }
 * }
 * title     否   音乐标题
 * description  否   音乐描述
 * musicurl     是   音乐链接
 * hqmusicurl   是   高品质音乐链接，wifi环境优先使用该链接播放音乐
 * thumb_media_id   是   缩略图的媒体ID
 * </pre>
 */
public class WeixinMusicMessage extends WeixinMessage {

    private Map<String, String> music;

    public WeixinMusicMessage(String touser) {
        this(touser, null, null, null, null, null);
    }

    public WeixinMusicMessage(String touser, String musicurl, String hqmusicurl, String thumbMediaId) {
        this(touser, musicurl, hqmusicurl, thumbMediaId, null, null);
    }

    public WeixinMusicMessage(String touser, String musicurl, String hqmusicurl, String thumbMediaId, String title,
                              String description) {
        super(touser, "music");
        music = new HashMap<String, String>();
        setMusicurl(musicurl);
        setHqmusicurl(hqmusicurl);
        setThumbMediaId(thumbMediaId);
        setTitle(title);
        setDescription(description);
    }

    public void setMusicurl(String musicurl) {
        music.put("musicurl", musicurl);
    }

    public void setHqmusicurl(String hqmusicurl) {
        music.put("hqmusicurl", hqmusicurl);
    }

    public void setThumbMediaId(String thumbMediaId) {
        music.put("thumb_media_id", thumbMediaId);
    }

    public void setTitle(String title) {
        music.put("title", title);
    }

    public void setDescription(String description) {
        music.put("description", description);
    }

}
