package com.visiors.visualstage.graph;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.visiors.visualstage.graph.model.EdgeModel;
import com.visiors.visualstage.graph.model.GraphModel;
import com.visiors.visualstage.graph.model.NodeModel;
import com.visiors.visualstage.graph.model.impl.DefaultEdgeModel;
import com.visiors.visualstage.graph.model.impl.DefaultGraphModel;
import com.visiors.visualstage.graph.model.impl.DefaultNodeModel;

public class GraphModel_Test {

	@Test
	public void dataIntegrityTest() {

		GraphModel graph = new DefaultGraphModel();

		EdgeModel edgeA = new DefaultEdgeModel();
		EdgeModel edgeB = new DefaultEdgeModel();
		NodeModel nodeA = new DefaultNodeModel();
		NodeModel nodeB = new DefaultNodeModel();
		NodeModel nodeC = new DefaultNodeModel();

		// add some nodes and edges
		graph.add(edgeA);
		graph.add(edgeB);
		graph.add(nodeA);
		graph.add(nodeB);
		graph.add(nodeC);
		// connect nodes
		edgeA.setSourceNode(nodeA);
		edgeA.setTargetNode(nodeB);
		edgeB.setSourceNode(nodeA);
		edgeB.setTargetNode(nodeC);
		// verify edges internal data
		assertEquals(edgeA.getSourceNode(), nodeA);
		assertEquals(edgeA.getTargetNode(), nodeB);
		assertEquals(edgeB.getSourceNode(), nodeA);
		assertEquals(edgeB.getTargetNode(), nodeC);
		// verify nodes' internal data
		assertEquals(nodeA.getIndegree(), 0);
		assertEquals(nodeA.getOutdegree(), 2);
		assertEquals(nodeA.getOutgoingEdges().get(0), edgeA);
		assertEquals(nodeA.getDegree(), 2);
		assertEquals(nodeB.getIndegree(), 1);
		assertEquals(nodeB.getIncomingEdges().get(0), edgeA);
		assertEquals(nodeB.getOutdegree(), 0);
		assertEquals(nodeC.getIndegree(), 1);
		assertEquals(nodeC.getIncomingEdges().get(0), edgeB);
		assertEquals(nodeC.getOutdegree(), 0);
		// verify graphs data
		assertEquals(graph.getNodes().size(), 3);
		assertEquals(graph.getEdges().size(), 2);
		// remove and edge
		graph.remove(edgeA);
		// verify edges data
		assertEquals(edgeA.getSourceNode(), null);
		assertEquals(edgeA.getTargetNode(), null);
		// verify graph' internal data
		assertEquals(graph.getEdge(edgeA.getID()), null);
		assertEquals(graph.getNodes().size(), 3);
		assertEquals(graph.getEdges().size(), 1);

		// verify nodes' internal data
		assertEquals(nodeA.getIndegree(), 0);
		assertEquals(nodeA.getOutdegree(), 1);
		assertEquals(nodeB.getIndegree(), 0);
		assertEquals(nodeB.getOutdegree(), 0);
		assertEquals(nodeC.getIndegree(), 1);
		assertEquals(nodeC.getIncomingEdges().get(0), edgeB);
		assertEquals(nodeC.getOutdegree(), 0);

		// remove a connected node
		graph.remove(nodeA);
		// verify graph' internal data
		assertEquals(graph.getNodes().size(), 2);
		assertEquals(graph.getEdges().size(), 1);
		assertEquals(graph.getNode(nodeA.getID()), null);

		// verify nodes' internal data
		assertEquals(nodeA.getIndegree(), 0);
		assertEquals(nodeA.getOutdegree(), 0);
		assertEquals(nodeC.getIndegree(), 1);
		assertEquals(nodeC.getOutdegree(), 0);

		// verify edges internal data
		assertEquals(edgeB.getSourceNode(), null);
		assertEquals(edgeB.getTargetNode(), nodeC);
	}

	@Test (timeout = 4000)
	public void performanceTest() {
		Random randomGenerator = new Random();
		GraphModel graph = new DefaultGraphModel();

		final int n = 1000000;
		final int e = 1000000;
		// create n nodes and n x n edges
		for (int i = 0; i < n; i++) {
			graph.add(new DefaultNodeModel());
		}
		List<NodeModel> nodes = graph.getNodes();

		for (int i = 0; i < e; i++) {
			int s = randomGenerator.nextInt(nodes.size() - 1);
			int t = randomGenerator.nextInt(nodes.size() - 1);
			DefaultEdgeModel edge = new DefaultEdgeModel();
			graph.add(edge);
			edge.setSourceNode(nodes.get(s));
			edge.setSourceNode(nodes.get(t));
		}
	}
}
