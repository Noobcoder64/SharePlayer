package gui.userlist;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import logic.net.Command;
import logic.net.client.Client;

public class UserListController implements Initializable {
	
	@FXML
	private BorderPane root;
	
	@FXML
	private VBox userListPane;
	
	private Map<String, Pane> usersMap;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.getStylesheets().add(getClass().getResource("userliststyle.css").toExternalForm());
		usersMap = new HashMap<>();
	}
	
	private void addUser(String username) {
		HBox userPane = new HBox();
		userPane.setAlignment(Pos.CENTER_LEFT);
		userPane.getStyleClass().add("userPane");
		
		Label usernameLabel = new Label(username);
		usernameLabel.getStyleClass().add("usernameLabel");
		
		Image image = new Image(getClass().getResource("user_icon.png").toExternalForm(), 0, 40, true, true);
		ImageView imageView = new ImageView(image);
		imageView.getStyleClass().add("userIcon");
		
		userPane.getChildren().add(imageView);
		userPane.getChildren().add(usernameLabel);
		userListPane.getChildren().add(userPane);
		
		usersMap.putIfAbsent(username, userPane);
	}
	
	private void removeUser(String username) {
		userListPane.getChildren().remove(usersMap.get(username));
	}
	
	public void setClient(Client client) {
		addUser(client.getUsername());
		
		client.addCommandListener(Command.ONLINE, tokens -> {
			Platform.runLater(() -> {
				String username = tokens[0];
				addUser(username);	
			});
		});
		
		client.addCommandListener(Command.OFFLINE, tokens -> {
			Platform.runLater(() -> {
				String username = tokens[0];
				removeUser(username);	
			});
		});
	}

}
