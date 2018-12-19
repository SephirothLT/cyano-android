package com.github.ont.cyanowallet.main;

import android.app.Application;
import android.content.Context;

import com.github.ont.cyanowallet.network.VolleySingleton;

public class AppApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        VolleySingleton.setContext(mContext);
    }

    public static Context getContext() {
        return mContext;
    }
}
