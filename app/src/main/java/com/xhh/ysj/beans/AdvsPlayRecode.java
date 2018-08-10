package com.xhh.ysj.beans;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2018/5/25 0025.
 */
@Table(name = "Advs_Play_Recode")
public class AdvsPlayRecode {
    /*记录ID*/
    @Column(name = "record_id", isId = true, autoGen = true)
    private int recordId;
    /*广告ID*/
    @Column(name = "advs_id")
    private int advsId;
    /*设备ID*/
    @Column(name = "device_id")
    private int deviceId;
    /*广告播放时间（datetime）*/
    @Column(name = "advs_play_time")
    private String advsPlayTime;
    /*广告时长*/
    @Column(name = "advs_play_length_of_time")
    private int advsPlayLengthOfTime;
    /*广告收费模式*/
    @Column(name = "advs_play_device_charg_mode")
    private int advsPlayDeviceChargMode;
    /*广告播放行业*/
    @Column(name = "advs_play_device_industry")
    private int advsPlayDeviceIndustry;
    /*广告播放场景*/
    @Column(name = "advs_play_scene")
    private int advsPlayScene;

    public AdvsPlayRecode() {
        super();
    }

    public AdvsPlayRecode(int advsId, int deviceId, String advsPlayTime, int advsPlayLengthOfTime, int advsPlayDeviceChargMode, int advsPlayDeviceIndustry, int advsPlayScene) {
        this.advsId = advsId;
        this.deviceId = deviceId;
        this.advsPlayTime = advsPlayTime;
        this.advsPlayLengthOfTime = advsPlayLengthOfTime;
        this.advsPlayDeviceChargMode = advsPlayDeviceChargMode;
        this.advsPlayDeviceIndustry = advsPlayDeviceIndustry;
        this.advsPlayScene = advsPlayScene;
    }

    public AdvsPlayRecode(int advsId, int deviceId, String advsPlayTime) {
        this.advsId = advsId;
        this.deviceId = deviceId;
        this.advsPlayTime = advsPlayTime;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getAdvsId() {
        return advsId;
    }

    public void setAdvsId(int advsId) {
        this.advsId = advsId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getAdvsPlayTime() {
        return advsPlayTime;
    }

    public void setAdvsPlayTime(String advsPlayTime) {
        this.advsPlayTime = advsPlayTime;
    }

    public int getAdvsPlayLengthOfTime() {
        return advsPlayLengthOfTime;
    }

    public void setAdvsPlayLengthOfTime(int advsPlayLengthOfTime) {
        this.advsPlayLengthOfTime = advsPlayLengthOfTime;
    }

    public int getAdvsPlayDeviceChargMode() {
        return advsPlayDeviceChargMode;
    }

    public void setAdvsPlayDeviceChargMode(int advsPlayDeviceChargMode) {
        this.advsPlayDeviceChargMode = advsPlayDeviceChargMode;
    }

    public int getAdvsPlayDeviceIndustry() {
        return advsPlayDeviceIndustry;
    }

    public void setAdvsPlayDeviceIndustry(int advsPlayDeviceIndustry) {
        this.advsPlayDeviceIndustry = advsPlayDeviceIndustry;
    }

    public int getAdvsPlayScene() {
        return advsPlayScene;
    }

    public void setAdvsPlayScene(int advsPlayScene) {
        this.advsPlayScene = advsPlayScene;
    }

    @Override
    public String toString() {
        return "AdvsPlayRecode{" +
                "recordId=" + recordId +
                ", advsId=" + advsId +
                ", deviceId=" + deviceId +
                ", advsPlayTime=" + advsPlayTime +
//                ", advsPlayLengthOfTime=" + advsPlayLengthOfTime +
//                ", advsPlayDeviceChargMode=" + advsPlayDeviceChargMode +
//                ", advsPlayDeviceIndustry=" + advsPlayDeviceIndustry +
//                ", advsPlayScene=" + advsPlayScene +
                '}';
    }
}
