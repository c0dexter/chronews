package pl.michaldobrowolski.chronews.utils;

import android.content.Context;
import android.util.AttributeSet;

public class DynamicHeightImage extends android.support.v7.widget.AppCompatImageView {

    private static float mAspectRatio = 1.5f; // for the screen ratio 3:2 = 1.5f

    public DynamicHeightImage(Context context) {
        super(context);
    }

    public DynamicHeightImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setUserAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    public void setRatioThreeTwo() {
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int) (measuredWidth / mAspectRatio));
    }
}
