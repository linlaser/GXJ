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

	public static final int MIN_WATER_TEMPRATURE = 40;// 最低水温
	public static final int MAX_WATER_TEMPRATURE = 100;// 最高水温
	public static final int MIN_WATER_AMOUNT = 50;// 最少水量
	public static final int MAX_WATER_AMOUNT = 2500;// 最多水量

	public static final int OUT_WATER_COUNT_DOWN_TIME = 120;// 出水倒计时设定，单位秒

	public static final String S_P_KEY_DEBUG = "debug";
	public static final String S_P_KEY_DEBUG_TIMES = "debugTimes";
	public static boolean debug = false; // DEBUG模式标记
	public static int debugTimes = 0; // 剩余DEBUG次数
	public static boolean testMode = false; // 检测模式模式标记

	public static int hostAdd = 0; // 主机Si4463无线地址
	public static long hostID = 0L; // 主机ID

	public static int loginKey = 0;

	public static final int startADTime = 3000; // 广告启动时间
	public static boolean enableAD = false; // 启用广告
	public static int adVolumeValue = 100; // 广告音量0~100
	public static ProtocolData gxjStatus = new ProtocolData();
	public static ProtocolData wirelessData = new ProtocolData();
	public static ProtocolData jsqStatus = new ProtocolData();

	public static int setTemperature = 0; // 设定温度0，40~100
	public static int setWaterAmount = 0; // 设定出水量10~2500mL
	public static String setMode = "请选择"; // 设定出水模式

	public static final int COOL_WATER = 10; // 冰水
	public static final int RoomTemperatureValue = 25; // 室温温度值
	public static final int MilkTemperatureValue = 45; // 冲奶温度值
	public static final int HoneyTemperatureValue = 55; // 蜂蜜温度值
	public static final int BoilingTemperatureValue = 100; // 沸水温度值
	public static final int WaterAmount150Value = 150; // 设定出水量150
	public static final int WaterAmount260Value = 260; // 设定出水量260
	public static final int WaterAmount300Value = 300; // 设定出水量300
	public static boolean selectCoolWater = false;// 选择冰水
	public static boolean selectRoomTemperatureWater = false;// 选择常温水
	public static boolean enableCoolWater = false;// 启用冰水

	public static int CustomWaterAmountValue = 200; // 自定义出水量值

	public static int Custom1TemperatureValue = 70; // 自定义1温度值
	public static int Custom1WaterAmountValue = 100; // 自定义1出水量值
	public static String Custom1Name = "自定义1"; // 自定义1标题
	public static int Custom2TemperatureValue = 80; // 自定义2温度值
	public static int Custom2WaterAmountValue = 200; // 自定义2出水量值
	public static String Custom2Name = "自定义2"; // 自定义2标题

	public static SubDevice boundJSQ = new SubDevice(); // 绑定的净水器
	public static boolean enableChildLock = true; // 启用童锁功能标记
	public static boolean enableWebViewHardwareAccelerated = false; // 是否启用WebView硬件加速

	public static final String S_P_KEY_USERNAME = "username";
	public static final String S_P_KEY_PASSWORD = "password";
	public static String username = "";// 用户名
	public static String password = "";// 用户密码
	public static Boolean Logined = false;// 登录状态
	public static Boolean savePassword = true;// 保存密码
	public static Boolean autoLogin = true;// 自动登录

	public static final String S_P_KEY_SERVER_ADDRESS = "serverAddress";
	public static String serverAddress = "192.168.0.123";// 服务器地址

	public static Boolean online = false;// 是否连接到服务器

	public static WaterCode boundWaterCode = new WaterCode();// 绑定的取水码
	public static Boolean isBoundWaterCode = false;// 是否绑定的取水码标记
	public static final String KEY_IS_BOUND_WATER_CODE = "isBoundWaterCode";
	public static final String KEY_NUMBER = "boundWaterCode_number";
	public static final String KEY_TYPE = "boundWaterCode_type";
	public static final String KEY_STATUS = "boundWaterCode_status";
	public static final String KEY_BOUND_DEVICE_ID = "boundWaterCode_boundDeviceID";
	public static final String KEY_PERIOD_VALIDITY = "boundWaterCode_periodValidity";
	public static final String KEY_ACTIVATION_TIME = "boundWaterCode_activationTime";

	// public static final int FILTER_RECOMMEND_DAYS_PP = 90; // PP棉推荐使用天数
	// public static final int FILTER_RECOMMEND_DAYS_FC = 90; // 前置颗粒活性炭(front_carbon)推荐使用天数
	// public static final int FILTER_RECOMMEND_DAYS_RO = 720; // RO反渗透推荐使用天数
	// public static final int FILTER_RECOMMEND_DAYS_BC = 90; // 后置颗粒活性炭(behind_carbon)推荐使用天数
	// public static long filterInstallTimePP = 0L; // PP棉安装时间
	// public static long filterInstallTimeFC = 0L; // 前置颗粒活性炭(front_carbon)安装时间
	// public static long filterInstallTimeRO = 0L; // RO反渗透安装时间
	// public static long filterInstallTimeBC = 0L; // 后置颗粒活性炭(behind_carbon)安装时间
	public static final int[] FILTER_RECOMMEND_DAYS = { 90, 90, 720, 90 }; // 滤芯使用天数
	public static long[] filterInstallTime = { 0L, 0L, 0L, 0L }; // 滤芯安装时间

	public static JsqDataStatistics todayJsqDataStatistics = new JsqDataStatistics();
	public static DatabaseHelper databaseHelperJSQ;
	public static DatabaseHelper databaseHelperGXJ;

	public static long timeBackup = 0L;// 备用时间

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
				return "主机";
			case 1:
				return "台上式净水器";
			case 2:
				return "台下式净水器";
			case 3:
				return "管线机";
			default:
				break;
			}
			return "其他";
		}
	}

	public static class GxjOutWaterDetails {
		public int averageTDS = 0;// 平均TDS
		public int waterAmount = 0; // 出水量单位毫升 mL
		public int temperature = 0;// 设定温度，10=冰水，25=常温水
		public long time = 0L; // 出水时间

		public void clear() {
			this.averageTDS = 0;
			this.waterAmount = 0;
			this.temperature = 0;
			this.time = 0L;
		}
	}

	public static class JsqDataStatistics {
		public int averageTDSIn = 0; // 进水平均TDS
		public int averageTDSOut = 0; // 日出水平均TDS
		public float totalWaterIn = 0f; // 进水总量单位升 L
		public float totalWaterOut = 0f; // 出水总量单位升 L
		public int totalFilterWaterTimes = 0; // 过滤水次数
		public long time = 0L; // 过滤水次数

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
