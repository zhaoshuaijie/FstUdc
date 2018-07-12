package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.moudle.logoInfo;
import com.lcsd.fstudc.utils.FileUtils;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.MyVideoView;
import com.muzhi.camerasdk.adapter.Filter_Sticker_Adapter;
import com.muzhi.camerasdk.library.views.HSuperImageView;
import com.muzhi.camerasdk.library.views.HorizontalListView;
import com.muzhi.camerasdk.model.Filter_Sticker_Info;
import com.muzhi.camerasdk.utils.FilterUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpDraw;
import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import butterknife.BindView;

/**
 * Created by jie on 2018/5/23.
 */
public class EditVideoActivity extends BaseDialogactivity implements View.OnClickListener {
    public static EditVideoActivity mEditVideoActivity;
    private Context mContext;
    @BindView(R.id.vv_play)
    MyVideoView vv_play;
    @BindView(R.id.rl_bottom)
    RelativeLayout rl_bottom;

    @BindView(R.id.rl_close)
    RelativeLayout rl_close;
    @BindView(R.id.tv_finish_video)
    TextView tv_finish_video;
    @BindView(R.id.tv_defult_title)
    TextView tv_title;

    @BindView(R.id.rl_cut_time)
    RelativeLayout rl_cut_time;
    @BindView(R.id.rl_crop)
    RelativeLayout rl_crop;

    @BindView(R.id.sticker_listview)
    HorizontalListView hlistview;
    @BindView(R.id.rl_sticker)
    RelativeLayout rl_stcker;
    private Filter_Sticker_Adapter sAdapter;
    private ArrayList<Filter_Sticker_Info> stickerList;
    private int sticknum = -1;// 贴纸添加的序号
    public static ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合
    private List<logoInfo> mLogolist;
    @BindView(R.id.rl_logo)
    RelativeLayout rl_logo;
    //手机屏幕宽
    private int windowWidth;
    private String path;
    //视频宽高
    private int videoWidth;
    private int videoHeight;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_edit_video;
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    @Override
    protected void initView() {
        mEditVideoActivity = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        mContext = this;
        tv_title.setText("视频预览");
        path = getIntent().getStringExtra("path");
        vv_play.setVideoPath(path);
        vv_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vv_play.setLooping(true);
                vv_play.start();
            }
        });
        vv_play.setOnPlayStateListener(new MyVideoView.OnPlayStateListener() {
            @Override
            public void onStateChanged(boolean isPlaying) {
                if (isPlaying) {
                    videoWidth = vv_play.getVideoWidth();
                    videoHeight = vv_play.getVideoHeight();

                    float ra = videoWidth * 1f / videoHeight;

                    ViewGroup.LayoutParams layoutParams = vv_play.getLayoutParams();
                    layoutParams.width = windowWidth;
                    layoutParams.height = (int) (layoutParams.width / ra);
                    vv_play.setLayoutParams(layoutParams);
                    //设置logo容器大小
                    ViewGroup.LayoutParams l = rl_logo.getLayoutParams();
                    l.width = windowWidth;
                    l.height = (int) (layoutParams.width / ra);
                    rl_logo.setLayoutParams(l);
                }
            }
        });
    }

    @Override
    protected void initData() {
        mLogolist = new ArrayList<>();
        sticklist = new ArrayList<>();
        stickerList = new ArrayList<>();
        stickerList = FilterUtils.getStickerList();
        sAdapter = new Filter_Sticker_Adapter(this, stickerList);
        hlistview.setAdapter(sAdapter);
    }

    @Override
    protected void setListener() {
        tv_finish_video.setOnClickListener(this);
        rl_close.setOnClickListener(this);
        rl_cut_time.setOnClickListener(this);
        rl_crop.setOnClickListener(this);
        rl_stcker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hlistview.setVisibility(View.VISIBLE);
            }
        });
        //贴纸点击事件
        hlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                String path = stickerList.get(arg2).getLocal_path();
                int drawableId = stickerList.get(arg2).getDrawableId();
                addSticker(drawableId, path);
            }
        });
    }

    //加贴纸
    public void addSticker(int drawableId, final String path) {
        sticknum++;
        HSuperImageView imageView = new HSuperImageView(this, sticknum);
        if (drawableId > 0) {
            //设置为原来的一半
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableId, options);
            showSticker(bmp, imageView);
        }

        sticklist.add(imageView);
        rl_logo.addView(imageView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void showSticker(final Bitmap bmp, HSuperImageView imageView) {
        if (bmp != null) {
            imageView.init(bmp);// 设置控件图片
            eventStickerImage(imageView);
            //将所有贴纸的外框全部去掉贴
            hideStickEditMode();
            imageView.setStickEditMode(true);

        } else {
            Toast.makeText(this, "加载贴纸失败", Toast.LENGTH_SHORT).show();
        }
    }

    //将所有的贴纸修改成不可编辑的模式(外框全部去掉贴)
    private void hideStickEditMode() {
        for (int i = 0; i < sticklist.size(); i++) {
            sticklist.get(i).setStickEditMode(false);
            sticklist.get(i).invalidate();
        }
    }

    //监听贴纸的事件
    private void eventStickerImage(HSuperImageView imageView) {

        imageView.setOnStickerListener(new HSuperImageView.OnStickerListener() {
            @Override
            public void onStickerModeChanged(int position, int flag, HSuperImageView view) {
                // TODO Auto-generated method stub
                if (flag == 1) {
                    //删除
                    try {
                        rl_logo.removeView(view);
                        sticklist.remove(view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (flag == 2) {
                    //点击
                    hideStickEditMode();
                    view.setStickEditMode(true);
                }
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        //裁剪回传
        if (requestCode == 2) {
            path = data.getStringExtra("return");
            vv_play.setVideoPath(path);
            vv_play.start();
            //剪切回传
        } else if (requestCode == 3) {
            path = data.getStringExtra("return");
            vv_play.setVideoPath(path);
            vv_play.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finish_video:
                if (!TextUtils.isEmpty(path)) {
                    //判断有没有添加贴纸
                    if (sticklist != null && sticklist.size() > 0) {
                        mLogolist.clear();
                        float f = (videoWidth * 1f) / windowWidth;
                        for (int i = 0; i < sticklist.size(); i++) {
                            logoInfo logoinfo = new logoInfo();
                            String path = Utils.SavaImage(Utils.getBitmapFromView(sticklist.get(i)), "/storage/emulated/0", mContext);
                            logoinfo.setPath(path);
                            logoinfo.setPicHeight(sticklist.get(i).getHeight() * f);
                            logoinfo.setPicWidth(sticklist.get(i).getWidth() * f);
                            logoinfo.setPicx((int) (sticklist.get(i).getLeft() * f));
                            logoinfo.setPicy((int) (sticklist.get(i).getTop() * f));
                            mLogolist.add(logoinfo);
                        }
                        videoaddlogo(mLogolist);
                    } else {
                        Intent intent = new Intent(mContext, VideoUploadActivity.class);
                        intent.putExtra("path", path);
                        startActivity(intent);
                        finish();
                    }

                }
                break;
            case R.id.rl_close:
                finish();
                break;
            case R.id.rl_cut_time:
                Intent intent2 = new Intent(mContext, CutTimeActivity.class);
                intent2.putExtra("path", path);
                startActivityForResult(intent2, 3);
                break;
            case R.id.rl_crop:
                Intent intent4 = new Intent(mContext, CutSizeActivity.class);
                intent4.putExtra("path", path);
                startActivityForResult(intent4, 2);
                break;
        }
    }

    private void videoaddlogo(final List<logoInfo> list) {
        showProgressDialog("处理中...");
        File file = new File(FileUtils.VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String output = FileUtils.VIDEO_PATH + "/" + System.currentTimeMillis() + ".mp4";
        final EpVideo epVideo = new EpVideo(path);
        for (int i = 0; i < list.size(); i++) {
            epVideo.addDraw(new EpDraw(list.get(i).getPath(), list.get(i).getPicx(), list.get(i).getPicy(), list.get(i).getPicWidth(), list.get(i).getPicHeight(), false));
        }
        EpEditor.exec(epVideo, new EpEditor.OutputOption(output), new OnEditorListener() {
            @Override
            public void onSuccess() {
                Intent t = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(output));
                t.setData(uri);
                EditVideoActivity.this.sendBroadcast(t);

                //删除生成的位图
                for (int i = 0; i < list.size(); i++) {
                    try {
                        File f = new File(list.get(i).getPath());
                        f.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                closeProgressDialog();

                Intent intent = new Intent(mContext, VideoUploadActivity.class);
                intent.putExtra("path", output);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure() {
                closeProgressDialog();
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
            }
        });
    }
}
