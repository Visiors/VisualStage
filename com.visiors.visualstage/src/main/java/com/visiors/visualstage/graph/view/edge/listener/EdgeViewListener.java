package com.visiors.visualstage.graph.view.edge.listener;

import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.node.VisualNode;

public interface EdgeViewListener {

	public void edgeSelectionChanged(VisualEdge edge);

	public void edgeHighlightingChanged(VisualEdge edge);

	public void edgeStartChangingPath(VisualEdge edge);

	public void edgePathChanging(VisualEdge edge);

	public void edgeStoppedChangingPath(VisualEdge edge, EdgePoint[] oldPath);

	public void edgeReconnected(VisualEdge edge, VisualNode oldSourceNode, int oldSourcePortID,
			VisualNode oldTagetNode, int oldTargetPortID);
}
