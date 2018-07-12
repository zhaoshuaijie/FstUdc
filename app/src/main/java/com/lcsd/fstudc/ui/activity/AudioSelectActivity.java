package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.adapter.SelectorAudioAdapter;
import com.lcsd.fstudc.ui.moudle.AudioInfo;
import com.lcsd.fstudc.view.MultipleStatusView;
import com.lcsd.fstudc.voice.RecordVoiceButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jie on 2018/5/23.
 */
public class AudioSelectActivity extends BaseDialogactivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.lv_audio)
    ListView lv;
    @BindView(R.id.fab)
    RecordVoiceButton mFab;
    @BindView(R.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;
    private List<AudioInfo> mAudioInfoList;
    private SelectorAudioAdapter audioAdapter;
    private Context mContext;
    private AudioInfo audioInfo;
    //最少时间
    private int duration=3000;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_audio_select;
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
        mTv_title.setText("音频选择");
        mTv_right.setText("确定");
    }

    @Override
    protected void initData() {
        mAudioInfoList = new ArrayList<>();
        audioAdapter = new SelectorAudioAdapter(mContext, mAudioInfoList, duration);
        lv.setAdapter(audioAdapter);
        mFab.attachToListView(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAudioInfoList.get(position).getDuration() < duration) {
                    showWarn("视频时间过短");
                    return;
                }
                audioAdapter.setSelectorPosition(position);

                if (audioAdapter.getSelectorPosition() != -1) {
                    audioInfo = mAudioInfoList.get(position);
                    mTv_right.setVisibility(View.VISIBLE);
                } else {
                    audioInfo = null;
                    mTv_right.setVisibility(View.INVISIBLE);
                }
            }
        });
        getAudioInfoList();
    }

    @Override
    protected void setListener() {
        mLl_back.setOnClickListener(this);
        mTv_right.setOnClickListener(this);
        mFab.setEnrecordVoiceListener(new RecordVoiceButton.EnRecordVoiceListener() {
            @Override
            public void onFinishRecord(long length, String strLength, String filePath) {
                if(filePath!=null){
                    //剪切完成通知音频库更新
                    Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(filePath));
                    t.setData(uri);
                    mContext.sendBroadcast(t);
                    startActivity(new Intent(mContext,CutAudioActivity.class).putExtra("path",filePath).putExtra("time",(int)length));
                    AudioSelectActivity.this.finish();
                }else {
                    Toast.makeText(mContext,"取消录音",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defute_ll_back:
                finish();
                break;
            case R.id.defute_tv_right:
                startActivity(new Intent(mContext,CutAudioActivity.class).putExtra("path",audioInfo.getPath()).putExtra("time",(int)(audioInfo.getDuration()/1000)));
                this.finish();
                break;
        }
    }

    private void getAudioInfoList() {
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);

        if (cursor == null) return;
        Observable.just(cursor)
                .map(new Func1<Cursor, List<AudioInfo>>() {
                    @Override
                    public List<AudioInfo> call(Cursor cursor) {
                        return cursorToList(cursor);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<AudioInfo>>() {
                    @Override
                    public void call(List<AudioInfo> audioInfos) {
                        audioAdapter.setData(audioInfos);
                        if(audioInfos.size()>0){
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
     * 将扫描的音频添加到集合中
     *
     * @param cursor
     * @return
     */
    private List<AudioInfo> cursorToList(Cursor cursor) {
        mAudioInfoList.clear();
        AudioInfo audioInfo;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            String date = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
            long duration = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
            //只获取MP3音频
            if ((displayName.substring(displayName.indexOf(".") + 1).trim()).equals("mp3")) {
                audioInfo = new AudioInfo(id, displayName,
                        mimeType, path, date, size, duration);
                mAudioInfoList.add(audioInfo);
            }
        }
        cursor.close();

        return mAudioInfoList;
    }

}
