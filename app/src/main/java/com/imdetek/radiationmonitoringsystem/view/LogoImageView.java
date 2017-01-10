package com.imdetek.radiationmonitoringsystem.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by toby on 2016/12/28.
 */

public class LogoImageView extends ImageView {

    public interface LogoImageViewInterface {
        void heightSetSuccess(int width, int height);
    }

    private LogoImageViewInterface viewInterface;

    public LogoImageView(Context context) {
        super(context);
    }

    public LogoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LogoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setViewInterface(LogoImageViewInterface viewInterface) {
        this.viewInterface = viewInterface;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
        if (viewInterface != null) {
            viewInterface.heightSetSuccess(width, height);
            viewInterface = null;
        }
    }
}
