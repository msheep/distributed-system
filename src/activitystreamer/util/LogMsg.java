package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LogMsg {
	public static boolean checkMsg(HashMap<String, String> messageMap) {
		if (messageMap.get(Constants.NAME).equals(Constants.INITIAL_USER)) {
			if(messageMap.get(Constants.SHARED_KEY) == null){
				return false;
			}else{
				return true;
			}
		} else if (messageMap.get(Constants.SECRET) == null
				|| messageMap.get(Constants.NAME) == null
				|| messageMap.get(Constants.SHARED_KEY) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static String replyFailed(String name) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_LOGIN);
		messageMap.put(Constants.INFO, name + Constants.INFO_LOGIN_FAILED);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}

	public static String replySuccess(String name) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_LOGIN_SUCCESS);
		messageMap.put(Constants.INFO, Constants.INFO_LOGIN_SUCCESS + name);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}

	public static String LoginMsg(String clientname, String secret, String sharedKey) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_LOGIN);
		messageMap.put(Constants.NAME, clientname);
		messageMap.put(Constants.SECRET, secret);
		messageMap.put(Constants.SHARED_KEY, sharedKey);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}

	public static String LogoutMsg(String clientname, String secret) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_LOGOUT);
		messageMap.put(Constants.NAME, clientname);
		messageMap.put(Constants.SECRET, secret);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
}