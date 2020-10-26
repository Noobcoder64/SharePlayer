package gui.app;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import gui.connectmenu.ConnectMenuController;
import gui.player.PlayerController;
import gui.userlist.UserListController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import logic.net.client.Client;
import logic.net.server.Server;

public class SharePlayerController implements Initializable {

	private Stage stage;

	@FXML
	private BorderPane root;
	@FXML
	private StackPane rightPane;

	private Parent playerView;
	private PlayerController playerController;

	private Parent connectMenuView;
	private ConnectMenuController connectMenuController;

	private Parent userListView;
	private UserListController userListController;

	private Server server;
	private Client client;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.getStylesheets().add(getClass().getResource("shareplayerstyle.css").toExternalForm());
		loadPlayerViewAndController();
		loadConnectMenuViewAndController();
		loadUserListViewAndController();

		connectMenuController.setController(this);
		
		rightPane.getChildren().add(connectMenuView);
	}

	private void loadPlayerViewAndController() {
		FXMLLoader loader = createLoader("../player/Player.fxml");
		playerView = loadViewFromLoader(loader);
		playerController = loader.getController();
	}
	
	private void loadConnectMenuViewAndController() {
		FXMLLoader loader = createLoader("../connectmenu/ConnectMenu.fxml");
		connectMenuView = loadViewFromLoader(loader);
		connectMenuController = loader.getController();
	}

	private void loadUserListViewAndController() {
		FXMLLoader loader = createLoader("../userlist/UserList.fxml");
		userListView = loadViewFromLoader(loader);
		userListController = loader.getController();
	}
	
	private FXMLLoader createLoader(String location) {
		return new FXMLLoader(getClass().getResource(location));
	}

	private Parent loadViewFromLoader(FXMLLoader loader) {
		Parent view = null;
		try {
			view = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return view;
	}

	public void setClient(Client client) {
		this.client = client;

		userListController.setClient(client);
		rightPane.getChildren().remove(connectMenuView);
		rightPane.getChildren().add(userListView);

		playerController.setClient(client);
		root.setCenter(playerView);

		client.connect();
	}

	public void setServer(Server server) {
		this.server = server;
		server.start();
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.setOnCloseRequest(event -> {
			if (client != null)
				client.disconnect();
			if (server != null)
				server.close();
		});
	}

}
