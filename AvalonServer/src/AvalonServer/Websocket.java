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
		if(dcedPlayer!=null)
		{
			dcedPlayer.socket = null;
			dcedPlayer.disconnectTime = System.currentTimeMillis();
		}
//		AvalonServer.playerDisconnected(dcedPlayer);
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
			conn.send("RedirectToGame|"+room.id);
		}
		else if(params[0].equals("JoinGame"))
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
					conn.send("GameId|"+room.id);
				}
			}
		}
		else if(params[0].equals("PlayerConnect"))
		{
			System.out.println(params[1] + " connecting");
			String sessionId = params[1];
			Player thatPlayer = AvalonServer.getPlayerById(sessionId);
			if(thatPlayer!=null)
			{
				thatPlayer.setSocket(conn);
				thatPlayer.disconnectTime = -1;
				conn.send("UpdateState|"+thatPlayer.s.toString());
				if(thatPlayer.myRoom!=null)
				{
					conn.send("Players"+thatPlayer.myRoom.generatePlayers(thatPlayer));
					conn.send("GameId|"+thatPlayer.myRoom.id);
				}
			}
			else
			{
				Player newPlayer = new Player(sessionId, conn);
				if(params.length>2)
				{
					newPlayer.name = params[2];
				}
				AvalonServer.allPlayers.add(newPlayer);
				conn.send("UpdateState|"+newPlayer.s.toString());
			}
		}
		else if(params[0].equals("LobbyOpen"))
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
		else if(params[0].equals("UpdateName"))
		{
			Player thePlayer = AvalonServer.getPlayerById(params[1]);
			if(params.length>2)
			{
				thePlayer.setName(params[2]);
			}
			else
			{
				thePlayer.setName("");
			}
		}
		else if(params[0].equals("Admin"))//Admin|playerSessionId|Action|parameters
		{
			Player thePlayer = AvalonServer.getPlayerById(params[1]);
			if(thePlayer!=null && thePlayer.myRoom.isHost(thePlayer))
			{
				String action = params[2];
				if(action.equals("Kick"))
				{
					Player targetPlayer = AvalonServer.getPlayerById(params[3]);
					System.out.println(thePlayer+" kicked "+targetPlayer);
					if(thePlayer.myRoom.isPlayerInGame(targetPlayer))
					{
						thePlayer.myRoom.removePlayer(targetPlayer);
					}
				}
				else if(action.equals("Promote"))
				{
					Player targetPlayer = AvalonServer.getPlayerById(params[3]);
					System.out.println(thePlayer+" promoted "+targetPlayer);
					if(thePlayer.myRoom.isPlayerInGame(targetPlayer))
					{
						thePlayer.myRoom.promote(targetPlayer);
					}
				}
				else if(action.equals("SetRole"))
				{
					String role = params[3];
					String amount = params[4];
					System.out.println(thePlayer+" set "+role+" to "+amount);
					thePlayer.myRoom.roles.put(Role.valueOf(role), Integer.parseInt(amount));
				}
				else if(action.equals("Start"))
				{
					System.out.println(thePlayer+" started "+thePlayer.myRoom);
					thePlayer.myRoom.startGame();
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