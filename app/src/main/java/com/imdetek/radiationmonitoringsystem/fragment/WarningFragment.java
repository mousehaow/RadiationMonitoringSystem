package com.imdetek.radiationmonitoringsystem.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.activity.DetailsActivity;
import com.imdetek.radiationmonitoringsystem.activity.VideoActivity;
import com.imdetek.radiationmonitoringsystem.adapter.MainListAdapter;
import com.imdetek.radiationmonitoringsystem.adapter.SceneListAdapter;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WarningFragment extends Fragment {

    @BindView(R.id.warning_recycler_view)
    RecyclerView warningRecyclerView;
    private SceneListAdapter mAdapter;

    private List<Equipment> mEquipments = new ArrayList<>();

    private Thread reflashThread;

    private boolean refreash = false;

    public WarningFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        ButterKnife.bind(this, view);
        getWarningEquipments();
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

    @Override
    public void onDestroy() {
        reflashThread.interrupt();
        super.onDestroy();
    }

    private void getWarningEquipments() {
        mEquipments.clear();
        for (Equipment equipment : DataManager.getInstance().getEquipmentList()) {
            if (equipment.getAlarmTimes() > 0) {
                mEquipments.add(equipment);
            }
        }
    }

    private void refreshView() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                getWarningEquipments();
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
            public void onItemLongClicked(int id) {

            }

            @Override
            public void onItemSceneBtnClicked(int id) {
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra(VideoActivity.TAG, id);
                startActivity(intent);
            }
        });
        warningRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        warningRecyclerView.setAdapter(mAdapter);
    }
}
