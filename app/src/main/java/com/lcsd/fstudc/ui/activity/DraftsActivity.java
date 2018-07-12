package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.sql.Constant;
import com.lcsd.fstudc.sql.DbManager;
import com.lcsd.fstudc.sql.ImageLive;
import com.lcsd.fstudc.sql.MySqliteHelper;
import com.lcsd.fstudc.ui.adapter.DraftsAdapter;
import com.lcsd.fstudc.view.MultipleStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jie on 2018/5/30.
 */
public class DraftsActivity extends BaseSionBarActivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    private Context mContext;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.lv_drafts)
    ListView mListView;
    @BindView(R.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;

    private List<ImageLive> mList;
    private DraftsAdapter mAdapter;
    private MySqliteHelper helper;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_drafts;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(mView).init();
    }

    @Override
    protected void initView() {
        mContext = this;
        mMultipleStatusView.showLoading();
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
        mTv_title.setText("草稿箱");
    }

    @Override
    protected void initData() {
        helper = DbManager.getIntance(mContext);
        mList = new ArrayList<>();
        mAdapter = new DraftsAdapter(mContext);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mLl_back.setOnClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(mContext, FoundImageLiveActivity.class).putExtra("id", mList.get(position).getId()).putExtra("imagelive", mList.get(position)));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defute_ll_back:
                finish();
                break;
        }
    }

    private void initSql() {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String sql = "select * from " + Constant.TABLE_NAME + " order by " + Constant.TIME + " DESC";
            Cursor cursor = DbManager.selectDataBySql(db, sql, null);
            mList.clear();
            mList.addAll(DbManager.cursorToList(cursor));
            if (mList.size() > 0) {
                mAdapter.setList(mList);
                mMultipleStatusView.showContent();
            } else {
                mMultipleStatusView.showEmpty();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            mMultipleStatusView.showError();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSql();
    }
}
