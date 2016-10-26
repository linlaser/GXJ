package com.ywangwang.gxj.device_management;

import com.ywangwang.gxj.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShareFragment extends Fragment {
	private final String TAG = "ShareFragment";
	private int rdoBtnId = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, TAG + "-->>onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, TAG + "-->>onCreateView");
		View view = inflater.inflate(R.layout.device_management_share, container, false);
		Button btnShare = (Button) view.findViewById(R.id.btnShare);
		btnShare.setOnClickListener(ButtonListener);
		Bundle bundle = getArguments();
		if (bundle != null) {
			String lable = bundle.getString("lable", "");
			((TextView) view.findViewById(R.id.tvLable)).setText(lable);
			btnShare.setText(lable);
			rdoBtnId = bundle.getInt("rdoBtnId", 0);
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

			switch (rdoBtnId) {
			case R.id.rdoBtnWiFiSetting:
				Intent intentWiFi = new Intent();
				intentWiFi.setAction("android.net.wifi.PICK_WIFI_NETWORK");
				intentWiFi.putExtra("extra_prefs_show_button_bar", true);
				intentWiFi.putExtra("extra_prefs_set_next_text", "Íê³É");
				intentWiFi.putExtra("extra_prefs_set_back_text", "·µ»Ø");
				intentWiFi.putExtra("wifi_enable_next_on_connect", true);
				startActivity(intentWiFi);
				break;
			default:
				break;
			}
		}
	};
}
