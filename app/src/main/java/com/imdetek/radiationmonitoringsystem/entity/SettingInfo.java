package com.imdetek.radiationmonitoringsystem.entity;

/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem.entity
 * @class describe
 * @anthor toby
 * @time 2016/11/25 上午10:49
 * @change
 * @chang time
 * @class describe
 */
public class SettingInfo {

    public static final String MY_PREDS = "MyPrefsFile";

    public String ip;
    public int port;
    public boolean warning;
    public boolean sounds;
    //public float thresholdValue;

    private static SettingInfo instance;

    public static synchronized SettingInfo getInstance() {
        if(instance == null) {
            instance = new SettingInfo();
        }
        return instance;
    }

}
