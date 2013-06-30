package com.visiors.visualstage.graph.view.listener;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.node.NodeView;

/**
 * A convenience adapter-class for GraphViewListener.
 * 
 */

public class GraphViewAdapter implements GraphViewListener {

	@Override
	public void edgeAdded(EdgeView edge) {

	}

	@Override
	public void edgeStartedChangingPath(EdgeView edge) {

	}

	@Override
	public void edgePathChanging(EdgeView edge) {

	}

	@Override
	public void edgeStoppedChangingPath(EdgeView edge, EdgePoint[] oldPath) {

	}

	@Override
	public void edgePortReassigned(EdgeView edge, int oldPortID, boolean sourcePortChanged) {

	}

	@Override
	public void edgeReassigned(EdgeView edge, NodeView oldConnectnode, int oldPortID,
			boolean sourceNode) {

	}

	@Override
	public void edgeRemoved(EdgeView edge) {

	}

	@Override
	public void edgeSelectionChanged(EdgeView edge) {

	}

	@Override
	public void nodeAdded(NodeView node) {

	}

	@Override
	public void nodeStartedChangingBoundary(NodeView node) {

	}

	@Override
	public void nodeBoundaryChangning(NodeView node) {

	}

	@Override
	public void nodeStoppedChangingBoundary(NodeView node, Rectangle oldBoundary) {

	}

	@Override
	public void nodeRemoved(NodeView node) {

	}

	@Override
	public void nodeSelectionChanged(NodeView node) {

	}

	@Override
	public void graphManipulated(GraphView graph) {

	}

	@Override
	public void viewChanged(GraphView graph) {

	}

	@Override
	public void graphExpansionChanged(GraphView graph, Rectangle newBoundary) {

	}

	@Override
	public void startGrouping(GraphView group) {

	}

	@Override
	public void endGrouping(GraphView group) {

	}
}
