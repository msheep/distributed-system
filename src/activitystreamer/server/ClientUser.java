package activitystreamer.server;

public class ClientUser extends Thread {
	private String username = "anonymous";
	private String password = null;
	// 0: request/lock; 1: valid; 2: Redirect 
	private int type = 0;
	private Connection con;
	
	public ClientUser(String username, String password, Connection con) {
		this.username = username;
		this.password = password;
		this.con = con;
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

	public Connection getConnection() {
		return con;
	}
}
