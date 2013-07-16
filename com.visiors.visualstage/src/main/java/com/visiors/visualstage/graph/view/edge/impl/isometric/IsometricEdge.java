package com.visiors.visualstage.graph.view.edge.impl.isometric;

import java.awt.Point;

import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.edge.impl.curved.polyline.PolygonalEdgeView;

public class IsometricEdge extends PolygonalEdgeView {

	public IsometricEdge(String name) {

		super(name);
	}

	protected IsometricEdge(String name, long id) {

		super(name, id);
	}

	protected IsometricEdge(VisualEdge edge, long id) {

		super(edge, id);
	}

	@Override
	public VisualEdge deepCopy(long id) {

		return new IsometricEdge(this, id);
	}

	@Override
	public void pathChanging() {

		// if(!isPinned())
		{
			Point[] points = getPoints();
			int processingIndex = manipulationID == points.length - 1 ? manipulationID : -1;
			points = IsometricEdgeRouter.routeEdge(this, points, 10, 10);

			if (processingIndex != -1) {
				manipulationID = points.length - 1;
			}

			setPoints(points);
		}

		super.pathChanging();
	}
}
