package com.visiors.visualstage.graph.view.edge.impl.orthogonal.rounded;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

public class RoundingTransformer {

	private final Point[] points;
	private final int size = 20;

	public RoundingTransformer(Point[] pts) {

		points = pts;
	}

	public Shape getPath() {

		Point p0;
		Point p1;
		Point p2;
		int radius;
		GeneralPath path = new GeneralPath();

		path.moveTo(points[0].x, points[0].y);

		for (int i = 1; i < points.length - 1; i++) {

			p0 = points[i - 1];
			p1 = points[i];
			p2 = points[i + 1];

			radius = this.size;
			if (p0.x == p1.x && p1.x != p2.x) {
				if (p0.y < p1.y) // -> South
				{
					radius = Math.min(radius, (p1.y - p0.y) / 2);
					if (p1.x < p2.x) // East.
					{
						radius = Math.min(radius, (p2.x - p1.x) / 2);
						path.lineTo(p1.x, p1.y - radius);
						path.append(new Arc2D.Double(p1.x, p1.y - radius, radius, radius, 180, 90,
								Arc2D.OPEN), true);
					} else if (p1.x > p2.x)// West.
					{
						radius = Math.min(radius, (p1.x - p2.x) / 2);
						path.lineTo(p1.x, p1.y - radius);
						path.append(new Arc2D.Double(p1.x - radius, p1.y - radius, radius, radius,
								0, -90, Arc2D.OPEN), true);
					}
				} else if (p0.y > p1.y) // -> North
				{
					radius = Math.min(radius, (p0.y - p1.y) / 2);
					if (p1.x < p2.x) // East.
					{
						radius = Math.min(radius, (p2.x - p1.x) / 2);
						path.lineTo(p1.x, p1.y + radius);
						path.append(new Arc2D.Double(p1.x, p1.y, radius, radius, 180, -90,
								Arc2D.OPEN), true);
					} else if (p1.x > p2.x)// West
					{
						radius = Math.min(radius, (p1.x - p2.x) / 2);
						path.lineTo(p1.x, p1.y + radius);
						path.append(new Arc2D.Double(p1.x - radius, p1.y, radius, radius, 0, 90,
								Arc2D.OPEN), true);
					}
				}
			} else if (p0.y == p1.y && p1.y != p2.y) {
				if (p0.x < p1.x) // -> East
				{
					radius = Math.min(radius, (p1.x - p0.x) / 2);
					if (p1.y < p2.y) // South.
					{
						radius = Math.min(radius, (p2.y - p1.y) / 2);
						path.lineTo(p1.x - radius, p1.y);
						path.append(new Arc2D.Double(p1.x - radius, p1.y, radius, radius, 90, -90,
								Arc2D.OPEN), true);
					} else if (p1.y > p2.y)// North.
					{
						radius = Math.min(radius, (p1.y - p2.y) / 2);
						path.lineTo(p1.x - radius, p1.y);
						path.append(new Arc2D.Double(p1.x - radius, p1.y - radius, radius, radius,
								270, 90, Arc2D.OPEN), true);
					}
				} else if (p0.x > p1.x) // -> West.
				{
					radius = Math.min(radius, (p0.x - p1.x) / 2);
					if (p1.y < p2.y) // South.
					{
						radius = Math.min(radius, (p2.y - p1.y) / 2);
						path.lineTo(p1.x + radius, p1.y);
						path.append(
								new Arc2D.Double(p1.x, p1.y, radius, radius, 90, 90, Arc2D.OPEN),
								true);
					} else if (p1.y > p2.y) // North.
					{
						radius = Math.min(radius, (p1.y - p2.y) / 2);
						path.lineTo(p1.x + radius, p1.y);
						path.append(new Arc2D.Double(p1.x, p1.y - radius, radius, radius, 270, -90,
								Arc2D.OPEN), true);
					}
				}
			} else {
				System.err.println("Warning: Bend rounding has failed because of "
						+ "an non-orthogonal edge!");
			}
		}
		path.lineTo(points[points.length - 1].x, points[points.length - 1].y);

		return path;
	}

	//
	//
	// /* creates an arc for given angles and write points into the points array
	// */
	// private int createArc(double a1, double a2, int x,int y,Point[] points,
	// int index, int radius)
	// {
	// double alpha;
	// double dist = (a2 - a1) / PPS;
	// alpha = a1;
	// for (int i = 0; i < PPS; i++, alpha += dist)
	// {
	// if(index == points.length)
	// break;
	// points[index++] =
	// new Point(
	// (int)Math.round(x + radius * Math.cos(alpha)),
	// (int)Math.round(y - radius * Math.sin(alpha)));
	// }
	//
	// return index;
	// }
}