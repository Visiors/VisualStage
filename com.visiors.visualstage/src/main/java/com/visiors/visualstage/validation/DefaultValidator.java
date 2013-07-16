package com.visiors.visualstage.validation;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.Path;
import com.visiors.visualstage.graph.view.node.VisualNode;

public class DefaultValidator implements Validator {

	@Override
	public boolean permitAddingNode(VisualNode node) {

		return true;
	}

	@Override
	public boolean permitDeletingNode(VisualNode node) {

		return true;
	}

	@Override
	public boolean permitAddingEdge(VisualEdge edge) {

		return true;
	}

	@Override
	public boolean permitDeletingEdge(VisualEdge edge) {

		return true;
	}

	@Override
	public boolean permitMovingEdge(VisualEdge edge, int dx, int dy) {

		return true;
	}

	@Override
	public boolean permitResizingEdge(VisualEdge edge, Path newPath) {

		return true;
	}

	@Override
	public boolean permitMovingNode(VisualNode node, int dx, int dy) {

		return true;
	}

	@Override
	public boolean permitResizingNode(VisualNode node, Rectangle targetBoundary) {

		return true;
	}

	@Override
	public boolean permitConnection(VisualNode sourceNode, int sourcePortId, VisualEdge edge,
			VisualNode targetNode, int targetPortId) {

		return true;
	}

}
