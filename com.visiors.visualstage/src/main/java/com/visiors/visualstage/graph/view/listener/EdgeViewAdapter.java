package com.visiors.visualstage.graph.view.listener;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.node.NodeView;

public class EdgeViewAdapter implements EdgeViewListener {

	@Override
	public void edgeSelectionChanged(EdgeView edge) {

	}

	@Override
	public void edgeHighlightingChanged(EdgeView edge) {

	}

	@Override
	public void edgePathChanging(EdgeView edge) {

	}

	@Override
	public void edgeStartChangingPath(EdgeView edge) {

	}

	@Override
	public void edgeReconnected(EdgeView edge, NodeView oldConnecedNode, int oldPortID,
			boolean sourceNodeChanged) {

	}

	@Override
	public void edgeStoppedChangingPath(EdgeView edge, EdgePoint[] oldPath) {

	}

}
