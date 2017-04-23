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
import com.example.cql.imzhihudaily.data.bean.ThemeEditorInfo;
import com.example.cql.imzhihudaily.data.bean.ThemeInfo;
import com.example.cql.imzhihudaily.data.bean.ThemeStoriesInfo;
import com.example.cql.imzhihudaily.util.CircleTransformation;
import com.example.cql.imzhihudaily.util.RateTransformation;

import java.util.List;

/**
 * Created by CQL on 2016/10/17.
 */

/**
 * 栏目适配器
 **/
public class PartitionRecycleViewAdapter extends RecyclerView.Adapter {
    private static final int TYPE_TOP = 0;
    private static final int TYPE_EDITOR = 1;
    private static final int TYPE_ITEM = 2;
    private static final int MAX_EDITOR_NUM = 6;

    private Context mContext;
    private ThemeInfo themeInfo;
    private OnItemClickListener mOnItemClickListener;

    public PartitionRecycleViewAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addData(ThemeInfo info) {
        if (themeInfo == null) {
            themeInfo = info;
        } else {
            themeInfo.getStories().addAll(info.getStories());
        }
    }

    public void clearData() {
        themeInfo = null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        } else if (position == 1) {
            return TYPE_EDITOR;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_TOP) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.theme_top_item, parent, false);
            holder = new TopViewHolder(view);
        } else if (viewType == TYPE_EDITOR) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.theme_editor_item, parent, false);
            holder = new EditorViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.theme_news_item, parent, false);
            holder = new ItemViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();
        if (type == TYPE_TOP) {
            TopViewHolder topViewHolder = (TopViewHolder) holder;
            Glide.with(mContext).load(themeInfo.getImage()).centerCrop().into(topViewHolder.iv_top);
            topViewHolder.tv_top.setText(themeInfo.getDescription());
        } else if (type == TYPE_EDITOR) {
            EditorViewHolder viewHolder = (EditorViewHolder) holder;
            List<ThemeEditorInfo> editorInfos = themeInfo.getEditors();
            int size = editorInfos.size();
            for (int i = 0; i < MAX_EDITOR_NUM; i++) {
                if (i < size) {
                    viewHolder.iv_editor[i].setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(editorInfos.get(i).getAvatar()).
                            transform(new CircleTransformation(mContext)).into(viewHolder.iv_editor[i]);
                } else {
                    viewHolder.iv_editor[i].setVisibility(View.GONE);
                }
            }
        }
        if (type == TYPE_ITEM) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            ThemeStoriesInfo info = themeInfo.getStories().get(position - 2);
            viewHolder.id = info.getId();
            if (info.getImages() != null && !info.getImages().isEmpty()) {
                viewHolder.iv_item.setVisibility(View.VISIBLE);
                Glide.with(mContext).
                        load(info.getImages()
                                .get(0))
                        .transform(new RateTransformation(mContext)).into(viewHolder.iv_item);
            } else {
                viewHolder.iv_item.setVisibility(View.GONE);
            }
            viewHolder.tv_item.setText(info.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        if (themeInfo == null) {
            return 0;
        } else {
            return themeInfo.getStories().size() + 2;
        }
    }

    private class TopViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_top;
        TextView tv_top;

        TopViewHolder(View itemView) {
            super(itemView);
            iv_top = (ImageView) itemView.findViewById(R.id.iv_theme_top);
            tv_top = (TextView) itemView.findViewById(R.id.tv_theme_top);
        }
    }

    private class EditorViewHolder extends RecyclerView.ViewHolder {
        TextView tv_theme_editor;
        ImageView[] iv_editor = new ImageView[MAX_EDITOR_NUM];

        EditorViewHolder(View itemView) {
            super(itemView);
            tv_theme_editor = (TextView) itemView.findViewById(R.id.tv_theme_editor);
            iv_editor[0] = (ImageView) itemView.findViewById(R.id.iv_editor1);
            iv_editor[1] = (ImageView) itemView.findViewById(R.id.iv_editor2);
            iv_editor[2] = (ImageView) itemView.findViewById(R.id.iv_editor3);
            iv_editor[3] = (ImageView) itemView.findViewById(R.id.iv_editor4);
            iv_editor[4] = (ImageView) itemView.findViewById(R.id.iv_editor5);
            iv_editor[5] = (ImageView) itemView.findViewById(R.id.iv_editor6);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView tv_item;
        ImageView iv_item;
        int id;

        ItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tv_item = (TextView) itemView.findViewById(R.id.tv_theme_item_title);
            iv_item = (ImageView) itemView.findViewById(R.id.iv_theme_item_pic);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(id);
            }
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }
}
