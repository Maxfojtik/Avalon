package AvalonServer;

import org.java_websocket.WebSocket;

public class Player 
{
	String sessionID;
	String name;
	WebSocket socket;
	Role myRole;
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
}
