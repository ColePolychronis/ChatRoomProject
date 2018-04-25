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
  Vector<JSONObject> toServer;
  Vector<JSONObject> fromServer;
  String clientName;


  public void processOutbound(Vector<String> clientList, Vector<JSONObject> messages) throws java.io.IOException{
    while(!messages.isEmpty()){
      JSONObject messToServer = messages.remove(0);
      // set up the necessary communication channels
			PrintWriter writeToServer = new PrintWriter(this.sock.getOutputStream(),true);
      writeToServer(messToServer.toString());
    }
  }

  public void processInbound(Vector<String> clientList, Vector<JSONObject> messages) throws java.io.IOException{
    while(!messages.isEmpty()){
      JSONObject messFromServer = messages.remove(0);
      if(messFromServer.get("type").equals("chatroom-update")){ // if we get an update message from the server
        if(messFromServer.get("type_of_update").equals("enter")){ // if someone has joined, add them to clientList
          clientList.add(messFromServer.get("id"));
          System.out.println("Client " + messFromServer.get("id") + " has joined the chatroom.");
        }
        if(messFromServer.get("type_of_update").equals("leave")){ // if someone has left, remove them to clientList
          clientList.remove(messFromServer.get("id"));
          System.out.println("Client " + messFromServer.get("id") + " has left the chatroom.");
        }
      }
      if(messFromServer.get("type").equals("chatroom-response")){
        clientName = clientName.concat(":").concat(messFromServer.get("id"));
      }
      // set up the necessary communication channels
			System.out.println(messFromServer.toString());
    }
  }

  //Constructor for runnable method
  public ServerConnection(Socket sock, Vector<String> clientList, Vector<JSONObject> toServer, Vector<JSONObject> fromServer, String clientName) {
    this.sock = sock;
    this.clientList = clientList;
    this.toServer = toServer;
    this.fromServer = fromServer;
    this.clientName = clientName;
  }

  public void run() {
    try {
      while(true){
        processOutbound(toServer);
        processInbound(fromServer);
      }
    }
    catch (java.io.IOException ioe) {
      System.err.println(ioe);
    }
  }
}
