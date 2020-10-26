package logic.player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class AdvancedPlayer extends AbstractPlayer {
	
	private AbstractPlayer player;
	private MediaPlayerDecorator mediaPlayerAdapter;
	private YoutubePlayerAdapter youtubePlayerAdapter;
	
	private Map<PlayerState, List<Runnable>> listenersMap;
	
	private StackPane stackPane;
	
	public AdvancedPlayer() {
		mediaPlayerAdapter = new MediaPlayerDecorator();
		youtubePlayerAdapter = new YoutubePlayerAdapter();
		stackPane = new StackPane();
		initiliazeListeners();
		
		setPlayer(youtubePlayerAdapter);
		
		stateProperty.addListener((observable, oldState, newState) -> {
			notifyListeners(newState);
		});
	}
	
	private void initiliazeListeners() {
		listenersMap = new EnumMap<>(PlayerState.class);
		for (PlayerState state : PlayerState.values()) {
			listenersMap.put(state, new ArrayList<>());
		}
	}
	
	@Override
	public void open(String filePath) {
		if (filePath.contains("youtube")) {
			if (!(player instanceof YoutubePlayerAdapter)) {
				setPlayer(youtubePlayerAdapter);
			}
		} else {
			if (!(player instanceof MediaPlayerDecorator)) {
				setPlayer(mediaPlayerAdapter);
			}
		}
		player.open(filePath);
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
		player.seek(seconds);
	}
	
	@Override
	public Duration getDuration() {
		return player.getDuration();
	}
	
	@Override
	public Node getView() {
		return stackPane;
	}
	
	public void addReadyListener(Runnable listener) {
		listenersMap.get(PlayerState.READY).add(listener);
	}
	
	public void addPlayListener(Runnable listener) {
		listenersMap.get(PlayerState.PLAYING).add(listener);
	}
	
	public void addPauseListener(Runnable listener) {
		listenersMap.get(PlayerState.PAUSED).add(listener);
	}
	
	public void addStopListener(Runnable listener) {
		listenersMap.get(PlayerState.STOPPED).add(listener);
	}
	
	public void addBufferListener(Runnable listener) {
		listenersMap.get(PlayerState.BUFFERING).add(listener);
	}
	
	private void notifyListeners(PlayerState state) {
		listenersMap.get(state).forEach(listener -> listener.run());
	}
	
	private void setPlayer(AbstractPlayer abstractPlayer) {
		if (player != null) {
			player.dispose();
			player.volumeProperty().unbind();
		}
		player = abstractPlayer;
		configurePlayer();
	}
	
	private void configurePlayer() {
		stateProperty.bind(player.stateProperty());
		currentTimeProperty.bind(player.currentTimeProperty());
		player.volumeProperty().bind(volumeProperty);
		stackPane.getChildren().clear();
		stackPane.getChildren().add(player.getView());
	}

	@Override
	public void dispose() {
		if (player != null) {
			player.dispose();
		}
	}
	
}
