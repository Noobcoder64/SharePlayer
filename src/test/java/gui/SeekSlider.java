package gui;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

public class SeekSlider extends StackPane {
	
	private Slider slider;
	
	private ProgressBar progressBar;
	
	public SeekSlider() {
		getStylesheets().add(getClass().getResource("seekSliderStyle.css").toString());
		slider = new Slider();
		progressBar = new ProgressBar();
		progressBar.setMaxWidth(Double.MAX_VALUE);
		progressBar.setProgress(0);
		getChildren().add(progressBar);
		getChildren().add(slider);
		
		slider.valueProperty().addListener((ob, o, n) -> {
			progressBar.setProgress(n.doubleValue() /100);
		});
	}

}
