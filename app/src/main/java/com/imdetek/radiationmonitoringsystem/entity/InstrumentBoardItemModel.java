package com.imdetek.radiationmonitoringsystem.entity;

import java.io.Serializable;

/**
 * Created by toby on 2016/12/28.
 */

public class InstrumentBoardItemModel implements Serializable {

    private String area;//区域:良好,中等,较差...
    private float min;//该区域最小值
    private float max;//该区域最大值

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }
}
