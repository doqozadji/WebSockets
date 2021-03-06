package com.za.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/chatserverEndpoint",encoders= {ChatMessageEncoder.class},decoders= {ChatMessageDecoder.class})
public class ChatServerEndpoint {
	static Set<Session> chatroomUsers= Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void handleOpen(Session userSession) {
		chatroomUsers.add(userSession);
	}
	
	@OnClose
	public void handleClose(Session sessionUser) {
		chatroomUsers.remove(sessionUser);
	}
	@OnMessage
	public void handleMessage(ChatMessage incomingChatMessage,Session userSession) throws IOException, EncodeException {
		 String username=(String) userSession.getUserProperties().get("username");
		 ChatMessage outgoingChatMessage = new ChatMessage();
		 if(username==null) {
			 userSession.getUserProperties().put("username", incomingChatMessage.getMessage());
			 outgoingChatMessage.setName("System");
			 outgoingChatMessage.setMessage("Vous êtes connecté en tant que "+ incomingChatMessage.getMessage());
			 userSession.getBasicRemote().sendObject(outgoingChatMessage);
		 }
		 else {
			 outgoingChatMessage.setName(username);
			 outgoingChatMessage.setMessage(incomingChatMessage.getMessage());
			 Iterator<Session> iterator =chatroomUsers.iterator();
			  while(iterator.hasNext()) iterator.next().getBasicRemote().sendObject(outgoingChatMessage);
		 }
		
	}

}
