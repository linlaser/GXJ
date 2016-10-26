package com.ywangwang.gxj.device_management;

import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.R;
import com.ywangwang.gxj.lib.CustomToast;
import com.ywangwang.gxj.lib.SessionKey;
import com.ywangwang.gxj.net.Operaton;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserInfoFragment extends Fragment {
	private static final String TAG = "UserInfoFragment";
	public static final int LAYOUT_LOGIN = 0;
	public static final int LAYOUT_REGISTER = 1;
	public static final int LAYOUT_USER_INFO = 2;

	private final int LOGIN_SUCCESS = 1;
	private final int LOGIN_FAIL = 2;
	private final int REGISTER_SUCCESS = 3;
	private final int REGISTER_FAIL = 4;

	private CustomToast toast;
	private boolean gotoWaterCodeFragment = false;
	SessionKey sessionKey = new SessionKey();

	private RelativeLayout layoutLogin, layoutRegister, layoutUserInfo;
	private EditText edtTxtUsernameLogin, edtTxtPasswordLogin, edtTxtUsernameRegister, edtTxtPasswordRegister;
	private CheckBox chkBoxSavePassword, chkBoxAutoLogin;
	private Button btnLogin, btnRegister;// , btnLogoff;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, TAG + "-->>onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, TAG + "-->>onCreateView");
		View view = inflater.inflate(R.layout.device_management_user_info, container, false);
		layoutLogin = (RelativeLayout) view.findViewById(R.id.includeLogin);
		layoutRegister = (RelativeLayout) view.findViewById(R.id.includeRegister);
		layoutUserInfo = (RelativeLayout) view.findViewById(R.id.includeUserInfo);

		chkBoxSavePassword = (CheckBox) layoutLogin.findViewById(R.id.chkBoxSavePassword);
		chkBoxAutoLogin = (CheckBox) layoutLogin.findViewById(R.id.chkBoxAutoLogin);
		chkBoxSavePassword.setChecked(GlobalInfo.savePassword);
		chkBoxAutoLogin.setChecked(GlobalInfo.autoLogin);
		chkBoxSavePassword.setOnClickListener(ButtonListener);
		chkBoxAutoLogin.setOnClickListener(ButtonListener);
		edtTxtUsernameLogin = (EditText) layoutLogin.findViewById(R.id.edtTxtUsername);
		edtTxtPasswordLogin = (EditText) layoutLogin.findViewById(R.id.edtTxtPassword);
		btnLogin = (Button) layoutLogin.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(ButtonListener);
		btnLogin.setText("登录");
		layoutLogin.findViewById(R.id.tvChangeToRegister).setOnClickListener(ButtonListener);

		edtTxtUsernameRegister = (EditText) layoutRegister.findViewById(R.id.edtTxtUsername);
		edtTxtPasswordRegister = (EditText) layoutRegister.findViewById(R.id.edtTxtPassword);
		layoutRegister.findViewById(R.id.tvChangeToLogin).setOnClickListener(ButtonListener);
		btnRegister = (Button) layoutRegister.findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(ButtonListener);
		btnRegister.setText("注册");

		layoutUserInfo.findViewById(R.id.btnLogout).setOnClickListener(ButtonListener);
		((TextView) layoutUserInfo.findViewById(R.id.tvInfo)).setText("【用户信息】");

		TextView tvChangeLogin = (TextView) layoutLogin.findViewById(R.id.tvChangeToRegister);
		tvChangeLogin.setText("注册新用户");
		tvChangeLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		tvChangeLogin.getPaint().setAntiAlias(true);// 抗锯齿
		TextView tvChangeRegister = (TextView) layoutRegister.findViewById(R.id.tvChangeToLogin);
		tvChangeRegister.setText("已注册用户点此登陆");
		tvChangeRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		tvChangeRegister.getPaint().setAntiAlias(true);// 抗锯齿

		int showLayout = -1;
		Bundle bundle = getArguments();
		if (bundle != null) {
			showLayout = bundle.getInt("show", LAYOUT_LOGIN);
			gotoWaterCodeFragment = bundle.getBoolean("getWaterCode", false);
		}
		if (showLayout == -1) {
			switchLayout(LAYOUT_USER_INFO);
		} else {
			switchLayout(showLayout);
		}
		toast = new CustomToast(getActivity());
		return view;
	}

	private void switchLayout(int layout) {
		switch (layout) {
		case LAYOUT_LOGIN:
			layoutLogin.setVisibility(View.VISIBLE);
			layoutRegister.setVisibility(View.GONE);
			layoutUserInfo.setVisibility(View.GONE);
			edtTxtUsernameLogin.setText(GlobalInfo.username);
			if (chkBoxSavePassword.isChecked() == true) {
				edtTxtPasswordLogin.setText(GlobalInfo.password);
			} else {
				edtTxtPasswordLogin.setText("");
			}
			break;
		case LAYOUT_REGISTER:
			layoutLogin.setVisibility(View.GONE);
			layoutRegister.setVisibility(View.VISIBLE);
			layoutUserInfo.setVisibility(View.GONE);
			break;
		case LAYOUT_USER_INFO:
			if (GlobalInfo.Logined == true) {
				layoutLogin.setVisibility(View.GONE);
				layoutRegister.setVisibility(View.GONE);
				layoutUserInfo.setVisibility(View.VISIBLE);
			} else {
				layoutLogin.setVisibility(View.VISIBLE);
				layoutRegister.setVisibility(View.GONE);
				layoutUserInfo.setVisibility(View.GONE);
				edtTxtUsernameLogin.setText(GlobalInfo.username);
				if (chkBoxSavePassword.isChecked() == true) {
					edtTxtPasswordLogin.setText(GlobalInfo.password);
				} else {
					edtTxtPasswordLogin.setText("");
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, TAG + "-->>onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, TAG + "-->>onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, TAG + "-->>onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i(TAG, TAG + "-->>onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(TAG, TAG + "-->>onDestroyView");
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		sessionKey.cleanSessionKey();
		super.onDestroy();
		Log.i(TAG, TAG + "-->>onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.i(TAG, TAG + "-->>onDetach");
	}

	OnClickListener ButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnLogin:
				final String username = edtTxtUsernameLogin.getText().toString().trim();
				final String password = edtTxtPasswordLogin.getText().toString().trim();
				if (username == null || username.length() <= 0) {
					edtTxtUsernameLogin.requestFocus();
					edtTxtUsernameLogin.setError("用户名不能为空！");
					return;
				}
				if (password == null || password.length() <= 0) {
					edtTxtPasswordLogin.requestFocus();
					edtTxtPasswordLogin.setError("密码不能为空！");
					return;
				}
				btnLogin.setText("正在登录...");
				btnLogin.setEnabled(false);
				new Thread() {
					@Override
					public void run() {
						Message msg = Message.obtain();
						msg.arg1 = sessionKey.generateNewSessionKey();
						Operaton operaton = new Operaton(getActivity());
						String result = operaton.login(username, password);
						Log.d(TAG, "login-->" + result);
						if (result.equals("1") == true) {
							msg.what = LOGIN_SUCCESS;
							String[] user = { username, password };
							msg.obj = user;
						} else {
							msg.what = LOGIN_FAIL;
							msg.obj = result;
						}
						handler.sendMessage(msg);
						super.run();
					}
				}.start();
				break;
			case R.id.btnRegister:
				final String usernameRegister = edtTxtUsernameRegister.getText().toString().trim();
				final String passwordRegister = edtTxtPasswordRegister.getText().toString().trim();
				if (usernameRegister == null || usernameRegister.length() <= 0) {
					edtTxtUsernameRegister.requestFocus();
					edtTxtUsernameRegister.setError("用户名不能为空！");
					return;
				}
				if (passwordRegister == null || passwordRegister.length() <= 0) {
					edtTxtPasswordRegister.requestFocus();
					edtTxtPasswordRegister.setError("密码不能为空！");
					return;
				}
				btnRegister.setText("正在注册...");
				btnRegister.setEnabled(false);
				new Thread() {
					@Override
					public void run() {
						Message msg = Message.obtain();
						msg.arg1 = sessionKey.generateNewSessionKey();
						Operaton operaton = new Operaton(getActivity());
						String result = operaton.register(usernameRegister, passwordRegister);
						Log.d(TAG, "register-->" + result);
						if (result.equals("1") == true) {
							msg.what = REGISTER_SUCCESS;
							String[] user = { usernameRegister, passwordRegister };
							msg.obj = user;
						} else {
							msg.what = REGISTER_FAIL;
							msg.obj = result;
						}
						handler.sendMessage(msg);
						super.run();
					}
				}.start();
				break;
			case R.id.tvChangeToRegister:
				switchLayout(LAYOUT_REGISTER);
				break;
			case R.id.tvChangeToLogin:
				switchLayout(LAYOUT_LOGIN);
				break;
			case R.id.btnLogout:
				GlobalInfo.Logined = false;
				if (chkBoxSavePassword.isChecked() == false) {
					GlobalInfo.password = "";
				}
				switchLayout(LAYOUT_LOGIN);
				getActivity().sendBroadcast(new Intent(GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION).putExtra(GlobalInfo.BROADCAST_SOCKET_LOGOUT, true));
				break;
			case R.id.chkBoxSavePassword:
				chkBoxAutoLogin.setChecked(false);
				break;
			case R.id.chkBoxAutoLogin:
				chkBoxSavePassword.setChecked(true);
				break;
			default:
				break;
			}
		}
	};
	@SuppressLint({ "HandlerLeak" })
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 如果会话KEY和最后一次不同，就忽略此msg
			if (sessionKey.getSessionKey() != msg.arg1) {
				return;
			}
			btnLogin.setText("登录");
			btnLogin.setEnabled(true);
			btnRegister.setText("注册");
			btnRegister.setEnabled(true);
			switch (msg.what) {
			case LOGIN_SUCCESS:
				toast.setText("登录成功！\nusername=" + ((String[]) msg.obj)[0] + "\npassword=" + ((String[]) msg.obj)[1]).show();
				loginOrRegisterSuccess((String[]) msg.obj);
				break;
			case LOGIN_FAIL:
				toast.setText("登陆失败！-->" + (String) msg.obj).show();
				break;
			case REGISTER_SUCCESS:
				toast.setText("注册成功！\nusername=" + ((String[]) msg.obj)[0] + "\npassword=" + ((String[]) msg.obj)[1]).show();
				loginOrRegisterSuccess((String[]) msg.obj);
				break;
			case REGISTER_FAIL:
				toast.setText("注册失败！-->" + (String) msg.obj).show();
				break;
			default:
				break;
			}
		}
	};

	private void loginOrRegisterSuccess(String[] usernameAndPassword) {
		GlobalInfo.username = usernameAndPassword[0];
		GlobalInfo.password = usernameAndPassword[1];
		GlobalInfo.Logined = true;
		GlobalInfo.savePassword = chkBoxSavePassword.isChecked();// 保存密码
		GlobalInfo.autoLogin = chkBoxAutoLogin.isChecked();// 自动登录

		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalInfo.S_P_NAME_USER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(GlobalInfo.S_P_KEY_USERNAME, GlobalInfo.username);
		if (chkBoxSavePassword.isChecked() == true) {
			editor.putString(GlobalInfo.S_P_KEY_PASSWORD, GlobalInfo.password);
		} else {
			editor.remove(GlobalInfo.S_P_KEY_PASSWORD);
		}
		editor.putBoolean("savePassword", GlobalInfo.savePassword);
		editor.putBoolean("autoLogin", GlobalInfo.autoLogin);
		editor.commit();
		if (gotoWaterCodeFragment == true) {
			WaterCodeFragment waterCodeFragment = new WaterCodeFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragmentContainer, waterCodeFragment);
			transaction.commit();
		} else {
			switchLayout(LAYOUT_USER_INFO);
		}
	}
}
