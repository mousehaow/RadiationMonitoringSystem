package com.imdetek.radiationmonitoringsystem.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.connect.MySocket;
import com.imdetek.radiationmonitoringsystem.entity.SettingInfo;
import com.imdetek.radiationmonitoringsystem.view.MyToast;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.ip_edit_text)
    EditText mIpEditText;
    @BindView(R.id.port_edit_text)
    EditText mPortEditText;
    @BindView(R.id.warning_switch)
    SwitchButton warningSwitch;
    @BindView(R.id.sound_switch)
    SwitchButton soundSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mIpEditText.setText(SettingInfo.getInstance().ip);
        mPortEditText.setText(String.valueOf(SettingInfo.getInstance().port));
        warningSwitch.setChecked(SettingInfo.getInstance().warning);
        soundSwitch.setChecked(SettingInfo.getInstance().sounds);
    }

    @OnClick({R.id.back_title_btn, R.id.connect_btn, R.id.submit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_title_btn:
                finish();
                break;
            case R.id.connect_btn:
                if (MySocket.getInstance().getConnected()) {
                    new MaterialDialog.Builder(this)
                            .title("警告")
                            .content("您当前已经连接，是否要重新连接？")
                            .positiveText("确定")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    socketDisconnect();
                                }
                            })
                            .show();
                } else {
                    socketConnect();
                }
                break;
            case R.id.submit_btn:
                checkSubmitInfo();
                break;
        }
    }

    private void socketDisconnect() {
        MySocket.getInstance().context = this;
        MySocket.getInstance().closeCallBack = new MySocket.SocketCloseCallBack() {
            @Override
            public void successCloseCallBack() {
                MySocket.getInstance().closeCallBack = null;
                MySocket.getInstance().context = null;
                socketConnect();
            }
        };
        MySocket.getInstance().stop();
    }


    private void socketConnect() {
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);
        }
        MySocket.getInstance().context = this;
        MySocket.getInstance().callBack = new MySocket.SocketConnectCallBack() {
            @Override
            public void successCallBack() {
                MyToast.showToastLong("连接成功！");
                MySocket.getInstance().callBack = null;
                MySocket.getInstance().context = null;
                finish();
            }

            @Override
            public void failCallBack() {
                MyToast.showToastLong("连接失败，请设置正确连接信息");
            }
        };
        MySocket.getInstance().open();
    }

    private void checkSubmitInfo() {
        if (mIpEditText.getText().length() > 0
                && mPortEditText.getText().length() > 0) {
            SharedPreferences settings = getSharedPreferences(SettingInfo.MY_PREDS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("ip", mIpEditText.getText().toString());
            editor.putInt("port", new Integer(mPortEditText.getText().toString()).intValue());
            editor.putBoolean("warning", warningSwitch.isChecked());
            editor.putBoolean("sounds", soundSwitch.isChecked());
            /* Commit the edits!*/
            editor.commit();
            SettingInfo.getInstance().ip = mIpEditText.getText().toString();
            SettingInfo.getInstance().port = new Integer(mPortEditText.getText().toString()).intValue();
            SettingInfo.getInstance().warning = warningSwitch.isChecked();
            SettingInfo.getInstance().sounds = soundSwitch.isChecked();
            MyToast.showToastLong("保存成功！");
        } else {
            new MaterialDialog.Builder(this)
                    .title("警告")
                    .content("您输入的信息有误！")
                    .positiveText("重新输入")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            mIpEditText.setText(SettingInfo.getInstance().ip);
                            mPortEditText.setText(String.valueOf(SettingInfo.getInstance().port));
                        }
                    })
                    .show();
        }
    }

}
