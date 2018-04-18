/**
 * Broadcast class containing the logic for
 * acting as a Broadcast server.
 *
 * @author Cole Polychronis & Matt Gerber
 */

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;
import java.util.HashMap;

public class Broadcast implements Runnable{
	/**
	 * this method is invoked by a separate thread
	 */
   private ConcurrentHashMap<String, Socket> clientList;
   private Vector<JSONObject> messages;
   public void process(ConcurrentHashMap<String, Socket> clientList, Vector<JSONObject> messages) throws java.io.IOException{
	   System.out.println("I cant broadcast");
     while(true){
       try { Thread.sleep(100); } catch (InterruptedException ignore) { }
       while(!messages.isEmpty()){
         //Remove messages and figure out where to send them then send them
       }
     }

   }

   public Broadcast(ConcurrentHashMap<String, Socket> clientList, Vector<JSONObject> messages) {
     this.clientList = clientList;
     this.messages = messages;
 	}

  public void run() {
		try {
			process(clientList, messages);
		}
		catch (java.io.IOException ioe) {
			System.err.println(ioe);
		}
	}
}
