package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcsd.fstudc.R;
import com.lcsd.fstudc.utils.FileUtils;
import com.lcsd.fstudc.utils.UIUtil;
import com.lcsd.fstudc.view.CutView;
import com.lcsd.fstudc.view.MyVideoView;

import java.io.File;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import butterknife.BindView;

/**
 * Created by jie on 2018/5/23.
 */
public class CutSizeActivity extends BaseDialogactivity implements View.OnClickListener{
    @BindView(R.id.cut_vv_play)
    MyVideoView vv_play;
    @BindView(R.id.cv_video)
    CutView cv_video;
    @BindView(R.id.rl_close)
    RelativeLayout rl_close;
    @BindView(R.id.tv_finish_video)
    TextView tv_finish_video;
    @BindView(R.id.tv_defult_title)
    TextView tv_title;
    private Context mContext;
    private String path;
    private int windowWidth, windowHeight, dp50, videoWidth, videoHeight, crop_x, crop_y, crop_width, crop_height;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_cutsize;
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getWindowManager().getDefaultDisplay().getHeight();
        dp50 = (int) getResources().getDimension(R.dimen.dp50);
        mContext = this;
        if (getIntent() != null) {
            path = getIntent().getStringExtra("path");
        }
        tv_title.setText("视频裁剪");
    }

    @Override
    protected void initData() {
        vv_play.setVideoPath(path);
        vv_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vv_play.setLooping(true);
                vv_play.start();

                videoWidth = vv_play.getVideoWidth();
                videoHeight = vv_play.getVideoHeight();

                float ra = videoWidth * 1f / videoHeight;

                ViewGroup.LayoutParams layoutParams = vv_play.getLayoutParams();
                if (videoHeight > videoWidth) {
                    layoutParams.height = windowHeight - UIUtil.dip2px(mContext, 80);
                    layoutParams.width = (int) (layoutParams.height * ra);
                } else {
                    layoutParams.width = windowWidth - UIUtil.dip2px(mContext, 30);
                    layoutParams.height = (int) (layoutParams.width / ra);
                }
                vv_play.setLayoutParams(layoutParams);
            }
        });

        vv_play.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //设置边距后显示
                cv_video.setMargin(vv_play.getLeft(), vv_play.getTop(), windowWidth - vv_play.getRight(), windowHeight - vv_play.getBottom() - dp50);
            }
        });
    }

    @Override
    protected void setListener() {
        rl_close.setOnClickListener(this);
        tv_finish_video.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_close:
                finish();
                break;
            case R.id.tv_finish_video:
                if (editVideo()) {
                    showProgressDialog("裁剪中...");
                    EpVideo epVideo = new EpVideo(path);
                    epVideo.crop(crop_width, crop_height, crop_x, crop_y);
                    File file = new File(FileUtils.VIDEO_PATH + "/crop");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    final String output = FileUtils.VIDEO_PATH + "/crop/" + System.currentTimeMillis() + ".mp4";
                    EpEditor.OutputOption outputOption = new EpEditor.OutputOption(output);

                    EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
                        @Override
                        public void onSuccess() {
                            //裁剪完成通知图库更新该视频在图库中显示
                            Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(new File(output));
                            t.setData(uri);
                            CutSizeActivity.this.sendBroadcast(t);
                            Toast.makeText(mContext, "剪切成功", Toast.LENGTH_SHORT).show();
                            closeProgressDialog();
                            Intent intent = new Intent();
                            intent.putExtra("return", output);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(mContext,"裁剪失败",Toast.LENGTH_SHORT).show();
                            closeProgressDialog();
                        }

                        @Override
                        public void onProgress(float v) {

                        }
                    });
                } else {
                    finish();
                }
                break;
        }
    }

    //获取裁剪值
    private boolean editVideo() {
        //得到裁剪后的margin值
        float[] cutArr = cv_video.getCutArr();
        float left = cutArr[0];
        float top = cutArr[1];
        float right = cutArr[2];
        float bottom = cutArr[3];
        int cutWidth = cv_video.getRectWidth();
        int cutHeight = cv_video.getRectHeight();

        //计算宽高缩放比
        float leftPro = left / cutWidth;
        float topPro = top / cutHeight;
        float rightPro = right / cutWidth;
        float bottomPro = bottom / cutHeight;

        //得到裁剪位置
        int cropWidth = (int) (videoWidth * (rightPro - leftPro));
        int cropHeight = (int) (videoHeight * (bottomPro - topPro));
        int x = (int) (leftPro * videoWidth);
        int y = (int) (topPro * videoHeight);
        Log.d("裁剪：", "x:" + x + " y:" + y + " 宽度：" + cropWidth + " 高度：" + cropHeight);
        //判断是否裁剪
        if (x == 0 && y == 0 && cropWidth == videoWidth && cropHeight == videoHeight) {
            return false;
        } else {
            crop_x = x;
            crop_y = y;
            crop_width = cropWidth;
            crop_height = cropHeight;
            return true;
        }
    }

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
