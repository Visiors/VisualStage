package com.visiors.visualstage.graph.view.graph.impl;

import java.awt.Rectangle;

import com.visiors.visualstage.graph.view.GraphObjectView;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.node.NodeView;

public class DepotObject {

	GraphObjectView object;

	int typeFixedPriority;
	int order;
	int x1;
	int x2;
	int y1;
	int y2;

	public DepotObject(GraphObjectView object, int order) {
		this.object = object;
		this.order = order;
		if (object instanceof GraphView) {
			typeFixedPriority = 1;
		} else if (object instanceof NodeView) {
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
