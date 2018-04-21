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

public class FromUser implements Runnable{
  /**
  * this method is invoked by a separate thread
  */
  private Vector<JSONObject> toServer = new Vector<JSONObject>();
  private String clientName = null;

  public void process(Vector<JSONObject> toServer, String clientName) throws java.io.IOException{
    BufferedReader localBin = null;
    try{
      localBin = new BufferedReader(new InputStreamReader(System.in));
      String input;
      System.out.println("Please Enter Username");
      input = localBin.readLine();
      JSONObject beginJSON = new JSONObject();
      //Create username
      beginJSON.put("type", "chatroom-begin");
      beginJSON.put("id", input);
      beginJSON.put("len", input.length());
      toServer.add(beginJSON);
      clientName = input;
      //Resolve error checking for more than 20 len username

      while(true){

        input = localBin.readLine();
        JSONObject messageJSON = new JSONObject();
        messageJSON.put("type", "chatroom-send");
        messageJSON.put("from", clientName);
        messageJSON.put("message", input);
        messageJSON.put("message-length", input.length());
        toServer.add(messageJSON);
      }
    }
    catch(IOException ioe){

    }
    finally{
      localBin.close();
    }
  }

  //Constructor for runnable method
  public FromUser(Vector<JSONObject> toServer, String clientName) {
    this.toServer = toServer;
    this.clientName = clientName;
  }

  public void run() {
    try {
      process(toServer, clientName);
    }
    catch (java.io.IOException ioe) {
      System.err.println(ioe);
    }
  }
}
