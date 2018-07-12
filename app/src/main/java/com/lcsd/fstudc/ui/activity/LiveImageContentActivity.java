package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ninegridview.NineGridView;
import com.lcsd.fstudc.ui.adapter.LiveImageContentAdapter;
import com.lcsd.fstudc.view.MultipleStatusView;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by jie on 2018/5/28.
 */
public class LiveImageContentActivity extends BaseDialogactivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;
    @BindView(R.id.ptrframelayout)
    PtrClassicFrameLayout mPtr;
    @BindView(R.id.lv_paike)
    ListView mListView;
    private Context mContext;
    private LiveImageContentAdapter mAdapter;
    //主题id
    private String id;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_live_image_content;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(mView).init();
    }

    @Override
    protected void initView() {
        mMultipleStatusView.showLoading();
        mContext = this;
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
        id = getIntent().getStringExtra("id");
        mTv_title.setText("图文直播");
        mTv_right.setText("添加内容");
        mTv_right.setVisibility(View.VISIBLE);
        NineGridView.setImageLoader(new GlideImageLoader());
    }

    @Override
    protected void initData() {
        mAdapter = new LiveImageContentAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mMultipleStatusView.showContent();
        mPtr.setLastUpdateTimeRelateObject(this);
        mPtr.disableWhenHorizontalMove(true);
        mPtr.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
            }

          /*  @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return super.checkCanDoLoadMore(frame, mLv, footer);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mLv, header);
            }*/
        });
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
                startActivity(new Intent(mContext,LiveImageAddContentActivity.class).putExtra("tid",id));
                break;
        }
    }

    /**
     * 设置Glide 加载
     */
    private class GlideImageLoader implements NineGridView.ImageLoader {
        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url)
                    .placeholder(R.drawable.ic_default_color)
                    .error(R.drawable.ic_default_color)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }
}
