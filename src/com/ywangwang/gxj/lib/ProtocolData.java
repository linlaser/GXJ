package com.ywangwang.gxj.lib;

public class ProtocolData {
	public static final int PROTOCOL_VER = 4; // 协议版本
	public static final int WIRELESS_SYNC_WORD = 0xFD; // 无线数据同步字
	public static final int CONTROL_BOARD_SYNC_WORD = 0xFE;// 控制板同步字
	public static final int PROTOCOL_DATA_LEAST_RECEIVE = 11;// 最少接收字节数
	public static final int PROTOCOL_DATA_MOST_RECEIVE = 63;// 最多接收字节数

	// public static final int GXJ_STOP = 1; // 停止
	// public static final int GXJ_HEAT_WATER = 6; // 热水
	// public static final int GXJ_ROOM_TEM_WATER = 7; // 常温水
	// public static final int GXJ_COOL_WATER = 8; // 冰水

	public static final int STANDBY = 1; // 停止/待机
	public static final int FILTER_WATER = 2; // 制水
	public static final int RINSING = 3; // 冲洗
	public static final int WATER_SHORTAGE = 4; // 缺水
	public static final int OVER_TEMPERATURE = 5; // 进水水温过高
	public static final int HEAT_WATER = 6; // 热水
	public static final int ROOM_TEMPERATURE_WATER = 7; // 常温水
	public static final int COOL_WATER = 8; // 冰水
	public static final int HEATER_OVER_TEMPERATURE = 9; // 加热系统温度过高
	public static final int IN_WATER_LOW_TEMPERATURE = 10; // 进水水温过低

	public static final int ACK = 100; // 应答
	public static final int ACTIVATION = 101; // 激活
	public static final int NOT_ACTIVATED = 102; // 未激活
	public static final int GET_STATUS = 103; // 获取状态
	public static final int SEARCHING = 104; // 搜寻
	public static final int BINDING = 105; // 绑定
	public static final int STATISTICS = 106; // 获取统计数据
	public static final int STATISTICS_GET = 0; // 获取统计数据
	public static final int STATISTICS_CLEAR = 1; // 清除统计数据
	public static final int STATISTICS_CLEARED = STATISTICS;// 统计数据已清除

	public static final int GXJ_FAULT = 200; // 管线机故障
	public static final int JSQ_FAULT = 201; // 净水器故障

	public static final int HOST = 0;
	public static final int TOP_WATER_PURIFIER = 1;
	public static final int UNDER_WATER_PURIFIER = 2;
	public static final int GXJ = 3;

	public int syncWord;
	public int dataLength;
	public int firmwareVer;
	public int deviceType;
	public int hostAdd;
	public int subDeviceAdd;
	public int commandOrStatus;
	public int rinsingTimeLeft;
	public int inFlow;
	public int outFlow;
	public int inTDS;
	public int outTDS;
	public int inTemperature;
	public int outTemperature;
	public int waterCount;
	public int coolStatus;
	public int parameter;
	public int checkData;

	// 数据统计
	public long total_Pulses_In = 0; // 进水流量脉冲数统计
	public long total_Pulses_Out = 0; // 出水流量脉冲数统计
	public int total_Filter_Water_Times = 0; // 制水次数统计
	public int average_TDS_In = 0; // 进水TDS平均值
	public int average_TDS_Out = 0; // 出水TDS平均值
	public int flow_Sensor_Hz = 0;// 水流速传感器参数，传感器在1L/min（升/分）时，每秒输出脉冲数量

	public void clear() {
		this.syncWord = 0;
		this.dataLength = 0;
		this.firmwareVer = 0;
		this.deviceType = 0;
		this.hostAdd = 0;
		this.subDeviceAdd = 0;
		this.commandOrStatus = 0;
		this.rinsingTimeLeft = 0;
		this.inFlow = 0;
		this.outFlow = 0;
		this.inTDS = 0;
		this.outTDS = 0;
		this.inTemperature = 0;
		this.outTemperature = 0;
		this.waterCount = 0;
		this.coolStatus = 0;
		this.parameter = 0;
		this.checkData = 0;

		this.total_Pulses_In = 0; // 进水流量脉冲数统计
		this.total_Pulses_Out = 0; // 出水流量脉冲数统计
		this.total_Filter_Water_Times = 0; // 制水次数统计
		this.average_TDS_In = 0; // 进水TDS平均值
		this.average_TDS_Out = 0; // 出水TDS平均值
		this.flow_Sensor_Hz = 0;// 水流速传感器参数，传感器在1L/min（升/分）时，每秒输出脉冲数量
	}

	public void setData(byte[] buffer) {
		this.setData(buffer, buffer.length);
	}

	public void setData(byte[] buffer, int size) {
		// Log.i("size111111", "----------------------->");
		switch (0xFF & buffer[0]) {
		case WIRELESS_SYNC_WORD:
			if (size < 10 || size > 63)
				break;
			syncWord = 0xFF & buffer[0];
			dataLength = 0xFF & buffer[1];
			firmwareVer = 0xFF & buffer[2];
			deviceType = 0xFF & buffer[3];
			hostAdd = (0xFF & buffer[4]) * 0x100 + (0xFF & buffer[5]);
			subDeviceAdd = (0xFF & buffer[6]) * 0x100 + (0xFF & buffer[7]);
			commandOrStatus = 0xFF & buffer[8];
			if (commandOrStatus == FILTER_WATER) {
				rinsingTimeLeft = 0xFF & buffer[9];
				inFlow = (0xFF & buffer[10]) * 0x100 + (0xFF & buffer[11]);
				outFlow = (0xFF & buffer[12]) * 0x100 + (0xFF & buffer[13]);
				inTDS = (0xFF & buffer[14]) * 0x100 + (0xFF & buffer[15]);
				outTDS = (0xFF & buffer[16]) * 0x100 + (0xFF & buffer[17]);
				inTemperature = 0xFF & buffer[18];
				checkData = 0xFF & buffer[19];
			} else if (commandOrStatus < 100) {
				if (commandOrStatus == RINSING)
					rinsingTimeLeft = 0xFF & buffer[9];
				inFlow = 0;
				outFlow = 0;
				inTDS = 0;
				outTDS = 0;
				inTemperature = 0;
				checkData = 0xFF & buffer[19];
			} else {
				switch (buffer[8] & 0xFF) {
				case STATISTICS:
					total_Filter_Water_Times = 0xFF & buffer[9];
					flow_Sensor_Hz = 0xFF & buffer[10];
					average_TDS_In = (0xFF & buffer[11]) * 0x100 + (0xFF & buffer[12]);
					average_TDS_Out = (0xFF & buffer[13]) * 0x100 + (0xFF & buffer[14]);
					total_Pulses_In = (0xFF & buffer[15]) * 0x10000 + (0xFF & buffer[16]) * 0x100 + (0xFF & buffer[17]);
					total_Pulses_Out = (0xFF & buffer[18]) * 0x10000 + (0xFF & buffer[19]) * 0x100 + (0xFF & buffer[20]);
					checkData = 0xFF & buffer[21];
					break;
				default:
					parameter = 0xFF & buffer[9];
					checkData = 0xFF & buffer[10];
					break;
				}
			}
			break;
		case CONTROL_BOARD_SYNC_WORD:
			if (size < 19)
				break;
			syncWord = 0xFF & buffer[0];
			dataLength = 0xFF & buffer[1];
			firmwareVer = 0xFF & buffer[2];
			deviceType = 0xFF & buffer[3];
			commandOrStatus = 0xFF & buffer[4];
			rinsingTimeLeft = 0xFF & buffer[5];
			inFlow = (0xFF & buffer[6]) * 0x100 + (0xFF & buffer[7]);
			outFlow = (0xFF & buffer[8]) * 0x100 + (0xFF & buffer[9]);
			inTDS = (0xFF & buffer[10]) * 0x100 + (0xFF & buffer[11]);
			outTDS = (0xFF & buffer[12]) * 0x100 + (0xFF & buffer[13]);
			inTemperature = 0xFF & buffer[14];
			outTemperature = 0xFF & buffer[15];
			waterCount = 0xFF & buffer[16];
			coolStatus = 0xFF & buffer[17];
			checkData = 0xFF & buffer[18];
			break;
		default:
			break;
		}
	}

	public void sendData(byte[] buffer) {
	}

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

	public String getStatus() {
		switch (this.commandOrStatus) {
		case 0:
			return "初始化";
		case 1:
			return "待机";
		case 2:
			return "制水";
		case 3:
			return "冲洗";
		case 4:
			return "缺水";
		case 5:
			return "进水水温过高";
		case 6:
			return "出热水";
		case 7:
			return "出常温水";
		case 8:
			return "出冰水";
		case 9:
			return "加热系统温度过高";
		case 10:
			return "进水水温过低";
		case 200:
			return "系统故障";
		default:
			break;
		}
		return "其他";
	}

	public String getCoolStatus() {
		switch (this.coolStatus) {
		case 0:
			return "正在制冷";
		case 1:
			return "制冷完成";
		case 3:
			return "设备不支持制冷";
		default:
			break;
		}
		return "其他";
	}
}
