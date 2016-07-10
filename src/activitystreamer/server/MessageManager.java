package activitystreamer.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.lang.reflect.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.util.*;

public class MessageManager {

	private static final Logger log = LogManager.getLogger();

	private static MessageManagerFunc func;
	protected static ControlSolution solution = ControlSolution.getInstance();

	public MessageManager(MessageManagerFunc functions) {
		func = functions;
	}

	public void processMessage(String jsonMessage, Connection con) {
		Gson gson = new Gson();
		Type type = new TypeToken<HashMap<String, String>>() {
		}.getType();
		HashMap<String, String> messageMap = null;
		try {
			messageMap = gson.fromJson(jsonMessage, type);
			String command = (String) messageMap.get(Constants.MESSAGE_TYPE);
			if (command == null) {
				solution.sendMessage(InvalidMsg.commandError(), con, true);
			} else {
				switch (command) {
				case Constants.MESSAGE_AUTHENTICATE:
					func.processServerAuthenticate(messageMap, con);
					break;
				case Constants.REPLY_AUTHENTICATION_FAIL:
					func.processServerAuthenticateFail(messageMap, con);
					break;
				case Constants.MESSAGE_LOGIN:
					if (con.isServer()) {
						solution.sendMessage(InvalidMsg.notClientError(), con, true);
					} else {
						func.processClientLogin(messageMap, con);
					}
					break;
				case Constants.MESSAGE_LOGOUT:
					if (con.isServer()) {
						solution.sendMessage(InvalidMsg.notClientError(), con, true);
					} else {
						func.processClientLogout(messageMap, con);
					}
					break;
				case Constants.MESSAGE_ACTIVITY_MESSAGE:
					if (con.isServer()) {
						solution.sendMessage(InvalidMsg.notClientError(), con, true);
					} else {
						func.processClientActivityMessage(messageMap, con);
					}
					break;
				case Constants.MESSAGE_SERVER_ANNOUNCE:
					if (!con.isServer()) {
						solution.sendMessage(InvalidMsg.notServerError(), con, true);
					} else {
						func.processServerAnnounce(messageMap, con);
					}
					break;
				case Constants.MESSAGE_ACTIVITY_BROADCAST:
					if (!con.isServer()) {
						solution.sendMessage(InvalidMsg.notServerError(), con, true);
					} else {
						func.processServerActivityBroadcast(messageMap, con);
					}
					break;
				case Constants.MESSAGE_REGISTER:
					func.processClientRegister(messageMap, con);
					break;
				case Constants.MESSAGE_LOCK_REQUEST:
					if (!con.isServer()) {
						solution.sendMessage(InvalidMsg.notServerError(), con, true);
					} else if (!con.isValid()) {
						solution.sendMessage(InvalidMsg.invalidServerError(), con,
								true);
					} else {
						func.processServerLockRequest(messageMap, con);
					}
					break;
				case Constants.MESSAGE_LOCK_DENIED:
					if (!con.isServer()) {
						solution.sendMessage(InvalidMsg.notServerError(), con, true);
					} else {
						func.processServerLockDenied(messageMap, con);
					}
					break;
				case Constants.MESSAGE_LOCK_ALLOWED:
					if (!con.isServer()) {
						solution.sendMessage(InvalidMsg.notServerError(), con, true);
					} else {
						func.processServerLockAllowed(messageMap, con);
					}
					break;
				case Constants.REPLY_INVALID_MESSAGE:
					func.processInvalidMessage(messageMap, con);
					break;
				case Constants.MESSAGE_REQUEST_KEY:
					func.processRequestKeyMessage(messageMap, con);
					break;
				case Constants.MESSAGE_REPLY_KEY:
					func.processReplyKeyMessage(messageMap, con);
					break;
				case Constants.SHARED_KEY:
					func.processSharedKeyMessage(messageMap, con);
					break;
				default:
					solution.sendMessage(InvalidMsg.commandError(), con, true);
					break;
				}
			}
		} catch (Exception e) {
			solution.sendMessage(InvalidMsg.jsonError(), con, true);
		}

	
	}

	public interface MessageManagerFunc {

		public void processServerAuthenticate(HashMap<String, String> messageMap,
				Connection con);

		public void processSharedKeyMessage(HashMap<String, String> messageMap,
				Connection con);

		public void processReplyKeyMessage(HashMap<String, String> messageMap,
				Connection con);

		public void processRequestKeyMessage(HashMap<String, String> messageMap,
				Connection con);

		public void processInvalidMessage(HashMap<String, String> messageMap,
				Connection con);

		public void processServerAuthenticateFail(HashMap<String, String> messageMap,
				Connection con);

		public void processClientLogin(HashMap<String, String> messageMap,
				Connection con);

		public void processClientLogout(HashMap<String, String> messageMap,
				Connection con);

		public void processClientActivityMessage(HashMap<String, String> messageMap,
				Connection con);

		public void processServerAnnounce(HashMap<String, String> messageMap,
				Connection con);

		public void processServerActivityBroadcast(HashMap<String, String> messageMap,
				Connection con);

		public void processClientRegister(HashMap<String, String> messageMap,
				Connection con);

		public void processServerLockRequest(HashMap<String, String> messageMap,
				Connection con);

		public void processServerLockDenied(HashMap<String, String> messageMap,
				Connection con);

		public void processServerLockAllowed(HashMap<String, String> messageMap,
				Connection con);
	}
}
