package com.ywangwang.gxj.device_management;

import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.R;
import com.ywangwang.gxj.lib.ScreenBrightnessTool;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class GeneralSettingFragment extends Fragment {
	private final String TAG = "GeneralSettingFragment";
	CheckBox chkBoxChildLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, TAG + "-->>onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, TAG + "-->>onCreateView");
		View view = inflater.inflate(R.layout.device_management_general_setting, container, false);

		chkBoxChildLock = (CheckBox) view.findViewById(R.id.chkBoxChildLock);
		chkBoxChildLock.setOnClickListener(ButtonListener);

		view.findViewById(R.id.btnSetScreenBrightness).setOnClickListener(ButtonListener);

		if (GlobalInfo.enableChildLock) {
			chkBoxChildLock.setChecked(true);
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
			case R.id.btnSetScreenBrightness:
				setScreenBrightness();
				break;
			case R.id.chkBoxChildLock:
				if (chkBoxChildLock.isChecked()) {
					GlobalInfo.enableChildLock = true;
				} else {
					GlobalInfo.enableChildLock = false;
				}
				SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalInfo.S_P_NAME_CONFIG, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("enableChildLock", GlobalInfo.enableChildLock);
				editor.commit();
				break;
			default:
				break;
			}
		}
	};

	private void setScreenBrightness() {
		final ScreenBrightnessTool screenBrightnessTool = new ScreenBrightnessTool(getActivity());
		final int brightnessCache = screenBrightnessTool.getSystemBrightness();
		final int automaticMode = screenBrightnessTool.getSystemAutomaticMode();
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setPadding(40, 40, 40, 40);
		ToggleButton tglBtn = new ToggleButton(getActivity());
		tglBtn.setTextOn("自动亮度");
		tglBtn.setTextOff("自动亮度");
		layout.addView(tglBtn);
		SeekBar skBar = new SeekBar(getActivity());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 15, 0, 0);
		skBar.setLayoutParams(layoutParams);
		layout.addView(skBar);

		tglBtn.setChecked(automaticMode == ScreenBrightnessTool.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		tglBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				screenBrightnessTool.setSystemAutomaticMode(isChecked ? ScreenBrightnessTool.SCREEN_BRIGHTNESS_MODE_AUTOMATIC : ScreenBrightnessTool.SCREEN_BRIGHTNESS_MODE_MANUAL);
			}
		});

		skBar.setMax(ScreenBrightnessTool.MAX_BRIGHTNESS - ScreenBrightnessTool.MIN_BRIGHTNESS);
		skBar.setProgress(screenBrightnessTool.getSystemBrightness() > ScreenBrightnessTool.MIN_BRIGHTNESS ? screenBrightnessTool.getSystemBrightness() - ScreenBrightnessTool.MIN_BRIGHTNESS : 0);
		skBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				screenBrightnessTool.setBrightness(progress + ScreenBrightnessTool.MIN_BRIGHTNESS);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("调节屏幕亮度");
		builder.setView(layout);
		builder.setPositiveButton("确定", null);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				screenBrightnessTool.setBrightness(brightnessCache);
				screenBrightnessTool.setSystemAutomaticMode(automaticMode);
			}
		});
		builder.show();
	}
}
