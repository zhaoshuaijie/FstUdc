package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.CleanableEditText;
import com.lcsd.fstudc.view.ScollGridview;

import butterknife.BindView;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/7/11.
 * contne:图文直播添加直播内容
 */
public class LiveImageAddContentActivity extends BaseSionBarActivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.image_scollgv)
    ScollGridview mScollGridview;
    @BindView(R.id.iv_addimg)
    ImageView iv_addimg;
    @BindView(R.id.addcontnet_ced)
    CleanableEditText mEditText;
    private Context mContext;
    //主题id
    private String tid;
    private PromptDialog promptDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_liveimage_addcontent;
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
        promptDialog = new PromptDialog(this);
        mTv_title.setText("添加内容");
        mTv_right.setText("提交");
        mTv_right.setVisibility(View.VISIBLE);
        tid = getIntent().getStringExtra("id");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mLl_back.setOnClickListener(this);
        mTv_right.setOnClickListener(this);
        iv_addimg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defute_ll_back:
                finish();
                break;
            case R.id.defute_tv_right:
                if (Utils.isEmpty(mEditText.getText().toString())) {
                    promptDialog.showWarn("请输入简介描述");
                    return;
                }

                break;
            case R.id.iv_addimg:
                PromptButton cancle = new PromptButton("取消", null);
                cancle.setTextColor(Color.parseColor("#0076ff"));
                promptDialog.showAlertSheet("选择图片或视频", true, cancle,
                        new PromptButton("视频", new PromptButtonListener() {
                            @Override
                            public void onClick(PromptButton promptButton) {

                            }
                        }),
                        new PromptButton("图片", new PromptButtonListener() {
                            @Override
                            public void onClick(PromptButton promptButton) {

                            }
                        }));
                break;
        }
    }
}
