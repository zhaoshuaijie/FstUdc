package com.lcsd.fstudc.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.CleanableEditText;
import com.lcsd.fstudc.xrichtext.RichTextEditor;
import com.muzhi.camerasdk.model.CameraSdkParameterInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jie on 2018/6/11.
 */
public class NewManuscriptActivity extends BaseDialogactivity implements View.OnClickListener, RichTextEditor.OnDeleteImageListener {
    @BindView(R.id.top_view)
    View mView;
    @BindView(R.id.defute_ll_back)
    LinearLayout mLl_back;
    @BindView(R.id.defute_tv_title)
    TextView mTv_title;
    @BindView(R.id.defute_tv_right)
    TextView mTv_right;
    @BindView(R.id.add_pic)
    ImageView mIV_add;
    @BindView(R.id.tv_bc)
    TextView mTv_bc;
    @BindView(R.id.et_new_content)
    RichTextEditor mRichTextEditor;
    @BindView(R.id.et_new_title)
    CleanableEditText mCEditTitle;
    @BindView(R.id.tv_new_time)
    TextView mTv_time;
    private Context mContext;
    private CameraSdkParameterInfo mCameraSdkParameterInfo;
    private Subscription subsInsert;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_new_manuscript;
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
        mTv_right.setVisibility(View.VISIBLE);
        mTv_title.setText("新建文稿");
        mTv_right.setText("上传");
        mCameraSdkParameterInfo = new CameraSdkParameterInfo();
    }

    @Override
    protected void initData() {
        mTv_time.setText(Utils.NowTime());
    }

    @Override
    protected void setListener() {
        mLl_back.setOnClickListener(this);
        mTv_right.setOnClickListener(this);
        mIV_add.setOnClickListener(this);
        mTv_bc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_pic:
                Intent intent = new Intent();
                intent.setClassName(mContext, "com.muzhi.camerasdk.PhotoPickActivity");
                Bundle b = new Bundle();
                b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
                intent.putExtras(b);
                startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);
                break;
            case R.id.tv_bc:

                break;
            case R.id.defute_ll_back:
                finish();
                break;
            case R.id.defute_tv_right:

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY) {
            if (data != null) {
                insertImagesSync(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 异步方式插入图片
     *
     * @param data
     */
    private void insertImagesSync(final Intent data) {
        showProgressDialog("正在插入图片...");

        subsInsert = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    mRichTextEditor.measure(0, 0);
                    mCameraSdkParameterInfo = (CameraSdkParameterInfo) data.getExtras().getSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER);
                    ArrayList<String> mSelected = mCameraSdkParameterInfo.getImage_list();
                    //可以同时插入多张图片
                    for (String imagePath : mSelected) {
                        subscriber.onNext(imagePath);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                mCameraSdkParameterInfo = new CameraSdkParameterInfo();
            }
        }).onBackpressureBuffer()
                .subscribeOn(Schedulers.io())//生产事件在io
                .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        closeProgressDialog();
                        showToast("图片插入成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeProgressDialog();
                        showToast("图片插入失败:" + e.getMessage());
                    }

                    @Override
                    public void onNext(String imagePath) {
                        mRichTextEditor.insertImage(imagePath, mRichTextEditor.getMeasuredWidth());
                    }
                });
    }

    @Override
    public void onDeleteImage(String imagePath) {

    }
}
