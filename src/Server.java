import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.io.IOException;

/**
*Our chat room server is a multithreaded server, 
*which allows for the connection of multiple clients. 
*Each client is requested to provide a name to the server. 
*This name must be unique, so that clients can be uniquely identified.
*Messages from the client are broadcast to other unique clients
*connected to this server.
**/
public class Server
{
	//Set the port number to listen on and create sets of clients and their print writers, and a set of client messages
	private static final int listenPORT = 9001;
	private static HashSet<String> nameSet = new HashSet<String>();
	private static HashSet<PrintWriter> writerSet = new HashSet<PrintWriter>();
	private static HashSet<String> serverLog = new HashSet<String>();
	
	//Listens on a port and creates new threads for clients
	public static void main(String [] args) throws Exception
	{
		ServerSocket listener = new ServerSocket(listenPORT);
		System.out.println("Chat server initialized.");
		try
		{
			while(true)
			{
				new Handler(listener.accept()).start();
			}
		}
		finally
		{
			listener.close();
		}
	}
	//Creates an object instance for the unique client
	private static class Handler extends Thread
	{
		private Socket clientSocket;
		private String clientName;
		
		private BufferedReader input;
		private PrintWriter output;
		
		//Handler thread is constructed here.
		public Handler(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
		}
		
		//Requests name, adds output stream for client to writerSet, broadcasts input
		public void run()
		{
			try
			{
				//Character streams for the socket created
				input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				output = new PrintWriter(clientSocket.getOutputStream(), true);
				
				//Request a name from the client
				while (true)
				{
					output.println("EnterName");
					clientName = input.readLine();
					if (clientName == null)
					{
						return;
					}
					//We do this since we have two threads or more accessing and modifying nameSet
					synchronized(nameSet)
					{
						if (!nameSet.contains(clientName))
						{
							nameSet.add(clientName);
							break;
						}
					}
				}
			
				//Once the print writer is added to writerSet, confirm name registration
				writerSet.add(output);
				output.println("NameRegister");
			
				//Accepts and broadcast messages 
				while (true)
				{
					String clientInput = input.readLine();
					if (clientInput != "")
					{
						for (PrintWriter writer : writerSet)
						{
							writer.println("Message " + clientName + ": " + clientInput);
							serverLog.add("Message " + clientName + ": " + clientInput);
						}
						//Prints client message history to output stream
						if (clientInput.endsWith("/log"))
						{
							output.println("START OF LOG");
							for(String string : serverLog)
							{
								output.println(string);
								
							}
							output.println("END OF LOG");
						}
					}
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
			finally
			{
				//Remove the client and close the socket
				nameSet.remove(clientName);
				writerSet.remove(output);
				try
				{
					clientSocket.close();
				}
				catch (IOException e)
				{
					System.out.println(e);
				}
			}
		}
	}
}
		
			
