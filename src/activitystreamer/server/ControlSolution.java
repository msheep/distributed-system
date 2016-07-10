package activitystreamer.server;

import java.io.IOException;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.util.*;


public class ControlSolution extends Control {
	private static final Logger log = LogManager.getLogger();
	private String localHostname = Settings.getLocalHostname();
	private int localPort = Settings.getLocalPort();
	private List<ServerUser> servers = new ArrayList<ServerUser>();
	private List<ClientUser> clients = new ArrayList<ClientUser>();
	private List<ClientUser> redirectedClients = new ArrayList<ClientUser>();
	private List<LockRequest> loginLockRequest = new ArrayList<LockRequest>();
	private List<LockRequest> registerLockRequest = new ArrayList<LockRequest>();
	private int myLoad;
	public static String myServerId = null;
	private PublicKey fatherPublicKey;
	protected static PublicKey publicKey;
	protected static PrivateKey privateKey;

	// since control and its subclasses are singleton, we get the singleton this
	// way
	public static ControlSolution getInstance() {
		if (control == null) {
			control = new ControlSolution();
		}
		return (ControlSolution) control;
	}

	public ControlSolution() {
		super();

		// check if we should initiate a connection
		myServerId = Settings.setServerId();
		log.info("myServerId: " + myServerId);

		// initial key pairs
		try {
			Map<String, Object> keyMap = Coder.genKeyPair();
			publicKey = (PublicKey) keyMap.get(Constants.PUBLIC_KEY);
			privateKey = (PrivateKey) keyMap.get(Constants.PRIVATE_KEY);
		} catch (Exception e) {
			log.error("Fail to generate the initial key pair");
		}

		initiateConnection();
		// start the server's activity loop
		// it will call doActivity every few seconds
		start();
	}

	/*
	 * a new incoming connection
	 */
	@Override
	public Connection incomingConnection(Socket s) throws IOException {
		Connection con = super.incomingConnection(s);
		return con;
	}

	/*
	 * a new outgoing connection
	 */
	@Override
	public Connection outgoingConnection(Socket s) throws IOException {
		Connection con = super.outgoingConnection(s);
		//request public key
		sendMessage(KeyMsg.requestKey(), con);
		con.setValid(true);
		con.setServer(true);
		return con;
	}

	/*
	 * the connection has been closed
	 */
	@Override
	public void connectionClosed(Connection con) {
		super.connectionClosed(con);

		if (redirectedClients.size() > 0) {
			for (ClientUser client : redirectedClients) {
				if (client.getConnection().equals(con)) {
					redirectedClients.remove(con);
				}
			}
		}
		con.closeCon();
	}

	/*
	 * process incoming msg, from connection con return true if the connection
	 * should be closed, false otherwise
	 */
	@Override
	public synchronized boolean process(Connection con, String msg) {
		MessageManager solution = new MessageManager(new MessageSolution());
		log.info("Received message: " + msg);
		solution.processMessage(msg, con);
		return false;
	}

	/*
	 * Called once every few seconds Return true if server should shut down,
	 * false otherwise
	 */
	@Override
	public boolean doActivity() {
		// Send the server announcements
		if (myServerId != null) {
			myLoad = getClientLoad();
			String announce = ServerAnnounceMsg.message(myServerId, myLoad, localHostname,
					localPort);
			sendBroadcastMessage(announce);
		}
		return false;
	}

	protected void sendBroadcastMessage(String content) {
		for (Connection connection : getConnections()) {
			if (connection.isValid() && connection.isServer()) {
				connection.writeMsg(content);
			}
		}
	}

	protected void sendBroadcastMessage(String content, Connection con) {
		for (Connection connection : getConnections()) {
			if (!connection.equals(con) && connection.isValid()
					&& connection.isServer()) {
				connection.writeMsg(content);
			}
		}
	}

	protected void sendMessage(String content, Connection con) {
		con.writeMsg(content);
	}

	protected void sendMessage(String content, Connection con, Boolean close) {
		con.writeMsg(content);
		if (con.isOpen()) {
			connectionClosed(con);
		}
	}

	protected synchronized void pushServerUser(String id, String hostname, int port,
			int load) {
		synchronized (servers) {
			ServerUser server = new ServerUser(id, hostname, port, load);
			servers.add(server);
		}
	}

	protected synchronized ClientUser addClient(String name, String secret,
			Connection con) {
		synchronized (clients) {
			ClientUser newUser = new ClientUser(name, secret, con);
			clients.add(newUser);
			return newUser;
		}
	}

	protected synchronized ClientUser addRedirectedClient(String name, String secret,
			Connection con) {
		synchronized (redirectedClients) {
			ClientUser newUser = new ClientUser(name, secret, con);
			redirectedClients.add(newUser);
			return newUser;
		}
	}

	protected synchronized void pushLockLoginRequestList(String name, String password,
			Connection con) {
		synchronized (loginLockRequest) {
			LockRequest request = new LockRequest(name, password, con, 1);
			loginLockRequest.add(request);
		}
	}

	protected synchronized void pushLockRegisterRequestList(String name, String password,
			Connection con) {
		synchronized (registerLockRequest) {
			LockRequest request = new LockRequest(name, password, con, 0);
			registerLockRequest.add(request);
		}
	}

	protected List<ServerUser> getSeverList() {
		return servers;
	}

	protected List<ClientUser> getClientList() {
		return clients;
	}

	protected int getClientLoad() {
		int load = 0;
		for (Connection connection : getConnections()) {
			if (!connection.isServer() && connection.isValid()) {
				load += 1;
			}
		}
		return load;
	}

	protected List<ClientUser> getRedirectedClientList() {
		return redirectedClients;
	}

	protected void removeClient(ClientUser client) {
		if (clients.contains(client)) {
			clients.remove(client);
		}
	}

	protected void removeRedirectedClient(ClientUser client) {
		if (redirectedClients.contains(client)) {
			redirectedClients.remove(client);
		}
	}

	protected List<LockRequest> getLoginLockRequestList() {
		return loginLockRequest;
	}

	protected void removeLoginLockRequestList(LockRequest request) {
		if (loginLockRequest.contains(request)) {
			loginLockRequest.remove(request);
		}
	}

	protected List<LockRequest> getRegisterLockRequestList() {
		return registerLockRequest;
	}

	protected void removeRegisterLockRequestList(LockRequest request) {
		if (registerLockRequest.contains(request)) {
			registerLockRequest.remove(request);
		}
	}

	protected String getMyServerId() {
		// TODO Auto-generated method stub
		return myServerId;
	}

	protected PublicKey getFatherPublicKey() {
		return fatherPublicKey;
	}

	protected void setFatherPublicKey(String key) {
		try {
			fatherPublicKey = Coder.stringToPublicKey(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
