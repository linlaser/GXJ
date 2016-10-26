package com.ywangwang.gxj.lib;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class ScreenBrightnessTool {
	// private static final String TAG = "ScreenBrightnessTool";

	public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

	public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

	public static final int MAX_BRIGHTNESS = 255;

	public static final int MIN_BRIGHTNESS = 50;

	private Context context;

	public ScreenBrightnessTool(Context context) {
		this.context = context;
	}

	public int getSystemAutomaticMode() {
		int automaticMode;
		try {
			// 获取当前系统调节模式
			automaticMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (SettingNotFoundException e) {
			return -1;
		}
		// Debug.d(TAG, "automaticMode=" + automaticMode);
		return automaticMode;
	}

	public int getSystemBrightness() {
		int brightness;
		try {
			// 获取当前系统亮度值
			brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			return -1;
		}
		// Debug.d(TAG, "brightness=" + brightness);
		return brightness;
	}

	public void setSystemAutomaticMode(int mode) {
		if (mode == SCREEN_BRIGHTNESS_MODE_AUTOMATIC || mode == SCREEN_BRIGHTNESS_MODE_MANUAL) {
			try {
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setBrightness(int brightness) {
		if (brightness >= MIN_BRIGHTNESS && brightness <= MAX_BRIGHTNESS) {
			ContentResolver resolver = context.getContentResolver();
			try {
				Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
