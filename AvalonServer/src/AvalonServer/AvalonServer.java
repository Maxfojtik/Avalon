package AvalonServer;

import java.util.LinkedList;

import org.java_websocket.WebSocket;

public class AvalonServer 
{
	static LinkedList<GameRoom> gameRooms = new LinkedList<GameRoom>();
	static LinkedList<Player> allPlayers = new LinkedList<Player>();
	public static void main(String args[])
	{
		Websockets s = new Websockets();
		s.start();
	    System.out.println("Avalon Server started on port: " + s.getPort());
	}
	static Player getPlayerById(String id)
	{
		for(int i = 0; i < allPlayers.size(); i++)
		{
			if(allPlayers.get(i).sessionID.equals(id) || allPlayers.get(i).publicSessionId.equals(id))
			{
				return allPlayers.get(i);
			}
		}
		return null;
	}
	static Player getPlayerByWebsocket(WebSocket sock)
	{
		for(int i = 0; i < allPlayers.size(); i++)
		{
			if(allPlayers.get(i).socket.equals(sock))
			{
				return allPlayers.get(i);
			}
		}
		return null;
	}
	static void playerDisconnected(Player dced)
	{
		for(int i = 0; i < gameRooms.size(); i++)
		{
			if(gameRooms.get(i).isPlayerInGame(dced))
			{
				gameRooms.get(i).removePlayer(dced);
			}
		}
		allPlayers.remove(dced);
	}
}
