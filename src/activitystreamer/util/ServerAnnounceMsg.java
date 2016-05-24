package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServerAnnounceMsg {
	
	public static boolean checkMsg(HashMap<String, String> messageMap){
		if(messageMap.get(Constants.ID) == null || messageMap.get(Constants.LOAD) == null
		|| messageMap.get(Constants.HOSTNAME) == null || messageMap.get(Constants.PORT) == null){
			return false;
		}else{
			return true;
		}
	}
	
	public static String message(String id, int load, String hostname, int port){
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(Constants.MESSAGE_TYPE, Constants.MESSAGE_SERVER_ANNOUNCE);
		messageMap.put(Constants.ID, id);
		messageMap.put(Constants.LOAD, String.valueOf(load));
		messageMap.put(Constants.HOSTNAME, hostname);
		messageMap.put(Constants.PORT, String.valueOf(port));
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}

}
