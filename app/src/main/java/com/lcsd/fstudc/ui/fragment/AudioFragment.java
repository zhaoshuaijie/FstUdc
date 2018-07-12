package com.lcsd.fstudc.ui.fragment;

import android.view.View;
import android.widget.ListView;

import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.moudle.SckInfo;
import com.lcsd.fstudc.view.MultipleStatusView;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by jie on 2018/7/10.
 */
public class AudioFragment extends BaseFragment { @BindView(R.id.multiple_status_view)
MultipleStatusView mMultipleStatusView;
    @BindView(R.id.ptr)
    PtrClassicFrameLayout mPtr;
    @BindView(R.id.listview)
    ListView mListView;
    private int pageid = 1;
    private int total_page;
    private List<SckInfo> mList;



    public static AudioFragment newInstance() {
        return new AudioFragment();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_audio;
    }

    @Override
    protected void initData() {
        mList = new ArrayList<>();
        mMultipleStatusView.showLoading();
        mPtr.setLastUpdateTimeRelateObject(this);
        mPtr.disableWhenHorizontalMove(true);

        //设置重试视图点击事件
        mMultipleStatusView.setOnRetryClickListener(mRetryClickListener);


        mPtr.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                if (pageid < total_page) {
                    pageid++;
                    request_data(2);
                }
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                // 下拉刷新操作
                request_data(1);
            }

            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                if (pageid < total_page) {
                    return super.checkCanDoLoadMore(frame, mListView, footer);
                } else {
                    return false;
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }
        });
        request_data(0);
    }

    private void request_data(final int i) {
        Map<String, String> map = new HashMap<>();
        map.put("c", "res");
        map.put("f", "audio");
        if (i == 1) {
            map.put("pageid", 1 + "");
            pageid = 1;
        } else {
            map.put("pageid", pageid + "");
        }
        App.getInstance().getmMyOkHttp().post(mActivity, Api.Index, map, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }
    final View.OnClickListener mRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            request_data(1);
        }
    };
}
