package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AuthenticateMsg {
	public static boolean checkMsg(HashMap<String, String> messageMap){
		if(messageMap.get(Constants.SECRET) == null || messageMap.get(Constants.PUBLIC_KEY) == null){
			return false;
		}else{
			return true;
		}
	}

	public static String message(String secret, String key){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_AUTHENTICATE);
		messageMap.put(Constants.SECRET, secret);
		messageMap.put(Constants.PUBLIC_KEY, key);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String replyFail(String secret){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_AUTHENTICATION_FAIL);
		messageMap.put(Constants.INFO, Constants.INFO_AUTHENTICATION_FAIL + secret);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	
}
