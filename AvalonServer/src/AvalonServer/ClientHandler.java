package AvalonServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ClientHandler 
{
	static ServerSocket welcomeSocket;
	static LinkedList<ClientConnection> clientConnections = new LinkedList<ClientConnection>();
	public static void start() throws IOException
	{
		welcomeSocket = new ServerSocket(12389);
		System.out.println("Started listening for clients");
		new Thread(new SocketListener()).start();
	}
	public static class SocketListener implements Runnable
	{
		public SocketListener()
		{
		}
		public void run() 
		{
			while(true)
			{
				try
				{
					Socket connectionSocket = welcomeSocket.accept();
					System.out.println("Incoming Client!!");
					ClientConnection con = new ClientConnection(connectionSocket);
					clientConnections.add(con);
					new Thread(con).start();
				}
				catch(Exception e) {e.printStackTrace();}
			}
		}
	}
	public static class ClientConnection implements Runnable
	{
		Socket connectionSocket;
		public ClientConnection(Socket cs)
		{
			connectionSocket = cs;
		}
		public void run() 
		{
			try
			{
				String clientSentence;
				while(true)
				{
					try
					{
						Socket connectionSocket = welcomeSocket.accept();
						BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
						PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
						while(true)
						{
							clientSentence = inFromClient.readLine();
							//System.out.println("Received: " + clientSentence);
							if(clientSentence.equals("Ping"))
							{
								//System.out.println("Responding");
								outToClient.println("Pong");
								outToClient.flush();
							}
						}
					}
					catch(Exception e) {e.printStackTrace();}
				}
			}
			finally 
			{
				clientConnections.remove(this);
			}
		}
	}
}
