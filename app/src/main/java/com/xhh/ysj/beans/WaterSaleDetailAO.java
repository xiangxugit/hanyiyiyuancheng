package com.xhh.ysj.beans;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/2 0002.
 */

public class WaterSaleDetailAO {
    /**
     * 终端用户ID
     */
    private Integer userId ;//


    /**
     * 设备ID
     */
    private Integer deviceId ;//


    /**
     * 设备收费模式(1:支付售水；2：免费饮水3：租赁用户取水；4：购买用户取水)
     */
    private Integer productChargMode ;//


    /**
     * 热水流量(单位ml)
     */
    private Integer waterHotFlow ;//


    /**
     * 温水流量(单位ml)
     */
    private Integer waterWarmFlow ;//


    /**
     * 冷水流量(单位ml)
     */
    private Integer waterColdFlow ;//


    /**
     * 热水价格
     */
    private java.math.BigDecimal waterHotMoney ;//--


    /**
     * 温水价格
     */
    private java.math.BigDecimal waterWarmMoney ;//--


    /**
     * 冷水价格
     */
    private java.math.BigDecimal waterColdMoney ;//--


    /**
     * 纸杯数量(单位个)
     */
    private Integer waterCupNum ;//


    /**
     * 喝水总流量
     */
    private Integer waterFlow ;//--


    /**
     * 售水总价
     */
    private java.math.BigDecimal waterMoney ;//--


    /**
     * 售水时间
     */
    private java.util.Date waterTime ;//--

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getProductChargMode() {
        return productChargMode;
    }

    public void setProductChargMode(Integer productChargMode) {
        this.productChargMode = productChargMode;
    }

    public Integer getWaterHotFlow() {
        return waterHotFlow;
    }

    public void setWaterHotFlow(Integer waterHotFlow) {
        this.waterHotFlow = waterHotFlow;
    }

    public Integer getWaterWarmFlow() {
        return waterWarmFlow;
    }

    public void setWaterWarmFlow(Integer waterWarmFlow) {
        this.waterWarmFlow = waterWarmFlow;
    }

    public Integer getWaterColdFlow() {
        return waterColdFlow;
    }

    public void setWaterColdFlow(Integer waterColdFlow) {
        this.waterColdFlow = waterColdFlow;
    }

    public BigDecimal getWaterHotMoney() {
        return waterHotMoney;
    }

    public void setWaterHotMoney(BigDecimal waterHotMoney) {
        this.waterHotMoney = waterHotMoney;
    }

    public BigDecimal getWaterWarmMoney() {
        return waterWarmMoney;
    }

    public void setWaterWarmMoney(BigDecimal waterWarmMoney) {
        this.waterWarmMoney = waterWarmMoney;
    }

    public BigDecimal getWaterColdMoney() {
        return waterColdMoney;
    }

    public void setWaterColdMoney(BigDecimal waterColdMoney) {
        this.waterColdMoney = waterColdMoney;
    }

    public Integer getWaterCupNum() {
        return waterCupNum;
    }

    public void setWaterCupNum(Integer waterCupNum) {
        this.waterCupNum = waterCupNum;
    }

    public Integer getWaterFlow() {
        return waterFlow;
    }

    public void setWaterFlow(Integer waterFlow) {
        this.waterFlow = waterFlow;
    }

    public BigDecimal getWaterMoney() {
        return waterMoney;
    }

    public void setWaterMoney(BigDecimal waterMoney) {
        this.waterMoney = waterMoney;
    }

    public Date getWaterTime() {
        return waterTime;
    }

    public void setWaterTime(Date waterTime) {
        this.waterTime = waterTime;
    }

    @Override
    public String toString() {
        return "WaterSaleDetailAO{" +
                "userId=" + userId +
                ", deviceId=" + deviceId +
                ", productChargMode=" + productChargMode +
                ", waterHotFlow=" + waterHotFlow +
                ", waterWarmFlow=" + waterWarmFlow +
                ", waterColdFlow=" + waterColdFlow +
                ", waterHotMoney=" + waterHotMoney +
                ", waterWarmMoney=" + waterWarmMoney +
                ", waterColdMoney=" + waterColdMoney +
                ", waterCupNum=" + waterCupNum +
                ", waterFlow=" + waterFlow +
                ", waterMoney=" + waterMoney +
                ", waterTime=" + waterTime +
                '}';
    }
}
