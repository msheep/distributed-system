package activitystreamer.util;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RedirectMsg {

	public static String message(String host,int port){
		Map<String, String> messageMap = new HashMap<>();
		
		messageMap.put(Constants.MESSAGE_TYPE, Constants.REPLY_REDIRECT);
		messageMap.put(Constants.HOSTNAME, host);
		messageMap.put(Constants.PORT, Integer.toString(port));
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(messageMap);
		return json;
	}
	
	
	
}
