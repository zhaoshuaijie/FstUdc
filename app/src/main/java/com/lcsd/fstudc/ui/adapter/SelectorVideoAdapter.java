package com.lcsd.fstudc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.moudle.VideoInfo;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * Created by Administrator on 2017/5/24 0024.
 */

public class SelectorVideoAdapter extends BaseAdapterRV<VideoInfo> {
    private Context mContext;
    private int mSelectorPosition = -1;
    //设置默认能选视频的最大值和最小值，单位毫秒
    private long mMinDuration = 0, mMaxDuration = Long.MAX_VALUE;

    @Override
    public RecyclerView.ViewHolder createVHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new SelectorVideoHolder(LayoutInflater.
                from(parent.getContext()).inflate(R.layout.item_selector_video, parent, false));
    }

    @Override
    protected void onBindVH(RecyclerView.ViewHolder viewHolder, int position, VideoInfo videoInfo) {

        SelectorVideoHolder holder = (SelectorVideoHolder) viewHolder;

        String path = videoInfo.getPath();
        //使用path操作获取图片 很费时！要使用glide 或者在子线程中获取
        if (!TextUtils.isEmpty(path)) {
            Glide.with(mContext).load(path).asBitmap().into(holder.mImageView);
        }
        if (videoInfo.getDuration() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            holder.mTvTime.setText(formatter.format(videoInfo.getDuration()));
        }
        if (position == mSelectorPosition) {
            holder.mSelectorView.setBackground(mContext.getResources().
                    getDrawable(R.drawable.img_select_yes));
        } else if (position != mSelectorPosition && videoInfo.getDuration() <= mMaxDuration && videoInfo.getDuration() >= mMinDuration) {
            holder.mSelectorView.setBackground(mContext.getResources()
                    .getDrawable(R.drawable.img_select_defult));
        } else if (position != mSelectorPosition && videoInfo.getDuration() >= mMaxDuration || videoInfo.getDuration() <= mMinDuration) {
            holder.mSelectorView.setBackground(mContext.getResources()
                    .getDrawable(R.drawable.img_select_no));
        }

    }

    /**
     * 设置选中哪一个
     *
     * @param position
     */
    public void setSelectorPosition(int position) {
        if (mSelectorPosition == position) {
            mSelectorPosition = -1;
        } else {
            mSelectorPosition = position;
        }

        notifyDataSetChanged();
    }

    public void setmMinDuration(long mMinDuration) {
        this.mMinDuration = mMinDuration;
    }

    public void setmMaxDuration(long mMaxDuration) {
        this.mMaxDuration = mMaxDuration;
    }

    /**
     * 获取被选中的position
     *
     * @return
     */
    public int getSelectorPosition() {
        return mSelectorPosition;
    }


    class SelectorVideoHolder extends Holder {
        private final ImageView mImageView;
        private final View mSelectorView;
        private final TextView mTvTime;

        public SelectorVideoHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_item_video);
            mSelectorView = itemView.findViewById(R.id.view_selector);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
