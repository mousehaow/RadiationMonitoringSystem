package com.imdetek.radiationmonitoringsystem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.entity.Record;


import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem.adapter
 * @class describe
 * @anthor toby
 * @time 2016/11/23 下午3:17
 * @change
 * @chang time
 * @class describe
 */
public class DetailsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static interface OnItemClickedListener {
        public void onItemClicked(int id);
    }

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Record> mData;
    private OnItemClickedListener mListener;

    private SimpleDateFormat mFormatter;

    public DetailsListAdapter(Context context, List<Record> data) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mData = data;
        mFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    }

    public void setListener(OnItemClickedListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_details_list, parent, false);
       ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder)viewHolder;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(mData.get(position).getId());
            }
        });
        holder.mItemStartTime.setText(String.format("起始: %s",
                mFormatter.format(mData.get(position).getStartTime())));
        holder.mItemStopTime.setText(String.format("结束: %s",
                mFormatter.format(mData.get(position).getEndTime())));
        holder.mItemAlarmTime.setText(String.format("报警: %d 次",
                mData.get(position).getAlarmTimes()));
        holder.mItemAvgValue.setText(String.format("平均: %.2f μSv/h",
                mData.get(position).getAvgValue()));
        holder.mItemMaxValue.setText(String.format("峰值: %.2f μSv/h",
                mData.get(position).getMaxValue()));
        holder.mItemMinValue.setText(String.format("谷值: %.2f μSv/h",
                mData.get(position).getMinValue()));
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_start_time)
        TextView mItemStartTime;
        @BindView(R.id.item_stop_time)
        TextView mItemStopTime;
        @BindView(R.id.item_alarm_time)
        TextView mItemAlarmTime;
        @BindView(R.id.item_avg_value)
        TextView mItemAvgValue;
        @BindView(R.id.item_max_value)
        TextView mItemMaxValue;
        @BindView(R.id.item_min_value)
        TextView mItemMinValue;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
