package com.ywangwang.gxj.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.ywangwang.gxj.GlobalInfo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @���� ʹ��socketʵ�ֳ�����
 * @��Ŀ���� App_Chat
 * @���� com.android.chat.utils
 * @���� TcpUtil
 * @author chenlin
 * @date 2012��6��26�� ����4:06:43
 * @version 1.0
 */
public class TcpManager {
	private static final String TAG = "TcpManager";

	private static final String BROADCAST_ACTION = GlobalInfo.BROADCAST_GXJ_MAIN_ACTIVITY_ACTION;
	private static final String BROADCAST_RECEIVE_NEW_MESSAGE = GlobalInfo.BROADCAST_RECEIVE_NEW_MESSAGE;
	private static final String BROADCAST_CONNECT_SOCKET_SUCCESS = GlobalInfo.BROADCAST_CONNECT_SOCKET_SUCCESS;

	public static final int RECEIVE_NEW_MESSAGE = 1;
	// private static String dstName = "www.ywangwang.com";
	private static String dstName = "192.168.0.123";
	private static int dstPort = 6600;
	private static Socket socket;

	private static BufferedReader input;
	private static BufferedWriter output;

	private static TcpManager instance;
	private static boolean isRun = true;

	private static Context context;

	private TcpManager() {
	}

	public static void setServerAddress(String address) {
		dstName = address;
	}

	public static TcpManager getInstance(Context context) {
		return getInstance(context, dstName);
	}

	public static TcpManager getInstance(Context context, String dstName) {
		if (instance == null) {
			TcpManager.context = context;
			TcpManager.dstName = dstName;
			synchronized (TcpManager.class) {
				if (instance == null) {
					instance = new TcpManager();
				}
			}
		}
		return instance;
	}

	public static boolean isConnect() {
		if (socket == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public static boolean connect() {
		System.out.println(TAG + "-->connect");
		if (socket == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Heartbeat.reset();
					try {
						String dstNameTemp = dstName;
						Socket socketTemp = new Socket(dstName, dstPort);
						synchronized (this) {
							if (socket == null && socketTemp != null && dstNameTemp.equals(dstName)) {
								socket = socketTemp;
							} else {
								if (socketTemp != null) {
									socketTemp.close();
								}
								return;
							}
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
						return;
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					if (socket != null) {
						try {
							Log.d(TAG, "socket=" + socket);
							input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
							output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
							context.sendBroadcast(new Intent(BROADCAST_ACTION).putExtra(BROADCAST_CONNECT_SOCKET_SUCCESS, true));
							String receiveMsg = null;
							isRun = true;
							while (isRun) {
								receiveMsg = input.readLine();
								if (receiveMsg != null && receiveMsg.length() > 2) {
									Log.d(TAG, "receiveMsg=" + receiveMsg);
									if (receiveMsg.trim().equals("ACK")) {
										Heartbeat.reset();
									} else {
										context.sendBroadcast(new Intent(BROADCAST_ACTION).putExtra(BROADCAST_RECEIVE_NEW_MESSAGE, receiveMsg));
									}
								}
							}
						} catch (NullPointerException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							close();
						}
					}
				}
			}).start();
		}
		return true;
	}

	/**
	 * ������Ϣ
	 * 
	 * @param content
	 */
	public synchronized static boolean sendMSG(String msg) {
		try {
			if (socket != null) {
				Log.d(TAG, "sendMsg=" + msg);
				output.write(msg);
				output.write(10);
				output.flush();
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
			return false;
		}
	}

	public static void reconnect() {
		close();
		connect();
	}

	/**
	 * �ر�����
	 */
	public synchronized static void close() {
		System.out.println(TAG + "-->close");
		isRun = false;
		try {
			if (output != null) {
				output.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			// �����������������ߣ���ͣinput�����̻߳Ῠ��������
			if (input != null) {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		input = null;
		output = null;
		socket = null;
	}
}