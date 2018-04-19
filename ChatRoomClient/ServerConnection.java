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
  private ConcurrentHashMap<String, Socket> clientList;
  private Vector<JSONObject> messages;

  public void process() throws java.io.IOException{

  }

  //Constructor for runnable method
  public ServerConnection() {

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
