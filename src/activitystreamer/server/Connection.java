package activitystreamer.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.util.Coder;
import activitystreamer.util.Settings;

public class Connection extends Thread {
	private static final Logger log = LogManager.getLogger();
	private DataInputStream in;
	private DataOutputStream out;
	private BufferedReader inreader;
	private PrintWriter outwriter;
	private boolean open = false;
	private Socket socket;
	private boolean term = false;
	private boolean valid = false;
	private boolean server = false;
	private SecretKey sharedKey = null;
	private PublicKey publicKey;

	Connection(Socket socket) throws IOException {
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		inreader = new BufferedReader(new InputStreamReader(in));
		outwriter = new PrintWriter(out, true);
		this.socket = socket;
		open = true;
		start();
	}

	/*
	 * returns true if the message was written, otherwise false
	 */
	public boolean writeMsg(String msg) {
		if (open) {
			try {
				log.info("sending message:" + msg + "++++++++++++++");
				if (sharedKey != null && !msg.contains("SHARED_KEY")
						&& !msg.contains("LOGIN") && !msg.contains("REGISTER")) {
					msg = Coder.encryptBySharedKey(msg, sharedKey);
				}
				log.info("sending message:" + msg);
				outwriter.println(msg);
				outwriter.flush();
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}

	public void closeCon() {
		if (open) {
			log.info("closing connection " + Settings.socketAddress(socket));
			try {
				term = true;
				inreader.close();
				out.close();
			} catch (IOException e) {
				// already closed?
				log.error("received exception closing the connection "
						+ Settings.socketAddress(socket) + ": " + e);
			}
		}
	}

	public void run() {
		try {
			String data;
			while (!term && (data = inreader.readLine()) != null) {
				if (sharedKey != null && !data.contains("{")) {
					data = Coder.decryptBySharedKey(data, sharedKey);
				}
				term = Control.getInstance().process(this, data);
			}
		} catch (IOException e) {
			log.info("connection " + Settings.socketAddress(socket) + " closed");
			Control.getInstance().connectionClosed(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		open = false;
	}

	public Socket getSocket() {
		return socket;
	}

	public boolean isOpen() {
		return open;
	}

	public final void setTerm(boolean t) {
		term = t;
	}

	public void setValid(boolean v) {
		valid = v;
	}

	public boolean isValid() {
		return valid;
	}

	public void setServer(boolean s) {
		server = true;
	}

	public boolean isServer() {
		return server;
	}

	public SecretKey getSharedKey() {
		return sharedKey;
	}

	public void setSharedKey(SecretKey key) {
		this.sharedKey = key;
	}

	protected void setPublicKey(String key) {
		try {
			publicKey = Coder.stringToPublicKey(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
