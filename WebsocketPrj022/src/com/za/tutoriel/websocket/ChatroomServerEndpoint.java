package com.za.tutoriel.websocket;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chatroomserverendpoint")
public class ChatroomServerEndpoint {
	static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void handleOpen(Session userSession) {
		chatroomUsers.add(userSession);
	}
	@OnMessage
	public void handleMessage(String message, Session userSession) throws IOException {
		String userName= (String) userSession.getUserProperties().get("username");
		  if(userName==null) {
			  userSession.getUserProperties().put("username", message);
			  userSession.getBasicRemote().sendText(buildJsonData("System","Vous êtes connecté en tant que "+message));
		  }
		  else {
			  Iterator<Session> iterator =chatroomUsers.iterator();
			  while(iterator.hasNext()) iterator.next().getBasicRemote().sendText(buildJsonData(userName, message));
		  }
	}
	
	private String buildJsonData(String username, String message) {
		JsonObject  JsonObject = Json.createObjectBuilder().add("message",username+": "+message).build();
		StringWriter stringWriter =new StringWriter();
		try(JsonWriter jsonWriter = Json.createWriter(stringWriter)){jsonWriter.write(JsonObject);}
         return stringWriter.toString();
	}
	
	@OnClose
	public void handleClose(Session userSession) {
		chatroomUsers.remove(userSession);
	}

}
