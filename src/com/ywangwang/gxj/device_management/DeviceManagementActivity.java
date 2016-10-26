package com.ywangwang.gxj.device_management;

import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.MainActivity;
import com.ywangwang.gxj.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class DeviceManagementActivity extends Activity {
	final String TAG = "DeviceManagement";

	ActionBar actionBar;
	CheckBox chkBoxChildLock;
	TextView tvAppInfo;
	RadioButton rdoBtnSetting, rdoBtnWaterCode;
	private FragmentManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_management);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("返回主界面");
		actionBar.setDisplayShowHomeEnabled(false);

		findViewById(R.id.rdoBtnGeneralSetting).setOnClickListener(ButtonListener);
		findViewById(R.id.rdoBtnWiFiSetting).setOnClickListener(ButtonListener);
		findViewById(R.id.rdoBtnBindDevice).setOnClickListener(ButtonListener);
		findViewById(R.id.rdoBtnUserInfo).setOnClickListener(ButtonListener);
		findViewById(R.id.rdoBtnWaterCode).setOnClickListener(ButtonListener);
		if (GlobalInfo.debug == true) {
			findViewById(R.id.rdoBtnAdvancedSetting).setOnClickListener(ButtonListener);
		} else {
			findViewById(R.id.rdoBtnAdvancedSetting).setVisibility(View.GONE);
		}
		findViewById(R.id.rdoBtnAbout).setOnClickListener(ButtonListener);

		rdoBtnSetting = (RadioButton) findViewById(R.id.rdoBtnGeneralSetting);
		rdoBtnWaterCode = (RadioButton) findViewById(R.id.rdoBtnWaterCode);

		manager = getFragmentManager();
		Intent intent = getIntent();
		if (intent != null) {
			int show = intent.getIntExtra("show", -1);
			if (show == R.id.rdoBtnWaterCode) {
				rdoBtnWaterCode.callOnClick();
			} else {
				rdoBtnSetting.performClick();
			}
		} else {
			rdoBtnSetting.performClick();
		}
	}

	OnClickListener ButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentTransaction transaction = manager.beginTransaction();
			switch (v.getId()) {
			case R.id.rdoBtnGeneralSetting:
				GeneralSettingFragment generalSettingFragment = new GeneralSettingFragment();
				transaction.replace(R.id.fragmentContainer, generalSettingFragment);
				transaction.commit();
				break;
			case R.id.rdoBtnWiFiSetting:
				ShareFragment shareFragmentWiFiSetting = new ShareFragment();
				transaction.replace(R.id.fragmentContainer, shareFragmentWiFiSetting);
				Bundle bundleWiFiSetting = new Bundle();
				bundleWiFiSetting.putString("lable", "WiFi设置");
				bundleWiFiSetting.putInt("rdoBtnId", v.getId());
				shareFragmentWiFiSetting.setArguments(bundleWiFiSetting);
				transaction.commit();
				break;
			case R.id.rdoBtnBindDevice:
				BindDeviceFragment bindDeviceFragment = new BindDeviceFragment();
				transaction.replace(R.id.fragmentContainer, bindDeviceFragment);
				transaction.commit();
				break;
			case R.id.rdoBtnUserInfo:
				UserInfoFragment userInfoFragment = new UserInfoFragment();
				transaction.replace(R.id.fragmentContainer, userInfoFragment);
				transaction.commit();
				break;
			case R.id.rdoBtnWaterCode:
				WaterCodeFragment waterCodeFragment = new WaterCodeFragment();
				transaction.replace(R.id.fragmentContainer, waterCodeFragment);
				transaction.commit();
				break;
			case R.id.rdoBtnAdvancedSetting:
				AdvancedSettingFragment advancedSettingFragment = new AdvancedSettingFragment();
				transaction.replace(R.id.fragmentContainer, advancedSettingFragment);
				transaction.commit();
				break;
			case R.id.rdoBtnAbout:
				AboutFragment aboutFragment = new AboutFragment();
				transaction.replace(R.id.fragmentContainer, aboutFragment);
				transaction.commit();
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
