package com.example.cql.imzhihudaily.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.data.bean.NewsDetailInfo;
import com.example.cql.imzhihudaily.util.RateTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CQL on 2017/3/5.
 */

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.LikeViewHolder> {

    private List<NewsDetailInfo> mNewsDetailInfos = new ArrayList<>();
    private onItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void addAll(List<NewsDetailInfo> infos){
        int start = mNewsDetailInfos.size();
        mNewsDetailInfos.addAll(infos);
        notifyItemRangeInserted(start,infos.size());
    }

    public void clear(){
        mNewsDetailInfos.clear();
        notifyDataSetChanged();
    }

    @Override
    public LikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_item,parent,false);
        return new LikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LikeViewHolder holder, int position) {
        NewsDetailInfo info = mNewsDetailInfos.get(position);
        holder.tv_item_title.setText(info.getTitle());
        holder.id = info.getId();
        if (!TextUtils.isEmpty(info.getImage())) {
            holder.iv_item_pic.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(info.getImage()).transform(new RateTransformation(holder.itemView.getContext().getApplicationContext())).into(holder.iv_item_pic);
        } else {
            holder.iv_item_pic.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mNewsDetailInfos.size();
    }

    class LikeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_item_pic;
        TextView tv_item_title;
        CardView cardView;
        private String id;

        LikeViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            iv_item_pic = (ImageView) itemView.findViewById(R.id.iv_item_pic);
            tv_item_title = (TextView) itemView.findViewById(R.id.tv_item_title);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick( id);
            }
        }
    }

    public interface onItemClickListener {
        void onItemClick(String id);
    }
}
