package com.xhh.ysj.utils;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;

import com.xhh.ysj.beans.AdvsVideo;

/**
 * Created by Administrator on 2018/5/11 0011.
 */

public class VideoUtils {
    private static final String TAG = "VideoUtils";
    
    /**
     * Returns the file extension or an empty string iff there is no
     * extension. This method is a convenience method for obtaining the
     * extension of a url and has undefined results for other Strings.
     * @param url
     * @return The file extension of the given url.
     */
    public static String getFileExtensionFromUrl(String url) {
        String filename = url.trim();

        filename = filename.substring(filename.lastIndexOf("/") + 1);
        return filename;

    }
    
    /**
     * 获取adVideoMap中第一个尚未下载到本地的AdvsVideo的bean类
     * @param adVideoMap 从配置文件上获取的AdvsVideo的全集
     * @return
     */
    public static AdvsVideo getFirstRemoteVideo(Map<String, List<AdvsVideo>> adVideoMap) {
        for (List<AdvsVideo> adList: adVideoMap.values()) {
            if (null != adList && 0 != adList.size()) {
                for (AdvsVideo ad : adList) {
                    if (null != ad && !ad.isLocal()) {
                        return ad;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 获取视频在List中的下标
     * @param ad 视频
     * @param adVideoList 列表
     * @return 存在返回下标，不存在返回-1，数据错误返回-100
     */
    public static int getAdIndexFromList(AdvsVideo ad, List<AdvsVideo> adVideoList) {
        for (int i = 0; i < adVideoList.size(); i++) {
            AdvsVideo advsVideo = adVideoList.get(i);
            if (null == advsVideo || null == ad) {
                LogUtils.d(TAG, "getAdIndexFromList: 数据为空");
                return -100;
            }
            int advs_id = advsVideo.getAdvsId();
            if (0 != advs_id && advs_id == ad.getAdvsId()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取视频在List是否存在并且是本地的
     * @param ad 视频
     * @param adVideoList 列表
     * @return 存在并本地返回本地地址，非本地（正常没有这种情况）或不存在或数据错误返回null
     */
    public static String checkIfVideoIsLocal(AdvsVideo ad, List<AdvsVideo> adVideoList) {
        for (int i = 0; i < adVideoList.size(); i++) {
            AdvsVideo advsVideo = adVideoList.get(i);
            if (null == advsVideo || null == ad) {
                LogUtils.d(TAG, "checkIfVideoIsLocal: 数据为空");
                return null;
            }
            int advs_id = advsVideo.getAdvsId();
            if (0 != advs_id && advs_id == ad.getAdvsId()) {
                if (advsVideo.isLocal()) {
                    String localPath = advsVideo.getAdvsVideoLocaltionPath();
                    return TextUtils.isEmpty(localPath) ? null : localPath;
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
