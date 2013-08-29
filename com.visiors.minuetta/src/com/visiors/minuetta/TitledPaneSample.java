package com.visiors.minuetta;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TitledPaneSample extends Application {
	final String[] imageNames = new String[]{"../images/person.png", "../images/person.png", "../images/person.png"};
	final Image[] images = new Image[imageNames.length];
	final ImageView[] pics = new ImageView[imageNames.length];
	final TitledPane[] tps = new TitledPane[imageNames.length];
	final Label label = new Label("N/A");

	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage stage) {
		stage.setTitle("TitledPane");
		Scene scene = new Scene(new Group(), 800, 250);
		scene.setFill(Color.GHOSTWHITE);


		// --- Accordion
		final Accordion accordion = new Accordion ();                
		for (int i = 0; i < imageNames.length; i++) {
			images[i] = new 
					Image(getClass().getResourceAsStream(imageNames[i]));
			pics[i] = new ImageView(images[i]);
			tps[i] = new TitledPane(imageNames[i],pics[i]); 
		}   
		accordion.getPanes().addAll(tps);        
		accordion.expandedPaneProperty().addListener(new 
				ChangeListener<TitledPane>() {
			@Override
			public void changed(ObservableValue<? extends TitledPane> ov,
					TitledPane old_val, TitledPane new_val) {
				if (new_val != null) {
					label.setText(accordion.getExpandedPane().getText() + 
							".jpg");
				}
			}
		});

		HBox hbox = new HBox(10);
		hbox.setPadding(new Insets(20, 0, 0, 20));
		hbox.getChildren().setAll( accordion);

		Group root = (Group)scene.getRoot();
		root.getChildren().add(hbox);
		stage.setScene(scene);
		stage.show();
	}
}