package com.imdetek.radiationmonitoringsystem.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.imdetek.radiationmonitoringsystem.MyApplication;
import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.activity.DetailsActivity;
import com.imdetek.radiationmonitoringsystem.activity.MainActivity;
import com.imdetek.radiationmonitoringsystem.services.MySoundPool;

import java.util.List;

/**
 * Created by toby on 2016/12/19.
 */

public class MySystemDialog {

    private static MySystemDialog instance = null;
    private boolean isShowing = false;

    public static synchronized MySystemDialog getInstance() {
        if (instance == null) {
            instance = new MySystemDialog();
        }
        return instance;
    }

    public int id = 0;

    private MySystemDialog() {}

    public boolean isShowing() {
        return isShowing;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyApplication.mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setIcon(R.drawable.warning);
        builder.setTitle("警告！！！");
        builder.setMessage(String.format("设备：No.%d 检测值超过阈值！", id));
        builder.setPositiveButton("查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isShowing = false;
                if (MySoundPool.getInstance().isPalying()) {
                    MySoundPool.getInstance().stopPlay();
                }
                if ("com.imdetek.radiationmonitoringsystem".equals(getCurrentPackageName(MyApplication.mContext))) {
                    if (MyApplication.currentActivity().getClass() == DetailsActivity.class) {
                        if (((DetailsActivity)MyApplication.currentActivity()).id == id) {
                            return;
                        } else {
                            MyApplication.finishCurrentActivity();
                        }
                    }
                    Intent intent = new Intent(MyApplication.currentActivity(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.TAG, id);
                    MyApplication.currentActivity().startActivity(intent);
                } else {
                    backToTopTask();
                    if (MyApplication.currentActivity().getClass() == DetailsActivity.class) {
                        if (((DetailsActivity)MyApplication.currentActivity()).id == id) {
                            return;
                        } else {
                            MyApplication.finishCurrentActivity();
                        }
                    }
                    Intent intent = new Intent(MyApplication.currentActivity(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.TAG, id);
                    MyApplication.currentActivity().startActivity(intent);
                }
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isShowing = false;
                if (MySoundPool.getInstance().isPalying()) {
                    MySoundPool.getInstance().stopPlay();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//指定会全局,可以在后台弹出
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(MyApplication.mContext)) {
                return;
            } else {
                //Android6.0以上
                if (dialog != null && dialog.isShowing() == false) {
                    dialog.show();
                    isShowing = true;
                }
            }
        } else {
            //Android6.0以下，不用动态声明权限
            if (dialog != null && dialog.isShowing() == false) {
                dialog.show();
                isShowing = true;
            }
        }

    }
    private String getCurrentPackageName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getPackageName();
    }

    private void backToTopTask() {
        ActivityManager manager = (ActivityManager) MyApplication.mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task_info = manager.getRunningTasks(20);
        String className = "";
        for (int i = 0; i < task_info.size(); i++)
        {
            if ("com.imdetek.radiationmonitoringsystem".equals(task_info
                    .get(i).topActivity.getPackageName())) {
                System.out.println("后台  "
                        + task_info.get(i).topActivity
                        .getClassName());
                className = task_info.get(i).topActivity
                        .getClassName();
                //这里是指从后台返回到前台  前两个的是关键
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                try {
                    intent.setComponent(new ComponentName(MyApplication.mContext, Class.forName(className)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                MyApplication.mContext.startActivity(intent);
            }
        }
    }
}
