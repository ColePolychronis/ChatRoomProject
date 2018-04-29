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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ServerConnection implements Runnable{
  /**
  * this method is invoked by a separate thread
  */
  // private ConcurrentHashMap<String, Socket> clientList;
  // private Vector<JSONObject> messages;
  Socket sock;
  Vector<String> clientList;
  String clientName;
  JTextArea displayArea;


  public void process(Socket sock, Vector<String> clientList, String clientName, JTextArea displayArea) throws java.io.IOException{
    BufferedReader serverRead = null;	// the reader from the server
    try{
      serverRead = new BufferedReader(new InputStreamReader(sock.getInputStream()));

      while(true){
        if(serverRead.ready()){

          String serverResponse = serverRead.readLine();
          System.out.println("Stuff from server has been read: " + serverResponse.toString());
          JSONObject dealio = new JSONObject((JSONObject)JSONValue.parse(serverResponse));
          System.out.println("JSON object created");
          processDealio(dealio);
        }
      }
    }catch(IOException e){
			System.out.println(e);
		}
		finally {
      if (serverRead != null)
			serverRead.close();
    }
  }

  /**
  * This gets the text the user entered and outputs it
  * in the display area.
  */
  public void processDealio(JSONObject dealio) {
	  System.out.println("Process dealio called");
    if(dealio.get("type").toString().equals("chatroom-broadcast")){
      displayArea.append(dealio.get("from").toString() + ": " + dealio.get("message").toString() + "\n");
      System.out.println(dealio.get("message").toString());
    }else if(dealio.get("type").toString().equals("chatroom-update")){ // if we get an update message from the server
      if(dealio.get("type_of_update").equals("enter")){ // if someone has joined, add them to clientList
        clientList.add((String)dealio.get("id"));
        displayArea.append("Client " + dealio.get("id") + " has joined the chatroom.\n");
      }
      if(dealio.get("type_of_update").toString().equals("leave")){ // if someone has left, remove them to clientList
        clientList.remove(dealio.get("id"));
        displayArea.append("Client " + dealio.get("id") + " has left the chatroom.\n");
      }
    }else if(dealio.get("type").toString().equals("chatroom-response")){ // if this is the first response from the server, update our user id
      clientName = clientName.concat(":").concat(dealio.get("id").toString());
      // System.out.println("The client id has been updated to " + clientName);
    }

  }

  //Constructor for runnable method
  public ServerConnection(Socket sock, Vector<String> clientList, String clientName, JTextArea displayArea) {
    this.sock = sock;
    this.clientList = clientList;
    this.clientName = clientName;
    this.displayArea = displayArea;
  }

  public void run() {
    try {
      process(sock, clientList, clientName, displayArea);
    }
    catch (java.io.IOException ioe) {
      System.err.println(ioe);
    }
  }
}
