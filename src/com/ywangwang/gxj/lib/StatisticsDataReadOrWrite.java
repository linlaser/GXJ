package com.ywangwang.gxj.lib;

import java.util.Calendar;

import com.ywangwang.gxj.GlobalInfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StatisticsDataReadOrWrite {
	public static void jsqDataInit() {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		mCalendar.set(Calendar.HOUR_OF_DAY, 0); // 24Сʱ�ƣ�ʱ����
		mCalendar.set(Calendar.MINUTE, 0); // ������
		mCalendar.set(Calendar.SECOND, 0); // ������
		mCalendar.set(Calendar.MILLISECOND, 0); // ��������
		SQLiteDatabase db = GlobalInfo.databaseHelperJSQ.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + GlobalInfo.DB_TABLE_NAME_JSQ + " WHERE " + GlobalInfo.DB_TIME + "=" + mCalendar.getTimeInMillis(), null);
		if (c != null) {
			Log.d("��ȡ��������", c.getCount() + "");
			if (c.getCount() > 0) {
				String[] cols = c.getColumnNames();
				for (String ColumnName : cols) {
					Log.i("LAST", ColumnName + ":" + c.getString(c.getColumnIndex(ColumnName)));
				}
				GlobalInfo.todayJsqDataStatistics.averageTDSIn = c.getInt(c.getColumnIndex(GlobalInfo.DB_AVERAGE_TDS_IN));
				GlobalInfo.todayJsqDataStatistics.averageTDSOut = c.getInt(c.getColumnIndex(GlobalInfo.DB_AVERAGE_TDS_OUT));
				GlobalInfo.todayJsqDataStatistics.totalWaterIn = c.getFloat(c.getColumnIndex(GlobalInfo.DB_TOTAL_WATER_IN));
				GlobalInfo.todayJsqDataStatistics.totalWaterOut = c.getFloat(c.getColumnIndex(GlobalInfo.DB_TOTAL_WATER_OUT));
				GlobalInfo.todayJsqDataStatistics.totalFilterWaterTimes = c.getInt(c.getColumnIndex(GlobalInfo.DB_TOTAL_FILTER_WATER_TIMES));
			}
			c.close();
		}
		GlobalInfo.todayJsqDataStatistics.time = mCalendar.getTimeInMillis();
		GlobalInfo.databaseHelperJSQ.close();
	}

	public static void jsqWrite() {
		boolean nowHourIs_0 = false;
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		if (mCalendar.get(Calendar.HOUR_OF_DAY) == 0) {// �����0��
			nowHourIs_0 = true;
			mCalendar.add(Calendar.DATE, -1); // �ռ�1
		}
		mCalendar.set(Calendar.HOUR_OF_DAY, 0); // 24Сʱ�ƣ�ʱ����
		mCalendar.set(Calendar.MINUTE, 0); // ������
		mCalendar.set(Calendar.SECOND, 0); // ������
		mCalendar.set(Calendar.MILLISECOND, 0); // ��������
		SQLiteDatabase db = GlobalInfo.databaseHelperJSQ.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(GlobalInfo.DB_AVERAGE_TDS_IN, GlobalInfo.todayJsqDataStatistics.averageTDSIn);
		values.put(GlobalInfo.DB_AVERAGE_TDS_OUT, GlobalInfo.todayJsqDataStatistics.averageTDSOut);
		values.put(GlobalInfo.DB_TOTAL_WATER_IN, GlobalInfo.todayJsqDataStatistics.totalWaterIn);
		values.put(GlobalInfo.DB_TOTAL_WATER_OUT, GlobalInfo.todayJsqDataStatistics.totalWaterOut);
		values.put(GlobalInfo.DB_TOTAL_FILTER_WATER_TIMES, GlobalInfo.todayJsqDataStatistics.totalFilterWaterTimes);
		values.put(GlobalInfo.DB_TIME, mCalendar.getTimeInMillis());// ��Ϊʱ�������Ϊ����ͬ����ԭ����ָ��£����Դ洢��ʱ����������ʱ��Ϊ׼
		db.delete(GlobalInfo.DB_TABLE_NAME_JSQ, GlobalInfo.DB_TIME + "=" + mCalendar.getTimeInMillis(), null);
		db.insert(GlobalInfo.DB_TABLE_NAME_JSQ, null, values);
		values.clear();
		if (nowHourIs_0 == true) {
			GlobalInfo.todayJsqDataStatistics.clear();// ����ʱ�����µ�һ�죬������Ҫ���ǰһ�������
			mCalendar.add(Calendar.DATE, 1); // �ռ�1
			GlobalInfo.todayJsqDataStatistics.time = mCalendar.getTimeInMillis();
		}
		GlobalInfo.databaseHelperJSQ.close();
	}

	public static void gxjWrite(DatabaseHelper databaseHelper, int[] gxjData_temp) {
		long time = System.currentTimeMillis();
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(GlobalInfo.DB_AVERAGE_TDS, gxjData_temp[0]);
		values.put(GlobalInfo.DB_WATER_AMOUNT, gxjData_temp[1]);
		values.put(GlobalInfo.DB_TEMPERATURE, gxjData_temp[2]);
		values.put(GlobalInfo.DB_TIME, time);
		db.insert(GlobalInfo.DB_TABLE_NAME_GXJ, null, values);
		values.clear();
		databaseHelper.close();
	}
}
