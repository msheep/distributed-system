package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KeyMsg {
	public static boolean checkMsg(HashMap<String, String> messageMap){
		if(messageMap.get(Constants.PUBLIC_KEY) == null){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean checkSharedKeyMsg(HashMap<String, String> messageMap){
		if(messageMap.get(Constants.SHARED_KEY) == null){
			return false;
		}else{
			return true;
		}
	}
	
	public static String requestKey(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_REQUEST_KEY);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String replyKey(String key){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_REPLY_KEY);
		messageMap.put(Constants.PUBLIC_KEY, key);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String sharedKeyMsg(String key){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.SHARED_KEY);
		messageMap.put(Constants.SHARED_KEY, key);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
}
