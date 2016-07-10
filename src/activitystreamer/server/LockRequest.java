package activitystreamer.server;

import java.util.ArrayList;

public class LockRequest extends Thread {
	private String username = null;
	private String password = null;
	private ArrayList<String> allowedServerId = new ArrayList<>();
	private Connection con;
	// 0: register lock; 1: login lock
	private int type = 0;
	
	public LockRequest(String username, String password, Connection con, int type) {
		this.username = username;
		this.password = password;
		this.con = con;
		this.type = type;
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<String> getAllowedServerId() {
		return allowedServerId;
	}

	public void addAllowedServerId(String allowedServerId) {
		this.allowedServerId.add(allowedServerId);
	}

	public Connection getConnection() {
		return con;
	}

	public void setConnection(Connection con) {
		this.con = con;
	}
}
