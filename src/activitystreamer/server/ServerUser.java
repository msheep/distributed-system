package activitystreamer.server;

import java.net.Socket;

public class ServerUser extends Thread {
	private String id = null;
	private String hostname = null;
	private int port = 0;
	private int load = 0;

	public ServerUser(String id, String hostname, int port, int load) {
		this.id = id;
		this.hostname = hostname;
		this.port = port;
		this.load = load;
	}

	public String getServerId() {
		return id;
	}
	
	public void setServerId(String id) {
		this.id = id;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getLoad() {
		return load;
	}

	public void setLoad(int load) {
		this.load = load;
	}

}
