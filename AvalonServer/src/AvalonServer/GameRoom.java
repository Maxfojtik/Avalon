package AvalonServer;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class GameRoom 
{
	static enum State
	{
		InLobby, InGame
	}
	static enum Role
	{
		GoodGuy, BadGuy, Merlin, Percival, Morgana, Oberon, Assassin
	}
	LinkedList<Player> players = new LinkedList<Player>();
	String id;
	State s;
	Date created;
	HashMap<Role, Integer> roles = new HashMap<Role, Integer>();
	public GameRoom()
	{
		for(Role r : Role.values())
		{
			roles.put(r, 0);
		}
		id = generateId();
		created = new Date();
		s = State.InLobby;
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
		for(int i = 0; i < players.size(); i++)
		{
			players.get(i).send("Players"+generatePlayers(players.get(i)));
//			players.get(i).send("PlayerJoinedGame|"+player.publicSessionId+"|"+player.name);
		}
	}
	boolean isPlayerInGame(Player p)
	{
		return players.contains(p);
	}
	void kickPlayer(Player p)
	{
		removePlayer(p);
		p.send("Removed");
	}
	void removePlayer(Player p)
	{
		players.remove(p);
		for(int i = 0; i < players.size(); i++)
		{
			players.get(i).send("Players"+generatePlayers(players.get(i)));
//			players.get(i).send("PlayerLeftGame|"+p.publicSessionId);
		}
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
	void startGame()
	{
		
	}
	void nameUpdated(Player thePlayer)
	{
		for(int i = 0; i < players.size(); i++)
		{
			if(!players.get(i).equals(thePlayer))
			{
				players.get(i).send("UpdatedName|"+thePlayer.publicSessionId+"|"+thePlayer.name);
			}
		}
	}
	String generatePlayers(Player myself)
	{
		StringBuilder builder = new StringBuilder();
		for(Player player : players)
		{
			if(player.equals(myself))
			{
				builder.append("|");
				builder.append(player.sessionID);
				builder.append("|");
				builder.append(player.name);
			}
			else
			{
				builder.append("|");
				builder.append(player.publicSessionId);
				builder.append("|");
				builder.append(player.name);
			}
		}
		return builder.toString();
	}
	static Role removeRandomly(LinkedList<Role> roles)
	{
		int index = (int) (Math.random()*roles.size());
		return roles.remove(index);
	}
	@Override
	public String toString() {
		return "Game("+id+")";
	}
}
