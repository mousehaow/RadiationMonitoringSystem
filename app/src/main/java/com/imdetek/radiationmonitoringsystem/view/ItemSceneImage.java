package com.imdetek.radiationmonitoringsystem.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;

import java.util.List;

/**
 * Created by toby on 2016/12/27.
 */

public class ItemSceneImage extends ImageView {

    private static final String TAG = "ItemSceneImage";

    private Bitmap mBitmap;

    private Bitmap warnBitmap;

    public List<Equipment> mData;
    private Paint paint;

    private int loadCont = 0;

    public ItemSceneImage(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBitmap = small(BitmapFactory.decodeResource(getResources(), R.drawable.equip));
        warnBitmap = small(BitmapFactory.decodeResource(getResources(), R.drawable.siren));
        paint = new Paint();
    }

    public ItemSceneImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemSceneImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemSceneImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }

    public void setData(List<Equipment> data) {
        mData = data;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mData != null) {
            for (Equipment equipment : mData) {
                if (equipment.getLocalX() != 0f && equipment.getLocalY() != 0f) {
                    if (equipment.getThresholdValue() > equipment.getCurrentValue()) {
                        canvas.drawBitmap(mBitmap, this.getWidth() * equipment.getLocalX() - mBitmap.getWidth() / 2,
                                this.getHeight() * equipment.getLocalY() - mBitmap.getHeight() / 2, paint);
                    } else {
                        canvas.drawBitmap(warnBitmap, this.getWidth() * equipment.getLocalX() - warnBitmap.getWidth() / 2,
                                this.getHeight() * equipment.getLocalY() - warnBitmap.getHeight() / 2, paint);
                    }
                }
            }
        }
    }

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.6f,0.6f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
}
