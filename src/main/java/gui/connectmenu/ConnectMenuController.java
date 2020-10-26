package gui.connectmenu;

import java.net.URL;
import java.util.ResourceBundle;

import gui.app.SharePlayerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import logic.net.client.Client;
import logic.net.server.Server;

public class ConnectMenuController implements Initializable {
	
	private SharePlayerController controller;
	
	@FXML
	private VBox root;
	
	@FXML
	private TextField usernameField;
	@FXML
	private TextField serverNameField;
	@FXML
	private TextField serverPortField;
	
	@FXML
	private Button hostButton;
	@FXML
	private Button joinButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.getStylesheets().add(getClass().getResource("connectmenustyle.css").toExternalForm());
		configureStyleClasses();
	}
	
	@FXML
	private void onHostButtonClicked(ActionEvent event) {
		int serverPort = Integer.valueOf(serverPortField.getText());
		Server server = new Server(serverPort);
		controller.setServer(server);
		createClient();
	}

	@FXML
	private void onJoinButtonClicked(ActionEvent event) {
		createClient();
	}
	
	private void createClient() {
		String username = usernameField.getText();
		String serverName = serverNameField.getText();
		int serverPort = Integer.valueOf(serverPortField.getText());
		
		Client client = new Client(username, serverName, serverPort);
		controller.setClient(client);
	}
	
	private void configureStyleClasses() {
		usernameField.getStyleClass().add("formField");
		serverNameField.getStyleClass().add("formField");
		serverPortField.getStyleClass().add("formField");
		
		hostButton.getStyleClass().add("formButton");
		joinButton.getStyleClass().add("formButton");
	}
	
	public void setController(SharePlayerController controller) {
		this.controller = controller;
	}

}
