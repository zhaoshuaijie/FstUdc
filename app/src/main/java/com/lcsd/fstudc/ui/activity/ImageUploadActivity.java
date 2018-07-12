package com.lcsd.fstudc.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.adapter.ImageGridAdapter;
import com.lcsd.fstudc.ui.moudle.ImageInfo;
import com.lcsd.fstudc.utils.L;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.CleanableEditText;
import com.muzhi.camerasdk.model.CameraSdkParameterInfo;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/5/22.
 */
public class ImageUploadActivity extends BaseSionBarActivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    private Context mContext;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.image_gv)
    GridView noScrollgridview;
    @BindView(R.id.image_edit)
    CleanableEditText mEditText;
    private ArrayList<ImageInfo> pic_list;
    private ImageGridAdapter mImageGridAdapter;
    private CameraSdkParameterInfo mCameraSdkParameterInfo;
    private ProgressDialog mProgressDialog;
    private PromptDialog mPromptDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_image_upload;
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
        mTv_title.setText("图片上传");
        mTv_right.setVisibility(View.VISIBLE);
        mTv_right.setText("上传");
    }

    @Override
    protected void initData() {
        mProgressDialog = new ProgressDialog(mContext);
        mPromptDialog = new PromptDialog(this);
        mCameraSdkParameterInfo = new CameraSdkParameterInfo();
        mImageGridAdapter = new ImageGridAdapter(this, mCameraSdkParameterInfo.getMax_image());
        noScrollgridview.setAdapter(mImageGridAdapter);
        Bundle b = getIntent().getExtras();
        getBundle(b);
        initEvent();
    }

    @Override
    protected void setListener() {
        mLl_back.setOnClickListener(this);
        mTv_right.setOnClickListener(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY:
                if (data != null) {
                    getBundle(data.getExtras());
                }
                break;
            case CameraSdkParameterInfo.TAKE_PICTURE_PREVIEW:
                if (data != null) {
                    int position = data.getIntExtra("position", -1);
                    if (position >= 0) {
                        mImageGridAdapter.deleteItem(position);
                        mCameraSdkParameterInfo.getImage_list().remove(position);
                    }
                }
                break;
        }

    }

    private void getBundle(Bundle bundle) {
        if (bundle != null) {
            pic_list = new ArrayList<>();

            mCameraSdkParameterInfo = (CameraSdkParameterInfo) bundle.getSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER);
            ArrayList<String> list = mCameraSdkParameterInfo.getImage_list();
            //判断是否为gif，进而判断是否还能增加图片
            boolean isgif = false;
            if (list != null && list.size() > 0) {
                isgif = (list.get(0).substring(list.get(0).indexOf(".") + 1)).equals("gif");
            }
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    ImageInfo img = new ImageInfo();
                    img.setSource_image(list.get(i));
                    pic_list.add(img);
                }
            }
            if (pic_list.size() < mCameraSdkParameterInfo.getMax_image() && !isgif) {
                ImageInfo item = new ImageInfo();
                item.setAddButton(true);
                pic_list.add(item);
            }
            mImageGridAdapter.setList(pic_list);
        }
    }

    private void initEvent() {
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                ImageInfo info = (ImageInfo) arg0.getAdapter().getItem(position);
                if (info.isAddButton()) {

                    ArrayList<String> list = new ArrayList<>();
                    for (ImageInfo pic : pic_list) {
                        if (!pic.isAddButton()) {
                            list.add(pic.getSource_image());
                        }
                    }
                    openCameraSDKPhotoPick(mContext, list);
                } else {
                    openCameraSDKImagePreview(mContext, pic_list.get(position).getSource_image(), position);
                }
            }
        });
    }

    //图片预览
    public void openCameraSDKImagePreview(Context context, String path, int position) {
        mCameraSdkParameterInfo.setPosition(position);
        Intent intent = new Intent();
        intent.setClassName(((AppCompatActivity) context).getApplication(), "com.muzhi.camerasdk.PreviewActivity");
        Bundle b = new Bundle();
        b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
        intent.putExtras(b);
        startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_PREVIEW);
    }

    //本地相册选择
    public void openCameraSDKPhotoPick(Context context, ArrayList<String> list) {
        Intent intent = new Intent();
        intent.setClassName(((AppCompatActivity) context).getApplication(), "com.muzhi.camerasdk.PhotoPickActivity");
        Bundle b = new Bundle();

        b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
        intent.putExtras(b);
        startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);

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
                L.d("图片数量", +mCameraSdkParameterInfo.getImage_list().size() + "张");
                if (mCameraSdkParameterInfo.getImage_list() != null && mCameraSdkParameterInfo.getImage_list().size() > 0) {
                    ArrayList<File> files = new ArrayList<>();
                    for (String path : mCameraSdkParameterInfo.getImage_list()) {
                        File file = new File(path);
                        files.add(file);
                    }
                    request_file(files);
                } else {
                    Toast.makeText(mContext, "您还没有选择图片！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void request_file(ArrayList<File> files) {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("正在上传");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Map<String, String> map = new HashMap<>();
        map.put("c", "usercp");
        map.put("f", "reporterBox");
        map.put("title", mEditText.getText().toString());
        Map<String, ArrayList<File>> map1 = new HashMap<>();
        map1.put("picture[]", files);
        App.getInstance().getmMyOkHttp().upload(mContext, Api.Index, map, null, map1, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                L.d("上传图片", response);
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
    private Handler mHandler=new Handler(){
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
