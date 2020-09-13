package AvalonServer;

import java.util.UUID;

import org.java_websocket.WebSocket;

import AvalonServer.GameRoom.Role;

public class Player 
{
	String sessionID;
	String publicSessionId;
	String name;
	WebSocket socket;
	Role myRole;
	GameRoom myRoom;
	static enum State
	{
		Disconnected, InMainMenu, InLobby, InGame
	}
	State s;
	public Player(String sess, WebSocket sock)
	{
		sessionID = sess;
		socket = sock;
		s = State.InMainMenu;
		publicSessionId = UUID.randomUUID().toString();
	}
	@Override
	public boolean equals(Object p) 
	{
		if(!(p instanceof Player)) { return false; }
		return this.sessionID.equals(((Player)p).sessionID);
	}
	void send(String message)
	{
		socket.send(message);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name+"("+sessionID+")";
	}
	void setRoom(GameRoom gr)
	{
		myRoom = gr;
	}
	void setName(String n)
	{
		name = n;
		myRoom.nameUpdated(this);
	}
}
