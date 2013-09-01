package com.visiors.minuetta.editor;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.GraphEditor;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;

public class EditorPage extends Tab implements EventHandler<Event> {

	private final GraphEditor editor;
	private final ScrollableCanvas scrollableCanvas;
	private final GraphDocument document;
	private final MultiPageEditor multiPageEditor;


	public EditorPage(MultiPageEditor multiPageEditor, String title) {

		super();

		this.multiPageEditor = multiPageEditor;
		this.editor = multiPageEditor.getEditor();
		this.document = editor.newDocument(title);		
		this.scrollableCanvas = new ScrollableCanvas(editor);
		setContent(scrollableCanvas);		
		editor.addCanvas(scrollableCanvas.getCanvas());
		setText(document.getTitle());
		setTooltip(new Tooltip(document.getTitle()));
		setId(title);
		setOnClosed(this);

		createRandomGraph();
	}



	private void createRandomGraph() {

		Random rnd = new Random();
		VisualGraph graph = document.getGraph();

		for (int n = 0; n < 1; ++n) {

			VisualNode node = graph.createNode();
			node.setBounds(new Rectangle(100, 100, 100, 100));
			int x = rnd.nextInt(400);
			int y = rnd.nextInt(400);
			node.move(x, y);

		}

		VisualEdge edge = graph.createEdge();

		EdgePoint points[] = new EdgePoint[2];
		points[0] = new EdgePoint(new Point(100,300));
		points[1] = new EdgePoint(new Point(400,100));
		edge.getPath().setPoints(points, false);

	}


	public GraphEditor getEditor() {

		return editor;
	}


	public GraphDocument getDocunemt() {

		return document;
	}



	@Override
	public void handle(Event e) {

		editor.closeDocument(document.getTitle());
		e.consume();
	}

}
