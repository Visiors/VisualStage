package com.visiors.visualstage.graph.view.listener;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.node.NodeView;

public interface NodeViewListener {

	void nodeSelectionChanged(NodeView node);

	void nodeHighlightingChanged(NodeView node);

	void nodeStartedChangingBoundary(NodeView node);

	void nodeBoundaryChangning(NodeView node);

	void nodeStoppedChangingBoundary(NodeView node, Rectangle oldBoundary);

	void nodeManipulated();
	// void nodeStartedMoving(NodeView node);
	// void nodeMoving(NodeView node);
	// void nodeStoppedMoving(NodeView node, int dx, int dy);
}
