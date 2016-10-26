package com.ywangwang.gxj.lib;

import java.util.concurrent.atomic.AtomicInteger;

import com.ywangwang.gxj.GlobalInfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private AtomicInteger mOpenCounter = new AtomicInteger();
	private SQLiteDatabase mDatabase;
	static final String JSQ_SQL_START = "create table if not exists " + GlobalInfo.DB_TABLE_NAME_JSQ + "(_id integer primary key autoincrement,";
	static final String JSQ_SQL1 = GlobalInfo.DB_AVERAGE_TDS_IN + " integer not null,";
	static final String JSQ_SQL2 = GlobalInfo.DB_AVERAGE_TDS_OUT + " integer not null,";
	static final String JSQ_SQL3 = GlobalInfo.DB_TOTAL_WATER_IN + " REAL not null,";
	static final String JSQ_SQL4 = GlobalInfo.DB_TOTAL_WATER_OUT + " REAL not null,";
	static final String JSQ_SQL5 = GlobalInfo.DB_TOTAL_FILTER_WATER_TIMES + " integer not null,";
	static final String JSQ_SQL_END = GlobalInfo.DB_TIME + " integer not null)";
	static final String JSQ_SQL_STATEMENT = JSQ_SQL_START + JSQ_SQL1 + JSQ_SQL2 + JSQ_SQL3 + JSQ_SQL4 + JSQ_SQL5 + JSQ_SQL_END;

	static final String GXJ_SQL_START = "create table if not exists " + GlobalInfo.DB_TABLE_NAME_GXJ + "(_id integer primary key autoincrement,";
	static final String GXJ_SQL1 = GlobalInfo.DB_AVERAGE_TDS + " integer not null,";
	static final String GXJ_SQL2 = GlobalInfo.DB_WATER_AMOUNT + " integer not null,";
	static final String GXJ_SQL3 = GlobalInfo.DB_TEMPERATURE + " integer not null,";
	static final String GXJ_SQL_END = GlobalInfo.DB_TIME + " integer not null)";
	static final String GXJ_SQL_STATEMENT = GXJ_SQL_START + GXJ_SQL1 + GXJ_SQL2 + GXJ_SQL3 + GXJ_SQL_END;

	public DatabaseHelper(Context context, String name) {
		super(context, name, null, 1);
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version, String tableName) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String databaseName = getDatabaseName();
		if (databaseName.equals(GlobalInfo.DB_FILE_NAME_JSQ)) {
			db.execSQL(JSQ_SQL_STATEMENT);
		} else if (databaseName.equals(GlobalInfo.DB_FILE_NAME_GXJ)) {
			db.execSQL(GXJ_SQL_STATEMENT);
		}
		Log.e("onCreate", "onCreate:" + databaseName);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	public synchronized void close() {
		if (mOpenCounter.decrementAndGet() == 0) {
			Log.e("close", "close");
			super.close();
		}
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			mDatabase = super.getWritableDatabase();
		}
		return mDatabase;
	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			mDatabase = super.getWritableDatabase();
		}
		return mDatabase;
	}
}
