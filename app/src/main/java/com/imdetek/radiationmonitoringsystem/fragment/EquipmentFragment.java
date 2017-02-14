package com.imdetek.radiationmonitoringsystem.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.imdetek.radiationmonitoringsystem.MyApplication;
import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.activity.DetailsActivity;
import com.imdetek.radiationmonitoringsystem.activity.SceneActivity;
import com.imdetek.radiationmonitoringsystem.activity.VideoActivity;
import com.imdetek.radiationmonitoringsystem.adapter.MainListAdapter;
import com.imdetek.radiationmonitoringsystem.adapter.SceneListAdapter;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;
import com.imdetek.radiationmonitoringsystem.services.MySoundPool;
import com.imdetek.radiationmonitoringsystem.view.MySystemDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EquipmentFragment extends Fragment {

    @BindView(R.id.scene_recycler_view)
    RecyclerView sceneRecyclerView;
    private SceneListAdapter mAdapter;

    private List<Equipment> mEquipments = new ArrayList<>();

    private Thread reflashThread;

    private boolean refreash = false;

    public EquipmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_equipment, container, false);
        ButterKnife.bind(this, view);
        mEquipments = DataManager.getInstance().getEquipmentList();
        initListView();

        reflashThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        while (refreash) {
                            refreshView();
                            Thread.sleep(1000);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        reflashThread.start();
        return view;
    }

    @Override
    public void onPause() {
        refreash = false;
        super.onPause();
    }

    @Override
    public void onResume() {
        refreash = true;
        super.onResume();
    }

    private void refreshView() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mEquipments = DataManager.getInstance().getEquipmentList();
                mAdapter.setData(mEquipments);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initListView() {
        mAdapter = new SceneListAdapter(getActivity(), mEquipments);
        mAdapter.setListener(new MainListAdapter.OnItemTouchedListener() {
            @Override
            public void onItemClicked(int id) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.TAG, id);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(final int id) {
                Log.i("ClickLong", "" + id);
                new MaterialDialog.Builder(MyApplication.currentActivity())
                        .title("警告")
                        .content("您确定要删除设备No." + id + "，以及其所有数据?")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                DataManager.getInstance().deleteEquipment(id);
                                dialog.dismiss();

                            }
                        })
                        .show();
            }

            @Override
            public void onItemSceneBtnClicked(int id) {
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra(VideoActivity.TAG, id);
                startActivity(intent);
            }
        });
        sceneRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        sceneRecyclerView.setAdapter(mAdapter);
    }
}
