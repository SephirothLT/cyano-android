package com.github.ont.cyanowallet.mine;

import android.os.Bundle;
import android.view.View;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;

public class SettingNetActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_net);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
        }
    }
}
