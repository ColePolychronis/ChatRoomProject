/**
 * This is the separate thread that services each
 * incoming echo client request.
 *
 * @author Cole Polychronis & Matt Gerber
 */

import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;
import java.util.Vector;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Connection implements Runnable {
	private Socket	client;
	private static Handler handler = new Handler();
	private ConcurrentHashMap<String, Socket> clientList;
	private Vector<JSONObject> messages;
	private Vector<Integer> freeIDs;

	public Connection(Socket client, ConcurrentHashMap<String, Socket> clientList, Vector<JSONObject> messages, Vector<Integer> freeIDs) {
		this.client = client;
		this.clientList = clientList;
		this.messages = messages;
		this.freeIDs = freeIDs;
	}

    /**
     * This method runs in a separate thread.
     */
	public void run() {
		try {
			handler.process(client, clientList, messages, freeIDs);
		}
		catch (java.io.IOException ioe) {
			System.err.println(ioe);
		}
	}
}
