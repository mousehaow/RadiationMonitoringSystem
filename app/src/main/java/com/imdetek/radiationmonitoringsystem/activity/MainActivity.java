package com.imdetek.radiationmonitoringsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.adapter.MainListAdapter;
import com.imdetek.radiationmonitoringsystem.adapter.MainSceneListAdapter;
import com.imdetek.radiationmonitoringsystem.connect.MySocket;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;
import com.imdetek.radiationmonitoringsystem.entity.Record;
import com.imdetek.radiationmonitoringsystem.entity.Scene;
import com.imdetek.radiationmonitoringsystem.services.MySoundPool;
import com.imdetek.radiationmonitoringsystem.view.DefultItemDecoration;
import com.imdetek.radiationmonitoringsystem.view.MyToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.main_recycler_view)
    RecyclerView mMainRecyclerView;

    private MainSceneListAdapter mAdapter;

    private List<Scene> mScene = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mScene = DataManager.getInstance().getSceneList();
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        refreshView();
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        initListView();
    }

    private void refreshView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScene = DataManager.getInstance().getSceneList();
                mAdapter.setData(mScene);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initListView() {
        mAdapter = new MainSceneListAdapter(this, mScene);
        mAdapter.setListener(new MainSceneListAdapter.OnItemTouchedListener() {
            @Override
            public void onItemClicked(String scene) {
                Intent intent = new Intent(MainActivity.this, SceneActivity.class);
                intent.putExtra(SceneActivity.TAG, scene);
                startActivity(intent);
            }
        });
        mMainRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mMainRecyclerView.setAdapter(mAdapter);
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
                    finish();
                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        DataManager manager = DataManager.getInstance();
        manager.setSceneList(mScene);
        manager.setEquipmentList(manager.getEquipmentList());
        List<Record> records = manager.getCurrentRecords();
        for (Record record : records) {
            record.setEndTime(new Date());
            manager.addToEquipmentRecordsList(record.getId(), record);
        }
        MySocket.getInstance().stop();
        Process.killProcess(Process.myPid());
        System.exit(0);
        super.onDestroy();
    }

    @OnClick(R.id.setting_btn)
    public void onClick() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }
}
