package com.lcsd.fstudc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2018/4/20.
 */
public class ScollGridview extends GridView {
    public ScollGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScollGridview(Context context) {
        super(context);
    }

    public ScollGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
