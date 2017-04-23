package com.example.cql.imzhihudaily.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.cql.imzhihudaily.R;

import java.lang.reflect.Field;

/*
 * Created by CQL on 2016/10/3.
 */

public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {
    ViewPager mViewPager;
    private Adapter adapter;
    private int halfSpace;
    private int mCurrentPosition;

    private boolean isTouch;
    private boolean isLoop;
    private long intervalTime;
    private ScrollRunnable scrollRunnable;//automatic
    private LinearLayout dotContainer;

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCurrentPosition = 0;
        intervalTime = 5000;
        halfSpace = getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
        mViewPager = new ViewPager(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(layoutParams);
        mViewPager.setPageMargin(2 * halfSpace);
        addView(mViewPager);
        scrollRunnable = new ScrollRunnable();
    }

    private void initDot(Context context) {
        dotContainer = new LinearLayout(context);
        dotContainer.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        layoutParams.bottomMargin = 3 * halfSpace;
        dotContainer.setLayoutParams(layoutParams);
        for (int i = 0; i < adapter.getBannerCount(); i++) {
            View view = new View(getContext());
            view.setBackgroundResource(R.drawable.banner_dot);
            view.setEnabled(false);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams((int) (halfSpace * 1.5), (int) (halfSpace * 1.5));
            dotParams.leftMargin = halfSpace / 2;
            dotParams.rightMargin = halfSpace / 2;
            view.setLayoutParams(dotParams);
            dotContainer.addView(view, i);
        }
        dotContainer.getChildAt(0).setEnabled(true);
        addView(dotContainer);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                if (isLoop) {
                    stopLoop();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouch = false;
                if (!isLoop) {
                    startLoop();
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    private int lastX;
    private int lastY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            lastX = (int) ev.getX();
            lastY = (int) ev.getY();
        }else if (ev.getAction() == MotionEvent.ACTION_MOVE){
            int currentX = (int) ev.getX();
            int currentY = (int) ev.getY();
            if (Math.abs(currentX - lastX)> Math.abs(currentY - lastY)){
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            lastY = currentY;
            lastX = currentX;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void startLoop() {
        if (!isLoop) {
            isLoop = true;
            postDelayed(scrollRunnable, intervalTime);
        }
        postInvalidate();
    }

    private void stopLoop() {
        isLoop = false;
        removeCallbacks(scrollRunnable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoop();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoop();
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(1, false);
        mViewPager.addOnPageChangeListener(this);
        initDot(getContext());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        if (position == 0) {
            selectDot(dotContainer.getChildCount() - 1);
        } else if (position == adapter.getCount()) {
            selectDot(0);
        } else {
            selectDot(mCurrentPosition - 1);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (mCurrentPosition == 0) {
                mViewPager.setCurrentItem(adapter.getCount() - 2, false);
            } else if (mCurrentPosition == adapter.getCount() - 1) {
                mViewPager.setCurrentItem(1, false);
            }
        }
    }

    private void selectDot(int index) {
        for (int i = 0; i < dotContainer.getChildCount(); i++) {
            if (i == index) {
                dotContainer.getChildAt(i).setEnabled(true);
            } else {
                dotContainer.getChildAt(i).setEnabled(false);
            }
        }
    }


    public static abstract class Adapter extends PagerAdapter {

        public abstract int getBannerCount();

        public abstract Object getItemView(ViewGroup container, int position);

        @Override
        public int getCount() {
            if (getBannerCount() == 0) {
                return 0;
            } else {
                return getBannerCount() + 2;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int realPosition = getRealPosition(position);
            return getItemView(container, realPosition);
        }

        private int getRealPosition(int position) {
            int realPosition;
            if (getCount() == 0) {
                realPosition = 0;
            } else {
                if (position == 0) {
                    realPosition = getBannerCount() - 1;
                } else if (position == getCount() - 1) {
                    realPosition = 0;
                } else {
                    realPosition = position - 1;
                }
            }
            return realPosition;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
                System.out.println(container.getChildCount());
            }
        }
    }

    private class ScrollRunnable implements Runnable {
        @Override
        public void run() {
            if (!isTouch) {
                mCurrentPosition++;
                try {
                    Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
                    mFirstLayout.setAccessible(true);
                    mFirstLayout.set(mViewPager, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mViewPager.setCurrentItem(mCurrentPosition, true);
                postDelayed(this, intervalTime);
            }
        }
    }
}