package com.visiors.visualstage.graph.view.edge.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.graph.view.ViewConstants;
import com.visiors.visualstage.graph.view.edge.EdgePoint;
import com.visiors.visualstage.graph.view.edge.Path;
import com.visiors.visualstage.graph.view.edge.PathChangeListener;

/**
 * The default implementation for {@link Path}
 * 
 * @author Shane
 * 
 */
public class DefaultPath implements Path {

	private EdgePoint[] points;
	private EdgePoint[] oldPath;

	/**
	 * Constructs a path with to points.
	 */
	public DefaultPath() {

		points = new EdgePoint[] { new EdgePoint(), new EdgePoint() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#getSize()
	 */
	@Override
	public int getSize() {

		return points.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#getPoints()
	 */
	@Override
	public EdgePoint[] getPoints() {

		return copyPoints(points);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#setPoints(com.visiors.
	 * visualstage.graph.view.edge.EdgePoint[], boolean)
	 */
	@Override
	public boolean setPoints(EdgePoint[] pts, boolean fireEvents) {

		if (!fireEvents) {
			this.points = pts;
			return true;
		}
		boolean modified = points.length != pts.length;
		if (!modified) {

			for (int i = 0; i < pts.length; i++) {
				modified = !pts[i].equals(points[i]);
				if (modified) {
					break;
				}
			}
		}
		if (modified) {

			final boolean singleAction = (oldPath == null);
			if (singleAction) {
				fireStartedPathChanging();
			}
			this.points = pts;
			firePathChanging();

			if (singleAction) {
				fireStoppedPathChanging();
			}
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#getStart()
	 */
	@Override
	public EdgePoint getStart() {

		return new EdgePoint(points[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#getEnd()
	 */
	@Override
	public EdgePoint getEnd() {

		return new EdgePoint(points[points.length - 1]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#getPointAt(int)
	 */
	@Override
	public EdgePoint getPointAt(int index) {

		return new EdgePoint(points[index]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#setPointAt(int,
	 * com.visiors.visualstage.graph.view.edge.EdgePoint)
	 */
	@Override
	public boolean setPointAt(int index, EdgePoint pt) {

		if (!points[index].equals(pt)) {
			final boolean singleAction = (oldPath == null);
			if (singleAction) {
				fireStartedPathChanging();
			}

			points[index] = pt;
			firePathChanging();

			if (singleAction) {
				fireStoppedPathChanging();
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#insertPointAt(int,
	 * com.visiors.visualstage.graph.view.edge.EdgePoint)
	 */
	@Override
	public void insertPointAt(int index, EdgePoint pt) {

		final EdgePoint[] tmp = new EdgePoint[points.length + 1];
		System.arraycopy(points, 0, tmp, 0, index + 1);
		System.arraycopy(points, index + 1, tmp, index + 2, points.length - index - 1);
		tmp[index + 1] = new EdgePoint(pt);
		setPoints(tmp, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#removePointAt(int)
	 */
	@Override
	public void removePointAt(int index) {

		final EdgePoint[] tmp = new EdgePoint[points.length - 1];
		System.arraycopy(points, 0, tmp, 0, index);
		System.arraycopy(points, index + 1, tmp, index, points.length - index - 1);
		setPoints(tmp, true);
	}

	private EdgePoint[] copyPoints(EdgePoint[] pts) {

		synchronized (pts) {
			final EdgePoint[] p = new EdgePoint[pts.length];
			for (int i = 0; i < pts.length; i++) {
				p[i] = new EdgePoint(pts[i]);
			}
			return p;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.view.edge.Path#deepCopy()
	 */
	@Override
	public Path deepCopy() {

		final DefaultPath p = new DefaultPath();
		p.setPoints(copyPoints(points), true);
		return p;
	}

	// //////////////////////////////////////////////////////////////////////////
	// Notifications - sending notification to listener

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.view.edge.Path#getHitPointIndex(java.awt
	 * .Point)
	 */
	@Override
	public int getHitPointIndex(Point pt) {

		for (int i = points.length - 1; i >= 0; i--) {
			if (pointHit(points[i].getPoint(), pt)) {
				return i;
			}
		}
		return NONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.view.edge.Path#getHitSegmentIndex(java.
	 * awt.Point)
	 */
	@Override
	public int getHitSegmentIndex(Point pt) {

		EdgePoint lineStart, lineEnd;
		for (int i = 0; i < points.length - 1; i++) {
			lineStart = points[i];
			lineEnd = points[i + 1];
			final Point start = lineStart.getPoint();
			final Point end = lineEnd.getPoint();
			if (distancePointLine(pt.x, pt.y, start.x, start.y, end.x, end.y) < ViewConstants.RESIZING_MARKER_SNAP_RADIUS) {
				return i;
			}
		}
		return NONE;
	}

	private final boolean pointHit(Point pt, Point hit) {

		final int ms = ViewConstants.RESIZING_MARKER_SNAP_RADIUS;
		return hit.x >= pt.x - ms && hit.x <= pt.x + ms && hit.y >= pt.y - ms && hit.y <= pt.y + ms;
	}

	private final int distancePointLine(int x, int y, int x1, int y1, int x2, int y2) {

		double lm;
		double u;
		lm = lineMagnitude(x2, y2, x1, y1);
		u = (((x - x1) * (x2 - x1)) + ((y - y1) * (y2 - y1))) / (lm * lm);
		if (u < 0.0 || u > 1.0) {
			final int ix = lineMagnitude(x, y, x1, y1);
			final int iy = lineMagnitude(x, y, x2, y2);
			return (ix > iy) ? iy : ix;
		}
		final int ix = (int) (x1 + u * (x2 - x1));
		final int iy = (int) (y1 + u * (y2 - y1));

		return lineMagnitude(x, y, ix, iy);
	}

	private final int lineMagnitude(int x1, int y1, int x2, int y2) {

		return (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.generics.interaction.Manipulatable#startManipulating
	 * ()
	 */
	@Override
	public void startManipulating() {

		fireStartedPathChanging();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.generics.interaction.Manipulatable#endManipulating
	 * ()
	 */
	@Override
	public void endManipulating() {

		fireStoppedPathChanging();
	}

	// ===================================================
	// sending notification to listener
	protected List<PathChangeListener> pathListener = new ArrayList<PathChangeListener>();
	protected boolean fireEvents;

	@Override
	public void addPathListener(PathChangeListener listener) {

		if (!pathListener.contains(listener)) {
			pathListener.add(listener);
		}
	}

	@Override
	public void removePathChangeListener(PathChangeListener listener) {

		pathListener.remove(listener);
	}

	@Override
	public void fireEvents(boolean enable) {

		fireEvents = enable;
	}

	protected void fireStartedPathChanging() {

		if (!fireEvents) {
			return;
		}
		if (oldPath == null) {
			oldPath = copyPoints(points);
			for (final PathChangeListener l : pathListener) {
				l.startChangingPath();
			}
		}
	}

	protected void firePathChanging() {

		if (!fireEvents) {
			return;
		}

		for (final PathChangeListener l : pathListener) {
			l.pathChanging();
		}

	}

	protected void fireStoppedPathChanging() {

		if (!fireEvents) {
			return;
		}
		if (oldPath != null) {
			for (final PathChangeListener l : pathListener) {
				l.stoppedChangingPath(oldPath);
			}
		}
		oldPath = null;

	}

}
