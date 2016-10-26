package com.ywangwang.gxj.waterinfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.GlobalInfo.GxjOutWaterDetails;
import com.ywangwang.gxj.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class GxjInfoFragment extends Fragment {
	private static final int READ_DB_FINISH = 1;
	private static final int SELECT_NEW_MONTH = READ_DB_FINISH + 1;

	private ProgressDialog proDialog;

	private float waterAmountRange = 100f;
	private int numColumns = 12;
	private int numData = 20;
	private int numMonth = 12;
	private int monthIndex;
	private int dayIndex;

	private float maxPointValue = 0f;
	private float maxColumnValue = 0f;

	private LineChartView chartTop;
	private ColumnChartView chartBottom;

	private LineChartData lineData;
	private ColumnChartData columnData;

	UsedDate usedDate;
	List<GxjWaterDetailsOfMonth> gxjWaterDetails = new ArrayList<GxjWaterDetailsOfMonth>();
	List<RadioButton> rdoBtnMonths = new ArrayList<RadioButton>();

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == READ_DB_FINISH) {
				proDialog.dismiss();
				numColumns = usedDate.getDaysNumInMonth(monthIndex);
				dayIndex = 0;
				numData = gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().size();
				generateColumnData();
				generateLineData();
				chartBottom.setOnValueTouchListener(new ValueTouchListener());

				for (int i = 0; i < rdoBtnMonths.size(); i++) {
					rdoBtnMonths.get(i).setOnClickListener(ButtonListener);
					rdoBtnMonths.get(i).setText(usedDate.monthList[i]);
				}
				rdoBtnMonths.get(rdoBtnMonths.size() - 1).setChecked(true);
				Log.d("gxj", "jsqWaterDetails.size()=" + gxjWaterDetails.size());
				for (int i = 0; i < gxjWaterDetails.size(); i++) {
					Log.d("gxj", "getGxjWaterDetailsOfDay()>" + i + "=" + gxjWaterDetails.get(i).getGxjWaterDetailsOfDay().size());
					for (int j = 0; j < gxjWaterDetails.get(i).getGxjWaterDetailsOfDay().size(); j++) {
						Log.d("gxj", "getDetails()>" + i + "=" + gxjWaterDetails.get(i).getGxjWaterDetailsOfDay().get(j).getDetails().size());
					}
				}
			} else if (msg.what == SELECT_NEW_MONTH) {
				numColumns = usedDate.getDaysNumInMonth(monthIndex);
				dayIndex = 0;
				numData = gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().size();
				generateColumnData();
				generateLineData();
			}
			super.handleMessage(msg);
		}
	};
	OnClickListener ButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Message msg = new Message();
			msg.what = SELECT_NEW_MONTH;
			for (int i = 0; i < rdoBtnMonths.size(); i++) {
				if (v.getId() == rdoBtnMonths.get(i).getId()) {
					monthIndex = i;
				}
			}
			handler.sendMessage(msg);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gxj_info, container, false);
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth0));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth1));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth2));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth3));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth4));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth5));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth6));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth7));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth8));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth9));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth10));
		rdoBtnMonths.add((RadioButton) rootView.findViewById(R.id.rdoBtnMonth11));

		numMonth = rdoBtnMonths.size();
		usedDate = new UsedDate();
		monthIndex = numMonth - 1;
		dayIndex = 0;
		numColumns = 16;

		chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);
		generateLine();

		chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);
		generateColumn();

		proDialog = new ProgressDialog(getActivity());
		proDialog.show();
		proDialog.setMessage("统计中... ");

		readDB();
		return rootView;
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	private void generateColumn() {

		columnData = new ColumnChartData(initColumns());
		columnData.setAxisXBottom(initColumnAxisXBottom());
		columnData.setAxisYLeft(initColumnAxisYLeft());
		columnData.setStacked(true);

		chartBottom.setColumnChartData(columnData);

		// Set value touch listener that will trigger changes for chartTop.
		// chartBottom.setOnValueTouchListener(new ValueTouchListener());

		// Set selection mode to keep selected month column highlighted.
		chartBottom.setValueSelectionEnabled(true);

		chartBottom.setViewportCalculationEnabled(false);// 手动调整Viewport，必须将此项设为false

		chartBottom.setZoomEnabled(false);
		// chartBottom.setZoomType(ZoomType.VERTICAL);
		// Viewport v = new Viewport(chartBottom.getMaximumViewport());
		// Viewport v1 = new Viewport(v.left, 100, v.right, v.bottom);
		// chartBottom.setMaximumViewport(v1);
		// // Viewport v1 = new Viewport(v.right / 2, v.top, v.right, v.bottom);
		// chartBottom.setCurrentViewport(v1);
	}

	/**
	 * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user will select value on column chart.
	 */
	private void generateLine() {

		lineData = new LineChartData(initLines());
		lineData.setAxisXBottom(initLineAxisXBottom());
		lineData.setAxisYLeft(intiLineAxisYLeft());
		lineData.setAxisYRight(intiLineAxisYRight());

		chartTop.setLineChartData(lineData);
		chartTop.setValueSelectionEnabled(true);

		// For build-up animation you have to disable viewport recalculation.
		chartTop.setViewportCalculationEnabled(false);// 手动调整Viewport，必须将此项设为false

		chartTop.setZoomType(ZoomType.HORIZONTAL);
	}

	private class ValueTouchListener implements ColumnChartOnValueSelectListener {

		@Override
		public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
			Log.d("gxj", "columnIndex=" + columnIndex + ",subcolumnIndex=" + subcolumnIndex + ",value=" + value);
			dayIndex = columnIndex;

			chartBottom.getSelectedValue().setSecondIndex(0);

			numData = gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().size();
			Log.d("gxj", "numData=" + numData);
			generateLineData();
		}

		@Override
		public void onValueDeselected() {
		}
	}

	/**
	 * Recalculated height values to display on axis.
	 */
	private static class HeightValueFormatter extends SimpleAxisValueFormatter {

		private float scale;
		private float sub;
		private int decimalDigits;

		public HeightValueFormatter(float scale, float sub, int decimalDigits) {
			this.scale = scale;
			this.sub = sub;
			this.decimalDigits = decimalDigits;
		}

		@Override
		public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value, int autoDecimalDigits) {
			float scaledValue = (value + sub) / scale;
			return super.formatValueForAutoGeneratedAxis(formattedValue, scaledValue, this.decimalDigits);
		}
	}

	private Axis initColumnAxisXBottom() {
		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		for (int i = 0; i < numColumns; i++) {
			axisValues.add(new AxisValue(i).setLabel(""));
		}
		Axis axis = new Axis();
		axis.setHasLines(true);
		axis.setName("月份");
		axis.setTextSize(16);
		axis.setTextColor(Color.BLACK);
		axis.setMaxLabelChars(1);
		axis.setValues(axisValues);
		return axis;
	}

	private Axis initColumnAxisYLeft() {

		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		axisValues.add(new AxisValue(0).setLabel(""));

		Axis axis = new Axis();
		axis.setHasLines(true);
		axis.setName("用水次数");
		axis.setTextSize(16);
		axis.setTextColor(Color.BLACK);
		axis.setMaxLabelChars(3);
		axis.setValues(axisValues);

		return axis;
	}

	private List<Column> initColumns() {

		List<Column> columns = new ArrayList<Column>();
		List<SubcolumnValue> values;
		for (int i = 0; i < numColumns; ++i) {
			values = new ArrayList<SubcolumnValue>();
			values.add(new SubcolumnValue(waterAmountRange, ChartUtils.pickColor()));
			values.add(new SubcolumnValue(waterAmountRange, Color.parseColor("#00000000")));
			columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
		}
		return columns;
	}

	private void setColumnAxisXBottom() {

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(usedDate.clearedTime);
		mCalendar.add(Calendar.MONTH, 1 - numColumns); // 月减11

		Axis axis = columnData.getAxisXBottom();

		axis.setName(usedDate.monthList[monthIndex]);

		List<AxisValue> axisValues = axis.getValues();
		axisValues.clear();
		String[] lable = usedDate.getDayList(usedDate.monthInMillis[monthIndex]);
		for (int i = 0; i < numColumns; i++) {
			axisValues.add(new AxisValue(i).setLabel(lable[i]));
			mCalendar.add(Calendar.MONTH, 1);
		}
	}

	private void setColumnAxisYLeft() {
		columnData.getAxisYLeft().setAutoGenerated(true);
	}

	private void setColumns() {

		List<Column> columns = columnData.getColumns();
		List<SubcolumnValue> values;
		while (columns.size() > numColumns) {
			columns.remove(numColumns);
		}
		if (columns.size() < numColumns) {
			for (int i = columns.size(); i < numColumns; i++) {
				values = new ArrayList<SubcolumnValue>();
				values.add(new SubcolumnValue(waterAmountRange, ChartUtils.pickColor()));
				values.add(new SubcolumnValue(waterAmountRange, Color.parseColor("#00000000")));
				columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
			}
		}
		int i = 0;
		maxColumnValue = 0;
		for (Column column : columnData.getColumns()) {
			// for (SubcolumnValue value : column.getValues()) {}
			maxColumnValue = maxColumnValue > (float) gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(i).getOutWaterTimes() ? maxColumnValue : (float) gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(i).getOutWaterTimes();
			column.getValues().get(0).setTarget((float) gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(i++).getOutWaterTimes());
		}
		i = 0;
		for (Column column : columnData.getColumns()) {
			column.getValues().get(1).setTarget(maxColumnValue - (float) gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(i++).getOutWaterTimes());
		}
	}

	private void generateColumnData() {
		chartBottom.cancelDataAnimation();
		setColumnAxisXBottom();
		setColumnAxisYLeft();
		setColumns();
		chartBottom.getSelectedValue().setFirstIndex(dayIndex);
		chartBottom.getSelectedValue().setSecondIndex(0);

		Viewport v1 = new Viewport(-0.5f, maxColumnValue, numColumns - 0.5f, 0);
		chartBottom.setMaximumViewport(v1);
		v1.right = 15.5f;
		chartBottom.setCurrentViewport(v1);

		chartBottom.startDataAnimation(500);
	}

	private Axis initLineAxisXBottom() {
		Axis axis = new Axis();

		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		for (int i = 0; i < numData; ++i) {
			axisValues.add(new AxisValue(i).setLabel(""));
		}
		axis.setValues(axisValues);
		axis.setHasLines(true);
		axis.setName("用水明细");
		axis.setTextSize(16);
		axis.setMaxLabelChars(1);
		axis.setTextColor(Color.BLACK);

		return axis;
	}

	private Axis intiLineAxisYLeft() {
		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		axisValues.add(new AxisValue(0).setLabel(""));
		Axis axis = new Axis();
		axis.setHasLines(true);
		axis.setMaxLabelChars(3);
		axis.setName("TDS(ppm)");
		axis.setTextSize(16);
		axis.setTextColor(Color.BLACK);
		axis.setValues(axisValues);
		return axis;
	}

	private Axis intiLineAxisYRight() {
		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		axisValues.add(new AxisValue(0).setLabel(""));
		Axis axis = new Axis();
		axis.setHasLines(true);
		axis.setMaxLabelChars(3);
		axis.setName("设定水量(mL)");
		axis.setTextSize(16);
		axis.setTextColor(Color.BLACK);
		axis.setFormatter(new HeightValueFormatter((float) ((float) 1 / (float) 2), 0, 0));
		axis.setValues(axisValues);
		return axis;
	}

	private List<Line> initLines() {
		List<PointValue> pointValues0 = new ArrayList<PointValue>();
		List<PointValue> pointValues1 = new ArrayList<PointValue>();
		List<PointValue> pointValues2 = new ArrayList<PointValue>();
		List<Line> lines = new ArrayList<Line>();

		for (int i = 0; i < numData; ++i) {
			pointValues0.add(new PointValue(i, 0));
			pointValues1.add(new PointValue(i, 0));
			pointValues2.add(new PointValue(i, 0));
		}

		Line line = new Line(pointValues0);
		line.setColor(ChartUtils.COLOR_RED).setCubic(true);
		line.setHasLabelsOnlyForSelected(true);
		lines.add(line);

		line = new Line(pointValues1);
		line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);
		line.setHasLabels(true);
		lines.add(line);

		line = new Line(pointValues2);
		line.setColor(Color.GRAY);
		line.setHasPoints(false);
		line.setFilled(true);
		line.setStrokeWidth(1);
		lines.add(line);

		return lines;
	}

	private void setLineAxisXBottom() {
		Axis axis = lineData.getAxisXBottom();

		List<AxisValue> axisValues = axis.getValues();

		axis.setName(usedDate.monthList[monthIndex] + (dayIndex + 1) + "日 共用水 " + numData + " 次");

		axisValues.clear();
		for (int i = 0; i < numData; i++) {
			axisValues.add(i, new AxisValue(i).setLabel((i + 1) + ""));
		}
	}

	private void setLineAxisY() {
		Axis axis = lineData.getAxisYLeft();
		axis.setAutoGenerated(true);
		axis = lineData.getAxisYRight();
		axis.setAutoGenerated(true);
	}

	private void setLines() {

		List<Line> lines = lineData.getLines();
		List<PointValue> pointValues0 = lines.get(0).getValues();
		List<PointValue> pointValues1 = lines.get(1).getValues();
		List<PointValue> pointValues2 = lines.get(2).getValues();
		boolean hasOneValue = false;
		if (numData == 1) {
			hasOneValue = true;
			numData = 2;
		}
		while (pointValues0.size() > numData) {
			pointValues0.remove(numData);
			pointValues1.remove(numData);
			pointValues2.remove(numData);
		}
		if (pointValues0.size() < numData) {
			for (int i = pointValues0.size(); i < numData; i++) {
				pointValues0.add(new PointValue(i, 0));
				pointValues1.add(new PointValue(i, 0));
				pointValues2.add(new PointValue(i, 0));
			}
		}
		// for (Line line : lineData.getLines()) {
		// for (PointValue pointValues : line.getValues()) {
		// pointValues.setTarget(pointValues.getX(), (float) Math.random() * 200);
		// }
		// }
		PointValue pointValue;
		maxPointValue = 0f;
		for (int i = 0; i < lineData.getLines().get(0).getValues().size(); i++) {
			pointValue = lineData.getLines().get(0).getValues().get(i);
			if (hasOneValue) {
				pointValue.setTarget(pointValue.getX(), gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).averageTDS);
				maxPointValue = maxPointValue > gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).averageTDS ? maxPointValue : gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).averageTDS;
			} else {
				pointValue.setTarget(pointValue.getX(), gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).averageTDS);
				maxPointValue = maxPointValue > gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).averageTDS ? maxPointValue : gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).averageTDS;
			}
		}
		for (int i = 0; i < lineData.getLines().get(1).getValues().size(); i++) {
			pointValue = lineData.getLines().get(1).getValues().get(i);
			if (hasOneValue) {
				pointValue.setTarget(pointValue.getX(), gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).temperature);
				maxPointValue = maxPointValue > gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).temperature ? maxPointValue : gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).temperature;
			} else {
				pointValue.setTarget(pointValue.getX(), gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).temperature);
				maxPointValue = maxPointValue > gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).temperature ? maxPointValue : gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).temperature;
			}
		}
		for (int i = 0; i < lineData.getLines().get(2).getValues().size(); i++) {
			pointValue = lineData.getLines().get(2).getValues().get(i);
			if (hasOneValue) {
				pointValue.setTarget(pointValue.getX(), gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).waterAmount / 2);
				maxPointValue = maxPointValue > gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).waterAmount / 2 ? maxPointValue : gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(0).waterAmount / 2;
			} else {
				pointValue.setTarget(pointValue.getX(), gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).waterAmount / 2);
				maxPointValue = maxPointValue > gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).waterAmount / 2 ? maxPointValue : gxjWaterDetails.get(monthIndex).getGxjWaterDetailsOfDay().get(dayIndex).getDetails().get(i).waterAmount / 2;
			}
		}
	}

	private void generateLineData() {
		chartTop.cancelDataAnimation();
		setLineAxisXBottom();
		setLineAxisY();
		setLines();
		Log.d("gxj", "maxPointValue=" + maxPointValue);
		float left = 0, top = maxPointValue, right = numData - 1, bottom = 0;
		Viewport v = new Viewport(left, top, right, bottom);
		chartTop.setMaximumViewport(v);
		v.right = v.right > 30 ? 30 : v.right;
		chartTop.setCurrentViewport(v);

		// chartTop.resetViewports();
		chartTop.startDataAnimation(300);
	}

	class UsedDate {
		Calendar clearedCalendar;
		long clearedTime;
		String monthList[] = new String[numColumns];
		long monthInMillis[] = new long[numColumns];

		UsedDate() {
			clearedCalendar = Calendar.getInstance();
			clearedCalendar.set(Calendar.HOUR_OF_DAY, 0); // 24小时制，时清零
			clearedCalendar.set(Calendar.MINUTE, 0); // 分清零
			clearedCalendar.set(Calendar.SECOND, 0); // 秒清零
			clearedCalendar.set(Calendar.MILLISECOND, 0); // 毫秒清零
			clearedTime = clearedCalendar.getTimeInMillis();// 获取清零后的时间

			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(clearedTime);
			mCalendar.add(Calendar.MONTH, 1 - numColumns); // 月减11

			for (int i = 0; i < numColumns; i++) {
				monthList[i] = (String) DateFormat.format("yyyy年MM月", mCalendar.getTimeInMillis());
				monthInMillis[i] = mCalendar.getTimeInMillis();
				mCalendar.add(Calendar.MONTH, 1);
			}
		}

		int getDaysNumInMonth(int index) {
			return getDaysNumInMonth(monthInMillis[index]);
		}

		int getDaysNumInMonth(long time) {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(time);
			return mCalendar.getActualMaximum(Calendar.DATE);
		}

		String[] getDayList(int index) {
			return getDayList(monthInMillis[index]);
		}

		String[] getDayList(long time) {
			String[] dayList = new String[getDaysNumInMonth(time)];
			for (int i = 0; i < dayList.length; i++) {
				dayList[i] = i + 1 + "";
			}
			return dayList;
		}
	}

	class GxjWaterDetailsOfDay {
		List<GxjOutWaterDetails> details = new ArrayList<GxjOutWaterDetails>();

		int getOutWaterTimes() {
			return details.size();
		}

		List<GxjOutWaterDetails> getDetails() {
			return details;
		}

		GxjWaterDetailsOfDay addDetails(GxjOutWaterDetails gxjData) {
			details.add(gxjData);
			return this;
		}

		void clearDetails() {
			details.clear();
		}
	}

	class GxjWaterDetailsOfMonth {
		List<GxjWaterDetailsOfDay> details = new ArrayList<GxjWaterDetailsOfDay>();

		List<GxjWaterDetailsOfDay> getGxjWaterDetailsOfDay() {
			return details;
		}

		GxjWaterDetailsOfMonth addDetails(GxjWaterDetailsOfDay gxjDataOfDay) {
			details.add(gxjDataOfDay);
			return this;
		}

		void clearDetails() {
			details.clear();
		}
	}

	void readDB() {
		new Thread() {
			@Override
			public void run() {
				GxjOutWaterDetails gxjData;
				GxjWaterDetailsOfMonth gxjMonthData;
				GxjWaterDetailsOfDay gxjDayData;

				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(usedDate.clearedTime);
				mCalendar.add(Calendar.MONTH, 1 - numMonth); // 月减11

				SQLiteDatabase db = GlobalInfo.databaseHelperGXJ.getWritableDatabase();

				for (int i = 0; i < numMonth; i++) {
					int daysNum = mCalendar.getActualMaximum(Calendar.DATE);
					gxjMonthData = new GxjWaterDetailsOfMonth();
					for (int j = 1; j <= daysNum; j++) {
						mCalendar.set(Calendar.DAY_OF_MONTH, j);
						gxjDayData = new GxjWaterDetailsOfDay();
						Cursor c = db.rawQuery("SELECT * FROM " + GlobalInfo.DB_TABLE_NAME_GXJ + " WHERE " + GlobalInfo.DB_TIME + ">=" + mCalendar.getTimeInMillis() + " and " + GlobalInfo.DB_TIME + "<" + (mCalendar.getTimeInMillis() + (long) (24 * 60 * 60 * 1000)), null);
						if (c != null) {
							Log.d("读取到条数：", c.getCount() + "");
							if (c.getCount() > 0) {
								// for (int x = 0; x < c.getCount(); x++) {
								while (c.moveToNext()) {
									gxjData = new GxjOutWaterDetails();
									gxjData.averageTDS = c.getInt(c.getColumnIndex(GlobalInfo.DB_AVERAGE_TDS));
									gxjData.temperature = c.getInt(c.getColumnIndex(GlobalInfo.DB_TEMPERATURE));
									gxjData.waterAmount = c.getInt(c.getColumnIndex(GlobalInfo.DB_WATER_AMOUNT));
									gxjData.time = c.getLong(c.getColumnIndex(GlobalInfo.DB_TIME));
									gxjDayData.addDetails(gxjData);
								}
							} else {// 生成测试数据///////////////////////////////////////////////////////////////////////////////////////////////////////
								int w = new Random().nextInt(150);
								for (int z = 0; z < w; z++) {
									gxjData = new GxjOutWaterDetails();
									gxjData.averageTDS = (int) (Math.random() * 90 + 10);
									gxjData.temperature = (int) (Math.random() * 100);
									gxjData.waterAmount = (int) (Math.random() * 450 + 50);
									// gxjData.time = (long) (Math.random() * 200 + 100);
									gxjDayData.addDetails(gxjData);
								}
							}
							c.close();
						}
						gxjMonthData.addDetails(gxjDayData);
					}
					gxjWaterDetails.add(gxjMonthData);
					mCalendar.add(Calendar.MONTH, 1);
				}

				GlobalInfo.databaseHelperGXJ.close();
				Message msg = new Message();
				msg.what = READ_DB_FINISH;
				handler.sendMessage(msg);
			}
		}.start();
	}
}