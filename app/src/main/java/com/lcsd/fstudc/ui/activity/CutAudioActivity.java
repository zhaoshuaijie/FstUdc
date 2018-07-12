package com.lcsd.fstudc.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.utils.L;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.CleanableEditText;
import com.lcsd.fstudc.view.scrolltrackview.ScrollTrackView;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/5/29.
 */
public class CutAudioActivity extends BaseDialogactivity implements View.OnClickListener {
    private Context mContext;
    @BindView(R.id.top_view)
    View mView;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.tv_druation)
    TextView mTv_druation;
    @BindView(R.id.stv)
    ScrollTrackView mScrollTrackView;
    private String mAudioPath;
    private int mAudioDruation;
    private MediaPlayer mp;
    private int statime;
    @BindView(R.id.image_edit)
    CleanableEditText mEditText;
    private ProgressDialog mProgressDialog;
    private PromptDialog mPromptDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_cut_audio;
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
        mTv_right.setText("上传");
        mTv_title.setText("音频上传");
        mTv_right.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            mAudioPath = getIntent().getStringExtra("path");
            mAudioDruation = getIntent().getIntExtra("time", 0);
        }
    }

    @Override
    protected void initData() {
        mProgressDialog = new ProgressDialog(mContext);
        mPromptDialog = new PromptDialog(this);
        mTv_druation.setText("音频时长：" + mAudioDruation + "s");
        mp = new MediaPlayer();
        try {
            mp.setDataSource(mAudioPath);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.prepareAsync();

        mScrollTrackView.setDuration(mAudioDruation * 1000); // 音频时间
        mScrollTrackView.setCutDuration(mAudioDruation * 1000);//屏幕左边跑到右边持续的时间
        mScrollTrackView.setLoopRun(true);//设置是否循环跑进度
        mScrollTrackView.setSpaceSize(8);
        mScrollTrackView.setTrackItemWidth(8);
        mScrollTrackView.setOnProgressRunListener(new ScrollTrackView.OnProgressRunListener() {
            @Override
            public void onTrackStart(int ms) {
                mp.seekTo(ms);
                statime = ms;
            }

            @Override
            public void onTrackStartTimeChange(int ms) {
                mp.seekTo(ms);
                statime = ms;
            }

            @Override
            public void onTrackEnd() {
                Log.d("截取时间", "结束");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) {
            mp.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null) {
            mp.start();
            mp.seekTo(statime);
        }
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
                if (Utils.isEmpty(mEditText.getText().toString())) {
                    Toast.makeText(mContext, "请输入标题！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mAudioPath != null && mAudioPath.length() > 0) {
                    request_file();
                }
                break;
        }
    }

    private void request_file() {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("正在上传");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Map<String, String> map = new HashMap<>();
        map.put("c", "usercp");
        map.put("f", "reporterau");
        map.put("title", mEditText.getText().toString());
        Map<String, File> map1 = new HashMap<>();
        map1.put("audios", new File(mAudioPath));
        App.getInstance().getmMyOkHttp().upload(mContext, Api.Index, map, map1, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                L.d("上传视频", response);
                mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("ok")) {
                            mPromptDialog.showSuccess("上传成功");
                            mHandler.sendEmptyMessageDelayed(1, 1200);
                        } else {
                            mPromptDialog.showError(object.getString("content"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mPromptDialog.showError("上传失败");
                    }
                }
            }

            @Override
            public void onProgress(long currentBytes, long totalBytes) {
                super.onProgress(currentBytes, totalBytes);
                mProgressDialog.setProgressNumberFormat(Utils.convertFileSize(currentBytes) + "/" + Utils.convertFileSize(totalBytes));
                mProgressDialog.setProgress((int) currentBytes);
                mProgressDialog.setMax((int) totalBytes);//做百分比更新
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                mProgressDialog.dismiss();
                mPromptDialog.showWarn("请检查网络");
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    finish();
                    break;
            }
        }
    };
}
