package activitystreamer.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import activitystreamer.server.Connection;
import activitystreamer.server.ControlSolution;
import activitystreamer.util.*;

public class ClientSolution extends Thread {
	private static final Logger log = LogManager.getLogger();
	private static ClientSolution clientSolution;
	private TextFrame textFrame;
	private MainFrame mainFrame;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private BufferedReader inreader;
	private PrintWriter outwriter;
	private boolean open = true;
	private boolean term = false;
	private Gson gson = new Gson();
	protected PublicKey fatherPublicKey;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private SecretKey sharedKey = null;

	// this is a singleton object
	public static ClientSolution getInstance() {
		if (clientSolution == null) {
			clientSolution = new ClientSolution();
		}
		return clientSolution;
	}

	public ClientSolution() {
		try {
			socket = new Socket(Settings.getRemoteHostname(), Settings.getRemotePort());
			in = new DataInputStream(socket.getInputStream());
			inreader = new BufferedReader(new InputStreamReader(in));
			out = new DataOutputStream(socket.getOutputStream());
			outwriter = new PrintWriter(out, true);

			Map<String, Object> keyMap = Coder.genKeyPair();
			publicKey = (PublicKey) keyMap.get(Constants.PUBLIC_KEY);
			privateKey = (PrivateKey) keyMap.get(Constants.PRIVATE_KEY);

			// request public key
			sendMessage(KeyMsg.requestKey());

			// open the gui
			log.debug("opening the gui");
			textFrame = new TextFrame();
			start();
		} catch (IOException e) {
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// called by the gui when the user clicks "send"
	public void sendActivityObject(String activityObj) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(activityObj);
			String activityMessage = ActivityBroadcastMsg.clientMessage(
					Settings.getUsername(), Settings.getSecret(), obj.toJSONString());
			activityMessage = Coder.encryptBySharedKey(activityMessage, sharedKey);
			sendMessage(activityMessage);
		} catch (ParseException e) {
			log.error("invalid JSON object entered into input text field, data not sent");
		} catch (Exception e1) {
			textFrame.showMsg("Faill to encrypt the message!");
		}

	}

	// called by the gui when the user clicks disconnect
	public void disconnect() {
		LogMsg.LogoutMsg(Settings.getUsername(), Settings.getSecret());
		try {
			in.close();
			inreader.close();
			out.close();
			outwriter.close();
			term = true;
		} catch (IOException e) {
			log.debug("Client close connection failed: " + e.getMessage());
		}
		mainFrame.dispose();
		textFrame.dispose();
	}

	protected boolean sendMessage(String msg) {
		if (open) {
			log.info("sending message:" + msg);
			outwriter.println(msg);

			outwriter.flush();
			return true;
		}
		return false;
	}

	// the client's run method, to receive messages
	@Override
	public void run() {
		log.debug("client start");
		mainFrame = new MainFrame();
		String data;
		try {
			while (!term && (data = inreader.readLine()) != null) {
				term = process(data);
			}
			in.close();
		} catch (IOException e) {
			log.info("connection " + Settings.socketAddress(socket) + " closed");
			disconnect();
		}
		open = false;
	}

	public synchronized boolean process(String jsonMessage) {
		Type type = new TypeToken<HashMap<String, String>>() {
		}.getType();
		HashMap<String, String> messageMap = null;
		try {
			if (!jsonMessage.contains("{")) {
				jsonMessage = Coder.decryptBySharedKey(jsonMessage, sharedKey);
				log.info(jsonMessage + "11111111");
			}
			messageMap = gson.fromJson(jsonMessage, type);
			String command = (String) messageMap.get(Constants.MESSAGE_TYPE);
			if (command == null) {
				sendMessage(InvalidMsg.commandError());
				return false;
			} else {
				switch (command) {
				case Constants.MESSAGE_ACTIVITY_BROADCAST:
					String activity = messageMap.get(Constants.ACTIVITY);
					JSONParser parser = new JSONParser();
					try {
						JSONObject obj = (JSONObject) parser.parse(activity);
						activity = obj.toJSONString();
					} catch (JsonSyntaxException e) {
						sendMessage(InvalidMsg.jsonError());
						return true;
					}
					textFrame.displayActivityMessageText(activity);
					return false;
				case Constants.REPLY_REGISTER_SUCCESS:
					mainFrame.showMsg(messageMap.get(Constants.INFO));
					mainFrame.hide();
					return false;
				case Constants.REPLY_REGISTER_FAILED:
					mainFrame.showMsg(messageMap.get(Constants.INFO));
					disconnect();
					return true;
				case Constants.REPLY_REDIRECT:
					return processRedirect(messageMap);
				case Constants.REPLY_LOGIN_SUCCESS:
					mainFrame.hide();
					if (textFrame == null) {
						textFrame = new TextFrame();
					}
					textFrame.showMsg(messageMap.get(Constants.INFO));
					return false;
				case Constants.REPLY_LOGIN_FAILED:
					mainFrame.showMsg(messageMap.get(Constants.INFO));
					disconnect();
					return true;
				case Constants.MESSAGE_REPLY_KEY:
					return processReplyKeyMessage(messageMap);
				case Constants.REPLY_INVALID_MESSAGE:
					textFrame.showMsg(messageMap.get(Constants.INFO));
					disconnect();
					return true;
				default:
					sendMessage(InvalidMsg.commandError());
					disconnect();
					return true;
				}
			}

		} catch (JsonSyntaxException e) {
			sendMessage(InvalidMsg.jsonError());
			return true;
		} catch (Exception e) {
			sendMessage(InvalidMsg.jsonError());
			return true;
		}
	}

	public boolean processReplyKeyMessage(HashMap<String, String> messageMap) {
		if (!KeyMsg.checkMsg(messageMap)) {
			mainFrame.showMsg(InvalidMsg.paramError());
			disconnect();
			return true;
		} else {
			// save the parent public key
			String publicKey = messageMap.get(Constants.PUBLIC_KEY);
			try {
				fatherPublicKey = Coder.stringToPublicKey(publicKey);
			} catch (Exception e) {
				mainFrame.showMsg("fail to store the public key!");
			}
			return false;
		}
	}

	private boolean processRedirect(HashMap<String, String> messageMap) {
		disconnect();
		String newHost = messageMap.get(Constants.HOSTNAME);
		int newPort = Integer.parseInt(messageMap.get(Constants.PORT));
		Settings.setRemoteHostname(newHost);
		Settings.setRemotePort(newPort);
		clientSolution = new ClientSolution();
		return false;
	}

	public void sendRegisterMsg() {
		if (open) {
			try {
				SecretKey sharedKey = Coder.genSharedKey();
				this.sharedKey = sharedKey;
				String key = Coder.sharedKeyToString(sharedKey);
				String secret = Coder.encryptByPublicKey(Settings.getSecret(),
						fatherPublicKey);
				String message = RegisterMsg.clientRequest(Settings.getUsername(), secret,
						key);
				sendMessage(message);
			} catch (Exception e) {
				mainFrame.showMsg("fail to send register message!");
			}
		}

	}

	public void sendInitialLoginMsg() {
		if (open) {
			try {
				SecretKey sharedKey = Coder.genSharedKey();
				this.sharedKey = sharedKey;
				String key = Coder.sharedKeyToString(sharedKey);
				String message = RegisterMsg.clientRequest(Constants.INITIAL_USER, "",
						key);
				sendMessage(message);
			} catch (Exception e) {
				mainFrame.showMsg("fail to send login message!");
			}
		}
	}

	public void sendLoginMsg() {
		if (open) {
			try {
				SecretKey sharedKey = Coder.genSharedKey();
				this.sharedKey = sharedKey;
				String key = Coder.sharedKeyToString(sharedKey);
				String secret = Coder.encryptByPublicKey(Settings.getSecret(),
						fatherPublicKey);
				String message = LogMsg.LoginMsg(Settings.getUsername(), secret, key);
				sendMessage(message);
			} catch (Exception e) {
				mainFrame.showMsg("fail to send login message!");
			}
		}
	}

}
