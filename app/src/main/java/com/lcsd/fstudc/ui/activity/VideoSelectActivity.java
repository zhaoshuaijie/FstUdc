package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.adapter.BaseAdapterRV;
import com.lcsd.fstudc.ui.adapter.SelectorVideoAdapter;
import com.lcsd.fstudc.ui.moudle.VideoInfo;
import com.lcsd.fstudc.view.MultipleStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jie on 2018/5/22.
 */
public class VideoSelectActivity extends BaseDialogactivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    private Context mContext;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.rv_selector_video)
    RecyclerView mRecyclerView;
    @BindView(R.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;
    private List<VideoInfo> mVideoInfoList;
    private SelectorVideoAdapter mAdapter;
    private VideoInfo mVideoInfo;
    private long mMin, mMax;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_video_select;
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
        mTv_title.setText("视频选择");
        mTv_right.setText("确定");
        mVideoInfoList = new ArrayList<>();
        if (getIntent() != null) {
            mMin = getIntent().getLongExtra("min", 0);
            mMax = getIntent().getLongExtra("max", 0);
        }
    }

    @Override
    protected void initData() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new SelectorVideoAdapter();
        mAdapter.setmMinDuration(mMin);
        mAdapter.setmMaxDuration(mMax);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItemOnClickListener(new BaseAdapterRV.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, Object o) {
                VideoInfo info = (VideoInfo) o;

                if (info.getDuration() > mMax || info.getDuration() < mMin) {
                    showWarn("请选择" + mMin / 1000 + "-" + mMax / 1000 + "s内视频");
                    return;
                }

                mAdapter.setSelectorPosition(position);

                if (mAdapter.getSelectorPosition() != -1) {
                    mVideoInfo = info;
                    mTv_right.setVisibility(View.VISIBLE);
                } else {
                    mVideoInfo = null;
                    mTv_right.setVisibility(View.INVISIBLE);
                }
            }
        });
        getVideoInfoList();
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
                startActivity(new Intent(mContext, EditVideoActivity.class).putExtra("path", mVideoInfo.getPath()));
                CameraActivity.cameraActivity.finish();
                finish();
                break;
        }
    }

    /**
     * 获取本地视频资源是耗时操作！！！！可以不使用rxjava 线程中操作也可以
     */
    private void getVideoInfoList() {
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);

        if (cursor == null) return;
        Observable.just(cursor)
                .map(new Func1<Cursor, List<VideoInfo>>() {
                    @Override
                    public List<VideoInfo> call(Cursor cursor) {
                        return cursorToList(cursor);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<VideoInfo>>() {
                    @Override
                    public void call(List<VideoInfo> videoInfos) {
                        mAdapter.addData(videoInfos);
                        mAdapter.notifyDataSetChanged();
                        if(videoInfos.size()>0){
                            mMultipleStatusView.showContent();
                        }else {
                            mMultipleStatusView.showEmpty();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 将扫描的视频添加到集合中
     *
     * @param cursor
     * @return
     */
    private List<VideoInfo> cursorToList(Cursor cursor) {
        mVideoInfoList.clear();
        VideoInfo videoInfo;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String album = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));

            String artist = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            String date = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));
            long duration = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            videoInfo = new VideoInfo(id, title, album, artist, displayName,
                    mimeType, path, date, size, duration);
            mVideoInfoList.add(videoInfo);
        }
        cursor.close();

        return mVideoInfoList;
    }
}
