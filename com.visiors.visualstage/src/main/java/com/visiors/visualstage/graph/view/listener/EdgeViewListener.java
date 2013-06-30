package com.visiors.visualstage.graph.view.listener;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.node.NodeView;

public interface EdgeViewListener {

	public void edgeSelectionChanged(EdgeView edge);

	public void edgeHighlightingChanged(EdgeView edge);

	public void edgeReconnected(EdgeView edge, NodeView oldConnecedNode, int oldPortID,
			boolean sourceNodeChanged);

	public void edgeStartChangingPath(EdgeView edge);

	public void edgePathChanging(EdgeView edge);

	public void edgeStoppedChangingPath(EdgeView edge, EdgePoint[] oldPath);
}
