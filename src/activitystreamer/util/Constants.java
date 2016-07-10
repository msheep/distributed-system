package activitystreamer.util;

public class Constants {
	public static final String ID = "id";
	public static final String INFO = "info";
	public static final String LOAD = "load";
	public static final String PORT = "port";
	public static final String NAME = "username";
	public static final String SECRET = "secret";
	public static final String HOSTNAME = "hostname";
	public static final String ACTIVITY = "activity";
	public static final String INITIAL_USER = "anonymous";
	public static final String SERVERID = "serverId";
	public static final String KEY = "KEY";
	public static final String SHARED_KEY = "SHARED_KEY";
	public static final String PUBLIC_KEY = "RSAPublicKey";
	public static final String PRIVATE_KEY = "RSAPrivateKey";
	
	public static final String MESSAGE_SHARED_KEY_RECEIVED = "SHARED_KEY_RECEIVED";
	
	public static final String MESSAGE_TYPE = "command";
	public static final String MESSAGE_AUTHENTICATE = "AUTHENTICATE";
	public static final String MESSAGE_REQUEST_KEY = "REQUEST_KEY";
	public static final String MESSAGE_REPLY_KEY = "REPLY_KEY";
	public static final String MESSAGE_LOGIN = "LOGIN";
	public static final String MESSAGE_LOGOUT = "LOGOUT";
	public static final String MESSAGE_ACTIVITY_MESSAGE = "ACTIVITY_MESSAGE";
	public static final String MESSAGE_SERVER_ANNOUNCE = "SERVER_ANNOUNCE";
	public static final String MESSAGE_ACTIVITY_BROADCAST = "ACTIVITY_BROADCAST";
	public static final String MESSAGE_REGISTER = "REGISTER";
	public static final String MESSAGE_LOCK_REQUEST = "LOCK_REQUEST";
	public static final String MESSAGE_LOCK_DENIED = "LOCK_DENIED";
	public static final String MESSAGE_LOCK_ALLOWED = "LOCK_ALLOWED";
	
	public static final String REPLY_INVALID_MESSAGE = "INVALID_MESSAGE";
	public static final String REPLY_AUTHENTICATION_FAIL = "AUTHENTICATION_FAIL";
	public static final String REPLY_AUTHENTICATION_SUCCESS = "AUTHENTICATION_SUCCESS";
	public static final String REPLY_LOGIN_SUCCESS = "LOGIN_SUCCESS";
	public static final String REPLY_REDIRECT = "REDIRECT";
	public static final String REPLY_LOGIN_FAILED = "LOGIN_FAILED";
	public static final String REPLY_REGISTER_FAILED = "REGISTER_FAILED";
	public static final String REPLY_REGISTER_SUCCESS = "REGISTER_SUCCESS";
	public static final String REPLY_LOCK_DENIED = "LOCK_DENIED";
	public static final String REPLY_LOCK_ALLOWED = "LOCK_ALLOWED";
	
	public static final String INFO_NO_COMMAND = "the received message did not contain a command";
	public static final String INFO_PARSE_ERROR = "JSON parse error while parsing message";
	public static final String INFO_AUTHENTICATION_FAIL = "the supplied secret is incorrect: ";
	public static final String INFO_LOGIN_SUCCESS = "logged in as user ";
	public static final String INFO_LOGIN_FAILED = "attempt to login with wrong secret";
	public static final String INFO_REGISTER_FAILED = " is already registered with the system";
	public static final String INFO_REGISTER_SUCCESS = "register success for ";
	public static final String INFO_PARAM_ERROR = "the received message did not contain right parameters";
	
	public static final String MESSAGE_ACTIVITY = "activity";
	
	public static final String REGIST_SUCESS = "REGISTER_SUCCESS";
	public static final String INFO_LOCK_DENIED = "LOCK_DENIED";
	
	public static final String REGIST_FAILED = "REGISTER_FAILED";
	public static final String INFO_LOCK_ALLOWED = "LOCK_ALLOWED";
	
	public static final String INFO_REGISTER_ERROR = "empty field for username or secret";
	public static final String INFO_LOCK_ERROR = "the username or secret is incorrect";
	public static final String INFO_ACTIVITY_MESSAGE = "Activity message is incorrect";
	
	public static final String INFO_INVALID_SERVER_ERROR = "server is unauthenticated";
	public static final String INFO_NOT_SERVER_ERROR = "the sender is not a server";
	public static final String INFO_NOT_CLIENT_ERROR = "the sender is not a client";
}
