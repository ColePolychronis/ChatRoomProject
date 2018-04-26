/**
 * A chatroom server that runs on port 8029
 *
 * This services each request in a separate thread.
 *
 * This conforms to RFC "Whats The Dealio" for chatroom servers.
 *
 * @author Cole Polychronis & Matt Gerber
 */

/**
 * This program is a rudimentary demonstration of Swing GUI programming.
 * Note, the default layout manager for JFrames is the border layout. This
 * enables us to position containers using the coordinates South and Center.
 *
 * Usage:
 *	java ChatScreen
 *
 * When the user enters text in the textfield, it is displayed backwards
 * in the display area.
 */

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import org.json.simple.JSONObject;
import java.util.Vector;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ChatRoomClient extends JFrame implements ActionListener, KeyListener
{
	private JButton sendButton;
	private JButton exitButton;
	private JTextField sendText;
	private JTextArea displayArea;
	private Socket sock = null;
	private Vector<String> clientList = new Vector<String>();
	private String clientName = null;
	private static String ipVal = null;

	public static final int DEFAULT_PORT = 8029;

	// construct a thread pool for concurrency
	private static final Executor exec = Executors.newCachedThreadPool();

	public ChatRoomClient() {
		/**
		 * a panel used for placing components
		 */
		JPanel p = new JPanel();


		Border etched = BorderFactory.createEtchedBorder();
		Border titled = BorderFactory.createTitledBorder(etched, "Enter Message Here ...");
		p.setBorder(titled);

		/**
		 * set up all the components
		 */
		sendText = new JTextField(30);
		sendButton = new JButton("Send");
		exitButton = new JButton("Exit");

		/**
		 * register the listeners for the different button clicks
		 */
		sendText.addKeyListener(this);
		sendButton.addActionListener(this);
		exitButton.addActionListener(this);

		/**
		 * add the components to the panel
		 */
		p.add(sendText);
		p.add(sendButton);
		p.add(exitButton);

		/**
		 * add the panel to the "south" end of the container
		 */
		getContentPane().add(p,"South");

		/**
		 * add the text area for displaying output. Associate
		 * a scrollbar with this text area. Note we add the scrollpane
		 * to the container, not the text area
		 */
		displayArea = new JTextArea(15,40);
		displayArea.setEditable(false);
		displayArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JScrollPane scrollPane = new JScrollPane(displayArea);
		getContentPane().add(scrollPane,"Center");

		/**
		 * set the title and size of the frame
		 */
		setTitle("GUI Demo");
		JButton button = new JButton();

		button.setText("Click me to show dialog!");
		p.add(button);
		button.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ipVal = JOptionPane.showInputDialog(p,
						"Enter the Server IP:", null);
				try{

					sock = new Socket(ipVal, DEFAULT_PORT);
					System.out.println("Connected");
					Runnable fromUser = new FromUser(sock, clientName);
					exec.execute(fromUser);
					Runnable serverConnection = new ServerConnection(sock, clientList, clientName, displayArea;);
					exec.execute(serverConnection);

				}
				catch(IOException ioe){

				}
			}

		});
		pack();

		setVisible(true);
		sendText.requestFocus();

		/** anonymous inner class to handle window closing events */
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		} );

	}

	/**
	 * This gets the text the user entered and outputs it
	 * in the display area.
	 */
	public void displayText() {
		String message = sendText.getText().trim();
		StringBuffer buffer = new StringBuffer(message.length());

		// now reverse it
		for (int i = message.length()-1; i >= 0; i--)
			buffer.append(message.charAt(i));

		displayArea.append(buffer.toString() + "\n");

		sendText.setText("");
		sendText.requestFocus();
	}


	/**
	 * This method responds to action events .... i.e. button clicks
	 * and fulfills the contract of the ActionListener interface.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source == sendButton)
			displayText();
		else if (source == exitButton)
			System.exit(0);
	}

	/**
	 * These methods responds to keystroke events and fulfills
	 * the contract of the KeyListener interface.
	 */

	/**
	 * This is invoked when the user presses
	 * the ENTER key.
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			displayText();
	}

	/** Not implemented */
	public void keyReleased(KeyEvent e) { }

	/** Not implemented */
	public void keyTyped(KeyEvent e) {  }


	public static void main(String[] args) {
		JFrame win = new ChatRoomClient();

		//Socket sock = null;
		//Vector<String> clientList = new Vector<String>();
		//String clientName = null;

	}
}
