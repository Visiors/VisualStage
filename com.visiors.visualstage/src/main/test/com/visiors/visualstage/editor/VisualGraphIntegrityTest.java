package com.visiors.visualstage.editor;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.editor.impl.GraphEditor;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.pool.ShapeDefinitionCollection;

public class VisualGraphIntegrityTest {

	@Test
	public void testGraphCreation() throws IOException {

		GraphEditor editor = new GraphEditor();
		initShapeDefinitionCollection(editor);
		GraphDocument document = editor.newDocument("GraphDocument");
		Assert.assertNotNull(document);
		// create two nodes and an edge using the default nodes and edge
		VisualGraph graph = document.getGraph();
		Assert.assertNotNull(graph);
		VisualNode startNode = graph.createNode("TestNode");
		VisualNode endNode = graph.createNode("TestNode");
		VisualEdge edge = graph.createEdge("TesConnector");
		Assert.assertNotNull(startNode);
		Assert.assertNotNull(endNode);
		Assert.assertNotNull(edge);
		// connect the both nodes using the edge
		edge.connect(startNode, 0, endNode, 0);
		Assert.assertEquals(edge.getSourceNode(), startNode);
		Assert.assertEquals(edge.getTargetNode(), endNode);
		Assert.assertEquals(edge.getSourcePortId(), 0);
		Assert.assertEquals(edge.getTargetPortId(), 0);
		Assert.assertEquals(edge.getSourceNode().getIndegree(), 0);
		Assert.assertEquals(edge.getSourceNode().getOutdegree(), 1);
		Assert.assertEquals(edge.getSourceNode().getOutgoingEdges().get(0), edge);
		Assert.assertEquals(edge.getTargetNode().getIndegree(), 1);
		Assert.assertEquals(edge.getTargetNode().getOutdegree(), 0);
		Assert.assertEquals(edge.getTargetNode().getIncomingEdges().get(0), edge);

		edge.connect(endNode, 0, startNode, 0);
		Assert.assertEquals(edge.getSourceNode(), endNode);
		Assert.assertEquals(edge.getTargetNode(), startNode);
		Assert.assertEquals(edge.getSourcePortId(), 0);
		Assert.assertEquals(edge.getTargetPortId(), 0);

		edge.connect(null, 0, null, 0);
		Assert.assertEquals(edge.getSourceNode(), null);
		Assert.assertEquals(edge.getTargetNode(), null);
		Assert.assertEquals(edge.getSourcePortId(), 0);
		Assert.assertEquals(edge.getTargetPortId(), 0);

		edge.connect(startNode, 2, startNode, 3);
		Assert.assertEquals(edge.getSourceNode(), startNode);
		Assert.assertEquals(edge.getTargetNode(), startNode);
		Assert.assertEquals(edge.getSourcePortId(), 2);
		Assert.assertEquals(edge.getTargetPortId(), 3);


		edge.connect(startNode, 0, endNode, 0);
		Assert.assertEquals(edge.getSourceNode().getIndegree(), 0);
		Assert.assertEquals(edge.getSourceNode().getOutdegree(), 1);
		Assert.assertEquals(edge.getSourceNode().getOutgoingEdges().get(0), edge);
		Assert.assertEquals(edge.getTargetNode().getIndegree(), 1);
		Assert.assertEquals(edge.getTargetNode().getOutdegree(), 0);
		Assert.assertEquals(edge.getTargetNode().getIncomingEdges().get(0), edge);

		graph.remove(edge);
		Assert.assertEquals(edge.getSourceNode().getIndegree(), 0);
		Assert.assertEquals(edge.getSourceNode().getOutdegree(), 0);
		Assert.assertEquals(edge.getTargetNode().getIndegree(), 0);
		Assert.assertEquals(edge.getTargetNode().getOutdegree(), 0);

		edge = graph.createEdge("Connector");
		edge.connect(startNode, 0, endNode, 0);
		graph.remove(startNode);
		Assert.assertEquals(startNode.getIndegree(), 0);
		Assert.assertEquals(startNode.getOutdegree(), 0);
		Assert.assertEquals(edge.getTargetNode().getIndegree(), 1);
		Assert.assertEquals(edge.getSourceNode(), null);
		Assert.assertEquals(edge.getTargetNode(), endNode);

		edge.connect(endNode, 0, endNode, 0);
		graph.remove(endNode);
		Assert.assertEquals(endNode.getIndegree(), 0);
		Assert.assertEquals(endNode.getOutdegree(), 0);
		Assert.assertEquals(edge.getSourceNode(), null);
		Assert.assertEquals(edge.getTargetNode(), null);

	}

	private void initShapeDefinitionCollection(Editor editor) throws IOException {

		ShapeDefinitionCollection shapesCollection = editor.getShapesCollection();
		String xmlContent = Files.toString(new File("src/main/test/com/visiors/visualstage/editor/graphExample.xml"),
				Charsets.UTF_8);
		shapesCollection.loadAndPool(xmlContent);

	}

	@Test
	public void testGraphCreationWithForm() {

		GraphEditor editor = new GraphEditor();

	}

	@Test
	public void testGraphCreationWithFormId() {

	}
}
