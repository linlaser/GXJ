package com.ywangwang.gxj.net;

import android.text.format.DateFormat;

//取水码
public class WaterCode {
	public static final int PERMANENT_CODE = 0;// 永久码
	public static final int TERM_CODE = 1;// 期限码

	public static final int NOT_ACTIVE = 0;// 未激活
	public static final int ACTIVATED = 1;// 已激活
	public static final int ABATE = 2;// 已失效

	public static final String JSON_KEY_NUMBER = "number";
	public static final String JSON_KEY_TYPE = "type";
	public static final String JSON_KEY_STATUS = "status";
	public static final String JSON_KEY_BOUND_DEVICE_ID = "boundDeviceID";
	public static final String JSON_KEY_PERIOD_VALIDITY = "periodValidity";
	public static final String JSON_KEY_ACTIVATION_TIME = "activationTime";

	private long number = 0L; // 取水码编号
	private int type = 0; // 取水码类型，0=永久码，1=期限码
	private int status = 0; // 取水码状态，0=未激活，1=已激活，2=已失效
	private long boundDeviceID = 0L; // 绑定设备的ID
	private int periodValidity = 0; // 取水码有效期,单位 天
	private long activationTime = 0L; // 激活时间

	public void clear() {
		this.number = 0L; // 取水码编号
		this.type = 0; // 取水码类型，0=永久码，1=期限码
		this.status = 0; // 取水码状态，0=未激活，1=已激活，2=已失效
		this.boundDeviceID = 0L; // 绑定设备的ID
		this.periodValidity = 0; // 取水码有效期,单位 天
		this.activationTime = 0L; // 激活时间
	}

	public String getNumberDescribe() {
		return String.format("%08X", number);
	}

	public long getNumber() {
		return this.number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getTypeDescribe() {
		if (this.type == PERMANENT_CODE) {
			return "永久码";
		} else {
			return "期限码";
		}
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStatusDescribe() {
		if (this.getStatus() == NOT_ACTIVE) {
			return "未激活";
		} else if (this.getStatus() == ACTIVATED) {
			return "已启用";
		} else {
			return "已失效";
		}
	}

	public int getStatus() {
		if (this.activationTime != 0L) {
			if (Math.abs((System.currentTimeMillis() - this.activationTime) / (long) (24 * 60 * 60 * 1000)) >= this.getPeriodValidity()) {
				this.status = ABATE;
			}
		}
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBoundDeviceIdDescribe() {
		if (this.boundDeviceID == 0L) {
			return "未绑定";
		} else {
			return String.format("%012X", this.boundDeviceID);
		}
	}

	public long getBoundDeviceId() {
		return this.boundDeviceID;
	}

	public void setBoundDeviceId(long boundDeviceID) {
		this.boundDeviceID = boundDeviceID;
	}

	public String getPeriodValidityDescribe() {
		if (this.type == PERMANENT_CODE) {
			return "永久";
		} else if (this.getStatus() == NOT_ACTIVE) {
			return this.getPeriodValidity() + "天";
		} else if (this.activationTime == 0L) {
			return this.getPeriodValidity() + "天";
		} else {
			long usedDays = Math.abs((System.currentTimeMillis() - this.activationTime) / (long) (24 * 60 * 60 * 1000));
			if (usedDays >= this.getPeriodValidity()) {
				return this.getPeriodValidity() + "天(已使用" + this.getPeriodValidity() + "天)";
			} else {
				return this.getPeriodValidity() + "天(已使用" + usedDays + "天)";
			}
		}
	}

	public int getPeriodValidity() {
		return this.periodValidity;
	}

	public void setPeriodValidity(int periodValidity) {
		this.periodValidity = periodValidity;
	}

	public String getActivationTimeDescribe() {
		if (getStatus() == NOT_ACTIVE) {
			return "未激活";
		} else {
			return (String) DateFormat.format("yyyy年MM月dd日 HH:mm:ss", this.activationTime);
		}
	}

	public long getActivationTime() {
		return this.activationTime;
	}

	public void setActivationTime(long activationTime) {
		this.activationTime = activationTime;
	}

	public int getValidDays() {
		if (getStatus() == ABATE) {
			return 0;
		} else if (type == PERMANENT_CODE) {
			return 10000;
		} else if (getStatus() == NOT_ACTIVE) {
			return periodValidity;
		} else {
			return (int) (periodValidity - (System.currentTimeMillis() - this.activationTime) / (long) (24 * 60 * 60 * 1000));
		}
	}
}
