package com.visiors.minuetta.view;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.google.common.collect.Lists;

public class RepositoryView extends VBox {

	protected static final int MIN_ZOOM = 50;
	protected static final int MAX_ZOOM = 150;
	protected static final int DEFAULT_ZOOM = 100;
	private double scale = 1.0;
	private final List<Pane> categories;

	public RepositoryView(Repository repository) {

		// TODO bind the to the parent's height

		getChildren().add(createToolbar());
		final Accordion accordion = new Accordion();
		accordion.setPrefWidth(300);
		accordion.setPrefHeight(1300);

		this.categories = createCategories(accordion, repository);
		getChildren().add(accordion);
		addListener();
	}

	private List<Pane> createCategories(Accordion accordion, Repository repository) {

		final List<Pane> categories = Lists.newArrayList();
		for (final String category : repository.getCategories()) {
			final FlowPane pane = createCategory(accordion, category);
			final List<RepositoryItem> shapes = repository.getShapes(category);
			for (final RepositoryItem shape : shapes) {
				addShape(pane, shape);
			}
			categories.add(pane);
		}
		return categories;
	}

	private Node createToolbar() {

		final Slider slider = new Slider(MIN_ZOOM, MAX_ZOOM, DEFAULT_ZOOM);
		slider.setShowTickMarks(false);
		slider.setSnapToTicks(true);
		slider.setMinSize(1, 15);

		slider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				updateItemSize(newValue.doubleValue() / 100.0);
			}
		});
		return slider;
	}

	//
	// expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
	//
	// @Override
	// public void changed(ObservableValue<? extends TitledPane> ov, TitledPane
	// old_val, TitledPane new_val) {
	//
	// if (new_val != null) {
	// }
	// }
	// });

	private FlowPane createCategory(Accordion accordion, String category) {

		final FlowPane content = new FlowPane();
		content.setAlignment(Pos.TOP_LEFT);
		content.setPrefHeight(20);
		content.setHgap(1);
		content.setVgap(1);
		content.setPadding(new Insets(0, 0, 0, 0));
		content.setPrefWidth(300);

		final TitledPane titledPane = new TitledPane(category, content);
		titledPane.getStyleClass().add("gallery-titled-pane");
		titledPane.setContentDisplay(ContentDisplay.RIGHT);
		titledPane.setMinWidth(30);
		accordion.getPanes().add(titledPane);
		return content;
	}

	private void addShape(FlowPane content, RepositoryItem shape) {

		final RepositoryViewItem repositoryViewItem = new RepositoryViewItem(shape);
		repositoryViewItem.setFontScale(scale);
		repositoryViewItem.setImageScale(scale);
		repositoryViewItem.create();
		content.getChildren().add(repositoryViewItem);
	}

	private void updateItemSize(double value) {

		scale = value;
		for (final Pane category : categories) {
			final ObservableList<Node> children = category.getChildren();
			for (final Node node : children) {
				if (node instanceof RepositoryViewItem) {
					final RepositoryViewItem repositoryViewItem = (RepositoryViewItem) node;
					repositoryViewItem.setFontScale(scale);
					repositoryViewItem.setImageScale(scale);
				}
			}
		}
	}

	private void addListener() {

		setOnDragOver(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				if (event.getGestureSource() != this && event.getDragboard().hasString()) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}
				event.consume();
			}
		});

	}
}
