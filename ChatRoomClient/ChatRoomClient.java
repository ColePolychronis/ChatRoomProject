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
import org.json.simple.*;
import java.util.Vector;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ChatRoomClient extends JFrame implements ActionListener, KeyListener
{
	private JButton sendButton;
	private JButton exitButton;
	private JTextField sendText;
	private JTextPane displayArea;
	private JScrollPane spane;
	private Socket sock = null;
	private Vector<String> clientList = new Vector<String>();
	private String clientName = null;
	private static String ipVal = null;
	private PrintWriter toHost = null;
	private JList list;

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
		displayArea = new JTextPane();
		displayArea.setEditable(false);
		displayArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JScrollPane scrollPane = new JScrollPane(displayArea);
		getContentPane().add(scrollPane,"Center");

		list = new JList();
		spane = new JScrollPane();
		spane.getViewport().add(list);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		getContentPane().add(spane,"East");
		/**
		 * set the title and size of the frame
		 */
		setTitle("");
		JButton button = new JButton();

		button.setText("Connect to new Server");
		p.add(button);
		button.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				connectToServer(p);
			}

		});

		pack();

		setVisible(true);

		sendText.requestFocus();

		/** anonymous inner class to handle window closing events */
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if(sock != null){
					JSONObject endJSON = new JSONObject();
					//Create username
					endJSON.put("type", "chatroom-end");
					endJSON.put("id", clientName);
					toHost.println(endJSON.toString());
					try {
						sock.close();
					} catch (IOException e) {
					}
				}
				System.exit(0);
			}
		} );
		connectToServer(p);
	}


	/**
	 * This method responds to action events .... i.e. button clicks
	 * and fulfills the contract of the ActionListener interface.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source == sendButton)
		 	sendMessage();
		else if (source == exitButton){
			if(sock != null){
				JSONObject endJSON = new JSONObject();
				//Create username
				endJSON.put("type", "chatroom-end");
				endJSON.put("id", clientName);
				toHost.println(endJSON.toString());
				try {
					sock.close();
				} catch (IOException e) {
				}
			}
			System.exit(0);
		}
	}

	public void connectToServer(JPanel p){
		ipVal = JOptionPane.showInputDialog(p, "Enter the Server IP:", null);

		try{
			sock = new Socket(ipVal, DEFAULT_PORT);
			toHost = new PrintWriter(sock.getOutputStream(), true);
			System.out.println("Connected");

			//Runnable fromUser = new FromUser(sock, clientName);
			//exec.execute(fromUser);
			clientName = JOptionPane.showInputDialog(p, "Enter Your Username:", null);
			JSONObject beginJSON = new JSONObject();
			//Create username
			beginJSON.put("type", "chatroom-begin");
			beginJSON.put("username", clientName);
			beginJSON.put("len", clientName.length());
			toHost.println(beginJSON.toString());

			Runnable serverConnection = new ServerConnection(sock, clientList, clientName, displayArea, list);
			exec.execute(serverConnection);

		}
		catch(IOException ioe){

		}
		finally{
//			if(sock != null){
//				JSONObject endJSON = new JSONObject();
//				//Create username
//				endJSON.put("type", "chatroom-end");
//				endJSON.put("id", clientName);
//				toHost.println(endJSON.toString());
//
//				try {
//					sock.close();
//				} catch (IOException e) {
//				}
//			}
		}
	}

	/**
	 * These methods responds to keystroke events and fulfills
	 * the contract of the KeyListener interface.
	 */
	 public void sendMessage(){
		 String input = sendText.getText().trim();
		 JSONObject messageJSON = new JSONObject();
		 messageJSON.put("type", "chatroom-send");
		 messageJSON.put("from", clientName);
		 messageJSON.put("message", input);
		 ArrayList<String> selection = new ArrayList(list.getSelectedValuesList());
		 if(selection.isEmpty()){
			 messageJSON.put("to", new JSONArray());
		 }
		 else{
			 String[] selectionArray = selection.toArray(new String[selection.size()]);
			 JSONArray recipients = new JSONArray();
			 for(int i = 0; i < selectionArray.length; i ++){
				 recipients.add(selectionArray[i]);
			 }
			 messageJSON.put("to", recipients);
		 }
		 messageJSON.put("message-length", input.length());
		 toHost.println(messageJSON.toString());

		 sendText.setText("");
     sendText.requestFocus();
	 }
	/**
	 * This is invoked when the user presses
	 * the ENTER key.
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			sendMessage();
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
