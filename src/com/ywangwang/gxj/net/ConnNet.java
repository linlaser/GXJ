package com.ywangwang.gxj.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.client.methods.HttpPost;

public class ConnNet {

	// ��·������Ϊһ���������޸ĵ�ʱ��Ҳ�ø���
	// ͨ��url��ȡ�������� connection
	public HttpURLConnection getConn(String uriPath) {
		String finalurl = uriPath;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(finalurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true); // ����������
			connection.setDoOutput(true); // ���������
			connection.setUseCaches(false); // ������ʹ�û���
			connection.setRequestMethod("POST"); // ����ʽ
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return connection;
	}

	public HttpPost gethttpPost(String uriPath) {
		HttpPost httpPost = new HttpPost(uriPath);

		System.out.println(uriPath);
		return httpPost;
	}

}
