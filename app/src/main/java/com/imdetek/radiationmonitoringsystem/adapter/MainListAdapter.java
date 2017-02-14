package com.imdetek.radiationmonitoringsystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem.adapter
 * @class describe
 * @anthor toby
 * @time 2016/11/23 下午1:49
 * @change
 * @chang time
 * @class describe
 */
public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static interface OnItemTouchedListener {
        public void onItemClicked(int id);
        public void onItemLongClicked(int id);
        public void onItemSceneBtnClicked(int id);
    }

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Equipment> mData;
    private OnItemTouchedListener mListener;

    public MainListAdapter(Context context, List<Equipment> data) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mData = data;
    }

    public void setListener(OnItemTouchedListener listener) {
        mListener = listener;
    }

    public void setData(List<Equipment> data) {
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_main_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        MainListAdapter.ViewHolder holder = (MainListAdapter.ViewHolder)viewHolder;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click", "" + position);
                mListener.onItemClicked(mData.get(position).getId());
            }
        });
        holder.mSceneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemSceneBtnClicked(mData.get(position).getId());
            }
        });
        holder.mItemEqmId.setText(String.format("设备: No.%d", mData.get(position).getId()));
        holder.mItemEqmScene.setText(String.format("场景: %s", mData.get(position).getScene()));
        holder.mItemEqmState.setText(String.format("状态: %s",
                mData.get(position).isOnLine() ? "连接" : "离线"));
        holder.mItemAlarmTime.setText(String.format("报警: %d 次", mData.get(position).getAlarmTimes()));
        holder.mItemCurrentValue.setText(String.format("检测: %.2f μSv/h", mData.get(position).getCurrentValue()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_eqm_id)
        TextView mItemEqmId;
        @BindView(R.id.item_eqm_scene)
        TextView mItemEqmScene;
        @BindView(R.id.item_eqm_state)
        TextView mItemEqmState;
        @BindView(R.id.item_alarm_time)
        TextView mItemAlarmTime;
        @BindView(R.id.item_current_value)
        TextView mItemCurrentValue;
        @BindView(R.id.scene_btn)
        ImageButton mSceneBtn;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
