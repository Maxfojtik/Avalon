package AvalonServer;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import AvalonServer.GameRoom.Role;

public class GameRoom 
{
	static enum State
	{
		InLobby, InGame
	}
	static enum Role
	{
		GoodGuy, BadGuy, Merlin, Percival, Morgana, Oberon, Assassin, Mordred
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
	static final String CONS = "BCDFGHJLMNPRSTVWXYZ";
	static final String VOWELS = "AEIOU";
	static SecureRandom rnd = new SecureRandom();
	
	String randomString( int len, String AB){
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
		String potentialId = "";
		while(potentialId.equals(""))
		{
			potentialId = randomString(1, CONS)+randomString(1, VOWELS)+randomString(1, CONS)+randomString(1, VOWELS);
			for(GameRoom room : AvalonServer.gameRooms)
			{
				if(room.id.equals(potentialId))
				{
					potentialId = "";
				}
			}
		}
		return potentialId;
	}
	void playerJoined(Player player)
	{
		players.add(player);
		sendRoleCounts(player);
		for(int i = 0; i < players.size(); i++)
		{
			players.get(i).send("Players"+generatePlayers(players.get(i)));
//			players.get(i).send("PlayerJoinedGame|"+player.publicSessionId+"|"+player.name);
		}
	}
	void sendRoleCounts(Player p)
	{
		StringBuilder sb = new StringBuilder();
		Set<Role> roleKeys = roles.keySet();
		for(Role role : roleKeys)
		{
			sb.append("|");
			sb.append(role);
			sb.append("|");
			sb.append(roles.get(role));
		}
		p.send("AllRoles"+sb.toString());
	}
	boolean isPlayerInGame(Player p)
	{
		return players.contains(p);
	}
	void kickPlayer(Player p)
	{
		removePlayer(p);
		p.send("Kicked");
	}
	void removePlayer(Player p)
	{
		p.s = Player.State.InMainMenu;
		p.send("UpdateState|"+p.s.toString());
		players.remove(p);
		for(int i = 0; i < players.size(); i++)
		{
			players.get(i).send("Players"+generatePlayers(players.get(i)));
//			players.get(i).send("PlayerLeftGame|"+p.publicSessionId);
		}
	}
	void setRole(Player host, String role, String amount)
	{
		roles.put(Role.valueOf(role), Integer.parseInt(amount));
		for(int i = 0; i < players.size(); i++)
		{
			if(!players.get(i).equals(host))
			{
				sendRoleCounts(players.get(i));
			}
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
		for(int i = 0; i < players.size(); i++)
		{
			players.get(i).send("Players"+generatePlayers(players.get(i)));
//			players.get(i).send("PlayerLeftGame|"+p.publicSessionId);
		}
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
	boolean startGame()
	{
		return true;
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
