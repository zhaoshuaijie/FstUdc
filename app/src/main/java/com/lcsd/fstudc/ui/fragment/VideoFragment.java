package com.lcsd.fstudc.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.adapter.Sc_VideoAdapter;
import com.lcsd.fstudc.ui.moudle.SckInfo;
import com.lcsd.fstudc.utils.L;
import com.lcsd.fstudc.view.MultipleStatusView;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

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
 *
 */
public class VideoFragment extends BaseFragment {
    @BindView(R.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;
    @BindView(R.id.ptr)
    PtrClassicFrameLayout mPtr;
    @BindView(R.id.recycleview)
    RecyclerView mRecyclerView;
    private int pageid = 1;
    private int total_page;
    private Sc_VideoAdapter mAdapter;
    private List<SckInfo> mList;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initData() {
        mList = new ArrayList<>();
        mMultipleStatusView.showLoading();
        mPtr.setLastUpdateTimeRelateObject(this);
        mPtr.disableWhenHorizontalMove(true);

        //设置重试视图点击事件
        mMultipleStatusView.setOnRetryClickListener(mRetryClickListener);

        mRecyclerView.setLayoutManager(new
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new Sc_VideoAdapter(mActivity);
        mRecyclerView.setAdapter(mAdapter);

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
                    return super.checkCanDoLoadMore(frame, mRecyclerView, footer);
                } else {
                    return false;
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
            }
        });
        request_data(0);
    }

    private void request_data(final int i) {
        Map<String, String> map = new HashMap<>();
        map.put("c", "res");
        map.put("f", "video");
        if (i == 1) {
            map.put("pageid", 1 + "");
            pageid = 1;
        } else {
            map.put("pageid", pageid + "");
        }
        App.getInstance().getmMyOkHttp().post(mActivity, Api.Index, map, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                if (response != null) {
                    L.d("视频素材库：", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        pageid = object.getInt("pageid");
                        total_page = Integer.parseInt(object.getString("total_page"));
                        List<SckInfo> list = JSON.parseArray(object.getString("rslist"), SckInfo.class);
                        if (list != null && list.size() > 0) {
                            if (i == 1) {
                                mList.clear();
                            }
                            mList.addAll(list);
                            mAdapter.setList(mList);
                            mMultipleStatusView.showContent();
                        } else {
                            mMultipleStatusView.showEmpty();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mMultipleStatusView.showError();
                    }
                    if (i == 1 || i == 2) {
                        mPtr.refreshComplete();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                try {
                    mMultipleStatusView.showNoNetwork();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
