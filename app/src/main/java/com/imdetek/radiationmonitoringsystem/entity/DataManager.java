package com.imdetek.radiationmonitoringsystem.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imdetek.radiationmonitoringsystem.connect.MySocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem.entity
 * @class describe
 * @anthor toby
 * @time 2016/11/24 下午2:39
 * @change
 * @chang time
 * @class describe
 */
public class DataManager {

    private static final String EQUIPMENT_LIST_PATH =
            "/data/data/com.imdetek.radiationmonitoringsystem/files/equipment";
    private static final String EQUIPMENT_FILE_NAME = "/equipmentList.txt";
    private static final String SCENE_LIST_PATH =
            "/data/data/com.imdetek.radiationmonitoringsystem/files/scene";
    private static final String SCENE_FILE_NAME = "/sceneList.txt";
    private static final String Record_FILE_NAME = "/recordList.txt";

    private static DataManager instance;

    public static synchronized DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Equipment> mEquipmentList = null;

    public List<Scene> mSceneList = null;

    public List<Record> mCurrentRecords = new ArrayList<Record>();

    public List<Equipment> getEquipmentList() {
        if (mEquipmentList == null) {
            String json = null;
            File file = new File(EQUIPMENT_LIST_PATH + EQUIPMENT_FILE_NAME);
            if (file.exists()) {
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    int length = inputStream.available();
                    byte [] buffer = new byte[length];
                    inputStream.read(buffer);
                    json = new String(buffer, "UTF-8");
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                mEquipmentList = gson.fromJson(json, new TypeToken<ArrayList<Equipment>>() {}.getType());
                if (mEquipmentList == null) {
                    mEquipmentList = new ArrayList<Equipment>();
                }
            } else {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try {
                    file.createNewFile();
                    mEquipmentList = new ArrayList<Equipment>();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mEquipmentList;
    }

    public void initEquipmentList() {
        if (mEquipmentList != null) {
            for (Equipment equipment : this.mEquipmentList) {
                equipment.setCurrentValue(0f);
                equipment.setOnLine(false);
            }
        }

    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        File file = new File(EQUIPMENT_LIST_PATH + EQUIPMENT_FILE_NAME);
        Gson gson = new Gson();
        String json = gson.toJson(equipmentList);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists()) {
            try{
                FileOutputStream out = new FileOutputStream(file, false);
                byte [] bytes = json.getBytes();
                out.write(bytes);
                out.flush();
                out.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        mEquipmentList = equipmentList;
    }

    public List<Scene> getSceneList() {
        if (mSceneList == null) {
            String json = null;
            File file = new File(SCENE_LIST_PATH + SCENE_FILE_NAME);
            if (file.exists()) {
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    int length = inputStream.available();
                    byte [] buffer = new byte[length];
                    inputStream.read(buffer);
                    json = new String(buffer, "UTF-8");
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                mSceneList = gson.fromJson(json, new TypeToken<ArrayList<Scene>>() {}.getType());
                if (mSceneList == null) {
                    mSceneList = new ArrayList<Scene>();
                }
            } else {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try {

                    file.createNewFile();
                    mSceneList = new ArrayList<Scene>();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mSceneList;
    }

    public void setSceneList(List<Scene> sceneList) {
        File file = new File(SCENE_LIST_PATH + SCENE_FILE_NAME);
        Gson gson = new Gson();
        String json = gson.toJson(sceneList);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists()) {
            try{
                FileOutputStream out = new FileOutputStream(file, false);
                byte [] bytes = json.getBytes();
                out.write(bytes);
                out.flush();
                out.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        mSceneList = sceneList;
    }

    public List<Record> getEquipmentRecordsList(int id) {
        ArrayList<Record> records = null;
        File file = new File(EQUIPMENT_LIST_PATH +"/" + id + Record_FILE_NAME);
        String json = null;
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                int length = inputStream.available();
                byte[] buffer = new byte[length];
                inputStream.read(buffer);
                json = new String(buffer, "UTF-8");
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            records = gson.fromJson(json, new TypeToken<ArrayList<Record>>() {
            }.getType());
        }
        return records;
    }
    public void addToEquipmentRecordsList(int id, Record record) {
        File file = new File(EQUIPMENT_LIST_PATH +"/" + id + Record_FILE_NAME);
        String json = null;
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                int length = inputStream.available();
                byte [] buffer = new byte[length];
                inputStream.read(buffer);
                json = new String(buffer, "UTF-8");
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            ArrayList<Record> records = gson.fromJson(json, new TypeToken<ArrayList<Record>>() {}.getType());
            if (records == null) {
                records = new ArrayList<>();
            }
            records.add(record);
            json = gson.toJson(records);
            try{
                FileOutputStream out = new FileOutputStream(file, false);
                byte [] bytes = json.getBytes();
                out.write(bytes);
                out.flush();
                out.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Record> getCurrentRecords() {
        return mCurrentRecords;
    }

    public void setCurrentRecords(List<Record> currentRecords) {
        mCurrentRecords = currentRecords;
    }

    public int getDataFromSocket(int id, String type, float data) {
        boolean hasEquipment = false;
        Equipment resultEquipment = null;
        Record resultRecord = null;
        float lastValue = 0f;
        for (int i = 0; i < mEquipmentList.size(); i++) {
            Equipment equipment = mEquipmentList.get(i);
            if (equipment.getId() == id) {
                hasEquipment = true;
                equipment.setOnLine(true);
                if (type.equals("阈值")) {
                    equipment.setThresholdValue(data);
                    boolean hasScene = false;
                    for (Scene scene : DataManager.getInstance().getSceneList()) {
                        if (scene.name.equals(equipment.getScene())) {
                            hasScene = true;
                            boolean hasEqi = false;
                            for (Integer integer : scene.equipmentsIdList) {
                                if (integer.intValue() == id) {
                                    hasEqi = true;
                                }
                            }
                            if (!hasEqi) {
                                scene.equipmentsIdList.add(new Integer(id));
                            }
                        }
                    }
                    if (!hasScene) {
                        Scene scene = new Scene();
                        scene.name = equipment.getScene();
                        scene.equipmentsIdList = new ArrayList<>();
                        scene.equipmentsIdList.add(new Integer(id));
                        DataManager.getInstance().getSceneList().add(scene);
                    }
                    boolean hasRecord = false;
                    for (int j = 0; j < mCurrentRecords.size(); j++) {
                        Record record = mCurrentRecords.get(j);
                        if (record.getId() == id) {
                            hasRecord = true;
                            record.setThresholdValue(data);
                        }
                    }
                    if (!hasRecord) {
                        Record record = new Record();
                        record.setId(id);
                        record.setStartTime(new Date());
                        record.setThresholdValue(data);
                        mCurrentRecords.add(record);
                    }
                } else {
                    lastValue = equipment.getCurrentValue();
                    equipment.setCurrentValue(data);
                    boolean hasRecord = false;
                    for (int j = 0; j < mCurrentRecords.size(); j++) {
                        Record record = mCurrentRecords.get(j);
                        if (record.getId() == id) {
                            hasRecord = true;
                            record.addValues(data);
                            resultRecord = record;
                        }
                    }
                    if (!hasRecord) {
                        Record record = new Record();
                        record.setId(id);
                        record.setStartTime(new Date());
                        record.addValues(data);
                        mCurrentRecords.add(record);
                        resultRecord = record;
                    }
                }
                resultEquipment = equipment;
                break;
            }
        }
        if (!hasEquipment) {
            Equipment equipment = new Equipment();
            equipment.setId(id);
            equipment.setOnLine(true);
            equipment.setScene("场景一");
            Record record = new Record();
            record.setId(id);
            record.setStartTime(new Date());
            if (type.equals("阈值")) {
                boolean hasScene = false;
                for (Scene scene : DataManager.getInstance().getSceneList()) {
                    if (scene.name.equals(equipment.getScene())) {
                        hasScene = true;
                        boolean hasEqi = false;
                        for (Integer integer : scene.equipmentsIdList) {
                            if (integer.intValue() == id) {
                                hasEqi = true;
                            }
                        }
                        if (!hasEqi) {
                            scene.equipmentsIdList.add(new Integer(id));
                        }
                    }
                }
                if (!hasScene) {
                    Scene scene = new Scene();
                    scene.name = equipment.getScene();
                    scene.equipmentsIdList = new ArrayList<>();
                    scene.equipmentsIdList.add(new Integer(id));
                    DataManager.getInstance().getSceneList().add(scene);
                }
                equipment.setThresholdValue(data);
                record.setThresholdValue(data);
            } else {
                equipment.setCurrentValue(data);
                record.addValues(data);
            }
            mEquipmentList.add(equipment);
            mCurrentRecords.add(record);
            resultEquipment = equipment;
            resultRecord = record;
        }
        if (!type.equals("阈值")) {
            if (resultEquipment.getThresholdValue() != 0f && resultEquipment.getThresholdValue() < data) {
                if (resultEquipment.getThresholdValue() != 0f && resultEquipment.getThresholdValue() > lastValue) {
                    resultEquipment.addAlarmTimes();
                    resultRecord.addAlarmTimes();
                    return resultEquipment.getId();
                }
            }
        }
        return -1;
    }
}
