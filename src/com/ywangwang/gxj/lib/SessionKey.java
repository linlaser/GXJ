package com.ywangwang.gxj.lib;

import java.util.Random;

public class SessionKey {
	private int sessionKey = 0;

	public SessionKey() {
	}

	public void cleanSessionKey() {
		sessionKey = 0;
	}

	public int getSessionKey() {
		return sessionKey;
	}

	public int generateNewSessionKey() {
		return generateNewSessionKey(10000);
	}

	public int generateNewSessionKey(int scope) {
		sessionKey = new Random().nextInt(scope) + 1;
		return sessionKey;
	}
}
