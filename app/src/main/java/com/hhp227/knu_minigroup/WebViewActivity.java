package com.hhp227.knu_minigroup;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.hhp227.knu_minigroup.ui.navigationdrawer.DrawerArrowDrawable;

public class WebViewActivity extends Activity {
    public static String URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView webView = findViewById(R.id.wv_notice);
        WebSettings webSettings = webView.getSettings();
        ActionBar actionBar = getActionBar();
        URL = getIntent().getStringExtra(URL);

        webView.loadUrl(URL);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("경북대 게시판");
            actionBar.setHomeAsUpIndicator(new DrawerArrowDrawable(this) {
                @Override
                public boolean isLayoutRtl() {
                    return false;
                }
            });
        }
        // 모바일에서 자바스크립트를 실행 시키기 위한 용도
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
