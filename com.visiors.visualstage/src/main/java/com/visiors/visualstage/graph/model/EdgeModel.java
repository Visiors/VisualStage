package com.visiors.visualstage.graph.model;

import com.visiors.visualstage.graph.listener.EdgeModelListener;

/**
 * <p>
 * This interface defines a graph <code>edge</code> (link, connection,
 * association). *
 */
public interface EdgeModel extends GraphObjectModel {

	/**
	 * Gets the {@link NodeModel} at the source end of this <code>Edge</code>. <br>
	 * The source node cannot be directly but only by the parent graph
	 */
	public NodeModel getSourceNode();

	/**
	 * Gets the {@link NodeModel} at the target end of this <code>Edge</code>. <br>
	 * The source node cannot be directly but only by the parent graph
	 */
	public NodeModel getTargetNode();

	/**
	 * Connects the edge to the the source node
	 */
	public void setSourceNode(NodeModel node);

	/**
	 * Connects the edge to the the target node
	 */
	public void setTargetNode(NodeModel node);

	public void addEdgeModelListener(EdgeModelListener listener);

	public void removeEdgeModelListener(EdgeModelListener listener);
}
