package logic.player;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import logic.net.Command;
import logic.net.client.Client;
import logic.net.server.Server;

public class PlayerTest implements Initializable {
	
	private AdvancedPlayer player;
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private Button playButton;
	
	@FXML
	private Button pauseButton;
	
	@FXML
	private Button stopButton;
	
	@FXML
	private TextField filePathField;
	
	@FXML
	private Button openButton;
	
	@FXML
	private Slider timeSlider;
	
	@FXML
	private Slider volumeSlider;
	
	@FXML
	private Label timeLabel;
	
	@FXML
	private Label durationLabel;
	
	@FXML
	private Label volumeLabel;
	
	private Client client;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
	
		player = new AdvancedPlayer();
		Node playerView = player.getView();
		borderPane.setCenter(playerView);
		
		player.addReadyListener(() -> {
			durationLabel.setText(player.getDuration().toSeconds() + "");
			timeSlider.setMax(player.getDuration().toSeconds());
		});
		
		player.currentTimeProperty().addListener((ob, o, n) -> {
			timeLabel.setText(n.toSeconds() + "");
			timeSlider.setValue(n.toSeconds());
		});
		
		/*
		timeSlider.valueProperty().addListener((ob, o, n) -> {
			abstractPlayer.seek(timeSlider.getValue());
		});
		*/
		
		timeSlider.setOnMouseClicked(e -> {
			player.seek(timeSlider.getValue());
		});
		
		volumeSlider.valueProperty().addListener((ob, o, n) -> {
			player.setVolume(volumeSlider.getValue());
			volumeLabel.setText(volumeSlider.getValue() + "");
		});
		
	}
	
	@FXML
	private void onOpenButtonClicked(ActionEvent event) {
		// C:/Users/HP PAVILION/Videos/Crash.mp4
		// C:\Users\HP PAVILION\Videos\Captures\League of Legends (TM) Client 12_10_2016 18_06_52.mp4
		// https://www.youtube.com/watch?v=VtzvlXL9gXk
		// https://www.youtube.com/watch?v=rdAQJBOHVfA
		player.open(new File(filePathField.getText()).toURI().toString());
		player.play();
	}
	@FXML
	private void onPlayButtonClicked(ActionEvent event) {
		player.play();
		client.getPrinter().println("PLAY");
	}
	@FXML
	private void onPauseButtonClicked(ActionEvent event) {
		player.pause();
	}
	@FXML
	private void onStopButtonClicked(ActionEvent event) {
		player.stop();
	}
	
	public void setClient(Client client) {
		this.client = client;
		client.addCommandListener(Command.PLAY, tokens -> {
			Platform.runLater(() -> {
				player.play();
			});
		});
	}
	
}
