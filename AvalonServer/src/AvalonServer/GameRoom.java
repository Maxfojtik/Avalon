package AvalonServer;

import java.security.SecureRandom;
import java.util.LinkedList;

public class GameRoom 
{
	LinkedList<Player> players = new LinkedList<Player>();
	String id;
	public GameRoom()
	{
		id = generateId();
	}
	static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static SecureRandom rnd = new SecureRandom();
	
	String randomString( int len ){
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}
	static GameRoom getById(String id)
	{
		for(int i = 0; i < AvalonServer.gameRooms.size(); i++)
		{
			if(AvalonServer.gameRooms.get(i).id.equals(id))
			{
				return AvalonServer.gameRooms.get(i);
			}
		}
		return null;
	}
	String generateId()
	{
		return randomString(4);
	}
	void playerJoined(Player player)
	{
		players.add(player);
	}
	boolean isPlayerInGame(Player p)
	{
		return players.contains(p);
	}
	void removePlayer(Player p)
	{
		players.remove(p);
	}
}
