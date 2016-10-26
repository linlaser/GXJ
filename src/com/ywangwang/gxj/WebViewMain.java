package com.ywangwang.gxj;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class WebViewMain extends Activity {
	private final int BACK = 101;
	private final int FORWARD = 102;
	private final int RELOAD = 103;
	private final int HOME = 104;
	ActionBar actionBar;
	ProgressDialog proDialog;
	RelativeLayout layout;
	WebView webViewMain;
	ProgressBar proBarWeb;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		layout = new RelativeLayout(WebViewMain.this);
		webViewMain = new WebView(WebViewMain.this);
		if (!GlobalInfo.enableWebViewHardwareAccelerated) {
			webViewMain.setLayerType(View.LAYER_TYPE_SOFTWARE, null);// 关闭WebView的硬件加速，否则退出WebView所在的Activity容易报错
		}
		webViewMain.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		layout.addView(webViewMain);
		proBarWeb = new ProgressBar(WebViewMain.this, null, android.R.attr.progressBarStyleHorizontal);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, -5, 0, 0);
		proBarWeb.setLayoutParams(layoutParams);
		layout.addView(proBarWeb);
		proBarWeb.setMax(100);
		setContentView(layout);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("返回            ");
		actionBar.setDisplayShowHomeEnabled(false);
		proDialog = new ProgressDialog(WebViewMain.this);
		webViewMain.getSettings().setJavaScriptEnabled(true);
		webViewMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				proDialog.show();
				super.onPageStarted(view, url, favicon);
			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
		webViewMain.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 100) {
					proBarWeb.setVisibility(View.GONE);
				} else {
					proBarWeb.setVisibility(View.VISIBLE);
					proBarWeb.setProgress(progress);
				}
				if (progress < 60) {
					proDialog.setMessage("加载中... " + progress + "%");
				} else {
					proDialog.dismiss();
				}
			}
		});
		webViewMain.loadUrl(GlobalInfo.HOME_URL);
		// proDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem goBack = menu.add(0, BACK, 0, "返回").setIcon(R.drawable.back);
		goBack.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		MenuItem goForward = menu.add(0, FORWARD, 0, "前进").setIcon(R.drawable.forward);
		goForward.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		MenuItem reload = menu.add(0, RELOAD, 0, "刷新").setIcon(R.drawable.reload);
		reload.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		MenuItem goHOME = menu.add(0, HOME, 0, "主页").setIcon(R.drawable.home);
		goHOME.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case BACK:
			webViewMain.goBack();
			return true;
		case FORWARD:
			webViewMain.goForward();
			return true;
		case RELOAD:
			webViewMain.reload();
			return true;
		case HOME:
			webViewMain.loadUrl(GlobalInfo.HOME_URL);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		if (webViewMain != null) {
			layout.removeView(webViewMain);
			webViewMain.removeAllViews();
			webViewMain.destroy();
		}
		super.onDestroy();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		MainActivity.resetADTimer();
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onResume() {
		MainActivity.resetADTimer();
		super.onResume();
	}

	@Override
	protected void onPause() {
		MainActivity.stopADTimer();
		super.onPause();
	}
}
