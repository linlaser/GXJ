package com.ywangwang.gxj;

import java.util.Arrays;

import rc522_spi_api.RC522;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LifeServicesActivity extends Activity {
	private final int GET_CARD_DATA_FAILED = -1;
	private final int GET_CARD_DATA_AUCCESS = 1;
	private FrameLayout container;
	private View viewCountCard;
	private boolean rc522Busy = false;
	TextView tvShow, tvCardData;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_services);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("返回主界面");
		getActionBar().setDisplayShowHomeEnabled(false);
		findViewById(R.id.btnCountCard).setOnClickListener(new ButtonListener());
		findViewById(R.id.btnMoneyCard).setOnClickListener(new ButtonListener());
		findViewById(R.id.btnHeXinTong).setOnClickListener(new ButtonListener());
		findViewById(R.id.btnPowerCard).setOnClickListener(new ButtonListener());
		container = (FrameLayout) findViewById(R.id.container);
		viewCountCard = LayoutInflater.from(LifeServicesActivity.this).inflate(R.layout.count_card, null);
		tvShow = (TextView) viewCountCard.findViewById(R.id.tvShow);
		tvCardData = (TextView) viewCountCard.findViewById(R.id.tvCardData);
		viewCountCard.findViewById(R.id.btnGetCardData).setOnClickListener(new ButtonListener());
	}

	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnCountCard:
				tvShow.setText(R.string.count_card);
				container.setBackgroundResource(R.drawable.img7);
				container.removeAllViews();
				container.addView(viewCountCard);
				break;
			case R.id.btnMoneyCard:
				tvShow.setText(R.string.money_card);
				container.setBackgroundResource(R.drawable.img8);
				container.removeAllViews();
				container.addView(viewCountCard);
				break;
			case R.id.btnHeXinTong:
				tvShow.setText(R.string.he_xin_tong);
				container.setBackgroundResource(R.drawable.img9);
				container.removeAllViews();
				container.addView(viewCountCard);
				break;
			case R.id.btnPowerCard:
				tvShow.setText(R.string.power_card);
				container.setBackgroundResource(R.drawable.img12);
				container.removeAllViews();
				container.addView(viewCountCard);
				break;
			case R.id.btnGetCardData:
				tvCardData.setText("正在获取IC卡信息...");
				getCardData();
				break;
			default:
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler delayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String newString = new String();
			if (msg.what == GET_CARD_DATA_AUCCESS) {
				newString = "获取IC卡信息成功！\n\nIC卡类型：";
				newString += String.format("%02X", ((CardData) msg.obj).card_type[0]) + String.format("%02X", ((CardData) msg.obj).card_type[1]);
				newString += "\nIC卡ID号：";
				for (int i = 0; i < 4; i++) {
					newString += String.format("%02X", ((CardData) msg.obj).cardId[i]);
				}
				newString += "\n\n";
				if (((CardData) msg.obj).checkResult) {
					newString += "第1数据块数据：\n";
					for (int i = 0; i < 16; i++) {
						newString += String.format("%02X", ((CardData) msg.obj).readData[i]);
						if (i == 7)
							newString += "\n";
					}
				} else {
					newString += "KEY校验失败，无法读取数据。";
				}
			} else if (msg.what == GET_CARD_DATA_FAILED) {
				newString = "获取IC卡信息失败！\n请重试";
			}
			tvCardData.setText(newString);
			super.handleMessage(msg);
		}
	};

	private class CardData {
		byte[] card_type = new byte[2];
		byte[] cardId = new byte[4];
		byte[] readData = new byte[16];
		byte[] DefaultKey = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		boolean checkResult = false;
	}

	private void getCardData() {
		new Thread() {
			@Override
			public void run() {
				if (rc522Busy) {
					return;
				} else {
					rc522Busy = true;
				}
				int t = 3;
				int fd = 0;
				boolean getData = false;
				CardData newCardData = new CardData();
				while (t-- > 0) {
					getData = false;
					if (fd <= 0) {
						fd = RC522.open(RC522.DEVICE, RC522.MODE, RC522.BITS, RC522.SPEED);
						Log.d("f=", fd + "");
						if (fd <= 0)
							continue;
					}
					if (RC522.request(RC522.PICC_REQALL, newCardData.card_type) != 0) {
						if (RC522.request(RC522.PICC_REQALL, newCardData.card_type) != 0) {
							continue;
						}
					}
					Log.d("card_type=", Arrays.toString(newCardData.card_type));
					if (RC522.anticoll(newCardData.cardId) != 0) {
						continue;
					}
					Log.d("cardId=", Arrays.toString(newCardData.cardId));
					if (RC522.select(newCardData.cardId) != 0) {
						continue;
					}
					getData = true;
					Log.d("select_cardId=", Arrays.toString(newCardData.cardId));
					if (RC522.auth_state(RC522.PICC_AUTHENT1A, (byte) 1, newCardData.DefaultKey, newCardData.cardId) != 0) {
						break;
					}
					Log.d("auth_state=", Arrays.toString(newCardData.cardId));
					if (RC522.read((byte) 1, newCardData.readData) != 0) {
						break;
					}
					Log.d("read=", Arrays.toString(newCardData.readData));
					newCardData.checkResult = true;
					break;
				}
				if (fd > 0)
					RC522.close(fd);
				try {
					Message msg = new Message();
					if (getData) {
						msg.what = GET_CARD_DATA_AUCCESS;
						msg.obj = newCardData;
					} else {
						msg.what = GET_CARD_DATA_FAILED;
					}
					delayHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				rc522Busy = false;
			}
		}.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
