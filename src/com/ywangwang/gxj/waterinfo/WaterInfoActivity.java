package com.ywangwang.gxj.waterinfo;

import java.util.ArrayList;

import com.ywangwang.gxj.MainActivity;
import com.ywangwang.gxj.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

public class WaterInfoActivity extends Activity {
	ActionBar actionBar;
	private ArrayList<Fragment> fragmentArrayList;
	private Fragment mCurrentFrgment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.water_info);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("返回主界面");
		actionBar.setDisplayShowHomeEnabled(false);
		initView();
		initFragment();
	}

	private void initView() {
		findViewById(R.id.rdoBtnFilterInfo).setOnClickListener(ButtonListener);
		((RadioButton) findViewById(R.id.rdoBtnFilterInfo)).setChecked(true);
		findViewById(R.id.rdoBtnGXJInfo).setOnClickListener(ButtonListener);
		findViewById(R.id.rdoBtnJSQInfo).setOnClickListener(ButtonListener);
	}

	private void initFragment() {
		fragmentArrayList = new ArrayList<Fragment>(3);
		fragmentArrayList.add(new FilterInfoFragment());
		fragmentArrayList.add(new GxjInfoFragment());
		fragmentArrayList.add(new JsqInfoFragment());
		changeTab(0);
	}

	private void changeTab(int index) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		// 判断当前的Fragment是否为空，不为空则隐藏
		if (null != mCurrentFrgment) {
			ft.hide(mCurrentFrgment);
		}

		// 先根据Tag从FragmentTransaction事物获取之前添加的Fragment
		Fragment fragment = getFragmentManager().findFragmentByTag(fragmentArrayList.get(index).getClass().getName());

		if (null == fragment) {
			// 如fragment为空，则之前未添加此Fragment。便从集合中取出
			fragment = fragmentArrayList.get(index);
		}
		mCurrentFrgment = fragment;

		// 判断此Fragment是否已经添加到FragmentTransaction事物中
		if (!fragment.isAdded()) {
			ft.add(R.id.fragmentContainer, fragment, fragment.getClass().getName());
		} else {
			ft.show(fragment);
		}
		ft.commit();
	}

	OnClickListener ButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rdoBtnFilterInfo:
				changeTab(0);
				break;
			case R.id.rdoBtnGXJInfo:
				changeTab(1);
				break;
			case R.id.rdoBtnJSQInfo:
				changeTab(2);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
