package AvalonServer;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

class Websockets extends WebSocketServer {
	public Websockets() {
		super(new InetSocketAddress(12389));
	}
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("Welcome to the server!"); //This method sends a message to the new client
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " disconnected");
		Player dcedPlayer = AvalonServer.getPlayerByWebsocket(conn);
		AvalonServer.playerDisconnected(dcedPlayer);
	}

	@Override
	public void onMessage(WebSocket conn, String message) 
	{
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message);
		String[] params = message.split("\\|");
		if(params[0].equals("CreateGame"))
		{
			System.out.println("Creating a game");
			GameRoom room = new GameRoom();
			AvalonServer.gameRooms.add(room);
		}
		if(params[0].equals("JoinGame"))
		{
			System.out.println(params[1]+" joining "+params[2]);
			Player thatPlayer = AvalonServer.getPlayerById(params[1]);
			if(thatPlayer!=null)
			{
				GameRoom room = GameRoom.getById(params[2]);
				room.playerJoined(thatPlayer);
			}
		}
		if(params[0].equals("PlayerConnect"))
		{
			System.out.println(params[1] + " connecting");
			String sessionId = params[1];
			Player thatPlayer = AvalonServer.getPlayerById(sessionId);
			if(thatPlayer!=null)
			{
				conn.send("UpdateState|"+thatPlayer.s.toString());
			}
			else
			{
				Player newPlayer = new Player(sessionId, conn);
				AvalonServer.allPlayers.add(newPlayer);
				conn.send("UpdateState|"+newPlayer.s.toString());
			}
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
	    setConnectionLostTimeout(60);
	}
}