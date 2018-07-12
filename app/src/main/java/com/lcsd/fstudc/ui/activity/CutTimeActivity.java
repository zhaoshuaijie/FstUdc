package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lcsd.fstudc.R;
import com.lcsd.fstudc.utils.FileUtils;
import com.lcsd.fstudc.utils.UIUtil;
import com.lcsd.fstudc.view.ThumbnailView;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import butterknife.BindView;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/5/23.
 */
public class CutTimeActivity extends BaseDialogactivity {
    private Context mContext;
    @BindView(R.id.ll_thumbnail)
    LinearLayout ll_thumbnail;
    @BindView(R.id.thumbnailView)
    ThumbnailView thumbnailView;
    @BindView(R.id.tv_jinqietime)
    TextView tv_time;
    @BindView(R.id.cut_progressBar)
    SeekBar sb;
    @BindView(R.id.textureView)
    TextureView textureView;
    private String path;
    //裁剪的开始及结束时间
    private int startTime;
    private int endTime;
    //屏幕宽
    private int windowWidth;
    private int windowHeight;
    //视频宽高时长
    private int videoWidth;
    private int videoHeight;
    private int videoDuration;

    private MediaPlayer mMediaPlayer;
    //区分有没有进行拖动
    private boolean isScorll = false;
    //当前播放进度
    private int timePosition;
    private Timer timer;
    private PromptDialog promptDialog;

    @BindView(R.id.rl_close)
    RelativeLayout rl_close;
    @BindView(R.id.tv_finish_video)
    TextView tv_finish_video;
    @BindView(R.id.tv_defult_title)
    TextView tv_title;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_cut_time;
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    @Override
    protected boolean isUnbinder() {
        return false;
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = this;
        //创建对象
        promptDialog = new PromptDialog(this);
        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getWindowManager().getDefaultDisplay().getHeight();
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        tv_title.setText("视频编辑");
    }

    @Override
    protected void initData() {
        timer = new Timer();
        sb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("ACETEST", "监听");
                return true;
            }
        });
    }

    @Override
    protected void setListener() {
        rl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_finish_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScorll) {
                    PromptButton cancle = new PromptButton("取消", null);
                    cancle.setTextColor(Color.parseColor("#0076ff"));
                    //默认两个按钮为Alert对话框，大于三个按钮的为底部SHeet形式展现
                    promptDialog.showAlertSheet("视频操作", true, cancle,
                            new PromptButton("删除滑块内视频转Gif", new PromptButtonListener() {
                                @Override
                                public void onClick(PromptButton promptButton) {
                                    cutVideoToGif2();
                                }
                            }),
                            new PromptButton("滑块内视频转Gif", new PromptButtonListener() {
                                @Override
                                public void onClick(PromptButton promptButton) {
                                    cutVideoToGif();
                                }
                            }),
                            new PromptButton("删除滑块内视频", new PromptButtonListener() {
                                @Override
                                public void onClick(PromptButton promptButton) {
                                    cutVideo2();
                                }
                            }), new PromptButton("保留滑块内视频", new PromptButtonListener() {
                                @Override
                                public void onClick(PromptButton promptButton) {
                                    cutVideo1();
                                }
                            }));
                } else {
                    finish();
                }
            }
        });
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                initMediaPlay(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        //监听裁剪器滑动
        thumbnailView.setOnScrollBorderListener(new ThumbnailView.OnScrollBorderListener() {
            @Override
            public void OnScrollBorder(float start, float end) {
                changeTime();
            }

            @Override
            public void onScrollStateChange() {
                changeVideoPlay();
            }
        });
    }

    private Handler mHandler_time = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //取出消息携带的数据
            Bundle data = msg.getData();
            timePosition = data.getInt("currentPosition");
            if (endTime == 0 && (videoDuration - timePosition) <= 50) {
                mMediaPlayer.seekTo(startTime);
            }
            if (endTime != 0 && (endTime - timePosition) <= 50) {
                mMediaPlayer.seekTo(startTime);
            }
            if (timePosition >= startTime) {
                sb.setVisibility(View.VISIBLE);
                //设置进度
                sb.setProgress(timePosition);
            }
        }
    };

    //保留中间
    private void cutVideo1() {
        showProgressDialog("剪切中...");
        float startM = (float) startTime / 1000;
        float endM = (float) (endTime - startTime) / 1000;
        Log.d("剪切时间", startM + "开始,减" + endM);
        EpVideo epVideo = new EpVideo(path);
        epVideo.clip(startM, endM);
        File file = new File(FileUtils.VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String output = FileUtils.VIDEO_PATH + "/" + System.currentTimeMillis() + ".mp4";
        EpEditor.exec(epVideo, new EpEditor.OutputOption(output), new OnEditorListener() {
            @Override
            public void onSuccess() {
                //剪切完成通知图库更新该视频在图库中显示
                Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(output));
                t.setData(uri);
                CutTimeActivity.this.sendBroadcast(t);
                Toast.makeText(mContext, "剪切成功", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
                Intent intent = new Intent();
                intent.putExtra("return", output);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure() {
                Toast.makeText(mContext, "剪切失败", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onProgress(float v) {
            }
        });
    }

    //剪辑掉中间，保留两头
    private void cutVideo2() {
        showProgressDialog("剪切中...");
        float startM1 = (float) startTime / 1000;
        float startM2 = (float) endTime / 1000;
        //滑块两端都拖动
        if (startTime != 0 && endTime < videoDuration) {
            EpVideo epVideo1 = new EpVideo(path);
            epVideo1.clip(0, startM1);
            Log.d("前一段", "0s开始，持续时间：" + startM1);
            EpVideo epVideo2 = new EpVideo(path);
            epVideo2.clip(startM2, ((videoDuration - endTime) / 1000));
            Log.d("后一段", startM2 + "开始，持续时间：" + ((videoDuration - endTime) / 1000));
            File file = new File(FileUtils.VIDEO_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            final String output = FileUtils.VIDEO_PATH + "/" + System.currentTimeMillis() + ".mp4";
            ArrayList<EpVideo> epVideos = new ArrayList<>();
            epVideos.add(epVideo1);//视频1
            epVideos.add(epVideo2);//视频2
            //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
            EpEditor.OutputOption outputOption = new EpEditor.OutputOption(output);
            outputOption.setWidth(videoWidth);
            outputOption.setHeight(videoHeight);
            EpEditor.merge(epVideos, outputOption, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    //剪切完成通知图库更新该视频在图库中显示
                    Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(output));
                    t.setData(uri);
                    CutTimeActivity.this.sendBroadcast(t);
                    Toast.makeText(mContext, "剪切成功", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();

                    Intent intent = new Intent();
                    intent.putExtra("return", output);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(CutTimeActivity.this, "剪切失败", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();
                }

                @Override
                public void onProgress(float v) {

                }
            });
        } else {
            EpVideo epVideo = new EpVideo(path);
            if (startTime == 0 && endTime < videoDuration) {
                epVideo.clip((float) (endTime / 1000), (float) (videoDuration - endTime) / 1000);
                Log.d("剪切时间", (float) (endTime / 1000) + "开始,减" + (float) (videoDuration - endTime) / 1000);
            } else if (startTime != 0 && endTime == videoDuration) {
                epVideo.clip(0, (float) startTime / 1000);
                Log.d("剪切时间", 0 + "开始,减" + (float) startTime / 1000);
            }
            File file = new File(FileUtils.VIDEO_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            final String output = FileUtils.VIDEO_PATH + "/" + System.currentTimeMillis() + ".mp4";
            EpEditor.exec(epVideo, new EpEditor.OutputOption(output), new OnEditorListener() {
                @Override
                public void onSuccess() {
                    //剪切完成通知图库更新该视频在图库中显示
                    Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(output));
                    t.setData(uri);
                    CutTimeActivity.this.sendBroadcast(t);
                    Toast.makeText(mContext, "剪切成功", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();

                    Intent intent = new Intent();
                    intent.putExtra("return", output);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(mContext, "剪切失败", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();
                }

                @Override
                public void onProgress(float v) {
                }
            });
        }
    }

    //保留一段转gif
    private void cutVideoToGif() {
        showProgressDialog("转换中...");
        float startM = (float) startTime / 1000;
        float endM = (float) (endTime - startTime) / 1000;
        //MP4生成gif
        File file = new File(FileUtils.VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String output = FileUtils.VIDEO_PATH + "/" + System.currentTimeMillis() + ".gif";
        EpEditor.execCmd("-ss " + startM + " -t " + endM + " -i " + path + " -s " + 360 + "*" + (360 * videoHeight) / videoWidth + " -r 6 -f gif " + output, 0, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(output));
                t.setData(uri);
                CutTimeActivity.this.sendBroadcast(t);
                Toast.makeText(mContext, "转换成功", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
                startActivity(new Intent(mContext, GifUploadActivity.class).putExtra("path", output));
                EditVideoActivity.mEditVideoActivity.finish();
                finish();
            }

            @Override
            public void onFailure() {
                Toast.makeText(mContext, "转换失败", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onProgress(float v) {
            }
        });
    }

    private void cutVideoToGif2() {
        showProgressDialog("转换中...");
        float startM1 = (float) startTime / 1000;
        float startM2 = (float) endTime / 1000;
        //滑块两端都拖动
        if (startTime != 0 && endTime < videoDuration) {
            EpVideo epVideo1 = new EpVideo(path);
            epVideo1.clip(0, startM1);
            Log.d("前一段", "0s开始，持续时间：" + startM1);
            EpVideo epVideo2 = new EpVideo(path);
            epVideo2.clip(startM2, ((videoDuration - endTime) / 1000));
            Log.d("后一段", startM2 + "开始，持续时间：" + ((videoDuration - endTime) / 1000));
            File file = new File(FileUtils.VIDEO_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            final String output = FileUtils.VIDEO_PATH + "/" + System.currentTimeMillis() + ".mp4";
            ArrayList<EpVideo> epVideos = new ArrayList<>();
            epVideos.add(epVideo1);//视频1
            epVideos.add(epVideo2);//视频2
            //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
            EpEditor.OutputOption outputOption = new EpEditor.OutputOption(output);
            outputOption.setWidth(videoWidth);
            outputOption.setHeight(videoHeight);
            EpEditor.merge(epVideos, outputOption, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    //剪切完成转gif
                    videotogif(output);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(CutTimeActivity.this, "转换失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(float v) {

                }
            });
        } else {
            EpVideo epVideo = new EpVideo(path);
            if (startTime == 0 && endTime < videoDuration) {
                epVideo.clip((float) (endTime / 1000), (float) (videoDuration - endTime) / 1000);
                Log.d("剪切时间", (float) (endTime / 1000) + "开始,减" + (float) (videoDuration - endTime) / 1000);
            } else if (startTime != 0 && endTime == videoDuration) {
                epVideo.clip(0, (float) startTime / 1000);
                Log.d("剪切时间", 0 + "开始,减" + (float) startTime / 1000);
            }
            File file = new File(FileUtils.VIDEO_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            final String output = FileUtils.VIDEO_PATH + "/" + System.currentTimeMillis() + ".mp4";
            EpEditor.exec(epVideo, new EpEditor.OutputOption(output), new OnEditorListener() {
                @Override
                public void onSuccess() {
                    //剪切完成转gif
                    videotogif(output);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(mContext, "转换失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(float v) {
                }
            });
        }
    }

    //剪切过视频转gif
    private void videotogif(final String outvideopath) {
        File file = new File(FileUtils.VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String output = FileUtils.VIDEO_PATH + "/" + System.currentTimeMillis() + ".gif";
        EpEditor.execCmd("-ss " + 0 + " -t " + FileUtils.getDuration(outvideopath) / 1000 + " -i " + outvideopath + " -s " + 360 + "*" + (360 * videoHeight) / videoWidth + " -r 6 -f gif " + output, 0, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(output));
                t.setData(uri);
                CutTimeActivity.this.sendBroadcast(t);
                Toast.makeText(mContext, "转换成功", Toast.LENGTH_SHORT).show();
                //删除剪切的视频
                try {
                    File f = new File(outvideopath);
                    f.delete();
                } catch (Exception e) {
                }

                closeProgressDialog();
                startActivity(new Intent(CutTimeActivity.this, GifUploadActivity.class).putExtra("path", output));
                EditVideoActivity.mEditVideoActivity.finish();
                finish();
            }

            @Override
            public void onFailure() {
                Toast.makeText(mContext, "转换失败", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onProgress(float v) {
            }
        });
    }

    /**
     * 更改选择的裁剪区间的时间
     */
    private void changeTime() {

        float left = thumbnailView.getLeftInterval();
        float pro1 = left / ll_thumbnail.getWidth();

        startTime = (int) (videoDuration * pro1);

        float right = thumbnailView.getRightInterval();
        float pro2 = right / ll_thumbnail.getWidth();
        endTime = (int) (videoDuration * pro2);
        tv_time.setText("时长：" + (double) (endTime - startTime) / 1000 + " s");

        sb.setVisibility(View.GONE);

        if (startTime != 0 || endTime != videoDuration) {
            isScorll = true;
        }
    }

    //改变视频进度
    private void changeVideoPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(startTime);
            sb.setProgress(startTime);
            Log.d("视频播放；", startTime + "");
        }
    }

    /**
     * 初始化视频播放器
     */
    private void initVideoSize() {
        float ra = videoWidth * 1f / videoHeight;
        ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
        if (videoHeight > videoWidth) {
            layoutParams.height = windowHeight - UIUtil.dip2px(mContext, 120);
            layoutParams.width = (int) (layoutParams.height * ra);
        } else {
            layoutParams.width = windowWidth - UIUtil.dip2px(mContext, 30);
            layoutParams.height = (int) (layoutParams.width / ra);
        }
        textureView.setLayoutParams(layoutParams);

        //最小剪切时间2秒
        int pxWidth = (int) (3000f / videoDuration * thumbnailView.getWidth());
        thumbnailView.setMinInterval(pxWidth);
    }

    /**
     * 初始化缩略图
     */
    private void initThumbs() {

        final int frame = 18;
        final int frameTime = videoDuration / frame * 1000;

        int thumbnailWidth = ll_thumbnail.getWidth() / frame;
        for (int x = 0; x < frame; x++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(thumbnailWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundColor(Color.parseColor("#666666"));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ll_thumbnail.addView(imageView);
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
                mediaMetadata.setDataSource(mContext, Uri.parse(path));
                for (int x = 0; x < frame; x++) {
                    Bitmap bitmap = mediaMetadata.getFrameAtTime(frameTime * x, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    Message msg = myHandler.obtainMessage();
                    msg.obj = bitmap;
                    msg.arg1 = x;
                    myHandler.sendMessage(msg);
                }
                mediaMetadata.release();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
            }
        }.execute();
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                ImageView imageView = (ImageView) ll_thumbnail.getChildAt(msg.arg1);
                Bitmap bitmap = (Bitmap) msg.obj;
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
            }
        }
    };

    private void initMediaPlay(SurfaceTexture surface) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setSurface(new Surface(surface));
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();

                    videoDuration = mMediaPlayer.getDuration();
                    videoWidth = mMediaPlayer.getVideoWidth();
                    videoHeight = mMediaPlayer.getVideoHeight();
                    tv_time.setText("时长：" + (double) videoDuration / 1000 + " s");
                    initVideoSize();
                    initThumbs();
                    //设置进度条最大值
                    sb.setMax(videoDuration);
                }
            });
            mMediaPlayer.prepareAsync();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        int currentPosition = mMediaPlayer.getCurrentPosition();
                        Message msg = Message.obtain();
                        //把播放进度存入Message中
                        Bundle data = new Bundle();
                        data.putInt("currentPosition", currentPosition);
                        msg.setData(data);
                        mHandler_time.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mMediaPlayer != null) {
            if (startTime != 0) {
                mMediaPlayer.seekTo(startTime);
                mMediaPlayer.start();
            } else {
                mMediaPlayer.start();
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        mHandler_time.removeCallbacksAndMessages(null);
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }
}
