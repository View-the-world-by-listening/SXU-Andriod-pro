package com.zsh.sight.feature;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;

import com.zsh.sight.R;
import com.zsh.sight.Utils.ScreenShotUtil;
import com.zsh.sight.Utils.ScreenSizeUtils;

import java.io.File;
import java.io.InputStream;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        String path = getIntent().getStringExtra("path");
        blur(BitmapFactory.decodeFile(path),
                findViewById(R.id.help_backound), 25);
        findViewById(R.id.help_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarUtil.transparencyBar(this);
        StatusBarUtil.BlackFontStatusBar(this.getWindow());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onClick(View v){
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void blur(Bitmap bkg, View view, float radius) {
        Bitmap overlay = Bitmap.createBitmap(ScreenSizeUtils.getInstance(this).getScreenWidth(),
                ScreenSizeUtils.getInstance(this).getScreenHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(bkg, -view.getLeft(), -view.getTop(), null);
        RenderScript rs = RenderScript.create(this);
        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
        rs.destroy();
    }
}