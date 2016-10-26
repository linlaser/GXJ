package com.ywangwang.gxj.lib;

import java.util.Arrays;

import android.util.Log;

public class CheckData {
	private static final int bufferLength = 256;

	public static int receiveDataCount = 0;
	private int checkCount = 0;
	private int checkData = 0;

	public static byte[] receiveData = new byte[bufferLength];
	private byte[] tempData = new byte[bufferLength];
	public byte[] checkedData = null;

	public boolean addData(byte[] buffer, int size) {
		Log.i("NewData=", "Length=" + size + ",Data=" + Arrays.toString(buffer));
		if (size > bufferLength)
			size = bufferLength;
		if (size < 0)
			return false;
		if (receiveDataCount + size > bufferLength) {
			System.arraycopy(receiveData, (receiveDataCount + size - bufferLength), receiveData, 0, bufferLength - size);
			receiveDataCount = bufferLength - size;
		}
		System.arraycopy(buffer, 0, receiveData, receiveDataCount, size);
		receiveDataCount += size;
		checkCount = 0;
		for (int i = 0; i < receiveDataCount; i++) {
			if (((receiveData[i] & 0xFF) == ProtocolData.WIRELESS_SYNC_WORD || (receiveData[i] & 0xFF) == ProtocolData.CONTROL_BOARD_SYNC_WORD) && checkCount == 0) {
				tempData[checkCount++] = receiveData[i];
			} else if (checkCount > 0) {
				tempData[checkCount++] = receiveData[i];
				if ((tempData[1] & 0xFF) < ProtocolData.PROTOCOL_DATA_LEAST_RECEIVE || (tempData[1] & 0xFF) > ProtocolData.PROTOCOL_DATA_MOST_RECEIVE) {
					checkCount = 0;
				}
				if (checkCount == (tempData[1] & 0xFF) && checkCount != 0) {
					checkData = 0;
					for (int j = 1; j < ((tempData[1] & 0xFF) - 1); j++) {
						checkData += (tempData[j] & 0xFF);
					}
					if (checkData % 0x100 == (tempData[(tempData[1] & 0xFF) - 1] & 0xFF)) {
						checkedData = new byte[(tempData[1] & 0xFF)];
						System.arraycopy(tempData, 0, checkedData, 0, (tempData[1] & 0xFF));
						System.arraycopy(receiveData, i + 1, receiveData, 0, receiveDataCount - (i + 1));
						receiveDataCount = receiveDataCount - (i + 1);
						return true;
					} else {
						break;
					}
				}

			}
		}
		System.arraycopy(receiveData, receiveDataCount - checkCount, receiveData, 0, checkCount);
		receiveDataCount = checkCount;
		return false;
	}

}
