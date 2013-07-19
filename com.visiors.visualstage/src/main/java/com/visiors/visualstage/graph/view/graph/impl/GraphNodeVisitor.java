package com.visiors.visualstage.graph.view.graph.impl;

import com.visiors.visualstage.graph.view.node.VisualNode;

/**
 * This class is a  visitor for graphs. 
 * 
 */
public interface GraphNodeVisitor
{
	/**
	 * Callback invoked while visiting nodes.
	 * 
	 * @param subgraph the that is currently being visited.
	 * 
	 * @return return true to continue visiting nodes, or false to terminate the visitor.
	 */
	public boolean visit (VisualNode node);
}
