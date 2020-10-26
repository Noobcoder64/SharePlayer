package gui.player;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.net.Command;
import logic.net.client.Client;
import logic.player.AdvancedPlayer;

public class PlayerController implements Initializable {
	
	private Stage fullScreenStage;
	private StackPane fullScreenPane;
	
	@FXML
	private BorderPane root;
	
	@FXML
	private StackPane centerPane;
	
	@FXML
	private StackPane playerPane;
	
	@FXML
	private BorderPane controlPane;
	
	@FXML
	private Label currentTimeLabel;
	@FXML
	private Label durationLabel;

	@FXML
	private Slider seekSlider;
	@FXML
	private ProgressBar seekProgressBar;
	
	@FXML
	private Button playButton;
	@FXML
	private Button pauseButton;
	@FXML
	private Button stopButton;
	
	@FXML
	private Slider volumeSlider;
	@FXML
	private Button fullScreenButton;
	
	@FXML
	private TextField filePathField;
	@FXML
	private Button openButton;
	
	private AdvancedPlayer player;
	
	private Client client;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.getStylesheets().add(getClass().getResource("playerstyle.css").toExternalForm());
		configureStyleClasses();
		configurePlayer();
		configureControlPaneVisibility();
		configureTimeLabels();
		configureSeekBar();
		configureVolumeSlider();
		configureFullScreen();
	}

	private void configurePlayer() {
		player = new AdvancedPlayer();
		playerPane.getChildren().add(player.getView());
	}
	
	private void configureControlPaneVisibility() {
		BooleanProperty mouseMoving = new SimpleBooleanProperty();
		
		mouseMoving.addListener((observable, wasMoving, isMoving) -> {
			if (isMoving) {
				controlPane.setVisible(true);
				playerPane.setCursor(Cursor.DEFAULT);
			} else {
				controlPane.setVisible(false);
				playerPane.setCursor(Cursor.NONE);
			}
		});
	
		PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
		pause.setOnFinished(e -> mouseMoving.set(false));
	
		playerPane.setOnMouseMoved(e -> {
			mouseMoving.set(true);
			pause.playFromStart();
		});
		controlPane.setOnMouseEntered(e -> {
			mouseMoving.set(true);
			pause.stop();
		});
	}
	
	private void configureTimeLabels() {
		player.addReadyListener(() -> {
			durationLabel.setText(convertSecondstoTimeFormat(player.getDuration().toSeconds()));
		});
		
		player.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
			currentTimeLabel.setText(convertSecondstoTimeFormat(newValue.toSeconds()));
		});
	}
	
	private void configureSeekBar() {
		player.addReadyListener(() -> {
			seekSlider.setMax(player.getDuration().toSeconds());
		});
		
		InvalidationListener sliderInvalidationListener = o -> {
			double seconds = seekSlider.getValue();
			player.seek(seekSlider.getValue());
			client.send(Command.SEEK, client.getUsername(), String.valueOf(seconds));
		};
		seekSlider.valueProperty().addListener(sliderInvalidationListener);
		
		player.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
			seekSlider.valueProperty().removeListener(sliderInvalidationListener);
			seekSlider.setValue(newValue.toSeconds());
			seekSlider.valueProperty().addListener(sliderInvalidationListener);
			seekProgressBar.setProgress(newValue.toSeconds() / player.getDuration().toSeconds());
		});
	}

	private void configureVolumeSlider() {
		player.volumeProperty().bindBidirectional(volumeSlider.valueProperty());
	}
	
	private void configureFullScreen() {
		fullScreenPane = new StackPane();
		fullScreenStage = new Stage();
		fullScreenStage.setScene(new Scene(fullScreenPane));
		fullScreenStage.fullScreenProperty().addListener((observable, wasFullScreen, isFullScreen) -> {
			if (isFullScreen) {
				fullScreenStage.show();
				root.getChildren().remove(centerPane);
				fullScreenPane.getChildren().add(centerPane);
			} else {
				fullScreenStage.hide();
				fullScreenPane.getChildren().remove(centerPane);
				root.setCenter(centerPane);
			}
		});
	}
	
	@FXML
	private void onOpenButtonClicked(ActionEvent event) {
		String filePath = filePathField.getText();
		player.open(filePath);
		player.play();
		client.send(Command.OPEN, client.getUsername(), filePath);
	}
	@FXML
	private void onPlayButtonClicked(ActionEvent event) {
		System.out.println("PLAY");
		player.play();
		client.send(Command.PLAY, client.getUsername());
	}
	@FXML
	private void onPauseButtonClicked(ActionEvent event) {
		System.out.println("PAUSE");
		player.pause();
		client.send(Command.PAUSE, client.getUsername());
	}
	@FXML
	private void onStopButtonClicked(ActionEvent event) {
		System.out.println("STOP");
		player.stop();
		client.send(Command.STOP, client.getUsername());
	}
	@FXML
	private void onFullScreenButtonClicked(ActionEvent event) {
		if (fullScreenStage.isFullScreen()) {
			fullScreenStage.setFullScreen(false);
		} else {
			fullScreenStage.setFullScreen(true);
		}
	}
	
	private void configureStyleClasses() {
		centerPane.getStylesheets().add(getClass().getResource("playerstyle.css").toExternalForm());
		currentTimeLabel.getStyleClass().add("timeLabel");
		durationLabel.getStyleClass().add("timeLabel");		
		
		playButton.getStyleClass().add("playerButton");
		pauseButton.getStyleClass().add("playerButton");
		stopButton.getStyleClass().add("playerButton");
		fullScreenButton.getStyleClass().add("playerButton");
	}
	
	public void setClient(Client client) {
		this.client = client;
		
		client.addCommandListener(Command.OPEN, tokens -> {
			Platform.runLater(() -> {
				player.open(tokens[1]);
				player.play();
			});
		});
		
		client.addCommandListener(Command.PLAY, tokens -> {
			Platform.runLater(() -> {
				player.play();
			});
		});
		
		client.addCommandListener(Command.PAUSE, tokens -> {
			Platform.runLater(() -> {
				player.pause();
			});
		});
		
		client.addCommandListener(Command.STOP, tokens -> {
			Platform.runLater(() -> {
				player.stop();
			});
		});
		
		client.addCommandListener(Command.SEEK, tokens -> {
			Platform.runLater(() -> {
				player.seek(Double.valueOf(tokens[1]));
			});
		});
	}
	
	private static String convertSecondstoTimeFormat(double secondsToConvert) {
		java.time.Duration duration = java.time.Duration.ofSeconds((long) secondsToConvert);
		long hours = duration.toHours();
		long minutes = duration.minusHours(hours).toMinutes();
		long seconds = duration.minusHours(hours).minusMinutes(minutes).toMillis() / 1000;
		return ((hours < 10) ? "0" + hours : hours) + ":" + ((minutes < 10) ? "0" + minutes : minutes) + ":" + ((seconds < 10) ? "0" + seconds : seconds);
	}
	
}
