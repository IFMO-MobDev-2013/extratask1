package com.example.extratask1;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: PWR
 * Date: 19.01.14
 * Time: 21:43
 * To change this template use File | Settings | File Templates.
 */
public class NewImageView extends ImageView{

    public NewImageView(Context context) {
        super(context);
    }

    public NewImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
