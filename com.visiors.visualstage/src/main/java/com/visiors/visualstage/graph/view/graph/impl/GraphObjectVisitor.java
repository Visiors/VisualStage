package com.visiors.visualstage.graph.view.graph.impl;

import com.visiors.visualstage.graph.view.VisualGraphObject;

/**
 * This class is a  visitor for graphs. 
 * 
 */
public interface GraphObjectVisitor
{
	/**
	 * Callback invoked while visiting visual graph objects.
	 * 
	 * @param subgraph the that is currently being visited.
	 * 
	 * @return return true to continue visiting nodes, or false to terminate the visitor.
	 */
	public boolean visit (VisualGraphObject vgo);
}
