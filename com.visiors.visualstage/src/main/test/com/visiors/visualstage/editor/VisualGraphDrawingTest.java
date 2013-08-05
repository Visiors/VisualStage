package com.visiors.visualstage.editor;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.pool.FormatCollection;
import com.visiors.visualstage.pool.ShapeCollection;

public class VisualGraphDrawingTest {

	@Test
	public void testCanvas() throws IOException {

		GraphEditor editor = new GraphEditor();
		MyCanvasImpl canvas = new MyCanvasImpl();
		editor.addCanvas(canvas);
		initShapeDefinitionCollection(editor);
		GraphDocument document = editor.newDocument("GraphDocument");
		Assert.assertNotNull(document);
		// create two nodes and an edge using the default nodes and edge
		VisualGraph graph = document.getGraph();
		Assert.assertNotNull(graph);
		VisualNode startNode = graph.createNode("StartNode");
		startNode.setBounds(new Rectangle(0,0, 100, 100));
		VisualNode endNode = graph.createNode("EndNode");
		endNode.setBounds(new Rectangle(200, 100, 80, 80));
		VisualEdge edge = graph.createEdge("Connector");
		Assert.assertNotNull(startNode);
		Assert.assertNotNull(endNode);
		Assert.assertNotNull(edge);
		// connect the both nodes using the edge
		edge.connect(startNode, 0, endNode, 0); 

		canvas.save();
	}

	private void initShapeDefinitionCollection(Editor editor) throws IOException {

		final ShapeCollection shapesCollection = editor.getShapesCollection();
		String xmlContent = Files.toString(new File("src/main/test/com/visiors/visualstage/editor/GraphObjecDefinition.xml"),
				Charsets.UTF_8);
		shapesCollection.loadAndPool(xmlContent);

		final FormatCollection formatsCollection = editor.getFormatsCollection();
		xmlContent = Files.toString(new File("src/main/test/com/visiors/visualstage/editor/FormatDefinition.xml"),
				Charsets.UTF_8);
		formatsCollection.loadAndPool(xmlContent);

	}

	@Test
	public void testGraphCreationWithForm() {

		GraphEditor editor = new GraphEditor();

	}

	@Test
	public void testGraphCreationWithFormId() {

	}
}
