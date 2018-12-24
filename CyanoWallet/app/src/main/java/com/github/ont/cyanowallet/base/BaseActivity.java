package com.github.ont.cyanowallet.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ont.cyanowallet.view.PasswordDialog;

public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "BaseActivity";

    private Dialog loadingDialog;
    public Activity baseActivity;
    private PasswordDialog passwordDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        baseActivity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
    }

    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
            loadingDialog.setContentView(inflate);
        }
        if (loadingDialog.isShowing()) {
            return;
        }
        loadingDialog.show();
    }

    public void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private Dialog dialogAttention;
    private TextView dialogContent = null;
    private ImageView imgLogo = null;

    public void showAttention(int content, int drawResource) {
        if (dialogAttention == null) {
            dialogAttention = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_attention, null);
            TextView dialogClose = (TextView) inflate.findViewById(R.id.img_close);
            imgLogo = (ImageView) inflate.findViewById(R.id.img_logo);
            dialogContent = (TextView) inflate.findViewById(R.id.tv_content);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttention != null) {
                        dialogAttention.dismiss();
                    }
                }
            });
            dialogAttention.setContentView(inflate);
        }
        if (dialogContent != null) {
            dialogContent.setText(content);
        }
        if (imgLogo != null) {
            imgLogo.setImageResource(drawResource);
        }
        if (dialogAttention != null && !dialogAttention.isShowing()) {
            dialogAttention.show();
        }
    }

    public void showAttention(String content, int drawResource) {
        if (dialogAttention == null) {
            dialogAttention = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_attention, null);
            TextView dialogClose = (TextView) inflate.findViewById(R.id.img_close);
            imgLogo = (ImageView) inflate.findViewById(R.id.img_logo);
            dialogContent = (TextView) inflate.findViewById(R.id.tv_content);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttention != null) {
                        dialogAttention.dismiss();
                    }
                }
            });
            dialogAttention.setContentView(inflate);
        }
        if (dialogContent != null) {
            dialogContent.setText(content);
        }
        if (imgLogo != null) {
            imgLogo.setImageResource(drawResource);
        }
        if (dialogAttention != null && !dialogAttention.isShowing()) {
            dialogAttention.show();
        }
    }

    public void showAttention(int content) {
        if (dialogAttention == null) {
            dialogAttention = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_attention, null);
            TextView dialogClose = (TextView) inflate.findViewById(R.id.img_close);
            imgLogo = (ImageView) inflate.findViewById(R.id.img_logo);
            dialogContent = (TextView) inflate.findViewById(R.id.tv_content);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttention != null) {
                        dialogAttention.dismiss();
                    }
                }
            });
            dialogAttention.setContentView(inflate);
        }
        if (dialogContent != null) {
            dialogContent.setText(content);
        }
        if (dialogAttention != null && !dialogAttention.isShowing()) {
            dialogAttention.show();
        }
    }

    public void showAttention(String content) {
        if (dialogAttention == null) {
            dialogAttention = new Dialog(this, R.style.dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_attention, null);
            TextView dialogClose = (TextView) inflate.findViewById(R.id.img_close);
            imgLogo = (ImageView) inflate.findViewById(R.id.img_logo);
            dialogContent = (TextView) inflate.findViewById(R.id.tv_content);
            dialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttention != null) {
                        dialogAttention.dismiss();
                    }
                }
            });
            dialogAttention.setContentView(inflate);
        }
        if (dialogContent != null) {
            dialogContent.setText(content);
        }
        if (dialogAttention != null && !dialogAttention.isShowing()) {
            dialogAttention.show();
        }
    }

    private GetDialogPassword getDialogPassword;

    public void setGetDialogPwd(GetDialogPassword getDialogPwd) {
        this.getDialogPassword = getDialogPwd;
    }

    //显示付款
    public void showPasswordDialog() {
        if (getDialogPassword == null) {
            ToastUtil.showToast(baseActivity, "System error ,Please restart");
            return;
        }
        if (passwordDialog != null && passwordDialog.isShowing()) {
            return;
        }
        passwordDialog = new PasswordDialog(this);
        passwordDialog.setConfirmListener(new PasswordDialog.ConfirmListener() {
            @Override
            public void passwordConfirm(String password) {
                passwordDialog.dismiss();
                if (getDialogPassword != null) {
                    getDialogPassword.handleDialog(password);
                }
            }
        });
        passwordDialog.show();
    }

    //隐藏付款
    public void dismissPwdDialog() {
        if (passwordDialog != null) {
            passwordDialog.dismiss();
        }
    }

    public interface GetDialogPassword {
        public void handleDialog(String pwd);
    }

    public void copyAddress(String data, String des) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) baseActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("key", data);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
        showAttention(des);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
        dismissPwdDialog();
    }
}
