package com.ywangwang.gxj;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends Activity {

	Button btnBack, t1, t2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new buttonListener());
		findViewById(R.id.t1).setOnClickListener(new buttonListener());
		findViewById(R.id.t2).setOnClickListener(new buttonListener());
	}

	class buttonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.t1:
				((TextView) findViewById(R.id.tvTime)).setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", System.currentTimeMillis()));
				break;
			case R.id.t2:
				// testDate();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		MainActivity.resetADTimer();
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onResume() {
		MainActivity.resetADTimer();
		super.onResume();
	}

	@Override
	protected void onPause() {
		MainActivity.stopADTimer();
		super.onPause();
	}
}
