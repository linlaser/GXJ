package com.ywangwang.gxj;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CustomDialog {
	static int temperature_temp = 0;
	static int waterAmount_temp = 0;
	static int rdoBtnCustom_id = 0;

	@SuppressLint("InflateParams")
	public static void ShowCustomOutWaterDialog(final Context context, View v) {

		final View newView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_out_water_parameter, null);
		final SeekBar skBarSetTemperature = (SeekBar) newView.findViewById(R.id.skBarSetTemperature);
		final SeekBar skBarSetWaterAmount = (SeekBar) newView.findViewById(R.id.skBarSetWaterAmount);
		final Button btnTemperatureSubtraction = (Button) newView.findViewById(R.id.btnTemperatureSubtraction);
		final Button btnTemperatureAddition = (Button) newView.findViewById(R.id.btnTemperatureAddition);
		final Button btnWaterAmountSubtraction = (Button) newView.findViewById(R.id.btnWaterAmountSubtraction);
		final Button btnWaterAmountAddition = (Button) newView.findViewById(R.id.btnWaterAmountAddition);
		final TextView tvSetTemperature = (TextView) newView.findViewById(R.id.tvSetTemperature);
		final TextView tvSetWaterAmount = (TextView) newView.findViewById(R.id.tvSetWaterAmount);
		final Builder builder = new AlertDialog.Builder(context);
		final EditText edtTxtSetName = (EditText) newView.findViewById(R.id.edtTxtSetName);
		skBarSetTemperature.setMax(GlobalInfo.MAX_WATER_TEMPRATURE - GlobalInfo.MIN_WATER_TEMPRATURE);
		skBarSetWaterAmount.setMax((500 - GlobalInfo.MIN_WATER_AMOUNT) / 10);
		rdoBtnCustom_id = v.getId();
		if (rdoBtnCustom_id == R.id.rdoBtnCustom1) {
			edtTxtSetName.setText(GlobalInfo.Custom1Name);
			temperature_temp = GlobalInfo.Custom1TemperatureValue;
			waterAmount_temp = GlobalInfo.Custom1WaterAmountValue;
			builder.setTitle(GlobalInfo.Custom1Name + "   温度/水量设定");
			tvSetTemperature.setText("温度：" + GlobalInfo.Custom1TemperatureValue + "℃");
			tvSetWaterAmount.setText("水量：" + GlobalInfo.Custom1WaterAmountValue + "mL");
			skBarSetTemperature.setProgress(GlobalInfo.Custom1TemperatureValue - GlobalInfo.MIN_WATER_TEMPRATURE);
			skBarSetWaterAmount.setProgress((GlobalInfo.Custom1WaterAmountValue - GlobalInfo.MIN_WATER_AMOUNT) / 10);
		} else {
			edtTxtSetName.setText(GlobalInfo.Custom2Name);
			temperature_temp = GlobalInfo.Custom2TemperatureValue;
			waterAmount_temp = GlobalInfo.Custom2WaterAmountValue;
			builder.setTitle(GlobalInfo.Custom2Name + "   温度/水量设定");
			tvSetTemperature.setText("温度：" + GlobalInfo.Custom2TemperatureValue + "℃");
			tvSetWaterAmount.setText("水量：" + GlobalInfo.Custom2WaterAmountValue + "mL");
			skBarSetTemperature.setProgress(GlobalInfo.Custom2TemperatureValue - GlobalInfo.MIN_WATER_TEMPRATURE);
			skBarSetWaterAmount.setProgress((GlobalInfo.Custom2WaterAmountValue - GlobalInfo.MIN_WATER_AMOUNT) / 10);
		}
		skBarSetTemperature.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				temperature_temp = progress + GlobalInfo.MIN_WATER_TEMPRATURE;
				tvSetTemperature.setText("温度：" + temperature_temp + "℃");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		skBarSetWaterAmount.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				waterAmount_temp = progress * 10 + GlobalInfo.MIN_WATER_AMOUNT;
				tvSetWaterAmount.setText("水量：" + waterAmount_temp + "mL");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		// builder.setCancelable(false);
		builder.setView(newView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				if (rdoBtnCustom_id == R.id.rdoBtnCustom1) {
					GlobalInfo.Custom1Name = edtTxtSetName.getText().toString().trim();
					GlobalInfo.Custom1TemperatureValue = temperature_temp;
					GlobalInfo.Custom1WaterAmountValue = waterAmount_temp;
					editor.putInt("Custom1TemperatureValue", temperature_temp);
					editor.putInt("Custom1WaterAmountValue", waterAmount_temp);
					editor.putString("Custom1Name", GlobalInfo.Custom1Name);
				} else {
					GlobalInfo.Custom2Name = edtTxtSetName.getText().toString().trim();
					GlobalInfo.Custom2TemperatureValue = temperature_temp;
					GlobalInfo.Custom2WaterAmountValue = waterAmount_temp;
					editor.putInt("Custom2TemperatureValue", temperature_temp);
					editor.putInt("Custom2WaterAmountValue", waterAmount_temp);
					editor.putString("Custom2Name", GlobalInfo.Custom2Name);
				}
				editor.commit();
				context.sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_RDOBTN_CUSTOM, rdoBtnCustom_id));
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_RDOBTN_CUSTOM, rdoBtnCustom_id));
			}
		});
		builder.show();
		btnTemperatureSubtraction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (skBarSetTemperature.getProgress() != 0) {
					skBarSetTemperature.setProgress(skBarSetTemperature.getProgress() - 1);
				}
			}
		});
		btnTemperatureAddition.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (skBarSetTemperature.getProgress() != skBarSetTemperature.getMax()) {
					skBarSetTemperature.setProgress(skBarSetTemperature.getProgress() + 1);
				}
			}
		});
		btnWaterAmountSubtraction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (skBarSetWaterAmount.getProgress() != 0) {
					skBarSetWaterAmount.setProgress(skBarSetWaterAmount.getProgress() - 1);
				}
			}
		});
		btnWaterAmountAddition.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (skBarSetWaterAmount.getProgress() != skBarSetWaterAmount.getMax()) {
					skBarSetWaterAmount.setProgress(skBarSetWaterAmount.getProgress() + 1);
				}
			}
		});
	}

	@SuppressLint("InflateParams")
	public static void ShowCustomWaterAmountDialog(final Context context, View v) {

		final View newView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_water_amount, null);
		final SeekBar skBarSetWaterAmount = (SeekBar) newView.findViewById(R.id.skBarSetWaterAmount);
		final Button btnWaterAmountSubtraction = (Button) newView.findViewById(R.id.btnWaterAmountSubtraction);
		final Button btnWaterAmountAddition = (Button) newView.findViewById(R.id.btnWaterAmountAddition);
		final TextView tvSetWaterAmount = (TextView) newView.findViewById(R.id.tvSetWaterAmount);
		final Builder builder = new AlertDialog.Builder(context);

		skBarSetWaterAmount.setMax((500 - GlobalInfo.MIN_WATER_AMOUNT) / 10);
		waterAmount_temp = GlobalInfo.CustomWaterAmountValue;
		builder.setTitle("手动选择水量：");
		tvSetWaterAmount.setText("水量：" + GlobalInfo.CustomWaterAmountValue + "mL");
		skBarSetWaterAmount.setProgress((GlobalInfo.CustomWaterAmountValue - GlobalInfo.MIN_WATER_AMOUNT) / 10);
		skBarSetWaterAmount.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				waterAmount_temp = progress * 10 + GlobalInfo.MIN_WATER_AMOUNT;
				tvSetWaterAmount.setText("水量：" + waterAmount_temp + "mL");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		builder.setView(newView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				GlobalInfo.CustomWaterAmountValue = waterAmount_temp;
				editor.putInt("CustomWaterAmountValue", waterAmount_temp);
				editor.commit();
				context.sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_CUSTOM_WATER_AMOUNT, true));
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_CUSTOM_WATER_AMOUNT, true));
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				GlobalInfo.CustomWaterAmountValue = waterAmount_temp;
				editor.putInt("CustomWaterAmountValue", waterAmount_temp);
				editor.commit();
				context.sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_UPDATE_CUSTOM_WATER_AMOUNT, true));
			}
		});
		builder.show();
		btnWaterAmountSubtraction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (skBarSetWaterAmount.getProgress() != 0) {
					skBarSetWaterAmount.setProgress(skBarSetWaterAmount.getProgress() - 1);
				}
			}
		});
		btnWaterAmountAddition.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (skBarSetWaterAmount.getProgress() != skBarSetWaterAmount.getMax()) {
					skBarSetWaterAmount.setProgress(skBarSetWaterAmount.getProgress() + 1);
				}
			}
		});
	}

	/******************** 自定义简单对话框,可带返回功能 ****************************************/
	private Runnable runnablePositive = null;
	private Runnable runnableNegative = null;
	private boolean cancelable = true;
	private int iconId = 0;

	public CustomDialog show(Context context, String title, String message) {
		return this.show(context, title, message, "", "确定", null, null);
	}

	public void setCancelable(boolean flag) {
		this.cancelable = flag;
	}

	public void setIcon(int resId) {
		this.iconId = resId;
	}

	public CustomDialog show(Context context, String title, String message, String negativeBtnText, String positiveBtnText, Runnable runnableN, Runnable runnableP) {
		if (runnableN != null)
			runnableNegative = runnableN;
		if (runnableP != null)
			runnablePositive = runnableP;
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setCancelable(cancelable);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, negativeBtnText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int buttonId) {
				if (runnableNegative != null)
					runnableNegative.run();
			}
		});
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveBtnText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int buttonId) {
				if (runnablePositive != null)
					runnablePositive.run();
			}
		});
		if (iconId != 0)
			dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.show();
		return this;
	}

	/**** runnableN，runnableP写法 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ***/
	// private Runnable runnableN() {
	// return new Runnable() {
	// public void run() {
	// 在此添加代码
	// }
	// };
	// }
	/** <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< **/
}
