package com.example.cql.imzhihudaily.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.data.bean.HomeInfo;
import com.example.cql.imzhihudaily.data.bean.HomeStoriesInfo;
import com.example.cql.imzhihudaily.util.DateUtils;
import com.example.cql.imzhihudaily.util.RateTransformation;
import com.example.cql.imzhihudaily.widget.BannerView;

import java.util.ArrayList;

/**
 * Created by CQL on 2016/10/6.
 */

public class HomeRecycleViewAdapter extends RecyclerView.Adapter {
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_DATE = 2;
    private HomeInfo homeInfo;
    private Context mContext;

    private ArrayList<Integer> mDateViewPosition;
    private ArrayList<String> mDateViewContent;
    private onItemClickListener mOnItemClickListener;
    private ArrayList<String> mIds;

    public HomeRecycleViewAdapter(Context context) {
        mContext = context;
        mDateViewPosition = new ArrayList<>();
        mDateViewContent = new ArrayList<>();
        mIds = new ArrayList<>();
    }

    public void addData(HomeInfo info) {
        if (info == null || homeInfo == info) {
            return;
        }
        if (homeInfo == null) {//首次加载
            homeInfo = info;
            mDateViewPosition.add(1);
            for (int i = 0; i < info.getTopStories().size(); i++) {
                mIds.add(info.getTopStories().get(i).getId());
            }
        } else {
            int preHomeSize = homeInfo.getStories().size();
            homeInfo.getStories().addAll(info.getStories());
            mDateViewPosition.add(preHomeSize + mDateViewPosition.size() + 1);
        }
        for (int i = 0; i < info.getStories().size(); i++) {
            String id = info.getStories().get(i).getId();
            if (!mIds.contains(id)) {
                mIds.add(id);
            }
        }
        mDateViewContent.add(info.getDate());
    }

    public void clearData() {
        homeInfo = null;
        mDateViewContent.clear();
        mDateViewPosition.clear();
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (mDateViewPosition.contains(position) && position != 0) {
            return TYPE_DATE;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_card_item, parent, false);
            holder = new ItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.banner_item, parent, false);
            holder = new HomeTopViewHolder(view);
        } else if (viewType == TYPE_DATE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_date_item, parent, false);
            holder = new DateViewHolder(view);
        }
        return holder;
    }

    //caution:这里的position 是绝对位置
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();
        if (type == TYPE_HEADER) {
            HomeTopViewHolder homeTopViewHolder = (HomeTopViewHolder) holder;
        } else if (type == TYPE_ITEM) {
            HomeStoriesInfo info = homeInfo.getStories().get(position - getFrontDateViewCount(position) - 1);
            ItemViewHolder vh = (ItemViewHolder) holder;
            vh.tv_item_title.setText(info.getTitle());
            vh.id = info.getId();
            if (!info.getImages().isEmpty()) {
                vh.iv_item_pic.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(info.getImages().get(0)).transform(new RateTransformation(mContext.getApplicationContext())).into(vh.iv_item_pic);
            } else {
                vh.iv_item_pic.setVisibility(View.GONE);
            }
        } else if (type == TYPE_DATE) {
            DateViewHolder dateViewHolder = (DateViewHolder) holder;
            dateViewHolder.tv_date.setText(DateUtils.formatDate(mDateViewContent.get(getFrontDateViewCount(position))));
        }
    }

    private int getFrontDateViewCount(int position) {
        int count = 0;
        for (int i = 0; i < mDateViewPosition.size(); i++) {
            if (mDateViewPosition.get(i) < position) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public String findLatestDateViewString(int position) {
        String str = null;
        for (int i = 0; i < mDateViewPosition.size(); i++) {
            if (mDateViewPosition.get(i) <= position) {
                str = DateUtils.formatDate(mDateViewContent.get(i));//选择在此处format而不是在onScroll中
            } else {
                break;
            }
        }
//        System.out.println("dateViewPosition:"+mDateViewPosition);
        return str;
    }

    @Override
    public int getItemCount() {
        if (homeInfo == null) {
            return 0;
        } else {
            return homeInfo.getStories().size() + mDateViewPosition.size() + 1;
        }
    }

    //top viewHolder
    private class HomeTopViewHolder extends RecyclerView.ViewHolder implements BannerViewAdapter.OnItemClickListener {
        BannerView bannerView;
        BannerViewAdapter adapter;

        HomeTopViewHolder(View itemView) {
            super(itemView);
            bannerView = (BannerView) itemView.findViewById(R.id.banner);
            adapter = new BannerViewAdapter();
            adapter.setHomeTopInfos(homeInfo.getTopStories());
            bannerView.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
        }

        @Override
        public void onItemClick(String id) {
            if (mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(mIds,id);
            }
        }

    }

    private class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date;

        DateViewHolder(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
        }

        public String getText() {
            return tv_date.getText().toString();
        }
    }

    //item viewHolder
    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_item_pic;
        TextView tv_item_title;
        CardView cardView;
        private String id;

        ItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            iv_item_pic = (ImageView) itemView.findViewById(R.id.iv_item_pic);
            tv_item_title = (TextView) itemView.findViewById(R.id.tv_item_title);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mIds, id);
            }
        }

    }

    public interface onItemClickListener {
        void onItemClick(ArrayList<String> mIds, String currentId);
    }
}
