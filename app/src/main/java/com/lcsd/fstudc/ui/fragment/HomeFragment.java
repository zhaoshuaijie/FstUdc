package com.lcsd.fstudc.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lcsd.fstudc.R;
import com.lcsd.fstudc.permissions.PerUtils;
import com.lcsd.fstudc.permissions.PerimissionsCallback;
import com.lcsd.fstudc.permissions.PermissionEnum;
import com.lcsd.fstudc.permissions.PermissionManager;
import com.lcsd.fstudc.ui.activity.AudioSelectActivity;
import com.lcsd.fstudc.ui.activity.CameraActivity;
import com.lcsd.fstudc.ui.activity.ImageUploadActivity;
import com.lcsd.fstudc.ui.activity.LiveImageListActivity;
import com.lcsd.fstudc.ui.activity.ManuscriptListActivity;
import com.muzhi.camerasdk.model.CameraSdkParameterInfo;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by jie on 2018/5/21.
 */
public class HomeFragment extends BaseFragment {
    @BindView(R.id.home_iv1)
    ImageView mIv1;
    @BindView(R.id.home_iv2)
    ImageView mIv2;
    @BindView(R.id.home_iv3)
    ImageView mIv3;
    @BindView(R.id.home_iv4)
    ImageView mIv4;
    @BindView(R.id.home_iv5)
    ImageView mIv5;
    @BindView(R.id.home_iv6)
    ImageView mIv6;
    private Context mContext;
    private CameraSdkParameterInfo mCameraSdkParameterInfo;

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        mContext = getActivity();
        mCameraSdkParameterInfo = new CameraSdkParameterInfo();
    }

    @Override
    protected void setListener() {
        mIv1.setOnClickListener(mOnClickListener);
        mIv2.setOnClickListener(mOnClickListener);
        mIv3.setOnClickListener(mOnClickListener);
        mIv4.setOnClickListener(mOnClickListener);
        mIv5.setOnClickListener(mOnClickListener);
        mIv6.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY) {
            if (data != null) {
                if (data != null) {
                    Intent intent = new Intent(mContext, ImageUploadActivity.class);
                    intent.putExtras(data.getExtras());
                    startActivity(intent);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.home_iv1:
                    checkPermissions(1, PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.WRITE_EXTERNAL_STORAGE, PermissionEnum.CAMERA);
                    break;
                case R.id.home_iv2:
                    //多选or单选 默认多选
                    mCameraSdkParameterInfo.setSingle_mode(false);
                    //设置是否可以拍照
                    mCameraSdkParameterInfo.setShow_camera(true);
                    //设置可选图片数
                    mCameraSdkParameterInfo.setMax_image(9);
                    //设置是否使用编辑功能
                    mCameraSdkParameterInfo.setFilter_image(true);

                    checkPermissions(2, PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.WRITE_EXTERNAL_STORAGE, PermissionEnum.CAMERA);
                    break;
                case R.id.home_iv3:
                    checkPermissions(3, PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.WRITE_EXTERNAL_STORAGE, PermissionEnum.RECORD_AUDIO);
                    break;
                case R.id.home_iv4:
                    checkPermissions(4, PermissionEnum.WRITE_EXTERNAL_STORAGE, PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.CAMERA, PermissionEnum.RECORD_AUDIO);
                    break;
                case R.id.home_iv5:
                    Toast.makeText(mContext, "暂无绑定", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.home_iv6:
                    startActivity(new Intent(mContext, LiveImageListActivity.class));
                    break;
            }
        }
    };

    /**
     * 权限检查
     */
    private void checkPermissions(final int pos, PermissionEnum... permissions) {
        PermissionManager
                .with(mContext)
                .tag(1867)
                .permission(permissions)
                .callback(new PerimissionsCallback() {
                    @Override
                    public void onGranted(ArrayList<PermissionEnum> grantedList) {
                        //权限通过
                        switch (pos) {
                            case 1:
                                startActivity(new Intent(mContext, ManuscriptListActivity.class));
                                break;
                            case 2:
                                Intent intent = new Intent();
                                intent.setClassName(mContext, "com.muzhi.camerasdk.PhotoPickActivity");
                                Bundle b = new Bundle();
                                b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
                                intent.putExtras(b);
                                startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);
                                break;
                            case 3:
                                startActivity(new Intent(mContext, AudioSelectActivity.class));
                                break;
                            case 4:
                                startActivity(new Intent(mContext, CameraActivity.class));
                                mActivity.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                                break;
                        }
                    }

                    @Override
                    public void onDenied(ArrayList<PermissionEnum> deniedList) {
                        PermissionDenied(deniedList);
                    }
                }).checkAsk();
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


}
