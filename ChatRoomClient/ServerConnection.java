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

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ServerConnection implements Runnable{
  /**
  * this method is invoked by a separate thread
  */
  Socket sock;
  Vector<String> clientList;
  StringBuilder clientNameBuilder;
  JTextPane displayArea;
  JList list;


  public void process(Socket sock, Vector<String> clientList, StringBuilder clientNameBuilder, JTextPane displayArea, JList list) throws java.io.IOException{
    BufferedReader serverRead = null;	// the reader from the server
    try{
      serverRead = new BufferedReader(new InputStreamReader(sock.getInputStream()));

      while(true){
        if(serverRead.ready()){
          String serverResponse = serverRead.readLine();
          JSONObject dealio = new JSONObject((JSONObject)JSONValue.parse(serverResponse));
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
    if(dealio.get("type").toString().equals("chatroom-broadcast")){
      JSONArray toFriends = (JSONArray)dealio.get("to");
      if(toFriends.isEmpty()){
        addColoredText(displayArea, dealio.get("from").toString() + ": " + dealio.get("message").toString() + "\n", Color.BLACK);
      }else{
        String friends = "";
        for(int i=0; i<toFriends.size(); i++){
          if(i == 0){
            friends = friends.concat(toFriends.get(i).toString() + " ");
          }else if(i == toFriends.size()-1){
            friends = friends.concat("and " + (toFriends.get(i)));
          }else{
            friends = friends.concat(toFriends.get(i) + ", ");
          }

        }

        addColoredText(displayArea, "From " + dealio.get("from").toString() + " to " + friends + ": "  + dealio.get("message").toString() + "\n", Color.CYAN);
      }
    }else if(dealio.get("type").toString().equals("chatroom-update")){ // if we get an update message from the server
      if(dealio.get("type_of_update").equals("enter")){ // if someone has joined, add them to clientList
        clientList.add((String)dealio.get("id"));
        list.setListData(clientList);
        addColoredText(displayArea, "Client " + dealio.get("id") + " has joined the chatroom.\n", Color.MAGENTA);

      }
      if(dealio.get("type_of_update").toString().equals("leave")){ // if someone has left, remove them to clientList
        clientList.remove(dealio.get("id"));
        list.setListData(clientList);
        addColoredText(displayArea, "Client " + dealio.get("id") + " has left the chatroom.\n", Color.MAGENTA);

      }
    }else if(dealio.get("type").toString().equals("chatroom-response")){ // if this is the first response from the server, update our user id
      if(((Long)dealio.get("id")).intValue() == -1){
        addColoredText(displayArea, "Server is Full :(, try again later\n", Color.GREEN);

      }
      else{
      clientNameBuilder.append(":" + Integer.toString(((Long)dealio.get("id")).intValue()));
      JSONArray clients = (JSONArray)dealio.get("users");
      for(int i = 0; i < clients.size(); i++){
        clientList.add((String)clients.get(i));
      }
    }
    }

  }

  //Constructor for runnable method
  public ServerConnection(Socket sock, Vector<String> clientList, StringBuilder clientNameBuilder, JTextPane displayArea, JList list) {
    this.sock = sock;
    this.clientList = clientList;
    this.clientNameBuilder = clientNameBuilder;
    this.displayArea = displayArea;
    this.list = list;
  }

  public void run() {
    try {
      process(sock, clientList, clientNameBuilder, displayArea, list);
    }
    catch (java.io.IOException ioe) {
      System.err.println(ioe);
    }
  }

  public void addColoredText(JTextPane pane, String text, Color color) {
        StyledDocument doc = pane.getStyledDocument();

        Style style = pane.addStyle("Color Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), text, style);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
