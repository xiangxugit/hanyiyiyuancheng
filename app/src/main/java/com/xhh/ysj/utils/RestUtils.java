package com.xhh.ysj.utils;

import com.xhh.ysj.constants.UriConstant;

/**
 * Created by Administrator on 2018/5/14 0014.
 */

public class RestUtils {

    public static String getPath() {
        // return "http //" + IP + ":" + PORT + UriConstant.FILE_SEPARATE;
        return "http://" + UriConstant.IP + ":" + UriConstant.PORT + UriConstant.FILE_SEPARATE;
    }

    public static String getUrl(String url) {
        return getPath() + url;
    }

    public static String getAppDir() {
        return UriConstant.APP_ROOT_PATH;
    }

    public static String getVideoDir(String period) {
        return UriConstant.APP_ROOT_PATH + UriConstant.VIDEO_DIR + period + UriConstant.FILE_SEPARATE;
    }

    public static String getDbDir() {
        return UriConstant.APP_ROOT_PATH + UriConstant.DB_DIR;
    }

    /**
     * 读取baseurl
     *
     * @param url
     * @return
     */
    public static String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");
        if (index != -1) {
            head = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index + 1);
        }
        return head + url;
    }


}
