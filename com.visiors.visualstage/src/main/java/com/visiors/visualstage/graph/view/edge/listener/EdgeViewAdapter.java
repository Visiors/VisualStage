package com.visiors.visualstage.graph.view.edge.listener;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.node.VisualNode;

public class EdgeViewAdapter implements EdgeViewListener {

	@Override
	public void edgeSelectionChanged(VisualEdge edge) {

	}

	@Override
	public void edgeHighlightingChanged(VisualEdge edge) {

	}

	@Override
	public void edgePathChanging(VisualEdge edge) {

	}

	@Override
	public void edgeStartChangingPath(VisualEdge edge) {

	}

	@Override
	public void edgeReconnected(VisualEdge edge, VisualNode oldConnecedNode, int oldPortID,
			boolean sourceNodeChanged) {

	}

	@Override
	public void edgeStoppedChangingPath(VisualEdge edge, EdgePoint[] oldPath) {

	}

}
