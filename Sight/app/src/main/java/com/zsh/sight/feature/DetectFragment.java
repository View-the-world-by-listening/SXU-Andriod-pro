package com.zsh.sight.feature;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import pl.droidsonroids.gif.GifImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zsh.sight.R;
import com.zsh.sight.Utils.ScreenShotUtil;

import java.io.IOException;


public class DetectFragment extends Fragment {
    // 布局组件
    private MaterialButton bt_model1, bt_model2, bt_model3;
    private FrameLayout fl_detect;
    private ImageView iv_banyuan, ivgreenbullon, iv_ocr;
    private GifImageView gifbird;
    private FloatingActionButton floatingActionButton;
    private ImageView button_model1, button_model2, button_model3;

    private View view;
    private TrunkActivity mActivity;

    private int choose_model = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detect, container, false);
        mActivity = (TrunkActivity) getActivity();
        initView();

        //属性动画
        //Animatation();


        return view;
    }

    private void initView() {
        bt_model1 = view.findViewById(R.id.model1);
        bt_model2 = view.findViewById(R.id.model2);
        bt_model3 = view.findViewById(R.id.model3);

        choose_model = 1;

        bt_model1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                choose_model = 1;
                Intent intentEnter = new Intent(mActivity, DetectActivity.class);
                intentEnter.putExtra("choose_model", choose_model);
                startActivity(intentEnter);
            }
        });

        bt_model2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                choose_model = 2;
                Intent intentEnter = new Intent(mActivity, DetectActivity.class);
                intentEnter.putExtra("choose_model", choose_model);
                startActivity(intentEnter);
            }
        });

        bt_model3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                choose_model = 3;
                Intent intentEnter = new Intent(mActivity, DetectActivity.class);
                intentEnter.putExtra("choose_model", choose_model);
                startActivity(intentEnter);
            }
        });
        floatingActionButton = view.findViewById(R.id.help_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, HelpActivity.class);
                Bitmap bitmap = ScreenShotUtil.screenShot(mActivity);
                try {
                    String path = ScreenShotUtil.save(bitmap, mActivity, Bitmap.CompressFormat.JPEG, true);
                    intent.putExtra("path", path);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    //动画实现
    private void Animatation() {
        //缩放动画
        // 组合动画对象
        AnimatorSet animatorSetsuofang = new AnimatorSet();

        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator scaleX = ObjectAnimator.ofFloat(fl_detect, "scaleX", 1, 0.9f, 1);//后几个参数是放大的倍数
        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator scaleY = ObjectAnimator.ofFloat(fl_detect, "scaleY", 1, 0.9f, 1);

        scaleX.setRepeatCount(ValueAnimator.INFINITE);//永久循环
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        animatorSetsuofang.setDuration(2500);//时间
        animatorSetsuofang.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSetsuofang.start();//开始

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_banyuan, "translationY", 0f, 40f, 0f);
        animator1.setDuration(3500);
        animator1.setRepeatCount(-1);//设置一直重复
        animator1.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(ivgreenbullon, "translationY", 0f, -1500f);
        animator2.setDuration(7000);
        animator2.setRepeatCount(-1);//设置一直重复
        animator2.start();

        AnimatorSet birdAnimatorSet = new AnimatorSet();
        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator animator3_x = ObjectAnimator.ofFloat(gifbird, "translationX", 0, 1800);
        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator animator3_y = ObjectAnimator.ofFloat(gifbird, "translationY", 0, -300);
        animator3_x.setRepeatCount(ValueAnimator.INFINITE);//永久循环
        animator3_y.setRepeatCount(ValueAnimator.INFINITE);
        birdAnimatorSet.setDuration(4000);//时间
        birdAnimatorSet.play(animator3_x).with(animator3_y);//两个动画同时开始

        birdAnimatorSet.start();//开始
    }
}