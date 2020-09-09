package AvalonServer;

import org.java_websocket.WebSocket;

public class Player 
{
	String sessionID;
	String name;
	WebSocket socket;
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
}
