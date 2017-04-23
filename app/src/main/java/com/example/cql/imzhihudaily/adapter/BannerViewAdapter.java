package com.example.cql.imzhihudaily.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.data.bean.HomeTopInfo;
import com.example.cql.imzhihudaily.widget.BannerView;

import java.util.List;

/**
 * Created by CQL on 2016/10/6.
 */

public class BannerViewAdapter extends BannerView.Adapter  {
    private List<HomeTopInfo> homeTopInfos;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getBannerCount() {
        return homeTopInfos.size();
    }

    public void setHomeTopInfos(List<HomeTopInfo> infos){
        homeTopInfos = infos;
    }

    @Override
    public Object getItemView(final ViewGroup container, final int position) {
        View bannerView = LayoutInflater.from(container.getContext()).inflate(R.layout.img_banner_inside_item,container,false);

        ImageView imageView = (ImageView) bannerView.findViewById(R.id.iv_banner);
        Glide.with(container.getContext()).load(homeTopInfos.get(position).getImage()).centerCrop().into(imageView);

        TextView textView = (TextView) bannerView.findViewById(R.id.tv_banner);
        textView.setText(homeTopInfos.get(position).getTitle());
        bannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(homeTopInfos.get(position).getId());
                }
            }
        });
        container.addView(bannerView);
        return bannerView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public interface OnItemClickListener{
        void onItemClick(String id);
    }
}
