package com.github.ont.cyanowallet.mine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.ont.cyanowallet.R;

public class SettingNetActivity extends AppCompatActivity implements View.OnClickListener {

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
