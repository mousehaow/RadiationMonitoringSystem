package com.imdetek.radiationmonitoringsystem.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.imdetek.radiationmonitoringsystem.MyApplication;
import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.connect.MySocket;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.SettingInfo;
import com.imdetek.radiationmonitoringsystem.services.MySoundPool;
import com.imdetek.radiationmonitoringsystem.view.LogoImageView;
import com.imdetek.radiationmonitoringsystem.view.MyToast;

public class WelcomeActivity extends AppCompatActivity {

    private Activity context;
    private LogoImageView logoIcon;
    private TextView loadText;
    private ImageView loadingIcon;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = this;
        logoIcon = (LogoImageView) findViewById(R.id.logo_image);
        loadText = (TextView) findViewById(R.id.loading_text);
        logoIcon.setViewInterface(new LogoImageView.LogoImageViewInterface() {
            @Override
            public void heightSetSuccess(int width, int height) {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_welcome);
                loadingIcon = new ImageView(context);
                loadingIcon.setBackgroundResource(R.color.welcome);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dip2px(context, 100), height);
                layoutParams.topMargin = (context.getWindowManager().getDefaultDisplay().getHeight() / 2 - height / 2);
                layoutParams.leftMargin = -dip2px(context, 100) / 2 - 20;
                layout.addView(loadingIcon, layout.getChildCount(), layoutParams);
                TranslateAnimation animation = new TranslateAnimation(0,
                        width + dip2px(context, 100) + 40, 0, 0);
                animation.setDuration(1000);
                animation.setRepeatCount(-1);
                animation.setRepeatMode(Animation.REVERSE);
                loadingIcon.startAnimation(animation);
            }
        });
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        refreshView();
                        Thread.sleep(400);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        permission();
    }

    private void refreshView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count == 6) {
                    count = 0;
                }
                switch (count) {
                    case 0:
                        loadText.setText(".");
                        break;
                    case 1:
                        loadText.setText("..");
                        break;
                    case 2:
                        loadText.setText("...");
                        break;
                    case 3:
                        loadText.setText("....");
                        break;
                    case 4:
                        loadText.setText(".....");
                        break;
                    case 5:
                        loadText.setText("......");
                        break;
                }
                count++;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MySoundPool.getInstance().putSound(MyApplication.mContext);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
                Log.i("hello", "start");
                DataManager.getInstance().getSceneList();
                DataManager.getInstance().getEquipmentList();
                DataManager.getInstance().initEquipmentList();
                SharedPreferences settings = getSharedPreferences(SettingInfo.MY_PREDS, 0);
                SettingInfo.getInstance().ip = settings.getString("ip", "192.168.0.102");
                SettingInfo.getInstance().port = settings.getInt("port", 8080);
                SettingInfo.getInstance().warning = settings.getBoolean("warning", true);
                SettingInfo.getInstance().sounds = settings.getBoolean("sounds", true);
                Log.i("hello", "initOver");
                MySocket.getInstance().context = context;
                MySocket.getInstance().callBack = new MySocket.SocketConnectCallBack() {
                    @Override
                    public void successCallBack() {
                        loadingIcon.clearAnimation();
                        MyToast.showToastLong("连接成功！");
                        MySocket.getInstance().callBack = null;
                        MySocket.getInstance().context = null;
                        Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                        startActivity(intent);
                        //loadingIcon.clearAnimation();
                        WelcomeActivity.this.finish();
                    }

                    @Override
                    public void failCallBack() {
                        loadingIcon.clearAnimation();
                        MyToast.showToastLong("连接失败，请设置正确连接信息");
                        Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                        startActivity(intent);
                        if (DataManager.getInstance().mEquipmentList.size() == 0) {
                            intent = new Intent(WelcomeActivity.this, SettingActivity.class);
                            startActivity(intent);
                        }
                        //loadingIcon.clearAnimation();
                        WelcomeActivity.this.finish();
                    }
                };
                MySocket.getInstance().open();
            }
        }, 4900);
    }

    private void permission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(MyApplication.mContext)) {
                new MaterialDialog.Builder(this)
                        .title("提醒")
                        .content("程序需要您打开通知权限。")
                        .positiveText("确定")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                startActivity(intent);
                            }
                        })
                        .show();
                return;
            }
        }
    }
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
