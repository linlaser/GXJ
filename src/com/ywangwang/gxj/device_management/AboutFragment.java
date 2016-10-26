package com.ywangwang.gxj.device_management;

import com.ywangwang.gxj.GlobalInfo;
import com.ywangwang.gxj.R;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AboutFragment extends Fragment {
	private final String TAG = "AboutFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, TAG + "-->>onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, TAG + "-->>onCreateView");
		View view = inflater.inflate(R.layout.device_management_about, container, false);

		try {
			((TextView) view.findViewById(R.id.tvAppInfo)).setText("���ڹ��߻���" + getAppInfo());
			((TextView) view.findViewById(R.id.tvAppInfo)).append("\n����ID=" + GlobalInfo.hostID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
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
			case R.id.chkBoxChildLock:
				break;
			default:
				break;
			}
		}
	};

	/*
	 * ��ȡ��ǰ����İ汾��
	 */
	private String getAppInfo() throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = getActivity().getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
		String label = (String) packageManager.getApplicationLabel(getActivity().getApplicationInfo());
		String versionName = packInfo.versionName;
		String versionCode = packInfo.versionCode + "";
		String packageName = getActivity().getPackageName();

		return "App���ƣ�" + label + "��versionName=" + versionName + "��versionCode=" + versionCode + "��packageName=" + packageName;
	}
}
