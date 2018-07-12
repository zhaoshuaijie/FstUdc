package com.lcsd.fstudc.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.utils.L;
import com.lcsd.fstudc.utils.UIUtil;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.CleanableEditText;
import com.lcsd.fstudc.view.MyVideoView;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/5/23.
 */
public class VideoUploadActivity extends BaseDialogactivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    private Context mContext;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.vv_play)
    MyVideoView vv_play;
    @BindView(R.id.image_edit)
    CleanableEditText mEditText;
    private int windowWidth, windowHeight;
    private String path;
    private ProgressDialog mProgressDialog;
    private PromptDialog mPromptDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_video_upload;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(mView).init();
    }

    @Override
    protected void initView() {
        mContext = this;
        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getWindowManager().getDefaultDisplay().getHeight();
        path = getIntent().getStringExtra("path");
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
        mTv_title.setText("视频上传");
        mTv_right.setVisibility(View.VISIBLE);
        mTv_right.setText("上传");

    }

    @Override
    protected void initData() {
        mProgressDialog = new ProgressDialog(mContext);
        mPromptDialog = new PromptDialog(this);
        vv_play.setVideoPath(path);
        vv_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vv_play.setLooping(true);
                vv_play.start();
                float ra = vv_play.getVideoWidth() * 1f / vv_play.getVideoHeight();
                ViewGroup.LayoutParams layoutParams = vv_play.getLayoutParams();
                layoutParams.width = (windowWidth - UIUtil.dip2px(mContext, 60));
                layoutParams.height = (int) (layoutParams.width / ra);
                vv_play.setLayoutParams(layoutParams);
            }
        });
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
                if (path != null && path.length() > 0) {
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
        map.put("f", "reportervi");
        map.put("title", mEditText.getText().toString());
        Map<String, File> map1 = new HashMap<>();
        map1.put("video", new File(path));
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

    @Override
    protected void onPause() {
        super.onPause();
        vv_play.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vv_play.start();
    }
}
