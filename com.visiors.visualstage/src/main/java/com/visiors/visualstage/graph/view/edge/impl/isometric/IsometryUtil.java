package com.visiors.visualstage.graph.view.edge.impl.isometric;

import java.awt.Point;

public class IsometryUtil {

	// public static final double ISO_ANGLE_SE = 0.46365; // Math.PI/6.0;
	// public static final double ISO_ANGLE_NE = -ISO_ANGLE_SE;
	// public static final double ISO_ANGLE_SW = Math.PI - ISO_ANGLE_SE;
	// public static final double ISO_ANGLE_NW = -ISO_ANGLE_SW;

	// public static final int DIRECTION_NE = 0;
	// public static final int DIRECTION_NW = 1;
	// public static final int DIRECTION_SW = 2;
	// public static final int DIRECTION_SE = 3;
	// public static final int INVALID = -1;
	//
	// public static double COS_ISO_ANGLE = Math.cos(ISO_ANGLE_SE);
	// public static double SIN_ISO_ANGLE = Math.sin(ISO_ANGLE_SE);

	// public static boolean isometricSegment(Point c0, Point c1) {
	// double angle = Math.abs(getSegmentAngle(c0, c1));
	//
	// return (isoDirection(angle, 0.005) != INVALID);
	// }

	// public static double getSegmentAngle(Point c0, Point c1) {
	// return Math.atan2(c1.getY() - c0.getY(), c1.getX() - c0.getX());
	// }
	//
	// private static int getSegmentLength(Point c1, Point c2) {
	//
	// return (int) Math.sqrt((c1.x - c2.x) * (c1.x - c2.x) + (c1.y - c2.y) *
	// (c1.y - c2.y));
	// }

	// public static int isoDirection(double angle) {
	// return isoDirection(angle, 0.1);
	// }
	//
	// public static int isoDirection(double angle, double accuracy) {
	// if (Math.abs(angle - ISO_ANGLE_SW) < accuracy)
	// return DIRECTION_SW;
	// if (Math.abs(angle - ISO_ANGLE_NE) < accuracy)
	// return DIRECTION_NE; //
	// if (Math.abs(angle - ISO_ANGLE_SE) < accuracy)
	// return DIRECTION_SE;
	// if (Math.abs(angle - ISO_ANGLE_NW) < accuracy)
	// return DIRECTION_NW;
	//
	// // System.err.println("isoDirection: INVALID");
	// return INVALID;
	// }

	// public static Point[] transformToIsometric(Point[] points, int
	// minEndSegLen) {
	//
	// double alpha = ISO_ANGLE_SE;
	// double cos30 = Math.cos(alpha);
	// double sin30 = Math.sin(-alpha);
	// Point newPoints[] = new Point[points.length];
	//
	// int sx = points[0].x;
	// int sy = points[0].y;
	// int ex = points[points.length - 1].x;
	// int ey = points[points.length - 1].y;
	//
	// // resize to adjust end-point
	// double dx = ex - sx;
	// double dy = ey - sy;
	//
	// double deltaX = ((dx / cos30) + (dy / sin30)) / (2 * dx);
	// double deltaY = ((dx / cos30) - (dy / sin30)) / (2 * dy);
	//
	// // transform
	// for (int i = 0, x, y; i < points.length; i++) {
	// newPoints[i] = new Point(points[i]);
	// newPoints[i].x -= sx;
	// newPoints[i].y -= sy;
	//
	// // make sure the start- end-points are connected
	// newPoints[i].x *= deltaX;
	// newPoints[i].y *= deltaY;
	//
	// x = (int) ((newPoints[i].x + newPoints[i].y) * cos30);
	// y = (int) ((newPoints[i].x - newPoints[i].y) * sin30);
	// newPoints[i].x = x;
	// newPoints[i].y = y;
	// newPoints[i].x += sx;
	// newPoints[i].y += sy;
	//
	// }
	//
	// // make sure that the segments are long enough to place arrows
	// return ensureMinSegmentLength(newPoints, minEndSegLen);
	// }
	//
	// private static Point[] ensureMinSegmentLength(Point[] points, int
	// minEndSegLen) {
	//
	// int last = points.length - 1;
	// int length = getSegmentLength(points[last - 1], points[last]);
	// if (length != 0 && length < minEndSegLen) {
	// double alpha = getSegmentAngle(points[last - 1], points[last]);
	// double cosA = Math.cos(alpha);
	// double sinA = Math.sin(alpha);
	// points[last - 1].x -= (int) ((minEndSegLen - length) * cosA);
	// points[last - 1].y -= (int) ((minEndSegLen - length) * sinA);
	// points[last - 2].x -= (int) ((minEndSegLen - length) * cosA);
	// points[last - 2].y -= (int) ((minEndSegLen - length) * sinA);
	// }
	// return points;
	// }

	public static Point findIntersection(Point p1, Point p2, Point p3, Point p4) {

		double xD1, yD1, xD2, yD2, xD3, yD3;
		double dot, deg, len1, len2;
		double ua, div;

		xD1 = p2.x - p1.x;
		xD2 = p4.x - p3.x;
		yD1 = p2.y - p1.y;
		yD2 = p4.y - p3.y;
		xD3 = p1.x - p3.x;
		yD3 = p1.y - p3.y;

		len1 = Math.sqrt(xD1 * xD1 + yD1 * yD1);
		len2 = Math.sqrt(xD2 * xD2 + yD2 * yD2);

		// calculate angle between the two lines.
		dot = (xD1 * xD2 + yD1 * yD2); // dot product
		deg = dot / (len1 * len2);

		// Parallel lines,
		if (Math.abs(deg) == 1) {
			return null;
		}

		// find intersection
		Point pt = new Point(0, 0);
		div = yD2 * xD1 - xD2 * yD1;
		ua = (xD2 * yD3 - yD2 * xD3) / div;
		pt.x = (int) (p1.x + ua * xD1);
		pt.y = (int) (p1.y + ua * yD1);

		return pt;
	}

}
