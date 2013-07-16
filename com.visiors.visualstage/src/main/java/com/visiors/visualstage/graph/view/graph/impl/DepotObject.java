package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.VisualGraphObject;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.node.VisualNode;

public class DepotObject {

	VisualGraphObject object;

	int typeFixedPriority;
	int order;
	int x1;
	int x2;
	int y1;
	int y2;

	public DepotObject(VisualGraphObject object, int order) {
		this.object = object;
		this.order = order;
		if (object instanceof VisualGraph) {
			typeFixedPriority = 1;
		} else if (object instanceof VisualNode) {
			typeFixedPriority = 2;
		} else {
			typeFixedPriority = 3;
		}
		updatePosition();
	}

	void updatePosition() {
		Rectangle r = object.getExtendedBoundary();
		this.x1 = r.x;
		this.x2 = r.x + r.width;
		this.y1 = r.y;
		this.y2 = r.y + r.height;
	}

}
