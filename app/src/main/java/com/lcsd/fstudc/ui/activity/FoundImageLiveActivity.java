package com.lcsd.fstudc.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.permissions.PerUtils;
import com.lcsd.fstudc.permissions.PerimissionsCallback;
import com.lcsd.fstudc.permissions.PermissionEnum;
import com.lcsd.fstudc.permissions.PermissionManager;
import com.lcsd.fstudc.sql.Constant;
import com.lcsd.fstudc.sql.DbManager;
import com.lcsd.fstudc.sql.ImageLive;
import com.lcsd.fstudc.sql.MySqliteHelper;
import com.lcsd.fstudc.ui.adapter.ImageGridAdapter;
import com.lcsd.fstudc.ui.moudle.ImageInfo;
import com.lcsd.fstudc.utils.AddressPickTask;
import com.lcsd.fstudc.utils.L;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.CleanableEditText;
import com.lcsd.fstudc.view.ScollGridview;
import com.muzhi.camerasdk.model.CameraSdkParameterInfo;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.entity.City;
import cn.addapp.pickers.entity.County;
import cn.addapp.pickers.entity.Province;
import cn.addapp.pickers.picker.DateTimePicker;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/5/25.
 */
public class FoundImageLiveActivity extends BaseDialogactivity implements View.OnClickListener {
    @BindView(R.id.top_view)
    View mView;
    private Context mContext;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.ed_title)
    CleanableEditText mEditText_title;
    @BindView(R.id.ll_star_time)
    LinearLayout mLl_star;
    @BindView(R.id.ll_end_time)
    LinearLayout mLl_end;
    @BindView(R.id.tv_star_time)
    TextView mTv_star;
    @BindView(R.id.tv_end_time)
    TextView mTv_end;
    @BindView(R.id.iv_star_time)
    ImageView mIv_star;
    @BindView(R.id.iv_end_time)
    ImageView mIv_end;
    @BindView(R.id.ed_zbf)
    CleanableEditText mEditText_zbf;
    @BindView(R.id.tv_local)
    TextView mTv_local;
    @BindView(R.id.ed_jj)
    CleanableEditText mEditText_jj;
    @BindView(R.id.image_scollgv)
    ScollGridview mScollGridview;
    @BindView(R.id.iv_addimg)
    ImageView iv_addimg;
    @BindView(R.id.tv_commit)
    TextView mTv_content;
    @BindView(R.id.tv_preservation)
    TextView mTv_pre;
    private ArrayList<ImageInfo> pic_list;
    private ImageGridAdapter mImageGridAdapter;
    private CameraSdkParameterInfo mCameraSdkParameterInfo;

    private MySqliteHelper helper;
    //草稿箱本地数据存储id
    private int id;
    private ImageLive mImageLive = null;
    private PromptDialog promptDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_fountlive;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(mView).init();
    }

    @Override
    protected void initView() {
        mContext = this;
        if (getIntent() != null) {
            id = getIntent().getIntExtra("id", 0);
            mImageLive = (ImageLive) getIntent().getSerializableExtra("imagelive");

        }
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
        promptDialog = new PromptDialog(this);
        mTv_title.setText("图文直播");
    }

    @Override
    protected void initData() {
        helper = DbManager.getIntance(this);
        //改变默认的单行模式
        mEditText_jj.setSingleLine(false);
        //水平滚动设置为False
        mEditText_jj.setHorizontallyScrolling(false);
        mCameraSdkParameterInfo = new CameraSdkParameterInfo();
        mImageGridAdapter = new ImageGridAdapter(this, mCameraSdkParameterInfo.getMax_image());
        mScollGridview.setAdapter(mImageGridAdapter);

        if (mImageLive != null) {
            mEditText_title.setText(mImageLive.getTitle());
            if (mImageLive.getStartime() != null && mImageLive.getStartime().length() > 0 && !mImageLive.getStartime().equals("开始时间")) {
                mTv_star.setText(mImageLive.getStartime());
                mTv_star.setTextColor(getResources().getColor(R.color.black));
                mIv_star.setVisibility(View.GONE);
            }
            if (mImageLive.getEndtime() != null && mImageLive.getEndtime().length() > 0 && !mImageLive.getEndtime().equals("结束时间")) {
                mTv_end.setText(mImageLive.getEndtime());
                mTv_end.setTextColor(getResources().getColor(R.color.black));
                mIv_end.setVisibility(View.GONE);
            }
            if (mImageLive.getAddress() != null && mImageLive.getAddress().length() > 0 && !mImageLive.getAddress().equals("地点")) {
                mTv_local.setText(mImageLive.getAddress());
                mTv_local.setTextColor(getResources().getColor(R.color.black));
            }
            mEditText_zbf.setText(mImageLive.getSponsor());
            mEditText_jj.setText(mImageLive.getSynopsis());
            if (mImageLive.getImgs() != null && mImageLive.getImgs().length() > 0) {
                //根据逗号将图集string还原成数组图集
                String[] result = mImageLive.getImgs().split(",");

                iv_addimg.setVisibility(View.GONE);
                pic_list = new ArrayList<>();
                ArrayList<String> list = new ArrayList<>();
                //判断是否为gif，进而判断是否还能增加图片
                boolean isgif = false;
                if (result != null && result.length > 0) {
                    isgif = (result[0].substring(result[0].indexOf(".") + 1)).equals("gif");
                }
                if (result != null) {
                    for (int i = 0; i < result.length; i++) {
                        ImageInfo img = new ImageInfo();
                        img.setSource_image(result[i]);
                        pic_list.add(img);
                        list.add(result[i]);
                        //图集放入图片选择已选图片集中
                        mCameraSdkParameterInfo.setImage_list(list);
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
    }

    @Override
    protected void setListener() {
        mLl_back.setOnClickListener(this);
        mLl_end.setOnClickListener(this);
        mLl_star.setOnClickListener(this);
        mTv_local.setOnClickListener(this);
        iv_addimg.setOnClickListener(this);
        mTv_content.setOnClickListener(this);
        mTv_pre.setOnClickListener(this);
        mScollGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    //时间选择控件
    public void onYearMonthDayTimePicker(final TextView tv, final ImageView iv) {
        Calendar c = Calendar.getInstance();
        DateTimePicker picker = new DateTimePicker(this, DateTimePicker.HOUR_24);
        picker.setActionButtonTop(false);
        picker.setDateRangeStart(2017, 1, 1);
        picker.setDateRangeEnd(2020, 12, 12);
        picker.setSelectedItem(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        picker.setCanLinkage(false);
        picker.setTitleText("开始时间");
        picker.setWeightEnable(true);
        picker.setWheelModeEnable(true);
        LineConfig config = new LineConfig();
        config.setColor(Color.BLUE);//线颜色
        config.setAlpha(120);//线透明度
        picker.setLineConfig(config);
        picker.setLabel("年", "月", "日", "时", "分");
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                tv.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                tv.setTextColor(getResources().getColor(R.color.black));
                iv.setVisibility(View.GONE);
            }
        });
        picker.show();
    }

    //地址选择
    public void onAddressPicker(final TextView tv) {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideProvince(false);
        task.setHideCounty(false);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                Toast.makeText(mContext, "数据初始化失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                tv.setTextColor(getResources().getColor(R.color.black));
                if (county != null) {
                    tv.setText(province.getAreaName() + city.getAreaName() + county.getAreaName());
                } else {
                    tv.setText(province.getAreaName() + city.getAreaName());
                }
            }
        });
        task.execute("安徽", "合肥");
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

    private void getBundle(Bundle bundle) {
        if (bundle != null) {
            iv_addimg.setVisibility(View.GONE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defute_ll_back:
                finish();
                break;
            case R.id.ll_star_time:
                onYearMonthDayTimePicker(mTv_star, mIv_star);
                break;
            case R.id.ll_end_time:
                onYearMonthDayTimePicker(mTv_end, mIv_end);
                break;
            case R.id.tv_local:
                onAddressPicker(mTv_local);
                break;
            case R.id.iv_addimg:
                PermissionManager
                        .with(mContext)
                        .tag(1890)
                        .permission(PermissionEnum.WRITE_EXTERNAL_STORAGE, PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.CAMERA)
                        .callback(new PerimissionsCallback() {
                            @Override
                            public void onGranted(ArrayList<PermissionEnum> grantedList) {
                                //设置是否可以拍照
                                mCameraSdkParameterInfo.setShow_camera(true);
                                mCameraSdkParameterInfo.setMax_image(1);
                                Intent intent = new Intent();
                                intent.setClassName(getApplication(), "com.muzhi.camerasdk.PhotoPickActivity");
                                Bundle b = new Bundle();
                                b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
                                intent.putExtras(b);
                                startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);
                            }

                            @Override
                            public void onDenied(ArrayList<PermissionEnum> deniedList) {
                                PermissionDenied(deniedList);
                            }
                        }).checkAsk();

                break;
            case R.id.tv_commit: //提交图文直播主题
                if (Utils.isEmpty(mEditText_title.getText().toString())) {
                    promptDialog.showWarn("请输入标题");
                    return;
                }
                if (Utils.isEmpty(mTv_star.getText().toString()) || mTv_star.getText().equals("开始时间")) {
                    promptDialog.showWarn("请输入开始时间");
                    return;
                }
                if (Utils.isEmpty(mTv_end.getText().toString()) || mTv_end.getText().equals("开始时间")) {
                    promptDialog.showWarn("请输入结束时间");
                    return;
                }
                if (Utils.isEmpty(mEditText_zbf.getText().toString())) {
                    promptDialog.showWarn("请输入主办方");
                    return;
                }
                if (Utils.isEmpty(mTv_local.getText().toString()) || mTv_local.getText().equals("地点")) {
                    promptDialog.showWarn("请输入地点");
                    return;
                }
                if (Utils.isEmpty(mEditText_jj.getText().toString())) {
                    promptDialog.showWarn("请输入简介");
                    return;
                }
                if (mCameraSdkParameterInfo.getImage_list() == null || mCameraSdkParameterInfo.getImage_list().size() < 1) {
                    promptDialog.showWarn("选择封面");
                    return;
                }
                promptDialog.showLoading("正在提交...");
                request_data();
                break;
            case R.id.tv_preservation:
                if (Utils.isEmpty(mEditText_title.getText().toString())) {
                    promptDialog.showWarn("请输入标题");
                    return;
                }
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Constant.TITLE, mEditText_title.getText().toString());
                values.put(Constant.TIME, Utils.NowTime());
                values.put(Constant.STARTIME, mTv_star.getText().toString());
                values.put(Constant.ENDTIME, mTv_end.getText().toString());
                values.put(Constant.ADDRESS, mTv_local.getText().toString());
                values.put(Constant.SPONSOR, mEditText_zbf.getText().toString());
                values.put(Constant.SYNOPSIS, mEditText_jj.getText().toString());
                if (pic_list != null && pic_list.size() > 0) {
                    //将图集地址转成根据逗号分割String保存
                    String imgs = "";
                    for (int i = 0; i < pic_list.size(); i++) {
                        if (!pic_list.get(i).isAddButton) {
                            imgs += pic_list.get(i).getSource_image() + ",";
                        }
                    }
                    values.put(Constant.IMGS, imgs);
                }
                if (id == 0) {
                    //添加
                    long result = db.insert(Constant.TABLE_NAME, null, values);
                    if (result > 0) {
                        L.e("TAG", "----------添加成功-----------");
                        Toast.makeText(mContext, "保存成功,可在草稿箱中查看！", Toast.LENGTH_SHORT).show();
                    } else {
                        L.e("TAG", "----------添加失败-----------");
                        Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //修改内容
                    int count = db.update(Constant.TABLE_NAME, values, Constant.ID + "=" + id, null);
                    if (count > 0) {
                        L.e("TAG", "----------修改成功-----------");
                        Toast.makeText(mContext, "修改成功，可在草稿箱中查看！", Toast.LENGTH_SHORT).show();
                    } else {
                        L.e("TAG", "----------修改失败-----------");
                        Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
                db.close();
                finish();
                break;
        }
    }

    private void request_data() {
        Map<String, String> map = new HashMap<>();
        map.put("c", "vlive");
        map.put("f", "index");
        map.put("title", mEditText_title.getText().toString());
        map.put("sponsor", mEditText_zbf.getText().toString());
        map.put("address", mTv_local.getText().toString());
        map.put("content", mEditText_jj.getText().toString());
        map.put("stime", mTv_star.getText().toString());
        map.put("etime", mTv_end.getText().toString());
        Map<String, File> map1 = new HashMap<>();
        map1.put("thumb", new File(mCameraSdkParameterInfo.getImage_list().get(0)));
        App.getInstance().getmMyOkHttp().upload(mContext, Api.Index, map, map1, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                if (response != null) {
                    L.d("上传主题：", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("ok")) {
                            promptDialog.showSuccess("提交成功");
                            mHandler.sendEmptyMessageDelayed(1, 1200);
                        } else {
                            promptDialog.showError(object.getString("content"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        promptDialog.showError("提交失败");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                promptDialog.showError("请检查网络");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleResult(requestCode, permissions, grantResults);
    }

    private void PermissionDenied(final ArrayList<PermissionEnum> permissionsDenied) {
        StringBuilder msgCN = new StringBuilder();
        for (int i = 0; i < permissionsDenied.size(); i++) {
            if (i == permissionsDenied.size() - 1) {
                msgCN.append(permissionsDenied.get(i).getName_cn());
            } else {
                msgCN.append(permissionsDenied.get(i).getName_cn() + ",");
            }
        }
        if (mContext == null) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setMessage(String.format(mContext.getResources().getString(R.string.permission_explain), msgCN.toString()))
                .setCancelable(false)
                .setPositiveButton(R.string.per_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PerUtils.openApplicationSettings(mContext, R.class.getPackage().getName());
                    }
                })
                .setNegativeButton(R.string.per_cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        alertDialog.show();
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
