package com.visiors.visualstage.graph.view.graph.listener;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;

public interface GraphViewListener {

	/**
	 * Invoked after a node was added to the graph.
	 * 
	 * @param node
	 *            the added node.
	 */
	public void nodeAdded(VisualNode node);

	/**
	 * Invoked after a node was removed from the graph.
	 * 
	 * @param node
	 *            the node that have been was removed.
	 */
	public void nodeRemoved(VisualNode node);

	void nodeStartedChangingBoundary(VisualNode node);

	void nodeBoundaryChangning(VisualNode node);

	void nodeStoppedChangingBoundary(VisualNode node, Rectangle previousBoundary);

	public void edgeStartedChangingPath(VisualEdge edge);

	public void edgePathChanging(VisualEdge edge);

	public void edgeStoppedChangingPath(VisualEdge edge, EdgePoint[] previousPath);

	public void nodeSelectionChanged(VisualNode node);

	public void edgeAdded(VisualEdge edge);

	public void edgeRemoved(VisualEdge edge);

	public void edgeReassigned(VisualEdge edge, VisualNode previousSourceNode, int previousSourcePort,
			VisualNode previousTagetNode, int previousTargetPort);

	public void edgePortReassigned(VisualEdge edge, int previousPort, boolean sourcePortChanged);

	public void edgeSelectionChanged(VisualEdge edge);

	public void startGrouping(VisualGraph group);

	public void endGrouping(VisualGraph group);

	/**
	 * View needs to be repainted
	 * 
	 */
	public void viewChanged(VisualGraph graph);

	/**
	 * changes to graph redarding expansion of graph
	 * 
	 * @param graph
	 */
	public void graphManipulated(VisualGraph graph);

	/**
	 * graph's boundary has changed
	 */
	void graphExpansionChanged(VisualGraph graph, Rectangle newBoundary);
}
