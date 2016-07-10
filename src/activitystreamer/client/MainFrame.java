package activitystreamer.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import activitystreamer.util.Settings;

public class MainFrame implements ActionListener {
	private JFrame frame;
	private JButton loginButton;
	private JButton registerButton;
	private JTextField userText;
	private JTextField hostnameText;
	private JTextField hostportText;
	private JPasswordField passwordText;
	private JButton anonymousButton;

	private ClientSolution clientSolution;

	public MainFrame() {
		frame = new JFrame("LOGIN");
		frame.setSize(450, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(null);

		JLabel remoteHostLabel = new JLabel("Host Name");
		remoteHostLabel.setBounds(10, 10, 80, 30);
		panel.add(remoteHostLabel);

		hostnameText = new JTextField(20);
		hostnameText.setText(Settings.getRemoteHostname());
		hostnameText.setBounds(100, 10, 160, 30);
		panel.add(hostnameText);

		JLabel portLabel = new JLabel("Port Number");
		portLabel.setBounds(10, 40, 80, 30);
		panel.add(portLabel);

		hostportText = new JTextField(20);
		hostportText.setText("" + Settings.getRemotePort());
		hostportText.setBounds(100, 40, 160, 30);
		panel.add(hostportText);

		JLabel userLabel = new JLabel("User Name");
		userLabel.setBounds(10, 70, 80, 30);
		panel.add(userLabel);

		userText = new JTextField(20);
		userText.setText(Settings.getUsername());
		userText.setBounds(100, 70, 160, 30);
		panel.add(userText);

		JLabel passwordLabel = new JLabel("Secret");
		passwordLabel.setBounds(10, 100, 80, 30);
		panel.add(passwordLabel);

		passwordText = new JPasswordField(20);
		passwordText.setText(Settings.getSecret());
		passwordText.setBounds(100, 100, 160, 30);
		panel.add(passwordText);

		loginButton = new JButton("Login");
		loginButton.setBounds(10, 130, 110, 30);
		panel.add(loginButton);
		loginButton.addActionListener(this);

		registerButton = new JButton("Register");
		registerButton.setBounds(140, 130, 110, 30);
		panel.add(registerButton);
		registerButton.addActionListener(this);

		anonymousButton = new JButton("Login as Anonymous");
		anonymousButton.setBounds(270, 130, 160, 30);
		panel.add(anonymousButton);
		anonymousButton.addActionListener(this);
		frame.setVisible(true);

		clientSolution = ClientSolution.getInstance();
	}

	public void actionPerformed(ActionEvent e) {
		if(clientSolution.fatherPublicKey == null){
			showMsg("Wait to receive the father public key!");
			return;
		}else{
			if (e.getSource() == loginButton) {
				if (passwordText.getPassword().length == 0 || userText.getText().isEmpty()
						|| hostportText.getText().isEmpty()
						|| hostnameText.getText().isEmpty()) {
					showMsg("Please fill all the forms!");
					return;
				} else {
					Settings.setUsername(userText.getText());
					Settings.setSecret(new String(passwordText.getPassword()));
					Settings.setRemoteHostname(hostnameText.getText());
					Settings.setRemotePort(Integer.parseInt(hostportText.getText()));
					clientSolution.sendLoginMsg();
				}
			} else if (e.getSource() == registerButton) {
				if (passwordText.getPassword().length == 0 || userText.getText().isEmpty()
						|| hostportText.getText().isEmpty()
						|| hostnameText.getText().isEmpty()) {
					showMsg("Please fill all the forms!");
					return;
				} else {
					Settings.setUsername(userText.getText());
					Settings.setSecret(new String(passwordText.getPassword()));
					Settings.setRemoteHostname(hostnameText.getText());
					Settings.setRemotePort(Integer.parseInt(hostportText.getText()));
					clientSolution.sendRegisterMsg();
				}

			} else if (e.getSource() == anonymousButton) {
				clientSolution.sendInitialLoginMsg();
			}
		}
		
	}

	public void showMsg(String error) {
		JOptionPane.showMessageDialog(null, error, "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void hide() {
		frame.setVisible(false);
	}

	public void dispose() {
		frame.setVisible(false);
		frame.dispose();
	}
}
