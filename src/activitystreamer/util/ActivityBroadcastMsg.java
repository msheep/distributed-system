package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ActivityBroadcastMsg {
	public static boolean checkMsg(HashMap<String, String> messageMap){
		if(messageMap.get(Constants.ACTIVITY) == null){
			return false;
		}else{
			return true;
		}
	}

	public static String message(String activity){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_ACTIVITY_BROADCAST);
		messageMap.put(Constants.ACTIVITY, activity);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static String clientMessage(String clientname, String secret, String activity){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_ACTIVITY_MESSAGE);
		messageMap.put(Constants.NAME, clientname);
		messageMap.put(Constants.SECRET, secret);
		messageMap.put(Constants.ACTIVITY, activity);
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	public static boolean checkActivityMsg(HashMap<String, String> messageMap){
		if (messageMap.get(Constants.NAME).equals(Constants.INITIAL_USER)) {
			if(messageMap.get(Constants.ACTIVITY) == null){
				return false;
			}else{
				return true;
			}
		} else if (messageMap.get(Constants.SECRET) == null
				|| messageMap.get(Constants.NAME) == null
				|| messageMap.get(Constants.ACTIVITY) == null) {
			return false;
		} else {
			return true;
		}
		
	}
}
