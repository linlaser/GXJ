package com.ywangwang.gxj.waterinfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.R;
import com.ywangwang.gxj.WebViewMain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class FilterInfoFragment extends Fragment {
	private static final int READ_CONFIG_FINISH = 1;
	// private static final int SET_FILTER_FINISH = READ_CONFIG_FINISH + 1;
	private static final int FILTER_NO_INIT = -1;
	private static final int FILTER_ERR = -2;
	private PieChartView[] pieChart = new PieChartView[4];
	private TextView[] tvFilterUseInfo = new TextView[4];

	private int filterUsedDays[] = { FILTER_NO_INIT, FILTER_NO_INIT, FILTER_NO_INIT, FILTER_NO_INIT }; // 滤芯使用天数
	private long filterInstallTime[] = { 0L, 0L, 0L, 0L }; // 滤芯安装时间

	private int[] includeId = { R.id.include0, R.id.include1, R.id.include2, R.id.include3 };
	// private Button[] btnResetFilter = new Button[4];
	// private Button[] btnBuyFilter = new Button[4];

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == READ_CONFIG_FINISH) {
				for (int i = 0; i < pieChart.length; i++) {
					generateDataAnimation(pieChart[i], filterUsedDays[i], GlobalInfo.FILTER_RECOMMEND_DAYS[i]);
					updateDescribe(tvFilterUseInfo[i], filterUsedDays[i], GlobalInfo.FILTER_RECOMMEND_DAYS[i]);
				}
			}
			super.handleMessage(msg);
		}
	};

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.filter_info, null);
		((ImageView) view.findViewById(R.id.include0).findViewById(R.id.ivFilterSequence)).setImageResource(R.drawable.number1);
		((ImageView) view.findViewById(R.id.include1).findViewById(R.id.ivFilterSequence)).setImageResource(R.drawable.number2);
		((ImageView) view.findViewById(R.id.include2).findViewById(R.id.ivFilterSequence)).setImageResource(R.drawable.number3);
		((ImageView) view.findViewById(R.id.include3).findViewById(R.id.ivFilterSequence)).setImageResource(R.drawable.number4);
		((TextView) view.findViewById(R.id.include0).findViewById(R.id.tvFilterName)).setText(R.string.pp_filter_name);
		((TextView) view.findViewById(R.id.include1).findViewById(R.id.tvFilterName)).setText(R.string.front_carbon_filter_name);
		((TextView) view.findViewById(R.id.include2).findViewById(R.id.tvFilterName)).setText(R.string.ro_filter_name);
		((TextView) view.findViewById(R.id.include3).findViewById(R.id.tvFilterName)).setText(R.string.behind_carbon_filter_name);
		((TextView) view.findViewById(R.id.include0).findViewById(R.id.tvFilterDepiction)).setText(R.string.pp_filter_depiction);
		((TextView) view.findViewById(R.id.include1).findViewById(R.id.tvFilterDepiction)).setText(R.string.front_carbon_filter_depiction);
		((TextView) view.findViewById(R.id.include2).findViewById(R.id.tvFilterDepiction)).setText(R.string.ro_filter_depiction);
		((TextView) view.findViewById(R.id.include3).findViewById(R.id.tvFilterDepiction)).setText(R.string.behind_carbon_filter_depiction);

		for (int i = 0; i < includeId.length; i++) {
			tvFilterUseInfo[i] = (TextView) view.findViewById(includeId[i]).findViewById(R.id.tvFilterUseInfo);
			pieChart[i] = ((PieChartView) (view.findViewById(includeId[i]).findViewById(R.id.chart)));
			((Button) (view.findViewById(includeId[i]).findViewById(R.id.btnResetFilter))).setOnClickListener(ButtonListener);
			((Button) (view.findViewById(includeId[i]).findViewById(R.id.btnBuyFilter))).setOnClickListener(ButtonListener);
		}
		return view;
	}

	@Override
	public void onResume() {
		getWaterInfo(getActivity());
		for (int i = 0; i < pieChart.length; i++) {
			generatePieChart(pieChart[i]);// .setCenterText1((i + 1) + "");
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	private PieChartData generatePieChart(PieChartView chart) {

		chart.setChartRotation(-90, true);

		List<SliceValue> values = new ArrayList<SliceValue>();
		values.add(new SliceValue(0f, ChartUtils.COLOR_RED));
		values.add(new SliceValue(100f, ChartUtils.COLOR_GREEN));
		PieChartData mPieChartData = new PieChartData(values);
		mPieChartData.setHasLabels(true);
		mPieChartData.setHasCenterCircle(true);
		// mPieChartData.setCenterText1FontSize(30);
		// mPieChartData.setCenterText1Color(Color.parseColor("#FFFFFF"));

		chart.setPieChartData(mPieChartData);
		chart.setChartRotationEnabled(false);
		chart.setChartRotation(-90, true);

		return mPieChartData;
	}

	private void generateDataAnimation(PieChartView chart, int usedDays, int recommendDays) {
		chart.cancelDataAnimation();
		if (usedDays >= 0 && usedDays < recommendDays) {
			chart.getPieChartData().getValues().get(0).setTarget((float) usedDays / (float) recommendDays * 100f);
			chart.getPieChartData().getValues().get(1).setTarget((float) (recommendDays - usedDays) / (float) recommendDays * 100f);
		} else {
			chart.getPieChartData().getValues().get(0).setTarget(100f);
			chart.getPieChartData().getValues().get(1).setTarget(0f);
		}
		chart.startDataAnimation(2000);
	}

	void updateDescribe(TextView tv, int usedDays, int recommendDays) {
		if (usedDays >= 0 && usedDays < recommendDays) {
			tv.setText("已使用 " + usedDays + " 天，推荐使用 " + recommendDays + " 天，剩余 " + (recommendDays - usedDays) + " 天。");
		} else if (usedDays >= recommendDays) {
			tv.setText("已使用 " + usedDays + " 天，推荐使用 " + recommendDays + " 天。\n请更换滤芯！");
		} else if (usedDays == FILTER_NO_INIT) {
			tv.setText("滤芯未配置！");
		} else {
			tv.setText("滤芯配置错误！");
		}
	}

	private void getWaterInfo(final Context context) {
		new Thread() {
			@Override
			public void run() {
				SharedPreferences sharedPreferences = context.getSharedPreferences("water_info", Context.MODE_PRIVATE);
				filterInstallTime[0] = sharedPreferences.getLong("filterInstallTime0", 0);
				filterInstallTime[1] = sharedPreferences.getLong("filterInstallTime1", 0);
				filterInstallTime[2] = sharedPreferences.getLong("filterInstallTime2", 0);
				filterInstallTime[3] = sharedPreferences.getLong("filterInstallTime3", 0);
				long nowTime = System.currentTimeMillis();
				for (int i = 0; i < filterInstallTime.length; i++) {
					if (filterInstallTime[i] > 0 && filterInstallTime[i] < nowTime) {
						filterUsedDays[i] = (int) ((nowTime - filterInstallTime[i]) / (long) (24 * 60 * 60 * 1000));
						if (filterUsedDays[i] == 0) {
							Calendar c = Calendar.getInstance();
							int mHour = c.get(Calendar.HOUR_OF_DAY);// 当前小时
							c.setTimeInMillis(filterInstallTime[i]);
							if (mHour != c.get(Calendar.HOUR_OF_DAY)) {
								filterUsedDays[i] = 1;// 不是同一天就标记为1
							}
						}
					} else if (filterInstallTime[i] == 0) {
						filterUsedDays[i] = FILTER_NO_INIT;
					} else {
						filterUsedDays[i] = FILTER_ERR;
					}
				}
				// filterUsedDays[0] = -1;
				// filterUsedDays[1] = -2;
				// filterUsedDays[2] = 100;
				// filterUsedDays[3] = 500;

				Message msg = new Message();
				msg.what = READ_CONFIG_FINISH;
				handler.sendMessage(msg);
			}
		}.start();
	}

	OnClickListener ButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnResetFilter) {
				for (int i = 0; i < includeId.length; i++) {
					if (((View) v.getParent()).getId() == includeId[i]) {
						showDialog(i);
						break;
					}
				}
			} else if (v.getId() == R.id.btnBuyFilter) {
				for (int i = 0; i < includeId.length; i++) {
					if (((View) v.getParent()).getId() == includeId[i]) {
						startActivity(new Intent(getActivity(), WebViewMain.class));
					}
				}
			}
		}
	};

	void showDialog(final int filterNum) {
		RelativeLayout layout = new RelativeLayout(getActivity());
		final EditText edtTxt = new EditText(getActivity());
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(15, 30, 15, 30);
		edtTxt.setLayoutParams(layoutParams);
		edtTxt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
		edtTxt.setFilters(new android.text.InputFilter[] { new android.text.InputFilter.LengthFilter(15) });
		edtTxt.setTextSize(30);
		layout.addView(edtTxt);
		final Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("请输入滤芯编码：");
		builder.setView(layout);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				checkCode(arg0, edtTxt, filterNum);
			}
		});
		builder.show();
	}

	private boolean checkCodeSuccess = false;

	void checkCode(final DialogInterface inputDialog, final EditText edtTxt, final int filterNum) {

		String title = "失败", msg = "滤芯编码无效！";
		checkCodeSuccess = false;
		if (edtTxt.getText().length() > 0) {
			// 如果输入号码等于7890，那就进入DEBUG模式
			if (edtTxt.getText().toString().indexOf("7890") != -1) {
				checkCodeSuccess = true;
				SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalInfo.S_P_NAME_CONFIG, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				if (edtTxt.getText().toString().equals("78900")) {
					title = "成功";
					msg = "成功退出DEBUG模式！";
					editor.remove(GlobalInfo.S_P_KEY_DEBUG);
					editor.remove(GlobalInfo.S_P_KEY_DEBUG_TIMES);
					GlobalInfo.debug = false;
					GlobalInfo.debugTimes = 0;
				} else {
					title = "成功";
					msg = "成功进入DEBUG模式！";
					GlobalInfo.debug = true;
					if (edtTxt.getText().toString().equals("78909")) {
						GlobalInfo.debugTimes = 100;
						editor.putBoolean(GlobalInfo.S_P_KEY_DEBUG, GlobalInfo.debug);
						editor.putInt(GlobalInfo.S_P_KEY_DEBUG_TIMES, GlobalInfo.debugTimes);
					} else if (edtTxt.getText().toString().equals("78901")) {
						GlobalInfo.debugTimes = 0;
						editor.putBoolean(GlobalInfo.S_P_KEY_DEBUG, GlobalInfo.debug);
						editor.putInt(GlobalInfo.S_P_KEY_DEBUG_TIMES, GlobalInfo.debugTimes);
					} else {
					}
				}
				editor.commit();
			}
			if (edtTxt.getText().toString().equals("321")) {
				title = "成功";
				msg = "成功进入检测模式模式！";
				checkCodeSuccess = true;
				GlobalInfo.testMode = true;
			}
			if (edtTxt.getText().toString().equals("123456")) {
				title = "成功";
				msg = "重置成功！";
				checkCodeSuccess = true;
				SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalInfo.S_P_NAME_WATER_INFO, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				long nowTime = System.currentTimeMillis();
				editor.putLong("filterInstallTime" + filterNum, nowTime);
				editor.commit();
			}
		}
		if (checkCodeSuccess == false) {
			try {
				java.lang.reflect.Field field = inputDialog.getClass().getSuperclass().getDeclaredField("mShowing");
				field.setAccessible(true);
				field.set(inputDialog, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Builder msgBuilder = new AlertDialog.Builder(getActivity());
		msgBuilder.setTitle(title);
		msgBuilder.setMessage(msg);
		msgBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (checkCodeSuccess) {
					getWaterInfo(getActivity());
				} else {
					try {
						java.lang.reflect.Field field = inputDialog.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(inputDialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		msgBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (!checkCodeSuccess) {
					try {
						java.lang.reflect.Field field = inputDialog.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(inputDialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		msgBuilder.show();
	}
}
