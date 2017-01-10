package com.imdetek.radiationmonitoringsystem.fragment;


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
import com.imdetek.radiationmonitoringsystem.activity.SceneActivity;
import com.imdetek.radiationmonitoringsystem.adapter.MainSceneListAdapter;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Scene;
import com.imdetek.radiationmonitoringsystem.view.MyScrollLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SceneFragment extends Fragment {


    @BindView(R.id.main_recycler_view)
    RecyclerView mainRecyclerView;
    @BindView(R.id.scroll_layout)
    MyScrollLayout scrollLayout;

    private MainSceneListAdapter mAdapter;

    private List<Scene> mScene = new ArrayList<>();

    private Thread reflashThread;

    private boolean refreash = false;

    public SceneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scene, container, false);
        ButterKnife.bind(this, view);
        operateView();
        return view;
    }

    private void operateView() {
        mScene = DataManager.getInstance().getSceneList();
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void refreshView() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mScene = DataManager.getInstance().getSceneList();
                mAdapter.setData(mScene);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private void initListView() {
        mAdapter = new MainSceneListAdapter(getActivity(), mScene);
        mAdapter.setListener(new MainSceneListAdapter.OnItemTouchedListener() {
            @Override
            public void onItemClicked(String scene) {
                Intent intent = new Intent(getActivity(), SceneActivity.class);
                intent.putExtra(SceneActivity.TAG, scene);
                startActivity(intent);
            }
        });
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mainRecyclerView.setAdapter(mAdapter);
    }

}
