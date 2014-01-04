package com.visiors.minuetta;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.google.common.io.CharStreams;
import com.visiors.minuetta.editor.MultiPageEditor;
import com.visiors.minuetta.view.PropertyView;
import com.visiors.minuetta.view.Repository;
import com.visiors.minuetta.view.RepositoryView;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.Editor;
import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.pool.FormatDefinitionCollection;
import com.visiors.visualstage.pool.ShapeDefinitionCollection;
import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;

public class Minuetta extends Application {

	public static void main(String[] args) {

		launch(args);
	}

	private MultiPageEditor multiPageEditor;

	@Override
	public void start(Stage primaryStage) {

		try {
			BorderPane root = new BorderPane();

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Scene scene = new Scene(root, screenSize.width - 400, screenSize.height - 300, Color.LIGHTGREY);			
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



	private Node createApplicationFrame() {

		SplitPane sp = new SplitPane();
		multiPageEditor = createEditorArea();
		Node left = createLeftPane();
		Node right = createRightPane();
		sp.getItems().addAll(left, multiPageEditor, right);
		sp.setDividerPositions(0.2f, 0.8f);
		return sp;
	}

	private Node createRightPane() {

		return new PropertyView();
	}

	private MultiPageEditor createEditorArea() {
		MultiPageEditor multiPageEditor = new MultiPageEditor();
		setupGraphEditor(multiPageEditor.getEditor());
		return multiPageEditor;
	}

	private void setupGraphEditor(Editor editor) {

		try {
			String resource = "/com/visiors/minuetta/resources/flowchart.xml";
			String xmlContent = readResource(resource);
			final ShapeDefinitionCollection shapesCollection = editor.getShapesCollection();
			shapesCollection.loadAndPool(xmlContent);

			resource = "/com/visiors/minuetta/resources/flowchartFormat.xml";
			xmlContent = readResource(resource);
			FormatDefinitionCollection formatCollection = editor.getFormatsCollection();
			formatCollection.loadAndPool(xmlContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Node createRepository() {

		String resource = "/com/visiors/minuetta/resources/repository.xml";
		final VBox vbox = new VBox();
		try {
			vbox.setMinHeight(100);
			vbox.setId("view");
			GraphEditor graphEditor = multiPageEditor.getEditor();
			Repository repository = new Repository(graphEditor);
			repository.loadRepository(readResource(resource));
			RepositoryView repositoryView = new RepositoryView(repository);
			vbox.getChildren().add(repositoryView);
			VBox.setMargin(vbox, new Insets(10));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("The content if the Repository-View could not be loaded. Reason: " + e.getMessage());
		}
		return vbox;
	}

	private String readResource(String resourcePath) throws UnsupportedEncodingException, IOException {

		InputStream stream = getClass().getResourceAsStream(resourcePath);
		return CharStreams.toString(new InputStreamReader(stream, "UTF-8"));
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

		Button btn0 = ButtonBuilder.create().text("Save").id("iphone").build();
		Button btn1 = ButtonBuilder.create().text("New").id("iphone").build();
		Button btn2 = ButtonBuilder.create().text("Zoom In").id("iphone").build();
		Button btn3 = ButtonBuilder.create().text("Zoom Out").id("iphone").build();
		Button btn4 = ButtonBuilder.create().text("Zoom 100%").id("iphone").build();
		Button btn5 = ButtonBuilder.create().text(" (0, 0)").id("iphone").build();
		Button btn6 = ButtonBuilder.create().text("Show/Hide Ruler").id("iphone").build();
		Button btn7 = ButtonBuilder.create().text("Show/Hide ScrollBar").id("iphone").build();
		Button btn8 = ButtonBuilder.create().text("Delete").id("iphone").build();
		buttonBar.getChildren().addAll(btn0,btn1, new Separator(), btn2, btn3, btn4, btn5, btn6, btn7, btn8);
		toolBar.getItems().addAll(spacer, buttonBar);

		btn0.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				GraphEditor editor = multiPageEditor.getEditor();
				//String doc = editor.saveDocument(editor.getActiveDocument().getTitle());
				//System.err.println("\n "+doc);

				String doc = editor.getActiveDocument().getSVGDocument(new DefaultDrawingContext(Resolution.SCREEN_LOW_DETAIL, DrawingSubject.OBJECT));
				final Clipboard clipboard = Clipboard.getSystemClipboard();
				final ClipboardContent content = new ClipboardContent();
				content.putString(doc);
				clipboard.setContent(content);
			}
		});
		btn1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				multiPageEditor.newDocument("" + System.currentTimeMillis());
			}
		});
		btn2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				final GraphDocument document = multiPageEditor.getEditor().getActiveDocument();
				document.setZoom(document.getZoom() + 0.1);
			}
		});
		btn3.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				final GraphDocument document = multiPageEditor.getEditor().getActiveDocument();
				document.setZoom(document.getZoom() - 0.1);
			}
		});
		btn4.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				final GraphDocument document = multiPageEditor.getEditor().getActiveDocument();
				document.setZoom(1.0);
			}
		});
		btn5.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				final GraphDocument document = multiPageEditor.getEditor().getActiveDocument();
				document.setViewportPos(0, 0);
			}
		});
		btn6.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				boolean b = multiPageEditor.getEditor().getStageDesigner().isRulerVisible();
				multiPageEditor.getEditor().getStageDesigner().showRuler(!b);
			}
		});
		btn7.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				boolean b = multiPageEditor.getEditor().getStageDesigner().isScrollBarVisible();
				multiPageEditor.getEditor().getStageDesigner().showScrollBar(!b);
			}
		});
		btn8.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				GraphEditor editor = multiPageEditor.getEditor();
				List<VisualGraphObject> selection = editor.getSelectionHandler().getSelection();
				if(!selection.isEmpty()) {
					editor.getActiveDocument().getGraph().remove(selection.toArray(new VisualGraphObject[0]) );
				}
			}
		});

		return toolBar;
	}

}
