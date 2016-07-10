package activitystreamer.server;

import java.net.Socket;

public class Message {
	private String content = null;
	private Connection con = null;
	private Socket socket = null;
	private boolean close = false;

	public Message(String content, Connection con) {
		this.content = content;
		this.con = con;
		this.socket = con.getSocket();
	}
	
	public Message(String content, Connection con, boolean close) {
		this.content = content;
		this.con = con;
		this.socket = con.getSocket();
		this.close = close;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket s) {
		this.socket = s;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Connection getConnection() {
		return con;
	}

	public void setConnection(Connection con) {
		this.con = con;
	}
	
	public boolean getClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}
}
