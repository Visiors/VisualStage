package com.visiors.minuetta;

import java.awt.Point;
import java.awt.Rectangle;

import javafx.application.Application;
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

import com.visiors.minuetta.editor.GraphEditorPane;
import com.visiors.minuetta.view.PropertyView;
import com.visiors.minuetta.view.ShapeGallery;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;

public class Minuetta extends Application {

	public static void main(String[] args) {

		launch(args);
	}

	private GraphEditor editor;

	@Override
	public void start(Stage primaryStage) {

		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 1024, 800, Color.WHITE);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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

		final GraphEditorPane graphPane = new GraphEditorPane();
		this.editor = graphPane.getEditor();

		GraphDocument document = editor.newDocument("New Document");
		VisualGraph graph = document.getGraph();
		VisualNode startNode = graph.createNode();
		startNode.setBounds(new Rectangle(100, 100, 100, 100));
		VisualNode endNode = graph.createNode();
		endNode.setBounds(new Rectangle(300, 300, 80, 80));
		VisualEdge edge = graph.createEdge();

		EdgePoint points[] = new EdgePoint[2];
		points[0] = new EdgePoint(new Point(100,300));
		points[1] = new EdgePoint(new Point(400,100));
		edge.getPath().setPoints(points, false);


		return graphPane;
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

		Button btn1 = ButtonBuilder.create().text("Copy").id("iphone").build();
		Button btn2 = ButtonBuilder.create().text("Cut").id("iphone").build();
		Button btn3 = ButtonBuilder.create().text("Paste").id("iphone").build();
		buttonBar.getChildren().addAll(btn1, new Separator(), btn2, btn3);
		toolBar.getItems().addAll(spacer, buttonBar);
		return toolBar;
	}

}
