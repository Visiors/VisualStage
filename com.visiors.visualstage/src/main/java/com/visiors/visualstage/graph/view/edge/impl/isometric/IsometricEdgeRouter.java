package com.visiors.visualstage.graph.view.edge.impl.isometric;

import java.awt.Point;

import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.node.VisualNode;

/**

 */
public class IsometricEdgeRouter {

	private static final int UNDEFINED = 0;
	private static final int FROM_NORTH_EAST = 1;
	private static final int FROM_NORTH_WEST = 2;
	private static final int FROM_SOUTH_EAST = 3;
	private static final int FROM_SOUTH_WEST = 4;
	private static final int TO_NORTH_EAST = 5;
	private static final int TO_NORTH_WEST = 6;
	private static final int TO_SOUTH_EAST = 7;
	private static final int TO_SOUTH_WEST = 8;

	private static final double ISO_ANGLE2 = 0.46365; // Math.PI / 3.0;
	private static final double ISO_ANGLE1 = Math.PI / 2 - ISO_ANGLE2;

	public static Point[] routeEdge(VisualEdge edge, Point[] points, int sDistance, int tDistance) {

		final Point start = points[0];
		final Point end = points[points.length - 1];
		final int outputDirection = getOutputDirection(edge, start, end);
		final int inputDirection = getInputDirection(edge, start, end);

		switch (outputDirection) {
		case TO_NORTH_EAST:
			switch (inputDirection) {
			case FROM_NORTH_EAST:
				return routeNorthEast2NorthEast(start, end, sDistance, tDistance);
			case FROM_NORTH_WEST:
				return routeNorthEast2NorthWest(start, end, sDistance, tDistance);
			case FROM_SOUTH_EAST:
				return routeNorthEast2SouthEast(start, end, sDistance, tDistance);
			case FROM_SOUTH_WEST:
				return routeNorthEast2SouthWest(start, end, sDistance, tDistance);
			}
			break;
		case TO_SOUTH_EAST:
			switch (inputDirection) {
			case FROM_NORTH_EAST:
				return routeSouthEast2NorthEast(start, end, sDistance, tDistance);
			case FROM_NORTH_WEST:
				return routeSouthEast2NorthWest(start, end, sDistance, tDistance);
			case FROM_SOUTH_EAST:
				return routeSouthEast2SouthEast(start, end, sDistance, tDistance);
			case FROM_SOUTH_WEST:
				return routeSouthEast2SouthWest(start, end, sDistance, tDistance);
			}
			break;
		case TO_SOUTH_WEST:
			switch (inputDirection) {
			case FROM_NORTH_EAST:
				return routeSouthWest2NorthEast(start, end, sDistance, tDistance);
			case FROM_NORTH_WEST:
				return routeSouthWest2NorthWest(start, end, sDistance, tDistance);
			case FROM_SOUTH_EAST:
				return routeSouthWest2SouthEast(start, end, sDistance, tDistance);
			case FROM_SOUTH_WEST:
				return routeSouthWest2SouthWest(start, end, sDistance, tDistance);
			}
			break;
		case TO_NORTH_WEST:
			switch (inputDirection) {
			case TO_NORTH_WEST:
			case FROM_NORTH_EAST:
				return routeNorthWest2NorthEast(start, end, sDistance, tDistance);
			case FROM_NORTH_WEST:
				return routeNorthWest2NorthWest(start, end, sDistance, tDistance);
			case FROM_SOUTH_EAST:
				return routeNorthWest2SouthEast(start, end, sDistance, tDistance);
			case FROM_SOUTH_WEST:
				return routeNorthWest2SouthWest(start, end, sDistance, tDistance);
			}
		}

		return points;
	}

	public static int getInputDirection(VisualEdge edge, Point start, Point end) {

		VisualNode targetNode = edge.getTargetNode();
		if (targetNode != null) {
			int spid = edge.getTargetPortId();
			if (spid != -1) {
				int range[] = targetNode.getPortAcceptedInterval(spid);
				if (range[1] < range[0]) {
					range[1] += 360;
				}
				int median = range[0] + (range[1] - range[0]) / 2;

				if (median >= 315 && median <= 405) {
					return FROM_NORTH_EAST;
				}
				if (median >= 45 && median < 135) {
					return FROM_NORTH_WEST;
				}
				if (median >= 135 && median < 225) {
					return FROM_SOUTH_WEST;
				}
				if (median >= 225 && median < 315) {
					return FROM_SOUTH_EAST;
				}
			}
		}

		if (start.x < end.x) {
			return start.y < end.y ? FROM_NORTH_WEST : FROM_SOUTH_WEST;
		} else {
			return start.y < end.y ? FROM_NORTH_EAST : FROM_SOUTH_EAST;
		}
	}

	public static int getOutputDirection(VisualEdge edge, Point start, Point end) {

		VisualNode sourceNode = edge.getSourceNode();
		if (sourceNode != null) {
			int spid = edge.getSourcePortId();
			if (spid != -1) {
				int range[] = sourceNode.getPortAcceptedInterval(spid);
				if (range[1] < range[0]) {
					range[1] += 360;
				}
				int median = range[0] + (range[1] - range[0]) / 2;

				if (median >= 315 && median <= 405) {
					return TO_NORTH_EAST;
				}
				if (median >= 45 && median < 135) {
					return TO_NORTH_WEST;
				}
				if (median >= 135 && median < 225) {
					return TO_SOUTH_WEST;
				}
				if (median >= 225 && median < 315) {
					return TO_SOUTH_EAST;
				}
			}
		}

		if (start.x < end.x) {
			return start.y < end.y ? TO_SOUTH_EAST : TO_NORTH_EAST;
		} else {
			return start.y < end.y ? TO_SOUTH_WEST : TO_NORTH_WEST;
		}
	}

	private static Point[] routeNorthEast2NorthEast(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points = new Point[4];
		points[0] = start;
		points[3] = end;

		points[1] = createStartHandle(start, dstToSrc, FROM_NORTH_EAST);
		points[2] = createEndHandle(end, dstToTrgt, TO_NORTH_EAST);

		adjustHitPoint(points[0], points[1], ISO_ANGLE1, points[2], points[3], dstToSrc, true);

		return points;
	}

	private static void adjustcentersegment(Point sourceLinefrom, Point sourceLineTo, double alpha,
			Point targetLineStart, Point targetLinEnd) {

		int dx = sourceLineTo.x - targetLineStart.x;

		Point tmp = new Point();
		tmp.x = (int) (sourceLineTo.x + dx * Math.cos(alpha));
		tmp.y = (int) (sourceLineTo.y + dx * Math.sin(alpha));
		tmp = IsometryUtil.findIntersection(sourceLineTo, tmp, targetLineStart, targetLinEnd);
		targetLineStart.x = tmp.x;
		targetLineStart.y = tmp.y;

	}

	static void adjustHitPoint(Point sourceLinefrom, Point sourceLineTo, double alpha,
			Point targetLineStart, Point targetLinEnd, int dstToTrgt, boolean upward) {

		int dx = sourceLinefrom.x - targetLinEnd.x;
		int dy = sourceLinefrom.y - targetLinEnd.y;
		double d = Math.sqrt(dx * dx + dy * dy);

		Point tmp = new Point();
		tmp.x = (int) (sourceLineTo.x + d * Math.sin(alpha));
		tmp.y = (int) (sourceLineTo.y + d * Math.cos(alpha));
		tmp = IsometryUtil.findIntersection(sourceLineTo, tmp, targetLineStart, targetLinEnd);
		targetLineStart.x = tmp.x;
		targetLineStart.y = tmp.y;
		if (upward) {
			if (targetLineStart.y + dstToTrgt > targetLinEnd.y) {
				dy = dstToTrgt - targetLinEnd.y + targetLineStart.y;
				sourceLineTo.y -= dy;
				sourceLineTo.x += (int) (dy * Math.tan(alpha));
				targetLineStart.y -= dy;
				targetLineStart.x += (int) (dy * Math.tan(alpha));
			}
		} else {
			if (targetLineStart.y - dstToTrgt < targetLinEnd.y) {
				dy = -dstToTrgt - targetLinEnd.y + targetLineStart.y;
				sourceLineTo.y -= dy;
				sourceLineTo.x += (int) (dy * Math.tan(alpha));
				targetLineStart.y -= dy;
				targetLineStart.x += (int) (dy * Math.tan(alpha));
			}
		}

	}

	private static Point createStartHandle(Point start, int dstToSrc, int outputDirection) {

		int dy1;
		double alpha = 0;
		Point coord = null;

		switch (outputDirection) {
		case FROM_NORTH_EAST:
			alpha = ISO_ANGLE1;
			coord = new Point(start.x, start.y - dstToSrc);
			dy1 = start.y - coord.y;
			coord.x += (int) (dy1 * Math.tan(alpha));
			break;
		case FROM_NORTH_WEST:
			alpha = -ISO_ANGLE1;
			coord = new Point(start.x, start.y - dstToSrc);
			dy1 = start.y - coord.y;
			coord.x += (int) (dy1 * Math.tan(alpha));
			break;
		case FROM_SOUTH_EAST:
			alpha = -ISO_ANGLE1;
			coord = new Point(start.x, start.y + dstToSrc);
			dy1 = start.y - coord.y;
			coord.x += (int) (dy1 * Math.tan(alpha));
			break;
		case FROM_SOUTH_WEST:
			alpha = ISO_ANGLE1;
			coord = new Point(start.x, start.y + dstToSrc);
			dy1 = start.y - coord.y;
			coord.x += (int) (dy1 * Math.tan(alpha));
			break;
		}
		return coord;
	}

	private static Point createEndHandle(Point end, int dstToDest, int input) {

		int dy1;
		double alpha = 0;
		Point point = null;

		switch (input) {
		case TO_NORTH_EAST:
			alpha = ISO_ANGLE1;
			point = new Point(end.x, end.y - dstToDest);
			dy1 = end.y - point.y;
			point.x += (int) (dy1 * Math.tan(alpha));
			break;
		case TO_NORTH_WEST:
			alpha = -ISO_ANGLE1;
			point = new Point(end.x, end.y - dstToDest);
			dy1 = end.y - point.y;
			point.x += (int) (dy1 * Math.tan(alpha));
			break;
		case TO_SOUTH_EAST:
			alpha = -ISO_ANGLE1;
			point = new Point(end.x, end.y + dstToDest);
			dy1 = end.y - point.y;
			point.x += (int) (dy1 * Math.tan(alpha));
			break;
		case TO_SOUTH_WEST:
			alpha = ISO_ANGLE1;
			point = new Point(end.x, end.y + dstToDest);
			dy1 = end.y - point.y;
			point.x += (int) (dy1 * Math.tan(alpha));
			break;
		}
		return point;
	}

	private static Point[] routeNorthEast2SouthEast(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[3];
		points[0] = start;
		points[2] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_NORTH_EAST);
		Point tmp = createEndHandle(end, dstToTrgt, TO_SOUTH_EAST);
		points[1] = IsometryUtil.findIntersection(points[0], points[1], tmp, points[2]);

		int dx = points[1].x - points[0].x;
		int dy = points[1].y - points[2].y;
		if (dy < dstToTrgt || dx < dstToSrc) {
			Point[] temps = new Point[5];
			temps[0] = start;
			temps[1] = createEndHandle(start, dstToSrc, TO_NORTH_EAST);
			temps[3] = createEndHandle(end, dstToTrgt, TO_SOUTH_EAST);
			temps[2] = createEndHandle(temps[3], dstToTrgt, TO_SOUTH_WEST);
			tmp = createEndHandle(temps[1], dstToSrc, TO_SOUTH_EAST);
			temps[2] = IsometryUtil.findIntersection(temps[1], tmp, temps[3], temps[2]);
			temps[4] = end;
			points = temps;
		}

		return points;
	}

	private static Point[] routeNorthEast2SouthWest(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[4];
		points[0] = start;
		points[3] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_NORTH_EAST);
		points[2] = createEndHandle(end, dstToSrc, TO_SOUTH_WEST);

		Point tmp = createEndHandle(end, dstToTrgt, TO_NORTH_WEST);
		tmp = IsometryUtil.findIntersection(points[0], points[1], tmp, end);
		int dx = tmp.x - points[0].x;
		int dy = tmp.y - points[0].y;

		double d = Math.sqrt(dx * dx + dy * dy);

		if (dx > dstToSrc + dstToTrgt) {
			// readjust point 1
			points[1].x = points[0].x + (int) (d / 2 * Math.cos(ISO_ANGLE2));
			points[1].y = points[0].y - (int) (d / 2 * Math.sin(ISO_ANGLE2));

			// readjust point 2
			tmp = createEndHandle(points[1], dstToSrc, TO_SOUTH_EAST);
			points[2] = IsometryUtil.findIntersection(points[1], tmp, points[2], end);
		} else {

			Point[] temps = new Point[6];
			temps[0] = points[0];
			temps[1] = points[1];
			temps[4] = points[2];
			temps[5] = points[3];

			dx = points[3].x - points[0].x;
			dy = points[3].y - points[0].y;
			double alpha = Math.atan2(dy, dx);
			d = Math.sqrt(dx * dx + dy * dy);
			int x1 = (int) (d / 2 * Math.cos(alpha));
			int y1 = (int) (d / 2 * Math.sin(alpha));
			temps[2] = new Point();
			temps[2].x = points[1].x + x1;
			temps[2].y = points[1].y + y1;

			temps[3] = new Point();
			temps[3].x = (int) (temps[2].x - d * Math.cos(-ISO_ANGLE2));
			temps[3].y = (int) (temps[2].y - d * Math.sin(-ISO_ANGLE2));

			// readjust 2
			tmp = createEndHandle(points[1], dstToTrgt, TO_SOUTH_EAST);
			temps[2] = IsometryUtil.findIntersection(points[1], tmp, temps[2], temps[3]);

			// readjust 3
			tmp = createEndHandle(points[2], dstToTrgt, TO_NORTH_WEST);
			temps[3] = IsometryUtil.findIntersection(temps[4], tmp, temps[2], temps[3]);

			points = temps;
		}

		return points;
	}

	private static Point[] routeNorthEast2NorthWest(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[3];
		points[0] = start;
		points[2] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_NORTH_EAST);
		Point tmp = createEndHandle(end, dstToTrgt, TO_NORTH_WEST);
		points[1] = IsometryUtil.findIntersection(points[0], points[1], tmp, points[2]);

		int dx = points[1].x - points[0].x;
		int dy = points[2].y - points[1].y;
		if (dy < dstToTrgt || dx < dstToSrc) {
			Point[] temps = new Point[5];
			temps[0] = start;
			temps[1] = createEndHandle(start, dstToSrc, TO_NORTH_EAST);
			temps[3] = createEndHandle(end, dstToTrgt, TO_NORTH_WEST);
			temps[2] = createEndHandle(temps[3], dstToTrgt, TO_SOUTH_WEST);
			tmp = createEndHandle(temps[1], dstToSrc, TO_NORTH_WEST);
			temps[2] = IsometryUtil.findIntersection(temps[1], tmp, temps[3], temps[2]);
			temps[4] = end;
			points = temps;
		}

		return points;
	}

	private static Point[] routeSouthEast2NorthEast(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[3];
		points[0] = start;
		points[2] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_SOUTH_EAST);
		Point tmp = createEndHandle(end, dstToTrgt, TO_NORTH_EAST);
		points[1] = IsometryUtil.findIntersection(points[0], points[1], tmp, points[2]);

		int dx = points[1].x - points[0].x;
		int dy = points[2].y - points[1].y;
		if (dy < dstToTrgt || dx < dstToSrc) {
			Point[] temps = new Point[5];
			temps[0] = start;
			temps[3] = createEndHandle(end, dstToTrgt, TO_NORTH_EAST);
			temps[2] = createEndHandle(temps[3], dstToTrgt, TO_NORTH_WEST);
			temps[1] = createEndHandle(start, dstToSrc, TO_SOUTH_EAST);
			tmp = createEndHandle(temps[1], dstToSrc, TO_NORTH_EAST);
			temps[2] = IsometryUtil.findIntersection(temps[1], tmp, temps[3], temps[2]);
			temps[4] = end;
			points = temps;
		}

		return points;
	}

	private static Point[] routeSouthEast2SouthEast(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points = new Point[4];
		points[0] = start;
		points[3] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_SOUTH_EAST);
		points[2] = createEndHandle(end, dstToTrgt, TO_SOUTH_EAST);
		adjustHitPoint(points[0], points[1], -ISO_ANGLE1, points[2], points[3], dstToSrc, false);
		return points;
	}

	private static Point[] routeSouthEast2SouthWest(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[3];
		points[0] = start;
		points[2] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_SOUTH_EAST);
		Point tmp = createEndHandle(end, dstToTrgt, TO_SOUTH_WEST);
		points[1] = IsometryUtil.findIntersection(points[0], points[1], tmp, points[2]);

		int dx = points[1].x - points[0].x;
		int dy = points[1].y - points[2].y;
		if (dy < dstToTrgt || dx < dstToSrc) {
			Point[] temps = new Point[5];
			temps[0] = start;
			temps[1] = createEndHandle(start, dstToSrc, TO_SOUTH_EAST);
			temps[3] = createEndHandle(end, dstToTrgt, TO_SOUTH_WEST);
			temps[2] = createEndHandle(temps[3], dstToTrgt, TO_NORTH_WEST);
			tmp = createEndHandle(temps[1], dstToSrc, TO_SOUTH_WEST);
			temps[2] = IsometryUtil.findIntersection(temps[1], tmp, temps[3], temps[2]);
			temps[4] = end;
			points = temps;
		}

		return points;
	}

	private static Point[] routeSouthEast2NorthWest(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[4];
		points[0] = start;
		points[3] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_SOUTH_EAST);
		points[2] = createEndHandle(end, dstToSrc, TO_NORTH_WEST);

		double dx = points[2].x - points[1].x;
		double dy = points[2].y - points[1].y;
		double alpha = Math.atan2(dy, dx);

		Point tmp = createEndHandle(end, dstToTrgt, TO_SOUTH_WEST);
		tmp = IsometryUtil.findIntersection(points[0], points[1], tmp, end);
		dx = tmp.x - points[0].x;
		dy = tmp.y - points[0].y;

		double d = Math.sqrt(dx * dx + dy * dy);

		if (dx > dstToSrc + dstToTrgt) {
			// readjust point 1
			points[1].x = points[0].x + (int) (d / 2 * Math.cos(-ISO_ANGLE2));
			points[1].y = points[0].y - (int) (d / 2 * Math.sin(-ISO_ANGLE2));

			// readjust point 2
			tmp = createEndHandle(points[1], dstToSrc, TO_NORTH_EAST);
			points[2] = IsometryUtil.findIntersection(points[1], tmp, points[2], end);
		} else {

			Point[] temps = new Point[6];
			temps[0] = points[0];
			temps[1] = points[1];
			temps[4] = points[2];
			temps[5] = points[3];

			dx = points[3].x - points[0].x;
			dy = points[3].y - points[0].y;
			alpha = Math.atan2(dy, dx);
			d = Math.sqrt(dx * dx + dy * dy);
			int x1 = (int) (d / 2 * Math.cos(alpha));
			int y1 = (int) (d / 2 * Math.sin(alpha));
			temps[2] = new Point();
			temps[2].x = points[1].x + x1;
			temps[2].y = points[1].y + y1;

			temps[3] = new Point();
			temps[3].x = (int) (temps[2].x - d * Math.cos(ISO_ANGLE2));
			temps[3].y = (int) (temps[2].y - d * Math.sin(ISO_ANGLE2));

			// readjust 2
			tmp = createEndHandle(points[1], dstToTrgt, TO_SOUTH_WEST);
			temps[2] = IsometryUtil.findIntersection(points[1], tmp, temps[2], temps[3]);

			// readjust 3
			tmp = createEndHandle(points[2], dstToTrgt, TO_NORTH_EAST);
			temps[3] = IsometryUtil.findIntersection(temps[4], tmp, temps[2], temps[3]);

			points = temps;
		}

		return points;
	}

	private static Point[] routeSouthWest2NorthEast(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[4];
		points[0] = start;
		points[3] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_SOUTH_WEST);
		points[2] = createEndHandle(end, dstToSrc, TO_NORTH_EAST);

		double dx = points[2].x - points[1].x;
		double dy = points[2].y - points[1].y;
		double alpha = Math.atan2(dy, dx);

		Point tmp = createEndHandle(end, dstToTrgt, TO_NORTH_WEST);
		tmp = IsometryUtil.findIntersection(points[0], points[1], tmp, end);
		dx = tmp.x - points[0].x;
		dy = tmp.y - points[0].y;

		double d = Math.sqrt(dx * dx + dy * dy);

		if (-dx > dstToSrc + dstToTrgt) {
			// readjust point 1
			points[1].x = points[0].x - (int) (d / 2 * Math.cos(ISO_ANGLE2));
			points[1].y = points[0].y + (int) (d / 2 * Math.sin(ISO_ANGLE2));

			// readjust point 2
			tmp = createEndHandle(points[1], dstToSrc, TO_SOUTH_EAST);
			points[2] = IsometryUtil.findIntersection(points[1], tmp, points[2], end);
		} else {

			Point[] temps = new Point[6];
			temps[0] = points[0];
			temps[1] = points[1];
			temps[4] = points[2];
			temps[5] = points[3];

			dx = points[3].x - points[0].x;
			dy = points[3].y - points[0].y;
			alpha = Math.atan2(dy, dx);
			d = Math.sqrt(dx * dx + dy * dy);
			int x1 = (int) (d / 2 * Math.cos(alpha));
			int y1 = (int) (d / 2 * Math.sin(alpha));
			temps[2] = new Point();
			temps[2].x = points[1].x + x1;
			temps[2].y = points[1].y + y1;

			temps[3] = new Point();
			temps[3].x = (int) (temps[2].x + d * Math.cos(ISO_ANGLE2));
			temps[3].y = (int) (temps[2].y - d * Math.sin(ISO_ANGLE2));

			// readjust 2
			tmp = createEndHandle(points[1], dstToTrgt, TO_SOUTH_EAST);
			temps[2] = IsometryUtil.findIntersection(points[1], tmp, temps[2], temps[3]);

			// readjust 3
			tmp = createEndHandle(points[2], dstToTrgt, TO_NORTH_WEST);
			temps[3] = IsometryUtil.findIntersection(temps[4], tmp, temps[2], temps[3]);

			points = temps;
		}

		return points;
	}

	private static Point[] routeSouthWest2SouthEast(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[3];
		points[0] = start;
		points[2] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_SOUTH_WEST);
		Point tmp = createEndHandle(end, dstToTrgt, TO_SOUTH_EAST);
		points[1] = IsometryUtil.findIntersection(points[0], points[1], tmp, points[2]);

		int dx = points[0].x - points[1].x;
		int dy = points[1].y - points[2].y;
		if (dy < dstToTrgt || dx < dstToSrc) {
			Point[] temps = new Point[5];
			temps[0] = start;
			temps[1] = createEndHandle(start, dstToSrc, TO_SOUTH_WEST);
			temps[3] = createEndHandle(end, dstToTrgt, TO_SOUTH_EAST);
			temps[2] = createEndHandle(temps[3], dstToTrgt, TO_NORTH_EAST);
			tmp = createEndHandle(temps[1], dstToSrc, TO_SOUTH_EAST);
			temps[2] = IsometryUtil.findIntersection(temps[1], tmp, temps[3], temps[2]);
			temps[4] = end;
			points = temps;
		}

		return points;
	}

	private static Point[] routeSouthWest2SouthWest(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points = new Point[4];
		points[0] = start;
		points[3] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_SOUTH_WEST);
		points[2] = createEndHandle(end, dstToTrgt, TO_SOUTH_WEST);
		adjustHitPoint(points[0], points[1], ISO_ANGLE1, points[2], points[3], dstToSrc, false);

		return points;
	}

	private static Point[] routeSouthWest2NorthWest(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[3];
		points[0] = start;
		points[2] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_SOUTH_WEST);
		Point tmp = createEndHandle(end, dstToTrgt, TO_NORTH_WEST);
		points[1] = IsometryUtil.findIntersection(points[0], points[1], tmp, points[2]);

		int dx = points[0].x - points[1].x;
		int dy = points[2].y - points[1].y;
		if (dy < dstToTrgt || dx < dstToSrc) {
			Point[] temps = new Point[5];
			temps[0] = start;
			temps[3] = createEndHandle(end, dstToTrgt, TO_NORTH_WEST);
			temps[2] = createEndHandle(temps[3], dstToTrgt, TO_NORTH_EAST);
			temps[1] = createEndHandle(start, dstToSrc, TO_SOUTH_WEST);
			tmp = createEndHandle(temps[1], dstToSrc, TO_NORTH_WEST);
			temps[2] = IsometryUtil.findIntersection(temps[1], tmp, temps[3], temps[2]);
			temps[4] = end;
			points = temps;
		}

		return points;
	}

	private static Point[] routeNorthWest2NorthEast(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[3];
		points[0] = start;
		points[2] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_NORTH_WEST);
		Point tmp = createEndHandle(end, dstToTrgt, TO_NORTH_EAST);
		points[1] = IsometryUtil.findIntersection(points[0], points[1], tmp, points[2]);

		int dx = points[0].x - points[1].x;
		int dy = points[2].y - points[1].y;
		if (dy < dstToTrgt || dx < dstToSrc) {
			Point[] temps = new Point[5];
			temps[0] = start;
			temps[1] = createEndHandle(start, dstToSrc, TO_NORTH_WEST);
			temps[3] = createEndHandle(end, dstToTrgt, TO_NORTH_EAST);
			temps[2] = createEndHandle(temps[3], dstToTrgt, TO_SOUTH_EAST);
			tmp = createEndHandle(temps[1], dstToSrc, TO_SOUTH_WEST);
			temps[2] = IsometryUtil.findIntersection(temps[1], tmp, temps[3], temps[2]);
			temps[4] = end;
			points = temps;
		}

		return points;
	}

	private static Point[] routeNorthWest2SouthEast(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[4];
		points[0] = start;
		points[3] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_NORTH_WEST);
		points[2] = createEndHandle(end, dstToSrc, TO_SOUTH_EAST);

		double dx = points[2].x - points[1].x;
		double dy = points[2].y - points[1].y;
		double alpha = Math.atan2(dy, dx);

		Point tmp = createEndHandle(end, dstToTrgt, TO_SOUTH_WEST);
		tmp = IsometryUtil.findIntersection(points[0], points[1], tmp, end);
		dx = tmp.x - points[0].x;
		dy = tmp.y - points[0].y;

		double d = Math.sqrt(dx * dx + dy * dy);

		if (-dx > dstToSrc + dstToTrgt) {
			// readjust point 1
			points[1].x = points[0].x - (int) (d / 2 * Math.cos(ISO_ANGLE2));
			points[1].y = points[0].y - (int) (d / 2 * Math.sin(ISO_ANGLE2));

			// readjust point 2
			tmp = createEndHandle(points[1], dstToSrc, TO_NORTH_EAST);
			points[2] = IsometryUtil.findIntersection(points[1], tmp, points[2], end);
		} else {

			Point[] temps = new Point[6];
			temps[0] = points[0];
			temps[1] = points[1];
			temps[4] = points[2];
			temps[5] = points[3];

			dx = points[3].x - points[0].x;
			dy = points[3].y - points[0].y;
			alpha = Math.atan2(dy, dx);
			d = Math.sqrt(dx * dx + dy * dy);
			int x1 = (int) (d / 2 * Math.cos(alpha));
			int y1 = (int) (d / 2 * Math.sin(alpha));
			temps[2] = new Point();
			temps[2].x = points[1].x + x1;
			temps[2].y = points[1].y + y1;

			temps[3] = new Point();
			temps[3].x = (int) (temps[2].x - d * Math.cos(ISO_ANGLE2));
			temps[3].y = (int) (temps[2].y - d * Math.sin(ISO_ANGLE2));

			// readjust 2
			tmp = createEndHandle(points[1], dstToTrgt, TO_SOUTH_WEST);
			temps[2] = IsometryUtil.findIntersection(points[1], tmp, temps[2], temps[3]);

			// readjust 3
			tmp = createEndHandle(points[2], dstToTrgt, TO_NORTH_EAST);
			temps[3] = IsometryUtil.findIntersection(temps[4], tmp, temps[2], temps[3]);

			points = temps;
		}

		return points;
	}

	private static Point[] routeNorthWest2SouthWest(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points;
		points = new Point[3];
		points[0] = start;
		points[2] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_NORTH_WEST);
		Point tmp = createEndHandle(end, dstToTrgt, TO_SOUTH_WEST);
		points[1] = IsometryUtil.findIntersection(points[0], points[1], tmp, points[2]);

		int dx = points[0].x - points[1].x;
		int dy = points[1].y - points[2].y;
		if (dy < dstToTrgt || dx < dstToSrc) {
			Point[] temps = new Point[5];
			temps[0] = start;
			temps[1] = createEndHandle(start, dstToSrc, TO_NORTH_WEST);
			temps[3] = createEndHandle(end, dstToTrgt, TO_SOUTH_WEST);
			temps[2] = createEndHandle(temps[3], dstToTrgt, TO_SOUTH_EAST);
			tmp = createEndHandle(temps[1], dstToSrc, TO_SOUTH_WEST);
			temps[2] = IsometryUtil.findIntersection(temps[1], tmp, temps[3], temps[2]);
			temps[4] = end;
			points = temps;
		}

		return points;
	}

	private static Point[] routeNorthWest2NorthWest(Point start, Point end, int dstToSrc,
			int dstToTrgt) {

		Point[] points = new Point[4];
		points[0] = start;
		points[3] = end;
		points[1] = createStartHandle(start, dstToSrc, FROM_NORTH_WEST);
		points[2] = createEndHandle(end, dstToTrgt, TO_NORTH_WEST);
		adjustHitPoint(points[0], points[1], -ISO_ANGLE1, points[2], points[3], dstToSrc, true);
		return points;
	}

}
