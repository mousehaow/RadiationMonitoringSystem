package com.imdetek.radiationmonitoringsystem.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imdetek.radiationmonitoringsystem.R;
import com.imdetek.radiationmonitoringsystem.Settings;
import com.imdetek.radiationmonitoringsystem.tools.ViewUtils;
import com.imdetek.radiationmonitoringsystem.widget.media.AndroidMediaController;
import com.imdetek.radiationmonitoringsystem.widget.media.MyVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends BaseActivity {

    public static final String TAG = "VideoActivity";

    private final int HIDE_INTERVAL = 7000;// 隐藏控制View时间间隔
    @BindView(R.id.orientation_changed)
    ImageView mOrientationChanged;


    private boolean first = true;

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.video_view)
    MyVideoView mVideoView;
    @BindView(R.id.video_toolbar)
    Toolbar mVideoToolbar;
    @BindView(R.id.player_bottom_layout)
    RelativeLayout mVideoBottom;
    @BindView(R.id.player_loading_layout)
    LinearLayout mLoadingView;
    @BindView(R.id.player_center_iv)
    ImageView mVideoCenter;
    @BindView(R.id.player_play_iv)
    ImageView mVideoPlayPause;
    @BindView(R.id.video_layout)
    RelativeLayout mVideoLayout;

    private AndroidMediaController mMediaController;

    private Settings mSettings;

    Handler mHandler = new Handler();

    public int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        id = getIntent().getIntExtra(TAG, 0);
        initVideoView();
    }

    @Override
    public void finish() {
        onVideoPause();
        super.finish();
    }

    private void initVideoView() {
        mSettings = new Settings(this);
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setVisibility(View.GONE);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView.setMediaController(mMediaController);
        mVideoView.requestFocus();
        mLoadingView.setVisibility(View.VISIBLE);
        Uri url = Uri.parse("rtmp://203.207.99.19:1935/live/CCTV5");
        //Uri url = Uri.parse("rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov");
        mVideoView.setVideoURI(url);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (mVideoBottom.getVisibility() == View.VISIBLE) {
                    mHandler.post(hiddenViewThread);
                } else {
                    showControllerBar();
                }
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new IjkMediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(IMediaPlayer player) {
                mLoadingView.setVisibility(View.GONE);
                if (first == true) {
                    mHandler.postDelayed(hiddenViewThread, HIDE_INTERVAL);
                    first = false;
                }
                mVideoView.start();
                onPlaySRCChange(true);
            }
        });
    }

    /**
     * 隐藏底部控制视图
     */
    private Runnable hiddenViewThread = new Runnable() {

        @Override
        public void run() {
            if (mVideoBottom.getVisibility() != View.GONE) {
                ViewUtils.translateAnimation(mVideoBottom, 0f, 0f, 0f, 1.0f, 400);
                mVideoBottom.setVisibility(View.GONE);
            }
        }
    };

    /**
     * 显示底部控制视图
     */
    private void showControllerBar() {
        mHandler.removeCallbacks(hiddenViewThread);
        mVideoBottom.setVisibility(View.VISIBLE);
        if (mVideoView.isPlaying()) {
            onPlaySRCChange(true);
        } else {
            onPlaySRCChange(false);
        }
        ViewUtils.translateAnimation(mVideoBottom,
                0f, 0f, 1.0f, 0f, 300);
        mHandler.postDelayed(hiddenViewThread, HIDE_INTERVAL);

    }

    /**
     * 根据播放状态改变 切换播放图标
     */
    private void onPlaySRCChange(boolean isPlaying) {
        if (isPlaying) {
            mVideoPlayPause.setImageResource(R.drawable.play_pause_icon);
        } else {
            mVideoPlayPause.setImageResource(R.drawable.play_start_icon);
        }
    }

    /**
     * 重新倒计时 隐藏底部控制视图
     */
    private void removeHideControllerBar() {
        mHandler.removeCallbacks(hiddenViewThread);
        mHandler.postDelayed(hiddenViewThread, HIDE_INTERVAL);
    }

    /**
     * 播放
     */
    private void onVideoPlay() {
        mVideoCenter.setVisibility(View.GONE);
        initVideoView();
    }

    /**
     * 点击播放按钮
     */
    private void onClickPlayButton() {
        removeHideControllerBar();
        if (mVideoView.isPlaying()) {
            onVideoPause();
        } else {
            onVideoPlay();
        }
    }

    /**
     * 暂停
     */
    private void onVideoPause() {
        mVideoView.stopPlayback();
        onPlaySRCChange(false);
        mVideoCenter.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mVideoToolbar.setVisibility(View.GONE);
            mOrientationChanged.setImageResource(R.drawable.collapse);
            RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) mOrientationChanged.getLayoutParams();
            mParams.rightMargin = dip2px(this, 25f);
            mOrientationChanged.setLayoutParams(mParams);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoLayout.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(params);
//            WindowManager.LayoutParams attrs = getWindow().getAttributes();
//            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            getWindow().setAttributes(attrs);
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            mVideoToolbar.setVisibility(View.VISIBLE);
            mOrientationChanged.setImageResource(R.drawable.expand);
            RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) mOrientationChanged.getLayoutParams();
            mParams.rightMargin = dip2px(this, 0f);
            mOrientationChanged.setLayoutParams(mParams);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoLayout.getLayoutParams();
            params.height = dip2px(this, 230f);
            mVideoLayout.setLayoutParams(params);
//            WindowManager.LayoutParams attrs = getWindow().getAttributes();
//            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().setAttributes(attrs);
//            getWindow().clearFlags(
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @OnClick({R.id.back_title_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_title_btn:
                finish();
                break;
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @OnClick(R.id.orientation_changed)
    public void onClick() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
