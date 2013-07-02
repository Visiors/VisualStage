package com.visiors.visualstage.graph.view.edge.listener;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.node.NodeView;

public interface EdgeViewListener {

	public void edgeSelectionChanged(EdgeView edge);

	public void edgeHighlightingChanged(EdgeView edge);

	public void edgeStartChangingPath(EdgeView edge);

	public void edgePathChanging(EdgeView edge);

	public void edgeStoppedChangingPath(EdgeView edge, EdgePoint[] oldPath);

	public void edgeReconnected(EdgeView edge, NodeView oldSourceNode, int oldSourcePortID,
			NodeView oldTagetNode, int oldTargetPortID);
}
