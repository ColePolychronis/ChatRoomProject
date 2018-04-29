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
  private String clientName = null;
  private Socket sock;

  public void process(Socket sock, String clientName) throws java.io.IOException{
    PrintWriter toHost = null;
    BufferedReader localBin = null;
    try{
      localBin = new BufferedReader(new InputStreamReader(System.in));
      toHost = new PrintWriter(sock.getOutputStream(), true);
      String input;
      System.out.println("Please Enter Username");
      input = localBin.readLine();
      JSONObject beginJSON = new JSONObject();
      //Create username
      beginJSON.put("type", "chatroom-begin");
      beginJSON.put("username", input);
      beginJSON.put("len", input.length());
      clientName = input;
      toHost.println(beginJSON.toString());

      //Resolve error checking for more than 20 len username

      while(true){

        input = localBin.readLine();
        JSONObject messageJSON = new JSONObject();
        messageJSON.put("type", "chatroom-send");
        messageJSON.put("from", clientName);
        messageJSON.put("message", input);
        messageJSON.put("to", new String[0]);
        messageJSON.put("message-length", input.length());
        toHost.println(messageJSON.toString());
      }
    }
    catch(IOException ioe){

    }
    finally{
      localBin.close();
    }
  }

  //Constructor for runnable method
  public FromUser(Socket sock, String clientName) {
    this.sock = sock;
    this.clientName = clientName;
  }

  public void run() {
    try {
      process(sock, clientName);
    }
    catch (java.io.IOException ioe) {
      System.err.println(ioe);
    }
  }
}
