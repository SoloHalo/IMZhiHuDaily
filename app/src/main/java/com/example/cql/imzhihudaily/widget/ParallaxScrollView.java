package com.example.cql.imzhihudaily.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.example.cql.imzhihudaily.R;

/**
 * Created by CQL on 2016/10/12.
 *视差效果
 */

public class ParallaxScrollView extends NestedScrollView {

    private View headView;
    private OnScrollChangeListener mOnScrollChangeListener;

    public ParallaxScrollView(Context context) {
        super(context);
    }

    public ParallaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallaxScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        mOnScrollChangeListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headView = findViewById(R.id.head_view);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (t < headView.getMeasuredHeight()+getPaddingTop()) {
            headView.setTop(t / 2);
        }
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

}
