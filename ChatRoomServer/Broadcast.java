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
import java.util.*;

public class Broadcast implements Runnable{
  /**
  * this method is invoked by a separate thread
  */
  private ConcurrentHashMap<String, Socket> clientList;
  private Vector<JSONObject> messages;

  private void sendToAll(JSONObject message, Iterator it) throws java.io.IOException{
    BufferedOutputStream toClient = null;
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();

      toClient = new BufferedOutputStream(((Socket)pair.getValue()).getOutputStream());
      toClient.write(message.toString().getBytes());
      toClient.flush();
      //System.out.println(pair.getKey() + " = " + pair.getValue());
      it.remove(); // avoids a ConcurrentModificationException
    }
  }

  public void process(ConcurrentHashMap<String, Socket> clientList, Vector<JSONObject> messages) throws java.io.IOException{
    System.out.println("I cant broadcast");
    BufferedOutputStream toClient = null;
    JSONObject message = null;
    Iterator it = null;
    while(true){
      try { Thread.sleep(100); } catch (InterruptedException ignore) { }

      while(!messages.isEmpty()){
        //Remove messages and figure out where to send them then send them
        message = messages.remove(0);
        // Handles chatroom update
        if(message.get("type").equals("chatroom-update")){
          // iterate through all sockets and send
          it = clientList.entrySet().iterator();
          sendToAll(message, it);

        }
        // Handles send messages
        else if(message.get("type").equals("chatroom-broadcast")){

          it = clientList.entrySet().iterator();

          // If the message goes to everyone
          if(((String[])message.get("to")).length == 0){
            sendToAll(message, it);
          }
          else{
            //If the message is a direct message to certain people
            while (it.hasNext()) {
              Map.Entry pair = (Map.Entry)it.next();
              String[] toField = (String[]) message.get("to");

              // Sends the message to the from user
              if(message.get("from").equals(pair.getKey())){
                toClient = new BufferedOutputStream(((Socket)pair.getValue()).getOutputStream());
                toClient.write(message.toString().getBytes());
                toClient.flush();
              }
              else{
                // Also sends the the message to all users defined in the "to" field
                for(int i = 0; i < toField.length; i++){
                  if(toField[i].equals(pair.getKey())){
                    toClient = new BufferedOutputStream(((Socket)pair.getValue()).getOutputStream());
                    toClient.write(message.toString().getBytes());
                    toClient.flush();
                  }
                }
              }
              it.remove(); // avoids a ConcurrentModificationException
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
