package com.visiors.visualstage.graph.view.edge;

import java.awt.Point;

/**
 * This class defines an edge point. Beside a coordinate, every edge point has a
 * type that determines its look and behaviour.
 * 
 */
public class EdgePoint {

	public static final int ZIRO = 1 << 0;
	public static final int SELECTABLE = 1 << 1;
	public static final int ALL = SELECTABLE;

	private int type;
	private Point point;

	public EdgePoint() {
		this(new Point(), ZIRO);
	}

	public EdgePoint(Point pt) {
		this(pt, ZIRO);
	}

	public EdgePoint(EdgePoint pt) {
		this(new Point(pt.getPoint()), pt.getType());
	}

	public EdgePoint(Point point, int type) {
		this.point = point;
		this.type = type;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Point getPoint() {
		return this.point;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	@Override
	public boolean equals(Object obj) {
		EdgePoint pt = (EdgePoint) obj;
		return this == obj || (pt.point.equals(point) && pt.type == type);
	}
}
