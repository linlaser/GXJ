package com.ywangwang.gxj;

import com.ywangwang.gxj.lib.DatabaseHelper;
import com.ywangwang.gxj.lib.ProtocolData;
import com.ywangwang.gxj.net.WaterCode;

public class GlobalInfo {
	public static final String HOME_URL = "http://www.ywangwang.com";

	public static final String BROADCAST_GXJ_MAIN_ACTIVITY_ACTION = "com.ywangwang.gxj.MainActivity.broadcast";
	public static final String BROADCAST_GXJ_BIND_DEVICE_ACTION = "com.ywangwang.gxj.BindDevice.broadcast";
	public static final String BROADCAST_GXJ_SERVER_GXJ_SERVER_ACTION = "com.ywangwang.gxjserver.GxjService.broadcast";
	public static final String BROADCAST_GXJ_SERVER_BROAD_RECEIVER_ACTION = "com.ywangwang.gxjserver.BroadReceiver.broadcast";
	public static final String BROADCAST_HAVE_NEW_GXJ_VERSION = "HAVE_NEW_VERSION";
	public static final String BROADCAST_UPDATE_GXJ = "UPDATE_GXJ";
	public static final String BROADCAST_UPDATE_GXJ_CHECK_NOW = "UPDATE_GXJ_CHECK_NOW";
	public static final String BROADCAST_UPDATE_GXJ_SERVER_CHECK_NOW = "UPDATE_GXJ_SERVER_CHECK_NOW";
	public static final String BROADCAST_UPDATE_SYSTEM_TIME = "UPDATE_SYSTEM_TIME";
	public static final String BROADCAST_SET_SYSTEM_TIME = "SET_SYSTEM_TIME";
	public static final String BROADCAST_REBOOT_SYSTEM = "REBOOT_SYSTEM";
	public static final String BROADCAST_GXJ_KEY = "UART_GXJ";
	public static final String BROADCAST_JSQ_KEY = "UART_JSQ";
	public static final String BROADCAST_UPDATE_RDOBTN_CUSTOM = "UPDATE_RDOBTN_CUSTOM";
	public static final String BROADCAST_UPDATE_CUSTOM_WATER_AMOUNT = "UPDATE_CUSTOM_WATER_AMOUNT";
	public static final String BROADCAST_SEARCH_DEVICE = "SEARCH_DEVICE";
	public static final String BROADCAST_BIND_DEVICE = "BIND_DEVICE";
	public static final String BROADCAST_STOP_SEARCH_DEVICE = "STOP_SEARCH_DEVICE";
	public static final String BROADCAST_NEW_WIRELESS_DATA_SEARCHING = "NEW_WIRELESS_DATA_SEARCHING";
	public static final String BROADCAST_NEW_WIRELESS_DATA_BINDING = "NEW_WIRELESS_DATA_BINDING";
	public static final String BROADCAST_WRITE_GXJ_DETAILS = "WRITE_GXJ_DETAILS";
	public static final String BROADCAST_SHOW_ACTIVITY = "SHOW_ACTIVITY";
	public static final String BROADCAST_RECEIVE_NEW_MESSAGE = "RECEIVE_NEW_MESSAGE";
	public static final String BROADCAST_CONNECT_SOCKET_SUCCESS = "CONNECT_SOCKET_SUCCESS";
	public static final String BROADCAST_SOCKET_LOGOUT = "SOCKET_LOGOUT";

	public static final String DB_FILE_NAME_JSQ = "jsq.db";
	public static final String DB_TABLE_NAME_JSQ = "jsq_tb";
	public static final String DB_ID = "_id";
	public static final String DB_AVERAGE_TDS_IN = "averageTDSIn";
	public static final String DB_AVERAGE_TDS_OUT = "averageTDSOut";
	public static final String DB_TOTAL_WATER_IN = "totalWaterIn";
	public static final String DB_TOTAL_WATER_OUT = "totalWaterOut";
	public static final String DB_TOTAL_FILTER_WATER_TIMES = "totalFilterWaterTimes";
	public static final String DB_TIME = "time";

	public static final String DB_FILE_NAME_GXJ = "gxj.db";
	public static final String DB_TABLE_NAME_GXJ = "gxj_tb";
	public static final String DB_AVERAGE_TDS = "averageTDS";
	public static final String DB_WATER_AMOUNT = "waterAmount";
	public static final String DB_TEMPERATURE = "temperature";

	public static final String BUNDLE_GXJ_DETAILS = "GXJ_DETAILS";

	public static final String S_P_NAME_CONFIG = "config";
	public static final String S_P_NAME_USER_INFO = "user_info";
	public static final String S_P_NAME_WATER_INFO = "water_info";

	public static final int HANDLER_DELAY_SEND_DATA = 1;
	public static final int HANDLER_DELAY_BROADCAST_BIND_DEVICE = 2;

	public static final int MIN_WATER_TEMPRATURE = 40;// ���ˮ��
	public static final int MAX_WATER_TEMPRATURE = 100;// ���ˮ��
	public static final int MIN_WATER_AMOUNT = 50;// ����ˮ��
	public static final int MAX_WATER_AMOUNT = 2500;// ���ˮ��

	public static final int OUT_WATER_COUNT_DOWN_TIME = 120;// ��ˮ����ʱ�趨����λ��

	public static final String S_P_KEY_DEBUG = "debug";
	public static final String S_P_KEY_DEBUG_TIMES = "debugTimes";
	public static boolean debug = false; // DEBUGģʽ���
	public static int debugTimes = 0; // ʣ��DEBUG����
	public static boolean testMode = false; // ���ģʽģʽ���

	public static int hostAdd = 0; // ����Si4463���ߵ�ַ
	public static long hostID = 0L; // ����ID

	public static int loginKey = 0;

	public static final int startADTime = 3000; // �������ʱ��
	public static boolean enableAD = false; // ���ù��
	public static int adVolumeValue = 100; // �������0~100
	public static ProtocolData gxjStatus = new ProtocolData();
	public static ProtocolData wirelessData = new ProtocolData();
	public static ProtocolData jsqStatus = new ProtocolData();

	public static int setTemperature = 0; // �趨�¶�0��40~100
	public static int setWaterAmount = 0; // �趨��ˮ��10~2500mL
	public static String setMode = "��ѡ��"; // �趨��ˮģʽ

	public static final int COOL_WATER = 10; // ��ˮ
	public static final int RoomTemperatureValue = 25; // �����¶�ֵ
	public static final int MilkTemperatureValue = 45; // �����¶�ֵ
	public static final int HoneyTemperatureValue = 55; // �����¶�ֵ
	public static final int BoilingTemperatureValue = 100; // ��ˮ�¶�ֵ
	public static final int WaterAmount150Value = 150; // �趨��ˮ��150
	public static final int WaterAmount260Value = 260; // �趨��ˮ��260
	public static final int WaterAmount300Value = 300; // �趨��ˮ��300
	public static boolean selectCoolWater = false;// ѡ���ˮ
	public static boolean selectRoomTemperatureWater = false;// ѡ����ˮ
	public static boolean enableCoolWater = false;// ���ñ�ˮ

	public static int CustomWaterAmountValue = 200; // �Զ����ˮ��ֵ

	public static int Custom1TemperatureValue = 70; // �Զ���1�¶�ֵ
	public static int Custom1WaterAmountValue = 100; // �Զ���1��ˮ��ֵ
	public static String Custom1Name = "�Զ���1"; // �Զ���1����
	public static int Custom2TemperatureValue = 80; // �Զ���2�¶�ֵ
	public static int Custom2WaterAmountValue = 200; // �Զ���2��ˮ��ֵ
	public static String Custom2Name = "�Զ���2"; // �Զ���2����

	public static SubDevice boundJSQ = new SubDevice(); // �󶨵ľ�ˮ��
	public static boolean enableChildLock = true; // ����ͯ�����ܱ��
	public static boolean enableWebViewHardwareAccelerated = false; // �Ƿ�����WebViewӲ������

	public static final String S_P_KEY_USERNAME = "username";
	public static final String S_P_KEY_PASSWORD = "password";
	public static String username = "";// �û���
	public static String password = "";// �û�����
	public static Boolean Logined = false;// ��¼״̬
	public static Boolean savePassword = true;// ��������
	public static Boolean autoLogin = true;// �Զ���¼

	public static final String S_P_KEY_SERVER_ADDRESS = "serverAddress";
	public static String serverAddress = "192.168.0.123";// ��������ַ

	public static Boolean online = false;// �Ƿ����ӵ�������

	public static WaterCode boundWaterCode = new WaterCode();// �󶨵�ȡˮ��
	public static Boolean isBoundWaterCode = false;// �Ƿ�󶨵�ȡˮ����
	public static final String KEY_IS_BOUND_WATER_CODE = "isBoundWaterCode";
	public static final String KEY_NUMBER = "boundWaterCode_number";
	public static final String KEY_TYPE = "boundWaterCode_type";
	public static final String KEY_STATUS = "boundWaterCode_status";
	public static final String KEY_BOUND_DEVICE_ID = "boundWaterCode_boundDeviceID";
	public static final String KEY_PERIOD_VALIDITY = "boundWaterCode_periodValidity";
	public static final String KEY_ACTIVATION_TIME = "boundWaterCode_activationTime";

	// public static final int FILTER_RECOMMEND_DAYS_PP = 90; // PP���Ƽ�ʹ������
	// public static final int FILTER_RECOMMEND_DAYS_FC = 90; // ǰ�ÿ�������̿(front_carbon)�Ƽ�ʹ������
	// public static final int FILTER_RECOMMEND_DAYS_RO = 720; // RO����͸�Ƽ�ʹ������
	// public static final int FILTER_RECOMMEND_DAYS_BC = 90; // ���ÿ�������̿(behind_carbon)�Ƽ�ʹ������
	// public static long filterInstallTimePP = 0L; // PP�ް�װʱ��
	// public static long filterInstallTimeFC = 0L; // ǰ�ÿ�������̿(front_carbon)��װʱ��
	// public static long filterInstallTimeRO = 0L; // RO����͸��װʱ��
	// public static long filterInstallTimeBC = 0L; // ���ÿ�������̿(behind_carbon)��װʱ��
	public static final int[] FILTER_RECOMMEND_DAYS = { 90, 90, 720, 90 }; // ��оʹ������
	public static long[] filterInstallTime = { 0L, 0L, 0L, 0L }; // ��о��װʱ��

	public static JsqDataStatistics todayJsqDataStatistics = new JsqDataStatistics();
	public static DatabaseHelper databaseHelperJSQ;
	public static DatabaseHelper databaseHelperGXJ;

	public static long timeBackup = 0L;// ����ʱ��

	public void reset() {
		todayJsqDataStatistics.clear();
	}

	public static class SubDevice {
		public boolean used = false;
		public int add = 0;
		public int deviceType = 0;
		public int itemId = 0;

		public String getDeviceType() {
			switch (this.deviceType) {
			case 0:
				return "����";
			case 1:
				return "̨��ʽ��ˮ��";
			case 2:
				return "̨��ʽ��ˮ��";
			case 3:
				return "���߻�";
			default:
				break;
			}
			return "����";
		}
	}

	public static class GxjOutWaterDetails {
		public int averageTDS = 0;// ƽ��TDS
		public int waterAmount = 0; // ��ˮ����λ���� mL
		public int temperature = 0;// �趨�¶ȣ�10=��ˮ��25=����ˮ
		public long time = 0L; // ��ˮʱ��

		public void clear() {
			this.averageTDS = 0;
			this.waterAmount = 0;
			this.temperature = 0;
			this.time = 0L;
		}
	}

	public static class JsqDataStatistics {
		public int averageTDSIn = 0; // ��ˮƽ��TDS
		public int averageTDSOut = 0; // �ճ�ˮƽ��TDS
		public float totalWaterIn = 0f; // ��ˮ������λ�� L
		public float totalWaterOut = 0f; // ��ˮ������λ�� L
		public int totalFilterWaterTimes = 0; // ����ˮ����
		public long time = 0L; // ����ˮ����

		public void clear() {
			this.averageTDSIn = 0;
			this.averageTDSOut = 0;
			this.totalWaterIn = 0f;
			this.totalWaterOut = 0f;
			this.totalFilterWaterTimes = 0;
			this.time = 0L;
		}

		// public void addData(JsqDataStatistics newData) {
		// this.averageTDSIn += newData.averageTDSIn;
		// this.averageTDSOut += newData.averageTDSOut;
		// this.totalWaterIn += newData.totalWaterIn;
		// this.totalWaterOut += newData.totalWaterOut;
		// this.totalFilterWaterTimes += newData.totalFilterWaterTimes;
		// }
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
