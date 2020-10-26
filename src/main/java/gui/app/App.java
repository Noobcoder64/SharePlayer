package gui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {
	
	private Pane sharePlayerPane;
	private SharePlayerController sharePlayerController;
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("SharePlayer.fxml"));
		sharePlayerPane = loader.load();
		sharePlayerController = loader.getController();
		sharePlayerController.setStage(stage);
		
		Scene scene = new Scene(sharePlayerPane);
		
		stage.setTitle("Share Player");
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
