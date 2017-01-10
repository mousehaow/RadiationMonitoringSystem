package com.imdetek.radiationmonitoringsystem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;
import com.imdetek.radiationmonitoringsystem.entity.Scene;
import com.imdetek.radiationmonitoringsystem.view.ItemSceneImage;
import com.imdetek.radiationmonitoringsystem.view.SceneImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by toby on 2016/12/27.
 */

public class MainSceneListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static interface OnItemTouchedListener {
        public void onItemClicked(String name);
    }

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Scene> mData;
    private OnItemTouchedListener mListener;

    public MainSceneListAdapter(Context context, List<Scene> data) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mData = data;
    }

    public void setListener(OnItemTouchedListener mListener) {
        this.mListener = mListener;
    }

    public void setData(List<Scene> mData) {
        this.mData = mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_scene, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder)viewHolder;
        holder.itemSceneName.setText(mData.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(mData.get(position).name);
            }
        });
        List<Equipment> data = new ArrayList<>();
        for (Equipment equipment : DataManager.getInstance().getEquipmentList()) {
            if (equipment.getScene().equals(mData.get(position).name)) {
                data.add(equipment);
            }
        }
        holder.itemSceneImage.setData(data);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_scene_name)
        TextView itemSceneName;
        @BindView(R.id.item_scene_image)
        ItemSceneImage itemSceneImage;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
