package com.imdetek.radiationmonitoringsystem.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.adapter.DetailsListAdapter;
import com.imdetek.radiationmonitoringsystem.connect.MySocket;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;
import com.imdetek.radiationmonitoringsystem.entity.Record;
import com.imdetek.radiationmonitoringsystem.view.MyMarkerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends BaseActivity implements OnChartValueSelectedListener, OnChartGestureListener {

    public static final String TAG = "DetailsActivity";

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.current_value_tv)
    TextView mCurrentValueTv;
    @BindView(R.id.max_value_tv)
    TextView mMaxValueTv;
    @BindView(R.id.min_value_tv)
    TextView mMinValueTv;
    @BindView(R.id.scene_tv)
    TextView mSceneTv;
    @BindView(R.id.alarm_past_records_tv)
    TextView mAlarmPastRecordsTv;
    @BindView(R.id.activity_details)
    RelativeLayout mActivityDetails;
    @BindView(R.id.details_recycler_view)
    RecyclerView mDetailsRecyclerView;
    @BindView(R.id.chart)
    LineChart mChart;


    private Equipment mEquipment;
    private Record mRecord;
    private List<Record> pastRecords = new ArrayList<>();
    private DetailsListAdapter mAdapter;


    private SimpleDateFormat mFormatter;

    private boolean seeBefore = false;

    private float chartScale = 8f;

    public int id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        mFormatter = new SimpleDateFormat("hh:mm:ss");
        id = getIntent().getIntExtra(TAG, 0);
        for (Equipment equipment : DataManager.getInstance().getEquipmentList()) {
            if (id == equipment.getId()) {
                mEquipment = equipment;
            }
        }
        pastRecords = DataManager.getInstance().getEquipmentRecordsList(mEquipment.getId());
        if (pastRecords == null) {
            pastRecords = new ArrayList<>();
        }
        for (Record record : DataManager.getInstance().getCurrentRecords()) {
            if (record.getId() == mEquipment.getId()) {
                mRecord = record;
            }
        }
        mTitle.setText(String.format("No.%d", mEquipment.getId()));
        if (MySocket.getInstance().getConnected() && mEquipment != null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            reflashView();
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        mAdapter = new DetailsListAdapter(this, pastRecords);
        mAdapter.setListener(new DetailsListAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int id) {

            }
        });
        mDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mDetailsRecyclerView.setAdapter(mAdapter);
        chartInit();
        mCurrentValueTv.setText("-- μSv/h");
        mMaxValueTv.setText("峰值: -- μSv/h");
        mMinValueTv.setText("谷值: -- μSv/h");
        mSceneTv.setText("场景: " + "--");
        mAlarmPastRecordsTv.setText("报警: -- 次");
    }

    private void chartInit() {
        mChart.setOnChartValueSelectedListener(this);
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        mChart.setOnChartGestureListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(this, R.layout.marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(15f, "阈值");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
        XAxis bottomAxis = mChart.getXAxis();
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setTextSize(8f);
        bottomAxis.setTextColor(Color.BLACK);
        bottomAxis.setDrawAxisLine(true);
        bottomAxis.setDrawGridLines(true);
        bottomAxis.setAvoidFirstLastClipping(true);
        // set a custom value formatter
        bottomAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if ((mRecord.getValues().size() - value) % 2 == 1) {
                    return mFormatter.format(mRecord.getValues().get((int)value).getTime());
                }
                return "";
            }
        });

        // add data
        if (MySocket.getInstance().getConnected()) {
            setData();
        } else {
            mChart.setNoDataText("当前没有检测数据");
        }

        mChart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setData() {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < mRecord.getValues().size(); i++) {
            values.add(new Entry(i, mRecord.getValues().get(i).getValue()));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "监测放射值");

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }

    private void reflashView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentValueTv.setText(String.format("%.2f μSv/h", mEquipment.getCurrentValue()));
                mMaxValueTv.setText(String.format("峰值: %.2f μSv/h", mRecord.getMaxValue()));
                mMinValueTv.setText(String.format("谷值: %.2f μSv/h", mRecord.getMinValue()));
                mSceneTv.setText("场景: " + mEquipment.getScene());
                mAlarmPastRecordsTv.setText(String.format("报警: %d 次", mEquipment.getAlarmTimes()));
                setData();
                mChart.setVisibleXRangeMaximum(chartScale);
                if (!seeBefore)
                    mChart.moveViewToX(mRecord.getValues().size() - mChart.getVisibleXRange());
            }
        });
    }


    @OnClick({R.id.back_title_btn, R.id.video_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_title_btn:
                finish();
                break;
            case R.id.video_btn:
                Intent intent = new Intent(DetailsActivity.this, VideoActivity.class);
                intent.putExtra(VideoActivity.TAG, id);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        if (dX > 0) {
            seeBefore = true;
        } else {
            seeBefore = false;
        }
    }
}
