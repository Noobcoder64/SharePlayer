package logic.player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.net.client.Client;
import logic.net.server.Server;

public class SharePlayerTest extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerViewTest.fxml"));
		BorderPane borderPane = loader.load();
		PlayerTest controller = loader.getController();
		
		//Server server = new Server(9000);
		//server.start();
		
		Client client = new Client("Client2" , "localhost", 9000);
		client.connect();
		
		controller.setClient(client);
		
		Scene scene = new Scene(borderPane);
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
