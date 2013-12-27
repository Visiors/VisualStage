package com.visiors.minuetta.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import com.visiors.minuetta.resources.Resource;

public class ShapeGallery extends Accordion {

	final String[] imageNames = new String[] { "images/person.png", "images/person.png", "images/person.png" };
	final Image[] images = new Image[imageNames.length];
	final ImageView[] pics = new ImageView[imageNames.length];
	final TitledPane[] tps = new TitledPane[imageNames.length];
	final Label label = new Label("N/A");

	public ShapeGallery() {

		for (int i = 0; i < imageNames.length; i++) {
			images[i] = new Image(Resource.class.getResourceAsStream(imageNames[i]));
			pics[i] = new ImageView(images[i]);

			final FlowPane content = new FlowPane();
			content.setAlignment(Pos.TOP_LEFT);
			content.setPrefWidth(100);
			content.setHgap(5);
			content.setVgap(5);
			for (int j = 0; j < 13; j++) {
				content.getChildren().add(new ImageView(new Image(Resource.class.getResourceAsStream("images/person.png"))));
			}

			tps[i] = new TitledPane("Shapes",content);
			tps[i].getStyleClass().add("gallery-titled-pane");
			tps[i].setContentDisplay(ContentDisplay.RIGHT);
			//			tps[i].setGraphic(
			//					HBoxBuilder.create().alignment(Pos.BASELINE_RIGHT).children(
			//							new ImageView(new Image(Resource.class.getResourceAsStream("images/pin.gif")))
			//							).build()
			//					);
			//			if(i == 1) {
			//				tps[1].setExpanded(true);
			//			}
		}


		getPanes().addAll(tps);
		expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {

			@Override
			public void changed(ObservableValue<? extends TitledPane> ov, TitledPane old_val, TitledPane new_val) {

				label.setText("Title");
				if (new_val != null) {
				}
			}
		});
	}
}
