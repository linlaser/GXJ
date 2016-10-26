package com.ywangwang.gxj;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.ywangwang.gxj.lib.CheckData;
import com.ywangwang.gxj.lib.CustomToast;
import com.ywangwang.gxj.lib.DatabaseHelper;
import com.ywangwang.gxj.lib.ProtocolData;
import com.ywangwang.gxj.lib.SessionKey;
import com.ywangwang.gxj.lib.SharedPreferencesConfig;
import com.ywangwang.gxj.lib.StatisticsDataReadOrWrite;
import com.ywangwang.gxj.lib.StrConv;
import com.ywangwang.gxj.lib.SyncTimeHelper;
import com.ywangwang.gxj.net.Heartbeat;
import com.ywangwang.gxj.net.JsonTools;
import com.ywangwang.gxj.net.MoMessage;
import com.ywangwang.gxj.net.Net;
import com.ywangwang.gxj.net.Operaton;
import com.ywangwang.gxj.net.TcpManager;
import com.ywangwang.gxj.net.User;
import com.ywangwang.gxj.net.WaterCode;
import com.ywangwang.gxj.waterinfo.WaterInfoActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android_serialport_api.SerialController;

public class MainActivity extends Activity {
	private final int TIMEOUT = -1;
	private final int LOGIN = 1;
	private final int LOGIN_SUCCESS = 2;
	private final int LOGIN_FAIL = 3;
	private final int UPDATE_WATER_CODE = 4;
	private final int UPDATE_WATER_CODE_SUCCESS = 5;
	private final int UPDATE_WATER_CODE_FAIL = 6;
	SessionKey sessionKey = new SessionKey();
	SessionKey socketSessionKey = new SessionKey();
	private boolean socketLogining = false;

	/* >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> */
	Button sendButton, clsButton, adButton, exitButton;
	ToggleButton tglBtnUART, toggleBtn_AD, tglBtnShowUARTDebug, tglBtnDebug, tglBtnEnableHardwareAccelerated;
	EditText showEditText;
	LinearLayout layoutDebugToolbar;
	/* <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< */

	private static final String TAG = "MainActivity";
	CustomToast toast = new CustomToast(MainActivity.this);
	ToggleButton tglBtnChildLock;
	RadioButton rdoBtnRoomTemperature, rdoBtnWaterAmount150, rdoBtnMilk, rdoBtnWaterAmount260, rdoBtnHoney, rdoBtnWaterAmount300, rdoBtnBoiling, rdoBtnWaterAmountCustom, rdoBtnCustom1, rdoBtnCustom2, rdoBtnCoolWater;
	Button btnDeviceManagement, btnWaterInformation, btnLifeServices, btnYWW, btnOutWater, btnWebViewMain;
	TextView tvTemperature, tvWaterAmount, tvMode, tvOutWaterTimes, tvNowTime;
	SerialControl UART = new SerialControl("/dev/ttyS2", 19200);
	boolean haveNewVersion = false, isShow = false;

	public static Handler handlerAD = new Handler(); // 线程定时Handler
	public static Runnable runnableAD; // 定时播放广告线程
	Handler handlerTimeDisplay = new Handler(); // 更新时间Handler
	Runnable runnableTimeDisplay = new Runnable() {
		@Override
		public void run() {
			handlerTimeDisplay.removeCallbacks(runnableTimeDisplay);
			handlerTimeDisplay.postDelayed(runnableTimeDisplay, 1000);
			tvNowTime.setText(DateFormat.format("yyyy年MM月dd日 EEEE HH:mm:ss", System.currentTimeMillis()));
		}
	}; // 更新时间线程

	public static SyncTimeHelper syncTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Debug.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvTemperature = (TextView) findViewById(R.id.tvTemperature);
		tvWaterAmount = (TextView) findViewById(R.id.tvWaterAmount);
		tvNowTime = (TextView) findViewById(R.id.tvNowTime);
		tvMode = (TextView) findViewById(R.id.tvMode);
		rdoBtnRoomTemperature = (RadioButton) findViewById(R.id.rdoBtnRoomTemperature);
		rdoBtnWaterAmount150 = (RadioButton) findViewById(R.id.rdoBtnWaterAmount150);
		rdoBtnMilk = (RadioButton) findViewById(R.id.rdoBtnMilk);
		rdoBtnWaterAmount260 = (RadioButton) findViewById(R.id.rdoBtnWaterAmount260);
		rdoBtnHoney = (RadioButton) findViewById(R.id.rdoBtnHoney);
		rdoBtnWaterAmount300 = (RadioButton) findViewById(R.id.rdoBtnWaterAmount300);
		rdoBtnBoiling = (RadioButton) findViewById(R.id.rdoBtnBoiling);
		rdoBtnWaterAmountCustom = (RadioButton) findViewById(R.id.rdoBtnWaterAmountCustom);
		rdoBtnCustom1 = (RadioButton) findViewById(R.id.rdoBtnCustom1);
		rdoBtnCustom2 = (RadioButton) findViewById(R.id.rdoBtnCustom2);
		rdoBtnCoolWater = (RadioButton) findViewById(R.id.rdoBtnCoolWater);
		rdoBtnRoomTemperature.setOnClickListener(RdoBtnTemperatureListener);
		rdoBtnMilk.setOnClickListener(RdoBtnTemperatureListener);
		rdoBtnHoney.setOnClickListener(RdoBtnTemperatureListener);
		rdoBtnBoiling.setOnClickListener(RdoBtnTemperatureListener);
		rdoBtnWaterAmount150.setOnClickListener(RdoBtnWaterAmountListener);
		rdoBtnWaterAmount260.setOnClickListener(RdoBtnWaterAmountListener);
		rdoBtnWaterAmount300.setOnClickListener(RdoBtnWaterAmountListener);
		rdoBtnWaterAmountCustom.setOnClickListener(RdoBtnWaterAmountListener);
		rdoBtnCustom1.setOnClickListener(RdoBtnCustomListener);
		rdoBtnCustom2.setOnClickListener(RdoBtnCustomListener);
		rdoBtnCustom1.setOnLongClickListener(RdoBtnCustomLongClickListener);
		rdoBtnCustom2.setOnLongClickListener(RdoBtnCustomLongClickListener);
		rdoBtnCoolWater.setOnClickListener(RdoBtnCoolWaterListener);

		btnOutWater = (Button) findViewById(R.id.btnOutWater);
		btnOutWater.setOnClickListener(ButtonListener);
		btnDeviceManagement = (Button) findViewById(R.id.btnDeviceManagement);
		btnDeviceManagement.setOnClickListener(ButtonListener);
		btnLifeServices = (Button) findViewById(R.id.btnLifeServices);
		btnLifeServices.setOnClickListener(ButtonListener);
		btnWebViewMain = (Button) findViewById(R.id.btnWebViewMain);
		btnWebViewMain.setOnClickListener(ButtonListener);
		findViewById(R.id.btnWaterInformation).setOnClickListener(ButtonListener);

		tglBtnChildLock = (ToggleButton) findViewById(R.id.tglBtnChildLock);
		tglBtnChildLock.setOnClickListener(ButtonListener);

		/* >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> */
		layoutDebugToolbar = (LinearLayout) findViewById(R.id.layoutDebugToolbar);
		showEditText = (EditText) findViewById(R.id.showEditText);
		findViewById(R.id.btnTest1).setOnClickListener(new DebugButtonListener());
		sendButton = (Button) findViewById(R.id.sendButton);
		sendButton.setOnClickListener(new DebugButtonListener());
		clsButton = (Button) findViewById(R.id.clsButton);
		clsButton.setOnClickListener(new DebugButtonListener());
		tglBtnUART = (ToggleButton) findViewById(R.id.tglBtnUART);
		tglBtnUART.setOnClickListener(new DebugButtonListener());
		adButton = (Button) findViewById(R.id.adButton);
		adButton.setOnClickListener(new DebugButtonListener());
		tglBtnShowUARTDebug = (ToggleButton) findViewById(R.id.tglBtnShowUARTDebug);
		tglBtnShowUARTDebug.setOnClickListener(new DebugButtonListener());
		toggleBtn_AD = (ToggleButton) findViewById(R.id.toggleBtn_AD);
		toggleBtn_AD.setOnClickListener(new DebugButtonListener());
		tglBtnDebug = (ToggleButton) findViewById(R.id.tglBtnDebug);
		tglBtnDebug.setOnClickListener(new DebugButtonListener());
		tglBtnEnableHardwareAccelerated = (ToggleButton) findViewById(R.id.tglBtnEnableHardwareAccelerated);
		tglBtnEnableHardwareAccelerated.setOnClickListener(new DebugButtonListener());
		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new DebugButtonListener());
		findViewById(R.id.btnUpdate).setOnClickListener(new DebugButtonListener());
		/* <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< */

		GlobalInfo.databaseHelperJSQ = new DatabaseHelper(MainActivity.this, GlobalInfo.DB_FILE_NAME_JSQ);
		GlobalInfo.databaseHelperGXJ = new DatabaseHelper(MainActivity.this, GlobalInfo.DB_FILE_NAME_GXJ);
		syncTime = new SyncTimeHelper(MainActivity.this);
		runnableAD = new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(MainActivity.this, ADActivity.class));
			}
		};
		registerReceiver(broadcastReceiver, new IntentFilter(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION));
		OpenComPort(UART);
		init();
		TcpManager.getInstance(this, GlobalInfo.serverAddress);
		Operaton.setServerAddress(GlobalInfo.serverAddress);
	}

	private void init() {
		handlerAD.removeCallbacksAndMessages(null);
		handlerTimeDisplay.removeCallbacksAndMessages(null);
		handlerTimer.removeCallbacksAndMessages(null);
		handlerUserInfo.removeCallbacksAndMessages(null);
		handlerAutoConnect.removeCallbacksAndMessages(null);
		SharedPreferencesConfig.read(MainActivity.this);
		// 一定要先读完配置数据，再启动handler
		handlerAutoConnect.post(runnableAutoConnect);
		handlerTimer.post(runnableTimer);
		handlerTimeDisplay.post(runnableTimeDisplay);
		if (GlobalInfo.password.length() > 0 && GlobalInfo.autoLogin == true) {
			Message msg = Message.obtain();
			msg.arg1 = sessionKey.generateNewSessionKey();
			msg.arg2 = 5;
			msg.what = LOGIN;
			handlerUserInfo.sendMessage(msg);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Debug.d(TAG, "onNewIntent()");
		init();
		super.onNewIntent(intent);
	}

	class DebugButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnTest1:
				sendWirelessData(ProtocolData.STATISTICS, GlobalInfo.boundJSQ.add, ProtocolData.STATISTICS_CLEAR);
				break;
			case R.id.sendButton:
				// dataStatistics();
				sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_BROAD_RECEIVER_ACTION).putExtra(GlobalInfo.BROADCAST_SHOW_ACTIVITY, true));
				// UART.send("bb测试测试aa".getBytes());
				// sendWirelessData(ProtocolData.STATISTICS,
				// GlobalInfo.boundJSQ.add);
				// if (needGetJsqDataStatistics == false &&
				// needClearJsqDataStatistics == false &&
				// pauseProtocolDataProcess == false) {
				// needGetJsqDataStatistics = true;
				// Message next_msg = new Message();
				// next_msg.what = ProtocolData.STATISTICS;
				// next_msg.obj = ProtocolData.STATISTICS_GET;
				// handlerProtocolDataProcess.sendMessage(next_msg);
				// }
				break;
			case R.id.clsButton:
				showEditText.setText("");
				break;
			case R.id.tglBtnEnableHardwareAccelerated:
				if (tglBtnEnableHardwareAccelerated.isChecked()) {
					GlobalInfo.enableWebViewHardwareAccelerated = true;
				} else {
					GlobalInfo.enableWebViewHardwareAccelerated = false;
				}
				break;
			case R.id.tglBtnUART:
				if (tglBtnUART.isChecked()) {
					OpenComPort(UART);
				} else {
					CloseComPort(UART);
				}
				break;
			case R.id.tglBtnShowUARTDebug:
				if (tglBtnShowUARTDebug.isChecked()) {
					showEditText.setVisibility(View.VISIBLE);
				} else {
					showEditText.setVisibility(View.GONE);
				}
				break;
			case R.id.toggleBtn_AD:
				if (toggleBtn_AD.isChecked()) {
					handlerAD.removeCallbacks(runnableAD);
					handlerAD.postDelayed(runnableAD, GlobalInfo.startADTime);
				} else {
					handlerAD.removeCallbacks(runnableAD);
				}
				GlobalInfo.enableAD = toggleBtn_AD.isChecked();
				break;
			case R.id.tglBtnDebug:
				if (tglBtnDebug.isChecked()) {
					layoutDebugToolbar.setVisibility(View.VISIBLE);
				} else {
					layoutDebugToolbar.setVisibility(View.GONE);
				}
				break;
			case R.id.adButton:
				startActivity(new Intent(MainActivity.this, TestActivity.class));
				break;
			case R.id.btnUpdate:
				// UpdateManager manager = new UpdateManager(MainActivity.this);
				// 检查软件更新
				// manager.checkUpdate();
				break;
			case R.id.exitButton:
				System.exit(0);
				break;
			default:
			}
		}
	}

	OnClickListener ButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnOutWater:
				outWater();
				break;
			case R.id.btnDeviceManagement:
				startActivity(new Intent(MainActivity.this, com.ywangwang.gxj.device_management.DeviceManagementActivity.class));
				break;
			case R.id.btnWaterInformation:
				startActivity(new Intent(MainActivity.this, WaterInfoActivity.class));
				break;
			case R.id.btnLifeServices:
				startActivity(new Intent(MainActivity.this, LifeServicesActivity.class));
				break;
			case R.id.btnWebViewMain:
				startActivity(new Intent(MainActivity.this, WebViewMain.class));
				break;
			default:
			}
			if (v.getId() != R.id.tglBtnChildLock)
				tglBtnChildLock.setChecked(false);
		}
	};

	private class SerialControl extends SerialController {
		public SerialControl(String sPort, int iBaudRate) {
			super(sPort, iBaudRate);
		}

		@Override
		protected void onDataReceived(final ComData ComRecData) {
			runOnUiThread(new Runnable() {
				public void run() {
					CheckData checkData = new CheckData();
					if (checkData.addData(ComRecData.bRec, ComRecData.bRec.length)) {
						if ((checkData.checkedData[0] & 0xFF) == ProtocolData.WIRELESS_SYNC_WORD) {
							if ((checkData.checkedData[8] & 0xFF) < 100) {
								GlobalInfo.jsqStatus.setData(checkData.checkedData);
							} else {
								GlobalInfo.wirelessData.setData(checkData.checkedData);
								if (GlobalInfo.wirelessData.commandOrStatus == ProtocolData.SEARCHING || GlobalInfo.wirelessData.commandOrStatus == ProtocolData.BINDING) {
									if (GlobalInfo.wirelessData.commandOrStatus == ProtocolData.SEARCHING) {
										sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_BIND_DEVICE_ACTION).putExtra(GlobalInfo.BROADCAST_NEW_WIRELESS_DATA_SEARCHING, checkData.checkedData));
									} else {
										sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_BIND_DEVICE_ACTION).putExtra(GlobalInfo.BROADCAST_NEW_WIRELESS_DATA_BINDING, checkData.checkedData));
									}
								} else if (GlobalInfo.wirelessData.subDeviceAdd == GlobalInfo.boundJSQ.add) {
									switch (GlobalInfo.wirelessData.commandOrStatus) {
									case ProtocolData.ACK:
										if (GlobalInfo.wirelessData.parameter == ProtocolData.STATISTICS_CLEARED) {
											needClearJsqDataStatistics = false;
										}
										break;
									case ProtocolData.ACTIVATION:
										break;
									case ProtocolData.STATISTICS:
										if (needGetJsqDataStatistics == true) {
											needGetJsqDataStatistics = false;
											if (GlobalInfo.todayJsqDataStatistics.averageTDSIn == 0) {
												GlobalInfo.todayJsqDataStatistics.averageTDSIn = GlobalInfo.wirelessData.average_TDS_In;
											} else {
												GlobalInfo.todayJsqDataStatistics.averageTDSIn = (GlobalInfo.todayJsqDataStatistics.averageTDSIn + GlobalInfo.wirelessData.average_TDS_In) >> 1;
											}
											if (GlobalInfo.todayJsqDataStatistics.averageTDSOut == 0) {
												GlobalInfo.todayJsqDataStatistics.averageTDSOut = GlobalInfo.wirelessData.average_TDS_Out;
											} else {
												GlobalInfo.todayJsqDataStatistics.averageTDSOut = (GlobalInfo.todayJsqDataStatistics.averageTDSOut + GlobalInfo.wirelessData.average_TDS_Out) >> 1;
											}
											GlobalInfo.todayJsqDataStatistics.totalWaterIn += (float) (GlobalInfo.wirelessData.total_Pulses_In) / (float) (GlobalInfo.wirelessData.flow_Sensor_Hz) / 60f;
											GlobalInfo.todayJsqDataStatistics.totalWaterOut += (float) (GlobalInfo.wirelessData.total_Pulses_Out) / (float) (GlobalInfo.wirelessData.flow_Sensor_Hz) / 60f;
											GlobalInfo.todayJsqDataStatistics.totalFilterWaterTimes += GlobalInfo.wirelessData.total_Filter_Water_Times;
										}
										break;
									case ProtocolData.JSQ_FAULT:
										break;
									default:
										break;
									}
									// Message msg = new Message();
									// msg.what =
									// GlobalInfo.wirelessData.commandOrStatus;
									// msg.obj =
									// GlobalInfo.wirelessData.parameter;
									// handlerProtocolDataProcess.sendMessage(msg);
								}
							}
						} else if ((checkData.checkedData[0] & 0xFF) == ProtocolData.CONTROL_BOARD_SYNC_WORD) {
							GlobalInfo.gxjStatus.setData(checkData.checkedData);
						}
						String printData = new String();
						printData += "CheckedData={";
						for (int p = 0; p < checkData.checkedData.length; p++) {
							printData += String.format("%X", checkData.checkedData[p]) + ",";
						}
						printData += "}>>>>>>>length=" + checkData.checkedData.length + "\r\n";
						if (tglBtnDebug.isChecked()) {
							showEditText.append(printData);
							showEditText.setSelection(showEditText.length());
						}
					}
					// String printData = new String();
					// printData += "ComRecData.bRec={";
					// for (int p = 0; p < ComRecData.bRec.length; p++) {
					// printData += String.format("%X", ComRecData.bRec[p]) +
					// ",";
					// }
					// printData += "}>>>>>>>length=" + ComRecData.bRec.length +
					// "\r\n";
					// showEditText.append(printData);
					// showEditText.setSelection(showEditText.length());
				}
			});
		}
	}

	// ----------------------------------------------------串口发送
	private void sendPortData(SerialController ComPort, byte[] data) {
		Debug.d("UART_Send_Data=", Arrays.toString(data));
		if (ComPort != null && ComPort.isOpen()) {
			ComPort.send(data);
		}
	}

	// ----------------------------------------------------关闭串口
	private void CloseComPort(SerialController ComPort) {
		if (ComPort != null) {
			ComPort.stopSend();
			ComPort.close();
		}
	}

	// ----------------------------------------------------打开串口
	private void OpenComPort(SerialController ComPort) {
		String info = null;
		try {
			ComPort.open();
		} catch (SecurityException e) {
			info = "打开串口失败:没有串口读/写权限!";
		} catch (IOException e) {
			info = "打开串口失败:未知错误!";
		} catch (InvalidParameterException e) {
			info = "打开串口失败:参数错误!";
		}
		if (info != null && GlobalInfo.debug == true) {
			Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		MainActivity.resetADTimer();
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onResume() {
		Debug.d(TAG, "onResume()");
		refreshDisplay();
		refreshInfoDisplay();
		resetADTimer();
		if (haveNewVersion) {
			haveNewVersion = false;
			sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_GXJ_SERVER_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_GXJ, true));
		}
		isShow = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		Debug.d(TAG, "onPause()");
		isShow = false;
		stopADTimer();
		toast.hide();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Debug.d(TAG, "onDestroy()");
		handlerAD.removeCallbacksAndMessages(null);
		handlerTimeDisplay.removeCallbacksAndMessages(null);
		handlerTimer.removeCallbacksAndMessages(null);
		handlerUserInfo.removeCallbacksAndMessages(null);
		handlerAutoConnect.removeCallbacksAndMessages(null);
		unregisterReceiver(broadcastReceiver);
		sessionKey.cleanSessionKey();
		socketSessionKey.cleanSessionKey();
		CloseComPort(UART);
		TcpManager.close();
		super.onDestroy();
	}

	public static void resetADTimer() {
		if (GlobalInfo.enableAD) {
			handlerAD.removeCallbacks(runnableAD);
			handlerAD.postDelayed(runnableAD, GlobalInfo.startADTime);
		}
	}

	public static void stopADTimer() {
		handlerAD.removeCallbacks(runnableAD);
	}

	OnClickListener RdoBtnTemperatureListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// rdoBtnWaterAmount150.setVisibility(View.VISIBLE);
			rdoBtnWaterAmount260.setVisibility(View.VISIBLE);
			rdoBtnWaterAmount300.setVisibility(View.VISIBLE);
			rdoBtnWaterAmountCustom.setVisibility(View.VISIBLE);
			rdoBtnCoolWater.setChecked(false);
			GlobalInfo.selectCoolWater = false;
			if (rdoBtnWaterAmount150.isChecked() || rdoBtnWaterAmount260.isChecked() || rdoBtnWaterAmount300.isChecked() || rdoBtnWaterAmountCustom.isChecked()) {
				rdoBtnCustom1.setChecked(false);
				rdoBtnCustom2.setChecked(false);
			}
			if (v.getId() != R.id.rdoBtnRoomTemperature)
				rdoBtnRoomTemperature.setChecked(false);
			if (v.getId() != R.id.rdoBtnMilk)
				rdoBtnMilk.setChecked(false);
			if (v.getId() != R.id.rdoBtnHoney)
				rdoBtnHoney.setChecked(false);
			if (v.getId() != R.id.rdoBtnBoiling)
				rdoBtnBoiling.setChecked(false);
			GlobalInfo.selectRoomTemperatureWater = false;
			if (v.getId() == R.id.rdoBtnRoomTemperature) {
				GlobalInfo.selectRoomTemperatureWater = true;
				GlobalInfo.setTemperature = GlobalInfo.RoomTemperatureValue;
				GlobalInfo.setMode = getResources().getString(R.string.room_temperature);
			} else if (v.getId() == R.id.rdoBtnMilk) {
				GlobalInfo.setTemperature = GlobalInfo.MilkTemperatureValue;
				GlobalInfo.setMode = getResources().getString(R.string.milk);
			} else if (v.getId() == R.id.rdoBtnHoney) {
				GlobalInfo.setTemperature = GlobalInfo.HoneyTemperatureValue;
				GlobalInfo.setMode = getResources().getString(R.string.honey);
			} else if (v.getId() == R.id.rdoBtnBoiling) {
				GlobalInfo.setTemperature = GlobalInfo.BoilingTemperatureValue;
				GlobalInfo.setMode = getResources().getString(R.string.boiling);
			}
			refreshInfoDisplay();
		}
	};

	OnClickListener RdoBtnWaterAmountListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.rdoBtnWaterAmountCustom) {
				CustomDialog.ShowCustomWaterAmountDialog(MainActivity.this, v);
			} else {
				RdoBtnWaterAmountOnClick(v);
			}
		}
	};

	OnClickListener RdoBtnCustomListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			RdoBtnCustomOnClick(v);
		}
	};
	OnClickListener RdoBtnCoolWaterListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			rdoBtnRoomTemperature.setChecked(false);
			rdoBtnMilk.setChecked(false);
			rdoBtnHoney.setChecked(false);
			rdoBtnBoiling.setChecked(false);
			rdoBtnWaterAmount150.setChecked(false);
			rdoBtnWaterAmount260.setChecked(false);
			rdoBtnWaterAmount300.setChecked(false);
			rdoBtnWaterAmountCustom.setChecked(false);
			GlobalInfo.selectRoomTemperatureWater = false;

			// rdoBtnWaterAmount150.setVisibility(View.INVISIBLE);
			rdoBtnWaterAmount260.setVisibility(View.INVISIBLE);
			rdoBtnWaterAmount300.setVisibility(View.INVISIBLE);
			rdoBtnWaterAmountCustom.setVisibility(View.INVISIBLE);

			rdoBtnCustom1.setChecked(false);
			rdoBtnCustom2.setChecked(false);

			GlobalInfo.selectCoolWater = true;
			GlobalInfo.setTemperature = GlobalInfo.COOL_WATER;
			GlobalInfo.setMode = getResources().getString(R.string.cool_water);
			refreshInfoDisplay();
		}
	};

	OnLongClickListener RdoBtnCustomLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			CustomDialog.ShowCustomOutWaterDialog(MainActivity.this, v);
			return false;
		}
	};

	private void refreshInfoDisplay() {
		tglBtnChildLock.setChecked(false);
		if (GlobalInfo.selectCoolWater || GlobalInfo.selectRoomTemperatureWater) {
			tvTemperature.setText(GlobalInfo.setMode);
		} else {
			tvTemperature.setText(GlobalInfo.setTemperature + "℃");
		}
		if (GlobalInfo.selectCoolWater) {
			tvWaterAmount.setText(GlobalInfo.setMode);
		} else {
			tvWaterAmount.setText(GlobalInfo.setWaterAmount + "mL");
		}
		tvMode.setText(GlobalInfo.setMode);
	}

	private byte[] getCommandData() {
		byte[] orderData = { (byte) ProtocolData.CONTROL_BOARD_SYNC_WORD, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		if (GlobalInfo.selectCoolWater) {
			orderData[4] = ProtocolData.COOL_WATER;
		} else if (GlobalInfo.selectRoomTemperatureWater) {
			orderData[4] = ProtocolData.ROOM_TEMPERATURE_WATER;
			orderData[6] = (byte) (GlobalInfo.setWaterAmount / 10);
		} else {
			orderData[4] = ProtocolData.HEAT_WATER;
			orderData[5] = (byte) (GlobalInfo.setTemperature);
			if (orderData[5] > 98) {
				orderData[5] = 98;// 传给管线机 控制板 制热最大温度 是98摄氏度
			}
			orderData[6] = (byte) (GlobalInfo.setWaterAmount / 10);
		}
		int checkData = 0;
		for (int i = 1; i < 7; i++) {
			checkData += (orderData[i] & 0xFF);
		}
		orderData[7] = (byte) (checkData % 0x100);
		return orderData;
	}

	private boolean allow() {
		if (GlobalInfo.testMode == true) {
			return true;
		} else if (GlobalInfo.isBoundWaterCode == false || GlobalInfo.boundWaterCode.getStatus() == WaterCode.ABATE) {
			AlertDialog.Builder builer = new Builder(this);
			builer.setTitle("取水码");
			if (GlobalInfo.isBoundWaterCode == false) {
				builer.setMessage("未绑定取水码\n\n请绑定！");
			} else {
				builer.setMessage("绑定的取水码无效\n\n请重新绑定！");
			}
			builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(MainActivity.this, com.ywangwang.gxj.device_management.DeviceManagementActivity.class);
					intent.putExtra("show", R.id.rdoBtnWaterCode);
					startActivity(intent);
				}
			});
			builer.setNegativeButton("取消", null);
			builer.show();
			return false;
		} else if (GlobalInfo.boundWaterCode.getValidDays() < 3) {
			Debug.i(TAG, "用户绑定的取水码有效天数小于3天，开始更新取水码。");
			Message newMsg3 = Message.obtain();
			newMsg3.arg1 = sessionKey.generateNewSessionKey();
			newMsg3.arg2 = 6;
			newMsg3.what = UPDATE_WATER_CODE;
			handlerUserInfo.sendMessage(newMsg3);
		}
		return true;
	}

	private void outWater() {
		if (allow() == false) {
			return;
		}
		if (GlobalInfo.selectCoolWater || rdoBtnCustom1.isChecked() || rdoBtnCustom2.isChecked() || ((rdoBtnRoomTemperature.isChecked() || rdoBtnMilk.isChecked() || rdoBtnHoney.isChecked() || rdoBtnBoiling.isChecked()) && (rdoBtnWaterAmount150.isChecked() || rdoBtnWaterAmount260.isChecked() || rdoBtnWaterAmount300.isChecked() || rdoBtnWaterAmountCustom.isChecked()))) {
			if (GlobalInfo.enableChildLock && !tglBtnChildLock.isChecked()) {
				toast.setImageText(R.drawable.lock, "童锁未打开").show();
				return;
			}
			sendPortData(UART, getCommandData());
			startActivity(new Intent(MainActivity.this, OutWaterActivity.class));
		} else {
			toast.setImageText(R.drawable.cup, "请选择水温和水量").show();
			return;
		}
	}

	private void stopOutWater() {
		byte[] data = { (byte) 0xFE, 0x08, 0x00, 0x00, 0x01, 0x00, 0x00, 0x09 };
		sendPortData(UART, data);
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getExtras().getInt(GlobalInfo.BROADCAST_UPDATE_RDOBTN_CUSTOM) == R.id.rdoBtnCustom1) {
				refreshDisplay();
				rdoBtnCustom1.setChecked(true);
				RdoBtnCustomOnClick(rdoBtnCustom1);
			} else if (intent.getExtras().getInt(GlobalInfo.BROADCAST_UPDATE_RDOBTN_CUSTOM) == R.id.rdoBtnCustom2) {
				refreshDisplay();
				rdoBtnCustom2.setChecked(true);
				RdoBtnCustomOnClick(rdoBtnCustom2);
			} else if (intent.getExtras().getBoolean(GlobalInfo.BROADCAST_UPDATE_CUSTOM_WATER_AMOUNT) == true) {
				refreshDisplay();
				RdoBtnWaterAmountOnClick(rdoBtnWaterAmountCustom);
			} else if (intent.getExtras().getInt(GlobalInfo.BROADCAST_GXJ_KEY) == ProtocolData.STANDBY) {
				stopOutWater();
			} else if (intent.getExtras().getInt(GlobalInfo.BROADCAST_JSQ_KEY) == ProtocolData.GET_STATUS) {
				if (GlobalInfo.boundJSQ.used)
					sendWirelessData(ProtocolData.GET_STATUS, GlobalInfo.boundJSQ.add);
			} else if (intent.getExtras().getBoolean(GlobalInfo.BROADCAST_STOP_SEARCH_DEVICE) == true) {
				sendWirelessData(ProtocolData.ACK);
			} else if (intent.getExtras().getBoolean(GlobalInfo.BROADCAST_SEARCH_DEVICE) == true) {
				sendWirelessData(ProtocolData.SEARCHING);
			} else if (intent.getExtras().getIntArray(GlobalInfo.BROADCAST_BIND_DEVICE) != null) {
				if (intent.getExtras().getIntArray(GlobalInfo.BROADCAST_BIND_DEVICE)[0] == ProtocolData.UNDER_WATER_PURIFIER) {
					sendWirelessData(ProtocolData.BINDING, intent.getExtras().getIntArray(GlobalInfo.BROADCAST_BIND_DEVICE)[1]);
				}
			} else if (intent.getExtras().getIntArray(GlobalInfo.BROADCAST_WRITE_GXJ_DETAILS) != null) {
				if (syncTime.isReadTimeSuccess() == true) {
					StatisticsDataReadOrWrite.gxjWrite(GlobalInfo.databaseHelperGXJ, intent.getExtras().getIntArray(GlobalInfo.BROADCAST_WRITE_GXJ_DETAILS));
				}
			} else if (intent.getExtras().getBoolean(GlobalInfo.BROADCAST_HAVE_NEW_GXJ_VERSION, false) == true) {
				haveNewVersion = true;
				if (haveNewVersion && isShow) {
					haveNewVersion = false;
					sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_GXJ_SERVER_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_GXJ, true));
				}
			} else if (intent.getExtras().getBoolean(GlobalInfo.BROADCAST_SOCKET_LOGOUT, false)) {
				socketLogout();
			} else if (intent.getExtras().getString(GlobalInfo.BROADCAST_RECEIVE_NEW_MESSAGE, null) != null) {
				MoMessage moMsg = MoMessage.analyzeJsonData(intent.getExtras().getString(GlobalInfo.BROADCAST_RECEIVE_NEW_MESSAGE, null));
				if (moMsg != null) {
					// TcpManager.sendMSG("ACK" + moMsg.sessionKey);
					toast.setText(moMsg.toString()).show();
					if (moMsg.sessionKey == socketSessionKey.getSessionKey()) {
						if (moMsg.cmd == MoMessage.LOGIN_SUCCESS) {
							User user = User.analyzeJsonData(moMsg.jsonData);
							Message newMsg = Message.obtain();
							newMsg.arg1 = moMsg.sessionKey;
							if (user != null) {
								newMsg.what = LOGIN_SUCCESS;
								newMsg.obj = user;
							} else {
								newMsg.what = LOGIN_FAIL;
								newMsg.obj = "数据解析失败";
							}
							socketHandler.sendMessage(newMsg);
						} else if (moMsg.cmd == MoMessage.LOGIN_FAIL) {
							Message newMsg = Message.obtain();
							newMsg.arg1 = moMsg.sessionKey;
							newMsg.what = LOGIN_FAIL;
							newMsg.obj = moMsg.info;
							socketHandler.sendMessage(newMsg);
						}
					} else if (moMsg.cmd == MoMessage.SEND_MESSAGE) {
						toast.setImageText(R.drawable.message, "收到新消息\n" + StrConv.Decode(moMsg.info)).setDuration(Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	};

	private void refreshDisplay() {
		if (GlobalInfo.enableCoolWater) {
			rdoBtnWaterAmount150.setVisibility(View.GONE);
			rdoBtnCoolWater.setVisibility(View.VISIBLE);
		} else {
			rdoBtnWaterAmount150.setVisibility(View.VISIBLE);
			rdoBtnCoolWater.setVisibility(View.GONE);
		}
		if (GlobalInfo.enableChildLock) {
			tglBtnChildLock.setVisibility(View.VISIBLE);
		} else {
			tglBtnChildLock.setVisibility(View.GONE);
		}
		if (GlobalInfo.debug) {
			tglBtnDebug.setVisibility(View.VISIBLE);
		} else {
			tglBtnDebug.setVisibility(View.GONE);
		}
		rdoBtnCustom1.setText(GlobalInfo.Custom1Name + "\n" + GlobalInfo.Custom1TemperatureValue + "℃/" + GlobalInfo.Custom1WaterAmountValue + "mL");
		rdoBtnCustom2.setText(GlobalInfo.Custom2Name + "\n" + GlobalInfo.Custom2TemperatureValue + "℃/" + GlobalInfo.Custom2WaterAmountValue + "mL");
		rdoBtnWaterAmountCustom.setText("手动选择水量\n" + GlobalInfo.CustomWaterAmountValue + "mL");
	}

	private void RdoBtnWaterAmountOnClick(View v) {
		rdoBtnCoolWater.setChecked(false);
		GlobalInfo.selectCoolWater = false;
		if (rdoBtnRoomTemperature.isChecked() || rdoBtnMilk.isChecked() || rdoBtnHoney.isChecked() || rdoBtnBoiling.isChecked()) {
			rdoBtnCustom1.setChecked(false);
			rdoBtnCustom2.setChecked(false);
		}
		if (v.getId() != R.id.rdoBtnWaterAmount150)
			rdoBtnWaterAmount150.setChecked(false);
		if (v.getId() != R.id.rdoBtnWaterAmount260)
			rdoBtnWaterAmount260.setChecked(false);
		if (v.getId() != R.id.rdoBtnWaterAmount300)
			rdoBtnWaterAmount300.setChecked(false);
		if (v.getId() != R.id.rdoBtnWaterAmountCustom)
			rdoBtnWaterAmountCustom.setChecked(false);
		if (v.getId() == R.id.rdoBtnWaterAmount150) {
			GlobalInfo.setWaterAmount = GlobalInfo.WaterAmount150Value;
		} else if (v.getId() == R.id.rdoBtnWaterAmount260) {
			GlobalInfo.setWaterAmount = GlobalInfo.WaterAmount260Value;
		} else if (v.getId() == R.id.rdoBtnWaterAmount300) {
			GlobalInfo.setWaterAmount = GlobalInfo.WaterAmount300Value;
		} else if (v.getId() == R.id.rdoBtnWaterAmountCustom) {
			GlobalInfo.setWaterAmount = GlobalInfo.CustomWaterAmountValue;
		}
		refreshInfoDisplay();
	}

	private void RdoBtnCustomOnClick(View v) {
		// rdoBtnWaterAmount150.setVisibility(View.VISIBLE);
		rdoBtnWaterAmount260.setVisibility(View.VISIBLE);
		rdoBtnWaterAmount300.setVisibility(View.VISIBLE);
		rdoBtnWaterAmountCustom.setVisibility(View.VISIBLE);
		rdoBtnCoolWater.setChecked(false);
		GlobalInfo.selectCoolWater = false;
		rdoBtnRoomTemperature.setChecked(false);
		rdoBtnMilk.setChecked(false);
		rdoBtnHoney.setChecked(false);
		rdoBtnBoiling.setChecked(false);
		rdoBtnWaterAmount150.setChecked(false);
		rdoBtnWaterAmount260.setChecked(false);
		rdoBtnWaterAmount300.setChecked(false);
		rdoBtnWaterAmountCustom.setChecked(false);
		GlobalInfo.selectRoomTemperatureWater = false;
		if (v.getId() == R.id.rdoBtnCustom1) {
			GlobalInfo.setTemperature = GlobalInfo.Custom1TemperatureValue;
			GlobalInfo.setWaterAmount = GlobalInfo.Custom1WaterAmountValue;
			GlobalInfo.setMode = GlobalInfo.Custom1Name;
			rdoBtnCustom2.setChecked(false);
		} else if (v.getId() == R.id.rdoBtnCustom2) {
			GlobalInfo.setTemperature = GlobalInfo.Custom2TemperatureValue;
			GlobalInfo.setWaterAmount = GlobalInfo.Custom2WaterAmountValue;
			GlobalInfo.setMode = GlobalInfo.Custom2Name;
			rdoBtnCustom1.setChecked(false);
		}
		refreshInfoDisplay();
	}

	private byte[] sendWirelessData(int command) {
		return sendWirelessData(command, 0, 0);
	}

	private byte[] sendWirelessData(int command, int subDeviceAdd) {
		return sendWirelessData(command, subDeviceAdd, 0);
	}

	private byte[] sendWirelessData(int command, int subDeviceAdd, int data) {
		int hostAdd = GlobalInfo.hostAdd;
		int checkData = 0;
		switch (command) {
		case ProtocolData.ACK:
			break;
		case ProtocolData.ACTIVATION:
			break;
		case ProtocolData.GET_STATUS:
			break;
		case ProtocolData.SEARCHING:
			hostAdd = 0xFFFF;
			break;
		case ProtocolData.BINDING:
			hostAdd = 0xFFFF;
			break;
		default:
			break;
		}
		byte[] sendData = { (byte) ProtocolData.WIRELESS_SYNC_WORD, (byte) 0x0B, (byte) ProtocolData.PROTOCOL_VER, (byte) ProtocolData.HOST, (byte) (subDeviceAdd / 0x100), (byte) (subDeviceAdd % 0x100), (byte) (hostAdd / 0x100), (byte) (hostAdd % 0x100), (byte) command, (byte) data, 0x0 };
		for (int j = 1; j < ((sendData[1] & 0xFF) - 1); j++) {
			checkData += (sendData[j] & 0xFF);
		}
		sendData[(sendData[1] & 0xFF) - 1] = (byte) (checkData % 0x100);
		sendPortData(UART, sendData);
		return sendData;
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
	 * 从网络获取数据线程
	 */
	void getUserData() {
	}

	boolean needGetJsqDataStatistics = false;// 获取统计数据标记
	boolean needClearJsqDataStatistics = false;// 清除净水器统计数据标记
	public static boolean pauseProtocolDataProcess = false;
	boolean dataStatistics_ing = false;// 正在统计数据标记

	/*
	 * 数据统计线程
	 */
	void dataStatistics() {
		if (dataStatistics_ing) {// 如果正在统计数据，就退出
			return;
		}
		dataStatistics_ing = true;// 标记正在统计数据
		new Thread() {
			@Override
			public void run() {
				// 开始统计数据
				int times = 5;
				needGetJsqDataStatistics = true; // 标记获取统计数据标记有效
				while (needGetJsqDataStatistics == true && times-- > 0) { // 如果获取统计数据标记有效，且次数不超
					while (pauseProtocolDataProcess == true) {// 如果暂停统计数据处理标记有效，就暂停线程
					}
					sendWirelessData(ProtocolData.STATISTICS, GlobalInfo.boundJSQ.add, ProtocolData.STATISTICS_GET);// 通过串口发送获取统计数据
					GlobalInfo.sleep(1800);// sleep1800毫秒
				}
				times = 5;
				if (needGetJsqDataStatistics == false) {// 如果获取统计数据标记无效，说明已经收到同步数据
					needClearJsqDataStatistics = true;// 标记清除净水器统计数据标记有效
				}
				while (needClearJsqDataStatistics == true && times-- > 0) {
					while (pauseProtocolDataProcess == true) {// 如果暂停统计数据处理标记有效，就暂停线程
					}
					sendWirelessData(ProtocolData.STATISTICS, GlobalInfo.boundJSQ.add, ProtocolData.STATISTICS_CLEAR);// 通过串口发送清除净水器统计数据
					GlobalInfo.sleep(1800);// sleep1800毫秒
				}
				if (needClearJsqDataStatistics == false && needClearJsqDataStatistics == false) {// 获取成功，就把数据存入数据库
					// 把统计数据存入数据库
					StatisticsDataReadOrWrite.jsqWrite();
				}
				needGetJsqDataStatistics = false;// 标记获取统计数据标记无效
				needClearJsqDataStatistics = false;// 标记清除净水器统计数据标记无效

				dataStatistics_ing = false;// 标记统计数据完成
			}
		}.start();
	}

	Handler handlerTimer = new Handler(); // 定时Handler
	Runnable runnableTimer = new Runnable() {
		@Override
		public void run() {
			boolean needGetAndClearJsqDataStatistics = false;
			boolean needGetUserData = true;
			long delayMillis = 0L;
			final int SET_MINUTE = 5;// 设置定时在每一小时的分钟数
			handlerTimer.removeCallbacks(runnableTimer);
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);
			delayMillis = minute > (SET_MINUTE - 1) ? (60 + SET_MINUTE - minute) * 60 * 1000 : (SET_MINUTE - minute) * 60 * 1000;
			switch (hourOfDay) {
			case 9:
			case 14:
			case 23:
				needGetUserData = true;
				break;
			case 10:
			case 16:
			case 0:
				needGetAndClearJsqDataStatistics = true;
				break;
			default:
				break;
			}
			if (syncTime.isReadTimeSuccess() == false) {
				Debug.w("更新时间", "更新时间");
				syncTime.syncNetworkTime();
				delayMillis = 2 * 60 * 1000L;
			} else {
				if (syncTime.isSyncTimeSuccess() == false) {
					syncTime.syncNetworkTime();
				}
				syncTime.updataTimeBackup();
				if (GlobalInfo.todayJsqDataStatistics.time == 0) {
					StatisticsDataReadOrWrite.jsqDataInit();
				}
				if (needGetUserData == true) {
					Debug.w("更新网络数据", "更新网络数据");
					getUserData();
					needGetUserData = false;
				} else if (needGetAndClearJsqDataStatistics == true) {
					Debug.w("数据统计", "数据统计");
					dataStatistics();
					needGetAndClearJsqDataStatistics = false;
				}
			}
			handlerTimer.postDelayed(runnableTimer, delayMillis);
			Debug.d("handlerTimer定时分钟数=", delayMillis / 1000 / 60 + "");
			Debug.d("现在时间=", DateFormat.format("yyyy年MM月dd日 EEEE HH:mm:ss", System.currentTimeMillis()) + "  定时时间=" + DateFormat.format("yyyy年MM月dd日 EEEE HH:mm:ss", System.currentTimeMillis() + delayMillis));
			sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_SERVER_BROAD_RECEIVER_ACTION).putExtra("WAKE_UP", true));
		}
	};

	/**
	 * 
	 * msg.arg1 SessionKey ,msg.arg2 剩余自动登录次数
	 */
	@SuppressLint("HandlerLeak")
	Handler handlerUserInfo = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (sessionKey.getSessionKey() != msg.arg1) {
				return;
			}
			final int newArg2 = msg.arg2;
			switch (msg.what) {
			case LOGIN:
				if (GlobalInfo.Logined == true) {
					Debug.i(TAG, "用户已登录，取消自动登录");
					break;
				}
				Debug.i(TAG, "开始自动登录");
				new Thread() {
					@Override
					public void run() {
						Message newMsg2 = Message.obtain();
						newMsg2.arg1 = sessionKey.generateNewSessionKey();
						newMsg2.arg2 = newArg2;
						Operaton operaton = new Operaton(MainActivity.this);
						String result = operaton.login(GlobalInfo.username, GlobalInfo.password);
						if (result.equals("1") == true) {
							newMsg2.what = LOGIN_SUCCESS;
						} else {
							newMsg2.what = LOGIN_FAIL;
							newMsg2.obj = result;
						}
						handlerUserInfo.sendMessage(newMsg2);
						super.run();
					}
				}.start();
				break;
			case LOGIN_SUCCESS:
				String log = null;
				GlobalInfo.Logined = true;
				if (GlobalInfo.isBoundWaterCode == true) {
					if (GlobalInfo.boundWaterCode.getType() != WaterCode.PERMANENT_CODE) {
						Message newMsg3 = Message.obtain();
						newMsg3.arg1 = sessionKey.generateNewSessionKey();
						newMsg3.arg2 = 6;
						newMsg3.what = UPDATE_WATER_CODE;
						handlerUserInfo.sendMessage(newMsg3);
						log = "自动登录成功，开始更新取水码";
					} else {
						log = "自动登录成功，用户已绑定永久取水码";
					}
				} else {
					log = "自动登录成功，用户未绑定取水码。";
				}
				Debug.i(TAG, log);
				break;
			case LOGIN_FAIL:
				Message newMsg = Message.obtain();
				newMsg.what = LOGIN;
				newMsg.arg1 = sessionKey.generateNewSessionKey();
				if (msg.arg2 > 0) {
					newMsg.arg2 = --msg.arg2;
					handlerUserInfo.sendMessageDelayed(newMsg, 10 * 60 * 1000L);
					Debug.w(TAG, (String) msg.obj + "，自动登录失败，10分钟后再次尝试自动登录，剩余次数=" + msg.arg2);
				} else {
					handlerUserInfo.sendMessageDelayed(newMsg, 2 * 60 * 60 * 1000L);
					Debug.w(TAG, (String) msg.obj + "，自动登录失败，2小时后再次尝试自动登录。");
				}
				break;
			case UPDATE_WATER_CODE:
				Debug.i(TAG, "开始更新取水码");
				new Thread() {
					@Override
					public void run() {
						Message newMsg3 = new Message();
						newMsg3.arg1 = sessionKey.generateNewSessionKey();
						newMsg3.arg2 = newArg2;
						Operaton operaton = new Operaton(MainActivity.this);
						String result = operaton.updateWaterCode(GlobalInfo.username, GlobalInfo.password, GlobalInfo.boundWaterCode.getNumber() + "");
						if (result.length() > 20) {
							List<WaterCode> waterCodes = new ArrayList<WaterCode>();
							waterCodes = JsonTools.getWaterCodesFormJSON("waterCode", result);
							if (waterCodes.size() > 0) {
								newMsg3.what = UPDATE_WATER_CODE_SUCCESS;
								newMsg3.obj = waterCodes.get(0);
							} else {
								newMsg3.what = UPDATE_WATER_CODE_FAIL;
								newMsg3.obj = "数据解析失败";
							}
						} else {
							newMsg3.what = UPDATE_WATER_CODE_FAIL;
							newMsg3.obj = result;
						}
						handlerUserInfo.sendMessage(newMsg3);
						super.run();
					}
				}.start();
				break;
			case UPDATE_WATER_CODE_SUCCESS:
				Debug.i(TAG, "，自动更新取水码成功。");
				GlobalInfo.boundWaterCode = (WaterCode) msg.obj;
				SharedPreferencesConfig.saveWaterCode(MainActivity.this, GlobalInfo.isBoundWaterCode, GlobalInfo.boundWaterCode);
				break;
			case UPDATE_WATER_CODE_FAIL:
				Message newMsg5 = Message.obtain();
				newMsg5.what = UPDATE_WATER_CODE;
				newMsg5.arg1 = sessionKey.generateNewSessionKey();
				if (msg.arg2 > 0) {
					newMsg5.arg2 = --msg.arg2;
					handlerUserInfo.sendMessageDelayed(newMsg5, 10 * 60 * 1000L);
					Debug.w(TAG, (String) msg.obj + "，自动更新取水码失败，10分钟后再次尝试自动更新取水码，剩余次数=" + msg.arg2);
				} else {
					Debug.w(TAG, (String) msg.obj + "，自动更新取水码失败，已停止自动更新取水码。");
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}; // 更新用户信息Handler

	Handler handlerAutoConnect = new Handler();
	Runnable runnableAutoConnect = new Runnable() {
		@Override
		public void run() {
			handlerAutoConnect.removeCallbacks(runnableAutoConnect);
			handlerAutoConnect.postDelayed(runnableAutoConnect, 3000);
			if (TcpManager.isConnect()) {
				if (GlobalInfo.online == false && socketLogining == false) {
					socketLogining = true;
					socketLogin(GlobalInfo.username, GlobalInfo.password);
				} else if (GlobalInfo.online == true) {
					if (Heartbeat.checkTimeout() == Heartbeat.SEND_ACK) {
						TcpManager.sendMSG("ACK");
					} else if (Heartbeat.checkTimeout() == Heartbeat.TIMEOUT) {
						GlobalInfo.online = false;
						TcpManager.reconnect();
					}
				}
			} else {
				GlobalInfo.online = false;
				if (GlobalInfo.password.length() > 0 && GlobalInfo.autoLogin == true && Net.isNetworkAvailable(MainActivity.this)) {
					Log.i(TAG, "正在连接到SOCKET服务器");
					TcpManager.connect();
				}
			}
		}
	};

	@SuppressLint("HandlerLeak")
	Handler socketHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Debug.d(TAG, "new handler msg.sessionKey=" + socketSessionKey.getSessionKey() + ",msg.what=" + msg.what);
			// 如果会话KEY和最后一次不同，就忽略此msg
			if (socketSessionKey.getSessionKey() != msg.arg1) {
				return;
			}
			socketSessionKey.cleanSessionKey();
			switch (msg.what) {
			case LOGIN_SUCCESS:
				Debug.d(TAG, "登录SOCKET服务器成功！");
				GlobalInfo.online = true;
				socketLogining = false;
				socketHandler.removeMessages(LOGIN_FAIL);
				break;
			case LOGIN_FAIL:
				Debug.d(TAG, "登录SOCKET服务器失败！");
				GlobalInfo.online = false;
				socketLogining = false;
				if (msg.arg2 == TIMEOUT) {
					TcpManager.reconnect();
				}
				break;
			default:
				break;
			}
		}
	};

	private void socketLogin(String username, String password) {
		Debug.i(TAG, "开始登陆SOCKET服务器");
		Message msg = Message.obtain();
		msg.arg1 = socketSessionKey.generateNewSessionKey();
		MoMessage moMsg = new MoMessage();
		moMsg.sessionKey = msg.arg1;
		moMsg.cmd = MoMessage.LOGIN;
		moMsg.type = MoMessage.TYPE_GXJ;
		moMsg.id = GlobalInfo.hostID;
		moMsg.loginKey = GlobalInfo.loginKey = new Random().nextInt(100000) + 1;
		User user = new User(username, password);
		try {
			moMsg.jsonData = new JSONObject(user.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (TcpManager.sendMSG(moMsg.toString())) {
			msg.what = LOGIN_FAIL;
			msg.arg2 = TIMEOUT;
			msg.obj = "登录SOCKET超时";
			socketHandler.sendMessageDelayed(msg, 10 * 1000L);
		} else {
			msg.what = LOGIN_FAIL;
			msg.obj = "登录SOCKET信息发送失败";
			socketHandler.sendMessage(msg);
		}
	}

	private boolean socketLogout() {
		Debug.i(TAG, "开始从SOCKET服务器注销");
		GlobalInfo.online = false;
		socketLogining = false;
		MoMessage moMsg = new MoMessage();
		moMsg.sessionKey = socketSessionKey.generateNewSessionKey();
		moMsg.cmd = MoMessage.LOGOUT;
		moMsg.id = GlobalInfo.hostID;
		moMsg.loginKey = GlobalInfo.loginKey;
		if (TcpManager.sendMSG(moMsg.toString())) {
			TcpManager.reconnect();
			return true;
		}
		return false;
	}
}
