package com.ywangwang.gxj.lib;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	static final String algorithmStr = "AES/ECB/PKCS5Padding";

	private static final Object TAG = "AES";

	static private KeyGenerator keyGen;

	static private Cipher cipher;

	static boolean isInited = false;

	private static void init() {
		try {
			/**
			 * Ϊָ���㷨����һ�� KeyGenerator ���� �����ṩ���Գƣ���Կ�������Ĺ��ܡ� ��Կ��������ʹ�ô����ĳ�� getInstance �෽������ġ� KeyGenerator ������ظ�ʹ�ã�Ҳ����˵����������Կ�� �����ظ�ʹ��ͬһ KeyGenerator ���������ɽ�һ������Կ�� ������Կ�ķ�ʽ�����֣����㷨�޹صķ�ʽ���Լ��ض����㷨�ķ�ʽ�� ����֮���Ωһ��ͬ�Ƕ���ĳ�ʼ���� ���㷨�޹صĳ�ʼ�� ������Կ��������������Կ���� �����Դ �ĸ�� �� KeyGenerator ������һ�� init ���������ɲ���������ͨ�ø���Ĳ����� ����һ��ֻ�� keysize ������ init ������ ��ʹ�þ���������ȼ����ṩ����� SecureRandom ʵ����Ϊ���Դ �������װ���ṩ���򶼲��ṩ SecureRandom ʵ�֣���ʹ��ϵͳ�ṩ�����Դ���� �� KeyGenerator �໹�ṩһ��ֻ�����Դ������ inti ������ ��Ϊ�����������㷨�޹ص� init ����ʱδָ������������ �������ṩ���������δ�����ÿ����Կ��ص��ض����㷨�Ĳ���������У��� �ض����㷨�ĳ�ʼ�� ���Ѿ������ض����㷨�Ĳ�����������£� ���������� AlgorithmParameterSpec ������ init ������ ����һ����������һ�� SecureRandom ������ ����һ���������Ѱ�װ�ĸ����ȼ��ṩ����� SecureRandom ʵ���������Դ ��������Ϊϵͳ�ṩ�����Դ�������װ���ṩ���򶼲��ṩ SecureRandom ʵ�֣��� ����ͻ���û����ʽ�س�ʼ�� KeyGenerator��ͨ������ init �������� ÿ���ṩ��������ṩ���ͼ�¼��Ĭ�ϳ�ʼ����
			 */
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// ��ʼ������Կ��������ʹ�����ȷ������Կ���ȡ�
		keyGen.init(128); // 128λ��AES����
		try {
			// ����һ��ʵ��ָ��ת���� Cipher ����
			cipher = Cipher.getInstance(algorithmStr);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		// ��ʶ�Ѿ���ʼ�����˵��ֶ�
		isInited = true;
	}

	private static byte[] genKey() {
		if (!isInited) {
			init();
		}
		// ���� ����һ����Կ(SecretKey),
		// Ȼ��,ͨ�������Կ,���ػ��������ʽ����Կ���������Կ��֧�ֱ��룬�򷵻� null��
		return keyGen.generateKey().getEncoded();
	}

	private static byte[] encrypt(byte[] content, byte[] keyBytes) {
		byte[] encryptedText = null;
		if (!isInited) {
			init();
		}
		/**
		 * �� SecretKeySpec ����ʹ�ô���������һ���ֽ����鹹��һ�� SecretKey�� ������ͨ��һ�������� provider �ģ�SecretKeyFactory�� ��������ܱ�ʾΪһ���ֽ����鲢��û���κ���֮�������Կ������ԭʼ��Կ���� ���췽�����ݸ������ֽ����鹹��һ����Կ�� �˹��췽�������������ֽ������Ƿ�ָ����һ���㷨����Կ��
		 */
		Key key = new SecretKeySpec(keyBytes, "AES");
		try {
			// ����Կ��ʼ���� cipher��
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		try {
			// �������ֲ������ܻ�������ݣ����߽���һ���ಿ�ֲ�����(��֪��������˼)
			encryptedText = cipher.doFinal(content);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return encryptedText;
	}

	private static byte[] encrypt(String content, String password) {
		try {
			byte[] keyStr = getKey(password);
			SecretKeySpec key = new SecretKeySpec(keyStr, "AES");
			Cipher cipher = Cipher.getInstance(algorithmStr);// algorithmStr
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);//
			byte[] result = cipher.doFinal(byteContent);
			return result; //
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] decrypt(byte[] content, String password) {
		try {
			byte[] keyStr = getKey(password);
			SecretKeySpec key = new SecretKeySpec(keyStr, "AES");
			Cipher cipher = Cipher.getInstance(algorithmStr);// algorithmStr
			cipher.init(Cipher.DECRYPT_MODE, key);//
			byte[] result = cipher.doFinal(content);
			return result; //
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getKey(String password) {
		byte[] rByte = null;
		if (password != null) {
			rByte = password.getBytes();
		} else {
			rByte = new byte[24];
		}
		return rByte;
	}

	/**
	 * ��������ת����16����
	 * 
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * ��16����ת��Ϊ������
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * ����
	 */
	public static String encode(String string) {
		// ����֮����ֽ�����,ת��16���Ƶ��ַ�����ʽ���
		return parseByte2HexStr(encrypt(string, keyBytes));
	}

	/**
	 * ����
	 */
	public static String decode(String string) {
		// ����֮ǰ,�Ƚ�������ַ�������16����ת�ɶ����Ƶ��ֽ�����,��Ϊ�����ܵ���������
		byte[] b = decrypt(parseHexStr2Byte(string), keyBytes);
		return new String(b);
	}

	// ע��: �����password(��Կ������16λ��)
	private static final String keyBytes = "JM.ywangwang.COM";

	// ��������
	// public static void test1() {
	// String string = "hello abcdefggsdfasdfasdf";
	// String pStr = encode(string);
	// System.out.println("����ǰ��" + string);
	// System.out.println("���ܺ�:" + pStr);
	//
	// String postStr = decode(pStr);
	// System.out.println("���ܺ�" + postStr);
	// }

	// public static void main(String[] args) {
	// test1();
	// }
}