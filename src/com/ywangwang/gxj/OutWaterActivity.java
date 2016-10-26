package com.ywangwang.gxj;

import com.ywangwang.gxj.lib.ProtocolData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class OutWaterActivity extends Activity {

	Button btnStop;

	TextView tvTemperature, tvWaterAmount, tvMode, tvOutTemperature, tvOutWaterAmount, tvCountDownTime;
	TextView tvGXJ, tvJSQ;
	SeekBar skBarOutWaterAmount;
	int countDownTime = GlobalInfo.OUT_WATER_COUNT_DOWN_TIME;
	int t = 0;
	Handler handler = new Handler();
	Runnable refreshRunnable;
	int outWaterAverageTDS = 0, outWaterWaterAmount = 0, outWaterTemperature = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.out_water);

		tvTemperature = (TextView) findViewById(R.id.include1).findViewById(R.id.tvTemperature);
		tvWaterAmount = (TextView) findViewById(R.id.include1).findViewById(R.id.tvWaterAmount);
		tvMode = (TextView) findViewById(R.id.include1).findViewById(R.id.tvMode);
		tvOutTemperature = (TextView) findViewById(R.id.tvOutTemperature);
		tvOutWaterAmount = (TextView) findViewById(R.id.tvOutWaterAmount);
		tvCountDownTime = (TextView) findViewById(R.id.tvCountDownTime);
		tvGXJ = (TextView) findViewById(R.id.tvGXJ);
		tvJSQ = (TextView) findViewById(R.id.tvJSQ);
		skBarOutWaterAmount = (SeekBar) findViewById(R.id.skBarOutWaterAmount);
		skBarOutWaterAmount.setEnabled(false);
		skBarOutWaterAmount.setMax(GlobalInfo.setWaterAmount);
		skBarOutWaterAmount.setProgress(0);

		btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (outWaterWaterAmount > 0 || GlobalInfo.selectCoolWater) {
					int[] temp = new int[3];
					temp[0] = outWaterAverageTDS;
					temp[1] = outWaterWaterAmount;
					if (GlobalInfo.selectCoolWater) {
						temp[2] = GlobalInfo.COOL_WATER;
					} else {
						temp[2] = GlobalInfo.setTemperature;
					}
					sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_WRITE_GXJ_DETAILS, temp));
				}
				sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_GXJ_KEY, 1));
				finish();
			}
		});
		MainActivity.pauseProtocolDataProcess = true;
		tvCountDownTime.setText(countDownTime-- + "");
		GlobalInfo.gxjStatus.clear();
		refreshInfoDisplay();
		refreshTvGXJ();
		refreshTvJSQ();
		refreshOutWaterDisplay();
		refreshRunnable = new Runnable() {
			@Override
			public void run() {
				refreshTvGXJ();
				refreshTvJSQ();
				refreshOutWaterDisplay();
				if (t++ % 5 == 0) {
					sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_JSQ_KEY, ProtocolData.GET_STATUS));
					if (GlobalInfo.gxjStatus.inTDS > 0) {
						outWaterAverageTDS = (outWaterAverageTDS + GlobalInfo.gxjStatus.inTDS) >> 1;
					}
					tvCountDownTime.setText(countDownTime-- + "");
					if (countDownTime < 1) {
						btnStop.callOnClick();
					}
				}
				handler.postDelayed(this, 200);
			}
		};
		handler.postDelayed(refreshRunnable, 200);
	}

	private void refreshInfoDisplay() {
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

	@SuppressLint("DefaultLocale")
	void refreshTvGXJ() {
		tvGXJ.setText("");
		tvGXJ.append("设备类型：" + GlobalInfo.gxjStatus.getDeviceType() + "\r\n");
		tvGXJ.append("设备状态：" + GlobalInfo.gxjStatus.getStatus() + "\r\n");
		tvGXJ.append("进水温度：" + GlobalInfo.gxjStatus.inTemperature + "℃\r\n");
		tvGXJ.append("出水温度：" + GlobalInfo.gxjStatus.outTemperature + "℃\r\n");
		tvGXJ.append("已出水量：" + GlobalInfo.gxjStatus.waterCount * 10 + "mL\r\n");
		tvGXJ.append("进水TDS：" + GlobalInfo.gxjStatus.inTDS + "ppm\r\n");
		tvGXJ.append("制冷状态：" + GlobalInfo.gxjStatus.getCoolStatus() + "\r\n");
		tvGXJ.append("\r\n");
	}

	@SuppressLint("DefaultLocale")
	void refreshTvJSQ() {
		if (GlobalInfo.boundJSQ.used == false) {
			tvJSQ.setText("未绑定净水器！");
			return;
		}
		tvJSQ.setText("");
		tvJSQ.append("设备类型：" + GlobalInfo.boundJSQ.getDeviceType() + "\r\n");
		tvJSQ.append("设备状态：" + GlobalInfo.jsqStatus.getStatus() + "\r\n");
		tvJSQ.append("冲洗时间：" + GlobalInfo.jsqStatus.rinsingTimeLeft + "秒\r\n");
		tvJSQ.append("进水流速：" + GlobalInfo.jsqStatus.inFlow + "mL/min\r\n");
		tvJSQ.append("出水流速：" + GlobalInfo.jsqStatus.outFlow + "mL/min\r\n");
		tvJSQ.append("进水TDS：" + GlobalInfo.jsqStatus.inTDS + "ppm\r\n");
		tvJSQ.append("出水TDS：" + GlobalInfo.jsqStatus.outTDS + "ppm\r\n");
		tvJSQ.append("进水温度：" + GlobalInfo.jsqStatus.inTemperature + "℃\r\n");
		tvJSQ.append("设备地址：" + Integer.toHexString(GlobalInfo.boundJSQ.add).toUpperCase() + "\r\n");
		tvJSQ.append("\r\n");
	}

	void refreshOutWaterDisplay() {
		if (GlobalInfo.gxjStatus.commandOrStatus == 1) {
			if (outWaterWaterAmount > 0) {
				outWaterWaterAmount = GlobalInfo.setWaterAmount;
				skBarOutWaterAmount.setProgress(outWaterWaterAmount);
				tvOutWaterAmount.setText(outWaterWaterAmount + "mL");
				tvOutTemperature.setText(tvTemperature.getText());
				btnStop.setText("【出水完成】");
			}
		} else {
			btnStop.setText("【停止出水】");
			if (GlobalInfo.selectCoolWater) {
				tvOutWaterAmount.setText(GlobalInfo.setMode);
				tvOutTemperature.setText(GlobalInfo.setMode);
				skBarOutWaterAmount.setProgress(GlobalInfo.setWaterAmount);
			} else {
				outWaterWaterAmount = GlobalInfo.gxjStatus.waterCount * 10;
				skBarOutWaterAmount.setProgress(outWaterWaterAmount);
				tvOutWaterAmount.setText(outWaterWaterAmount + "mL");
				tvOutTemperature.setText(GlobalInfo.gxjStatus.outTemperature + "℃");
			}
		}
	}

	@Override
	protected void onDestroy() {
		MainActivity.pauseProtocolDataProcess = false;
		handler.removeCallbacks(refreshRunnable);
		super.onDestroy();
	}
}
