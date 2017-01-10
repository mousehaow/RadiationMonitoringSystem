package com.imdetek.radiationmonitoringsystem.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by toby on 2016/12/28.
 */

public class InstrumentBoardModel implements Serializable {

    private float userTotal;//用户信用分
    private String assess;//评价
    private float totalMin;//区间最小值
    private float totalMax;//区间最大值
    private String firstText;//第一个文本值:BETA
    private String fourText;//第四个文本:评估时间
    private String topText;//顶部文本
    private ArrayList<InstrumentBoardItemModel> instrumentBoardItemModels;

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public String getFirstText() {
        return firstText;
    }

    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    public String getFourText() {
        return fourText;
    }

    public void setFourText(String fourText) {
        this.fourText = fourText;
    }

    public float getUserTotal() {
        return userTotal;
    }

    public void setUserTotal(float userTotal) {
        this.userTotal = userTotal;
    }

    public String getAssess() {
        return assess;
    }

    public void setAssess(String assess) {
        this.assess = assess;
    }

    public float getTotalMin() {
        return totalMin;
    }

    public void setTotalMin(float totalMin) {
        this.totalMin = totalMin;
    }

    public float getTotalMax() {
        return totalMax;
    }

    public void setTotalMax(float totalMax) {
        this.totalMax = totalMax;
    }

    public ArrayList<InstrumentBoardItemModel> getInstrumentBoardItemModels() {
        return instrumentBoardItemModels;
    }

    public void setInstrumentBoardItemModels(ArrayList<InstrumentBoardItemModel> instrumentBoardItemModels) {
        this.instrumentBoardItemModels = instrumentBoardItemModels;
    }

}
