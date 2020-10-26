package logic.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
	
	private final int serverPort;
	
	private ServerSocket serverSocket;
	
	private ArrayList<ClientHandler> clientHandlers;
	
	public Server(int serverPort) {
		this.serverPort = serverPort;
		clientHandlers = new ArrayList<>();
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(serverPort);
			System.out.println("Server started...");
			handleServerSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private void handleServerSocket() throws IOException {
		while (!serverSocket.isClosed()) {
			Socket clientSocket = serverSocket.accept();
			process(clientSocket);
		}
	}

	private void process(Socket clientSocket) {
		ClientHandler clientHandler = new ClientHandler(this, clientSocket);
		clientHandlers.add(clientHandler);
		clientHandler.start();
	}
	
	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<ClientHandler> getClientHandlers() {
		return clientHandlers;
	}
	
}
