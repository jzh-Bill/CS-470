package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class Server
{
	private static final int PORT_NO = 9876;
	private static final int MAX_TIME = 20;

	private static DatagramSocket socket;
	
	
	private static Hashtable <String, IPEntry> clientList = new Hashtable<>();
	private static Set<String> setOfClientIPs = new HashSet<String>(); 
	private static String myIP;
	private static Message message = new Message(false, false, "Hello, this is the server ", myIP, clientList);

	public static void main(String[] args)
	{
		runServer();
	}
	
	public Server(Hashtable <String, IPEntry> passedList)
	{
		this.setOfClientIPs = passedList.keySet();
		for (String ip : setOfClientIPs)
		{
			this.clientList.put(ip, passedList.get(ip));
		}
	}

	public static void loadIPs() 
	{
		//Load myIP from txt file
		ConfigReader configReader = new ConfigReader();
		myIP = configReader.getSingleIP("myIP.txt");
		message.setSenderIP(myIP);
		if (!setOfClientIPs.contains(myIP))
		{
			IPEntry newNode = new IPEntry(true);
			clientList.put(myIP, newNode);
			setOfClientIPs.add(myIP);
		}
	}

	public static void runServer()
	{
		createSocket();
		loadIPs();
		
		//Starting our timer
		int timer = 0;
		
		//Setting up our buffer and dummy message to load data into
		byte[]  buffer = new byte[65508];
		DatagramPacket recievedPacket = new DatagramPacket(buffer, buffer.length);
		Message recievedMessage = new Message(false, false, myIP,"", null);
		
		
		while(true)
		{
			printClientStatus();
			if(timer == MAX_TIME)
			{
				timer = 0;
				sendToAll();
			}
			try
			{
				socket.receive(recievedPacket);
				recievedMessage = recievedMessage.deserializer(recievedPacket);
				String tempIPString = recievedMessage.getSenderIP();
				System.out.println(timer + "\t"+tempIPString + " : " + recievedMessage.getText());
				
				if(!recievedMessage.getisSimple() && !recievedMessage.getSenderIP().equals(myIP))
				{
					becomeClient(recievedMessage.getSenderIP());
					break;
				}
				
				if(!setOfClientIPs.contains(tempIPString))
				{
					IPEntry newNode = new IPEntry(true);
					clientList.put(tempIPString, newNode);
					setOfClientIPs.add(tempIPString);
				}
				clientList.get(tempIPString).setIsAlive(true);
				clientList.get(tempIPString).setTimeToLive(MAX_TIME*2);
				sendToAll();
			}
			catch (IOException e)nt
			{
				System.out.println(timer + "\t No message recieved");
			}
			
			
			timer++;
			for (String ip : setOfClientIPs)
			{
				if(!ip.equals(myIP))
				{
					clientList.get(ip).setTimeToLive(clientList.get(ip).getTimeToLive()-1);	
				}
				if (clientList.get(ip).getTimeToLive() <= 0)
				{
					clientList.get(ip).setIsAlive(false);
				}
			}
		}
	}



	private static void becomeClient(String serverString)
	{
		socket.close();
		Client newClient = new Client(serverString);
		newClient.runClient(true);
		
	}

	private static void sendToAll()
	{
		for (String ip : setOfClientIPs)
		{
			if(!ip.equals(myIP)) 
			{
				try
				{
					message.updateTimestamp();
					DatagramPacket packet = message.createPacket(InetAddress.getByName(ip), PORT_NO);
					socket.send(packet);
				} 
				catch (IOException e)
				{
					System.out.println("There was an error creating a packet for " + ip);
					e.printStackTrace();
				}
			}
		}	
	}
	
	private static void printClientStatus()
	{
		System.out.println("=================================");
		for (String ip : setOfClientIPs)
		{
			System.out.println(ip + clientList.get(ip).getStatusString() +" timeout in "+ clientList.get(ip).getTimeToLive());
		}
		System.out.println("=================================");
	}

	private static void createSocket()
	{
		try
		{
			socket = new DatagramSocket(PORT_NO);
			socket.setSoTimeout(1000);
		}
		catch (SocketException e)
		{
			System.out.println("The datagram socket could not be created");
			e.printStackTrace();
		}
	}
}
