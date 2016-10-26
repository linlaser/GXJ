package com.ywangwang.gxj.device_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ywangwang.gxj.Debug;
import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.R;
import com.ywangwang.gxj.WebViewMain;
import com.ywangwang.gxj.lib.CustomToast;
import com.ywangwang.gxj.lib.SessionKey;
import com.ywangwang.gxj.lib.SharedPreferencesConfig;
import com.ywangwang.gxj.net.JsonTools;
import com.ywangwang.gxj.net.Operaton;
import com.ywangwang.gxj.net.WaterCode;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class WaterCodeFragment extends Fragment {
	private final String TAG = "WaterCodeFragment";
	private final boolean DEBUG = true;

	private final int MENU_RELOAD = 1;
	private final int BIND_SUCCESS = 1;
	private final int BIND_FAIL = 2;
	private final int UNBIND_SUCCESS = 3;
	private final int UNBIND_FAIL = 4;
	private final int LOAD_WATER_CODE_SUCCESS = 5;
	private final int LOAD_WATER_CODE_FAIL = 6;
	private final int LOAD_WATER_CODE_EMPTY = 7;
	private final int UPDATE_WATER_CODE_SUCCESS = 8;
	private final int UPDATE_WATER_CODE_FAIL = 9;

	CustomToast toast;
	SessionKey sessionKey = new SessionKey();

	private Button btnUnbind, btnUpdateWaterCode;
	private TextView tvBindWaterCode;
	private ListView lvwWaterCode;
	private List<Map<String, Object>> listWaterCode;
	private MySimpleAdapter saWaterCode;
	private List<WaterCode> gotWaterCodes = new ArrayList<WaterCode>();

	RelativeLayout layoutWait;
	ProgressDialog proDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.i(TAG, TAG + "-->>onCreate", DEBUG);
		Debug.i(TAG, TAG + "-->>onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Debug.i(TAG, TAG + "-->>onCreateView", DEBUG);
		View view = inflater.inflate(R.layout.device_management_water_code, container, false);
		layoutWait = (RelativeLayout) view.findViewById(R.id.includeWaitLayout);
		layoutWait.setVisibility(View.VISIBLE);
		((TextView) layoutWait.findViewById(R.id.tvWait)).setText("正在获取");
		btnUnbind = (Button) view.findViewById(R.id.btnUnbind);
		btnUnbind.setOnClickListener(ButtonListener);
		btnUpdateWaterCode = (Button) view.findViewById(R.id.btnUpdateWaterCode);
		btnUpdateWaterCode.setOnClickListener(ButtonListener);
		view.findViewById(R.id.btnBuyWaterCode).setOnClickListener(ButtonListener);
		tvBindWaterCode = (TextView) view.findViewById(R.id.tvBindWaterCode);
		tvBindWaterCode.setOnClickListener(ButtonListener);
		lvwWaterCode = (ListView) view.findViewById(R.id.lvwWaterCode);
		lvwWaterCode.setVisibility(View.GONE);
		listWaterCode = new ArrayList<Map<String, Object>>();
		saWaterCode = new MySimpleAdapter(getActivity(), listWaterCode, R.layout.water_code_list_item, new String[] { "tvNumber", "tvPeriodValidity", "tvStatus", "tvBoundDeviceID" }, new int[] { R.id.tvNumber, R.id.tvPeriodValidity, R.id.tvStatus, R.id.tvBoundDeviceID });
		lvwWaterCode.setAdapter(saWaterCode);
		lvwWaterCode.setOnItemClickListener(itemClickListener);
		setHasOptionsMenu(true);
		toast = new CustomToast(getActivity());

		if (GlobalInfo.Logined == false) {
			UserInfoFragment userInfoFragment = new UserInfoFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragmentContainer, userInfoFragment);
			Bundle bundle = new Bundle();
			bundle.putBoolean("getWaterCode", true);
			userInfoFragment.setArguments(bundle);
			transaction.commit();
		} else {
			getWaterCode();
			// gotWaterCodes = getWaterCode();
			// addWaterCodes(gotWaterCodes);
			refreshDisplay();
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Debug.i(TAG, TAG + "-->>onStart", DEBUG);
	}

	@Override
	public void onResume() {
		super.onResume();
		Debug.i(TAG, TAG + "-->>onResume", DEBUG);
	}

	@Override
	public void onPause() {
		super.onPause();
		Debug.i(TAG, TAG + "-->>onPause", DEBUG);
	}

	@Override
	public void onStop() {
		super.onStop();
		Debug.i(TAG, TAG + "-->>onStop", DEBUG);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Debug.i(TAG, TAG + "-->>onDestroyView", DEBUG);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		sessionKey.cleanSessionKey();
		super.onDestroy();
		Debug.i(TAG, TAG + "-->>onDestroy", DEBUG);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Debug.i(TAG, TAG + "-->>onDetach", DEBUG);
	}

	OnClickListener ButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnUnbind:
				if (GlobalInfo.isBoundWaterCode == false) {
					return;
				}
				AlertDialog.Builder builer1 = new Builder(getActivity());
				builer1.setTitle("解除绑定");
				builer1.setMessage("确认解除绑定取水码：" + GlobalInfo.boundWaterCode.getNumberDescribe() + " ?");
				builer1.setPositiveButton("解绑", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						new Thread() {
							@Override
							public void run() {
								Message msg = new Message();
								msg.arg1 = sessionKey.generateNewSessionKey();
								Operaton operaton = new Operaton(getActivity());
								String result = operaton.unbindWaterCode(GlobalInfo.username, GlobalInfo.password, GlobalInfo.boundWaterCode.getNumber() + "", GlobalInfo.boundWaterCode.getBoundDeviceId() + "");
								Debug.d(TAG, "unbindWaterCode-->" + result);
								if (result.equals("1") == true) {
									msg.what = UNBIND_SUCCESS;
								} else {
									msg.what = UNBIND_FAIL;
									msg.obj = result;
								}
								handler.sendMessage(msg);
								super.run();
							}
						}.start();
					}
				});
				builer1.setNegativeButton("取消", null);
				AlertDialog dialog1 = builer1.create();
				dialog1.show();
				break;
			case R.id.btnUpdateWaterCode:
				if (GlobalInfo.isBoundWaterCode == false) {
					return;
				}
				if (proDialog == null) {
					proDialog = new ProgressDialog(getActivity());
				}
				proDialog.setMessage("正在更新...");
				proDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						sessionKey.cleanSessionKey();
					}
				});
				new Thread() {
					@Override
					public void run() {
						Message msg = new Message();
						msg.arg1 = sessionKey.generateNewSessionKey();
						Operaton operaton = new Operaton(getActivity());
						String result = operaton.updateWaterCode(GlobalInfo.username, GlobalInfo.password, GlobalInfo.boundWaterCode.getNumber() + "");
						if (result.length() > 20) {
							List<WaterCode> waterCodes = new ArrayList<WaterCode>();
							waterCodes = JsonTools.getWaterCodesFormJSON("waterCode", result);
							if (waterCodes.size() > 0) {
								msg.what = UPDATE_WATER_CODE_SUCCESS;
								msg.obj = waterCodes.get(0);
							} else {
								msg.what = UPDATE_WATER_CODE_FAIL;
								msg.obj = "数据解析失败";
							}
						} else {
							msg.what = UPDATE_WATER_CODE_FAIL;
							msg.obj = result;
						}
						handler.sendMessage(msg);
						super.run();
					}
				}.start();
				proDialog.show();
				break;
			case R.id.btnBuyWaterCode:
				startActivity(new Intent(getActivity(), WebViewMain.class));
				break;
			case R.id.tvBindWaterCode:
				if (GlobalInfo.isBoundWaterCode == true) {
					String msg = "编号：" + GlobalInfo.boundWaterCode.getNumberDescribe();
					msg += "\n类型：" + GlobalInfo.boundWaterCode.getTypeDescribe();
					msg += "\n状态：" + GlobalInfo.boundWaterCode.getStatusDescribe();
					msg += "\n绑定设备：" + GlobalInfo.boundWaterCode.getBoundDeviceIdDescribe();
					msg += "\n有效期：" + GlobalInfo.boundWaterCode.getPeriodValidityDescribe();
					msg += "\n激活时间：" + GlobalInfo.boundWaterCode.getActivationTimeDescribe();
					AlertDialog.Builder builer = new Builder(getActivity());
					builer.setTitle("已绑定取水码信息");
					builer.setMessage(msg);
					builer.setPositiveButton("确定", null);
					AlertDialog dialog = builer.create();
					dialog.show();
				}
				break;
			default:
				break;
			}
		}
	};
	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
			String msg = "编号：" + gotWaterCodes.get(position).getNumberDescribe();
			msg += "\n类型：" + gotWaterCodes.get(position).getTypeDescribe();
			msg += "\n状态：" + gotWaterCodes.get(position).getStatusDescribe();
			msg += "\n绑定设备：" + gotWaterCodes.get(position).getBoundDeviceIdDescribe();
			msg += "\n有效期：" + gotWaterCodes.get(position).getPeriodValidityDescribe();
			msg += "\n激活时间：" + gotWaterCodes.get(position).getActivationTimeDescribe();
			AlertDialog.Builder builer = new Builder(getActivity());
			builer.setTitle("取水码信息");
			builer.setMessage(msg);
			builer.setPositiveButton("确定", null);
			AlertDialog dialog = builer.create();
			dialog.show();
		}
	};

	private void refreshDisplay() {
		if (GlobalInfo.isBoundWaterCode == true) {
			btnUnbind.setEnabled(true);
			btnUpdateWaterCode.setEnabled(true);
			tvBindWaterCode.setText("编号：" + GlobalInfo.boundWaterCode.getNumberDescribe() + "，期限：" + GlobalInfo.boundWaterCode.getPeriodValidityDescribe());
		} else {
			btnUnbind.setEnabled(false);
			btnUpdateWaterCode.setEnabled(false);
			tvBindWaterCode.setText("未绑定取水码");
		}
		addWaterCodes(gotWaterCodes);
	}

	void addWaterCodes(List<WaterCode> newGotWaterCodes) {
		listWaterCode.clear();
		if (newGotWaterCodes != null && newGotWaterCodes.size() > 0) {
			for (WaterCode newWaterCode : newGotWaterCodes) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("tvNumber", newWaterCode.getNumberDescribe());
				map.put("tvPeriodValidity", newWaterCode.getPeriodValidityDescribe());
				map.put("tvStatus", newWaterCode.getStatusDescribe());
				map.put("tvBoundDeviceID", newWaterCode.getBoundDeviceIdDescribe());
				listWaterCode.add(map);
			}
		}
		saWaterCode.notifyDataSetChanged();
	}

	private void getWaterCode() {
		// List<WaterCode> a = new ArrayList<WaterCode>();
		// int x = new Random().nextInt(100);
		// for (int i = 0; i < x; i++) {
		// WaterCode w = new WaterCode();
		// w.setNumber(i);
		// w.setType(i);
		// w.setPeriodValidity(i);
		// w.setStatus(i);
		// w.setBoundDeviceId(234234L + i);
		// w.setActivationTime(i);
		// a.add(w);
		// }
		layoutWait.setVisibility(View.VISIBLE);
		lvwWaterCode.setVisibility(View.GONE);
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.arg1 = sessionKey.generateNewSessionKey();
				Operaton operaton = new Operaton(getActivity());
				String result = operaton.loadWaterCode(GlobalInfo.username, GlobalInfo.password);
				Debug.d(TAG, "loadWaterCode-->" + result);
				if (result.equals("0") == true) {
					msg.what = LOAD_WATER_CODE_EMPTY;
					msg.obj = "无取水码";
				} else if (result.length() > 20) {
					List<WaterCode> waterCodes = new ArrayList<WaterCode>();
					waterCodes = JsonTools.getWaterCodesFormJSON("waterCode", result);
					if (waterCodes.size() > 0) {
						msg.what = LOAD_WATER_CODE_SUCCESS;
						msg.obj = waterCodes;
					} else {
						msg.what = LOAD_WATER_CODE_FAIL;
						msg.obj = "数据解析失败";
					}
				} else {
					msg.what = LOAD_WATER_CODE_FAIL;
					msg.obj = result;
				}
				handler.sendMessage(msg);
				super.run();
			}

		}.start();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 如果会话KEY和最后一次不同，就忽略此msg
			if (sessionKey.getSessionKey() != msg.arg1) {
				return;
			}
			switch (msg.what) {
			case BIND_SUCCESS:
				GlobalInfo.isBoundWaterCode = true;
				GlobalInfo.boundWaterCode = (WaterCode) msg.obj;
				SharedPreferencesConfig.saveWaterCode(getActivity(), GlobalInfo.isBoundWaterCode, GlobalInfo.boundWaterCode);
				refreshDisplay();
				getWaterCode();
				toast.setText("绑定成功！").show();
				break;
			case BIND_FAIL:
				toast.setText("绑定失败！" + (String) msg.obj).show();
				break;
			case UNBIND_SUCCESS:
				GlobalInfo.isBoundWaterCode = false;
				GlobalInfo.boundWaterCode.clear();
				SharedPreferencesConfig.saveWaterCode(getActivity(), GlobalInfo.isBoundWaterCode, null);
				refreshDisplay();
				getWaterCode();
				toast.setText("解绑成功！").show();
				break;
			case UNBIND_FAIL:
				toast.setText("解绑失败！" + (String) msg.obj).show();
				break;
			case LOAD_WATER_CODE_SUCCESS:
				layoutWait.setVisibility(View.GONE);
				lvwWaterCode.setVisibility(View.VISIBLE);
				gotWaterCodes = (List<WaterCode>) msg.obj;
				refreshDisplay();
				// toast.setText("获取成功！").show();
				break;
			case LOAD_WATER_CODE_EMPTY:
				layoutWait.setVisibility(View.GONE);
				lvwWaterCode.setVisibility(View.VISIBLE);
				gotWaterCodes = null;
				refreshDisplay();
				toast.setText("用户无取水码").show();
				break;
			case LOAD_WATER_CODE_FAIL:
				layoutWait.setVisibility(View.GONE);
				lvwWaterCode.setVisibility(View.VISIBLE);
				gotWaterCodes = null;
				refreshDisplay();
				toast.setText("获取失败！" + (String) msg.obj).show();
				break;
			case UPDATE_WATER_CODE_SUCCESS:
				GlobalInfo.boundWaterCode = (WaterCode) msg.obj;
				SharedPreferencesConfig.saveWaterCode(getActivity(), GlobalInfo.isBoundWaterCode, GlobalInfo.boundWaterCode);
				refreshDisplay();
				proDialog.dismiss();
				toast.setText("更新成功！").show();
				break;
			case UPDATE_WATER_CODE_FAIL:
				refreshDisplay();
				proDialog.dismiss();
				toast.setText("更新失败！" + (String) msg.obj).show();
				break;
			default:
				break;
			}
		}
	};

	class MySimpleAdapter extends SimpleAdapter {
		public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.water_code_list_item, null);
			}
			Button button = (Button) convertView.findViewById(R.id.btnBind);
			button.setTag(position);
			button.setOnClickListener(btnBindListener);
			if (gotWaterCodes != null && gotWaterCodes.size() > position) {
				TextView tv = (TextView) convertView.findViewById(R.id.tvStatus);
				if (gotWaterCodes.get(position).getStatus() == WaterCode.NOT_ACTIVE) {
					tv.setTextColor(Color.BLUE);
					button.setEnabled(true);
				} else if (gotWaterCodes.get(position).getStatus() == WaterCode.ACTIVATED) {
					tv.setTextColor(Color.GREEN);
					button.setEnabled(true);
				} else {
					tv.setTextColor(Color.RED);
					button.setEnabled(false);
				}
				if (gotWaterCodes.get(position).getNumber() == GlobalInfo.boundWaterCode.getNumber() && GlobalInfo.isBoundWaterCode == true) {
					button.setEnabled(false);
				}
			}
			return super.getView(position, convertView, parent);
		}
	}

	OnClickListener btnBindListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final int position = (Integer) v.getTag();

			if (gotWaterCodes.get(position).getNumber() == GlobalInfo.boundWaterCode.getNumber() && GlobalInfo.isBoundWaterCode == true) {
				return;
			}
			AlertDialog.Builder builer = new Builder(getActivity());
			builer.setTitle("绑定取水码");
			if (gotWaterCodes.get(position).getNumber() != GlobalInfo.boundWaterCode.getNumber() && GlobalInfo.isBoundWaterCode == true) {
				builer.setMessage("当前设备已经绑定了取水码： " + GlobalInfo.boundWaterCode.getNumberDescribe() + "\n确认重新绑定：" + gotWaterCodes.get(position).getNumberDescribe() + "  ?");
			} else {
				builer.setMessage("是否绑定取水码： " + gotWaterCodes.get(position).getNumberDescribe() + "  ?");
			}
			builer.setPositiveButton("绑定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new Thread() {
						@Override
						public void run() {
							Message msg = new Message();
							msg.arg1 = sessionKey.generateNewSessionKey();
							Operaton operaton = new Operaton(getActivity());
							String result = operaton.bindWaterCode(GlobalInfo.username, GlobalInfo.password, gotWaterCodes.get(position).getNumber() + "", gotWaterCodes.get(position).getBoundDeviceId() + "");
							Debug.d(TAG, "bindWaterCode-->" + result);
							if (result.equals("1") == true) {
								msg.what = BIND_SUCCESS;
								msg.obj = gotWaterCodes.get(position);
							} else {
								msg.what = BIND_FAIL;
								msg.obj = result;
							}
							handler.sendMessage(msg);
							super.run();
						}
					}.start();
				}
			});
			builer.setNegativeButton("取消", null);
			AlertDialog dialog = builer.create();
			dialog.show();
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem reSearch = menu.add(Menu.NONE, MENU_RELOAD, 0, "重新获取");
		reSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_RELOAD:
			getWaterCode();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
