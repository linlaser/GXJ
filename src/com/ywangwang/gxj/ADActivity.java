package com.ywangwang.gxj;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.VideoView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ADActivity extends Activity {

	protected static final String TAG = "ScreenSaverActivity";
	private MediaPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);

		GlobalInfo.adVolumeValue = getVolume();

		// if (checkNetworkState()) {
		// playmusic();
		// doPushToService();
		// }
		// else{
		// 执行本地的的视频音频文件
		// }

		// ViewFlipper flipper = (ViewFlipper) findViewById(R.id.flipper);
		// flipper.setInAnimation(AnimationUtils.loadAnimation(this,
		// R.anim.push_left_in));
		// flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
		// R.anim.push_left_in));
		// flipper.setFlipInterval(5000);
		// flipper.startFlipping();

		final VideoView video1 = (VideoView) findViewById(R.id.videoView1);

		video1.setVideoURI(Uri.parse("android.resource://com.ywangwang.gxj/" + R.raw.ad));
		video1.start();
		video1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				player = mp;
				setVolume(GlobalInfo.adVolumeValue);
				// mp.start();
				// mp.setLooping(true);
			}
		});
		video1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				video1.setVideoPath("android.resource://com.ywangwang.gxj/" + R.raw.ad);
				video1.start();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@SuppressLint("InflateParams")
	public void ShowVolumeDialog(View view) {
		final View viewADVolume = LayoutInflater.from(this).inflate(R.layout.dialog_ad_volume, null);
		final SeekBar skbBar = (SeekBar) viewADVolume.findViewById(R.id.skBarVolume);
		final Builder builder = new AlertDialog.Builder(this);
		skbBar.setMax(100);
		skbBar.setProgress(GlobalInfo.adVolumeValue);
		skbBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setVolume(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		builder.setTitle("设定音量");
		builder.setView(viewADVolume);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				setVolume(GlobalInfo.adVolumeValue);
				saveVolume(GlobalInfo.adVolumeValue);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				GlobalInfo.adVolumeValue = getVolume();
				setVolume(GlobalInfo.adVolumeValue);
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				setVolume(GlobalInfo.adVolumeValue);
				saveVolume(GlobalInfo.adVolumeValue);
			}
		});
		builder.show();

	}

	private void setVolume(int volume) {
		try {
			player.setVolume((float) volume / 100, (float) volume / 100);
		} catch (Exception e) {
			// 视频切换的时候player可能没有实例，所以setVolume可能报错
		}
		GlobalInfo.adVolumeValue = volume;
	}

	private int getVolume() {
		SharedPreferences sharedPreferences = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sharedPreferences.getInt("adVolumeValue", 100);
	}

	private boolean saveVolume(int volume) {
		SharedPreferences sharedPreferences = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("adVolumeValue", volume);
		return editor.commit();
	}
}
