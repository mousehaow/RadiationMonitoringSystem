package com.imdetek.radiationmonitoringsystem.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.InstrumentBoardItemModel;
import com.imdetek.radiationmonitoringsystem.entity.InstrumentBoardModel;
import com.imdetek.radiationmonitoringsystem.view.InstrumentBoard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EquipDetailsActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.chart)
    LineChart chart;
    @BindView(R.id.instrument_board)
    InstrumentBoard instrumentBoard;

    private SimpleDateFormat formater = new SimpleDateFormat("yyyy.MM.dd mm:ss");
    private InstrumentBoardModel model;
    int num = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equip_details);
        ButterKnife.bind(this);
        instrumentBoard.setDataModel(getData());

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
    }

    private void refreshView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                num++;
//                model.setUserTotal(num);
                //instrumentBoard.setDataModel(model);
            }
        });
    }

    @OnClick({R.id.back_btn, R.id.back_title_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
            case R.id.back_title_btn:
                finish();
                break;
        }
    }

    private InstrumentBoardModel getData() {
        model = new InstrumentBoardModel();
        model.setUserTotal(949.2f);
        model.setAssess("监测数值");
        model.setTotalMin(350f);
        model.setTotalMax(950f);
        model.setFirstText("μSv/h");
        model.setTopText("No.202");
        model.setFourText("时间:" + formater.format(new Date()));
        ArrayList<InstrumentBoardItemModel> sesameItemModels = new ArrayList<InstrumentBoardItemModel>();

        InstrumentBoardItemModel ItemModel350 = new InstrumentBoardItemModel();
        ItemModel350.setArea("较差");
        ItemModel350.setMin(350f);
        ItemModel350.setMax(550f);
        sesameItemModels.add(ItemModel350);

        InstrumentBoardItemModel ItemModel550 = new InstrumentBoardItemModel();
        ItemModel550.setArea("中等");
        ItemModel550.setMin(550f);
        ItemModel550.setMax(600f);
        sesameItemModels.add(ItemModel550);

        InstrumentBoardItemModel ItemModel600 = new InstrumentBoardItemModel();
        ItemModel600.setArea("良好");
        ItemModel600.setMin(600f);
        ItemModel600.setMax(650f);
        sesameItemModels.add(ItemModel600);

        InstrumentBoardItemModel ItemModel650 = new InstrumentBoardItemModel();
        ItemModel650.setArea("优秀");
        ItemModel650.setMin(650f);
        ItemModel650.setMax(700f);
        sesameItemModels.add(ItemModel650);

        InstrumentBoardItemModel ItemModel700 = new InstrumentBoardItemModel();
        ItemModel700.setArea("较好");
        ItemModel700.setMin(700f);
        ItemModel700.setMax(950f);
        sesameItemModels.add(ItemModel700);

        model.setInstrumentBoardItemModels(sesameItemModels);
        return model;
    }
}
