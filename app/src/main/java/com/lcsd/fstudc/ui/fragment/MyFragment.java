package com.lcsd.fstudc.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.activity.DraftsActivity;
import com.lcsd.fstudc.ui.moudle.UserInfo;

import butterknife.BindView;

/**
 * Created by jie on 2018/5/21.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.my_iv_head)
    ImageView mIv_head;
    @BindView(R.id.my_tv_name)
    TextView mTv_name;
    @BindView(R.id.my_tv_autograph)
    TextView mTv_autograph;
    @BindView(R.id.my_ll1)
    LinearLayout mLl1;
    @BindView(R.id.my_ll2)
    LinearLayout mLl2;
    @BindView(R.id.my_ll3)
    LinearLayout mLl3;
    @BindView(R.id.my_ll4)
    LinearLayout mLl4;

    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initData() {
        UserInfo userInfo = App.getInstance().getUserInfo();
        if(userInfo.getUser()!=null){
            mTv_name.setText(userInfo.getUser());
        }else {
            mTv_name.setText("FST");
        }
        mTv_autograph.setText("这家伙很懒，什么都没有留下");
    }

    @Override
    protected void setListener() {
        mIv_head.setOnClickListener(this);
        mLl1.setOnClickListener(this);
        mLl2.setOnClickListener(this);
        mLl3.setOnClickListener(this);
        mLl4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_iv_head:

                break;
            case R.id.my_ll1:

                break;
            case R.id.my_ll2:
                startActivity(new Intent(mActivity, DraftsActivity.class));
                break;
            case R.id.my_ll3:

                break;
            case R.id.my_ll4:

                break;
        }
    }
}
