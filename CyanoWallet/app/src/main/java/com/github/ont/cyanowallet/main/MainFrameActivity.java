package com.github.ont.cyanowallet.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.game.GameFragment;
import com.github.ont.cyanowallet.mine.MineFragment;
import com.github.ont.cyanowallet.ontid.IdentityFragment;
import com.github.ont.cyanowallet.view.jptab.JPTabBar;
import com.github.ont.cyanowallet.view.jptab.anno.NorIcons;
import com.github.ont.cyanowallet.view.jptab.anno.SeleIcons;
import com.github.ont.cyanowallet.wallet.WalletFragment;

import java.util.ArrayList;
import java.util.List;

public class MainFrameActivity extends BaseActivity {

    @SeleIcons
    private static final int[] SELE_ICONS = {R.drawable.tab_asset_selected, R.drawable.tab_id_selected, R.drawable.logo, R.drawable.tab_me_selected};

    @NorIcons
    private static final int[] NORMAL_ICONS = {R.drawable.tab_asset_selected, R.drawable.tab_id_selected, R.drawable.logo, R.drawable.tab_me_selected};


    private List<Fragment> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
        initView();
    }

    private void initView() {
        JPTabBar mTabbar = findViewById(R.id.tabbar);
        ViewPager mPager = findViewById(R.id.view_pager);

        BaseFragment mTab1 = new WalletFragment();
        BaseFragment mTab2 = new IdentityFragment();
        BaseFragment mTab3 = new GameFragment();
        BaseFragment mTab4 = new MineFragment();

        list.add(mTab1);
        list.add(mTab2);
        list.add(mTab3);
        list.add(mTab4);
        mPager.setAdapter(new MainAdapter(getSupportFragmentManager(), list));
        mTabbar.setContainer(mPager);
    }
}
