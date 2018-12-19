package com.github.ont.cyanowallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.main.MainFrameActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
        SDKWrapper.initOntSDK(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {

            }

            @Override
            public void onSDKFail(String tag, String message) {

            }
        },TAG,Constant.TEST_NET,sp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(baseActivity, MainFrameActivity.class));
                finish();
            }
        }, 3000);
    }
}
