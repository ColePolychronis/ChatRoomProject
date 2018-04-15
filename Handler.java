/**
 * Handler class containing the logic for
 * acting as a Proxy Server.
 *
 * @author Cole Polychronis & Matt Gerber
 */

import java.io.*;
import java.net.*;
import org.json.simple.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Handler {

	/**
	 * this method is invoked by a separate thread
	 */
	public void process(Socket client, ConcurrentHashMap<String, Socket> clientList, Vector<JSONObject> messages, Vector<Integer> freeIDs) throws java.io.IOException {
		BufferedReader fromClient = null;
		BufferedOutputStream toClient = null;

		try {
      fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

      // read in the dealio request
      String requestLine = fromClient.readLine();
      //String requestLine2 = "{\"username\":\"Matt\", \"len\":4}";
			JSONObject requestJSON = new JSONObject((JSONObject)JSONValue.parse(requestLine));
			JSONObject responseJSON = new JSONObject();
			if(((Long)requestJSON.get("len")).intValue() <= 20){
			if(!freeIDs.isEmpty()){
				responseJSON.put("id", freeIDs.remove(0));
				responseJSON.put("clientNo", clientList.size());
				responseJSON.put("users", clientList.keySet().toArray());
			}
			else{
				responseJSON.put("id", new Integer(-1));
				responseJSON.put("clientNo", clientList.size());
				responseJSON.put("users", clientList.keySet().toArray());
			}
		}
		else{
			//send error dealio
		}
		toClient = new BufferedOutputStream(client.getOutputStream());
		toClient.write(responseJSON.toString().getBytes());
		toClient.flush();

		}catch(IOException e){
			System.out.println(e);
		}
		finally {
      // close streams and socket
      if (fromClient != null)
      fromClient.close();
      if (toClient != null)
      toClient.close();
    }
		// add username:id, client socket to clientList
		System.out.println("Success!");


	}
}
