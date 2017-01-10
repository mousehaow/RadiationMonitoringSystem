package com.imdetek.radiationmonitoringsystem.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem.entity
 * @class describe
 * @anthor toby
 * @time 2016/11/23 下午3:20
 * @change
 * @chang time
 * @class describe
 */
public class Record implements Cloneable {

    private int id;
    private Date startTime;
    private Date endTime;
    private int alarmTimes = 0;
    private float avgValue;
    private float maxValue;
    private float minValue;
    private List<RecordValue> mValues = new ArrayList<>();
    private float thresholdValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getAlarmTimes() {
        return alarmTimes;
    }

    public void addAlarmTimes() {
        this.alarmTimes++;
    }

    public float getAvgValue() {
        if (this.getValues().size() == 0) {
            return 0f;
        }
        int i;
        List<RecordValue> datas = this.getValues();
        float sum = 0f;
        for (i = 0; i < datas.size(); i++) {
            sum += datas.get(i).getValue();
        }
        return sum / i;
    }


    public float getMaxValue() {
        if (this.getValues().size() == 0) {
            return 0f;
        }
        int i;
        List<RecordValue> datas = this.getValues();
        float max = datas.get(0).getValue();
        for (i = 0; i < datas.size(); i++) {
            if (max < datas.get(i).getValue())
                max = datas.get(i).getValue();
        }
        return max;
    }


    public float getMinValue() {
        if (this.getValues().size() == 0) {
            return 0f;
        }
        int i;
        List<RecordValue> datas = this.getValues();
        float min = datas.get(0).getValue();
        for (i = 0; i < datas.size(); i++) {
            if (min > datas.get(i).getValue())
                min = datas.get(i).getValue();
        }
        return min;
    }


    public List<RecordValue> getValues() {
        return mValues;
    }

    public void addValues(float values) {
        mValues.add(new RecordValue(new Date() ,values));
    }

    public float getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(float thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public class RecordValue implements Cloneable {

        private Date time;
        private float value;

        public RecordValue(Date time, float value) {
            this.time = time;
            this.value = value;
        }

        public Date getTime() {
            return time;
        }

        public float getValue() {
            return value;
        }

        @Override
        public RecordValue clone() throws CloneNotSupportedException {
            RecordValue clone = null;
            try{
                clone = (RecordValue) super.clone();
                clone.time = (Date) time.clone();
            }catch(CloneNotSupportedException e){
                throw new RuntimeException(e); // won't happen
            }
            return clone;
        }
    }

    @Override
    public Record clone() {
        Record clone = null;
        try{
            clone = (Record) super.clone();
            clone.startTime = (Date) startTime.clone();
            clone.endTime = (Date) endTime.clone();
            clone.mValues = new ArrayList<>();
            for (int i = 0; i < mValues.size(); i++) {
                RecordValue recordValue = mValues.get(i).clone();
                clone.mValues.add(recordValue);
            }

        }catch(CloneNotSupportedException e){
            throw new RuntimeException(e); // won't happen
        }
        return clone;
    }
}
