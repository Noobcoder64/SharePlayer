package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SeekSliderTest extends Application {
	
	private Label currentTimeLabel;
	
	@Override
	public void start(Stage stage) throws Exception {
		BorderPane borderPane = new BorderPane();
		
		SeekSlider seekSlider = new SeekSlider();
		borderPane.setCenter(seekSlider);
		
		HBox hBox = new HBox();
		borderPane.setBottom(hBox);
		hBox.getChildren().add(new Label("Current Time:"));
		currentTimeLabel = new Label();
		hBox.getChildren().add(currentTimeLabel);
		
		
		Scene scene = new Scene(borderPane);
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
