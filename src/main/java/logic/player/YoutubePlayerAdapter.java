package logic.player;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import logic.player.youtubeplayer.YoutubePlayer;
import netscape.javascript.JSObject;

public class YoutubePlayerAdapter extends AbstractPlayer {
	
	private YoutubePlayer player;
	
	private Map<Integer, PlayerState> stateMap;
	
	public YoutubePlayerAdapter() {
		player = new YoutubePlayer();
		configureStateMap();
		
		player.stateProperty().addListener((observable, oldState, newState) -> {
			setState(stateMap.get(newState));
		});
		currentTimeProperty.bind(player.currentTimeProperty());
		player.volumeProperty().bind(volumeProperty);
	}
	
	private void configureStateMap() {
		stateMap = new HashMap<>();
		stateMap.put(-1, PlayerState.READY);
		stateMap.put(0 , PlayerState.STOPPED);
		stateMap.put(1, PlayerState.PLAYING);
		stateMap.put(2, PlayerState.PAUSED);
		stateMap.put(3, PlayerState.BUFFERING);
		stateMap.put(5, PlayerState.STOPPED);
	}
	
	@Override
	public void open(String url) {
		player.loadVideoByUrl(url);
	}

	@Override
	public void play() {
		player.play();
	}

	@Override
	public void pause() {
		player.pause();
	}

	@Override
	public void stop() {
		player.stop();
	}

	@Override
	public void seek(double seconds) {
		player.seekTo(seconds);
	}
	
	@Override
	public Duration getCurrentTime() {
		return player.getCurrentTime();
	}
	
	@Override
	public Duration getDuration() {
		return player.getDuration();
	}

	@Override
	public Node getView() {
		return player.getView();
	}
	
	@Override
	public void dispose() {
		player.dispose();
	}

}
