package com.visiors.visualstage.editor;




import org.junit.Assert;
import org.junit.Test;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;


public class VisualGraphTest {

	@Test
	public void testGraphCreation() {

		GraphEditor editor = new GraphEditor();
		editor.addCanvas(new CanvasImpl());
		GraphDocument document = editor.newDocument("GraphDocument");
		Assert.assertNotNull(document);
		// create two nodes and an edge using the default nodes and edge
		VisualGraph graph = document.getGraph();
		Assert.assertNotNull(graph);
		VisualNode n1 = graph.createNode("default");
		VisualNode n2 = graph.createNode("default");
		VisualEdge e = graph.createEdge("default");
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

}
