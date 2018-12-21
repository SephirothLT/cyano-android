package com.github.ont.cyanowallet.wallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.BalanceReq;
import com.github.ont.cyanowallet.utils.CommonUtil;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;

public class WalletFragment extends BaseFragment implements View.OnClickListener, BaseRequest.ResultListener {
    private static final String TAG = "WalletFragment";

    private LinearLayout layoutHasWallet;
    private LinearLayout layoutNoWallet;
    private TextView tvOnt;
    private TextView tvOng;
    private TextView tvClaim;
    private BalanceReq balanceReq;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutNoWallet = (LinearLayout) view.findViewById(R.id.layout_no_wallet);
        layoutHasWallet = (LinearLayout) view.findViewById(R.id.layout_has_wallet);
        initViewNoWallet(view);
        initViewHasWallet(view);
    }

    private void initViewHasWallet(View view) {
        TextView tvAddress = (TextView) view.findViewById(R.id.tv_address);
        tvOnt = (TextView) view.findViewById(R.id.tv_ont);
        tvOng = (TextView) view.findViewById(R.id.tv_ong);
        tvClaim = (TextView) view.findViewById(R.id.tv_claim);
        Button btnSend = (Button) view.findViewById(R.id.btn_send);
        Button btnReceiver = (Button) view.findViewById(R.id.btn_receiver);
        Button btnRecord = (Button) view.findViewById(R.id.btn_record);
        Button btnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        tvAddress.setText(SPWrapper.getDefaultAddress());

        btnSend.setOnClickListener(this);
        btnReceiver.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
    }

    public void refresh() {
        baseActivity.showLoading();
        if (balanceReq != null) {
            balanceReq.cancel();
        }
        balanceReq = new BalanceReq(SPWrapper.getDefaultAddress());
        balanceReq.setRequestTag(TAG);
        balanceReq.setOnResultListener(this);
        balanceReq.excute();
    }

    private void initViewNoWallet(View view) {
        Button btnNew = (Button) view.findViewById(R.id.btn_new);
        Button btnImport = (Button) view.findViewById(R.id.btn_import);
        btnNew.setOnClickListener(this);
        btnImport.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new:
                startActivity(new Intent(baseActivity, CreateWalletActivity.class));
                break;
            case R.id.btn_import:
                startActivity(new Intent(baseActivity, ImportWalletActivity.class));
                break;
            case R.id.btn_send:
                startActivity(new Intent(baseActivity, SendWalletActivity.class));
                break;
            case R.id.btn_receiver:
                copyAddress();
                break;
            case R.id.btn_record:
                Intent intent = new Intent(baseActivity, WebActivity.class);
                intent.putExtra(Constant.KEY, String.format("https://explorer.ont.io/address/%s", SPWrapper.getDefaultAddress()));
                startActivity(intent);
                break;
            case R.id.btn_refresh:
                refresh();
                break;
            default:
        }
    }

    private void copyAddress() {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) baseActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("key", SPWrapper.getDefaultAddress());
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
        baseActivity.showAttention("Your wallet address has been copied");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
            layoutHasWallet.setVisibility(View.GONE);
            layoutNoWallet.setVisibility(View.VISIBLE);
        } else {
            layoutHasWallet.setVisibility(View.VISIBLE);
            layoutNoWallet.setVisibility(View.GONE);
            refresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        baseActivity.dismissLoading();
        if (balanceReq != null) {
            balanceReq.cancel();
        }
    }

    @Override
    public void onResult(Result result) {
        baseActivity.dismissLoading();
        if (result.isSuccess && tvOng != null) {
            JSONObject jsonObject = JSONObject.parseObject((String) result.info);
            JSONArray array = jsonObject.getJSONArray("Result");
            JSONObject bean;
            for (int i = 0; i < array.size(); i++) {
                bean = array.getJSONObject(i);
                switch (bean.getString("AssetName")) {
                    case "ong":
                        tvOng.setText(bean.getString("Balance"));
                        break;
                    case "ont":
                        tvOnt.setText(bean.getString("Balance"));
                        break;
                    case "unboundong":
                        tvClaim.setText(String.format("%s(Claim)", bean.getString("Balance")));
                        break;
                    case "waitboundong":
                    default:
                }
            }
        }
    }

    @Override
    public void onResultFail(Result error) {
        baseActivity.dismissLoading();
    }

}
