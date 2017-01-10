package com.imdetek.radiationmonitoringsystem.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;

import java.util.List;

/**
 * @name RadiationMonitoringSystem
 * @class name：com.imdetek.radiationmonitoringsystem
 * @class describe
 * @anthor toby
 * @time 2016/11/23 下午2:34
 * @change
 * @chang time
 * @class describe
 */
public class SceneImageView extends ImageView {
    public interface EquipmentManger {
        void seeEquipmentDetilInfo(Equipment equipment);
        void widthSetSuccess(int width);
        void reDrawImage();
    }

    private EquipmentManger mEquipmentManger;

    private static final String TAG = "SceneImageView";

    private Bitmap mBitmap;

    public List<Equipment> mData;
    private Paint paint;

    private int loadCont = 0;


    public SceneImageView(Context context) {
        super(context);
        init();
    }

    public SceneImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SceneImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SceneImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
        if (loadCont == 0 && width > 0) {
            loadCont++;
            this.mEquipmentManger.widthSetSuccess(width);
        }
    }

    public void setEquipmentManger(EquipmentManger equipmentManger) {
        mEquipmentManger = equipmentManger;
    }

    public void setData(List<Equipment> data) {
        mData = data;
        invalidate();
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.equipment);
        paint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.mEquipmentManger.reDrawImage();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (mData != null) {
//            for (Equipment equipment : mData) {
//                if (equipment.getLocalX() >= event.getX() - mBitmap.getWidth() / 2 && equipment.getLocalX() <= event.getX() + mBitmap.getWidth() / 2) {
//                    if (equipment.getLocalY() >= event.getY() - mBitmap.getHeight() / 2 && equipment.getLocalY() <= event.getY() + mBitmap.getHeight() / 2) {
//                        //单击处理,跳转设备详细页面
//                        mEquipmentManger.seeEquipmentDetilInfo(equipment);
//                        Log.e(TAG, "单击了设备" + String.valueOf(equipment.getId()));
//                    }
//                }
//            }
//        }
//        return super.onTouchEvent(event);
//    }
}
