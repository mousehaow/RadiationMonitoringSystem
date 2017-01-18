package com.imdetek.radiationmonitoringsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.adapter.ViewpagerAdapter;
import com.imdetek.radiationmonitoringsystem.connect.MySocket;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Record;
import com.imdetek.radiationmonitoringsystem.entity.TabEntity;
import com.imdetek.radiationmonitoringsystem.fragment.EquipmentFragment;
import com.imdetek.radiationmonitoringsystem.fragment.SceneFragment;
import com.imdetek.radiationmonitoringsystem.fragment.WarningFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements MySocket.SocketCloseCallBack {

    public static String[] tabTitle = {"区域地图", "设备列表", "报警记录"};
    @BindView(R.id.tab_picker)
    CommonTabLayout tabPicker;

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    @BindView(R.id.setting_btn)
    TextView settingBtn;
    @BindView(R.id.home_viewpager)
    ViewPager homeViewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        for (int i = 0; i < tabTitle.length; i++) {
            mTabEntities.add(new TabEntity(tabTitle[i], 0, 0));
        }
        tabPicker.setTabData(mTabEntities);
        operateView();
    }

    private void operateView() {
        if (homeViewpager != null) {
            setUpViewPager(homeViewpager);
        }
        tabPicker.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                homeViewpager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        homeViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabPicker.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabPicker.setCurrentTab(0);
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        SceneFragment sceneFragment = new SceneFragment();

        EquipmentFragment equipmentFragment = new EquipmentFragment();

        WarningFragment warningFragment = new WarningFragment();

        adapter.addFragments(sceneFragment, tabTitle[0]);
        adapter.addFragments(equipmentFragment, tabTitle[1]);
        adapter.addFragments(warningFragment, tabTitle[2]);
        viewPager.setAdapter(adapter);
    }

    private boolean firstBack = false;
    private TimerTask task;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK://处理返回键消息响应
                if (!firstBack) {
                    firstBack = true;
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            firstBack = false;
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 2000);
                    return true;
                } else {
                    MySocket.getInstance().closeCallBack = this;
                    MySocket.getInstance().context = this;
                    DataManager manager = DataManager.getInstance();
                    manager.setSceneList(manager.getSceneList());
                    manager.setEquipmentList(manager.getEquipmentList());
                    for (Record record : manager.getCurrentRecords()) {
                        if (record.getValues().size() == 0) {
                            continue;
                        }
                        record.setEndTime(new Date());
                        manager.addToEquipmentRecordsList(record.getId(), record.clone());
                    }
                    MySocket.getInstance().stop();
                    //Process.killProcess(Process.myPid());
                    //System.exit(0);
                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {

        Log.i("Home", "destory");

        super.onDestroy();
    }

    @OnClick(R.id.setting_btn)
    public void onClick() {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void successCloseCallBack() {
        finish();
    }
}
