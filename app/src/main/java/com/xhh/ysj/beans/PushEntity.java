package com.xhh.ysj.beans;

import java.util.List;

public class PushEntity {

    private int deviceId;
    private int operationType;
    private String pushId;
    private List<String> pushIdList;
    private String title;
    private String operationContent;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public List<String> getPushIdList() {
        return pushIdList;
    }

    public void setPushIdList(List<String> pushIdList) {
        this.pushIdList = pushIdList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOperationContent() {
        return operationContent;
    }

    public void setOperationContent(String operationContent) {
        this.operationContent = operationContent;
    }

    @Override
    public String toString() {
        return "PushEntity{" +
                "deviceId=" + deviceId +
                ", operationType=" + operationType +
                ", pushId='" + pushId + '\'' +
                ", pushIdList=" + pushIdList +
                ", title='" + title + '\'' +
                ", operationContent='" + operationContent + '\'' +
                '}';
    }
}
