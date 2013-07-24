package com.visiors.visualstage.editor;




import org.junit.Assert;
import org.junit.Test;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.exception.DocumentExistsException;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;


public class GraphEditorTest {

	@Test
	public void testNewDocument() {

		GraphEditor editor = new GraphEditor();
		GraphDocument document = editor.newDocument("New 1");
		Assert.assertNotNull(document);
		// create a two nodes and an edge
		VisualGraph graph = document.getGraph();
		Assert.assertNotNull(graph);
		VisualNode n1 = graph.createNode(1, "default");
		VisualNode n2 = graph.createNode(2, "default");
		VisualEdge e = graph.createEdge(3, "default");
		Assert.assertNotNull(n1);
		Assert.assertNotNull(n2);
		Assert.assertNotNull(e);
		// connect the both nodes using the edge
		e.connect(n1, 0, n2, 0);
		Assert.assertEquals(e.getSourceNode(), n1);
		Assert.assertEquals(e.getTargetNode(), n2);
		Assert.assertEquals(e.getSourcePortId(), 0);
		Assert.assertEquals(e.getTargetPortId(), 0);

	}

	@Test (expected = DocumentExistsException.class)
	public void testNameConflict() {

		GraphEditor editor = new GraphEditor();
		editor.newDocument("New 1");
		editor.newDocument("New 1");		
	}
}
