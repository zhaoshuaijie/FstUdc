package com.lcsd.fstudc.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lcsd.fstudc.R;

import me.leefeng.promptlibrary.PromptDialog;

/**
 * Created by jie on 2018/5/22.
 */
public abstract class BaseDialogactivity extends BaseSionBarActivity {
    private PromptDialog promptDialog;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promptDialog = new PromptDialog(this);
    }

    public void showSuccess(String s) {
        promptDialog.showSuccess(s);
    }

    public void showError(String s) {
        promptDialog.showError(s);
    }

    public void showWarn(String s) {
        promptDialog.showWarn(s);
    }
    public TextView showProgressDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View view = View.inflate(this, R.layout.dialog_loading, null);
        builder.setView(view);
        ProgressBar pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        TextView tv_hint = (TextView) view.findViewById(R.id.tv_hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pb_loading.setIndeterminateTintList(ContextCompat.getColorStateList(this, R.color.blue));
        }
        tv_hint.setText(text);
        progressDialog = builder.create();
        progressDialog.show();

        return tv_hint;
    }

    public void closeProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
