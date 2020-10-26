package logic.player;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MediaPlayerDecorator extends AbstractPlayer {
	
	private ObjectProperty<MediaPlayer> mediaPlayerProperty;
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	
	public MediaPlayerDecorator() {
		super();
		mediaPlayerProperty = new SimpleObjectProperty<>();
		mediaView = new MediaView();
		
		mediaPlayerProperty.addListener((observable, oldMediaPlayer, newMediaPlayer) -> {
			mediaPlayer = newMediaPlayer;

			if (oldMediaPlayer != null)
				oldMediaPlayer.dispose();
			
			configureMediaPlayer(newMediaPlayer);
		});
	}
	
	private void configureMediaPlayer(MediaPlayer mediaPlayer) {
		currentTimeProperty.bind(mediaPlayer.currentTimeProperty());
		mediaPlayer.volumeProperty().bind(volumeProperty.divide(100));
		mediaPlayer.setOnReady(() -> setState(PlayerState.READY));
		mediaPlayer.setOnPlaying(() -> setState(PlayerState.PLAYING));
		mediaPlayer.setOnPaused(() -> setState(PlayerState.PAUSED));
		mediaPlayer.setOnStopped(() -> setState(PlayerState.STOPPED));
		mediaPlayer.setOnStalled(() -> setState(PlayerState.BUFFERING));
	}
	
	@Override
	public void open(String filePath) {
		Media media = null;
		try {
		media = new Media(filePath);
		} catch (Exception e) {
			media = new Media(new File(filePath).toURI().toString());
		}
		
		mediaPlayerProperty.set(new MediaPlayer(media));
		mediaView.setMediaPlayer(mediaPlayer);
	}
	
	@Override
	public void play() {
		mediaPlayer.play();
	}

	@Override
	public void pause() {
		mediaPlayer.pause();
	}

	@Override
	public void stop() {
		mediaPlayer.stop();
	}

	@Override
	public void seek(double seconds) {
		mediaPlayer.seek(Duration.seconds(seconds));
	}

	@Override
	public Duration getDuration() {
		return mediaPlayer.getMedia().getDuration();
	}

	@Override
	public Node getView() {
		Pane pane = new StackPane();
		pane.setMinSize(0, 0);
		pane.getChildren().add(mediaView);
		pane.layoutBoundsProperty().addListener((observable, oldSize, newSize) -> {
			mediaView.setFitWidth(newSize.getWidth());
			mediaView.setFitHeight(newSize.getHeight());
		});
		return pane;
	}
	
	@Override
	public void dispose() {
		mediaPlayer.dispose();
	}
	
}
