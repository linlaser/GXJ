package com.ywangwang.gxj.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast2 {
	private Context context;
	private Toast toast = null;
	private int imageViewID = 0;
	private int textViewID = 0;
	private int text2ViewID = 0;

	private int imageResID = 0;
	private String text = "";
	private float textSize = 30f;
	private int textColor = Color.WHITE;
	private String text2 = "";
	private float textSize2 = 30f;
	private int textColor2 = Color.WHITE;
	private int padding = 15;

	private int duration = Toast.LENGTH_SHORT;

	private int gravity = Gravity.CENTER, xOffset = 0, yOffset = 0;

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public void setDuration(int duration) {
		if (toast == null) {
			this.duration = duration;
		} else {
			toast.setDuration(duration);
		}
	}

	public void setGravity(int gravity, int xOffset, int yOffset) {
		if (toast == null) {
			this.gravity = gravity;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		} else {
			toast.setGravity(gravity, xOffset, yOffset);
		}
	}

	public CustomToast2(Context context) {
		this.context = context;
	}

	public CustomToast2 setTextOnDefault(String text) {
		return this.setText(text, 30f, Color.WHITE, null, 10f, Color.WHITE);
	}

	public CustomToast2 setText(String text) {
		return this.setText(text, textSize, textColor, null, 10f, Color.WHITE);
	}

	public CustomToast2 setText(String text, float size) {
		return this.setText(text, size, textColor, null, 10f, Color.WHITE);
	}

	public CustomToast2 setText(String text, int color) {
		return this.setText(text, textSize, color, null, 10f, Color.WHITE);
	}

	public CustomToast2 setTextOnDefault(String text, String text2) {
		return this.setText(text, 30f, Color.WHITE, text2, 10f, Color.WHITE);
	}

	public CustomToast2 setText(String text, String text2) {
		return this.setText(text, textSize, textColor, text2, textSize2, textColor2);
	}

	public CustomToast2 setText(String text, float size, String text2, float size2) {
		return this.setText(text, size, textColor, null, size2, textColor2);
	}

	public CustomToast2 setText(String text, int color, String text2, int color2) {
		return this.setText(text, textSize, color, null, textSize2, color2);
	}

	public CustomToast2 setText(String text, float size, int color, String text2, float size2, int color2) {
		imageResID = 0;
		if (text == null) {
			this.text = "";
		} else {
			this.text = text;
		}
		textSize = size;
		textColor = color;
		if (text2 == null) {
			this.text2 = "";
		} else {
			this.text2 = text2;
		}
		textSize2 = size2;
		textColor2 = color2;
		return this;
	}

	public void setImage(int resId) {
		text = "";
		imageResID = resId;
	}

	public CustomToast2 setImageTextC(int resId, String text) {
		return this.setImageText(resId, text, 30f, Color.WHITE);
	}

	public CustomToast2 setImageText(int resId, String text) {
		return this.setImageText(resId, text, textSize, textColor);
	}

	public CustomToast2 setImageText(int resId, String text, float size) {
		return this.setImageText(resId, text, size, textColor);
	}

	public CustomToast2 setImageText(int resId, String text, int color) {
		return this.setImageText(resId, text, textSize, color);
	}

	public CustomToast2 setImageText(int resId, String text, float size, int color) {
		this.setText(text, size, color, null, 10f, Color.WHITE);
		imageResID = resId;
		return this;
	}

	@SuppressLint("NewApi")
	public void show() {
		if (toast == null) {
			toast = new Toast(context);

			LinearLayout toastView = new LinearLayout(context);
			toastView.setOrientation(LinearLayout.VERTICAL);
			Drawable background = new ColorDrawable(Color.DKGRAY);
			background.setAlpha(200);
			toastView.setBackground(background);
			toastView.setPadding(padding, padding, padding, padding);

			ImageView imageView = new ImageView(context);
			imageView.setImageResource(imageResID);
			imageView.setId(View.generateViewId());
			imageViewID = imageView.getId();
			toastView.addView(imageView);
			if (imageResID == 0) {
				imageView.setVisibility(View.GONE);
			}

			TextView textView = new TextView(context);
			textView.setText(text);
			textView.setTextSize(textSize);
			textView.setTextColor(textColor);
			textView.setGravity(Gravity.CENTER);
			textView.setId(View.generateViewId());
			textViewID = textView.getId();
			toastView.addView(textView);
			if (text.equals("")) {
				textView.setVisibility(View.GONE);
			}

			toast.setView(toastView);
			toast.setDuration(duration);
			toast.setGravity(gravity, xOffset, yOffset);

		} else {
			LinearLayout toastView = (LinearLayout) toast.getView();
			toastView.setVisibility(View.VISIBLE);
			if (imageResID > 0) {
				ImageView iv = (ImageView) toastView.findViewById(imageViewID);
				iv.setImageResource(imageResID);
				iv.setVisibility(View.VISIBLE);
			} else {
				toastView.findViewById(imageViewID).setVisibility(View.GONE);
			}
			if (text.equals("") == false) {
				TextView tv = (TextView) toastView.findViewById(textViewID);
				tv.setText(text);
				tv.setTextSize(textSize);
				tv.setTextColor(textColor);
				tv.setVisibility(View.VISIBLE);
			} else {
				toastView.findViewById(textViewID).setVisibility(View.GONE);
			}
			toast.setDuration(duration);
			toast.setGravity(gravity, xOffset, yOffset);
		}
		toast.show();
	}

	public void hide() {
		if (toast != null) {
			toast.getView().setVisibility(View.GONE);
			toast.show();
		}
	}
}