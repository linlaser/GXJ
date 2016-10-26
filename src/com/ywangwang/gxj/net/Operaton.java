package com.ywangwang.gxj.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class Operaton {
	// private static final String URL_DEBUG = "http://192.168.0.123:81/index.php/App/Jingshuiqi/";
	// private static final String URL = "http://www.ywangwang.com/index.php/App/Jingshuiqi/";

	private Context context;
	private static String url = "http://192.168.0.123:81/index.php/App/Jingshuiqi/";

	public Operaton(Context context) {
		this.context = context;
	}

	public static void setServerAddress(String address) {
		url = "http://" + address + "/index.php/App/Jingshuiqi/";
	}

	public String login(String username, String password) {
		return loginOrRegistera(url + "login", username, password);
	}

	public String register(String username, String password) {
		return loginOrRegistera(url + "register", username, password);
	}

	public String loginOrRegistera(String url, String username, String password) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		return sendPost(url, params);
	}

	public String updateWaterCode(String username, String password, String waterCodeNumber) {
		return waterCode(url + "updateWaterCode", username, password, waterCodeNumber, null);
	}

	public String bindWaterCode(String username, String password, String waterCodeNumber, String deviceId) {
		return waterCode(url + "bindWaterCode", username, password, waterCodeNumber, deviceId);
	}

	public String unbindWaterCode(String username, String password, String waterCodeNumber, String deviceId) {
		return waterCode(url + "unbindWaterCode", username, password, waterCodeNumber, deviceId);
	}

	public String loadWaterCode(String username, String password) {
		return waterCode(url + "getWaterCode", username, password, null, null);
	}

	public String waterCode(String url, String username, String password, String waterCodeNumber, String deviceId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		if (waterCodeNumber != null) {
			params.add(new BasicNameValuePair("waterCodeNumber", waterCodeNumber));
		}
		if (deviceId != null) {
			params.add(new BasicNameValuePair("deviceId", deviceId));
		}
		return sendPost(url, params);
	}

	public String sendPost(String url, List<NameValuePair> params) {
		String result = "�޷����ӵ�������";
		if (Net.isNetworkAvailable(context) == false) {
			return result;
		}
		ConnNet connNet = new ConnNet();
		try {
			HttpPost httpPost = connNet.gethttpPost(url);
			if (params != null) {
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				httpPost.setEntity(entity);
			}
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
				// result = EntityUtils.toString(httpResponse.getEntity(), "GBK");
			} else {
				result = "����ʧ��";
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return result;
	}

	public String checkusername(String url, String username) {
		String result = null;
		ConnNet connNet = new ConnNet();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		try {
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			HttpPost httpPost = connNet.gethttpPost(url);
			System.out.println(httpPost.toString());
			httpPost.setEntity(entity);
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
				System.out.println("resu" + result);
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return result;
	}

	public String UpData(String uripath, String jsonString) {
		String result = null;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		NameValuePair nvp = new BasicNameValuePair("jsonstring", jsonString);
		list.add(nvp);
		ConnNet connNet = new ConnNet();
		HttpPost httpPost = connNet.gethttpPost(uripath);
		try {
			HttpEntity entity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
			// �˾������Ϸ��򴫵��ͻ��˵����Ľ�������
			httpPost.setEntity(entity);
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
				System.out.println("resu" + result);
			} else {
				result = "ע��ʧ��";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String uploadFile(File file, String urlString) {
		final String TAG = "uploadFile";
		final int TIME_OUT = 10 * 1000; // ��ʱʱ��
		final String CHARSET = "utf-8"; // ���ñ���
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // �߽��ʶ �������
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // ��������

		try {
			ConnNet connNet = new ConnNet();
			HttpURLConnection conn = connNet.getConn(urlString);
			conn.setReadTimeout(TIME_OUT);
			// conn.setConnectTimeout(TIME_OUT);
			// conn.setDoInput(true); //����������
			// conn.setDoOutput(true); //���������
			// conn.setUseCaches(false); //������ʹ�û���
			// conn.setRequestMethod("POST"); //����ʽ
			conn.setRequestProperty("Charset", CHARSET); // ���ñ���
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			if (file != null) {
				/**
				 * ���ļ���Ϊ�գ����ļ���װ�����ϴ�
				 */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * �����ص�ע�⣺ name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ� filename���ļ������֣�������׺���� ����:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ����
				 */
				int res = conn.getResponseCode();
				Log.e(TAG, "response code:" + res);
				// if(res==200)
				// {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
				// }
				// else{
				// Log.e(TAG, "request error");
				// }
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
