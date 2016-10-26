package com.ywangwang.gxj.net;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class JsonTools {

	public JsonTools() {
		// TODO Auto-generated constructor stub
	}

	// public static Person getPerson(String key, String jsonString) {
	// Person person = new Person();
	// try {
	// JSONObject jsonObject = new JSONObject(jsonString);
	// JSONObject personObject = jsonObject.getJSONObject("person");
	// person.setId(personObject.getInt("id"));
	// person.setName(personObject.getString("name"));
	// person.setAddress(personObject.getString("address"));
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// return person;
	// }

	public static List<WaterCode> getWaterCodesFormJSON(String key, String jsonString) {
		List<WaterCode> list = new ArrayList<WaterCode>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = jsonArray.getJSONObject(i);
				if (jsonObject2.getLong(WaterCode.JSON_KEY_NUMBER) > 0) {
					WaterCode watercode = new WaterCode();
					watercode.setNumber(jsonObject2.getLong(WaterCode.JSON_KEY_NUMBER));
					watercode.setType(jsonObject2.getInt(WaterCode.JSON_KEY_TYPE));
					watercode.setStatus(jsonObject2.getInt(WaterCode.JSON_KEY_STATUS));
					watercode.setBoundDeviceId(jsonObject2.getLong(WaterCode.JSON_KEY_BOUND_DEVICE_ID));
					watercode.setPeriodValidity(jsonObject2.getInt(WaterCode.JSON_KEY_PERIOD_VALIDITY));
					watercode.setActivationTime(jsonObject2.getLong(WaterCode.JSON_KEY_ACTIVATION_TIME));
					list.add(watercode);
				}
			}
		} catch (Exception e) {
			Log.e("JSON", "JSON���ݽ���ʧ��");
		}
		return list;
	}

	// public static List<String> getList(String key, String jsonString) {
	// List<String> list = new ArrayList<String>();
	// try {
	// JSONObject jsonObject = new JSONObject(jsonString);
	// JSONArray jsonArray = jsonObject.getJSONArray(key);
	// for (int i = 0; i < jsonArray.length(); i++) {
	// String msg = jsonArray.getString(i);
	// list.add(msg);
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// return list;
	// }

	// public static List<Map<String, Object>> listKeyMaps(String key, String jsonString) {
	// List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	// try {
	// JSONObject jsonObject = new JSONObject(jsonString);
	// JSONArray jsonArray = jsonObject.getJSONArray(key);
	// for (int i = 0; i < jsonArray.length(); i++) {
	// JSONObject jsonObject2 = jsonArray.getJSONObject(i);
	// Map<String, Object> map = new HashMap<String, Object>();
	// Iterator<String> iterator = jsonObject2.keys();
	// while (iterator.hasNext()) {
	// String json_key = iterator.next();
	// Object json_value = jsonObject2.get(json_key);
	// if (json_value == null) {
	// json_value = "";
	// }
	// map.put(json_key, json_value);
	// }
	// list.add(map);
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// return list;
	// }
}