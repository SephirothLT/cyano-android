/*
 * *************************************************************************************
 *   Copyright © 2014-2018 Ontology Foundation Ltd.
 *   All rights reserved.
 *
 *   This software is supplied only under the terms of a license agreement,
 *   nondisclosure agreement or other written agreement with Ontology Foundation Ltd.
 *   Use, redistribution or other disclosure of any parts of this
 *   software is prohibited except in accordance with the terms of such written
 *   agreement with Ontology Foundation Ltd. This software is confidential
 *   and proprietary information of Ontology Foundation Ltd.
 *
 * *************************************************************************************
 */

package com.github.ont.cyanowallet.scan.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.alibaba.fastjson.JSONArray;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.ScanInvokeCallbackReq;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SettingSingleton;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ont.cyanowallet.view.PasswordDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScanWalletInvokeActivity extends BaseActivity implements View.OnClickListener {

    TextView name;
    TextView fromAddress;

    private static final String TAG = "ScanWalletLoginActivity";

    private PasswordDialog passwordDialog;
    private String address;
    private String qrcodeUrl;
    private String callback;
    private ScanInvokeCallbackReq scanInvokeCallbackReq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wallet_invoke);
        initData();
        initView();
    }

    private void initView() {
        View confirm = findViewById(R.id.confirm);
        View layout_back = findViewById(R.id.layout_back);
        fromAddress = findViewById(R.id.from_address);
        confirm.setOnClickListener(this);
        layout_back.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            String data = bundle.getString(Constant.KEY, "");
//            "login": true,
//                    "qrcodeUrl": "http://101.132.193.149:4027/qrcode/AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ",
//                    "message": "will pay 1 ONT in this transaction",
            address = bundle.getString(Constant.ADDRESS, "");
            fromAddress.setText(address);
            try {
                JSONObject jsonObject = new JSONObject(data);
                qrcodeUrl = jsonObject.getString("qrcodeUrl");
                callback = jsonObject.getString("callback");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void showSecretDialog() {
        if (passwordDialog != null && passwordDialog.isShowing()) {
            passwordDialog.dismiss();
        }
        if (passwordDialog == null) {
            passwordDialog = new PasswordDialog(this);
            passwordDialog.setConfirmListener(new PasswordDialog.ConfirmListener() {
                @Override
                public void passwordConfirm(String password) {
                    passwordDialog.dismiss();
                    showLoading();
//                    TODO
                    req(password);
                }
            });
        }
        passwordDialog.show();
    }

    private void req(String password) {
        SDKWrapper.scanAddSign(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, final Object message) {

                ArrayList<String> result = (ArrayList<String>) message;
                if (result != null && result.size() > 1) {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject((String) result.get(0));
                    JSONArray notify = jsonObject.getJSONArray("Notify");

                    if (notify != null && notify.size() > 0) {
                        dismissLoading();
                        showChooseDialog((String) result.get(0), (String) result.get(1));
                    } else {
                        sendTran((String) result.get(1));
                    }
                }
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                String error = null;
                try {
                    int errorCode = new JSONObject(message).optInt("Error");
                    switch (errorCode) {
                        case 51015:
                        case 58018:
                            error = "password error";
                            break;
                        case 58004:
                            error = "address error";
                            break;
                        case 47001:
                            error = "insufficient balance";
                            break;
                        default:
                            error = "system error " + errorCode;
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error == null) {
                    error = "system error ";
                }
                showAttention(error);
            }
        }, TAG, qrcodeUrl, address, password);
    }

    private Dialog chooseDialog;

    private void showChooseDialog(String showMessage, final String transactionHex) {
        chooseDialog = new Dialog(this, R.style.dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_choose, null);
        TextView tvContent = (TextView) inflate.findViewById(R.id.tv_content);
        TextView tv_pay = (TextView) inflate.findViewById(R.id.tv_pay);
        TextView tv_address_from = (TextView) inflate.findViewById(R.id.tv_address_from);
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(showMessage);
        JSONArray notify = jsonObject.getJSONArray("Notify");

        if (notify != null && notify.size() > 0) {
            for (int i = 0; i < notify.size(); i++) {
                JSONArray states = notify.getJSONObject(i).getJSONArray("States");
                String contractAddress = notify.getJSONObject(i).getString("ContractAddress");
                if (TextUtils.equals(states.getString(0), "transfer") && TextUtils.equals(states.getString(1), address)) {
                    if (TextUtils.equals(contractAddress.substring(0, 2), "01")) {
                        tv_pay.setText(String.format("%s ONT", states.getLong(3)));
                        tvContent.setText(states.getString(2));
                        tv_address_from.setText(address);
                    } else if (TextUtils.equals(contractAddress.substring(0, 2), "02")) {
                        tv_pay.setText(String.format("%s ONG", CommonUtil.formatONG(states.getLong(3) + "")));
                        tvContent.setText(states.getString(2));
                        tv_address_from.setText(address);
                    }
                }
            }

        }
        TextView tvSure = (TextView) inflate.findViewById(R.id.tv_sure);
        TextView tvCancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseDialog != null) {
                    chooseDialog.dismiss();
                }
                dismissLoading();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseDialog != null) {
                    chooseDialog.dismiss();
                }
                showLoading();
                sendTran(transactionHex);
            }
        });
        chooseDialog.setContentView(inflate);
        if (chooseDialog != null && !chooseDialog.isShowing()) {
            chooseDialog.show();
        }
    }

    private void sendTran(String transactionHex) {
        SDKWrapper.sendTransactionHex(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                String txHash = (String) message;
                if (!TextUtils.isEmpty(txHash)) {
                    if (TextUtils.isEmpty(callback)) {
                        dismissLoading();
                        ToastUtil.showToast(ScanWalletInvokeActivity.this, "Success");
                        finish();
                    } else {
                        JSONObject jsonObject1 = new JSONObject();
                        try {
                            jsonObject1.put("hash", txHash);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        scanInvokeCallbackReq = new ScanInvokeCallbackReq(callback, jsonObject1);
                        scanInvokeCallbackReq.setOnResultListener(new BaseRequest.ResultListener() {
                            @Override
                            public void onResult(Result result) {
                                dismissLoading();
                                ToastUtil.showToast(ScanWalletInvokeActivity.this, "success");
                                finish();
                            }

                            @Override
                            public void onResultFail(Result error) {
                                dismissLoading();
                                ToastUtil.showToast(ScanWalletInvokeActivity.this, "success");
                                finish();
                            }
                        });
                        scanInvokeCallbackReq.excute();
                    }
                } else {
                    dismissLoading();
                    ToastUtil.showToast(ScanWalletInvokeActivity.this, "fail");
                }
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                ToastUtil.showToast(ScanWalletInvokeActivity.this, "fail" + " : " + message);
            }
        }, TAG, transactionHex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (passwordDialog != null) {
            passwordDialog.dismiss();
        }
        if (chooseDialog != null) {
            chooseDialog.dismiss();
        }
        if (scanInvokeCallbackReq != null) {
            scanInvokeCallbackReq.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                showLoading();
                showSecretDialog();
                break;
            case R.id.layout_back:
                finish();
                break;
            default:
        }
    }
}
