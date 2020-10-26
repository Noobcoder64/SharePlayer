package logic.net.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import logic.net.Command;

public class ClientHandler extends Thread {
	
	private final Socket clientSocket;
	private final Server server;
	
	private BufferedReader fromClient;
	private PrintWriter toClient;
	
	private String username;
	
	public ClientHandler(Server server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
		
		try {
			fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			toClient = new PrintWriter(this.clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {
			handleClientSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleClientSocket() throws IOException {
		String line;
		while (!clientSocket.isClosed() && (line = fromClient.readLine()) != null) {
			String[] tokens = line.split("\\|");
			String cmd = tokens[0];
			if ("CONNECT".equals(cmd)) {
				handleConnect(tokens[1]);
			} else if ("DISCONNECT".equals(cmd)) {
				handleDisconnect();
			} else {
				sendToAllClients(line);
			}
		}
	}

	private void handleConnect(String username) {
		System.out.println(username + " has connected.");
		this.username = username;
		sendToAllClients("ONLINE|" + getUsername());
		for (ClientHandler clientHandler : server.getClientHandlers()) {
			if (!this.equals(clientHandler)) {
				toClient.println("ONLINE|" + clientHandler.getUsername());
			}
		}
	}

	private void handleDisconnect() {
		sendToAllClients("OFFLINE|" + getUsername());
		server.getClientHandlers().remove(this);
		try {
			System.out.println(username + " has disconnected.");
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendToAllClients(String line) {
		for (ClientHandler clientHandler : server.getClientHandlers()) {
			if (!this.equals(clientHandler)) {
				clientHandler.getPrinter().println(line);
			}
		}
	}
	
	public void send(Command command, String...tokens) {
		getPrinter().println(command.toString() + "|" + String.join("|", tokens));
	}
	
	public String getUsername() {
		return username;
	}

	public PrintWriter getPrinter() {
		return toClient;
	}

}
