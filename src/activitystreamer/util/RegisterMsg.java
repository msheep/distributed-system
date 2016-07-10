package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RegisterMsg {
	public static boolean checkMsg(HashMap<String, String> messageMap) {
		if (messageMap.get(Constants.SECRET) == null
				|| messageMap.get(Constants.NAME) == null
				|| messageMap.get(Constants.SHARED_KEY) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static String clientRequest(String name, String secret, String sharedKey) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_REGISTER);
		messageMap.put(Constants.NAME, name);
		messageMap.put(Constants.SECRET, secret);
		messageMap.put(Constants.SHARED_KEY, sharedKey);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}

	public static String replyFailed(String name) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REGIST_FAILED);
		messageMap.put(Constants.INFO, Constants.INFO_REGISTER_FAILED + name);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}

	public static String replySuccess(String name) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REGIST_SUCESS);
		messageMap.put(Constants.INFO, Constants.INFO_REGISTER_SUCCESS + name);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
}