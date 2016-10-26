package com.ywangwang.gxj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ywangwang.gxj.lib.ProtocolData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class BindDevice extends Activity {

	ListView lvwBoundDevice, lvwSearchedDevice;
	SimpleAdapter saBoundDevice, saSearchedDevice;
	GlobalInfo.SubDevice[] searchedDevice = new GlobalInfo.SubDevice[10];
	int searchedDeviceCount = 0;
	List<Map<String, Object>> listBoundDevice, listSearchedDevice;
	GlobalInfo.SubDevice bindSubDevice_temp = new GlobalInfo.SubDevice();

	ProgressDialog proDialog;
	Handler handler = new Handler();
	Runnable runnableSearch;
	// int t = 0;
	int times = 0;
	final int RESEARCH = 222;
	int listViewPosition = 0;
	@SuppressLint("HandlerLeak")
	private Handler delayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == GlobalInfo.HANDLER_DELAY_BROADCAST_BIND_DEVICE) {
				times++;
				if (bindSubDevice_temp.used) {
					if (times > 5) {
						times = 0;
						bindSubDevice_temp.used = false;
						proDialog.dismiss();
						refreshList();
						CustomDialog newDialog = new CustomDialog();
						newDialog.show(BindDevice.this, "��ʧ��", "��ʧ�ܣ�");
					} else {
						sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_BIND_DEVICE, (int[]) msg.obj));
						delaySendData(GlobalInfo.HANDLER_DELAY_BROADCAST_BIND_DEVICE, (int[]) msg.obj, 1800);
					}
				} else {
					times = 0;
					proDialog.dismiss();
					bindNewJSQDeivce(bindSubDevice_temp);
					CustomDialog newDialog = new CustomDialog();
					newDialog.show(BindDevice.this, "�󶨳ɹ�", "�󶨳ɹ���");
				}
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bind_device);

		lvwBoundDevice = (ListView) findViewById(R.id.lvwBoundDevice);
		lvwSearchedDevice = (ListView) findViewById(R.id.lvwSearchedDevice);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("�����豸����");
		getActionBar().setDisplayShowHomeEnabled(false);
		listBoundDevice = new ArrayList<Map<String, Object>>();
		listSearchedDevice = new ArrayList<Map<String, Object>>();
		saBoundDevice = new SimpleAdapter(this, listBoundDevice, R.layout.device_list_item, new String[] { "tvDeviceInfo", "tvBindingStatus" }, new int[] { R.id.tvDeviceInfo, R.id.tvBindingStatus });
		saSearchedDevice = new SimpleAdapter(this, listSearchedDevice, R.layout.device_list_item, new String[] { "tvDeviceInfo", "tvBindingStatus" }, new int[] { R.id.tvDeviceInfo, R.id.tvBindingStatus });
		lvwBoundDevice.setAdapter(saBoundDevice);
		lvwSearchedDevice.setAdapter(saSearchedDevice);
		lvwBoundDevice.setOnItemClickListener(boundDeviceListener);
		lvwSearchedDevice.setOnItemClickListener(searchedDeviceListener);
		getBoundDevice();

		proDialog = new ProgressDialog(BindDevice.this);
		proDialog.setMessage("���С�����"); // ����˵������
		proDialog.setCanceledOnTouchOutside(false); // ���õ����Ļ����ʧ

		MainActivity.pauseProtocolDataProcess = true;
		sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_SEARCH_DEVICE, true));
		runnableSearch = new Runnable() {
			@Override
			public void run() {
				// handler.postDelayed(this, 500);
			}
		};
		// handler.postDelayed(runnableSearch, 500);
		registerReceiver(broadcastReceiver, new IntentFilter(GlobalInfo.BROADCAST_GXJ_BIND_DEVICE_ACTION));
		CustomDialog newDialog = new CustomDialog();
		newDialog.show(BindDevice.this, "˵��", "������ˮ���ĳ�ϴ��5~10�룬������..��..��..������\n��ˮ���Ϳɽ��뱻����״̬��");
	}

	OnItemClickListener boundDeviceListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			CustomDialog newDialog = new CustomDialog();
			newDialog.show(BindDevice.this, "ȡ�����豸", "ȷ��ȡ���󶨾�ˮ����" + GlobalInfo.boundJSQ.getDeviceType() + " (" + GlobalInfo.boundJSQ.add + ")����\nȡ���󶨺�֮ǰ�ľ�ˮ���ݽ��������", "ȡ��", "ȷ��", null, runnableUnBindNewJSQDeivce());
		}
	};
	OnItemClickListener searchedDeviceListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			CustomDialog newDialog = new CustomDialog();
			if (position < searchedDevice.length) {
				if (searchedDevice[position].add == GlobalInfo.boundJSQ.add && GlobalInfo.boundJSQ.used == true) {
					return;
				} else if (searchedDevice[position].add != GlobalInfo.boundJSQ.add && GlobalInfo.boundJSQ.used == true) {
					listViewPosition = position;
					newDialog.show(BindDevice.this, "���豸", "��ǰ�豸�Ѿ����˾�ˮ����" + GlobalInfo.boundJSQ.getDeviceType() + " (" + GlobalInfo.boundJSQ.add + ")����\n������°��µľ�ˮ����" + searchedDevice[position].getDeviceType() + " (" + searchedDevice[position].add + ")����֮ǰ�ľ�ˮ���ݽ��������\n��ȷ���Ƿ�󶨣�", "ȡ��", "ȷ��", null, runnableBindNewJSQDeivce());
				} else if (GlobalInfo.boundJSQ.used == false) {
					listViewPosition = position;
					newDialog.show(BindDevice.this, "���豸", "ȷ�ϰ��µľ�ˮ����" + searchedDevice[position].getDeviceType() + " (" + searchedDevice[position].add + ")����", "ȡ��", "ȷ��", null, runnableBindNewJSQDeivce());
				}
			}
		}
	};

	void getBoundDevice() {
		listBoundDevice.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		if (GlobalInfo.boundJSQ.used == true) {
			map.put("tvDeviceInfo", GlobalInfo.boundJSQ.getDeviceType() + " (" + GlobalInfo.boundJSQ.add + ")");
			map.put("tvBindingStatus", "�Ѱ�");
			GlobalInfo.boundJSQ.itemId = 0;
			listBoundDevice.add(map);
		}
		saBoundDevice.notifyDataSetChanged();
	}

	void addSearchedDevice(GlobalInfo.SubDevice newSearchedDevice) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (searchedDeviceCount < searchedDevice.length) {
			for (int i = 0; i < searchedDeviceCount; i++) {
				if (newSearchedDevice.add == searchedDevice[i].add) {
					return;
				}
			}
			map.put("tvDeviceInfo", newSearchedDevice.getDeviceType() + " (" + newSearchedDevice.add + ")");
			if (newSearchedDevice.add == GlobalInfo.boundJSQ.add) {
				map.put("tvBindingStatus", "�Ѱ�");
			} else {
				map.put("tvBindingStatus", "���豸");
			}
			listSearchedDevice.add(map);
			newSearchedDevice.itemId = searchedDeviceCount;
			searchedDevice[searchedDeviceCount] = newSearchedDevice;
			searchedDeviceCount++;
		} else if (searchedDeviceCount == searchedDevice.length) {
			map.put("tvDeviceInfo", "�ѵ���");
			map.put("tvBindingStatus", "");
			listSearchedDevice.add(map);
			searchedDeviceCount++;
		} else {
			return;
		}
		saSearchedDevice.notifyDataSetChanged();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ProtocolData si4463Data_temp = new ProtocolData();
			if (intent.getExtras().getByteArray(GlobalInfo.BROADCAST_NEW_WIRELESS_DATA_SEARCHING) != null) {
				si4463Data_temp.setData(intent.getExtras().getByteArray(GlobalInfo.BROADCAST_NEW_WIRELESS_DATA_SEARCHING));
				GlobalInfo.SubDevice searchedDevice_temp = new GlobalInfo.SubDevice();
				searchedDevice_temp.deviceType = si4463Data_temp.deviceType;
				searchedDevice_temp.add = si4463Data_temp.subDeviceAdd;
				addSearchedDevice(searchedDevice_temp);
			} else if (intent.getExtras().getByteArray(GlobalInfo.BROADCAST_NEW_WIRELESS_DATA_BINDING) != null) {
				si4463Data_temp.setData(intent.getExtras().getByteArray(GlobalInfo.BROADCAST_NEW_WIRELESS_DATA_BINDING));
				if (bindSubDevice_temp.used) {
					if (bindSubDevice_temp.deviceType == si4463Data_temp.deviceType && bindSubDevice_temp.add == si4463Data_temp.subDeviceAdd) {
						bindSubDevice_temp.used = false;
					}
				}
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case RESEARCH:
			sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_SEARCH_DEVICE, true));
			refreshList();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_STOP_SEARCH_DEVICE, true));
		handler.removeCallbacksAndMessages(null);
		delayHandler.removeCallbacksAndMessages(null);
		unregisterReceiver(broadcastReceiver);
		MainActivity.pauseProtocolDataProcess = false;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem reSearch = menu.add(0, RESEARCH, 0, "��������");
		reSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	private Runnable runnableBindNewJSQDeivce() {
		return new Runnable() {
			public void run() {
				int[] temp = { searchedDevice[listViewPosition].deviceType, searchedDevice[listViewPosition].add };
				bindSubDevice_temp.used = true;
				bindSubDevice_temp.deviceType = searchedDevice[listViewPosition].deviceType;
				bindSubDevice_temp.add = searchedDevice[listViewPosition].add;
				sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_BIND_DEVICE, temp));
				delaySendData(GlobalInfo.HANDLER_DELAY_BROADCAST_BIND_DEVICE, temp, 1800);
				proDialog.show();
			}
		};
	}

	private Runnable runnableUnBindNewJSQDeivce() {
		return new Runnable() {
			public void run() {
				bindNewJSQDeivce(null, false);
			}
		};
	}

	private void bindNewJSQDeivce(GlobalInfo.SubDevice sDeivce) {
		bindNewJSQDeivce(sDeivce, true);
	}

	private void bindNewJSQDeivce(GlobalInfo.SubDevice sDeivce, boolean bind) {
		GlobalInfo.boundJSQ.used = bind;
		GlobalInfo.boundJSQ.add = bind ? sDeivce.add : 0;
		GlobalInfo.boundJSQ.deviceType = bind ? sDeivce.deviceType : 0;
		SharedPreferences sharedPreferences = BindDevice.this.getSharedPreferences("config", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("boundJSQ_used", GlobalInfo.boundJSQ.used);
		editor.putInt("boundJSQ_add", GlobalInfo.boundJSQ.add);
		editor.putInt("boundJSQ_deviceType", GlobalInfo.boundJSQ.deviceType);
		editor.commit();
		refreshList();
	}

	private void refreshList() {
		getBoundDevice();
		listSearchedDevice.clear();
		searchedDeviceCount = 0;
		saSearchedDevice.notifyDataSetChanged();
	}

	private void delaySendData(final int what, final int[] data, final long delayMillis) {
		new Thread() {
			@Override
			public void run() {
				try {
					Message msg = new Message();
					msg.what = what;
					msg.obj = data;
					delayHandler.sendMessageDelayed(msg, delayMillis);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}