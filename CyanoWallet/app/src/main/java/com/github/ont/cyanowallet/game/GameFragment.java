package com.github.ont.cyanowallet.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.utils.Constant;

public class GameFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        Button btnGame = (Button) view.findViewById(R.id.btn_game);
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(baseActivity, GameWebActivity.class);
                intent.putExtra(Constant.KEY, "http://192.168.3.31:8080/");
                startActivity(intent);
            }
        });
    }
}
