package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.gyf.barlibrary.BarHide;
import com.lcsd.camerarecord.JCameraView;
import com.lcsd.camerarecord.listener.ClickListener;
import com.lcsd.camerarecord.listener.ErrorListener;
import com.lcsd.camerarecord.listener.JCameraListener;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.utils.FileUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/29.
 */
public class CameraActivity extends BaseDialogactivity {
    private Context mContext;
    @BindView(R.id.jcameraview)
    JCameraView jCameraView;
    public static CameraActivity cameraActivity;
    private long mMin, mMax;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_BAR).init();
        mContext = this;
        cameraActivity = this;
    }

    protected void initData() {
        File file = new File(FileUtils.VIDEO_PATH1);
        if (!file.exists()) {
            file.mkdirs();
        }
        //设置视频保存路径
        jCameraView.setSaveVideoPath(FileUtils.VIDEO_PATH1);
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);

        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                showError("视频录制出错");
                finish();
                overridePendingTransition(R.anim.in_from_top, R.anim.out_to_bottom);
            }

            @Override
            public void AudioPermissionError() {
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //拍摄的照片
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //拍摄视频完成通知图库更新该视频在图库中显示
                Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(url));
                t.setData(uri);
                mContext.sendBroadcast(t);
                startActivity(new Intent(mContext, EditVideoActivity.class).putExtra("path", url));
                finish();
            }
        });
        //左边边控件
        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraActivity.this.finish();
                overridePendingTransition(R.anim.in_from_top, R.anim.out_to_bottom);
            }
        });
        //右边控件
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                //由系统视频库改为自定义视频库
                startActivity(new Intent(mContext, VideoSelectActivity.class).putExtra("min", mMin).putExtra("max", mMax));
            }
        });
        mMin = 3000;
        mMax = 180000;
        jCameraView.setDrution((int) mMax);
        jCameraView.setMinDrution((int) mMin);
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }
}
