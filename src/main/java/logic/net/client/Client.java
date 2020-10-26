package logic.net.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import logic.net.Command;
import logic.player.PlayerState;

public class Client {

	private final String serverName;
	private final int serverPort;
	private Socket socket;

	private BufferedReader fromClientHandler;
	private PrintWriter toClientHandler;

	private String username;

	private Map<Command, List<Consumer<String[]>>> listenersMap;

	public Client(String username, String serverName, int serverPort) {
		this.username = username;
		this.serverName = serverName;
		this.serverPort = serverPort;

		listenersMap = new HashMap<>();
		for (Command command : Command.values()) {
			listenersMap.put(command, new ArrayList<>());
		}
	}

	public void connect() {
		try {
			socket = new Socket(serverName, serverPort);
			fromClientHandler = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toClientHandler = new PrintWriter(socket.getOutputStream(), true);
			startReceiverLoop();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		send(Command.CONNECT, username);
	}

	public void disconnect() {
		send(Command.DISCONNECT);
	}

	private void startReceiverLoop() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				receiverLoop();
			};
		};
		thread.start();
	}

	private void receiverLoop() {
		String line;
		try {
			while ((line = fromClientHandler.readLine()) != null) {
				String[] tokens = line.split("\\|");
				Command command = Command.valueOf(tokens[0]);
				listenersMap.get(command).forEach(listener -> listener.accept(Arrays.copyOfRange(tokens, 1, tokens.length)));;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Command command, String...tokens) {
		getPrinter().println(command.toString() + "|" + String.join("|", tokens));
	}
	
	public String getUsername() {
		return username;
	}

	public PrintWriter getPrinter() {
		return toClientHandler;
	}
	
	public void addCommandListener(Command command, Consumer<String[]> listener) {
		listenersMap.get(command).add(listener);
	}

}
