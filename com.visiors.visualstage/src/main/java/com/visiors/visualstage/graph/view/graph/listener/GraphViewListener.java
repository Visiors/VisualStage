package com.visiors.visualstage.graph.view.graph.listener;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.node.NodeView;

public interface GraphViewListener {

	/**
	 * Invoked after a node was added to the graph.
	 * 
	 * @param node
	 *            the added node.
	 */
	public void nodeAdded(NodeView node);

	/**
	 * Invoked after a node was removed from the graph.
	 * 
	 * @param node
	 *            the node that have been was removed.
	 */
	public void nodeRemoved(NodeView node);

	void nodeStartedChangingBoundary(NodeView node);

	void nodeBoundaryChangning(NodeView node);

	void nodeStoppedChangingBoundary(NodeView node, Rectangle oldBoundary);

	public void edgeStartedChangingPath(EdgeView edge);

	public void edgePathChanging(EdgeView edge);

	public void edgeStoppedChangingPath(EdgeView edge, EdgePoint[] oldPath);

	public void nodeSelectionChanged(NodeView node);

	public void edgeAdded(EdgeView edge);

	public void edgeRemoved(EdgeView edge);

	public void edgeReassigned(EdgeView edge, NodeView oldConnecedNod, int oldPort,
			boolean sourceNode);

	public void edgePortReassigned(EdgeView edge, int oldPortID, boolean sourcePortChanged);

	public void edgeSelectionChanged(EdgeView edge);

	public void startGrouping(GraphView group);

	public void endGrouping(GraphView group);

	/**
	 * View needs to be repainted
	 * 
	 */
	public void viewChanged(GraphView graph);

	/**
	 * changes to graph redarding expansion of graph
	 * 
	 * @param graph
	 */
	public void graphManipulated(GraphView graph);

	/**
	 * graph's boundary has changed
	 */
	void graphExpansionChanged(GraphView graph, Rectangle newBoundary);
}
