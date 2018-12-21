/*
 * **************************************************************************************
 *  Copyright © 2014-2018 Ontology Foundation Ltd.
 *  All rights reserved.
 *
 *  This software is supplied only under the terms of a license agreement,
 *  nondisclosure agreement or other written agreement with Ontology Foundation Ltd.
 *  Use, redistribution or other disclosure of any parts of this
 *  software is prohibited except in accordance with the terms of such written
 *  agreement with Ontology Foundation Ltd. This software is confidential
 *  and proprietary information of Ontology Foundation Ltd.
 *
 * **************************************************************************************
 */

package com.github.ont.cyanowallet.game;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ont.cyanowallet.view.PasswordDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/17.
 */
public class GameWebActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "SocialMediaWebview";
    protected String mUrl = "";
    ProgressBar pg;
    FrameLayout frameLayout;

    private LinearLayout layoutBack;
    private LinearLayout layoutFinish;
    private WebView mWebView = null;
    private boolean mActivityDestroyed = false;

    private static final String BRIDGE_NAME = "android";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_web);

        initView();
        initData();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        frameLayout.addView(mWebView);
        initWebView();

        mWebView.loadUrl(mUrl);

    }

    private void initView() {
        pg = findViewById(R.id.progress_loading);
        frameLayout = findViewById(R.id.frame);
        layoutBack = findViewById(R.id.layout_back);
        layoutFinish = findViewById(R.id.layout_finish);
        layoutBack.setOnClickListener(this);
        layoutFinish.setOnClickListener(this);
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUrl = extras.getString(Constant.KEY);
        }
    }

    private void initWebView() {
        final WebSettings webSetting = mWebView.getSettings();
        //if your build is in debug mode, enable inspecting of webviews
//        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                WebView.setWebContentsDebuggingEnabled(true);
//            }
//        }
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        // 开启javascript设置
        webSetting.setJavaScriptEnabled(true);
//        webSetting.setAllowFileAccess(true);
//        webSetting.setSupportMultipleWindows(true);
        // 应用可以有缓存
//        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
//        webSetting.setUseWideViewPort(true);
//        webSetting.setAppCacheEnabled(false);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setAppCachePath(appCacheDir);
//        webSetting.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小，我设的是8M
        // 设置可以使用localStorage
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//            webSetting.setAllowUniversalAccessFromFileURLs(true);
//        }
// 特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
//• Ensure that all WebViews call:
        webSetting.setAllowFileAccess(false);
//        • Consider calling:
        webSetting.setAllowFileAccessFromFileURLs(false);

        webSetting.setAllowContentAccess(false);
        webSetting.setDomStorageEnabled(true);
        mWebView.addJavascriptInterface(new NativeJsBridge(), BRIDGE_NAME);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                showPayDialog(message);
                String[] split = message.split("params=");
                handleAction(split[split.length - 1]);
                if (result != null) {
                    result.confirm("");
                }
                if (result != null) {
                    result.cancel();
                }
// 返回布尔值：判断点击时确认还是取消
// true表示点击了确认；false表示点击了取消
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    pg.setVisibility(View.GONE);
                } else {
                    pg.setVisibility(View.VISIBLE);
                    pg.setProgress(newProgress);
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Android8.0以下的需要返回true 并且需要loadUrl；8.0之后效果相反
                if (Build.VERSION.SDK_INT < 26) {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mActivityDestroyed) {
                    return;
                }
                linkBridge();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                error.getErrorCode()
//                加载本地失败的界面
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //https
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });

    }

    private void linkBridge() {
        String js = "window.originalPostMessage = window.postMessage;" + "window.postMessage = function(data) {" + BRIDGE_NAME + ".postMessage(data);}";
        evaluateJavascriptWithFallback(js);
    }

    protected void evaluateJavascriptWithFallback(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(script, null);
            return;
        }

        try {
            mWebView.loadUrl("javascript:" + URLEncoder.encode(script, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // UTF-8 should always be supported
            throw new RuntimeException(e);
        }
    }

    private class NativeJsBridge {
        @JavascriptInterface
        public void postMessage(String userInfo) {
            Log.i(TAG, "register: " + userInfo);
            if (userInfo.contains("ontprovider://ont.io")) {
                String[] split = userInfo.split("params=");
                handleAction(split[split.length - 1]);
            }
        }

    }

    private void handleAction(String message) {

        byte[] decode = Base64.decode(message, Base64.NO_WRAP);
        String result = Uri.decode(new String(decode));
//        {"action":"login","params":{"type":"account","dappName":"My dapp","message":"test message","expired":"201812181000","callback":""}}
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            String action = jsonObject.getString("action");
            switch (action) {
                case "login":
                case "invoke":
                    showDialog(result);
                    break;
                case "getAccount":
                    getAccount(jsonObject);
                    break;
                default:
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getAccount(JSONObject reqJson) {
//        {
//            action    string   // action
//            version   string   // version
//            error     int      // error code
//            desc      string   // desc of error code
//            result    string   // result
//        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", reqJson.getString("action"));
            jsonObject.put("version", reqJson.getString("version"));
            jsonObject.put("error", 0);
            jsonObject.put("desc", "");
            jsonObject.put("result", SPWrapper.getDefaultAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String s = Base64.encodeToString(Uri.encode(jsonObject.toString()).getBytes(), Base64.NO_WRAP);
//        mWebView.loadUrl("javascript:emitMessage(\"" + s + "\")");
//        mWebView.loadUrl("javascript:" + BRIDGE_NAME + ".postMessage(\"" + s + "\")");
        sendBack(s);
    }

    private PasswordDialog passwordDialog;

    //显示付款
    private void showDialog(final String message) {
        if (passwordDialog != null && passwordDialog.isShowing()) {
            passwordDialog.dismiss();
        }
        passwordDialog = new PasswordDialog(this);
        passwordDialog.setConfirmListener(new PasswordDialog.ConfirmListener() {
            @Override
            public void passwordConfirm(String password) {
                passwordDialog.dismiss();
                showLoading();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(message);
                    String action = jsonObject.getString("action");
                    switch (action) {
                        case "login":
                            handleLogin(jsonObject.getJSONObject("params").getString("message"), password);
                            break;
                        case "invoke":
                            handleInvokeTransaction(message, password);
                            break;
                        default:
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        passwordDialog.show();
    }

    private void handleInvokeTransaction(String data, String password) {
        SDKWrapper.getSendAddress(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                dismissLoading();
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
                showAttention((String) message);
            }
        }, TAG, data, password, SPWrapper.getDefaultAddress());
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
                if (TextUtils.equals(states.getString(0), "transfer") && TextUtils.equals(states.getString(1), SPWrapper.getDefaultAddress())) {
                    if (TextUtils.equals(contractAddress.substring(0, 2), "01")) {
                        tv_pay.setText(String.format("%s ONT", states.getLong(3)));
                        tvContent.setText(states.getString(2));
                        tv_address_from.setText(SPWrapper.getDefaultAddress());
                    } else if (TextUtils.equals(contractAddress.substring(0, 2), "02")) {
                        tv_pay.setText(String.format("%s ONG", CommonUtil.formatONG(states.getLong(3) + "")));
                        tvContent.setText(states.getString(2));
                        tv_address_from.setText(SPWrapper.getDefaultAddress());
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
                dismissLoading();
                String txHash = (String) message;
                if (!TextUtils.isEmpty(txHash)) {
                    ToastUtil.showToast(GameWebActivity.this, "Success");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("action", "invoke");
                        jsonObject.put("version", "v1.0.0");
                        jsonObject.put("error", 0);
                        jsonObject.put("desc", "SUCCESS");
                        jsonObject.put("result", txHash);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String s = Base64.encodeToString(Uri.encode(jsonObject.toString()).getBytes(), Base64.NO_WRAP);
//                    mWebView.loadUrl("javascript:emitMessage(\"" + s + "\")");
//                    mWebView.loadUrl("javascript:" + BRIDGE_NAME + ".postMessage(\"" + s + "\")");
                    sendBack(s);
                } else {
                    ToastUtil.showToast(GameWebActivity.this, "Fail");
                }
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                ToastUtil.showToast(GameWebActivity.this, " Fail : " + message);
            }
        }, TAG, transactionHex);
    }

    private void handleLogin(String data, String password) {
        SDKWrapper.getGameLogin(new SDKCallback() {
            @Override
            public void onSDKSuccess(String tag, Object message) {
                String result = (String) message;
                final String s = Base64.encodeToString(Uri.encode(result).getBytes(), Base64.NO_WRAP);
//                mWebView.loadUrl("javascript:emitMessage(\"" + s + "\")");
//                mWebView.loadUrl("javascript:" + BRIDGE_NAME + ".postMessage(\"" + s + "\")");
//                evaluateJavascriptWithFallback(BRIDGE_NAME + ".postMessage(\"" + s + "\")");
                sendBack(s);
                dismissLoading();
            }

            @Override
            public void onSDKFail(String tag, String message) {
                dismissLoading();
                ToastUtil.showToast(GameWebActivity.this, message);
            }
        }, TAG, password, data);
    }

    private void sendBack(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject eventInitDict = new JSONObject();
                    eventInitDict.put("data", data);
                    evaluateJavascriptWithFallback("(function () {" + "var event;" + "var data = " + eventInitDict.toString() + ";" + "try {" + "event = new MessageEvent('message', data);" + "} catch (e) {" + "event = document.createEvent('MessageEvent');" + "event.initMessageEvent('message', true, true, data.data, data.origin, data.lastEventId, data.source);" + "}" + "document.dispatchEvent(event);" + "})();");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 停止webview的加载
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.i("ansen","是否有上一个页面:"+webView.canGoBack());
        if (mWebView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
            mWebView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mActivityDestroyed = true;
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                if (mWebView.canGoBack()) {//点击返回按钮的时候判断有没有上一页
                    mWebView.goBack(); // goBack()表示返回webView的上一页面
                } else {
                    finish();
                }
                break;
            case R.id.layout_finish:
                finish();
                break;
            default:
        }
    }
}
