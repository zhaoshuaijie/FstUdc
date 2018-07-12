package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.adapter.LiveImageAdapter;
import com.lcsd.fstudc.ui.moudle.LiveImage;
import com.lcsd.fstudc.utils.L;
import com.lcsd.fstudc.view.MultipleStatusView;
import com.melnykov.fab.FloatingActionButton;
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
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/5/22.
 */
public class LiveImageListActivity extends BaseSionBarActivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;
    @BindView(R.id.lv_livelist)
    ListView mLv;
    @BindView(R.id.ptrframelayout)
    PtrClassicFrameLayout mPtr;
    private Context mContext;
    private LiveImageAdapter mAdapter;
    private List<LiveImage> mList;
    private int pageid = 1;
    private int total_page;
    private PromptDialog promptDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_live_list;
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
        promptDialog = new PromptDialog(this);
        mTv_title.setText("图文直播");
    }

    @Override
    protected void initData() {
        mList = new ArrayList<>();
        mFab.attachToListView(mLv);
        mAdapter = new LiveImageAdapter(mContext);
        mLv.setAdapter(mAdapter);
        mPtr.setLastUpdateTimeRelateObject(this);
        mPtr.disableWhenHorizontalMove(true);
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
                    return super.checkCanDoLoadMore(frame, mLv, footer);
                } else {
                    return false;
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mLv, header);
            }
        });
        mLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PromptButton cancle = new PromptButton("取消", null);
                cancle.setTextColor(Color.parseColor("#0076ff"));
                //默认两个按钮为Alert对话框，大于三个按钮的为底部SHeet形式展现
                promptDialog.showAlertSheet("确定删除该主题？", true, cancle,
                        new PromptButton("确定", new PromptButtonListener() {
                            @Override
                            public void onClick(PromptButton promptButton) {
                                request_delete(mList.get(position).getId());
                            }
                        }));
                return false;
            }
        });
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(mContext,LiveImageContentActivity.class).putExtra("id",mList.get(position).getId()));
            }
        });
        request_data(0);
    }

    private void request_data(final int i) {
        Map<String, String> map = new HashMap<>();
        map.put("c", "list");
        if (i == 1) {
            map.put("pageid", 1 + "");
            pageid = 1;
        } else {
            map.put("pageid", pageid + "");
        }
        App.getInstance().getmMyOkHttp().post(mContext, Api.Index, map, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                if (response != null) {
                    L.d("图文直播主题列表：", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        pageid = object.getInt("pageid");
                        total_page =object.getInt("total_page");
                        List<LiveImage> list = JSON.parseArray(object.getString("rslist"), LiveImage.class);
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

    private void request_delete(String id){
        Map<String, String> map = new HashMap<>();
        map.put("c", "vlive");
        map.put("f","deindex");
        map.put("id",id);
        App.getInstance().getmMyOkHttp().post(mContext, Api.Index, map, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                if(response!=null){
                    L.d("删除主题：",response);
                    try {
                        JSONObject object=new JSONObject(response);
                        if(object.getString("status").equals("ok")){
                            promptDialog.showSuccess("删除成功");
                            request_data(1);
                        }else {
                            promptDialog.showError(object.getString("content"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                promptDialog.showWarn("请检查网络");
            }
        });
    }
    @Override
    protected void setListener() {
        mLl_back.setOnClickListener(this);
        mMultipleStatusView.setOnRetryClickListener(mOnRetrListene);
        mFab.setOnClickListener(this);
        mMultipleStatusView.showContent();
    }

    //重试监听
    private View.OnClickListener mOnRetrListene = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mMultipleStatusView.getViewStatus() == MultipleStatusView.STATUS_EMPTY) {
                startActivity(new Intent(mContext, FoundImageLiveActivity.class).putExtra("id", 0));
            } else {
                //刷新重试
                request_data(1);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defute_ll_back:
                finish();
                break;
            case R.id.fab:
                startActivity(new Intent(mContext, FoundImageLiveActivity.class).putExtra("id", 0));
                break;
        }
    }
}
