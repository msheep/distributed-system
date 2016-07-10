package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InvalidMsg {
	
	public static boolean checkInfoMsg(HashMap<String, String> messageMap){
		if(messageMap.get(Constants.INFO) == null){
			return false;
		}else{
			return true;
		}
	}

	public static String commandError(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_INVALID_MESSAGE);
		messageMap.put(Constants.INFO, Constants.INFO_NO_COMMAND);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String jsonError(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_INVALID_MESSAGE);
		messageMap.put(Constants.INFO, Constants.INFO_PARSE_ERROR);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String invalidServerError(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_INVALID_MESSAGE);
		messageMap.put(Constants.INFO, Constants.INFO_INVALID_SERVER_ERROR);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String paramError(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_INVALID_MESSAGE);
		messageMap.put(Constants.INFO, Constants.INFO_NO_COMMAND);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String fieldEmptyError(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_INVALID_MESSAGE);
		messageMap.put(Constants.INFO, Constants.INFO_REGISTER_ERROR);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
		
	}
	
	public static String lockError(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_INVALID_MESSAGE);
		messageMap.put(Constants.INFO, Constants.INFO_LOCK_ERROR);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String notServerError(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_INVALID_MESSAGE);
		messageMap.put(Constants.INFO, Constants.INFO_NOT_SERVER_ERROR);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String notClientError(){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_INVALID_MESSAGE);
		messageMap.put(Constants.INFO, Constants.INFO_NOT_CLIENT_ERROR);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
}
