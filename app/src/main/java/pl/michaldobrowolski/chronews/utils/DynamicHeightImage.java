package pl.michaldobrowolski.chronews.utils;

import android.content.Context;

public class DynamicHeightImage extends android.support.v7.widget.AppCompatImageView {

    private float mAspectRatio = 1.5f;

    public DynamicHeightImage(Context context) {
        super(context);
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int) (measuredWidth / mAspectRatio));
    }
}
