package com.visiors.visualstage.graph.view.edge;

import java.awt.Point;

import com.visiors.visualstage.tool.Manipulatable;

/**
 * This interface defines the path for drawing graph edges. A path can consist
 * of unlimited number of {link EdgePoint}s. Hence, it provides methods to
 * detect points or segments at a specific position.
 * 
 */
public interface Path extends Manipulatable {

	static final int NONE = Integer.MIN_VALUE;

	/**
	 * returns the start-point of the path
	 */
	public EdgePoint getStart();

	/**
	 * returns the end-point of the path
	 */
	public EdgePoint getEnd();

	/**
	 * returns the number of {@link EdgePoint}s that this path is made of
	 */
	public int getSize();

	/**
	 * returns all path's {@link EdgePoint}s
	 */
	public EdgePoint[] getPoints();

	/**
	 * set all {@link EdgePoint}s for this path
	 */
	public boolean setPoints(EdgePoint[] points, boolean fireEvents);

	/**
	 * returns the {@link EdgePoint} at the specified <code>index</code>
	 */
	public EdgePoint getPointAt(int index);

	/**
	 * sets an {@link EdgePoint} at the given <code>index</code>
	 */
	public boolean setPointAt(int index, EdgePoint pt);

	/**
	 * inserts an {@link EdgePoint} at the given <code>index</code>
	 */
	public void insertPointAt(int index, EdgePoint pt);

	/**
	 * removes the {@link EdgePoint} with the specified <code>index</code>
	 */
	public void removePointAt(int index);

	/**
	 * return a copy of this path
	 */
	public Path deepCopy();

	/**
	 * returns the {@link EdgePoint} at the specified coordinate if any;
	 * otherwise <b>NONE</b>
	 */
	public int getHitPointIndex(Point pt);

	/**
	 * returns the index of the segment hit by the specified coordinate if any;
	 * otherwise <b>NONE</b>
	 */
	public int getHitSegmentIndex(Point pt);

	public void addPathListener(PathChangeListener listener);

	public void removePathChangeListener(PathChangeListener listener);

	public void fireEvents(boolean enable);

}
