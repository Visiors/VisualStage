package com.visiors.minuetta.editor;

import java.awt.Rectangle;
import java.util.Random;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;

import com.visiors.minuetta.GraphCanvas;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;

public class EditorPage extends Tab implements EventHandler<Event> {

	private final GraphEditor editor;
	private final GraphCanvas graphCanvas;
	private final GraphDocument document;



	public EditorPage(GraphEditor editor, String title) {

		super();

		this.editor = editor;
		this.document = editor.newDocument(title);
		this.graphCanvas = new GraphCanvas(editor);
		setContent(graphCanvas);				
		setText(document.getTitle());
		setTooltip(new Tooltip(document.getTitle()));
		setId(title);
		setOnClosed(this);

		createRandomGraph();
	}



	private void createRandomGraph() {

		Random rnd = new Random();
		VisualGraph graph = document.getGraph();

		for (int n = 0; n < 10; ++n) {

			VisualNode node = graph.createNode();
			node.setBounds(new Rectangle(0, 0, 80, 50));
			int x = rnd.nextInt(800);
			int y = rnd.nextInt(800);
			node.move(x, y);

		}

		//		VisualEdge edge = graph.createEdge();
		//
		//		EdgePoint points[] = new EdgePoint[2];
		//		points[0] = new EdgePoint(new Point(100,300));
		//		points[1] = new EdgePoint(new Point(400,100));
		//		edge.getPath().setPoints(points, false);

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
