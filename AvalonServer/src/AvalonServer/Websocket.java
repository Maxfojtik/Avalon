package AvalonServer;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import AvalonServer.GameRoom.Role;

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
			conn.send("GameId|"+room.id);
		}
		if(params[0].equals("JoinGame"))
		{
			Player thatPlayer = AvalonServer.getPlayerById(params[1]);
			if(thatPlayer!=null)
			{
				System.out.println(thatPlayer+" joining "+params[2]);
				GameRoom room = GameRoom.getById(params[2]);
				if(room!=null)
				{
					room.playerJoined(thatPlayer);
					thatPlayer.s = Player.State.InLobby;
					thatPlayer.setRoom(room);
					conn.send("UpdateState|"+thatPlayer.s.toString());
					conn.send("Players"+room.generatePlayers(thatPlayer));
				}
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
				if(params.length>1)
				{
					newPlayer.name = params[2];
				}
				AvalonServer.allPlayers.add(newPlayer);
				conn.send("UpdateState|"+newPlayer.s.toString());
			}
		}
		if(params[0].equals("LobbyOpen"))
		{
			System.out.print(params[1]+" open? ");
			GameRoom room = GameRoom.getById(params[1]);
			if(room!=null)
			{
				if(room.isLobbyOpen())
				{
					System.out.println("yup!");
					conn.send("LobbyOpen|"+true);
				}
				else
				{
					System.out.println("nope");
					conn.send("LobbyOpen|"+false);
				}
			}
			else
			{
				System.out.println("nope does not exist");
				conn.send("LobbyOpen|"+false);
			}
		}
		if(params[0].equals("UpdateName"))//Admin|playerSessionId|Gameid|Action|parameters
		{
			Player thePlayer = AvalonServer.getPlayerById(params[1]);
			if(params.length>1)
			{
				thePlayer.setName(params[2]);
			}
			else
			{
				thePlayer.setName("");
			}
		}
		if(params[0].equals("Admin"))//Admin|playerSessionId|Gameid|Action|parameters
		{
			GameRoom theRoom = GameRoom.getById(params[2]);
			if(theRoom!=null)
			{
				Player thePlayer = AvalonServer.getPlayerById(params[1]);
				if(thePlayer!=null && theRoom.isHost(thePlayer))
				{
					String action = params[3];
					if(action.equals("Kick"))
					{
						Player targetPlayer = AvalonServer.getPlayerById(params[4]);
						System.out.println(thePlayer+" kicked "+targetPlayer);
						if(theRoom.isPlayerInGame(targetPlayer))
						{
							theRoom.removePlayer(targetPlayer);
						}
					}
					if(action.equals("Promote"))
					{
						Player targetPlayer = AvalonServer.getPlayerById(params[4]);
						System.out.println(thePlayer+" promoted "+targetPlayer);
						if(theRoom.isPlayerInGame(targetPlayer))
						{
							theRoom.promote(targetPlayer);
						}
					}
					if(action.equals("SetRole"))
					{
						String role = params[4];
						String amount = params[5];
						System.out.println(thePlayer+" set "+role+" to "+amount);
						theRoom.roles.put(Role.valueOf(role), Integer.parseInt(amount));
					}
					if(action.equals("Start"))
					{
						System.out.println(thePlayer+" started "+theRoom);
						theRoom.startGame();
					}
				}
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