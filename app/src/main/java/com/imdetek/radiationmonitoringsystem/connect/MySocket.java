package com.imdetek.radiationmonitoringsystem.connect;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.imdetek.radiationmonitoringsystem.MyApplication;
import com.imdetek.radiationmonitoringsystem.activity.DetailsActivity;
import com.imdetek.radiationmonitoringsystem.activity.SettingActivity;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;
import com.imdetek.radiationmonitoringsystem.entity.Record;
import com.imdetek.radiationmonitoringsystem.entity.SettingInfo;
import com.imdetek.radiationmonitoringsystem.services.MySoundPool;
import com.imdetek.radiationmonitoringsystem.view.MySystemDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.List;


/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem.connect
 * @class describe
 * @anthor toby
 * @time 2016/11/24 上午8:36
 * @change
 * @chang time
 * @class describe
 */
public class MySocket {

    public interface SocketConnectCallBack{
        void successCallBack();
        void failCallBack();
    }
    public interface SocketCloseCallBack{
        void successCloseCallBack();
    }

    public static final int MESSAGE_START=11;
    public static final int MESSAGE_STOP=22;

    public Socket socket;
    public Activity context;
    public SocketConnectCallBack callBack;
    public SocketCloseCallBack closeCallBack;
    private DataOutputStream output;
    private BufferedReader input;

    private Thread mReceiveThread;
    private Boolean isConnected = false;
    private static MySocket instance;

    private float thresholdValue;

    private Handler mSendHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case MESSAGE_START:
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                output.writeBytes("S");
                                output.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case MESSAGE_STOP:
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                output.writeBytes("T");
                                output.flush();
                                if(!socket.isInputShutdown()){
                                    try {
                                        socket.shutdownInput();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(!socket.isOutputShutdown()){
                                    try {
                                        socket.shutdownOutput();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mReceiveThread.interrupt();
                                if(socket.isConnected() || !socket.isClosed()){
                                    try {
                                        socket.close();
                                        isConnected = false;
                                        if (context != null) {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (closeCallBack != null) {
                                                        Log.i("Socket", "关闭成功");
                                                        closeCallBack.successCloseCallBack();
                                                    }
                                                }
                                            });
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
            }
        }
    };


    public MySocket() {
    }

    private void receiveThreadInit() {
        mReceiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int waittingCount = 0;
                    while (true) {
                        String result;
                        while(!((result = input.readLine()) == null)){
                            waittingCount = 0;
                            JSONObject jsonObject = null;
                            int id = -1;
                            String type = null;
                            float data = 0f;
                            try {
                                jsonObject = new JSONObject(result.toString());
                                //Log.i("Socket", result.toString());
                                id = Integer.parseInt(jsonObject.getString("id"));
                                type = jsonObject.getString("type").equals("D") ? "数据" : "阈值";
                                data = Float.parseFloat(jsonObject.getString("data"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            DataManager.getInstance().getDataFromSocket(id, type, data);
                            if (SettingInfo.getInstance().warning && type.equals("数据")) {
                                for (Record record : DataManager.getInstance().getCurrentRecords()) {
                                    if (record.getId() == id) {
                                        if (record.getThresholdValue() < data
                                                && record.getValues().get(record.getValues().size() - 2).getValue() < record.getThresholdValue()) {
                                            if (MySystemDialog.getInstance().isShowing() == false) {
                                                MySystemDialog.getInstance().id = id;
                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MySystemDialog.getInstance().showDialog();
                                                        if (SettingInfo.getInstance().sounds) {
                                                            if (!MySoundPool.getInstance().isPalying()) {
                                                                MySoundPool.getInstance().playSound();
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        waittingCount++;
                        Thread.sleep(1000);
                        if (isConnected && waittingCount >= 10) {
                            isConnected = false;
                            for (Equipment equipment : DataManager.getInstance().getEquipmentList()) {
                                equipment.setOnLine(false);
                            }
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new MaterialDialog.Builder(MyApplication.currentActivity())
                                            .title("错误")
                                            .content("当前网络错误！")
                                            .positiveText("重新设置")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                    if (MyApplication.currentActivity().getClass() != SettingActivity.class) {
                                                        Intent intent = new Intent(MyApplication.currentActivity(), SettingActivity.class);
                                                        MyApplication.currentActivity().startActivity(intent);
                                                    }
                                                }
                                            })
                                            .negativeText("取消")
                                            .show();
                                }
                            });
                            DataManager manager = DataManager.getInstance();
                            manager.setEquipmentList(manager.getEquipmentList());
                            List<Record> records = manager.getCurrentRecords();
                            for (Record record : records) {
                                if (record.getValues().size() == 0) {
                                    continue;
                                }
                                record.setEndTime(new Date());
                                manager.addToEquipmentRecordsList(record.getId(), record);
                            }
                            mReceiveThread.interrupt();
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static synchronized MySocket getInstance() {
        if(instance == null) {
            instance = new MySocket();
        }
        return instance;
    }

    public void open() {
        new Thread(){
            @Override
            public void run() {
                try {
                    Log.i("Socket", "start");
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(SettingInfo.getInstance().ip,
                            SettingInfo.getInstance().port), 5000);
                    Log.i("Socket", SettingInfo.getInstance().ip);
                    Log.i("Socket", String.valueOf(SettingInfo.getInstance().port));
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new DataOutputStream(socket.getOutputStream());
                    receiveThreadInit();
                    mReceiveThread.start();
                    if(mSendHandler!=null){
                        mSendHandler.sendEmptyMessage(MESSAGE_START);
                    }
                    isConnected = true;
                    if (context != null) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (callBack != null) {
                                    callBack.successCallBack();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    isConnected = false;
                    e.printStackTrace();
                    if (context != null) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (callBack != null) {
                                    callBack.failCallBack();
                                }
                            }
                        });
                    }
                }
            }
        }.start();
    }
    public void stop() {
        new Thread(){
            @Override
            public void run() {
                if(mSendHandler != null){
                    mSendHandler.sendEmptyMessage(MESSAGE_STOP);
                }
            }
        }.start();

    }

    public Boolean getConnected() {
        return isConnected;
    }
}
