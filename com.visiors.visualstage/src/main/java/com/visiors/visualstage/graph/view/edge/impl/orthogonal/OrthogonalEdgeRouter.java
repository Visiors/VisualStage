package com.visiors.visualstage.graph.view.edge.impl.orthogonal;

import java.awt.Point;

import com.visiors.visualstage.graph.view.edge.VisualEdge;
import com.visiors.visualstage.graph.view.node.VisualNode;

/**
 * TODO: comment.
 * 
 * @author Sharokh Khani
 * @version $Id: $
 */
public class OrthogonalEdgeRouter {

	private static final int UNDEFINED = 0;
	private static final int TO_WEST = 1;
	private static final int TO_EAST = 2;
	private static final int TO_SOUTH = 3;
	private static final int TO_NORTH = 4;
	private static final int FROM_WEST = 5;
	private static final int FROM_EAST = 6;
	private static final int FROM_NORTH = 7;
	private static final int FROM_SOUTH = 8;

	public static Point[] ensureOrthogonality(VisualEdge edge, Point[] points) {

		final Point start = points[0];
		final Point end = points[points.length - 1];

		if (!isSegmentOrthogonal(points[0], points[1])) {
			final int outputDirection = getOutputDirection(edge, start, end);
			switch (outputDirection) {
			case TO_NORTH:
			case TO_SOUTH:
				points[1].x = points[0].x;
				break;
			case TO_EAST:
			case TO_WEST:
				points[1].y = points[0].y;
				break;
			}
		}

		if (!isSegmentOrthogonal(points[points.length - 2], points[points.length - 1])) {
			final int inputDirection = getInputDirection(edge, start, end);
			switch (inputDirection) {
			case FROM_NORTH:
			case FROM_SOUTH:
				points[points.length - 2].x = points[points.length - 1].x;
				break;
			case FROM_EAST:
			case FROM_WEST:
				points[points.length - 2].y = points[points.length - 1].y;
				break;
			}
		}

		return points;
	}

	private static boolean isSegmentOrthogonal(Point pt1, Point pt2) {

		return pt1.x == pt2.x || pt1.y == pt2.y;
	}

	public static Point[] routeEdge(VisualEdge edge, Point[] points, int distanceToNode) {

		final Point start = points[0];
		final Point end = points[points.length - 1];
		final int outputDirection = getOutputDirection(edge, start, end);
		final int inputDirection = getInputDirection(edge, start, end);

		switch (outputDirection) {
		case TO_NORTH:
			switch (inputDirection) {
			case FROM_NORTH:
				return routeNorthNorthEdge(start, end, distanceToNode);
			case FROM_EAST:
				return routeSouthEastEdge(start, end, distanceToNode);
			case FROM_SOUTH:
				return routeSouthNorthEdge(start, end, distanceToNode);
			case FROM_WEST:
				return routeSouthWestEdge(start, end, distanceToNode);
			}
			break;
		case TO_EAST:
			switch (inputDirection) {
			case FROM_NORTH:
				return routeWestNorthEdge(start, end, distanceToNode);
			case FROM_EAST:
				return routeEastEastEdge(start, end, distanceToNode);
			case FROM_SOUTH:
				return routeWestSouthEdge(start, end, distanceToNode);
			case FROM_WEST:
				return routeWestToEastEdge(start, end, distanceToNode);
			}
			break;
		case TO_SOUTH:
			switch (inputDirection) {
			case FROM_NORTH:
				return routeNorthSouthEdge(start, end, distanceToNode);
			case FROM_EAST:
				return routeNorthEastdge(start, end, distanceToNode);
			case FROM_SOUTH:
				return routeSouthSouthEdge(start, end, distanceToNode);
			case FROM_WEST:
				return routeNorthWestEdge(start, end, distanceToNode);
			}
			break;
		case TO_WEST:
			switch (inputDirection) {
			case FROM_NORTH:
				return routeEastSouthEdge(start, end, distanceToNode);
			case FROM_EAST:
				return routeEastWestEdge(start, end, distanceToNode);
			case FROM_SOUTH:
				return routeEastNorthEdge(start, end, distanceToNode);
			case FROM_WEST:
				return routeWestWestEdge(start, end, distanceToNode);
			}
		}

		return points;
	}

	private static Point[] routeEastEastEdge(Point start, Point end, int distanceToNode) {

		Point[] points = new Point[4];
		points[0] = start;
		int x = Math.max(start.x, end.x);
		points[1] = new Point(x + distanceToNode, points[0].y);
		points[2] = new Point(x + distanceToNode, end.y);
		points[3] = end;
		return points;
	}

	private static Point[] routeWestWestEdge(Point start, Point end, int distanceToNode) {

		Point[] points = new Point[4];
		points[0] = start;
		int x = Math.min(start.x, end.x);
		points[1] = new Point(x - distanceToNode, points[0].y);
		points[2] = new Point(x - distanceToNode, end.y);
		points[3] = end;
		return points;
	}

	private static Point[] routeSouthSouthEdge(Point start, Point end, int distanceToNode) {

		Point[] points = new Point[4];
		points[0] = start;
		int y = Math.max(start.y, end.y);
		points[1] = new Point(points[0].x, y + distanceToNode);
		points[2] = new Point(end.x, y + distanceToNode);
		points[3] = end;
		return points;
	}

	private static Point[] routeNorthNorthEdge(Point start, Point end, int distanceToNode) {

		Point[] points = new Point[4];
		points[0] = start;
		int y = Math.min(start.y, end.y);
		points[1] = new Point(points[0].x, y - distanceToNode);
		points[2] = new Point(end.x, y - distanceToNode);
		points[3] = end;
		return points;
	}

	private static Point[] routeWestSouthEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		if (end.x < start.x) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x + distanceToNode, start.y);
			points[2] = new Point(points[1].x, end.y - midway(end.y, start.y));
			points[3] = new Point(end.x, points[2].y);
			points[4] = end;
		} else if (end.y > start.y) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x + midway(end.x, start.x), start.y);
			points[2] = new Point(points[1].x, end.y + distanceToNode);
			points[3] = new Point(end.x, points[2].y);
			points[4] = end;
		} else {
			points = new Point[3];
			points[0] = start;
			points[1] = new Point(end.x, start.y);
			points[2] = end;
		}
		return points;
	}

	private static Point[] routeWestNorthEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		if (end.x < start.x) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x + distanceToNode, start.y);
			points[2] = new Point(points[1].x, end.y + midway(start.y, end.y));
			points[3] = new Point(end.x, points[2].y);
			points[4] = end;
		} else if (end.y < start.y) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x + midway(end.x, start.x), start.y);
			points[2] = new Point(points[1].x, end.y - distanceToNode);
			points[3] = new Point(end.x, points[2].y);
			points[4] = end;
		} else {
			points = new Point[3];
			points[0] = start;
			points[1] = new Point(end.x, start.y);
			points[2] = end;
		}
		return points;
	}

	private static Point[] routeEastSouthEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		if (end.x > start.x) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x - distanceToNode, start.y);
			points[2] = new Point(points[1].x, end.y - midway(end.y, start.y));
			points[3] = new Point(end.x, points[2].y);
			points[4] = end;
		} else if (end.y < start.y) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x + midway(end.x, start.x), start.y);
			points[2] = new Point(points[1].x, end.y - distanceToNode);
			points[3] = new Point(end.x, points[2].y);
			points[4] = end;
		} else {
			points = new Point[3];
			points[0] = start;
			points[1] = new Point(end.x, start.y);
			points[2] = end;
		}
		return points;
	}

	private static Point[] routeEastNorthEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		if (end.x > start.x) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x - distanceToNode, start.y);
			points[2] = new Point(points[1].x, end.y + midway(start.y, end.y));
			points[3] = new Point(end.x, points[2].y);
			points[4] = end;
		} else if (end.y > start.y) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x - midway(start.x, end.x), start.y);
			points[2] = new Point(points[1].x, end.y + distanceToNode);
			points[3] = new Point(end.x, points[2].y);
			points[4] = end;
		} else {
			points = new Point[3];
			points[0] = start;
			points[2] = end;
			points[1] = new Point(points[2].x, points[0].y);
		}
		return points;
	}

	private static Point[] routeSouthWestEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		if (end.x < start.x) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x, start.y + midway(end.y, start.y));
			points[2] = new Point(end.x - distanceToNode, points[1].y);
			points[3] = new Point(points[2].x, end.y);
			points[4] = end;
		} else if (end.y > start.y) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x, start.y - distanceToNode);
			points[2] = new Point(end.x - midway(end.x, start.x), points[1].y);
			points[3] = new Point(points[2].x, end.y);
			points[4] = end;
		} else {
			points = new Point[3];
			points[0] = start;
			points[1] = new Point(start.x, end.y);
			points[2] = end;
		}

		for (Point iterable_element : points) {
			System.err.println(iterable_element);

		}
		System.err.println();
		return points;
	}

	private static Point[] routeNorthEastdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		if (end.x > start.x) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x, start.y + midway(end.y, start.y));
			points[2] = new Point(end.x + distanceToNode, points[1].y);
			points[3] = new Point(points[2].x, end.y);
			points[4] = end;
		} else if (end.y < start.y) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x, start.y + distanceToNode);
			points[2] = new Point(end.x - midway(end.x, start.x), points[1].y);
			points[3] = new Point(points[2].x, end.y);
			points[4] = end;
		} else {
			points = new Point[3];
			points[0] = start;
			points[1] = new Point(start.x, end.y);
			points[2] = end;
		}
		return points;
	}

	private static Point[] routeSouthEastEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		if (end.x > start.x) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x, start.y + midway(end.y, start.y));
			points[2] = new Point(end.x + distanceToNode, points[1].y);
			points[3] = new Point(points[2].x, end.y);
			points[4] = end;
		} else if (end.y > start.y) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x, start.y - distanceToNode);
			points[2] = new Point(end.x - midway(end.x, start.x), points[1].y);
			points[3] = new Point(points[2].x, end.y);
			points[4] = end;
		} else {
			points = new Point[3];
			points[0] = start;
			points[1] = new Point(start.x, end.y);
			points[2] = end;
		}
		return points;
	}

	private static Point[] routeNorthWestEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		if (end.x < start.x) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x, start.y + midway(end.y, start.y));
			points[2] = new Point(end.x - distanceToNode, points[1].y);
			points[3] = new Point(points[2].x, end.y);
			points[4] = end;
		} else if (end.y < start.y) {
			points = new Point[5];
			points[0] = start;
			points[1] = new Point(start.x, start.y + distanceToNode);
			points[2] = new Point(end.x - midway(end.x, start.x), points[1].y);
			points[3] = new Point(points[2].x, end.y);
			points[4] = end;
		} else {
			points = new Point[3];
			points[0] = start;
			points[1] = new Point(start.x, end.y);
			points[2] = end;
		}
		return points;
	}

	private static Point[] routeEastWestEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		/* A simple horizontal line */
		if (end.y == start.y) {
			points = new Point[2];
			points[0] = start;
			points[1] = end;
		} else if (end.x < start.x) {
			points = new Point[4];
			points[0] = start;
			points[1] = new Point(end.x - midway(end.x, start.x), start.y);
			points[2] = new Point(points[1].x, end.y);
			points[3] = end;
		} else {
			points = new Point[6];
			points[0] = start;
			points[1] = new Point(start.x - distanceToNode, start.y);
			points[2] = new Point(points[1].x, end.y - midway(end.y, start.y));
			points[3] = new Point(end.x + distanceToNode, points[2].y);
			points[4] = new Point(points[3].x, end.y);
			points[5] = end;
		}
		return points;
	}

	private static Point[] routeWestToEastEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		/* A simple horizontal line */
		if (end.y == start.y) {
			points = new Point[2];
			points[0] = start;
			points[1] = end;
		} else if (end.x > start.x) {
			points = new Point[4];
			points[0] = start;
			points[1] = new Point(start.x + midway(end.x, start.x), start.y);
			points[2] = new Point(points[1].x, end.y);
			points[3] = end;
		} else {
			points = new Point[6];
			points[0] = start;
			points[1] = new Point(start.x + distanceToNode, start.y);
			points[2] = new Point(points[1].x, start.y + midway(end.y, start.y));
			points[3] = new Point(end.x - distanceToNode, points[2].y);
			points[4] = new Point(points[3].x, end.y);
			points[5] = end;
		}
		return points;
	}

	private static Point[] routeNorthSouthEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		/* A simple vertical line */
		if (end.x == start.x) {
			points = new Point[2];
			points[0] = start;
			points[1] = end;
		} else if (end.y > start.y) {
			points = new Point[4];
			points[0] = start;
			points[1] = new Point(start.x, end.y - midway(end.y, start.y));
			points[2] = new Point(end.x, points[1].y);
			points[3] = end;
		} else {
			points = new Point[6];
			points[0] = start;
			points[1] = new Point(start.x, start.y + distanceToNode);
			points[2] = new Point(end.x - midway(end.x, start.x), points[1].y);
			points[3] = new Point(points[2].x, end.y - distanceToNode);
			points[4] = new Point(end.x, points[3].y);
			points[5] = end;
		}
		return points;
	}

	private static Point[] routeSouthNorthEdge(Point start, Point end, int distanceToNode) {

		Point[] points;

		/* A simple vertical line */
		if (end.x == start.x) {
			points = new Point[2];
			points[0] = start;
			points[1] = end;
		} else if (end.y < start.y) {
			points = new Point[4];
			points[0] = start;
			points[1] = new Point(start.x, start.y + midway(end.y, start.y));
			points[2] = new Point(end.x, points[1].y);
			points[3] = end;
		} else {
			points = new Point[6];
			points[0] = start;
			points[1] = new Point(start.x, start.y - distanceToNode);
			points[2] = new Point(start.x + midway(end.x, start.x), points[1].y);
			points[3] = new Point(points[2].x, end.y + distanceToNode);
			points[4] = new Point(end.x, points[3].y);
			points[5] = end;
		}
		return points;
	}

	private static final int midway(int a, int b) {

		return (int) ((a - b) / 2.0);
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
					return FROM_EAST;
				}
				if (median >= 45 && median < 135) {
					return FROM_NORTH;
				}
				if (median >= 135 && median < 225) {
					return FROM_WEST;
				}
				if (median >= 225 && median < 315) {
					return FROM_SOUTH;
				}
			}
		} else {
			if (Math.abs(end.x - start.x) > Math.abs(end.y - start.y)) {
				return end.x < start.x ? FROM_EAST : FROM_WEST;
			} else {
				return end.y < start.y ? FROM_SOUTH : FROM_NORTH;
			}
		}
		return UNDEFINED;
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
					return TO_EAST;
				}
				if (median >= 45 && median < 135) {
					return TO_NORTH;
				}
				if (median >= 135 && median < 225) {
					return TO_WEST;
				}
				if (median >= 225 && median < 315) {
					return TO_SOUTH;
				}
			}
		} else {

			if (Math.abs(end.x - start.x) > Math.abs(end.y - start.y)) {
				return end.x < start.x ? TO_WEST : TO_EAST;
			} else {
				return end.y < start.y ? TO_NORTH : TO_SOUTH;
			}
		}
		return UNDEFINED;
	}
}
