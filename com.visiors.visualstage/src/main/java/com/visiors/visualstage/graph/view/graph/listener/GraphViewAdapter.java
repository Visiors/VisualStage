package com.visiors.visualstage.graph.view.graph.listener;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;

/**
 * A convenience adapter-class for GraphViewListener.
 * 
 */

public class GraphViewAdapter implements GraphViewListener {

	@Override
	public void edgeAdded(VisualEdge edge) {

	}

	@Override
	public void edgeStartedChangingPath(VisualEdge edge) {

	}

	@Override
	public void edgePathChanging(VisualEdge edge) {

	}

	@Override
	public void edgeStoppedChangingPath(VisualEdge edge, EdgePoint[] oldPath) {

	}

	@Override
	public void edgePortReassigned(VisualEdge edge, int oldPortID, boolean sourcePortChanged) {

	}


	@Override
	public void edgeRemoved(VisualEdge edge) {

	}

	@Override
	public void edgeSelectionChanged(VisualEdge edge) {

	}

	@Override
	public void nodeAdded(VisualNode node) {

	}

	@Override
	public void nodeStartedChangingBoundary(VisualNode node) {

	}

	@Override
	public void nodeBoundaryChangning(VisualNode node) {

	}

	@Override
	public void nodeStoppedChangingBoundary(VisualNode node, Rectangle oldBoundary) {

	}

	@Override
	public void nodeRemoved(VisualNode node) {

	}

	@Override
	public void nodeSelectionChanged(VisualNode node) {

	}

	@Override
	public void graphManipulated(VisualGraph graph) {

	}

	@Override
	public void viewInvalid(VisualGraph graph) {

	}

	@Override
	public void graphExpansionChanged(VisualGraph graph) {

	}

	@Override
	public void startGrouping(VisualGraph group) {

	}

	@Override
	public void endGrouping(VisualGraph group) {

	}

	@Override
	public void edgeReassigned(VisualEdge edge, VisualNode previousSourceNode, int previousSourcePort,
			VisualNode previousTagetNode, int previousTargetPort) {

	}
}
