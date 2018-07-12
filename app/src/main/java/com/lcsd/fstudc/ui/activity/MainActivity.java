package com.lcsd.fstudc.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.permissions.PermissionManager;
import com.lcsd.fstudc.ui.adapter.FragPagerAdapter;
import com.lcsd.fstudc.ui.fragment.HomeFragment;
import com.lcsd.fstudc.ui.fragment.MaterialFragment;
import com.lcsd.fstudc.ui.fragment.MyFragment;
import com.lcsd.fstudc.utils.L;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MainActivity extends BaseSionBarActivity {
    @BindView(R.id.main_viewpage)
    ViewPager mViewPager;
    @BindView(R.id.ll_01)
    LinearLayout mL1;
    @BindView(R.id.ll_02)
    LinearLayout mL2;
    @BindView(R.id.ll_03)
    LinearLayout mL3;
    @BindView(R.id.iv_01)
    ImageView mIv1;
    @BindView(R.id.iv_02)
    ImageView mIv2;
    @BindView(R.id.iv_03)
    ImageView mIv3;
    @BindView(R.id.tv_01)
    TextView mTv1;
    @BindView(R.id.tv_02)
    TextView mTv2;
    @BindView(R.id.tv_03)
    TextView mTv3;
    private Context mContext;
    private List<Fragment> mFragments;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mContext = this;
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
        mFragments = new ArrayList<>();
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(MaterialFragment.newInstance());
        mFragments.add(MyFragment.newInstance());
    }

    @Override
    protected void initData() {
        mViewPager.setAdapter(new FragPagerAdapter(getSupportFragmentManager(), mFragments));
        mViewPager.setCurrentItem(0, false);
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void setListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTv1.setTextColor(getResources().getColor(R.color.buttomtext));
                mTv2.setTextColor(getResources().getColor(R.color.buttomtext));
                mTv3.setTextColor(getResources().getColor(R.color.buttomtext));
                mIv1.setImageResource(R.mipmap.img_icon_home);
                mIv2.setImageResource(R.mipmap.img_icon_material);
                mIv3.setImageResource(R.mipmap.img_icon_my);
                switch (position) {
                    case 0:
                        ImmersionBar.with((Activity) mContext)
                                .statusBarDarkFont(true, 0.2f)
                                .init();
                        mTv1.setTextColor(getResources().getColor(R.color.buttomtextis));
                        mIv1.setImageResource(R.mipmap.img_icon_homeis);
                        break;
                    case 1:
                        ImmersionBar.with((Activity) mContext)
                                .statusBarDarkFont(true, 0.2f)
                                .init();
                        mTv2.setTextColor(getResources().getColor(R.color.buttomtextis));
                        mIv2.setImageResource(R.mipmap.img_icon_materialis);
                        break;
                    case 2:
                        ImmersionBar.with((Activity) mContext)
                                .statusBarDarkFont(false)
                                .init();
                        mTv3.setTextColor(getResources().getColor(R.color.buttomtextis));
                        mIv3.setImageResource(R.mipmap.img_icon_myis);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mL1.setOnClickListener(mOnClickListener);
        mL2.setOnClickListener(mOnClickListener);
        mL3.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_01:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.ll_02:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.ll_03:
                    mViewPager.setCurrentItem(2);
                    break;
            }
        }
    };

    /**
     * 返回键两次退出程序
     */
    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        request_logout();
    }

    private void request_logout() {
        Map<String, String> map = new HashMap<>();
        map.put("c", "logout");
        App.getInstance().getmMyOkHttp().post(mContext, Api.Index, map, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                if (response != null) {
                    L.d("退出登录：", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("ok")) {
                            if (App.getInstance().checkUser()) {
                                App.getInstance().cleanUserInfo();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }
}
