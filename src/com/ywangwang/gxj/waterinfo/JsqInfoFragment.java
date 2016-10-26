package com.ywangwang.gxj.waterinfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.GlobalInfo.JsqDataStatistics;
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
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class JsqInfoFragment extends Fragment {
	private static final int READ_DB_FINISH = 1;
	private float waterAmountRange = 100f;
	private int numColumns = 12;
	private int numData;
	private int numMonth = 12;
	private int monthIndex;

	// private float maxPointValue = 0f;
	private float maxColumnValue = 0f;

	private LineChartView chartTop;
	private ColumnChartView chartBottom;

	private LineChartData lineData;
	private ColumnChartData columnData;

	private ProgressDialog proDialog;

	UsedDate usedDate;
	List<JsqWaterDetailsOfMonth> jsqWaterDetails = new ArrayList<JsqWaterDetailsOfMonth>();

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == READ_DB_FINISH) {
				proDialog.dismiss();
				generateColumnData();
				generateLineData();
				chartBottom.setOnValueTouchListener(new ValueTouchListener());
				Log.d("gxj", "jsqWaterDetails.size()=" + jsqWaterDetails.size());
				for (int i = 0; i < jsqWaterDetails.size(); i++) {
					Log.d("gxj", "getDetails()>" + i + "=" + jsqWaterDetails.get(i).getDetails().size());
				}
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.jsq_info, container, false);
		usedDate = new UsedDate();
		numColumns = numMonth;
		monthIndex = numMonth - 1;
		numData = usedDate.getDaysNumInMonth(usedDate.monthInMillis[monthIndex]);

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

		// chartBottom.setViewportCalculationEnabled(false);//手动调整Viewport，必须将此项设为false

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
		// chartTop.setViewportCalculationEnabled(false);// 手动调整Viewport，必须将此项设为false

		chartTop.setZoomType(ZoomType.HORIZONTAL);
	}

	private class ValueTouchListener implements ColumnChartOnValueSelectListener {

		@Override
		public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
			Log.d("gxj", "columnIndex=" + columnIndex + ",subcolumnIndex=" + subcolumnIndex + ",value=" + value);
			monthIndex = columnIndex;
			chartBottom.getSelectedValue().setSecondIndex(0);
			numData = usedDate.getDaysNumInMonth(usedDate.monthInMillis[monthIndex]);
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
		axis.setTextSize(14);
		axis.setTextColor(Color.BLACK);
		axis.setMaxLabelChars(2);
		axis.setValues(axisValues);
		return axis;
	}

	private Axis initColumnAxisYLeft() {

		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		axisValues.add(new AxisValue(0).setLabel(""));

		Axis axis = new Axis();
		axis.setHasLines(true);
		axis.setName("制水次数");
		axis.setTextSize(16);
		axis.setTextColor(Color.BLACK);
		axis.setMaxLabelChars(5);
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

		Axis axis = columnData.getAxisXBottom();
		List<AxisValue> axisValues = axis.getValues();
		axisValues.clear();

		for (int i = 0; i < numColumns; i++) {
			axisValues.add(new AxisValue(i).setLabel(usedDate.monthList[i]));
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
			maxColumnValue = maxColumnValue > (float) jsqWaterDetails.get(i).getFilterWaterTimes() ? maxColumnValue : (float) jsqWaterDetails.get(i).getFilterWaterTimes();
			column.getValues().get(0).setTarget((float) jsqWaterDetails.get(i++).getFilterWaterTimes());
		}
		i = 0;
		for (Column column : columnData.getColumns()) {
			column.getValues().get(1).setTarget(maxColumnValue - (float) jsqWaterDetails.get(i++).getFilterWaterTimes());
		}
	}

	private void generateColumnData() {
		chartBottom.cancelDataAnimation();
		setColumnAxisXBottom();
		setColumnAxisYLeft();
		setColumns();
		chartBottom.getSelectedValue().setFirstIndex(monthIndex);
		chartBottom.getSelectedValue().setSecondIndex(0);
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
		axis.setName(usedDate.monthList[monthIndex].replace("-", "年") + "月");
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
		axis.setName("制水量(L)");
		axis.setTextSize(16);
		axis.setTextColor(Color.BLACK);
		axis.setFormatter(new HeightValueFormatter((float) ((float) 20 / (float) 1), 0, 0));
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
		line.setColor(ChartUtils.COLOR_BLUE).setCubic(true);
		line.setHasLabelsOnlyForSelected(true);
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

		axis.setName(usedDate.monthList[monthIndex].replace("-", "年") + "月");

		List<AxisValue> axisValues = axis.getValues();
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
		axis.setFormatter(new HeightValueFormatter((float) ((float) 20 / (float) 1), 0, 0));
	}

	private void setLines() {

		List<Line> lines = lineData.getLines();
		List<PointValue> pointValues0 = lines.get(0).getValues();
		List<PointValue> pointValues1 = lines.get(1).getValues();
		List<PointValue> pointValues2 = lines.get(2).getValues();

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
		PointValue pointValues;
		for (int i = 0; i < lineData.getLines().get(0).getValues().size(); i++) {
			pointValues = lineData.getLines().get(0).getValues().get(i);
			pointValues.setTarget(pointValues.getX(), jsqWaterDetails.get(monthIndex).getDetails().get(i).averageTDSIn);
		}
		for (int i = 0; i < lineData.getLines().get(1).getValues().size(); i++) {
			pointValues = lineData.getLines().get(1).getValues().get(i);
			pointValues.setTarget(pointValues.getX(), jsqWaterDetails.get(monthIndex).getDetails().get(i).averageTDSOut);
		}
		for (int i = 0; i < lineData.getLines().get(2).getValues().size(); i++) {
			pointValues = lineData.getLines().get(2).getValues().get(i);
			pointValues.setTarget(pointValues.getX(), jsqWaterDetails.get(monthIndex).getDetails().get(i).totalWaterOut * 20);
		}
	}

	private void generateLineData() {
		chartTop.cancelDataAnimation();
		setLineAxisXBottom();
		setLineAxisY();
		setLines();
		// float left = 0, top = 300, right = numData - 1, bottom = 0;
		// Viewport v = new Viewport(left, top, right, bottom);
		// chartTop.setMaximumViewport(v);
		// chartTop.setCurrentViewport(v);

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
				monthList[i] = (String) DateFormat.format("yyyy-MM", mCalendar.getTimeInMillis());
				monthInMillis[i] = mCalendar.getTimeInMillis();
				mCalendar.add(Calendar.MONTH, 1);
			}
		}

		int getDaysNumInMonth(long time) {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(time);
			return mCalendar.getActualMaximum(Calendar.DATE);
		}
	}

	class JsqWaterDetailsOfMonth {
		List<JsqDataStatistics> details = new ArrayList<JsqDataStatistics>();

		int getFilterWaterTimes() {
			int times = 0;
			for (JsqDataStatistics data : details) {
				times += data.totalFilterWaterTimes;
			}
			return times;
		}

		List<JsqDataStatistics> getDetails() {
			return details;
		}

		JsqWaterDetailsOfMonth addDetails(JsqDataStatistics jsqData) {
			details.add(jsqData);
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
				JsqDataStatistics jsqData;
				JsqWaterDetailsOfMonth jsqMonthData;

				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(usedDate.clearedTime);
				mCalendar.add(Calendar.MONTH, 1 - numMonth); // 月减11

				SQLiteDatabase db = GlobalInfo.databaseHelperJSQ.getWritableDatabase();

				for (int i = 0; i < numMonth; i++) {
					int daysNum = mCalendar.getActualMaximum(Calendar.DATE);
					jsqMonthData = new JsqWaterDetailsOfMonth();
					for (int j = 1; j <= daysNum; j++) {
						mCalendar.set(Calendar.DAY_OF_MONTH, j);
						jsqData = new JsqDataStatistics();
						Cursor c = db.rawQuery("SELECT * FROM " + GlobalInfo.DB_TABLE_NAME_JSQ + " WHERE " + GlobalInfo.DB_TIME + "=" + mCalendar.getTimeInMillis(), null);
						if (c != null) {
							Log.d("读取到条数：", c.getCount() + "");
							if (c.getCount() > 0) {
								String[] cols = c.getColumnNames();
								for (String ColumnName : cols) {
									Log.i("LAST", ColumnName + ":" + c.getString(c.getColumnIndex(ColumnName)));
								}
								jsqData.averageTDSIn = c.getInt(c.getColumnIndex(GlobalInfo.DB_AVERAGE_TDS_IN));
								jsqData.averageTDSOut = c.getInt(c.getColumnIndex(GlobalInfo.DB_AVERAGE_TDS_OUT));
								jsqData.totalWaterIn = c.getFloat(c.getColumnIndex(GlobalInfo.DB_TOTAL_WATER_IN));
								jsqData.totalWaterOut = c.getFloat(c.getColumnIndex(GlobalInfo.DB_TOTAL_WATER_OUT));
								jsqData.totalFilterWaterTimes = c.getInt(c.getColumnIndex(GlobalInfo.DB_TOTAL_FILTER_WATER_TIMES));
							} else {// 生成测试数据///////////////////////////////////////////////////////////////////////////////////////////////////////
								jsqData.averageTDSIn = (int) (Math.random() * 200 + 100);//// 生成测试数据//////////////////////////////////////////////////////////////////////////////////////////////////////
								jsqData.averageTDSOut = (int) (Math.random() * 80);//// 生成测试数据//////////////////////////////////////////////////////////////////////////////////////////////////////
								jsqData.totalWaterIn = (float) Math.random() * 20;//// 生成测试数据//////////////////////////////////////////////////////////////////////////////////////////////////////
								jsqData.totalWaterOut = (float) Math.random() * 10;//// 生成测试数据//////////////////////////////////////////////////////////////////////////////////////////////////////
								jsqData.totalFilterWaterTimes = (int) (Math.random() * 100);//// 生成测试数据///////////////////////////////////////////////////////////////////////////////////////////////////////
							}
							c.close();
						}
						jsqMonthData.addDetails(jsqData);
					}
					jsqWaterDetails.add(jsqMonthData);
					mCalendar.add(Calendar.MONTH, 1);
				}

				GlobalInfo.databaseHelperJSQ.close();
				Message msg = new Message();
				msg.what = READ_DB_FINISH;
				handler.sendMessage(msg);
			}
		}.start();
	}
}