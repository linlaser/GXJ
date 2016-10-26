package com.ywangwang.gxj.lib;

import java.util.TimeZone;

import com.ywangwang.gxj.GlobalInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class SyncTimeHelper {
	private Context context;
	public static final int SUCCESS = 1;
	public static final int SYNCING = 0;
	public static final int FAILURE = -1;

	private boolean SyncingTime = false;
	private boolean readTimeSuccess = false;
	private boolean syncTimeSuccess = false;

	public SyncTimeHelper(Context context) {
		this.context = context;
	}

	public boolean isReadTimeSuccess() {
		return readTimeSuccess;
	}

	public boolean isSyncTimeSuccess() {
		return syncTimeSuccess;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityMgr = (ConnectivityManager) context.getSystemService("connectivity");
		NetworkInfo _networkInfo = connectivityMgr.getActiveNetworkInfo();
		if (_networkInfo == null || !_networkInfo.isAvailable() || !_networkInfo.isConnected()) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * 同步时间线程
	 */
	public void syncNetworkTime() {
		if (SyncingTime)
			return;
		SyncingTime = true;
		if (TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT).equals("GMT+08:00") == false) {
			DateAndTime.setTimeZone(context, "GMT+08:00");
		}
		new Thread() {
			@Override
			public void run() {
				int times = 10;
				long time = 0;
				while (times-- > 0) {
					if (isNetworkAvailable(context) == true) {
						time = DateAndTime.getWebsiteDatetime();
						if (time > 0) {
							if (Math.abs(time - System.currentTimeMillis()) > 600000L) {
								// 发送校时广播
								context.sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_BROAD_RECEIVER_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_SYSTEM_TIME, true));
							} else {
								// 校时成功
								syncTimeSuccess = true;
								// getUserData();
								break;
							}
						}
					}
					GlobalInfo.sleep(1800);
					syncTimeSuccess = false;
				}
				SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				GlobalInfo.timeBackup = sharedPreferences.getLong("timeBackup", 0L);
				if (GlobalInfo.timeBackup < System.currentTimeMillis() || syncTimeSuccess == true) {
					GlobalInfo.timeBackup = System.currentTimeMillis();
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putLong("timeBackup", GlobalInfo.timeBackup);
					editor.commit();
					readTimeSuccess = true;
				} else if (syncTimeSuccess == false) {
					times = 10;
					while (times-- > 0) {
						if (GlobalInfo.timeBackup > System.currentTimeMillis()) {
							context.sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_BROAD_RECEIVER_ACTION).putExtra(GlobalInfo.BROADCAST_SET_SYSTEM_TIME, GlobalInfo.timeBackup));
						} else {
							readTimeSuccess = true;
							break;
						}
						GlobalInfo.sleep(1800);
						readTimeSuccess = false;
					}
				}
				SyncingTime = false;
			}
		}.start();
	}

	public void updataTimeBackup() {
		SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		long timeBackup_temp = sharedPreferences.getLong("timeBackup", 0L);
		if (timeBackup_temp < System.currentTimeMillis()) {
			GlobalInfo.timeBackup = System.currentTimeMillis();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putLong("timeBackup", GlobalInfo.timeBackup);
			editor.commit();
		}
	}
}
