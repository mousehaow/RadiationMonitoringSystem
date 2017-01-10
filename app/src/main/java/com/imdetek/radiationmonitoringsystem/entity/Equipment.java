package com.imdetek.radiationmonitoringsystem.entity;

import java.util.List;

/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem.entity
 * @class describe
 * @anthor toby
 * @time 2016/11/23 下午1:54
 * @change
 * @chang time
 * @class describe
 */
public class Equipment {

    private int id;
    private String scene;
    private int alarmTimes = 0;
    private boolean onLine;
    private float currentValue;
    private float thresholdValue;
    private float localX;
    private float localY;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public int getAlarmTimes() {
        return alarmTimes;
    }

    public void addAlarmTimes() {
        this.alarmTimes++;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
    }

    public float getLocalX() {
        return localX;
    }

    public void setLocalX(float localX) {
        this.localX = localX;
    }

    public float getLocalY() {
        return localY;
    }

    public void setLocalY(float localY) {
        this.localY = localY;
    }

    public float getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(float thresholdValue) {
        this.thresholdValue = thresholdValue;
    }
}
