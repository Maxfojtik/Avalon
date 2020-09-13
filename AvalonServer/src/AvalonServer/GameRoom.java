package AvalonServer;

import java.security.SecureRandom;
import java.util.Date;
import java.util.LinkedList;

public class GameRoom 
{
	static enum State
	{
		InLobby, InGame
	}
	LinkedList<Player> players = new LinkedList<Player>();
	String id;
	State s;
	Date created;
	public GameRoom()
	{
		id = generateId();
		created = new Date();
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
		p.send("Removed");
	}
	boolean isLobbyOpen()
	{
		return s == State.InLobby;
	}
	Player getHost()
	{
		return players.get(0);
	}
	boolean isHost(Player p)
	{
		return getHost().equals(p);
	}
	void promote(Player p)
	{
		players.remove(p);
		players.addFirst(p);
	}
	void assignRoles(LinkedList<Role> roles)
	{
		assert roles.size() == players.size();
		int i = 0;
		while(roles.size()>0)
		{
			players.get(i).myRole = removeRandomly(roles);
			i++;
		}
	}
	static Role removeRandomly(LinkedList<Role> roles)
	{
		int index = (int) (Math.random()*roles.size());
		return roles.remove(index);
	}
}
