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
		ClientSocket sock = null;


}
}
