package com.visiors.minuetta.view;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class RepositoryViewItem extends VBox {

	private static final double DEFAULT_FONT_SIZE = 11;
	private static final double DEFAULT_IMAGE_SIZE = 80;
	private static final double SNAP_TICK = 10.0;

	private static final String FONT_NAME = "Tahoma";
	private static final String FONT_COLOR = "#969696";

	private static final String styleSelected = "-fx-border-insets:0;\n" + "-fx-border-radius:4;\n"
			+ "-fx-border-width:1.0;\n" + "-fx-background-color:#FCF6DF;\n" + "-fx-border-color:#ffbd69";
	private static final String styleUnselected = "-fx-border-insets:0;\n" + "-fx-border-radius:4;\n"
			+ "-fx-border-width:1.0;\n" + "-fx-background-color:transparent;\n" + "-fx-border-color:white";

	private final RepositoryItem repositoryItem;
	private ImageView imageView;
	private double fontScale = 1.0;
	private double imageScale = 1.0;
	private Label label;

	public RepositoryViewItem(RepositoryItem repositoryItem) {

		this.repositoryItem = repositoryItem;

		setPadding(new Insets(8, 8, 8, 8));
	}

	public void create() {

		this.imageView = new ImageView(repositoryItem.getShapePreview(getNormalizeImageSize()));
		this.label = new Label(repositoryItem.getDisplayName(), imageView);
		label.setTooltip(new Tooltip(repositoryItem.getTooltip()));
		label.setContentDisplay(ContentDisplay.TOP);
		label.setFont(Font.font(FONT_NAME, FontWeight.NORMAL, getNormalizeFontSize()));
		label.setTextFill(Color.web(FONT_COLOR));
		label.setWrapText(false);
		label.setTextAlignment(TextAlignment.LEFT);
		label.setGraphicTextGap(0);
		getChildren().add(label);
		addEventHandler();
		setSelected(false);
	}

	public void setFontScale(double fontScale) {

		fontScale = (int) (fontScale * SNAP_TICK) / SNAP_TICK;
		if (this.fontScale != fontScale) {
			this.fontScale = fontScale;
			if (label != null) {
				label.setFont(Font.font(FONT_NAME, FontWeight.NORMAL, getNormalizeFontSize()));
			}
		}
	}

	public void setImageScale(double imageScale) {

		imageScale = imageScale / SNAP_TICK * SNAP_TICK;
		if (this.imageScale != imageScale) {
			this.imageScale = imageScale;
			if (this.imageView != null) {
				this.imageView = new ImageView(repositoryItem.getShapePreview(getNormalizeImageSize()));
				label.setGraphic(imageView);
			}
		}
	}

	private int getNormalizeFontSize() {

		return (int) (DEFAULT_FONT_SIZE * fontScale);
	}

	private int getNormalizeImageSize() {

		return (int) (DEFAULT_IMAGE_SIZE * imageScale);
	}

	private void addEventHandler() {

		addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				setSelected(true);
			}
		});
		addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				setSelected(false);
			}
		});

		label.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				final Dragboard db = label.startDragAndDrop(TransferMode.COPY);
				final ClipboardContent content = new ClipboardContent();
				content.putString(repositoryItem.getShapeName() + "#" + repositoryItem.getType());
				db.setContent(content);
				event.consume();
			}
		});

	}

	private void setSelected(boolean selected) {

		if (selected) {
			setStyle(styleSelected);
		} else {
			setStyle(styleUnselected);
		}
	}
}
