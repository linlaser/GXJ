package com.ywangwang.gxj;

import android.util.Log;

public class Debug {

	public Debug() {
	}

	public static void d(String tag, String msg, boolean enable) {
		if (enable) {
			d(tag, msg);
		}
	}

	public static void e(String tag, String msg, boolean enable) {
		if (enable) {
			e(tag, msg);
		}
	}

	public static void i(String tag, String msg, boolean enable) {
		if (enable) {
			i(tag, msg);
		}
	}

	public static void v(String tag, String msg, boolean enable) {
		if (enable) {
			v(tag, msg);
		}
	}

	public static void w(String tag, String msg, boolean enable) {
		if (enable) {
			w(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (GlobalInfo.debug) {
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (GlobalInfo.debug) {
			Log.e(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (GlobalInfo.debug) {
			Log.i(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (GlobalInfo.debug) {
			Log.v(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (GlobalInfo.debug) {
			Log.w(tag, msg);
		}
	}
}
