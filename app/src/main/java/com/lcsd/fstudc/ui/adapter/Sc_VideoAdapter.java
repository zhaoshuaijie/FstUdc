package com.lcsd.fstudc.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.moudle.SckInfo;
import com.lcsd.fstudc.utils.L;
import com.lcsd.fstudc.utils.UIUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jie on 2018/7/10.
 */
public class Sc_VideoAdapter extends RecyclerView.Adapter<Sc_ImageAdapter.ViewHolder> {
    private Context mContext;
    private List<SckInfo> mList;
    private int width;

    public Sc_VideoAdapter(Context mContext) {
        this.mContext = mContext;
        WindowManager wm = ((Activity) mContext).getWindowManager();
        width = wm.getDefaultDisplay().getWidth() / 2 - UIUtil.dip2px(mContext, 16);
    }

    public void setList(List<SckInfo> mList) {
        this.mList = mList;
        for (final SckInfo scImageInfo : mList) {
            if (scImageInfo.getScale() == 0) {
                Glide.with(mContext).load("http://" + scImageInfo.getIco()).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        float scale = resource.getIntrinsicWidth() / (float) resource.getIntrinsicHeight();
                        scImageInfo.setScale(scale);
                        notifyDataSetChanged();
                    }

                });
            } else {
                notifyDataSetChanged();
            }
        }
    }

    @NonNull
    @Override
    public Sc_ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_sc_video, parent, false);
        Sc_ImageAdapter.ViewHolder viewHolder = new Sc_ImageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final Sc_ImageAdapter.ViewHolder holder, int position) {
        final ViewGroup.LayoutParams layoutParams = holder.mImageView.getLayoutParams();
        layoutParams.width = width;
        if (mList.get(position).getScale() != 0) {
            layoutParams.height = (int) (layoutParams.width / mList.get(position).getScale());
        }
        holder.mImageView.setBackgroundColor(R.color.topbar);
        L.d("图片地址：","http://" + mList.get(position).getIco());
        Glide.with(mContext)
                .load("http://" + mList.get(position).getIco())
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        L.d("失败原因：",e.toString());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageview)
        ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
