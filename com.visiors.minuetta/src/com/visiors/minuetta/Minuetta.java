package com.visiors.minuetta;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.visiors.minuetta.editor.MultiPageEditor;
import com.visiors.minuetta.view.PropertyView;
import com.visiors.minuetta.view.ShapeGallery;
import com.visiors.visualstage.document.GraphDocument;

public class Minuetta extends Application {

	public static void main(String[] args) {

		launch(args);
	}

	private MultiPageEditor multiPageEditor;

	@Override
	public void start(Stage primaryStage) {

		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 1024, 800, Color.LIGHTGREY   );
			//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("minuetta");

			root.setTop(createToolbar());
			root.setCenter(createApplicationFrame());
			root.setBottom(createStatusBar());

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Node createStatusBar() {

		HBox hbox = new HBox();

		Text text = new Text("Status bar");
		text.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

		hbox.getChildren().add(text);
		// VBox.setMargin(hbox, new Insets(12));
		HBox.setMargin(text, new Insets(6, 6, 10, 10));

		return hbox;
	}

	private Node createRepository() {

		final VBox vbox = new VBox();

		vbox.setMinHeight(100);
		vbox.setId("view");
		ShapeGallery gallery = new ShapeGallery();
		vbox.getChildren().add(gallery);
		VBox.setMargin(vbox, new Insets(10));

		return vbox;
	}

	private void addSeparator(Pane parent, String text) {

		Label label = new Label(text);
		label.setFont(Font.font("Calibri", FontWeight.NORMAL, 14));
		label.getStyleClass().add("separator-label");
		label.setUnderline(true);
		// label.setEffect(new DropShadow());
		// VBox.setMargin(label, new Insets(50, 10, 10, 10));

		parent.getChildren().add(label);
	}

	private Node createApplicationFrame() {

		SplitPane sp = new SplitPane();
		sp.getItems().addAll(createLeftPane(), createEditorArea(), createRightPane());
		sp.setDividerPositions(0.2f, 0.8f, 0.1f);
		return sp;
	}



	private Node createRightPane() {

		return  new PropertyView();
	}

	private Node createEditorArea() {

		this.multiPageEditor = new MultiPageEditor();

		return multiPageEditor;
	}

	private Node createLeftPane() {

		return createRepository();
	}

	private Node createToolbar() {

		ToolBar toolBar = new ToolBar();

		toolBar.setId("iphone-toolbar");

		Region spacer = new Region();
		spacer.getStyleClass().setAll("spacer");

		HBox buttonBar = new HBox();

		Button btn1 = ButtonBuilder.create().text("New").id("iphone").build();
		Button btn2 = ButtonBuilder.create().text("Zoom In").id("ZoomIn").build();
		Button btn3 = ButtonBuilder.create().text("Zoom Out").id("ZoomOut").build();
		buttonBar.getChildren().addAll(btn1, new Separator(), btn2, btn3);
		toolBar.getItems().addAll(spacer, buttonBar);

		btn1.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				multiPageEditor.newDocument(""+System.currentTimeMillis());
			}
		});
		btn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				final GraphDocument document = multiPageEditor.getEditor().getActiveDocument();
				document.setZoom(document.getZoom() + 0.1);
			}
		});
		btn3.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				final GraphDocument document = multiPageEditor.getEditor().getActiveDocument();
				document.setZoom(document.getZoom() - 0.1);
			}
		});
		return toolBar;
	}

}
