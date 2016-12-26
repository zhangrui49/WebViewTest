package com.rftracking.web;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * DESC:
 * Created by zhangrui on 2016/12/1.
 */

public class VerticalRollingView extends View {

    private Paint mPaint;

    public VerticalRollingView(Context context) {
        this(context, null);
    }

    public VerticalRollingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.textViewStyle);
    }

    public VerticalRollingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint.FontMetricsInt metricsInt = mPaint.getFontMetricsInt();

    }




}
