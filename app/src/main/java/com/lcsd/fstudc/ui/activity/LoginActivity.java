package com.lcsd.fstudc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lcsd.fstudc.App;
import com.lcsd.fstudc.Api;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.moudle.LoginInfo;
import com.lcsd.fstudc.ui.moudle.UserInfo;
import com.lcsd.fstudc.utils.L;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.CleanableEditText;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import me.leefeng.promptlibrary.OnAdClickListener;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/5/21.
 */
public class LoginActivity extends BaseSionBarActivity implements View.OnClickListener {
    @BindView(R.id.login_ed1)
    CleanableEditText mEditText1;
    @BindView(R.id.login_ed2)
    CleanableEditText mEditText2;
    @BindView(R.id.login_ed3)
    CleanableEditText mEditText3;
    @BindView(R.id.login_ed4)
    CleanableEditText mEditText4;
    @BindView(R.id.login_radiobutton)
    CheckBox mCheckBox;
    @BindView(R.id.login_tv_hq)
    TextView mTv_hq;
    @BindView(R.id.login_tv_login)
    TextView mTv_login;
    private Context mContext;
    private PromptDialog mPromptDialog, mPromptDialogtip;
    private SharedPreferences mSharedPreferences;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_login;
    }


    @Override
    protected void initView() {
        mContext = this;
        mPromptDialog = new PromptDialog(this);
    }

    @Override
    protected void initData() {
        mPromptDialogtip = new PromptDialog(this);
        mSharedPreferences = getSharedPreferences("fsteditUser", MODE_PRIVATE);
        mCheckBox.setChecked(mSharedPreferences.getBoolean("isKeep", false));
        if (mSharedPreferences.getBoolean("isKeep", false)) {
            mEditText1.setText(mSharedPreferences.getString("code", ""));
            mEditText2.setText(mSharedPreferences.getString("name", ""));
            mEditText3.setText(mSharedPreferences.getString("pwd", ""));
            mEditText4.setText(mSharedPreferences.getString("phone", ""));
        } else {//否则显示为空
            mEditText1.setText("");
            mEditText2.setText("");
            mEditText3.setText("");
            mEditText4.setText("");
        }
    }

    @Override
    protected void setListener() {
        mTv_hq.setOnClickListener(this);
        mTv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_hq:
                mPromptDialog.getDefaultBuilder().backAlpha(300);
                Glide.with(mContext).load(Api.GetAccountImg)
                        .into(mPromptDialog.showAd(true, new OnAdClickListener() {
                            @Override
                            public void onAdClick() {
                                Toast.makeText(mContext, "点击了广告", Toast.LENGTH_SHORT).show();
                            }
                        }));
                break;
            case R.id.login_tv_login:
                if (Utils.isEmpty(mEditText1.getText().toString())) {
                    mPromptDialogtip.showWarn("请输入机构代码！");
                    return;
                }
                if (Utils.isEmpty(mEditText2.getText().toString())) {
                    mPromptDialogtip.showWarn("请输入用户名！");
                    return;
                }
                if (Utils.isEmpty(mEditText3.getText().toString())) {
                    mPromptDialogtip.showWarn("请输入密码！");
                    return;
                }
                if (Utils.isEmpty(mEditText4.getText().toString())) {
                    mPromptDialogtip.showWarn("请输入手机号！");
                    return;
                }
                mPromptDialogtip.showLoading("正在登录");
                Map<String, String> map = new HashMap<>();
                map.put("c", "login");
                map.put("user", mEditText2.getText().toString());
                map.put("pass", mEditText3.getText().toString());
                map.put("codeword", mEditText1.getText().toString());
                map.put("admintele", mEditText4.getText().toString());
                App.getInstance().getmMyOkHttp().post(mContext, Api.Index, map, new RawResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, String response) {
                        if (response != null) {
                            LoginInfo loginInfo= JSON.parseObject(response,LoginInfo.class);
                            if(loginInfo.getStatus().equals("ok")){
                                request_information();
                                mPromptDialogtip.showSuccess("登陆成功");
                                if (mCheckBox.isChecked()) {
                                    SharedPreferences.Editor edit = mSharedPreferences.edit();//得到Editor对象
                                    edit.putBoolean("isKeep", true);//记录保存标记
                                    edit.putString("code", mEditText1.getText().toString());//机构代码
                                    edit.putString("name", mEditText2.getText().toString());//用户名
                                    edit.putString("pwd", mEditText3.getText().toString());//密码
                                    edit.putString("phone", mEditText4.getText().toString());//手机号
                                    edit.commit();//**提交
                                } else {
                                    SharedPreferences.Editor edit = mSharedPreferences.edit();//得到Editor对象
                                    edit.putBoolean("isKeep", false);//记录保存标记
                                    edit.putString("code", "");
                                    edit.putString("name", "");
                                    edit.putString("pwd", "");
                                    edit.putString("phone", "");
                                    edit.commit();//**提交
                                }
                                finish();
                                startActivity(new Intent(mContext,MainActivity.class));
                            }else {
                                mPromptDialogtip.showError("登录失败");
                            }
                        }else {
                            mPromptDialogtip.showError("登录失败");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        mPromptDialogtip.showWarn("请检查网络");
                    }
                });
                break;
        }
    }
    //管理员信息
    private void request_information() {
        Map<String, String> map = new HashMap<>();
        map.put("c", "usercp");
        App.getInstance().getmMyOkHttp().post(mContext, Api.Index, map, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                if (response != null) {
                    L.d("管理员信息：", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("ok")) {
                            UserInfo info = JSON.parseObject(object.getString("content"), UserInfo.class);
                            App.getInstance().saveUserInfo(info);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {

            }
        });
    }

    //返回键的处理仅是Alert 或AlertSheet的关闭操作
    @Override
    public void onBackPressed() {
        if (mPromptDialog.onBackPressed())
            super.onBackPressed();
    }
}
