package com.imdetek.radiationmonitoringsystem.view;

import android.widget.Toast;

import com.imdetek.radiationmonitoringsystem.MyApplication;

/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem.view
 * @class describe
 * @anthor toby
 * @time 2016/11/24 下午1:10
 * @change
 * @chang time
 * @class describe
 */
public class MyToast {
    public static void showToast(String message,int duration){
        Toast toast=Toast.makeText(MyApplication.currentActivity(),message,duration);
        toast.show();
    }
    public static void showToastShort(String message){
        MyToast.showToast(message,Toast.LENGTH_SHORT);
    }

    public static void showToastLong(String message){
        MyToast.showToast(message,Toast.LENGTH_LONG);
    }
}
