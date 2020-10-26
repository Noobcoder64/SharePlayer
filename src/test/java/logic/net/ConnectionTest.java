package logic.net;

import logic.net.client.Client;
import logic.net.server.Server;

public class ConnectionTest {
	
	public static void main(String[] args) {
		Server server = new Server(9000);
		server.start();
		
		Client client1 = new Client("Client1" , "localhost", 9000);
		Client client2 = new Client("Client2" , "localhost", 9000);
		
		client1.connect();
		client2.connect();
		
		client2.addCommandListener(Command.ONLINE, parameters -> {
			System.out.println(parameters[0] + " has joined.");
		});
		client1.getPrinter().println("ONLINE " + client1.getUsername());
	}
	
	
	
}
