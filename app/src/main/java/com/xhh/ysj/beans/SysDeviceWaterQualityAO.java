package com.xhh.ysj.beans;

import java.io.Serializable;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


/**
 * 水质监测对象
 *
 * @author 自动生成
 * @createTime 2018/5/22 星期二 下午 3:07:09
 */
@Table(name = "SysDeviceWaterQualityAO")
public class SysDeviceWaterQualityAO implements Serializable {

//    private static final long serialVersionUID = -3221953410749686858L;

    /**
     * 设备id
     */
    @Column(name = "deviceId", isId = true)
    private Integer deviceId;

    /**
     * 原水水质
     */
    @Column(name = "deviceRawWater")
    private int deviceRawWater;

    /**
     * 纯水水质
     */
    @Column(name = "devicePureWater")
    private int devicePureWater;

    /**
     * 检测时间
     */
//    @Temporal(TemporalType.DATE)
//    @JsonFormat(timezone = "GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "deviceWaterQualityTime")
    private String deviceWaterQualityTime;

    /**
     * 热水温度
     */
    @Column(name = "hotTemp")
    private Integer hotTemp;

    /**
     * 冷水温度
     */
    @Column(name = "coldTemp")
    private Integer coldTemp;

    /**
     * 是否正在制热：1-是，0-否
     */
    @Column(name = "heatingStatus")
    private Integer heatingStatus;

    /**
     * 是否正在制冷：1-是，0-否
     */
    @Column(name = "coolingStatus")
    private Integer coolingStatus;

    /**
     * 是否正在制水：1-是，0-否
     */
    @Column(name = "waterPurificationStatus")
    private Integer waterPurificationStatus;

    /**
     * 是否正在冲洗：1-是，0-否
     */
    @Column(name = "flushStatus")
    private Integer flushStatus;

    /**
     * 原水状态：1：正常（不缺水），0-异常（缺水）
     */
    @Column(name = "rawWaterStatus")
    private Integer rawWaterStatus;

    /**
     * 漏水状态：1：正常（未漏水）0-异常（漏水）
     */
    @Column(name = "waterLeakageStatus")
    private Integer waterLeakageStatus;

    /**
     * 开关机状态：1-开机，0-关机
     */
    @Column(name = "switchStatus")
    private Integer switchStatus;

    /**
     * 水杯状态：1-正常（被缺杯）；0-异常（缺杯）
     */
    @Column(name = "waterCupStatus")
    private Byte waterCupStatus;

    /**
     * 热水出水状态：1：出水中；0-停止
     */
    @Column(name = "hotWaterOutletStatus")
    private Integer hotWaterOutletStatus;

    /**
     * 冷水出水状态：1：出水中；0-停止
     */
    @Column(name = "coldWaterOutletStatus")
    private Integer coldWaterOutletStatus;

    /**
     * 温水出水状态：1：出水中；0-停止
     */
    @Column(name = "warmWaterOutletStatus")
    private Integer warmWaterOutletStatus;

    /**
     * 加热设备温度
     */
    @Column(name = "heatingTemp")
    private Integer heatingTemp;

    /**
     * 制冷设备温度
     */
    @Column(name = "coolingTemp")
    private Integer coolingTemp;

    /**
     * 冲洗间隔(小时)
     */
    @Column(name = "flushInterval")
    private Integer flushInterval;

    /**
     * 冲洗时长(秒)
     */
    @Column(name = "flushDuration")
    private Integer flushDuration;




//    public static long getSerialVersionUID() {
//        return serialVersionUID;
//    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceRawWater() {
        return deviceRawWater;
    }

    public void setDeviceRawWater(int deviceRawWater) {
        this.deviceRawWater = deviceRawWater;
    }

    public int getDevicePureWater() {
        return devicePureWater;
    }

    public void setDevicePureWater(int devicePureWater) {
        this.devicePureWater = devicePureWater;
    }

    public String getDeviceWaterQualityTime() {
        return deviceWaterQualityTime;
    }

    public void setDeviceWaterQualityTime(String deviceWaterQualityTime) {
        this.deviceWaterQualityTime = deviceWaterQualityTime;
    }

    public Integer getHotTemp() {
        return hotTemp;
    }

    public void setHotTemp(int hotTemp) {
        this.hotTemp = hotTemp;
    }

    public Integer getColdTemp() {
        return coldTemp;
    }

    public void setColdTemp(Integer coldTemp) {
        this.coldTemp = coldTemp;
    }

    public Integer getHeatingStatus() {
        return heatingStatus;
    }

    public void setHeatingStatus(Integer heatingStatus) {
        this.heatingStatus = heatingStatus;
    }

    public Integer getCoolingStatus() {
        return coolingStatus;
    }

    public void setCoolingStatus(Integer coolingStatus) {
        this.coolingStatus = coolingStatus;
    }

    public Integer getWaterPurificationStatus() {
        return waterPurificationStatus;
    }

    public void setWaterPurificationStatus(Integer waterPurificationStatus) {
        this.waterPurificationStatus = waterPurificationStatus;
    }

    public Integer getFlushStatus() {
        return flushStatus;
    }

    public void setFlushStatus(Integer flushStatus) {
        this.flushStatus = flushStatus;
    }

    public Integer getRawWaterStatus() {
        return rawWaterStatus;
    }

    public void setRawWaterStatus(Integer rawWaterStatus) {
        this.rawWaterStatus = rawWaterStatus;
    }

    public Integer getWaterLeakageStatus() {
        return waterLeakageStatus;
    }

    public void setWaterLeakageStatus(Integer waterLeakageStatus) {
        this.waterLeakageStatus = waterLeakageStatus;
    }

    public Integer getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(Integer switchStatus) {
        this.switchStatus = switchStatus;
    }

    public Byte getWaterCupStatus() {
        return waterCupStatus;
    }

    public void setWaterCupStatus(Byte waterCupStatus) {
        this.waterCupStatus = waterCupStatus;
    }

    public Integer getHotWaterOutletStatus() {
        return hotWaterOutletStatus;
    }

    public void setHotWaterOutletStatus(Integer hotWaterOutletStatus) {
        this.hotWaterOutletStatus = hotWaterOutletStatus;
    }

    public Integer getColdWaterOutletStatus() {
        return coldWaterOutletStatus;
    }

    public void setColdWaterOutletStatus(Integer coldWaterOutletStatus) {
        this.coldWaterOutletStatus = coldWaterOutletStatus;
    }

    public Integer getWarmWaterOutletStatus() {
        return warmWaterOutletStatus;
    }

    public void setWarmWaterOutletStatus(Integer warmWaterOutletStatus) {
        this.warmWaterOutletStatus = warmWaterOutletStatus;
    }

    public Integer getHeatingTemp() {
        return heatingTemp;
    }

    public void setHeatingTemp(Integer heatingTemp) {
        this.heatingTemp = heatingTemp;
    }

    public Integer getCoolingTemp() {
        return coolingTemp;
    }

    public void setCoolingTemp(Integer coolingTemp) {
        this.coolingTemp = coolingTemp;
    }

    public Integer getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(Integer flushInterval) {
        this.flushInterval = flushInterval;
    }

    public Integer getFlushDuration() {
        return flushDuration;
    }

    public void setFlushDuration(Integer flushDuration) {
        this.flushDuration = flushDuration;
    }

}
