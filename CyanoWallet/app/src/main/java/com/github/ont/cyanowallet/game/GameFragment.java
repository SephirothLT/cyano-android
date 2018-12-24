package com.github.ont.cyanowallet.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.beans.GameListBean;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.GameListReq;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends BaseFragment {
    private static final String TAG = "GameFragment";

    private Banner banner;
    private GameListReq gameListReq;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        test(view);
    }

    private void test(View view) {
        Button btn = (Button) view.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
                    ToastUtil.showToast(baseActivity, "No Wallet");
                } else {
                    Intent intent = new Intent(baseActivity, GameWebActivity.class);
                    intent.putExtra(Constant.KEY, "http://192.168.3.31:8081");
                    startActivity(intent);
                }
            }
        });
    }

    private void initView(View view) {
        banner = (Banner) view.findViewById(R.id.banner);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        initData();
    }

    private void initData() {
        baseActivity.showLoading();
        if (gameListReq != null) {
            gameListReq.cancel();
        }
        gameListReq = new GameListReq();
        gameListReq.setRequestTag(TAG);
        gameListReq.setOnResultListener(new BaseRequest.ResultListener() {
            @Override
            public void onResult(Result result) {
                baseActivity.dismissLoading();
                if (result.isSuccess) {
                    GameListBean gameListBean = JSONObject.parseObject((String) result.info, GameListBean.class);
                    final List<GameListBean.ResultBean.BannerBean> banners = gameListBean.getResult().getBanner();
                    ArrayList<String> appIcons = new ArrayList<>();
                    for (GameListBean.ResultBean.BannerBean bean : banners) {
                        appIcons.add(bean.getImage());
                    }
                    //设置图片集合
                    banner.setImages(appIcons);
                    //banner设置方法全部调用完毕时最后调用
                    banner.setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            Intent intent = new Intent(baseActivity, GameWebActivity.class);
                            intent.putExtra(Constant.KEY, banners.get(position).getLink());
                            startActivity(intent);
                        }
                    });
                    banner.start();

                    //小列表
                    final List<GameListBean.ResultBean.AppsBean> apps = gameListBean.getResult().getApps();

                    recyclerView.setLayoutManager(new GridLayoutManager(baseActivity, 4));
                    RecycleImageAdapter adapter = new RecycleImageAdapter(baseActivity, apps);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListern(new RecycleImageAdapter.OnItemClick() {
                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(baseActivity, GameWebActivity.class);
                            intent.putExtra(Constant.KEY, apps.get(position).getLink());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onResultFail(Result error) {
                baseActivity.dismissLoading();
            }
        });
        gameListReq.excute();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (banner != null) {
            banner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (gameListReq != null) {
            gameListReq.cancel();
        }
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }
}
