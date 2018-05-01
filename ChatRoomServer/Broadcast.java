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

public class Broadcast implements Runnable{
  /**
  * this method is invoked by a separate thread
  */
  private ConcurrentHashMap<String, Socket> clientList;
  private Vector<JSONObject> messages;

  private void sendToAll(JSONObject message, Iterator it) throws java.io.IOException{
	  PrintWriter toClient = null;
	 while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      System.out.println(pair.getValue());
      toClient = new PrintWriter(((Socket)pair.getValue()).getOutputStream(), true);
      toClient.println(message.toString());

      //toClient.flush();
      //System.out.println(pair.getKey() + " = " + pair.getValue());
    }
  }

  public void process(ConcurrentHashMap<String, Socket> clientList, Vector<JSONObject> messages) throws java.io.IOException{
    System.out.println("I cant broadcast");
    PrintWriter toClient = null;
    JSONObject message = null;
    Iterator it = null;
    while(true){
      try { Thread.sleep(100); } catch (InterruptedException ignore) { }

      while(!messages.isEmpty()){
        //Remove messages and figure out where to send them then send them
        message = messages.remove(0);
        System.out.println(message);
        it = clientList.entrySet().iterator();
        // Handles chatroom update
        if(message.get("type").toString().equals("chatroom-update")){
          // iterate through all sockets and send
          //it = clientList.entrySet().iterator();
          sendToAll(message, it);

        }
        // Handles send messages
        else if(message.get("type").toString().equals("chatroom-broadcast")){
        	System.out.println("Broadcasting");
          //it = clientList.entrySet().iterator();

          // If the message goes to everyone
          if(((String)message.get("to")).equals("[]")){
        	  System.out.println("To All");
        	  sendToAll(message, it);
          }
          else{
            //If the message is a direct message to certain people
            while (it.hasNext()) {
              Map.Entry pair = (Map.Entry)it.next();
              JSONArray toField = (JSONArray) message.get("to");

              // Sends the message to the from user
              if(message.get("from").toString().equals(pair.getKey())){
                toClient = new PrintWriter(((Socket)pair.getValue()).getOutputStream(), true);
                toClient.println(message.toString());
                //toClient.flush();
              }
              else{
                // Also sends the the message to all users defined in the "to" field
                for(int i = 0; i < toField.size(); i++){
                  if(((String)toField.get(i)).equals(pair.getKey())){
                    toClient = new PrintWriter(((Socket)pair.getValue()).getOutputStream(), true);
                    toClient.println(message.toString());
                    //toClient.flush();
                  }
                }
              }
            }
          }
        }
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
