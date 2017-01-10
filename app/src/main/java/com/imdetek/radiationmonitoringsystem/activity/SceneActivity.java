package com.imdetek.radiationmonitoringsystem.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.adapter.MainListAdapter;
import com.imdetek.radiationmonitoringsystem.adapter.SceneListAdapter;
import com.imdetek.radiationmonitoringsystem.entity.DataManager;
import com.imdetek.radiationmonitoringsystem.entity.Equipment;
import com.imdetek.radiationmonitoringsystem.entity.Record;
import com.imdetek.radiationmonitoringsystem.services.MySoundPool;
import com.imdetek.radiationmonitoringsystem.view.DefultItemDecoration;
import com.imdetek.radiationmonitoringsystem.view.SceneImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SceneActivity extends BaseActivity {

    public static final String TAG = "SceneActivity";

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.scene_image_view)
    SceneImageView mSceneImageView;
    @BindView(R.id.scene_recycler_view)
    RecyclerView mSceneRecyclerView;
    @BindView(R.id.activity_scene)
    RelativeLayout activityScene;
    @BindView(R.id.scene_layout)
    RelativeLayout sceneLayout;

    private SceneListAdapter mAdapter;

    private List<Equipment> mEquipments = new ArrayList<>();

    private String scene = null;

    private Map<String, ImageView> mEquipmentImages = new HashMap<>();

    List<Record> records = new ArrayList<>();

    private float minScale = 0f;

    private float currentWidth = 0f;

    private float currentOfftrackX = 0f;

    private float currentOfftrackY = 0f;

    private float minWidth = 0f;

    private float scaleWH = 0f;

    private boolean firstLoad = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        ButterKnife.bind(this);
        scene = getIntent().getStringExtra(TAG);
        mTitle.setText(scene);

        mEquipments = DataManager.getInstance().getEquipmentList();
        int ccc = 0;
        for (Equipment equipment : mEquipments) {
            if (equipment.getScene().equals(scene)) {
                ccc++;
                int num = (int) (Math.random() * 100);
                if (ccc == 1) {
                    equipment.setOnLine(true);
                    float x = 0.5f;
                    float y = 0.5f;
                    equipment.setLocalX(x);
                    equipment.setLocalY(y);
                }
                if (ccc == 2) {
                    equipment.setOnLine(true);
                    float x = 0.75f;
                    float y = 0.5f;
                    equipment.setLocalX(x);
                    equipment.setLocalY(y);
                }
                if (ccc == 3) {
                    equipment.setOnLine(true);
                    float x = 0.25f;
                    float y = 0.75f;
                    equipment.setLocalX(x);
                    equipment.setLocalY(y);
                }
            }
        }
        initListView();
        mSceneImageView.setOnTouchListener(new TouchListener());

        mSceneImageView.setEquipmentManger(new SceneImageView.EquipmentManger() {
            @Override
            public void seeEquipmentDetilInfo(Equipment equipment) {

            }

            @Override
            public void widthSetSuccess(int width) {
                initSceneImage(width);
            }

            @Override
            public void reDrawImage() {
                refreshSceneImage();
            }
        });

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
                mEquipments = DataManager.getInstance().getEquipmentList();
                mAdapter.setData(mEquipments);
                mAdapter.notifyDataSetChanged();
                refreshSceneImage();
            }
        });
    }

    private void initSceneImage(int widthImageView) {
        Matrix matrix = new Matrix();
        matrix.set(mSceneImageView.getImageMatrix());
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.scene1);
        int height = bitmap.getHeight();
        int width= bitmap.getWidth();
        scaleWH = (float) width / (float)height;
        minScale = (float) widthImageView / (float) width;
        currentWidth = widthImageView;
        minWidth = widthImageView;
        matrix.setScale(minScale, minScale, 0f, 0f);
        mSceneImageView.setImageMatrix(matrix);
    }

    private void refreshSceneImage() {
        for (Equipment equipment : mEquipments) {
            if (equipment.isOnLine()) {
                float outZoomX = (currentWidth - minWidth) / 2;
                float outZoomY = outZoomX / scaleWH;
                float localX = equipment.getLocalX() * currentWidth;
                float localY = equipment.getLocalY() * currentWidth / scaleWH;
                if (localX >= (outZoomX - currentOfftrackX + 15) && localX <= (currentWidth - (outZoomX + currentOfftrackX)) - 15) {
                    if (localY >= (outZoomY - currentOfftrackY + 15) && localY <= (currentWidth / scaleWH - (outZoomY + currentOfftrackY)) - 15) {
                        if (equipment.getCurrentValue() < equipment.getThresholdValue()) {
                            if (mEquipmentImages.containsKey(String.valueOf(equipment.getId()))) {
                                ImageView commonImage = mEquipmentImages.get(String.valueOf(equipment.getId()));
                                commonImage.setImageResource(R.drawable.equip);
                                commonImage.setVisibility(View.VISIBLE);
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)commonImage.getLayoutParams();
                                layoutParams.topMargin = (int) (localY - outZoomY + currentOfftrackY - 15);
                                layoutParams.leftMargin = (int) (localX - outZoomX + currentOfftrackX - 15);
                                commonImage.setLayoutParams(layoutParams);
                                commonImage.clearAnimation();
                            } else {
                                ImageView commonImage = new ImageView(this);
                                commonImage.setImageResource(R.drawable.equip);
                                commonImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30);
                                layoutParams.topMargin = (int) (localY - outZoomY + currentOfftrackY - 15);
                                layoutParams.leftMargin = (int) (localX - outZoomX + currentOfftrackX - 15);
                                sceneLayout.addView(commonImage, sceneLayout.getChildCount(), layoutParams);
                                mEquipmentImages.put(String.valueOf(equipment.getId()), commonImage);
                            }
                        } else {
                            if (mEquipmentImages.containsKey(String.valueOf(equipment.getId()))) {
                                ImageView sirenImage = mEquipmentImages.get(String.valueOf(equipment.getId()));
                                sirenImage.clearAnimation();
                                sirenImage.setImageResource(R.drawable.siren);
                                sirenImage.setVisibility(View.VISIBLE);
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)sirenImage.getLayoutParams();
                                layoutParams.topMargin = (int) (localY - outZoomY + currentOfftrackY - 15);
                                layoutParams.leftMargin = (int) (localX - outZoomX + currentOfftrackX - 15);
                                sirenImage.setLayoutParams(layoutParams);
                                AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
                                alphaAnimation1.setDuration(250);
                                alphaAnimation1.setRepeatCount(Animation.INFINITE);
                                alphaAnimation1.setRepeatMode(Animation.REVERSE);
                                sirenImage.setAnimation(alphaAnimation1);
                                alphaAnimation1.start();
                            } else {
                                ImageView sirenImage = new ImageView(this);
                                sirenImage.setImageResource(R.drawable.siren);
                                sirenImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30);
                                layoutParams.topMargin = (int) (localY - outZoomY + currentOfftrackY - 15);
                                layoutParams.leftMargin = (int) (localX - outZoomX + currentOfftrackX - 15);
                                sceneLayout.addView(sirenImage, sceneLayout.getChildCount(), layoutParams);
                                AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
                                alphaAnimation1.setDuration(250);
                                alphaAnimation1.setRepeatCount(Animation.INFINITE);
                                alphaAnimation1.setRepeatMode(Animation.REVERSE);
                                sirenImage.setAnimation(alphaAnimation1);
                                alphaAnimation1.start();
                                mEquipmentImages.put(String.valueOf(equipment.getId()), sirenImage);
                            }
                        }
                    } else {
                        if (mEquipmentImages.containsKey(String.valueOf(equipment.getId()))) {
                            ImageView commonImage = mEquipmentImages.get(String.valueOf(equipment.getId()));
                            commonImage.setImageResource(R.drawable.equip);
                            commonImage.setVisibility(View.GONE);
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)commonImage.getLayoutParams();
                            layoutParams.topMargin = (int) (localY - outZoomY + currentOfftrackY - 15);
                            layoutParams.leftMargin = (int) (localX - outZoomX + currentOfftrackX - 15);
                            commonImage.setLayoutParams(layoutParams);
                            commonImage.clearAnimation();
                        } else {
                            ImageView commonImage = new ImageView(this);
                            commonImage.setImageResource(R.drawable.equip);
                            commonImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            commonImage.setVisibility(View.GONE);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30);
                            layoutParams.topMargin = (int) (localY - outZoomY + currentOfftrackY - 15);
                            layoutParams.leftMargin = (int) (localX - outZoomX + currentOfftrackX - 15);
                            sceneLayout.addView(commonImage, sceneLayout.getChildCount(), layoutParams);
                            mEquipmentImages.put(String.valueOf(equipment.getId()), commonImage);
                        }
                    }
                } else {
                    if (mEquipmentImages.containsKey(String.valueOf(equipment.getId()))) {
                        ImageView commonImage = mEquipmentImages.get(String.valueOf(equipment.getId()));
                        commonImage.setImageResource(R.drawable.equip);
                        commonImage.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)commonImage.getLayoutParams();
                        layoutParams.topMargin = (int) (localY - outZoomY + currentOfftrackY - 15);
                        layoutParams.leftMargin = (int) (localX - outZoomX + currentOfftrackX - 15);
                        commonImage.setLayoutParams(layoutParams);
                        commonImage.clearAnimation();
                    } else {
                        ImageView commonImage = new ImageView(this);
                        commonImage.setImageResource(R.drawable.equip);
                        commonImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        commonImage.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30);
                        layoutParams.topMargin = (int) (localY - outZoomY + currentOfftrackY - 15);
                        layoutParams.leftMargin = (int) (localX - outZoomX + currentOfftrackX - 15);
                        sceneLayout.addView(commonImage, sceneLayout.getChildCount(), layoutParams);
                        mEquipmentImages.put(String.valueOf(equipment.getId()), commonImage);
                    }
                }
            }
        }
    }


    private void initListView() {
        mAdapter = new SceneListAdapter(this, mEquipments);
        mAdapter.setListener(new MainListAdapter.OnItemTouchedListener() {
            @Override
            public void onItemClicked(int id) {
                Intent intent = new Intent(SceneActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.TAG, id);
                startActivity(intent);
            }

            @Override
            public void onItemSceneBtnClicked(int id) {
                Intent intent = new Intent(SceneActivity.this, VideoActivity.class);
                intent.putExtra(VideoActivity.TAG, id);
                startActivity(intent);
            }
        });
        mSceneRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSceneRecyclerView.setAdapter(mAdapter);
    }

    @OnClick({R.id.back_title_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_title_btn:
                finish();
                break;
        }
    }

    private final class TouchListener implements View.OnTouchListener {

        /** 记录是拖拉照片模式还是放大缩小照片模式 */
        private int mode = 0;// 初始状态
        /** 拖拉照片模式 */
        private static final int MODE_DRAG = 1;
        /** 放大缩小照片模式 */
        private static final int MODE_ZOOM = 2;
        /** 用于记录开始时候的坐标位置 */
        private PointF startPoint = new PointF();
        /** 用于记录拖拉图片移动的坐标位置 */
        private Matrix matrix = new Matrix();
        /** 用于记录图片要进行拖拉时候的坐标位置 */
        private Matrix currentMatrix = new Matrix();

        /** 两个手指的开始距离 */
        private float startDis;
        /** 两个手指的中间点 */
        private PointF midPoint;

        private float startWidth = 0f;

        private float offtrackX = 0f;

        private float offtrackY = 0f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 手指压下屏幕
                case MotionEvent.ACTION_DOWN:
                    mode = MODE_DRAG;
                    // 记录ImageView当前的移动位置
                    currentMatrix.set(mSceneImageView.getImageMatrix());
                    matrix.set(currentMatrix);
                    startPoint.set(event.getX(), event.getY());
                    offtrackX = currentOfftrackX;
                    offtrackY = currentOfftrackY;
                    break;
                // 手指在屏幕上移动，改事件会被不断触发
                case MotionEvent.ACTION_MOVE:
                    // 拖拉图片
                    if (mode == MODE_DRAG) {
                        float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                        float dy = event.getY() - startPoint.y; // 得到y轴的移动距离
                        if (currentWidth > minWidth) {
                            float zoomX = (currentWidth - minWidth) / 2;
                            float zoomY = zoomX / scaleWH;
                            float compareX = offtrackX + dx;
                            float compareY = offtrackY + dy;
                            if ((-zoomX) <= compareX && compareX <= zoomX && (-zoomY) <= compareY && compareY <= zoomY) {
                                //Log.i("MOVE_XYXY", "XYXYXYXYXYXYXY");
                                currentOfftrackX = compareX;
                                currentOfftrackY = compareY;
                                matrix.set(currentMatrix);
                                matrix.postTranslate(dx, dy);
                            } else if ((-zoomY) <= compareY && compareY <= zoomY) {
                                currentOfftrackY = compareY;
                                matrix.set(currentMatrix);
                                if ((-zoomX) >= compareX) {
                                    currentOfftrackX = -zoomX;
                                    matrix.postTranslate(-(zoomX + offtrackX), dy);
                                } else if (compareX >= zoomX) {
                                    currentOfftrackX = zoomX;
                                    matrix.postTranslate(zoomX - offtrackX, dy);
                                }
                            } else if ((-zoomX) <= compareX && compareX <= zoomX) {
                                currentOfftrackX = compareX;
                                matrix.set(currentMatrix);
                                if (-(zoomY) >= compareY) {
                                    currentOfftrackY = -zoomY;
                                    matrix.postTranslate(dx, -(zoomY + offtrackY));
                                } else if (compareY >= zoomY){
                                    currentOfftrackY = zoomY;
                                    matrix.postTranslate(dx, zoomY - offtrackY);
                                }
                            } else {
                                matrix.set(currentMatrix);
                                if ((-zoomX) >= compareX) {
                                    currentOfftrackX = -zoomX;
                                    if (-(zoomY) >= compareY) {
                                        currentOfftrackY = -zoomY;
                                        matrix.postTranslate(-(zoomX + offtrackX), -(zoomY + offtrackY));
                                    } else {
                                        currentOfftrackY = zoomY;
                                        matrix.postTranslate(-(zoomX + offtrackX), zoomY - offtrackY);
                                    }
                                } else {
                                    currentOfftrackX = zoomX;
                                    if (-(zoomY) >= compareY) {
                                        currentOfftrackY = -zoomY;
                                        matrix.postTranslate(zoomX - offtrackX, -(zoomY + offtrackY));
                                    } else {
                                        currentOfftrackY = zoomY;
                                        matrix.postTranslate(zoomX - offtrackX, zoomY - offtrackY);
                                    }
                                }
                            }
                            break;
                        } else {
                            matrix.set(currentMatrix);
                            matrix.setScale(minScale, minScale, 0f, 0f);
                            currentWidth = minWidth;
                            currentOfftrackX = 0f;
                            currentOfftrackY = 0f;
                            return true;
                        }
                    }
                    // 放大缩小图片
                    else if (mode == MODE_ZOOM) {
                        float endDis = distance(event);// 结束距离
                        if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                            float scale = endDis / startDis;// 得到缩放倍数
                            if (currentWidth > minWidth) {
                                matrix.set(currentMatrix);
                                matrix.postScale(scale, scale, minWidth / 2 + offtrackX, minWidth / 2 / scaleWH + offtrackY);
                                currentWidth = startWidth * scale;
                            } else if (scale > 1){
                                matrix.set(currentMatrix);
                                matrix.postScale(scale, scale, minWidth / 2 + offtrackX, minWidth / 2 / scaleWH + offtrackY);
                                currentWidth = startWidth * scale;
                            } else {
                                matrix.set(currentMatrix);
                                matrix.setScale(minScale, minScale, 0f, 0f);
                                currentWidth = minWidth;
                                currentOfftrackX = 0f;
                                currentOfftrackY = 0f;
                            }
                        }
                    }
                    break;
                // 手指离开屏幕
                case MotionEvent.ACTION_UP:
                    // 当触点离开屏幕，但是屏幕上还有触点(手指)
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
                // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE_ZOOM;
                    /** 计算两个手指间的距离 */
                    startDis = distance(event);
                    /** 计算两个手指间的中间点 */
                    if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        midPoint = mid(event);
                        //记录当前ImageView的缩放倍数
                        currentMatrix.set(mSceneImageView.getImageMatrix());
                    }
                    startWidth = currentWidth;
                    break;
            }
            mSceneImageView.setImageMatrix(matrix);
            return true;
        }

        /** 计算两个手指间的距离 */
        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            /** 使用勾股定理返回两点之间的距离 */
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        /** 计算两个手指间的中间点 */
        private PointF mid(MotionEvent event) {
            float midX = (event.getX(1) + event.getX(0)) / 2;
            float midY = (event.getY(1) + event.getY(0)) / 2;
            return new PointF(midX, midY);
        }
    }
}
