package com.visiors.visualstage.graph.view.graph.impl;

import com.visiors.visualstage.graph.view.graph.VisualGraph;

/**
 * This class is a  visitor for graphs.
 * 
 */
public interface GraphVisitor
{
	/**
	 * Callback invoked while visiting subgraphs.
	 * 
	 * @param subgraph the that is currently being visited.
	 * 
	 * @return return true to continue visiting nodes, or false to terminate the visitor.
	 */
	public boolean visit (VisualGraph subgraph);
}
