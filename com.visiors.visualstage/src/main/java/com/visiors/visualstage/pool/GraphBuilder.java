package com.visiors.visualstage.pool;

import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;
import com.visiors.visualstage.property.PropertyList;

public interface GraphBuilder {

	public VisualNode createNode();

	public VisualNode createNode(String name);

	public VisualNode createNode(PropertyList properties);

	public VisualEdge createEdge();

	public VisualEdge createEdge(String name);

	public VisualEdge createEdge(PropertyList properties);

	public VisualGraph createSubgraph();

	public VisualGraph createSubgraph(String name);

	public VisualGraph createSubgraph(PropertyList properties);

}
