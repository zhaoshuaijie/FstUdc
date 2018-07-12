package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.R;

import java.io.File;

import butterknife.BindView;

/**
 * Created by jie on 2018/5/23.
 */
public class GifUploadActivity extends BaseDialogactivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    private Context mContext;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.iv_gif)
    ImageView mIv;
    private String iv_path;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_gif_upload;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(mView).init();
    }

    @Override
    protected void initView() {
        mContext = this;
        if (getIntent() != null) {
            iv_path = getIntent().getStringExtra("path");
        }
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
        mTv_title.setText("Gif上传");
        mTv_right.setVisibility(View.VISIBLE);
        mTv_right.setText("上传");

    }

    @Override
    protected void initData() {
        showProgressDialog("加载中...");
        Glide.with(mContext).load(new File(iv_path)).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().listener(new RequestListener<File, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                closeProgressDialog();
                Toast.makeText(mContext, "加载出错", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                closeProgressDialog();
                return false;
            }
        }).into(mIv);
    }

    @Override
    protected void setListener() {
        mLl_back.setOnClickListener(this);
        mTv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defute_ll_back:
                finish();
                break;
            case R.id.defute_tv_right:

                break;
        }
    }
}
