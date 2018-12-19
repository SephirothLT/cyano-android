package com.github.ont.cyanowallet.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Wallet;

import java.io.IOException;

public class MineFragment extends BaseFragment implements View.OnClickListener {
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
        btnWallet.setOnClickListener(this);
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
        deleteAccount(SPWrapper.getDefaultAddress());
        SPWrapper.setDefaultAddress("");
    }
}
