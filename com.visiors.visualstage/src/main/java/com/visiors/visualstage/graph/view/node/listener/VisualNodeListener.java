package com.visiors.visualstage.graph.view.node.listener;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.node.VisualNode;

public interface VisualNodeListener {

	void nodeSelectionChanged(VisualNode node);

	void nodeHighlightingChanged(VisualNode node);

	void nodeStartedChangingBoundary(VisualNode node);

	void nodeBoundaryChangning(VisualNode node);

	void nodeStoppedChangingBoundary(VisualNode node, Rectangle oldBoundary);

	void nodeManipulated();
	// void nodeStartedMoving(NodeView node);
	// void nodeMoving(NodeView node);
	// void nodeStoppedMoving(NodeView node, int dx, int dy);
}
