package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.R;

import butterknife.BindView;

/**
 * Created by jie on 2018/6/11.
 */
public class ManuscriptListActivity extends BaseSionBarActivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;

    private Context mContext;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_manuscript_list;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(mView).init();
    }

    @Override
    protected void initView() {
        mContext = this;
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
        mTv_right.setVisibility(View.VISIBLE);
        mTv_title.setText("文稿");
        mTv_right.setText("新建文稿");
    }

    @Override
    protected void initData() {

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
                startActivity(new Intent(mContext,NewManuscriptActivity.class));
                break;
        }
    }
}
