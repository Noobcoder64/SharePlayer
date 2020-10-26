package logic.player;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.util.Duration;

public abstract class AbstractPlayer {
	
	private static final PlayerState INITIAL_PLAYER_STATE = PlayerState.STOPPED;
	private static final Duration INITIAL_CURRENT_TIME = Duration.seconds(0);
	private static final double INITIAL_VOLUME = 100;
	
	protected ReadOnlyObjectWrapper<PlayerState> stateProperty;
	protected ReadOnlyObjectWrapper<Duration> currentTimeProperty;
	protected DoubleProperty volumeProperty;
	
	public AbstractPlayer() {
		stateProperty = new ReadOnlyObjectWrapper<>(INITIAL_PLAYER_STATE);
		currentTimeProperty = new ReadOnlyObjectWrapper<>(INITIAL_CURRENT_TIME);
		volumeProperty = new SimpleDoubleProperty(INITIAL_VOLUME);
	}
	
	public abstract void open(String filePath);
	
	public abstract void play();
	
	public abstract void pause();
	
	public abstract void stop();

	public abstract void seek(double seconds);
	
	public ReadOnlyObjectProperty<PlayerState> stateProperty() {
		return stateProperty.getReadOnlyProperty();
	}
	
	public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
		return currentTimeProperty.getReadOnlyProperty();
	}
	
	public DoubleProperty volumeProperty() {
		return volumeProperty;
	}
	
	public PlayerState getState() {
		return stateProperty.get();
	}
	protected void setState(PlayerState state) {
		stateProperty.set(state);
	}
	
	public Duration getCurrentTime() {
		return currentTimeProperty.get();
	};
	
	public abstract Duration getDuration();

	public double getVolume() {
		return volumeProperty.get();
	}
	public void setVolume(double volume) {
		volumeProperty.set(volume);
	}
	
	public abstract Node getView();
	
	public abstract void dispose();
	
}
