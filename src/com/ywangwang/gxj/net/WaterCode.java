package com.ywangwang.gxj.net;

import android.text.format.DateFormat;

//ȡˮ��
public class WaterCode {
	public static final int PERMANENT_CODE = 0;// ������
	public static final int TERM_CODE = 1;// ������

	public static final int NOT_ACTIVE = 0;// δ����
	public static final int ACTIVATED = 1;// �Ѽ���
	public static final int ABATE = 2;// ��ʧЧ

	public static final String JSON_KEY_NUMBER = "number";
	public static final String JSON_KEY_TYPE = "type";
	public static final String JSON_KEY_STATUS = "status";
	public static final String JSON_KEY_BOUND_DEVICE_ID = "boundDeviceID";
	public static final String JSON_KEY_PERIOD_VALIDITY = "periodValidity";
	public static final String JSON_KEY_ACTIVATION_TIME = "activationTime";

	private long number = 0L; // ȡˮ����
	private int type = 0; // ȡˮ�����ͣ�0=�����룬1=������
	private int status = 0; // ȡˮ��״̬��0=δ���1=�Ѽ��2=��ʧЧ
	private long boundDeviceID = 0L; // ���豸��ID
	private int periodValidity = 0; // ȡˮ����Ч��,��λ ��
	private long activationTime = 0L; // ����ʱ��

	public void clear() {
		this.number = 0L; // ȡˮ����
		this.type = 0; // ȡˮ�����ͣ�0=�����룬1=������
		this.status = 0; // ȡˮ��״̬��0=δ���1=�Ѽ��2=��ʧЧ
		this.boundDeviceID = 0L; // ���豸��ID
		this.periodValidity = 0; // ȡˮ����Ч��,��λ ��
		this.activationTime = 0L; // ����ʱ��
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
			return "������";
		} else {
			return "������";
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
			return "δ����";
		} else if (this.getStatus() == ACTIVATED) {
			return "������";
		} else {
			return "��ʧЧ";
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
			return "δ��";
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
			return "����";
		} else if (this.getStatus() == NOT_ACTIVE) {
			return this.getPeriodValidity() + "��";
		} else if (this.activationTime == 0L) {
			return this.getPeriodValidity() + "��";
		} else {
			long usedDays = Math.abs((System.currentTimeMillis() - this.activationTime) / (long) (24 * 60 * 60 * 1000));
			if (usedDays >= this.getPeriodValidity()) {
				return this.getPeriodValidity() + "��(��ʹ��" + this.getPeriodValidity() + "��)";
			} else {
				return this.getPeriodValidity() + "��(��ʹ��" + usedDays + "��)";
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
			return "δ����";
		} else {
			return (String) DateFormat.format("yyyy��MM��dd�� HH:mm:ss", this.activationTime);
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
