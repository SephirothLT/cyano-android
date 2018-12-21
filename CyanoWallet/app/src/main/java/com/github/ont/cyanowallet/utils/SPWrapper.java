package com.github.ont.cyanowallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.ont.cyanowallet.main.AppApplication;

public class SPWrapper {
    public static SharedPreferences getSharedPreferences() {
        return AppApplication.getContext().getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
    }

    public static String getDefaultAddress() {
        return getSharedPreferences().getString(Constant.DEFAULT_ADDRESS, "");
    }

    public static void setDefaultAddress(String address) {
        getSharedPreferences().edit().putString(Constant.DEFAULT_ADDRESS, address).apply();
    }

    public static String getDefaultNet() {
        return getSharedPreferences().getString(Constant.DEFAULT_NET, "");
    }

    public static void setDefaultNet(String address) {
        getSharedPreferences().edit().putString(Constant.DEFAULT_NET, address).apply();
    }


}
