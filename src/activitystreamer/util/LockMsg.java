package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class LockMsg {
	public static boolean checkMsg(HashMap<String, String> messageMap){
		if(messageMap.get(Constants.SECRET) == null || messageMap.get(Constants.NAME)==null){
			return false;
		}else{
			return true;
		}
	}

	public static String request(String name, String secret){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_LOCK_REQUEST);
		messageMap.put(Constants.NAME,name);
		messageMap.put(Constants.SECRET, secret);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}

	public static String replyDenied(String secret,String name, String serverId){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_LOCK_DENIED);
		messageMap.put(Constants.NAME,name);
		messageMap.put(Constants.SECRET, secret);
		messageMap.put(Constants.SERVERID, serverId);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String replyAllowed(String name, String secret, String serverId){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_LOCK_ALLOWED);
		messageMap.put(Constants.NAME, name);
		messageMap.put(Constants.SECRET, secret);
		messageMap.put(Constants.SERVERID, serverId);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
}