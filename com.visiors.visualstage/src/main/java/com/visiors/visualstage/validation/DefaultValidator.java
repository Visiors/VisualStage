package com.visiors.visualstage.validation;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.edge.EdgeView;
import com.visiors.visualstage.graph.view.edge.Path;
import com.visiors.visualstage.graph.view.node.NodeView;

public class DefaultValidator implements Validator {

	@Override
	public boolean permitAddingNode(NodeView node) {

		return true;
	}

	@Override
	public boolean permitDeletingNode(NodeView node) {

		return true;
	}

	@Override
	public boolean permitAddingEdge(EdgeView edge) {

		return true;
	}

	@Override
	public boolean permitDeletingEdge(EdgeView edge) {

		return true;
	}

	@Override
	public boolean permitMovingEdge(EdgeView edge, int dx, int dy) {

		return true;
	}

	@Override
	public boolean permitResizingEdge(EdgeView edge, Path newPath) {

		return true;
	}

	@Override
	public boolean permitMovingNode(NodeView node, int dx, int dy) {

		return true;
	}

	@Override
	public boolean permitResizingNode(NodeView node, Rectangle targetBoundary) {

		return true;
	}

	@Override
	public boolean permitConnection(NodeView sourceNode, int sourcePortId, EdgeView edge,
			NodeView targetNode, int targetPortId) {

		return true;
	}

}
