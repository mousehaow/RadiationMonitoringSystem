package com.imdetek.radiationmonitoringsystem.services;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.imdetek.radiationmonitoringsystem.MyApplication;
import com.imdetek.radiationmonitoringsystem.R;

import java.util.HashMap;

/**
 * Created by toby on 2016/12/19.
 */
public class MySoundPool {

    private SoundPool soundPool;

    private HashMap soundPoolMap;

    private boolean isPalying = false;

    private static MySoundPool instance = null;

    private int id = 0;

    public static synchronized MySoundPool getInstance() {
        if (instance == null) {
            instance = new MySoundPool();
        }
        return instance;
    }

    private MySoundPool() {
        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.i("Sound", "load Success");
            }
        });
    }

    public void putSound(Context context) {
        soundPoolMap.put(1, soundPool.load(context, R.raw.siren_sound, 1));
    }

    public void playSound() {
        id = soundPool.play(1, 1, 1, 0, -1, 1);
        Log.i("tagSound", "" + id);
        isPalying = true;
    }

    public void stopPlay() {
        soundPool.pause(id);
        isPalying = false;
    }

    public boolean isPalying() {
        return isPalying;
    }
}
