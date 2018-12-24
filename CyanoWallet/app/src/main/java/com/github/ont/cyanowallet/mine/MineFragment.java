package com.github.ont.cyanowallet.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SDKCallback;
import com.github.ont.cyanowallet.utils.SDKWrapper;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Wallet;

import java.io.IOException;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "MineFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        Button btnWallet = (Button) view.findViewById(R.id.btn_wallet);
        Button exportWallet = (Button) view.findViewById(R.id.export_wallet);
        btnWallet.setOnClickListener(this);
        exportWallet.setOnClickListener(this);
        btnWallet.setVisibility((TextUtils.isEmpty(SPWrapper.getDefaultAddress()) ? View.GONE : View.VISIBLE));
    }

    public static void deleteAccount(String address) {
        WalletMgr walletMgr = OntSdk.getInstance().getWalletMgr();
        Wallet wallet = walletMgr.getWallet();
        wallet.removeAccount(address);
        try {
            walletMgr.writeWallet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wallet:
                if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
                    ToastUtil.showToast(baseActivity, "no wallet");
                } else {
                    deleteAccount(SPWrapper.getDefaultAddress());
                    SPWrapper.setDefaultAddress("");
                }
                break;
            case R.id.export_wallet:
                if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
                    ToastUtil.showToast(baseActivity, "no wallet");
                } else {
                    baseActivity.setGetDialogPwd(new BaseActivity.GetDialogPassword() {
                        @Override
                        public void handleDialog(String pwd) {
                            baseActivity.showLoading();
                            SDKWrapper.getWalletKey(new SDKCallback() {
                                @Override
                                public void onSDKSuccess(String tag, Object message) {
                                    baseActivity.dismissLoading();
                                    Intent intent = new Intent(baseActivity, ExportWalletActivity.class);
                                    intent.putExtra(Constant.KEY, (String) message);
                                    startActivity(intent);
                                }

                                @Override
                                public void onSDKFail(String tag, String message) {
                                    baseActivity.dismissLoading();
                                    baseActivity.showAttention(message);
                                }
                            }, TAG, pwd);
                        }
                    });
                    baseActivity.showPasswordDialog();
                }
                break;
            default:
        }
    }

    @Override
    public void onStop() {
        baseActivity.dismissPwdDialog();
        super.onStop();
    }
}
