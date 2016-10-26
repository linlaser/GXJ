package com.ywangwang.gxj.lib;

import java.util.Random;

import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.net.Net;
import com.ywangwang.gxj.net.WaterCode;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesConfig {
	public static void read(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalInfo.S_P_NAME_CONFIG, Context.MODE_PRIVATE);
		GlobalInfo.serverAddress = sharedPreferences.getString(GlobalInfo.S_P_KEY_SERVER_ADDRESS, "192.168.0.123");
		GlobalInfo.CustomWaterAmountValue = sharedPreferences.getInt("CustomWaterAmountValue", GlobalInfo.CustomWaterAmountValue);
		GlobalInfo.Custom1TemperatureValue = sharedPreferences.getInt("Custom1TemperatureValue", GlobalInfo.Custom1TemperatureValue);
		GlobalInfo.Custom1WaterAmountValue = sharedPreferences.getInt("Custom1WaterAmountValue", GlobalInfo.Custom1WaterAmountValue);
		GlobalInfo.Custom1Name = sharedPreferences.getString("Custom1Name", "自定义1");
		GlobalInfo.Custom2TemperatureValue = sharedPreferences.getInt("Custom2TemperatureValue", GlobalInfo.Custom2TemperatureValue);
		GlobalInfo.Custom2WaterAmountValue = sharedPreferences.getInt("Custom2WaterAmountValue", GlobalInfo.Custom2WaterAmountValue);
		GlobalInfo.Custom2Name = sharedPreferences.getString("Custom2Name", "自定义2");
		GlobalInfo.enableChildLock = sharedPreferences.getBoolean("enableChildLock", true);
		GlobalInfo.enableCoolWater = sharedPreferences.getBoolean("enableCoolWater", false);
		GlobalInfo.boundJSQ.used = sharedPreferences.getBoolean("boundJSQ_used", false);
		GlobalInfo.boundJSQ.add = sharedPreferences.getInt("boundJSQ_add", 0);
		GlobalInfo.boundJSQ.deviceType = sharedPreferences.getInt("boundJSQ_deviceType", 0);
		GlobalInfo.hostAdd = sharedPreferences.getInt("hostAdd", 0);
		if (GlobalInfo.hostAdd == 0 || GlobalInfo.hostAdd == 0xFFFF) {
			GlobalInfo.hostAdd = new Random().nextInt(0xFFFE) + 1;
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt("hostAdd", GlobalInfo.hostAdd);
			editor.commit();
		}
		GlobalInfo.hostID = sharedPreferences.getLong("hostID", 0L);
		if (GlobalInfo.hostID == 0L) {
			GlobalInfo.hostID = Long.parseLong(Net.getMacAddress(context).replace(":", ""), 16);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putLong("hostID", GlobalInfo.hostID);
			editor.commit();
		}

		GlobalInfo.debug = sharedPreferences.getBoolean(GlobalInfo.S_P_KEY_DEBUG, false);
		GlobalInfo.debugTimes = sharedPreferences.getInt(GlobalInfo.S_P_KEY_DEBUG_TIMES, 0);
		if (GlobalInfo.debug == true) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (GlobalInfo.debugTimes > 1) {
				editor.putInt(GlobalInfo.S_P_KEY_DEBUG_TIMES, --GlobalInfo.debugTimes);
			} else {
				editor.remove(GlobalInfo.S_P_KEY_DEBUG);
				editor.remove(GlobalInfo.S_P_KEY_DEBUG_TIMES);
				GlobalInfo.debug = false;
				GlobalInfo.debugTimes = 0;
			}
			editor.commit();
		}
		readUserInfo(context);
		readWaterInfo(context);
	}

	private static void readUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalInfo.S_P_NAME_USER_INFO, Context.MODE_PRIVATE);
		GlobalInfo.username = sharedPreferences.getString(GlobalInfo.S_P_KEY_USERNAME, "");
		GlobalInfo.password = sharedPreferences.getString(GlobalInfo.S_P_KEY_PASSWORD, "");
		GlobalInfo.savePassword = sharedPreferences.getBoolean("savePassword", true);
		GlobalInfo.autoLogin = sharedPreferences.getBoolean("autoLogin", true);

		GlobalInfo.isBoundWaterCode = sharedPreferences.getBoolean(GlobalInfo.KEY_IS_BOUND_WATER_CODE, false);
		GlobalInfo.boundWaterCode.setNumber(sharedPreferences.getLong(GlobalInfo.KEY_NUMBER, 0L));
		GlobalInfo.boundWaterCode.setType(sharedPreferences.getInt(GlobalInfo.KEY_TYPE, 0));
		GlobalInfo.boundWaterCode.setStatus(sharedPreferences.getInt(GlobalInfo.KEY_STATUS, 0));
		GlobalInfo.boundWaterCode.setBoundDeviceId(sharedPreferences.getLong(GlobalInfo.KEY_BOUND_DEVICE_ID, 0L));
		GlobalInfo.boundWaterCode.setPeriodValidity(sharedPreferences.getInt(GlobalInfo.KEY_PERIOD_VALIDITY, 0));
		GlobalInfo.boundWaterCode.setActivationTime(sharedPreferences.getLong(GlobalInfo.KEY_ACTIVATION_TIME, 0L));
	}

	private static void readWaterInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalInfo.S_P_NAME_WATER_INFO, Context.MODE_PRIVATE);
		GlobalInfo.filterInstallTime[0] = sharedPreferences.getLong("filterInstallTime0", 0);
		GlobalInfo.filterInstallTime[1] = sharedPreferences.getLong("filterInstallTime1", 0);
		GlobalInfo.filterInstallTime[2] = sharedPreferences.getLong("filterInstallTime2", 0);
		GlobalInfo.filterInstallTime[3] = sharedPreferences.getLong("filterInstallTime3", 0);
	}

	public static void saveWaterCode(Context context, boolean isBoundWaterCode, WaterCode watercode) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalInfo.S_P_NAME_USER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(GlobalInfo.KEY_IS_BOUND_WATER_CODE, isBoundWaterCode);
		if (isBoundWaterCode == true) {
			editor.putLong(GlobalInfo.KEY_NUMBER, watercode.getNumber());
			editor.putInt(GlobalInfo.KEY_TYPE, watercode.getType());
			editor.putInt(GlobalInfo.KEY_STATUS, watercode.getStatus());
			editor.putLong(GlobalInfo.KEY_BOUND_DEVICE_ID, watercode.getBoundDeviceId());
			editor.putInt(GlobalInfo.KEY_PERIOD_VALIDITY, watercode.getPeriodValidity());
			editor.putLong(GlobalInfo.KEY_ACTIVATION_TIME, watercode.getActivationTime());
		} else {
			editor.remove(GlobalInfo.KEY_NUMBER);
			editor.remove(GlobalInfo.KEY_TYPE);
			editor.remove(GlobalInfo.KEY_STATUS);
			editor.remove(GlobalInfo.KEY_BOUND_DEVICE_ID);
			editor.remove(GlobalInfo.KEY_PERIOD_VALIDITY);
			editor.remove(GlobalInfo.KEY_ACTIVATION_TIME);
		}
		editor.commit();
	}
}
