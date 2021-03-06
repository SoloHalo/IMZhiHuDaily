package com.example.cql.imzhihudaily.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cql.imzhihudaily.R;

/*
 * Created by liuhui on 2016/7/9.
 */
public class LoadMoreRecyclerView extends RecyclerView {

    public static final int TYPE_LOAD_MORE = -1;

    private boolean mIsLoading = false;
    private boolean mEnableLoadMore = true;
    private boolean mShowLoadingView = true;

    private LoadMoreAdapter mAdapter;
    private String mLoadMoreViewText = "正在加载";

    private boolean mShowProgressBar = true;

    private OnLoadMoreListener mOnLoadMoreListener;
    private OnLoadMoreViewClickListener mOnLoadMoreViewClickListener;
    private boolean mShouldChangeLoadViewState;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(new LoadMoreScrollListener());
    }

    @SuppressWarnings("unchecked")
    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        mAdapter = new LoadMoreAdapter(adapter);
        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mAdapter.notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                mAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mAdapter.notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                mAdapter.notifyItemMoved(fromPosition, toPosition);
            }
        });
        super.setAdapter(mAdapter);
    }

    public void setEnableLoadMore(boolean enable) {
        if (mEnableLoadMore == enable) {
            return;
        }
        mEnableLoadMore = enable;
        mShouldChangeLoadViewState = true;
    }

    public void setLoadView(String text, boolean showProgress) {
        mLoadMoreViewText = text;
        mShowProgressBar = showProgress;
        if (getLayoutManager().getChildCount() == 0 || !mShowLoadingView || mAdapter == null) {
            return;
        }
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    public void setShowLoadingView(boolean show) {
        if (mShowLoadingView == show) {
            return;
        }
        mShowLoadingView = show;
        if (getLayoutManager().getChildCount() == 0 || mAdapter == null) {
            return;
        }
        if (mShowLoadingView) {
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        } else {
            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public void setOnLoadMoreViewClickListener(OnLoadMoreViewClickListener listener) {
        mOnLoadMoreViewClickListener = listener;
        mShouldChangeLoadViewState = true;
    }

    public int getItemViewType(int position) {
        if (mAdapter == null) {
            return TYPE_LOAD_MORE;
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
    }

    private final class LoadMoreAdapter extends Adapter<ViewHolder> {

        private Adapter<ViewHolder> mInternalAdapter;

        LoadMoreAdapter(Adapter<ViewHolder> innerAdapter) {
            mInternalAdapter = innerAdapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_LOAD_MORE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_load_more_item, parent, false);
                return new LoadMoreViewHolder(view);
            } else {
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_LOAD_MORE) {
                LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
                loadMoreViewHolder.textView.setText(mLoadMoreViewText);
                loadMoreViewHolder.progressBar.setVisibility(mShowProgressBar ? VISIBLE : GONE);
                if (mShouldChangeLoadViewState) {
                    mShouldChangeLoadViewState = false;
                    if (mOnLoadMoreViewClickListener != null && mEnableLoadMore) {
                        loadMoreViewHolder.itemView.setClickable(true);
                    } else {
                        loadMoreViewHolder.itemView.setClickable(false);
                    }
                }
            } else {
                mInternalAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            if (mShowLoadingView) {
                return mInternalAdapter.getItemCount() + 1;
            } else {
                return mInternalAdapter.getItemCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mShowLoadingView && position == getItemCount() - 1) {
                return TYPE_LOAD_MORE;
            } else {
                return mInternalAdapter.getItemViewType(position);
            }
        }

        class LoadMoreViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

            ProgressBar progressBar;
            TextView textView;

            LoadMoreViewHolder(View itemView) {
                super(itemView);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
                textView = (TextView) itemView.findViewById(R.id.text);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mOnLoadMoreViewClickListener != null) {
                    mOnLoadMoreViewClickListener.onLoadMoreViewClick();
                }
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnLoadMoreViewClickListener {
        void onLoadMoreViewClick();
    }

    public class LoadMoreScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            boolean isBottom = false;
            if (layoutManager instanceof LinearLayoutManager) {
                int position = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                if (position == mAdapter.getItemCount() - 1) {
                    isBottom = true;
                }
            } else if (layoutManager instanceof GridLayoutManager) {
                int position = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                if (position == mAdapter.getItemCount() - 1) {
                    isBottom = true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] position = ((StaggeredGridLayoutManager) layoutManager).findLastCompletelyVisibleItemPositions(null);
                for (int aPosition : position) {
                    if (aPosition == mAdapter.getItemCount() - 1) {
                        isBottom = true;
                        break;
                    }
                }
            }
//            View view = layoutManager.getChildAt(layoutManager.getChildCount() - 1);
//            ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !mIsLoading && mOnLoadMoreListener != null &&
                    mEnableLoadMore && isBottom &&
                    mAdapter != null && mAdapter.getItemCount() > 0) {
                mIsLoading = true;
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }

}