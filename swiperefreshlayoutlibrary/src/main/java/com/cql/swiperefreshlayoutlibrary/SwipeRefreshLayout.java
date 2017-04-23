package com.cql.swiperefreshlayoutlibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by CQL on 2016/9/19.
 */
public class SwipeRefreshLayout extends FrameLayout {

    private View mRefreshHead;
    private TextView mTvMsg;
    private ImageView mIvArrow;
    private ProgressBar mPb;
    private View mScrollableView;
    private TextView mTvFloatingMsg;
    private int mTvFloatingViewColor;

    private boolean isDragging;
    private int mMinTouchSlop;
    private int mLastTouchY;
    private int mCurrentOffset;
    private int mTriggerY;

    private boolean isSendCancelEvent;
    private boolean isSendDownEvent;

    private OnRefreshListener mOnRefreshListener;

    private ValueAnimator mValueAnimator;
    private AlphaAnimation mAlphaAnimation;

    private RefreshState mCurrentState;

    private enum RefreshState {
        IDLE,
        REFRESHING,
        PULL_TO_REFRESH,
        RELEASE_TO_REFRESH
    }

    //下面
    public SwipeRefreshLayout(Context context) {
        super(context);
        throw new RuntimeException("unSupport");
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        isDragging = false;
        isSendCancelEvent = false;
        isSendDownEvent = false;
        mCurrentState = RefreshState.IDLE;
        mMinTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwipeRefreshLayout);
        mTvFloatingViewColor = array.getColor(R.styleable.SwipeRefreshLayout_float_background, Color.parseColor("#f57700"));
        int resId = array.getResourceId(R.styleable.SwipeRefreshLayout_head_layout, R.layout.refresh_default_head_layout);
        array.recycle();

        mTvFloatingMsg = new TextView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTvFloatingMsg.setBackgroundColor(mTvFloatingViewColor);
        mTvFloatingMsg.setTextColor(Color.WHITE);
        mTvFloatingMsg.setGravity(Gravity.CENTER);
        final int padding = dp2px(context, 8);
        mTvFloatingMsg.setPadding(padding, padding, padding, padding);
        mTvFloatingMsg.setVisibility(GONE);

        mRefreshHead = LayoutInflater.from(context).inflate(resId, this, false);
        addView(mRefreshHead);
        addView(mTvFloatingMsg, 0, params);
    }

    public void setOnRefreshListener(OnRefreshListener l) {
        this.mOnRefreshListener = l;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScrollableView = findViewById(R.id.scrollable_layout);
        mIvArrow = (ImageView) findViewById(R.id.scrollable_layout_arrow);
        mTvMsg = (TextView) findViewById(R.id.scrollable_layout_msg);
        mPb = (ProgressBar) findViewById(R.id.scrollable_layout_progressbar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTriggerY = mRefreshHead.getMeasuredHeight();
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mRefreshHead.layout(l, t - mRefreshHead.getMeasuredHeight(), r, 0);
        mScrollableView.layout(l, t, r, mScrollableView.getMeasuredHeight());
        mTvFloatingMsg.layout(l, t, r, mTvFloatingMsg.getMeasuredHeight());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchY = (int) ev.getY();
                isDragging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) ev.getY();
                int diffY = currentY - mLastTouchY;
                if (!isDragging && Math.abs(diffY) > mMinTouchSlop) {
                    isDragging = true;
                }
                if (isDragging) {
                    mLastTouchY = (int) ev.getY();
                    if (diffY > 0 && canPullDown()) {
                        if (!isSendCancelEvent) {
                            sendCancelEvent(ev);
                        }
                        if (diffY + mCurrentOffset > 0 && mCurrentState == RefreshState.IDLE) {
                            switchCurrentState(RefreshState.PULL_TO_REFRESH);
                        } else if (diffY + mCurrentOffset > mTriggerY && mCurrentState == RefreshState.PULL_TO_REFRESH) {
                            switchCurrentState(RefreshState.RELEASE_TO_REFRESH);
                        }
                        offsetChildrenBy(diffY / 2);
                        return true;
                    } else if (diffY < 0 && mCurrentOffset > 0) {
                        if (mCurrentOffset + diffY < 0) {
                            diffY = -mCurrentOffset;
                        }
                        if (!isSendDownEvent) {
                            sendDownEvent(ev);
                        }
                        if (mCurrentOffset + diffY < mTriggerY && mCurrentState == RefreshState.RELEASE_TO_REFRESH) {
                            switchCurrentState(RefreshState.PULL_TO_REFRESH);
                        }
                        offsetChildrenBy(diffY);
                        return true;
                    }
                } else {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCurrentOffset > 0) {
                    if (mValueAnimator != null && mValueAnimator.isRunning()) {
                        mValueAnimator.cancel();
                    }
                    if (mCurrentOffset > mTriggerY && mCurrentState == RefreshState.RELEASE_TO_REFRESH) {
                        switchCurrentState(RefreshState.REFRESHING);
                        startScroll(mCurrentOffset, mTriggerY);
                    } else {
                        startScroll(mCurrentOffset, 0);
                    }
                }
                break;
        }
        System.out.println("parent:" + ev);
        return super.dispatchTouchEvent(ev);
    }

    private void sendCancelEvent(MotionEvent event) {
        System.out.println("sendCancelEvent");
        isSendCancelEvent = true;
        isSendDownEvent = false;
        MotionEvent cancelEvent = MotionEvent.obtain(event);
        cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
//        event.setAction(MotionEvent.ACTION_DOWN);
        super.dispatchTouchEvent(cancelEvent);
//        super.dispatchTouchEvent(event);
    }

    private void sendDownEvent(MotionEvent event) {
        System.out.println("sendDownEvent");
        isSendDownEvent = true;
        isSendCancelEvent = false;
        MotionEvent downEvent = MotionEvent.obtain(event);
        downEvent.setAction(MotionEvent.ACTION_DOWN);
//        event.setAction(MotionEvent.ACTION_CANCEL);
//        super.dispatchTouchEvent(event);
        super.dispatchTouchEvent(downEvent);
    }

    private void startScroll(int from, int to) {
        mValueAnimator = ValueAnimator.ofInt(from, to);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetChildrenTo((Integer) animation.getAnimatedValue());
            }
        });
        mValueAnimator.start();
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            switchCurrentState(RefreshState.REFRESHING);
        } else {
            switchCurrentState(RefreshState.IDLE);
        }
    }

    public void showFloatingMsg(String msg, long time) {
        mAlphaAnimation = new AlphaAnimation(0, 1);
        mAlphaAnimation.setDuration(500);
        mTvFloatingMsg.setVisibility(VISIBLE);
        mTvFloatingMsg.setText(msg);
        mTvFloatingMsg.startAnimation(mAlphaAnimation);
        bringChildToFront(mTvFloatingMsg);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mAlphaAnimation = new AlphaAnimation(1, 0);
                mAlphaAnimation.setDuration(500);
                mTvFloatingMsg.setVisibility(GONE);
                mTvFloatingMsg.startAnimation(mAlphaAnimation);
            }
        }, time);
    }

    private void switchCurrentState(RefreshState state) {
        switch (state) {
            case PULL_TO_REFRESH:
                mTvMsg.setText("下拉刷新");
                if (mCurrentState == RefreshState.RELEASE_TO_REFRESH) {
                    mIvArrow.animate().rotation(0).start();
                }
                break;
            case RELEASE_TO_REFRESH:
                mIvArrow.animate().rotation(-180).start();
                mTvMsg.setText("释放刷新");
                break;
            case REFRESHING:
                mPb.setVisibility(VISIBLE);
                mIvArrow.setVisibility(GONE);
                mTvMsg.setText("正在刷新");
                if (mCurrentState == RefreshState.IDLE) {
                    startScroll(0, mTriggerY);
                }
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
                break;
            case IDLE:
                mIvArrow.setVisibility(VISIBLE);
                isSendCancelEvent = false;
                isSendDownEvent = false;
                mPb.setVisibility(GONE);
                mTvMsg.setText("下拉刷新");
                mIvArrow.setRotation(0);
                if (mCurrentState == RefreshState.REFRESHING) {
                    startScroll(mTriggerY, 0);
                }
                break;
        }
        mCurrentState = state;
    }

    protected boolean canPullDown() {
        if (mScrollableView instanceof RecyclerView) {
            RecyclerView.LayoutManager layoutManager = ((RecyclerView) mScrollableView).getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() == 0;
            }
        }
        return mScrollableView.getScrollY() == 0;
    }

    private void offsetChildrenBy(int dy) {
        offsetChildrenTo(mCurrentOffset + dy);
    }

    private void offsetChildrenTo(int y) {
        mCurrentOffset = y;
        ViewCompat.setY(mRefreshHead, y - mRefreshHead.getHeight());
        ViewCompat.setY(mScrollableView, y);
    }

    public int dp2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
