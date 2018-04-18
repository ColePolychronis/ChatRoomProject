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
				responseJSON.put("type", "chatroom-response");
				if(!freeIDs.isEmpty()){
					Integer freeId = freeIDs.remove(0);
					responseJSON.put("id", freeId);
					responseJSON.put("clientNo", clientList.size());
					responseJSON.put("users", clientList.keySet().toArray());
					// add new user to clientList
					String userKey = requestJSON.get("username") + ":" + freeId;
					clientList.put(userKey, client);
				}
				else{
					responseJSON.put("id", new Integer(-1));
					responseJSON.put("clientNo", clientList.size());
					responseJSON.put("users", clientList.keySet().toArray());
				}
			}
			else{
				//If username is too long
				responseJSON.put("type", "chatroom-error");
				String[] errorType = {"user_name_length_exceeded"};
				responseJSON.put("type_of_error", errorType);
			}
			toClient = new BufferedOutputStream(client.getOutputStream());
			toClient.write(responseJSON.toString().getBytes());
			toClient.flush();

			//While loop which waits for input from the user

			while(true){
				if(fromClient.ready()){
					String nextRequest = fromClient.readLine();
					System.out.println(nextRequest);
					JSONObject nextReqJSON = new JSONObject((JSONObject)JSONValue.parse(nextRequest));
					String reqType = nextReqJSON.get("type").toString();

					if(reqType.equals("chatroom-send")){ // client wishes to post to chatroom
						broadcast(nextReqJSON, messages);
					}else if(reqType.equals("chatroom-end")){ // client wishes to leave chatroom
						disconnect(nextReqJSON, clientList, toClient, freeIDs);
					}else if(reqType.equals("chatroom-special")){ // client wishes to send media message - unsupported on this server
						returnError(nextReqJSON, toClient);
					}else{ // client sends an invalid request
						returnError(nextReqJSON, toClient);
					}
				}
				try { Thread.sleep(1000); } catch (InterruptedException ignore) { }

			}

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

	//Methods to handle server response to client
	private static void broadcast(JSONObject request, Vector<JSONObject> messages){
		String from = (String) request.get("from");
		JSONArray to = (JSONArray)request.get("to");
		String mess = (String) request.get("message");
		Long len = (Long)request.get("message-len");

		JSONObject broadcastJSON = new JSONObject();
		broadcastJSON.put("type", "chatroom-broadcast");
		broadcastJSON.put("from", from);
		broadcastJSON.put("to", to);
		broadcastJSON.put("message", mess);
		broadcastJSON.put("len", len);
		// broadcast thread stuff
		messages.add(broadcastJSON);
	}

	private static void returnError(JSONObject request, BufferedOutputStream toClient)throws java.io.IOException{
		String from = request.get("from").toString();
		String type = request.get("type").toString();

		JSONObject errorJSON = new JSONObject();
		errorJSON.put("type", "chatroom-error");
		errorJSON.put("id", from);
		if(type.equals("chatroom-special"))
			errorJSON.put("type_of_error", "special_unsupported");
		else
			errorJSON.put("type_of_error", "malformed_dealio");
		// send out errorJSON
		toClient.write(errorJSON.toString().getBytes());
		toClient.flush();
	}

	private static void disconnect(JSONObject request, ConcurrentHashMap<String, Socket> clientList, BufferedOutputStream toClient, Vector<Integer> freeIDs)throws java.io.IOException{
		String id = request.get("id").toString();
		freeIDs.add(Integer.valueOf(id.substring(id.indexOf(":") + 1)));
		clientList.remove(id);

		// call update dealio
	}


}
