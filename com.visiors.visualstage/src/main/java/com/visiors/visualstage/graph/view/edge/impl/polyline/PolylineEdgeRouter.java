package com.visiors.visualstage.graph.view.edge.impl.polyline;

import java.awt.Point;

public class PolylineEdgeRouter {

	public static Point[] routeEdge(PolygonalEdgeView edge) {
		
		Point[] points = edge.getPoints();
		final Point start = points[0];
		final Point end = points[points.length - 1];
		points = new Point[4];
		points[0] = start;
		points[1] = new Point(start.x + (end.x - start.x) / 2+15, start.y + (end.y - start.y) / 2 + 15) ;
		points[2] = new Point(start.x + (end.x - start.x) / 2-15, start.y + (end.y - start.y) / 2 - 15) ;
		points[3] = end;
		return points;
	}

}
