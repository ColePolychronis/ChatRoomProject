/**
 * A chatroom server that runs on port 8029
 *
 * This services each request in a separate thread.
 *
 * This conforms to RFC "Whats The Dealio" for chatroom servers.
 *
 * @author Cole Polychronis & Matt Gerber
 */

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import org.json.simple.JSONObject;
import java.util.Vector;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class  ChatRoomServer
{
	public static final int DEFAULT_PORT = 8029;
	public static final int maxUsers = 15; // maximum amount of users that can occupy chatroom at a time

    // construct a thread pool for concurrency
	private static final Executor exec = Executors.newCachedThreadPool();

	public static void main(String[] args) throws IOException {
		ServerSocket sock = null;
		ConcurrentHashMap<String, Socket> clientList = new ConcurrentHashMap<String, Socket>(maxUsers);
		Vector<JSONObject> messages = new Vector<JSONObject>();
		Vector<Integer> freeIDs = new Vector<Integer>();
		Runnable broadcast = new Broadcast(clientList, messages);
		exec.execute(broadcast);

		try {
			for(int i=0; i < maxUsers; i++){
				freeIDs.add((Integer)i);
			}
			// establish the socket
			sock = new ServerSocket(DEFAULT_PORT);

			while (true) {
				/**
				 * now listen for connections
				 * and service the connection in a separate thread.
				 */
				Runnable task = new Connection(sock.accept(), clientList, messages, freeIDs);
				exec.execute(task);
			}
		}
		catch (IOException ioe) { }
		finally {
			if (sock != null)
				sock.close();
		}
	}
}
