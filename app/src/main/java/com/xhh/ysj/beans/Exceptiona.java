package com.xhh.ysj.beans;

/**
 * Created by Administrator on 2018/7/18 0018.
 */

public class Exceptiona {
    private int exceptionalId;
    /**
     * 类型:0 system-service 1xhh-web 2xhh-api 3xhh-wechat 4xhh-bridge 5xhh-task 6android 9other
     */
    private int exceptionalType;
    /**
     * 异常主题
     */
    private String exceptionalSubject ;
    /**
     * 异常内容
     */
    private String  exceptionalContent ;

    /**
     * 异常产生的时间
     */

    private String exceptionalTime;
    /**
     * 异常的状态 0未处理 1已处理
     */
    private Integer exceptionalStatus ;

    public int getExceptionalId() {
        return exceptionalId;
    }

    public void setExceptionalId(int exceptionalId) {
        this.exceptionalId = exceptionalId;
    }

    public int getExceptionalType() {
        return exceptionalType;
    }

    public void setExceptionalType(int exceptionalType) {
        this.exceptionalType = exceptionalType;
    }

    public String getExceptionalSubject() {
        return exceptionalSubject;
    }

    public void setExceptionalSubject(String exceptionalSubject) {
        this.exceptionalSubject = exceptionalSubject;
    }

    public String getExceptionalContent() {
        return exceptionalContent;
    }

    public void setExceptionalContent(String exceptionalContent) {
        this.exceptionalContent = exceptionalContent;
    }

    public String getExceptionalTime() {
        return exceptionalTime;
    }

    public void setExceptionalTime(String exceptionalTime) {
        this.exceptionalTime = exceptionalTime;
    }

    public Integer getExceptionalStatus() {
        return exceptionalStatus;
    }

    public void setExceptionalStatus(Integer exceptionalStatus) {
        this.exceptionalStatus = exceptionalStatus;
    }
}
