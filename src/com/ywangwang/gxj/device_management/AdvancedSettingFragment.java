package com.ywangwang.gxj.device_management;

import com.ywangwang.gxj.CustomDialog;
import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.R;
import com.ywangwang.gxj.net.Operaton;
import com.ywangwang.gxj.net.TcpManager;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class AdvancedSettingFragment extends Fragment {
	private final String TAG = "AdvancedSettingFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, TAG + "-->>onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, TAG + "-->>onCreateView");
		View view = inflater.inflate(R.layout.device_management_advanced_setting, container, false);
		view.findViewById(R.id.btnUpdateGxj).setOnClickListener(ButtonListener);
		view.findViewById(R.id.btnUpdateGxjServer).setOnClickListener(ButtonListener);
		view.findViewById(R.id.btnResetDevice).setOnClickListener(ButtonListener);
		view.findViewById(R.id.btnReboot).setOnClickListener(ButtonListener);
		view.findViewById(R.id.btnLauncher).setOnClickListener(ButtonListener);
		view.findViewById(R.id.chkboxEnableCoolWater).setOnClickListener(ButtonListener);
		view.findViewById(R.id.rdoBtnLocal).setOnClickListener(ButtonListener);
		view.findViewById(R.id.rdoBtnRemote).setOnClickListener(ButtonListener);
		if (GlobalInfo.enableCoolWater) {
			((CheckBox) view.findViewById(R.id.chkboxEnableCoolWater)).setChecked(true);
		}
		if (GlobalInfo.serverAddress.equals("www.ywangwang.com")) {
			((RadioButton) view.findViewById(R.id.rdoBtnRemote)).setChecked(true);
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, TAG + "-->>onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, TAG + "-->>onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, TAG + "-->>onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i(TAG, TAG + "-->>onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(TAG, TAG + "-->>onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, TAG + "-->>onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.i(TAG, TAG + "-->>onDetach");
	}

	OnClickListener ButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btnUpdateGxj:
				new CustomDialog().show(getActivity(), "确认更新？", "确认更新管线机？", "取消", "确定", null, runnableResetDevice(R.id.btnUpdateGxj));
				break;
			case R.id.btnUpdateGxjServer:
				new CustomDialog().show(getActivity(), "确认更新？", "确认更新管线机后台服务？", "取消", "确定", null, runnableResetDevice(R.id.btnUpdateGxjServer));
				break;
			case R.id.btnResetDevice:
				new CustomDialog().show(getActivity(), "确认重置设备？", "确认重置设备？\n重置后，设备的所有配置信息将恢复出厂设置。", "取消", "确定", null, runnableResetDevice(R.id.btnResetDevice));
				break;
			case R.id.btnReboot:
				new CustomDialog().show(getActivity(), "确认重启设备？", "确认重启设备？", "取消", "确定", null, runnableResetDevice(R.id.btnReboot));
				break;
			case R.id.btnLauncher:
				try {
					Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.launcher");
					startActivity(intent);
				} catch (Exception e) {
				}
				break;
			case R.id.chkboxEnableCoolWater:
				GlobalInfo.enableCoolWater = ((CheckBox) v).isChecked();
				SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalInfo.S_P_NAME_CONFIG, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("enableCoolWater", GlobalInfo.enableCoolWater);
				editor.commit();
				break;
			case R.id.rdoBtnLocal:
				switchServer((String) ((RadioButton) v).getText());
				break;
			case R.id.rdoBtnRemote:
				switchServer((String) ((RadioButton) v).getText());
				break;
			default:
				break;
			}
		}
	};

	private Runnable runnableResetDevice(final int viewID) {
		return new Runnable() {
			public void run() {
				switch (viewID) {
				case R.id.btnUpdateGxj:
					getActivity().sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_BROAD_RECEIVER_ACTION).putExtra("WAKE_UP", true));
					getActivity().sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_GXJ_SERVER_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_GXJ_CHECK_NOW, true));
					break;
				case R.id.btnUpdateGxjServer:
					getActivity().sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_BROAD_RECEIVER_ACTION).putExtra("WAKE_UP", true));
					getActivity().sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_GXJ_SERVER_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_GXJ_SERVER_CHECK_NOW, true));
					break;
				case R.id.btnResetDevice:
					getActivity().getSharedPreferences("config", Context.MODE_PRIVATE).edit().clear().commit();
					Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					break;
				case R.id.btnReboot:
					getActivity().sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_BROAD_RECEIVER_ACTION).putExtra(GlobalInfo.BROADCAST_REBOOT_SYSTEM, true));
					// 方法1，需要系统签名，并安装在system/app下
					// PowerManager pManager = (PowerManager)
					// getSystemService(Context.POWER_SERVICE);
					// pManager.reboot("");
					// 方法2，需要系统签名，并安装在system/app下
					// Intent intentReboot = new Intent();
					// intentReboot.setAction(Intent.ACTION_REBOOT);
					// intentReboot.putExtra("nowait", 1);
					// intentReboot.putExtra("interval", 1);
					// intentReboot.putExtra("window", 0);
					// sendBroadcast(intentReboot);
					break;
				}
			}
		};
	}

	private void switchServer(String address) {
		GlobalInfo.serverAddress = address;
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalInfo.S_P_NAME_CONFIG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(GlobalInfo.S_P_KEY_SERVER_ADDRESS, GlobalInfo.serverAddress);
		editor.commit();
		TcpManager.setServerAddress(GlobalInfo.serverAddress);
		TcpManager.reconnect();
		Operaton.setServerAddress(GlobalInfo.serverAddress);
	}
}
