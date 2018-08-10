package com.xhh.ysj.beans;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2018/5/21 0021.
 */
@Table(name = "Sys_Device_Monitor_Config")
public class SysDeviceMonitorConfig implements Serializable {

    @Column(name = "mot_cfg_id", isId = true, autoGen = true)
    private Integer motCfgId;


    /**
     * 设备ID
     */
    @Column(name = "deviceId")
    private Integer deviceId;


    /**
     * 巡检时间(单位分钟)
     */
    @Column(name = "mot_cfg_network_time")
    private Integer motCfgNetworkTime;


    /**
     * 巡检次数(单位次)
     */
    @Column(name = "mot_cfg_network_times")
    private int motCfgNetworkTimes;


    /**
     * PP棉使用时间(单位天)
     */
    @Column(name = "mot_cfg_pp_time")
    private Integer motCfgPpTime;


    /**
     * PP棉制水总流量(单位L)
     */
    @Column(name = "mot_cfg_pp_flow")
    private Integer motCfgPpFlow;


    /**
     * PP棉制更换时间
     */
    @Column(name = "mot_cfg_pp_change_time")
    private Date motCfgPpChangeTime;


    /**
     * 颗粒活性炭使用时间(单位天) 注：颗粒活性炭使用时长 100天
     */
    @Column(name = "mot_cfg_grain_carbon_time")
    private Integer motCfgGrainCarbonTime;


    /**
     * 颗粒活性炭使用时间(单位L) 注：颗粒活性炭制水总流量11355L
     */
    @Column(name = "mot_cfg_grain_carbon_flow")
    private Integer motCfgGrainCarbonFlow;


    /**
     * 颗粒活性炭更换时间
     */
    @Column(name = "mot_cfg_grain_carbon_change_time")
    private Date motCfgGrainCarbonChangeTime;


    /**
     * 压缩活性炭使用时间(单位天)
     * 注：压缩活性炭使用时长 100天
     */
    @Column(name = "mot_cfg_press_carbon_time")
    private Integer motCfgPressCarbonTime;


    /**
     * 压缩活性炭使用时间(单位L)
     * 注：压缩活性炭制水总流量11355L
     */
    @Column(name = "mot_cfg_press_carbon_flow")
    private Integer motCfgPressCarbonFlow;


    /**
     * 压缩活性炭更换时间
     */
    @Column(name = "mot_cfg_press_carbon_change_time")
    private Date motCfgPressCarbonChangeTime;


    /**
     * 后置活性炭使用时间(单位天)
     * 注：后置活性炭使用时长 100天
     */
    @Column(name = "mot_cfg_pose_carbon_time")
    private Integer motCfgPoseCarbonTime;


    /**
     * 后置活性炭使用时间(单位L)
     * 注：后置活性炭制水总流量11355L
     */
    @Column(name = "mot_cfg_pose_carbon_flow")
    private Integer motCfgPoseCarbonFlow;


    /**
     * 后置活性炭更换时间
     */
    @Column(name = "mot_cfg_pose_carbon_change_time")
    private Date motCfgPoseCarbonChangeTime;


    /**
     * RO反渗透膜使用时间(单位天)
     * 注：RO反渗透膜使用时长 540天
     */
    @Column(name = "mot_cfg_ro_time")
    private Integer motCfgRoTime;


    /**
     * RO反渗透膜使用时间(单位L)
     * 注：RO反渗透膜制水总流量11355L
     */
    @Column(name = "mot_cfg_ro_flow")
    private Integer motCfgRoFlow;


    /**
     * RO反渗透膜更换时间
     */
    @Column(name = "mot_cfg_ro_change_time")
    private Date motCfgRoChangeTime;

    /**
     * 监控数据上报时间间隔（单位分钟）(激活时间开始)
     */
    @Column(name = "mot_cfg_up_time")
    private Integer motCfgUpTime;


    /**
     * 设备单次最大出水量（单位ml）
     */
    @Column(name = "mot_cfg_max_flow")
    private Integer motCfgMaxFlow;


    /**
     * 音量控制
     */
    @Column(name = "mot_cfg_volume")
    private Integer motCfgVolume;


    /**
     * 冲洗时间间隔(/分/次)
     */
    @Column(name = "mot_cfg_flush_interval")
    private Integer motCfgFlushInterval;


    /**
     * 冲洗持续时长(/次/秒)
     */
    @Column(name = "mot_cfg_flush_duration")
    private Integer motCfgFlushDuration;


    /**
     * 加热临界温度
     */
    @Column(name = "mot_cfg_heating_temp")
    private Integer motCfgHeatingTemp;


    /**
     * 制冷临界温度
     */
    @Column(name = "mot_cfg_cooling_temp")
    private Integer motCfgCoolingTemp;


    /**
     * 是否全天加热(0-否 1-是)
     */
    @Column(name = "mot_cfg_heating_allday")
    private Integer motCfgHeatingAllday;


    /**
     * 按时间段加热
     */
    @Column(name = "mot_cfg_heating_interval")
    private String motCfgHeatingInterval;


    /**
     * 是否全天制冷(0-否 1-是)
     */
    @Column(name = "mot_cfg_cooling_allday")
    private Integer motCfgCoolingAllday;


    /**
     * 按时间段制冷
     */
    @Column(name = "mot_cfg_cooling_interval")
    private String motCfgCoolingInterval;


    /**
     * 订单子ID（激活时选择）
     */
    @Column(name = "order_dtl_id")
    private Integer orderDtlId;


    /**
     * 维护人员
     */
    @Column(name = "maintenance_admin_user_id")
    private Integer maintenanceAdminUserId;


    /**
     * 维护人员
     */
    @Column(name = "maintenance_admin_user_name")
    private String maintenanceAdminUserName;


    /**
     * 收费模式(1:售水；2：租赁；3：销售)
     */
    @Column(name = "product_charg_mode")
    private Integer productChargMode;

    /**
     * 手机号
     */
    @Column(name = "admin_user_telephone")
    private String adminUserTelephone;

    /**
     * 设备租赁到期时间
     */
    @Column(name = "product_rent_time")
    private Date productRentTime;

    /**
     * 设备编号
     */
    @Column(name = "device_number")
    private String deviceNumber;

    /**
     * 单次取水最大流量ml
     */
    @Column(name = "max_get_water_capacity")
    private Integer maxGetwaterCapacity ;

    /**
     * 单次消费最大流量ml
     */
    @Column(name = "max_consume_capacity")
    private Integer maxConsumeCapacity;

    /**
     * 饮水机广告倒计时（秒）
     */
    @Column(name = "advs_count_down")
    private Integer advsCountDown ;
    /**
     * 饮水机操作倒计时（秒）
     */
    @Column(name = "operation_countdown")
    private Integer operationCountDown ;

    public Integer getMotCfgId() {
        return motCfgId;
    }

    public void setMotCfgId(Integer motCfgId) {
        this.motCfgId = motCfgId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getMotCfgNetworkTime() {
        return motCfgNetworkTime;
    }

    public void setMotCfgNetworkTime(Integer motCfgNetworkTime) {
        this.motCfgNetworkTime = motCfgNetworkTime;
    }

    public int getMotCfgNetworkTimes() {
        return motCfgNetworkTimes;
    }

    public void setMotCfgNetworkTimes(int motCfgNetworkTimes) {
        this.motCfgNetworkTimes = motCfgNetworkTimes;
    }

    public Integer getMotCfgPpTime() {
        return motCfgPpTime;
    }

    public void setMotCfgPpTime(Integer motCfgPpTime) {
        this.motCfgPpTime = motCfgPpTime;
    }

    public Integer getMotCfgPpFlow() {
        return motCfgPpFlow;
    }

    public void setMotCfgPpFlow(Integer motCfgPpFlow) {
        this.motCfgPpFlow = motCfgPpFlow;
    }

    public Date getMotCfgPpChangeTime() {
        return motCfgPpChangeTime;
    }

    public void setMotCfgPpChangeTime(Date motCfgPpChangeTime) {
        this.motCfgPpChangeTime = motCfgPpChangeTime;
    }

    public Integer getMotCfgGrainCarbonTime() {
        return motCfgGrainCarbonTime;
    }

    public void setMotCfgGrainCarbonTime(Integer motCfgGrainCarbonTime) {
        this.motCfgGrainCarbonTime = motCfgGrainCarbonTime;
    }

    public Integer getMotCfgGrainCarbonFlow() {
        return motCfgGrainCarbonFlow;
    }

    public void setMotCfgGrainCarbonFlow(Integer motCfgGrainCarbonFlow) {
        this.motCfgGrainCarbonFlow = motCfgGrainCarbonFlow;
    }

    public Date getMotCfgGrainCarbonChangeTime() {
        return motCfgGrainCarbonChangeTime;
    }

    public void setMotCfgGrainCarbonChangeTime(Date motCfgGrainCarbonChangeTime) {
        this.motCfgGrainCarbonChangeTime = motCfgGrainCarbonChangeTime;
    }

    public Integer getMotCfgPressCarbonTime() {
        return motCfgPressCarbonTime;
    }

    public void setMotCfgPressCarbonTime(Integer motCfgPressCarbonTime) {
        this.motCfgPressCarbonTime = motCfgPressCarbonTime;
    }

    public Integer getMotCfgPressCarbonFlow() {
        return motCfgPressCarbonFlow;
    }

    public void setMotCfgPressCarbonFlow(Integer motCfgPressCarbonFlow) {
        this.motCfgPressCarbonFlow = motCfgPressCarbonFlow;
    }

    public Date getMotCfgPressCarbonChangeTime() {
        return motCfgPressCarbonChangeTime;
    }

    public void setMotCfgPressCarbonChangeTime(Date motCfgPressCarbonChangeTime) {
        this.motCfgPressCarbonChangeTime = motCfgPressCarbonChangeTime;
    }

    public Integer getMotCfgPoseCarbonTime() {
        return motCfgPoseCarbonTime;
    }

    public void setMotCfgPoseCarbonTime(Integer motCfgPoseCarbonTime) {
        this.motCfgPoseCarbonTime = motCfgPoseCarbonTime;
    }

    public Integer getMotCfgPoseCarbonFlow() {
        return motCfgPoseCarbonFlow;
    }

    public void setMotCfgPoseCarbonFlow(Integer motCfgPoseCarbonFlow) {
        this.motCfgPoseCarbonFlow = motCfgPoseCarbonFlow;
    }

    public Date getMotCfgPoseCarbonChangeTime() {
        return motCfgPoseCarbonChangeTime;
    }

    public void setMotCfgPoseCarbonChangeTime(Date motCfgPoseCarbonChangeTime) {
        this.motCfgPoseCarbonChangeTime = motCfgPoseCarbonChangeTime;
    }

    public Integer getMotCfgRoTime() {
        return motCfgRoTime;
    }

    public void setMotCfgRoTime(Integer motCfgRoTime) {
        this.motCfgRoTime = motCfgRoTime;
    }

    public Integer getMotCfgRoFlow() {
        return motCfgRoFlow;
    }

    public void setMotCfgRoFlow(Integer motCfgRoFlow) {
        this.motCfgRoFlow = motCfgRoFlow;
    }

    public Date getMotCfgRoChangeTime() {
        return motCfgRoChangeTime;
    }

    public void setMotCfgRoChangeTime(Date motCfgRoChangeTime) {
        this.motCfgRoChangeTime = motCfgRoChangeTime;
    }

    public Integer getMotCfgUpTime() {
        return motCfgUpTime;
    }

    public void setMotCfgUpTime(Integer motCfgUpTime) {
        this.motCfgUpTime = motCfgUpTime;
    }

    public Integer getMotCfgMaxFlow() {
        return motCfgMaxFlow;
    }

    public void setMotCfgMaxFlow(Integer motCfgMaxFlow) {
        this.motCfgMaxFlow = motCfgMaxFlow;
    }

    public Integer getMotCfgVolume() {
        return motCfgVolume;
    }

    public void setMotCfgVolume(Integer motCfgVolume) {
        this.motCfgVolume = motCfgVolume;
    }

    public Integer getMotCfgFlushInterval() {
        return motCfgFlushInterval;
    }

    public void setMotCfgFlushInterval(Integer motCfgFlushInterval) {
        this.motCfgFlushInterval = motCfgFlushInterval;
    }

    public Integer getMotCfgFlushDuration() {
        return motCfgFlushDuration;
    }

    public void setMotCfgFlushDuration(Integer motCfgFlushDuration) {
        this.motCfgFlushDuration = motCfgFlushDuration;
    }

    public Integer getMotCfgHeatingTemp() {
        return motCfgHeatingTemp;
    }

    public void setMotCfgHeatingTemp(Integer motCfgHeatingTemp) {
        this.motCfgHeatingTemp = motCfgHeatingTemp;
    }

    public Integer getMotCfgCoolingTemp() {
        return motCfgCoolingTemp;
    }

    public void setMotCfgCoolingTemp(Integer motCfgCoolingTemp) {
        this.motCfgCoolingTemp = motCfgCoolingTemp;
    }

    public Integer getMotCfgHeatingAllday() {
        return motCfgHeatingAllday;
    }

    public void setMotCfgHeatingAllday(Integer motCfgHeatingAllday) {
        this.motCfgHeatingAllday = motCfgHeatingAllday;
    }

    public String getMotCfgHeatingInterval() {
        return motCfgHeatingInterval;
    }

    public void setMotCfgHeatingInterval(String motCfgHeatingInterval) {
        this.motCfgHeatingInterval = motCfgHeatingInterval;
    }

    public Integer getMotCfgCoolingAllday() {
        return motCfgCoolingAllday;
    }

    public void setMotCfgCoolingAllday(Integer motCfgCoolingAllday) {
        this.motCfgCoolingAllday = motCfgCoolingAllday;
    }

    public String getMotCfgCoolingInterval() {
        return motCfgCoolingInterval;
    }

    public void setMotCfgCoolingInterval(String motCfgCoolingInterval) {
        this.motCfgCoolingInterval = motCfgCoolingInterval;
    }

    public Integer getOrderDtlId() {
        return orderDtlId;
    }

    public void setOrderDtlId(Integer orderDtlId) {
        this.orderDtlId = orderDtlId;
    }

    public Integer getMaintenanceAdminUserId() {
        return maintenanceAdminUserId;
    }

    public void setMaintenanceAdminUserId(Integer maintenanceAdminUserId) {
        this.maintenanceAdminUserId = maintenanceAdminUserId;
    }

    public String getMaintenanceAdminUserName() {
        return maintenanceAdminUserName;
    }

    public void setMaintenanceAdminUserName(String maintenanceAdminUserName) {
        this.maintenanceAdminUserName = maintenanceAdminUserName;
    }

    public Integer getProductChargMode() {
        return productChargMode;
    }

    public void setProductChargMode(Integer productChargMode) {
        this.productChargMode = productChargMode;
    }

    public String getAdminUserTelephone() {
        return adminUserTelephone;
    }

    public void setAdminUserTelephone(String adminUserTelephone) {
        this.adminUserTelephone = adminUserTelephone;
    }

    public Date getProductRentTime() {
        return productRentTime;
    }

    public void setProductRentTime(Date productRentTime) {
        this.productRentTime = productRentTime;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Integer getMaxGetwaterCapacity() {
        return maxGetwaterCapacity;
    }

    public void setMaxGetwaterCapacity(Integer maxGetwaterCapacity) {
        this.maxGetwaterCapacity = maxGetwaterCapacity;
    }

    public Integer getMaxConsumeCapacity() {
        return maxConsumeCapacity;
    }

    public void setMaxConsumeCapacity(Integer maxConsumeCapacity) {
        this.maxConsumeCapacity = maxConsumeCapacity;
    }

    public Integer getAdvsCountDown() {
        return advsCountDown;
    }

    public void setAdvsCountDown(Integer advsCountDown) {
        this.advsCountDown = advsCountDown;
    }

    public Integer getOperationCountDown() {
        return operationCountDown;
    }

    public void setOperationCountDown(Integer operationCountDown) {
        this.operationCountDown = operationCountDown;
    }

    @Override
    public String toString() {
        return "SysDeviceMonitorConfig{" +
                "motCfgId=" + motCfgId +
                ", deviceId=" + deviceId +
                ", motCfgNetworkTime=" + motCfgNetworkTime +
                ", motCfgNetworkTimes=" + motCfgNetworkTimes +
                ", motCfgPpTime=" + motCfgPpTime +
                ", motCfgPpFlow=" + motCfgPpFlow +
                ", motCfgPpChangeTime=" + motCfgPpChangeTime +
                ", motCfgGrainCarbonTime=" + motCfgGrainCarbonTime +
                ", motCfgGrainCarbonFlow=" + motCfgGrainCarbonFlow +
                ", motCfgGrainCarbonChangeTime=" + motCfgGrainCarbonChangeTime +
                ", motCfgPressCarbonTime=" + motCfgPressCarbonTime +
                ", motCfgPressCarbonFlow=" + motCfgPressCarbonFlow +
                ", motCfgPressCarbonChangeTime=" + motCfgPressCarbonChangeTime +
                ", motCfgPoseCarbonTime=" + motCfgPoseCarbonTime +
                ", motCfgPoseCarbonFlow=" + motCfgPoseCarbonFlow +
                ", motCfgPoseCarbonChangeTime=" + motCfgPoseCarbonChangeTime +
                ", motCfgRoTime=" + motCfgRoTime +
                ", motCfgRoFlow=" + motCfgRoFlow +
                ", motCfgRoChangeTime=" + motCfgRoChangeTime +
                ", motCfgUpTime=" + motCfgUpTime +
                ", motCfgMaxFlow=" + motCfgMaxFlow +
                ", motCfgVolume=" + motCfgVolume +
                ", motCfgFlushInterval=" + motCfgFlushInterval +
                ", motCfgFlushDuration=" + motCfgFlushDuration +
                ", motCfgHeatingTemp=" + motCfgHeatingTemp +
                ", motCfgCoolingTemp=" + motCfgCoolingTemp +
                ", motCfgHeatingAllday=" + motCfgHeatingAllday +
                ", motCfgHeatingInterval='" + motCfgHeatingInterval + '\'' +
                ", motCfgCoolingAllday=" + motCfgCoolingAllday +
                ", motCfgCoolingInterval='" + motCfgCoolingInterval + '\'' +
                ", orderDtlId=" + orderDtlId +
                ", maintenanceAdminUserId=" + maintenanceAdminUserId +
                ", maintenanceAdminUserName='" + maintenanceAdminUserName + '\'' +
                ", productChargMode=" + productChargMode +
                ", adminUserTelephone='" + adminUserTelephone + '\'' +
                ", productRentTime=" + productRentTime +
                ", deviceNumber='" + deviceNumber + '\'' +
                ", maxGetwaterCapacity=" + maxGetwaterCapacity +
                ", maxConsumeCapacity=" + maxConsumeCapacity +
                ", advsCountDown=" + advsCountDown +
                ", operationCountDown=" + operationCountDown +
                '}';
    }
}
