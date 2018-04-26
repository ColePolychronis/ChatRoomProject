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
import org.json.simple.*;
import java.util.*;

public class ServerConnection implements Runnable{
  /**
  * this method is invoked by a separate thread
  */
  // private ConcurrentHashMap<String, Socket> clientList;
  // private Vector<JSONObject> messages;
  Socket sock;
  Vector<String> clientList;
  String clientName;


  public void process(Socket sock, Vector<String> clientList, String clientName) throws java.io.IOException{
    BufferedReader serverRead = null;	// the reader from the server
    try{
      serverRead = new BufferedReader(new InputStreamReader(sock.getInputStream()));

      while(true){
        if(serverRead.ready()){
          String serverResponse = serverRead.readLine();
          JSONObject dealio = new JSONObject((JSONObject)JSONValue.parse(serverResponse));
          if(dealio.get("type").equals("chatroom-update")){ // if we get an update message from the server
            if(dealio.get("type_of_update").equals("enter")){ // if someone has joined, add them to clientList
              clientList.add((String)dealio.get("id"));
              System.out.println("Client " + dealio.get("id") + " has joined the chatroom.");
            }
            if(dealio.get("type_of_update").equals("leave")){ // if someone has left, remove them to clientList
              clientList.remove(dealio.get("id"));
              System.out.println("Client " + dealio.get("id") + " has left the chatroom.");
            }
          }

        if(dealio.get("type").equals("chatroom-response")){ // if this is the first response from the server, update our user id
          clientName = clientName.concat(":").concat((String)dealio.get("id"));
          System.out.println("The client id has been updated to " + clientName);
        }

        System.out.println(serverResponse);
        }
        try { Thread.sleep(1000); } catch (InterruptedException ignore) { }
      }
    }catch(IOException e){
			System.out.println(e);
		}
		finally {
      if (serverRead != null)
			serverRead.close();
    }
  }

  //Constructor for runnable method
  public ServerConnection(Socket sock, Vector<String> clientList, String clientName) {
    this.sock = sock;
    this.clientList = clientList;
    this.clientName = clientName;
  }

  public void run() {
    try {
      process(sock, clientList, clientName);
    }
    catch (java.io.IOException ioe) {
      System.err.println(ioe);
    }
  }
}
