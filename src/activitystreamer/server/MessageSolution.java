package activitystreamer.server;

import javax.crypto.SecretKey;
import java.util.HashMap;

import activitystreamer.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonSyntaxException;

public class MessageSolution implements MessageManager.MessageManagerFunc {
	private static final Logger log = LogManager.getLogger();
	private static final ControlSolution solution = ControlSolution.getInstance();

	public MessageSolution() {
	}

	/*
	 * #######################################################################
	 * There is a tag to distinguish whether the server is authenticated.If the
	 * server pass the authentication, the "valid" tag will be set to be true.
	 * If the secret is wrong, the connection will be closed.
	 * #######################################################################
	 */
	@Override
	public void processServerAuthenticate(HashMap<String, String> messageMap,
			Connection con) {
		if (!AuthenticateMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			try {
				// only the secret is encrypted by public key, should be
				// decrypted by private key
				String secret = messageMap.get(Constants.SECRET);
				secret = Coder.decryptByPrivateKey(secret, ControlSolution.privateKey);
				String publicKey = messageMap.get(Constants.PUBLIC_KEY);
				
				if (!secret.equals(Settings.getSecret())) {
					solution.sendMessage(AuthenticateMsg.replyFail(secret), con, true);
				} else {
					con.setValid(true);
					con.setServer(true);
					con.setPublicKey(publicKey);
					
					// Send the shared key when authenticate success
					SecretKey sharedKey = Coder.genSharedKey();
					con.setSharedKey(sharedKey);
					String key = Coder.sharedKeyToString(sharedKey);
					solution.sendMessage(KeyMsg.sharedKeyMsg(key), con);
				}
			} catch (Exception e) {
				solution.sendMessage(InvalidMsg.paramError(), con, true);
			}
			
		}
	}

	/*
	 * #######################################################################
	 * If server did not pass the authentication, connection will be closed
	 * #######################################################################
	 */
	@Override
	public void processServerAuthenticateFail(HashMap<String, String> messageMap,
			Connection con) {
		if (con.isOpen()) {
			solution.connectionClosed(con);
			solution.setTerm(true);
		} else {
			log.info("connection is alredy closed");
		}
	}

	/*
	 * #######################################################################
	 * Send the announce message to all servers except the sever who sent
	 * message to the current server. If the server is new added, should be
	 * added in the server list or update its info.
	 * #######################################################################
	 */
	@Override
	public void processServerAnnounce(HashMap<String, String> messageMap,
			Connection con) {
		// Check whether the message is valid
		if (!ServerAnnounceMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			// Get information from the message
			String id = messageMap.get(Constants.ID);
			int load = Integer.parseInt(messageMap.get(Constants.LOAD));
			String hostname = messageMap.get(Constants.HOSTNAME);
			int port = Integer.parseInt(messageMap.get(Constants.PORT));
			if (!id.equals(solution.getMyServerId())) {
				String announce = ServerAnnounceMsg.message(id, load, hostname, port);
				// Announce to all the servers unless current message is from
				// this server
				solution.sendBroadcastMessage(announce, con);
				// for (Connection connection : solution.getConnections()) {
				// if (!connection.equals(con) && connection.isValid()) {
				// solution.sendMessage(announce, connection);
				// }
				// }
				// Make sure whether the server is new
				Boolean haveServer = false;
				for (ServerUser server : solution.getSeverList()) {
					if (server.getPort() == port
							&& server.getHostname().equals(hostname)) {
						haveServer = true;
						// Update servers' load
						server.setLoad(load);
					}
				}
				// Add new server in server table
				if (!haveServer) {
					solution.pushServerUser(id, hostname, port, load);
				}
			}
		}

	}

	/*
	 * #######################################################################
	 * Send the activity message to all servers.
	 * #######################################################################
	 */
	@Override
	public void processClientActivityMessage(HashMap<String, String> messageMap,
			Connection con) {
		if (!ActivityBroadcastMsg.checkActivityMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			String activity = ActivityBroadcastMsg
					.message(messageMap.get(Constants.ACTIVITY));
			JSONParser parser = new JSONParser();
			try {
				JSONObject obj = (JSONObject) parser.parse(activity);
				activity = obj.toJSONString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Announce to all the servers and clients
			 for (Connection connection : solution.getConnections()) {
				 if (connection.isValid()) {
					 solution.sendMessage(activity, connection);
				 }
			 }
		}

	}

	/*
	 * #######################################################################
	 * Send the activity message to all servers except the sender.
	 * #######################################################################
	 */
	@Override
	public void processServerActivityBroadcast(HashMap<String, String> messageMap,
			Connection con) {
		if (!ActivityBroadcastMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			String activity = ActivityBroadcastMsg
					.message(messageMap.get(Constants.ACTIVITY));
			JSONParser parser = new JSONParser();
			try {
				JSONObject obj = (JSONObject) parser.parse(activity);
				activity = obj.toJSONString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (Connection connection : solution.getConnections()) {
				if (!connection.equals(con) && connection.isValid()) {
					solution.sendMessage(activity, connection);
				}
			}
		}

	}

	/*
	 * #######################################################################
	 * Check whether the client is valid and the user's password is correct.
	 * #######################################################################
	 */
	public ClientUser checkClients(String username, String secret) {
		ClientUser clientUser = null;
		for (ClientUser client : solution.getClientList()) {
			if (client.getPassword().equals(secret)
					&& client.getUsername().equals(username)) {
				clientUser = client;
				break;
			}
		}
		return clientUser;
	}

	public ClientUser checkRedirectedClients(String username, String secret) {
		ClientUser clientUser = null;
		for (ClientUser client : solution.getRedirectedClientList()) {
			if (client.getPassword().equals(secret)
					&& client.getUsername().equals(username)) {
				clientUser = client;
				break;
			}
		}
		return clientUser;
	}

	/*
	 * #######################################################################
	 * If the server knows of any other server with a load at least 2 clients
	 * less than its own, it will send back a redirect message or it will let
	 * requested client to login.
	 * #######################################################################
	 */
	@Override
	public void processClientLogin(HashMap<String, String> messageMap, Connection con) {
		if (!LogMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			String username = messageMap.get(Constants.NAME);
			String secret = messageMap.get(Constants.SECRET);
			try {
				secret = Coder.decryptByPrivateKey(secret, ControlSolution.privateKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String key = messageMap.get(Constants.SHARED_KEY);
			try {
				SecretKey sharedKey = Coder.stringToSharedKey(key);
				con.setSharedKey(sharedKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ClientUser client = checkClients(username, secret);
			if (client == null || username.equals(Constants.INITIAL_USER)) {
				// check if the current client is a redirected client
				if (solution.getSeverList().size() > 0) {
					// add into the lock request list
					solution.pushLockLoginRequestList(username, secret, con);
					String lockRequest = LockMsg.request(username, secret);
					solution.sendBroadcastMessage(lockRequest);
				} else {
					solution.sendMessage(
							LogMsg.replyFailed(messageMap.get(Constants.NAME)), con,
							true);
				}
			} else {
				/*
				 * Redirect the connection to another server if the server knows
				 * of any other server with a load at least 2 clients less than
				 * its own
				 */
				ServerUser lessLoaderServer = null;
				for (ServerUser server : solution.getSeverList()) {
					if (server.getLoad() + 2 <= solution.getClientLoad()) {
						lessLoaderServer = server;
						break;
					}
				}
				if (lessLoaderServer != null) {
					// update client type 2:Redirect
					client.setType(2);
					// add the redirected client list
					solution.addRedirectedClient(username, secret, con);

					for (Connection connection : solution.getConnections()) {
						if (connection.isServer() && connection.isValid()) {
							solution.sendMessage(
									RedirectMsg.message(lessLoaderServer.getHostname(),
											lessLoaderServer.getPort()),
									con, true);
						}
					}
				} else {
					// update client type 1: valid
					client.setType(1);
					con.setValid(true);
					solution.sendMessage(LogMsg.replySuccess(username), con);
				}
			}
		}
	}

	/*
	 * #######################################################################
	 * Close the connection if the client log out
	 * #######################################################################
	 */
	@Override
	public void processClientLogout(HashMap<String, String> messageMap, Connection con) {
		if (con.isOpen()) {
			solution.connectionClosed(con);
		} else {
			log.info("connection is alredy closed");
		}

	}

	@Override
	public void processClientRegister(HashMap<String, String> messageMap,
			Connection con) {
		if (!RegisterMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			String username = messageMap.get(Constants.NAME);
			String secret = messageMap.get(Constants.SECRET);
			try {
				secret = Coder.decryptByPrivateKey(secret, ControlSolution.privateKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String key = messageMap.get(Constants.SHARED_KEY);
			try {
				SecretKey sharedKey = Coder.stringToSharedKey(key);
				con.setSharedKey(sharedKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (checkClients(username, secret) != null
					&& !username.equals(Constants.INITIAL_USER)) {
				solution.sendMessage(RegisterMsg.replyFailed(username), con, true);
			} else {
				// Add client to client list
				ClientUser newUser = solution.addClient(username, secret, con);
				// if there is only one server, reply success
				if (solution.getSeverList().size() == 0) {
					// 1: valid
					newUser.setType(1);
					// Register success
					con.setValid(true);
					solution.sendMessage(RegisterMsg.replySuccess(username), con);
				} else {
					// Broadcast the Lock Request to other servers when a new
					// client register
					String lockRequest = LockMsg.request(username, secret);
					solution.sendBroadcastMessage(lockRequest);
					// for (Connection connection : solution.getConnections()) {
					// if (connection.isServer() && connection.isValid()) {
					// solution.sendMessage(lockRequest, connection);
					// }
					// }
					// add into the lock request list, 0:register lock
					solution.pushLockRegisterRequestList(username, secret, con);
				}

			}
		}
	}

	public LockRequest checkRegisterLockRequest(String name, String secret) {
		LockRequest lockRequest = null;
		for (LockRequest request : solution.getRegisterLockRequestList()) {
			if (request.getUsername().equals(name)
					&& request.getPassword().equals(secret)) {
				lockRequest = request;
				break;
			}
		}
		return lockRequest;
	}

	public LockRequest checkLoginLockRequest(String name, String secret) {
		LockRequest lockRequest = null;
		for (LockRequest request : solution.getLoginLockRequestList()) {
			if (request.getUsername().equals(name)
					&& request.getPassword().equals(secret)) {
				lockRequest = request;
				break;
			}
		}
		return lockRequest;
	}

	@Override
	public void processServerLockRequest(HashMap<String, String> messageMap,
			Connection con) {
		if (!LockMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			String username = messageMap.get(Constants.NAME);
			String secret = messageMap.get(Constants.SECRET);
			ClientUser client = checkClients(username, secret);

			// Send lock allowed request if the client does not exist or
			// send lock denied
			if (client != null) {
				// broadcast the lock denied info to other servers
				solution.sendBroadcastMessage(
						LockMsg.replyDenied(username, secret, solution.getMyServerId()));

				// for (Connection connection : solution.getConnections()) {
				// if (connection.isServer()) {
				// solution.sendMessage(LockMsg.replyDenied(username, secret,
				// solution.getMyServerId()), con);
				// }
				// }
			} else {
				// broadcast the lock allowed info to other servers
				solution.sendBroadcastMessage(
						LockMsg.replyAllowed(username, secret, solution.getMyServerId()));

				// for (Connection connection : solution.getConnections()) {
				// if (connection.isServer()) {
				// solution.sendMessage(LockMsg.replyAllowed(username, secret,
				// solution.getMyServerId()), con);
				// }
				// }
			}
		}

	}

	@Override
	public void processServerLockDenied(HashMap<String, String> messageMap,
			Connection con) {
		if (!LockMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			String username = messageMap.get(Constants.NAME);
			String secret = messageMap.get(Constants.SECRET);
			LockRequest registerLock = checkRegisterLockRequest(username, secret);
			LockRequest LoginLock = checkLoginLockRequest(username, secret);
			ClientUser client = checkClients(username, secret);
			ClientUser redirected = checkRedirectedClients(username, secret);

			// Reply the register fail message if the register lock is denied
			if (registerLock != null && client != null) {
				// remove register request information from lock list
				solution.removeRegisterLockRequestList(registerLock);
				// remove the client from client list
				solution.removeClient(client);
				solution.sendMessage(RegisterMsg.replyFailed(username),
						client.getConnection(), true);

			}

			// Reply login success if the login lock is denied
			if (LoginLock != null) {
				// remove login request information from lock list
				solution.removeLoginLockRequestList(LoginLock);
				LoginLock.getConnection().setValid(true);
				solution.sendMessage(LogMsg.replySuccess(username),
						LoginLock.getConnection());
			}

			// if the server does not have the register and login lock info,
			// client info, it means the lock request
			// is not from the current server, then broadcast the info
			if (registerLock == null && LoginLock == null && client == null
					&& redirected == null) {
				// broadcast the lock denied info to other servers except the
				// sender
				solution.sendBroadcastMessage(LockMsg.replyDenied(username, secret,
						messageMap.get(Constants.SERVERID)), con);

				// for (Connection connection : solution.getConnections()) {
				// if (!connection.equals(con) && connection.isServer()) {
				// solution.sendMessage(LockMsg.replyDenied(username, secret,
				// messageMap.get(Constants.SERVERID)), con);
				// }
				// }

			}
		}
	}

	@Override
	public void processServerLockAllowed(HashMap<String, String> messageMap,
			Connection con) {
		if (!LockMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			String username = messageMap.get(Constants.NAME);
			String secret = messageMap.get(Constants.SECRET);
			String serverId = messageMap.get(Constants.SERVERID);
			LockRequest registerLock = checkRegisterLockRequest(username, secret);
			LockRequest LoginLock = checkLoginLockRequest(username, secret);
			ClientUser client = checkClients(username, secret);
			ClientUser redirected = checkRedirectedClients(username, secret);

			// Reply the register fail message if the register lock is allowed
			if (registerLock != null && client != null) {
				// add the serverId to the lock request
				registerLock.addAllowedServerId(serverId);
				// if receive all the server allowed responses then reply
				// success info to client
				if (registerLock.getAllowedServerId().size() == solution.getSeverList()
						.size()) {
					// 1: valid
					client.setType(1);
					// Register success
					client.getConnection().setValid(true);
					solution.sendMessage(RegisterMsg.replySuccess(username),
							client.getConnection());
				}
			}

			// Reply login success if the login lock is allowed
			if (LoginLock != null && redirected != null) {
				// add the serverId to the lock request
				LoginLock.addAllowedServerId(serverId);
				// if receive all the server allowed responses then reply login
				// fail info to client
				if (LoginLock.getAllowedServerId().size() == solution.getSeverList()
						.size()) {
					// remove login request information from lock list
					solution.removeLoginLockRequestList(LoginLock);
					// if there is another server has the requested-login user
					// then allow to login
					solution.sendMessage(LogMsg.replyFailed(username), con);
				}
			}

			// if the server does not have the register and login lock info,
			// client info, it means the lock request
			// is not from the current server, then broadcast the info
			if (registerLock == null && LoginLock == null && client == null
					&& redirected == null) {
				// broadcast the lock denied info to other servers except the
				// sender

				solution.sendBroadcastMessage(LockMsg.replyDenied(username, secret,
						messageMap.get(Constants.SERVERID)), con);
				// for (Connection connection : solution.getConnections()) {
				// if (!connection.equals(con) && connection.isServer()) {
				// solution.sendMessage(LockMsg.replyDenied(username, secret,
				// messageMap.get(Constants.SERVERID)), con);
				// }
				// }

			}
		}
	}

	@Override
	public void processInvalidMessage(HashMap<String, String> messageMap,
			Connection con) {
		if (con.isOpen()) {
			solution.connectionClosed(con);
		} else {
			log.info("connection is alredy closed");
		}
	}

	@Override
	public void processReplyKeyMessage(HashMap<String, String> messageMap,
			Connection con) {
		if (!KeyMsg.checkMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			// save the parent public key
			String publicKey = messageMap.get(Constants.PUBLIC_KEY);
			solution.setFatherPublicKey(publicKey);

			// send the authenticate message
			try {
				String secret = Settings.getSecret();
				secret = Coder.encryptByPublicKey(secret,
						solution.getFatherPublicKey());
				String selfPublicKey = Coder.publicKeyToString(ControlSolution.publicKey);
				solution.sendMessage(AuthenticateMsg.message(secret, selfPublicKey), con);
			} catch (Exception e) {
				solution.sendMessage(InvalidMsg.paramError(), con, true);
			}
		}
	}

	@Override
	public void processRequestKeyMessage(HashMap<String, String> messageMap,
			Connection con) {
		try {
			String key = Coder.publicKeyToString(ControlSolution.publicKey);
			solution.sendMessage(KeyMsg.replyKey(key), con);
		} catch (Exception e) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		}

	}

	@Override
	public void processSharedKeyMessage(HashMap<String, String> messageMap,
			Connection con) {
		if (!KeyMsg.checkSharedKeyMsg(messageMap)) {
			solution.sendMessage(InvalidMsg.paramError(), con, true);
		} else {
			String key = messageMap.get(Constants.SHARED_KEY);
			try {
				SecretKey sharedKey = Coder.stringToSharedKey(key);
				con.setSharedKey(sharedKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
