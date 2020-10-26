package logic.player.youtubeplayer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSObject;

public class YoutubePlayer {
	
	private static final int INITIAL_PLAYER_STATE = 5;
	private static final Duration INITIAL_CURRENT_TIME = Duration.seconds(0);
	private static final double INITIAL_VOLUME = 100;
	
	private final ReadOnlyIntegerWrapper stateProperty;
	private final ReadOnlyObjectWrapper<Duration> currentTimeProperty;
	private final DoubleProperty volumeProperty;
	
	private final WebEngine webEngine;
	private final WebView webView;
	
	private JSObject player;
	
	private Timeline timeline;
	
	public YoutubePlayer() {
		webView = new WebView();
		webEngine = webView.getEngine();

		stateProperty = new ReadOnlyIntegerWrapper(INITIAL_PLAYER_STATE);
		currentTimeProperty = new ReadOnlyObjectWrapper<>(INITIAL_CURRENT_TIME);
		volumeProperty = new SimpleDoubleProperty(INITIAL_VOLUME);
		
		configureWebEngine();
		configurePlayer();
		configureStateProperty();
		configureCurrentTimeProperty();
		configureVolumeProperty();
	}
	
	private void configureWebEngine() {
		webEngine.load(getClass().getResource("youtube_player.html").toString());
	}
	
	private void configurePlayer() {
		webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
			System.out.println(newState);
			if (newState == Worker.State.SUCCEEDED) {
				player = (JSObject) webEngine.executeScript("getPlayer()");
			}
		});
	}
	
	private void configureStateProperty() {
		webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				JSObject window = (JSObject) webEngine.executeScript("window");
				window.setMember("stateProperty", stateProperty);
			}
		});
		
		stateProperty.addListener((observable, oldState, newState) -> {
			switch (newState.intValue()) {
			case -1:
				break;
			case 0:
				timeline.stop();
				break;
			case 1:
				timeline.play();
				break;
			case 2:
				timeline.pause();
				break;
			case 3:
				timeline.pause();
				break;
			case 5:
				currentTimeProperty.set(getCurrentTime());
				timeline.stop();
				break;
			}
		});
	}
	
	private void configureCurrentTimeProperty() {
		timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
			currentTimeProperty.set(getCurrentTime());
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
	}
	
	private void configureVolumeProperty() {
		volumeProperty.addListener((observable, oldVolume, newVolume) -> {
			player.call("setVolume", newVolume);
		});
	}

	public void loadVideoById(String videoId) {
		timeline.stop();
		player.call("loadVideoById", videoId);
	}
	
	public void loadVideoByUrl(String url) {
		String videoId = url.substring(url.length() - 11);
		loadVideoById(videoId);
	}
	
	public void play() {
		player.call("playVideo");
	}

	public void pause() {
		player.call("pauseVideo");
	}

	public void stop() {
		player.call("stopVideo");
	}

	public void seekTo(double seconds) {
		player.call("seekTo", seconds, true);
	}
	
	public ReadOnlyIntegerProperty stateProperty() {
		return stateProperty.getReadOnlyProperty();
	}
	
	public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
		return currentTimeProperty.getReadOnlyProperty();
	}
	
	public DoubleProperty volumeProperty() {
		return volumeProperty;
	}
	
	public int getState() {
		return stateProperty.get();
	}
	
	public Duration getCurrentTime() {
		return Duration.seconds(((Number) player.call("getCurrentTime")).doubleValue());
	}
	
	public Duration getDuration() {
		return Duration.seconds(((Number) player.call("getDuration")).doubleValue());
	}
	
	public double getVolume() {
		return volumeProperty.get();
	}
	
	public void setVolume(double volume) {
		volumeProperty.set(volume);
	}
	
	public Node getView() {
		Pane pane = new StackPane();
		pane.getChildren().add(webView);
		pane.getChildren().add(new Pane());
		return pane;
	}
	
	public void dispose() {
		webEngine.executeScript("dispose()");
	}

	public static final class PlayerState {
		public static final int UNSTARTED = -1;
		public static final int ENDED = 0;
		public static final int PLAYING = 1;
		public static final int PAUSED = 2;
		public static final int BUFFERING = 3;
		public static final int CUED = 5;
	}

}
