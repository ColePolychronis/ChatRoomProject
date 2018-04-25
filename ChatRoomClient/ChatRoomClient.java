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

public class  ChatRoomClient
{
	public static final int DEFAULT_PORT = 8029;

    // construct a thread pool for concurrency
	private static final Executor exec = Executors.newCachedThreadPool();

	public static void main(String[] args) throws IOException {
	Socket sock = null;
    Vector<String> clientList = new Vector<String>();
    String clientName = null;

    BufferedReader localBin = null;

    try{
      localBin = new BufferedReader(new InputStreamReader(System.in));
      boolean done = false;
      String serverIP = localBin.readLine();
      localBin.close();

      sock = new Socket(serverIP, DEFAULT_PORT);
      Runnable fromUser = new FromUser(sock, clientName);
      exec.execute(fromUser);
      Runnable serverConnection = new ServerConnection(sock, clientList, clientName);
      exec.execute(serverConnection);

    }
    catch(IOException ioe){

    }

}
}
